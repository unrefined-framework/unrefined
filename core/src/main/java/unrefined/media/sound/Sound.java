package unrefined.media.sound;

import unrefined.io.Disposable;
import unrefined.io.asset.Asset;
import unrefined.util.Resettable;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.Signal;

import java.io.File;
import java.io.IOException;

public abstract class Sound implements Resettable, Disposable {

    public static Sound read(File input) throws IOException {
        return Sampled.getInstance().readSound(input);
    }

    public static Sound read(File input, int pool) throws IOException {
        return Sampled.getInstance().readSound(input, pool);
    }

    public static Sound read(Asset input, int pool) throws IOException {
        return Sampled.getInstance().readSound(input, pool);
    }

    public static Sound read(Asset input) throws IOException {
        return Sampled.getInstance().readSound(input);
    }

    public void setLeftVolume(float leftVolume) {
        setLeftVolume(-1, leftVolume);
    }
    public void setRightVolume(float rightVolume) {
        setRightVolume(-1, rightVolume);
    }

    public void setVolume(float volume) {
        setVolume(-1, volume);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        setVolume(-1, leftVolume, rightVolume);
    }

    public void setLooping(int loops) {
        setLooping(-1, loops);
    }

    public float getLeftVolume() {
        return getLeftVolume(-1);
    }
    public float getRightVolume() {
        return getRightVolume(-1);
    }

    public int getLooping() {
        return getLooping(-1);
    }

    public void setSpeed(float speed) {
        setSpeed(-1, speed);
    }

    public float getSpeed() {
        return getSpeed(-1);
    }

    public abstract void setLeftVolume(int id, float leftVolume);
    public abstract void setRightVolume(int id, float rightVolume);

    public void setVolume(int id, float volume) {
        setLeftVolume(id, volume);
        setRightVolume(id, volume);
    }

    public void setVolume(int id, float leftVolume, float rightVolume) {
        setLeftVolume(id, leftVolume);
        setRightVolume(id, rightVolume);
    }

    public abstract void setLooping(int id, int loops);

    public abstract float getLeftVolume(int id);
    public abstract float getRightVolume(int id);

    public abstract int getLooping(int id);

    public abstract void setSpeed(int id, float speed);

    public abstract float getSpeed(int id);

    @Override
    public void reset() {
        setVolume(1.0f, 1.0f);
        setLooping(0);
        setSpeed(1.0f);
    }

    public boolean isIdentity() {
        return getLeftVolume() == 1.0f && getRightVolume() == 1.0f && getLooping() == 0 && getSpeed() == 1.0f;
    }

    public int start() {
        return start(getLeftVolume(), getRightVolume(), getSpeed(), getLooping());
    }
    public abstract int start(float leftVolume, float rightVolume, float speed, int loops);
    public int start(float leftVolume, float rightVolume) {
        return start(leftVolume, rightVolume, getSpeed(), getLooping());
    }
    public int start(float leftVolume, float rightVolume, float speed) {
        return start(leftVolume, rightVolume, speed, getLooping());
    }
    public int play(float volume, float speed, int loops) {
        return start(volume, volume, speed, loops);
    }
    public int play(float volume, float speed) {
        return start(volume, volume, speed, 0);
    }
    public int play(float volume) {
        return start(volume, volume, 1.0f, 0);
    }
    public int play() {
        return start(1.0f, 1.0f, 1.0f, 0);
    }
    public abstract void pause(int id);
    public abstract void resume(int id);
    public abstract void stop(int id);
    public abstract boolean isPlaying(int id);

    private final Signal<EventSlot<StopEvent>> onStop = Signal.ofSlot();

    public Signal<EventSlot<StopEvent>> onStop() {
        return onStop;
    }

    public static class StopEvent extends Event<Sound> {

        private final int loops;
        private final int id;

        public StopEvent(Sound source, int id, int loops) {
            super(source);
            this.id = id;
            this.loops = loops;
        }

        public int getId() {
            return id;
        }

        public int getLooping() {
            return loops;
        }

    }

}
