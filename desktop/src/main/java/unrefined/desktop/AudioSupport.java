package unrefined.desktop;

import unrefined.util.NotInstantiableError;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.util.Objects;

import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public final class AudioSupport {

    public static final int DEFAULT_BUFFER_FRAMES = 2048;
    public static final AudioFormat DEFAULT_AUDIO_FORMAT = new AudioFormat(44100, 16, 2, true, false);
    public static final DataLine.Info DEFAULT_AUDIO_LINE_INFO = new DataLine.Info(SourceDataLine.class, DEFAULT_AUDIO_FORMAT);
    public static final int SOUND_VOLUME_STEPS = 1024;
    public static final int SOUND_SPEED_STEPS = 4096;

    private AudioSupport() {
        throw new NotInstantiableError(AudioSupport.class);
    }

    public static void adjustVolume(byte[] samples, int offset, int length, double leftVolume, double rightVolume) {
        short[] buf = new short[4];
        short left, right;
        for (int i = offset; i < offset + length; i += 4) {

            buf[0] = (short) ((samples[i + 1] & 0xFF) << 8);
            buf[1] = (short) (samples[i] & 0xFF);
            buf[2] = (short) ((samples[i + 3] & 0xFF) << 8);
            buf[3] = (short) (samples[i + 2] & 0xFF);

            left  = (short) ((buf[0] | buf[1]) * leftVolume);
            right = (short) ((buf[2] | buf[3]) * rightVolume);

            samples[i]     = (byte) left;
            samples[i + 1] = (byte) (left >> 8);
            samples[i + 2] = (byte) right;
            samples[i + 3] = (byte) (right >> 8);
        }
    }

    public static AudioInputStream getSupportedAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream) {
        Objects.requireNonNull(sourceStream);
        AudioFormat sourceFormat = sourceStream.getFormat();

        int sampleSizeInBits = sourceFormat.getSampleSizeInBits();
        if (sampleSizeInBits == NOT_SPECIFIED) sampleSizeInBits = targetFormat.getSampleSizeInBits();
        int channels = sourceFormat.getChannels();
        if (channels == NOT_SPECIFIED) channels = targetFormat.getChannels();

        AudioFormat decodedFormat = new AudioFormat(
                sourceFormat.getSampleRate(),
                sampleSizeInBits,
                channels,
                true,
                sourceFormat.isBigEndian()
        );
        return AudioSystem.getAudioInputStream(targetFormat, AudioSystem.getAudioInputStream(decodedFormat, sourceStream));
    }

    public static void fromPcmToAudioBytes(byte[] audioBytes, float[] sourcePcm) {
        if (sourcePcm.length * 2 != audioBytes.length) {
            throw new IllegalArgumentException(
                    "Destination array must be exactly twice the length of the source array");
        }

        for (int i = 0, n = sourcePcm.length; i < n; i ++) {
            sourcePcm[i] *= 32767;

            audioBytes[i * 2] = (byte) sourcePcm[i];
            audioBytes[i * 2 + 1] = (byte)((int) sourcePcm[i] >> 8);
        }
    }

}
