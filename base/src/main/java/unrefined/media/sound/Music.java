package unrefined.media.sound;

import unrefined.io.Disposable;
import unrefined.io.asset.Asset;
import unrefined.util.Resettable;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.Signal;

import java.io.File;
import java.io.IOException;

public abstract class Music implements Resettable, Disposable {

    public static Music read(File input) throws IOException {
        return Sampled.getInstance().readMusic(input);
    }

    public static Music read(Asset input) throws IOException {
        return Sampled.getInstance().readMusic(input);
    }

    public abstract void setLeftVolume(float leftVolume);
    public abstract void setRightVolume(float rightVolume);

    public void setVolume(float volume) {
        setLeftVolume(volume);
        setRightVolume(volume);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        setLeftVolume(leftVolume);
        setRightVolume(rightVolume);
    }

    public abstract void setLooping(int loops);

    public abstract float getLeftVolume();
    public abstract float getRightVolume();

    public abstract int getLooping();

    public abstract void setMillisecondPosition(int position);

    public abstract int getMillisecondPosition();
    public abstract int getMillisecondLength();

    @Override
    public void reset() {
        setVolume(1.0f, 1.0f);
        setLooping(0);
        setMillisecondPosition(0);
    }

    public boolean isIdentity() {
        return getLeftVolume() == 1.0f && getRightVolume() == 1.0f && getLooping() == 0 && getMillisecondPosition() == 0;
    }

    public abstract void prepare() throws IOException;
    public abstract boolean isPrepared();
    public abstract void start();
    public abstract void pause();
    public abstract void resume();
    public abstract void stop();

    public abstract boolean isPlaying();

    private final Signal<EventSlot<StopEvent>> onStop = Signal.ofSlot();

    public Signal<EventSlot<StopEvent>> onStop() {
        return onStop;
    }

    public static class StopEvent extends Event<Music> {
        private final int looping;
        public StopEvent(Music source, int looping) {
            super(source);
            this.looping = looping;
        }
        public int getLooping() {
            return looping;
        }
    }

}
