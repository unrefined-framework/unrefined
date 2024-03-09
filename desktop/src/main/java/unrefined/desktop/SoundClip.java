package unrefined.desktop;

import unrefined.io.IOStreams;
import unrefined.math.FastMath;
import unrefined.util.FastArray;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static unrefined.desktop.AudioSupport.*;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class SoundClip implements AutoCloseable {

	private final LinkedBlockingDeque<SoundClipCursor> available;
	private final float[] pcm;

	private final int frameLength;
	private final SoundClipCursor[] cursors;
	private final int polyphony;
	public int getPolyphony() {
		return polyphony;
	}

	private SoundClipPlayer player;
	private SoundMuxer soundMuxer;
	public SoundMuxer getSoundMuxer() {
		return soundMuxer;
	}

	private volatile boolean open;
	public boolean isOpen() {
		return open;
	}
	private volatile boolean playing;
	public boolean isPlaying() {
		return playing;
	}

	private final List<SoundListener> listeners;

	public void addSoundListener(SoundListener listener) {
		listeners.add(listener);
	}

	public void removeSoundListener(SoundListener listener) {
		listeners.remove(listener);
	}

	public SoundClip(float[] pcm, int polyphony) {
		this.pcm = pcm;
		this.frameLength = pcm.length / 2;
		this.polyphony = polyphony;
		
		available = new LinkedBlockingDeque<>(polyphony);
		cursors = new SoundClipCursor[polyphony];
		
		for (int i = 0; i < polyphony; i ++) {
			cursors[i] = new SoundClipCursor(i);
			cursors[i].resetInstance();
			available.add(cursors[i]);
		}

		listeners = new CopyOnWriteArrayList<>();
	}

	public SoundClip(AudioInputStream stream, int polyphony) throws IOException {
		this(load(getSupportedAudioInputStream(DEFAULT_AUDIO_FORMAT, stream)), polyphony);
	}

	private static float[] load(AudioInputStream stream) throws IOException {
		byte[] buffer = IOStreams.readNBytes(stream, FastArray.ARRAY_LENGTH_MAX / 2);
		IOStreams.closeQuietly(stream);

		// stereo output, so two entries per frame
		float[] tmp = new float[buffer.length * 2];
		int bufferIndex = 0;
		for (int i = 0; i < tmp.length; i ++) {
			tmp[i] = ((buffer[bufferIndex ++] & 0xFF) | (buffer[bufferIndex ++] << 8)) / 32767f;
		}

		return tmp;
	}

	public float[] copyPCM() {
		return Arrays.copyOf(pcm, pcm.length);
	}

	public void open() throws LineUnavailableException {
		open(null, DEFAULT_BUFFER_FRAMES, Thread.MAX_PRIORITY);
	}

	public void open(int bufferFrames) throws LineUnavailableException {
		open(null, bufferFrames, Thread.MAX_PRIORITY);
	}

	private static final AtomicInteger nextSerialNumber = new AtomicInteger();
	private static int serialNumber() {
		return nextSerialNumber.getAndIncrement();
	}
	public void open(Mixer mixer, int bufferFrames, int threadPriority) throws LineUnavailableException {
		if (open) return;

		threadPriority = FastMath.clamp(threadPriority, Thread.MIN_PRIORITY, Thread.MAX_PRIORITY);
		
		player = new SoundClipPlayer(mixer, bufferFrames);
		Thread t = new Thread(player, "SoundClipPlayback-" + serialNumber());

		t.setPriority(threadPriority);
		open = true;
		t.start();

		broadcastEvent(new SoundEvent(this, SoundEvent.Type.OPEN, NOT_SPECIFIED, 0));
	}

	public void open(SoundMuxer soundMuxer) {
		if (open) return;
		open = true;
		// default: SoundClip is open
		playing = true;
		this.soundMuxer = soundMuxer;

		soundMuxer.addClip(this);
		soundMuxer.updateClips();

		broadcastEvent(new SoundEvent(this, SoundEvent.Type.OPEN, NOT_SPECIFIED, NOT_SPECIFIED));
	}

	public void close() {
		if (!open) return;

		if (soundMuxer != null) {
			soundMuxer.removeClip(this);
			soundMuxer.updateClips();
			soundMuxer = null;
		}
		else player.stopRunning();
		
		open = false;
		broadcastEvent(new SoundEvent(this, SoundEvent.Type.CLOSE, NOT_SPECIFIED, NOT_SPECIFIED));
	}

	public int getFrameLength() {
		return frameLength;
	}

	public long getMicrosecondLength() {
		return (long)((frameLength * 1_000_000.0) / DEFAULT_AUDIO_FORMAT.getFrameRate());
	}

	public int obtainInstance() {
		SoundClipCursor acc = available.pollLast();
		
		if (acc == null) return NOT_SPECIFIED;
		else {
			acc.isActive = true;
			broadcastEvent(new SoundEvent(this, SoundEvent.Type.OBTAIN_INSTANCE, acc.id, acc.cursor));
			return acc.id;
		}
	}

	public void releaseInstance(int instanceID) {
		cursors[instanceID].resetInstance();
		available.offerFirst(cursors[instanceID]);
		broadcastEvent(new SoundEvent(this, SoundEvent.Type.RELEASE_INSTANCE, instanceID, cursors[instanceID].cursor));
	}

	public int play() {
		return play(1.0, 1.0, 1, 0);
	}
	
	public int play(double leftVolume, double rightVolume) {
		return play(leftVolume, rightVolume, 1, 0);
	}
	
	public int play(double volume) {
		return play(volume, volume);
	}
	
	public int play(double leftVolume, double rightVolume, double speed, int loop) {
		int instanceID = obtainInstance();
		if (instanceID < 0) return instanceID;
		
		setVolume(instanceID, leftVolume, rightVolume);
		setSpeed(instanceID, speed);
		setLooping(instanceID, loop);
		setRecycleWhenDone(instanceID, true);
		
		start(instanceID);

		return instanceID;
	}

	public void start(int instanceID) {
		checkActive(instanceID);
		checkNotPlaying(instanceID);

		cursors[instanceID].instantaneousUpdate();
		cursors[instanceID].isPlaying = true;
		broadcastEvent(new SoundEvent(this, SoundEvent.Type.START_INSTANCE, instanceID, cursors[instanceID].cursor));
	}

	public void stop(int instanceID) {
		checkActive(instanceID);
		
		cursors[instanceID].isPlaying = false;
		broadcastEvent(new SoundEvent(this, SoundEvent.Type.STOP_INSTANCE, instanceID, cursors[instanceID].cursor));
	}

	public double getFramePosition(int instanceID) {
		checkActive(instanceID);

		return cursors[instanceID].cursor;
	}

	private void checkActive(int instanceID) {
		if (!cursors[instanceID].isActive) {
			throw new IllegalStateException("instance: " + instanceID + " is inactive");
		}
	}

	private void checkNotPlaying(int instanceID) {
		if (cursors[instanceID].isPlaying) {
			throw new IllegalStateException("You need to call the function before instance: " + instanceID + " playing");
		}
	}

	public void seekToFrames(int instanceID, double framePosition) {
		checkActive(instanceID);
		checkNotPlaying(instanceID);
		
		cursors[instanceID].cursor = FastMath.clamp(framePosition, 0, getFrameLength() - 1);
	}
	
	public void seekToMicroseconds(int instanceID, long microsecondPosition) {
		checkActive(instanceID);
		checkNotPlaying(instanceID);

		double frames = Math.min(((double) DEFAULT_AUDIO_FORMAT.getFrameRate() * microsecondPosition) / 1000_000.0, FastArray.ARRAY_LENGTH_MAX / 2.0);
		cursors[instanceID].cursor = FastMath.clamp(frameLength, 0, frames);
	}
	
	public double getLeftVolume(int instanceID) {
		checkActive(instanceID);
		
		return cursors[instanceID].isPlaying ? 
				cursors[instanceID].leftVolume : cursors[instanceID].newTargetLeftVolume; 				
	}
	
	public double getRightVolume(int instanceID) {
		checkActive(instanceID);
		
		return cursors[instanceID].isPlaying ? 
				cursors[instanceID].rightVolume : cursors[instanceID].newTargetRightVolume; 				
	}
	
	public void setLeftVolume(int instanceID, double leftVolume) {
		checkActive(instanceID);

		cursors[instanceID].newTargetLeftVolume = FastMath.clamp(leftVolume, 0, 1);
	}
	
	public void setRightVolume(int instanceID, double rightVolume) {
		checkActive(instanceID);
		
		cursors[instanceID].newTargetRightVolume = FastMath.clamp(rightVolume, 0, 1);
	}
	
	public void setVolume(int instanceID, double volume) {
		setVolume(instanceID, volume, volume);
	}
	
	public void setVolume(int instanceID, double leftVolume, double rightVolume) {
		checkActive(instanceID);

		cursors[instanceID].newTargetLeftVolume = FastMath.clamp(leftVolume, 0, 1);
		cursors[instanceID].newTargetRightVolume = FastMath.clamp(rightVolume, 0, 1);
	}
	
	public double getSpeed(int instanceID) {
		checkActive(instanceID);

		return cursors[instanceID].isPlaying ? cursors[instanceID].speed : cursors[instanceID].newTargetSpeed; 
	}
	
	public void setSpeed(int instanceID, double speed) {
		checkActive(instanceID);

		cursors[instanceID].newTargetSpeed = FastMath.clamp(speed, 0.125, 8);
	}

	public int getLooping(int instanceID) {
		checkActive(instanceID);
		return cursors[instanceID].loop;
	}
	
	public void setLooping(int instanceID, int loops) {
		checkActive(instanceID);
		
		cursors[instanceID].loop = Math.max(loops, -1);
	}
	
	public void setRecycleWhenDone(int instanceID, boolean recycleWhenDone) {
		checkActive(instanceID);
		
		cursors[instanceID].recycleWhenDone = recycleWhenDone;
	}
	
	public boolean isActive(int instanceID) {
		return cursors[instanceID].isActive;
	}
	
	public boolean isPlaying(int instanceID) {
		return cursors[instanceID].isPlaying;
	}
	
	private static class SoundClipCursor {
		volatile boolean isPlaying;
		volatile boolean isActive;
		final int id;
		
		double cursor;
		double speed;
		double leftVolume, rightVolume;
		int loop;
		boolean recycleWhenDone;

		double newTargetSpeed;
		double targetSpeed;
		double targetSpeedIncr;
		int targetSpeedSteps;
		
		double newTargetLeftVolume;
		double targetLeftVolume;
		double targetLeftVolumeIncr;
		
		double newTargetRightVolume;
		double targetRightVolume;
		double targetRightVolumeIncr;

		int targetLeftVolumeSteps, targetRightVolumeSteps;
		
		SoundClipCursor(int instanceID) {
			this.id = instanceID;
		}
		
		/*
		 * Used to clear settings from previous plays
		 * and put in default settings.
		 */
		private void resetInstance() {
			isActive = false;
			isPlaying = false;
			cursor = 0;
			
			leftVolume = rightVolume = 0;
			newTargetLeftVolume = newTargetRightVolume = 0;
			targetLeftVolume = targetRightVolume = 0;
			targetLeftVolumeSteps = targetRightVolumeSteps = 0;
			
			speed = 1;
			newTargetSpeed = 1;
			targetSpeed = 1;
			targetSpeedSteps = 0;
			
			loop = 0;
			recycleWhenDone = false;
		}
		
		private void instantaneousUpdate() {
			if (!isActive) throw new IllegalStateException("instance: " + id + " is inactive");
			if (isPlaying) throw new IllegalStateException("You need to call the function before instance: " + id + " playing");
			
			// OK to execute instantaneous changes
			leftVolume = newTargetLeftVolume;
			targetLeftVolume = newTargetLeftVolume;
			rightVolume = newTargetRightVolume;
			targetRightVolume = newTargetRightVolume;
			targetLeftVolumeSteps = targetRightVolumeSteps = 0;
			
			speed = newTargetSpeed;
			targetSpeed = newTargetSpeed;
			targetSpeedSteps = 0;
		}
		
	}
	
	private class SoundClipPlayer implements Runnable {

		private final SourceDataLine sourceDataLine;
		private final int sdlBufferSize;
		private final float[] audioData;
		private final byte[] audioBytes;
		
		public void stopRunning() {
			open = false;
		}
		
		SoundClipPlayer(Mixer mixer, int bufferFrames) throws LineUnavailableException {
			// twice the frames length, because stereo
			audioData = new float[bufferFrames * 2];
			// SourceDataLine must be 4 * number of frames, to 
			// account for 16-bit encoding and stereo.
			sdlBufferSize = bufferFrames * 4;
			audioBytes = new byte[sdlBufferSize];

			sourceDataLine = (SourceDataLine) (mixer == null ? AudioSystem.getLine(DEFAULT_AUDIO_LINE_INFO) : mixer.getLine(DEFAULT_AUDIO_LINE_INFO));
			sourceDataLine.open(DEFAULT_AUDIO_FORMAT, sdlBufferSize);
			sourceDataLine.start();
		}
		
		public void run() {
			while(open) {
				read(audioData);
				fromPcmToAudioBytes(audioBytes, audioData);
				sourceDataLine.write(audioBytes, 0, sdlBufferSize);
			}
			sourceDataLine.drain();
			sourceDataLine.close();
		}
	}
	
	void read(float[] buffer) {
		read(buffer, 0, buffer.length);
	}
	
	void read(float[] buffer, int offset, int length) {
		if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
		else if (offset + length > buffer.length) throw new ArrayIndexOutOfBoundsException(offset + length);
		// Start with 0-filled buffer, send out silence
		// if nothing playing.
		Arrays.fill(buffer, offset, offset + length, 0);
		
		for (int ci = 0; ci < polyphony; ci ++) {
			if (cursors[ci].isPlaying) {
				SoundClipCursor acc = cursors[ci];
				
				for (int i = offset; i < length; i += 2) {
					// has volume setting changed? if so recalc
					if (acc.newTargetLeftVolume != acc.targetLeftVolume) {
						acc.targetLeftVolume = acc.newTargetLeftVolume;
						acc.targetLeftVolumeIncr = (acc.targetLeftVolume - acc.leftVolume) / SOUND_VOLUME_STEPS;
						acc.targetLeftVolumeSteps = SOUND_VOLUME_STEPS;
					}
					if (acc.newTargetRightVolume != acc.targetRightVolume) {
						acc.targetRightVolume = acc.newTargetRightVolume;
						acc.targetRightVolumeIncr = (acc.targetRightVolume - acc.rightVolume) / SOUND_VOLUME_STEPS;
						acc.targetRightVolumeSteps = SOUND_VOLUME_STEPS;
					}
					// adjust volume if needed
					if (acc.targetLeftVolumeSteps -- > 0) {
						acc.leftVolume += acc.targetLeftVolumeIncr;
						if (acc.targetLeftVolumeSteps == 0) {
							acc.leftVolume = acc.targetLeftVolume;
						}
					}
					if (acc.targetRightVolumeSteps -- > 0) {
						acc.rightVolume += acc.targetRightVolumeIncr;
						if (acc.targetRightVolumeSteps == 0) {
							acc.rightVolume = acc.targetRightVolume;
						}
					}
					
					// get audioVals, with LERP for fractional cursor position
					float[] audioVals = new float[2];
					if (acc.cursor == (int) acc.cursor) {
						audioVals[0] = pcm[(int) acc.cursor * 2];
						audioVals[1] = pcm[(int) acc.cursor * 2 + 1];
					} 
					else readFractionalFrame(audioVals, acc.cursor);
					
					buffer[i] += audioVals[0] * acc.leftVolume;
					buffer[i + 1] += audioVals[1] * acc.rightVolume;
					
					// SET UP FOR NEXT ITERATION
					// has speed setting changed? if so, recalc
					if (acc.newTargetSpeed != acc.targetSpeed) {
						acc.targetSpeed = acc.newTargetSpeed;
						acc.targetSpeedIncr = (acc.targetSpeed - acc.speed) / SOUND_SPEED_STEPS;
						acc.targetSpeedSteps = SOUND_SPEED_STEPS;
					}
					// adjust speed if needed
					if (acc.targetSpeedSteps -- > 0) {
						acc.speed += acc.targetSpeedIncr;
					}
	
					// set NEXT read position
					acc.cursor += acc.speed;
					
					// test for "eof" and "looping"
					if (acc.cursor > (frameLength - 1)) {
						// keep looping indefinitely
						if (acc.loop == -1) {
							acc.cursor = 0;
							broadcastEvent(new SoundEvent(this, SoundEvent.Type.LOOP_INSTANCE, acc.id, acc.cursor));
						}
						// loop specific number of times
						else if (acc.loop > 0) {
							acc.loop --;
							acc.cursor = 0;
							broadcastEvent(new SoundEvent(this, SoundEvent.Type.LOOP_INSTANCE, acc.id, acc.cursor));
						}
						// no more loops to do
						else {
							acc.isPlaying = false;
							broadcastEvent(new SoundEvent(this, SoundEvent.Type.STOP_INSTANCE, acc.id, acc.cursor));
							if (acc.recycleWhenDone) {
								acc.resetInstance();
								available.offerFirst(acc);
								broadcastEvent(new SoundEvent(this, SoundEvent.Type.RELEASE_INSTANCE, acc.id, acc.cursor));
							}
							// cursor is at end of clip before
							// buffer filled, no need to
							// process further (default 0's)
							break;
						}
					}
				}
			}
		}
	}
	
	private void readFractionalFrame(float[] audioVals, double index) {
		int intIndex = (int) index;
		int stereoIndex = intIndex * 2;
		
		audioVals[0] = (float)(pcm[stereoIndex + 2] * (index - intIndex)
				+ pcm[stereoIndex] * ((intIndex + 1) - index));
		
		audioVals[1] = (float)(pcm[stereoIndex + 3] * (index - intIndex)
				+ pcm[stereoIndex + 1] * ((intIndex + 1) - index));
	}

	private void broadcastEvent(SoundEvent event) {
		for (SoundListener listener : listeners) {
			listener.update(event);
		}
	}

}
