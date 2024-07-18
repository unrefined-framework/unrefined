package unrefined.desktop.audio;

import com.tianscar.javasound.sampled.AudioResourceLoader;
import unrefined.desktop.MusicPlayer;
import unrefined.desktop.SoundClip;
import unrefined.io.asset.Asset;
import unrefined.media.sound.Sampled;
import unrefined.media.sound.Music;
import unrefined.media.sound.Sound;
import unrefined.runtime.DesktopAssetLoader;
import unrefined.runtime.DesktopMusic;
import unrefined.runtime.DesktopSound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class JavaSoundAudioHandler extends Sampled.Handler {

    public static final JavaSoundAudioHandler INSTANCE = new JavaSoundAudioHandler();

    @Override
    public Sound readSound(File input, int pool) throws IOException {
        Objects.requireNonNull(input);
        SoundClip soundClip;
        try {
            soundClip = new SoundClip(AudioSystem.getAudioInputStream(input), pool);
        } catch (UnsupportedAudioFileException e) {
            return null;
        }
        return new DesktopSound(soundClip);
    }

    @Override
    public Sound readSound(Asset input, int pool) throws IOException {
        Objects.requireNonNull(input);
        SoundClip soundClip;
        try {
            soundClip = new SoundClip(AudioResourceLoader
                    .getAudioInputStream(((DesktopAssetLoader) input.getAssetLoader()).getClassLoader(),
                            input.getPathname()), pool);
        } catch (UnsupportedAudioFileException e) {
            return null;
        }
        return new DesktopSound(soundClip);
    }

    @Override
    public Music readMusic(File input) throws IOException {
        Objects.requireNonNull(input);
        MusicPlayer musicPlayer = new MusicPlayer();
        try {
            musicPlayer.setDataSource(input);
        } catch (UnsupportedAudioFileException e) {
            return null;
        }
        return new DesktopMusic(musicPlayer);
    }

    @Override
    public Music readMusic(Asset input) throws IOException {
        Objects.requireNonNull(input);
        MusicPlayer musicPlayer = new MusicPlayer();
        try {
            musicPlayer.setDataSource(((DesktopAssetLoader) input.getAssetLoader()).getClassLoader(), input.getPathname());
        } catch (UnsupportedAudioFileException e) {
            return null;
        }
        return new DesktopMusic(musicPlayer);
    }

    private static final Set<String> LOADER_FORMAT_NAMES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "au", "snd", "mp4", "m4a", "aac", "aiff", "flac", "aifc", "mp3", "wav", "ogg", "mp1", "mp2", "mid", "midi"
    )));

    @Override
    public Set<String> readerFormats() {
        return LOADER_FORMAT_NAMES;
    }

}
