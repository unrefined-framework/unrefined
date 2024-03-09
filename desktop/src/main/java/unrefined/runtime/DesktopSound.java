package unrefined.runtime;

import unrefined.desktop.SoundClip;
import unrefined.desktop.SoundMuxer;
import unrefined.math.FastMath;
import unrefined.media.sound.Sound;

import javax.sound.sampled.LineUnavailableException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopSound extends Sound {

    private static final SoundMuxer SOUND_MUXER = new SoundMuxer();

    private volatile float leftVolume;
    private volatile float rightVolume;
    private volatile float speed;
    private volatile int loops;
    private final SoundClip soundClip;
    private volatile int instanceID;

    public SoundClip getSoundClip() {
        return soundClip;
    }

    public DesktopSound(SoundClip soundClip) {
        this.soundClip = Objects.requireNonNull(soundClip);
        this.instanceID = soundClip.obtainInstance();
        reset();
    }

    public int getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(int instanceID) {
        this.instanceID = instanceID;
    }

    @Override
    public void setLeftVolume(float leftVolume) {
        this.leftVolume = FastMath.clamp(leftVolume, 0, 1);
        soundClip.setLeftVolume(instanceID, this.leftVolume);
    }

    @Override
    public void setRightVolume(float rightVolume) {
        this.rightVolume = FastMath.clamp(rightVolume, 0, 1);
        soundClip.setRightVolume(instanceID, this.rightVolume);
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = FastMath.clamp(speed, 0.5f, 2.f);
        soundClip.setSpeed(instanceID, this.speed);
    }

    @Override
    public void setLooping(int loops) {
        this.loops = Math.max(-1, loops);
        soundClip.setLooping(instanceID, this.loops);
    }

    @Override
    public float getLeftVolume() {
        return leftVolume;
    }

    @Override
    public float getRightVolume() {
        return rightVolume;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void start() {
        if (!SOUND_MUXER.isPlaying()) {
            try {
                SOUND_MUXER.start();
            }
            catch (LineUnavailableException e) {
                return;
            }
        }
        if (!soundClip.isPlaying()) soundClip.open(SOUND_MUXER);
        if (!soundClip.isPlaying(instanceID)) {
            soundClip.seekToFrames(instanceID, 0);
            soundClip.start(instanceID);
        }
    }

    @Override
    public void pause() {
        soundClip.stop(instanceID);
    }

    @Override
    public void resume() {
        soundClip.start(instanceID);
    }

    @Override
    public void stop() {
        if (soundClip.isPlaying(instanceID)) soundClip.stop(instanceID);
        if (soundClip.isPlaying()) {
            soundClip.close();
            try {
                soundClip.releaseInstance(instanceID);
            }
            finally {
                try {
                    setInstanceID(soundClip.obtainInstance());
                }
                finally {
                    soundClip.setVolume(instanceID, leftVolume, rightVolume);
                    soundClip.setLooping(instanceID, loops);
                    soundClip.setSpeed(instanceID, speed);
                }
            }
        }
        if (SOUND_MUXER.getClipCacheCount() < 1 && SOUND_MUXER.isPlaying()) {
            SOUND_MUXER.stop();
        }
    }

    @Override
    public int getLooping() {
        return loops;
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) {
            if (soundClip.isOpen()) soundClip.close();
            if (SOUND_MUXER.getClipCacheCount() < 1 && SOUND_MUXER.isPlaying()) {
                SOUND_MUXER.stop();
            }
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

}
