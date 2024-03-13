package unrefined.desktop;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static unrefined.desktop.AudioSupport.*;

public class SoundMuxer implements AutoCloseable {

	private SoundClip[] clipCache;
	private final CopyOnWriteArrayList<SoundClip> clipManager;
	private volatile boolean clipCacheUpdated;
	private int clipCount;

	public int getClipCount() {
		return clipCount;
	}

	public int getClipCacheCount() {
		return clipCache == null ? 0 : clipCache.length;
	}

	private final int bufferFrames;
	public int getBufferFrames() {
		return bufferFrames;
	}
	private final int readBufferSize;
	public int getReadBufferSize() {
		return readBufferSize;
	}
	private final int sdlByteBufferSize;
	public int getSourceDataLineBufferSize() {
		return sdlByteBufferSize;
	}

	private final Mixer mixer;
	public Mixer getMixer() {
		return mixer;
	}

	private final int threadPriority;
	public int getThreadPriority() {
		return threadPriority;
	}

	private volatile boolean playing;
	public boolean isPlaying() {
		return playing;
	}

	public SoundMuxer() {
		this(DEFAULT_BUFFER_FRAMES);
	}

	public SoundMuxer(int bufferFrames) {
		this(null, bufferFrames, Thread.MAX_PRIORITY);
	}

	public SoundMuxer(Mixer mixer, int threadPriority) {
		this(mixer, DEFAULT_BUFFER_FRAMES, threadPriority);
	}

	public SoundMuxer(Mixer mixer, int bufferFrames, int threadPriority) {
		clipManager = new CopyOnWriteArrayList<>();
		this.bufferFrames = bufferFrames;
		this.readBufferSize = bufferFrames * 2;
		this.sdlByteBufferSize = bufferFrames * 4;
		this.mixer = mixer;
		this.threadPriority = threadPriority;
	}

	public void addClip(SoundClip clip) {
		clipManager.add(clip);
	}

	public void removeClip(SoundClip clip) {
		clipManager.remove(clip);
	}

	public void updateClips() {
		int size = clipManager.size();
		SoundClip[] workCopyClips = new SoundClip[size];
		for (int i = 0; i < size; i ++) {
			workCopyClips[i] = clipManager.get(i);
		}
		clipCache = workCopyClips;
		clipCacheUpdated = true;
	}

	private static final AtomicInteger nextSerialNumber = new AtomicInteger();
	private static int serialNumber() {
		return nextSerialNumber.getAndIncrement();
	}

	public void start() throws LineUnavailableException {
		if (playing) return;
		playing = true;
		
		updateClips();
		
		SoundMuxerPlayer player = new SoundMuxerPlayer(mixer);
		Thread t = new Thread(player, "SoundMuxerPlayback-" + serialNumber());
		t.setDaemon(true);
		t.setPriority(threadPriority);
		t.start();
	}

	public void stop() {
		if (!playing) return;
		playing = false;
	}

	@Override
	public void close() {
		stop();
	}
	
	private final class SoundMuxerPlayer implements Runnable {
		private final SourceDataLine sourceDataLine;
		private final float[] readBuffer;
		private final float[] audioData;
		private final byte[] audioBytes;
		private SoundClip[] mixerClips;

		private SoundMuxerPlayer(Mixer mixer) throws LineUnavailableException {
			audioBytes = new byte[sdlByteBufferSize];
			readBuffer = new float[readBufferSize];
			audioData = new float[readBufferSize];

			sourceDataLine = (SourceDataLine) (mixer == null ? AudioSystem.getLine(DEFAULT_AUDIO_LINE_INFO) : mixer.getLine(DEFAULT_AUDIO_LINE_INFO));
			sourceDataLine.open(DEFAULT_AUDIO_FORMAT, sdlByteBufferSize);
			sourceDataLine.start();
		}

		public void run() {
			while(playing) {
		    	if (clipCacheUpdated) {
		    		/*
		    		 * Concurrency plan: Better to allow a late  
		    		 * or redundant update than to skip an update.
		    		 */
		    		clipCacheUpdated = false;
		    		mixerClips = clipCache;
		    		clipCount = mixerClips.length;
		    	}
				Arrays.fill(readBuffer, 0);
				fillBufferFromClips(readBuffer);
				fromPcmToAudioBytes(audioBytes, readBuffer);
				sourceDataLine.write(audioBytes, 0, sdlByteBufferSize);
			}

			sourceDataLine.drain();
			sourceDataLine.close();
		}
		
	   private void fillBufferFromClips(float[] normalizedOut) {
	    	// loop through all clips, summing
			for (int n = 0; n < clipCount; n ++) {
				if (mixerClips[n].isPlaying()) {
					try {
						mixerClips[n].read(audioData, 0, readBufferSize);
						for (int i = 0; i < readBufferSize; i ++) {
							normalizedOut[i] += audioData[i];
						}
					} 
					catch (Exception ignored) {
					}							
				}
				for (int i = 0; i < readBufferSize; i ++) {
					if (normalizedOut[i] > 1) normalizedOut[i] = 1;
					else if (normalizedOut[i] < -1) normalizedOut[i] = -1;
				}
			}
		}

	}

}