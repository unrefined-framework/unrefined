package unrefined.util;

/**
 * A quite simple frames-per-second counter. NOT thread-safe.
 * Usage:
 * <pre> {@code
 *     FPSCounter counter = new FPSCounter(60);
 *     ...
 *     void render() {
 *         counter.update();
 *
 *         doRender();
 *         ...
 *     }
 * }</pre>
 */
public final class FPSCounter {

	private int size = 0;

	private final int maximum;
	private int average = 0;
	private final double[] fpsBuffer;
	private int fpsBufferIndex = 0;
	private long lastTime = -1;
	private long elapsed = 0;
	private float delta = 0;

	public FPSCounter(int maximum, int count) {
		if (maximum < 0) throw new IllegalArgumentException("maximum fps cannot be < 0");
		this.maximum = maximum;
		this.fpsBuffer = new double[count];
	}

	public FPSCounter(int maximum) {
		this(maximum, 15);
	}

	public int getCount() {
		return fpsBuffer.length;
	}

	public int getMaximum() {
		return maximum;
	}

	public void update() {
		if (lastTime == -1) lastTime = System.nanoTime();
		else {
			long nowTime = System.nanoTime();
			updateBuffer((elapsed = (nowTime - lastTime)) / 1.e9);
			delta = elapsed / 1_000_000_000f;
			calculate();
			lastTime = nowTime;
		}
	}

	/**
	 * @return the elapsed time between frames, in nanoseconds.
	 */
	public long getElapsed() {
		return elapsed;
	}

	/**
	 * @return the elapsed time between frames, in seconds.
	 */
	public float getDelta() {
		return delta;
	}

	/**
	 * @return the calculated average fps.
	 */
	public int getAverage() {
		return average;
	}

	private void updateBuffer(double deltaTime) {
		fpsBuffer[fpsBufferIndex ++] = 1.0 / deltaTime;
		fpsBufferIndex %= fpsBuffer.length;
		size = Math.min(size + 1, fpsBuffer.length);
	}

	private void calculate() {
		double sum = 0;
		for (double f : fpsBuffer) {
			sum += f;
		}
		average = (int) Math.min(maximum, Math.round(sum / size));
	}

}