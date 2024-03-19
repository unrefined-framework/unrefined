package unrefined.runtime;

import unrefined.desktop.CleanerSupport;
import unrefined.desktop.MusicEvent;
import unrefined.desktop.MusicListener;
import unrefined.desktop.MusicPlayer;
import unrefined.math.FastMath;
import unrefined.media.sound.Music;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DesktopMusic extends Music implements MusicListener {

    private volatile float leftVolume;
    private volatile float rightVolume;
    private volatile int loops;
    private final AtomicInteger loopsLeft = new AtomicInteger();
    private volatile int position;

    private final MusicPlayer musicPlayer;

    public AtomicInteger loopsLeft() {
        return loopsLeft;
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public DesktopMusic(MusicPlayer musicPlayer) {
        this.musicPlayer = Objects.requireNonNull(musicPlayer);
        reset();
        musicPlayer.addMusicListener(this);
        CleanerSupport.register(this, this::dispose);
    }

    @Override
    public void reset() {
        super.reset();
        loopsLeft.set(loops);
    }

    @Override
    public void prepare() throws IOException {
        try {
            musicPlayer.prepare();
        } catch (LineUnavailableException ignored) {
        } catch (UnsupportedAudioFileException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean isPrepared() {
        return musicPlayer.isPrepared();
    }

    private void checkPrepared() {
        if (!musicPlayer.isPrepared()) throw new IllegalStateException("Please prepare first!");
    }

    @Override
    public void start() {
        checkPrepared();
        if (musicPlayer.isPlaying()) return;
        try {
            musicPlayer.seekToMicroseconds(FastMath.clamp(position * 1000L, 0, musicPlayer.getMicrosecondLength()));
            musicPlayer.start();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void pause() {
        checkPrepared();
        if (!musicPlayer.isPlaying()) return;
        musicPlayer.pause();
    }

    @Override
    public void resume() {
        checkPrepared();
        if (musicPlayer.isPlaying()) return;
        musicPlayer.start();
    }

    @Override
    public void stop() {
        checkPrepared();
        if (!musicPlayer.isPlaying()) return;
        musicPlayer.stop();
    }

    @Override
    public boolean isPlaying() {
        return musicPlayer.isPlaying();
    }

    @Override
    public void setLeftVolume(float leftVolume) {
        this.leftVolume = FastMath.clamp(leftVolume, 0, 1);
        musicPlayer.setLeftVolume(this.leftVolume);
    }

    @Override
    public void setRightVolume(float rightVolume) {
        this.rightVolume = FastMath.clamp(rightVolume, 0, 1);
        musicPlayer.setRightVolume(this.rightVolume);
    }

    @Override
    public void setLooping(int loops) {
        this.loops = Math.max(-1, loops);
        loopsLeft.set(this.loops);
    }

    @Override
    public void setMillisecondPosition(int pos) {
        this.position = pos;
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
    public int getLooping() {
        return loops;
    }

    @Override
    public int getMillisecondPosition() {
        return position;
    }

    public int getLoopsLeft() {
        return loopsLeft.get();
    }

    @Override
    public int getMillisecondLength() {
        return (int) Math.min(Integer.MAX_VALUE, musicPlayer.getMicrosecondLength() / 1000L);
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) {
            musicPlayer.close();
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public void update(MusicEvent event) {
        if (event.getSource() != musicPlayer) return;
        if (event.getType().equals(MusicEvent.Type.STOP)) {
            try {
                if (getLoopsLeft() == -1) {
                    onStop().emit(new StopEvent(this, -1));
                    musicPlayer.prepare();
                    musicPlayer.start();
                }
                else if (getLoopsLeft() > 0) {
                    onStop().emit(new StopEvent(this, loopsLeft.decrementAndGet()));
                    musicPlayer.prepare();
                    musicPlayer.start();
                }
                else {
                    onStop().emit(new StopEvent(this, 0));
                }
            }
            catch (UnsupportedAudioFileException | LineUnavailableException ignored) {
            }
        }
    }

}
