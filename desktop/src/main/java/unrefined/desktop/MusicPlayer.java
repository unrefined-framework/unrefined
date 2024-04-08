package unrefined.desktop;

import com.tianscar.javasound.sampled.AudioResourceLoader;
import unrefined.io.ReadWriteIO;
import unrefined.math.FastMath;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;
import static unrefined.desktop.AudioSupport.*;

public class MusicPlayer implements AutoCloseable {

    private final int threadPriority;
    public int getThreadPriority() {
        return threadPriority;
    }

    private final Mixer mixer;
    private final AudioFormat playbackFormat;
    private final DataLine.Info playbackInfo;
    private final int streamBufferSize;

    private volatile SourceDataLine sourceDataLine = null;
    private volatile AudioInputStream audioInputStream = null;

    private final List<MusicListener> listeners = new CopyOnWriteArrayList<>();

    private final LineListener broadcastEvent = event -> {
        if (event.getType().equals(LineEvent.Type.START)) broadcastEvent(new MusicEvent(
                MusicPlayer.this, MusicEvent.Type.START, event.getFramePosition()));
        else if (event.getType().equals(LineEvent.Type.STOP)) broadcastEvent(new MusicEvent(
                MusicPlayer.this, MusicEvent.Type.STOP, event.getFramePosition()));
    };
    private void broadcastEvent(MusicEvent event) {
        for (MusicListener listener : listeners) {
            listener.update(event);
        }
    }

    private volatile boolean paused = false;

    private volatile double leftVolume = 1.0;
    private volatile double rightVolume = 1.0;

    private final Object lock = new byte[0];

    public double getLeftVolume() {
        return leftVolume;
    }

    public double getRightVolume() {
        return rightVolume;
    }

    public void setLeftVolume(double leftVolume) {
        this.leftVolume = FastMath.clamp(leftVolume, 0, 1);
    }

    public void setRightVolume(double rightVolume) {
        this.rightVolume = FastMath.clamp(rightVolume, 0, 1);
    }

    public void setVolume(double leftVolume, double rightVolume) {
        setLeftVolume(leftVolume);
        setRightVolume(rightVolume);
    }

    public void setVolume(double volume) {
        setVolume(volume, volume);
    }

    public MusicPlayer(Mixer mixer, AudioFormat playbackFormat, int bufferFrames, int threadPriority) {
        if (bufferFrames < 0) throw new ArrayIndexOutOfBoundsException(bufferFrames);
        this.mixer = mixer;
        if (playbackFormat == null) {
            this.playbackFormat = DEFAULT_AUDIO_FORMAT;
            this.playbackInfo = DEFAULT_AUDIO_LINE_INFO;
        }
        else {
            this.playbackFormat = playbackFormat;
            this.playbackInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
        }
        this.streamBufferSize = bufferFrames * this.playbackFormat.getFrameSize();
        this.threadPriority = FastMath.clamp(threadPriority, Thread.MIN_PRIORITY, Thread.MAX_PRIORITY);
    }

    public MusicPlayer(Mixer mixer, AudioFormat playbackFormat, int streamBufferSize) {
        this(mixer, playbackFormat, streamBufferSize, Thread.MAX_PRIORITY);
    }

    public MusicPlayer(Mixer mixer, AudioFormat playbackFormat) {
        this(mixer, playbackFormat, DEFAULT_BUFFER_FRAMES);
    }

    public MusicPlayer(AudioFormat playbackFormat) {
        this(null, playbackFormat);
    }

    public MusicPlayer(Mixer mixer) {
        this(mixer, null);
    }

    public MusicPlayer() {
        this(null, null);
    }

    public MusicPlayer(int streamBufferSize) {
        this(null, null, streamBufferSize);
    }

    public MusicPlayer(Mixer mixer, int threadPriority) {
        this(mixer, null, DEFAULT_BUFFER_FRAMES, threadPriority);
    }

    public void setDataSource(ClassLoader classLoader, String resource) throws UnsupportedAudioFileException, IOException {
        if (isPrepared()) throw new IllegalStateException("You need to call the function before prepared");
        synchronized (lock) {
            AudioInputStream sourceStream = AudioResourceLoader.getAudioInputStream(classLoader, resource);
            AudioFileFormat sourceFileFormat = AudioResourceLoader.getAudioFileFormat(classLoader, resource);
            Long duration = (Long) sourceFileFormat.properties().get("duration");
            if (duration == null) {
                if (sourceFileFormat.getFrameLength() != NOT_SPECIFIED &&
                        sourceFileFormat.getFormat().getFrameRate() != NOT_SPECIFIED) {
                    microsecondsLength.set((long) (((double) sourceFileFormat.getFrameLength() /
                            (double) sourceFileFormat.getFormat().getFrameRate()) * 1_000_000L));
                }
            }
            else microsecondsLength.set(duration);
            if (audioInputStream != null) audioInputStream.close();
            audioInputStream = getSupportedAudioInputStream(playbackFormat, sourceStream);
        }
    }

    public void setDataSource(File file) throws UnsupportedAudioFileException, IOException {
        if (isPrepared()) throw new IllegalStateException("You need to call the function before prepared");
        synchronized (lock) {
            AudioInputStream sourceStream = AudioSystem.getAudioInputStream(file);
            AudioFileFormat sourceFileFormat = AudioSystem.getAudioFileFormat(file);
            Long duration = (Long) sourceFileFormat.properties().get("duration");
            if (duration == null) {
                if (sourceFileFormat.getFrameLength() != NOT_SPECIFIED &&
                        sourceFileFormat.getFormat().getFrameRate() != NOT_SPECIFIED) {
                    microsecondsLength.set((long) (((double) sourceFileFormat.getFrameLength() /
                            (double) sourceFileFormat.getFormat().getFrameRate()) * 1_000_000L));
                }
            }
            else microsecondsLength.set(duration);
            if (audioInputStream != null) audioInputStream.close();
            audioInputStream = getSupportedAudioInputStream(playbackFormat, sourceStream);
        }
    }

    public Mixer getMixer() {
        return mixer;
    }

    private static final AtomicInteger nextSerialNumber = new AtomicInteger();
    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement();
    }

    public void seekToFrames(long framePosition) throws IOException {
        checkPrepared();
        if (isPlaying()) throw new IllegalStateException("You need to call the function before playing");
        ReadWriteIO.discardNBytes(audioInputStream, framePosition * playbackFormat.getFrameSize());
    }

    public void seekToMicroseconds(long microsecondPosition) throws IOException {
        seekToFrames((long) (microsecondPosition / 1000_000.0 * playbackFormat.getFrameRate()));
    }

    public boolean isPlaying() {
        return isPrepared() && playing;
    }

    private volatile boolean playing = false;
    public void start() {
        checkPrepared();
        paused = false;
        if (playing) return;
        playing = true;
        Thread t = new Thread(() -> {
            sourceDataLine.start();
            byte[] buffer = new byte[streamBufferSize];
            int read = 0;
            while (playing && read != -1) {
                if (!paused) {
                    try {
                        read = audioInputStream.read(buffer, 0, buffer.length);
                    } catch (Throwable e) {
                        stop();
                        return;
                    }
                    adjustVolume(buffer, 0, read, leftVolume, rightVolume);
                    if (read != -1) sourceDataLine.write(buffer, 0, read);
                }
            }
            stop();
        }, "MusicPlayback-" + serialNumber());
        t.setPriority(threadPriority);
        t.setDaemon(true);
        t.start();
    }

    public void pause() {
        checkPrepared();
        paused = true;
    }

    public void stop() {
        checkPrepared();
        stop0();
    }

    private void stop0() {
        if (!isPrepared()) return;
        synchronized (lock) {
            if (!isPlaying()) return;
            if (audioInputStream != null) {
                try {
                    audioInputStream.close();
                    audioInputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SourceDataLine tmp = sourceDataLine;
            sourceDataLine = null;
            tmp.drain();
            tmp.stop();
            tmp.removeLineListener(broadcastEvent);
            tmp.close();
        }
    }

    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    public AudioFormat getPlaybackFormat() {
        return playbackFormat;
    }

    public int getStreamBufferSize() {
        return streamBufferSize;
    }

    public int getDataLineBufferSize() {
        return isPrepared() ? sourceDataLine.getBufferSize() : 0;
    }

    public long getMicrosecondLength() {
        return isPrepared() ? microsecondsLength.get() : NOT_SPECIFIED;
    }

    public int getFramePosition() {
        return isPrepared() ? sourceDataLine.getFramePosition() : 0;
    }

    public long getLongFramePosition() {
        return isPrepared() ? sourceDataLine.getLongFramePosition() : 0;
    }

    public long getMicrosecondPosition() {
        return isPrepared() ? sourceDataLine.getMicrosecondPosition() : 0;
    }

    public DataLine.Info getPlaybackLineInfo() {
        return playbackInfo;
    }

    private final AtomicLong microsecondsLength = new AtomicLong(NOT_SPECIFIED);
    public void prepare() throws LineUnavailableException, UnsupportedAudioFileException {
        if (isPrepared()) return;
        synchronized (lock) {
            SourceDataLine tmp = (SourceDataLine) (mixer == null ? AudioSystem.getLine(playbackInfo) : mixer.getLine(playbackInfo));
            tmp.addLineListener(broadcastEvent);
            tmp.open();
            sourceDataLine = tmp;
            broadcastEvent(new MusicEvent(this, MusicEvent.Type.PREPARE, NOT_SPECIFIED));
        }
    }

    public boolean isPrepared() {
        return sourceDataLine != null && sourceDataLine.isOpen();
    }

    private void checkPrepared() {
        if (!isPrepared()) throw new IllegalStateException("Please prepare first!");
    }

    public void addMusicListener(MusicListener listener) {
        listeners.add(listener);
    }

    public void removeMusicListener(MusicListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void close() {
        stop0();
    }

}
