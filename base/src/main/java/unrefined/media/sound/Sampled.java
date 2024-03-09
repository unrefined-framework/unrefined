package unrefined.media.sound;

import unrefined.context.Environment;
import unrefined.io.UnsupportedFormatException;
import unrefined.io.asset.Asset;
import unrefined.util.concurrent.ConcurrentHashSet;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Sampled {

    private static volatile Sampled INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Sampled getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.sampled", Sampled.class);
        }
        return INSTANCE;
    }

    public static abstract class Handler {
        public abstract Sound readSound(File input) throws IOException;
        public abstract Sound readSound(Asset input) throws IOException;
        public abstract Music readMusic(File input) throws IOException;
        public abstract Music readMusic(Asset input) throws IOException;
        public abstract Set<String> readerFormats();
    }

    private final Set<Handler> audioHandlers = new ConcurrentHashSet<>();
    public Set<Handler> audioHandlers() {
        return audioHandlers;
    }

    public Sound readSound(File input) throws IOException {
        for (Handler handler : audioHandlers()) {
            Sound sound = handler.readSound(input);
            if (sound != null) return sound;
        }
        throw new UnsupportedFormatException();
    }

    public Sound readSound(Asset input) throws IOException {
        for (Handler handler : audioHandlers()) {
            Sound sound = handler.readSound(input);
            if (sound != null) return sound;
        }
        throw new UnsupportedFormatException();
    }

    public Music readMusic(File input) throws IOException {
        for (Handler handler : audioHandlers()) {
            Music music = handler.readMusic(input);
            if (music != null) return music;
        }
        throw new UnsupportedFormatException();
    }

    public Music readMusic(Asset input) throws IOException {
        for (Handler handler : audioHandlers()) {
            Music music = handler.readMusic(input);
            if (music != null) return music;
        }
        throw new UnsupportedFormatException();
    }

    public Set<String> getAudioLoaderFormats() {
        Set<String> formats = new HashSet<>();
        for (Handler handler : audioHandlers()) {
            formats.addAll(handler.readerFormats());
        }
        return Collections.unmodifiableSet(formats);
    }

}
