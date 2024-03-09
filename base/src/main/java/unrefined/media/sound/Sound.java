package unrefined.media.sound;

import unrefined.io.Disposable;
import unrefined.io.asset.Asset;
import unrefined.util.Resettable;

import java.io.File;
import java.io.IOException;

public abstract class Sound implements Resettable, Disposable {

    public static Sound read(File input) throws IOException {
        return Sampled.getInstance().readSound(input);
    }

    public static Sound read(Asset input) throws IOException {
        return Sampled.getInstance().readSound(input);
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

    public abstract void setSpeed(float speed);

    public abstract float getSpeed();

    @Override
    public void reset() {
        setVolume(1.0f, 1.0f);
        setLooping(0);
        setSpeed(1.0f);
    }

    public boolean isIdentity() {
        return getLeftVolume() == 1.0f && getRightVolume() == 1.0f && getLooping() == 0 && getSpeed() == 1.0f;
    }

    public abstract void start();
    public abstract void pause();
    public abstract void resume();
    public abstract void stop();

}
