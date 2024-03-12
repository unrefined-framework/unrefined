package unrefined.runtime;

import unrefined.desktop.SoundClip;
import unrefined.desktop.SoundEvent;
import unrefined.desktop.SoundListener;
import unrefined.desktop.SoundMuxer;
import unrefined.math.FastMath;
import unrefined.media.sound.Sound;

import javax.sound.sampled.LineUnavailableException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopSound extends Sound implements SoundListener {

    private static final SoundMuxer SOUND_MUXER = new SoundMuxer();

    private volatile float leftVolume;
    private volatile float rightVolume;
    private volatile float speed;
    private volatile int loops;
    private final SoundClip soundClip;

    public SoundClip getSoundClip() {
        return soundClip;
    }

    public DesktopSound(SoundClip soundClip) {
        this.soundClip = Objects.requireNonNull(soundClip);
        soundClip.addSoundListener(this);
        reset();
    }

    @Override
    public void setLeftVolume(int id, float leftVolume) {
        leftVolume = FastMath.clamp(leftVolume, 0, 1);
        if (id < 0) this.leftVolume = leftVolume;
        else soundClip.setLeftVolume(id, leftVolume);
    }

    @Override
    public void setRightVolume(int id, float rightVolume) {
        rightVolume = FastMath.clamp(rightVolume, 0, 1);
        if (id < 0) this.rightVolume = rightVolume;
        else soundClip.setRightVolume(id, rightVolume);
    }

    @Override
    public void setSpeed(int id, float speed) {
        speed = FastMath.clamp(speed, 0.5f, 2.f);
        if (id < 0) this.speed = speed;
        else soundClip.setSpeed(id, speed);
    }

    @Override
    public void setLooping(int id, int loops) {
        loops = Math.max(-1, loops);
        if (id < 0) this.loops = loops;
        else soundClip.setLooping(id, loops);
    }

    @Override
    public float getLeftVolume(int id) {
        return id < 0 ? leftVolume : (float) soundClip.getLeftVolume(id);
    }

    @Override
    public float getRightVolume(int id) {
        return id < 0 ? rightVolume : (float) soundClip.getRightVolume(id);
    }

    @Override
    public float getSpeed(int id) {
        return id < 0 ? speed : (float) soundClip.getSpeed(id);
    }

    @Override
    public int start() {
        return start(leftVolume, rightVolume, speed, loops);
    }

    @Override
    public int start(float leftVolume, float rightVolume, float speed, int loops) {
        if (!SOUND_MUXER.isPlaying()) {
            try {
                SOUND_MUXER.start();
            }
            catch (LineUnavailableException e) {
                return -1;
            }
        }
        if (!soundClip.isPlaying()) soundClip.open(SOUND_MUXER);
        int id = soundClip.obtainInstance();
        if (id < 0) return id;
        if (!soundClip.isPlaying(id)) {
            try {
                soundClip.seekToFrames(id, 0);
                soundClip.setVolume(id, leftVolume, rightVolume);
                soundClip.setSpeed(id, speed);
                soundClip.setLooping(id, loops);
            }
            finally {
                soundClip.start(id);
            }
        }
        return id;
    }

    @Override
    public void pause(int id) {
        soundClip.stop(id);
    }

    @Override
    public void resume(int id) {
        soundClip.start(id);
    }

    @Override
    public void stop(int id) {
        try {
            if (soundClip.isPlaying(id)) soundClip.stop(id);
        }
        finally {
            soundClip.releaseInstance(id);
        }
        try {
            if (soundClip.instanceCount() < 1) soundClip.close();
        }
        finally {
            if (SOUND_MUXER.getClipCacheCount() < 1 && SOUND_MUXER.isPlaying()) {
                SOUND_MUXER.stop();
            }
        }
    }

    @Override
    public boolean isPlaying(int id) {
        return soundClip.isPlaying(id);
    }

    @Override
    public int getLooping(int id) {
        return id < 0 ? loops : soundClip.getLooping(id);
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

    @Override
    public void update(SoundEvent event) {
        if (event.getSource() != soundClip) return;
        if (event.getType().equals(SoundEvent.Type.STOP_INSTANCE)) {
            onStop().emit(new StopEvent(this, event.instanceID, 0));
            soundClip.releaseInstance(event.instanceID);
            try {
                if (soundClip.instanceCount() < 1) soundClip.close();
            }
            finally {
                if (SOUND_MUXER.getClipCacheCount() < 1 && SOUND_MUXER.isPlaying()) {
                    SOUND_MUXER.stop();
                }
            }
        }
        else if (event.getType().equals(SoundEvent.Type.LOOP_INSTANCE)) {
            onStop().emit(new StopEvent(this, event.instanceID, soundClip.getLooping(event.instanceID)));
        }
    }

}
