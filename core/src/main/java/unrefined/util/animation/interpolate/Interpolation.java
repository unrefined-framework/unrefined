package unrefined.util.animation.interpolate;

/**
 * Base class for every interpolation path. You can create your own paths and directly use
 * them in the Tween engine by inheriting from this class.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Karstian Lee
 */
public abstract class Interpolation {

	/**
	 * Computes the next value of the interpolation, based on its waypoints and
	 * the current progress.
	 *
	 * @param t The progress of the interpolation, between 0 and 1. May be out
	 * of these bounds if the easing equation involves some kind of rebounds.
	 * @param points The waypoints of the tween, from start to target values.
	 * @param offset The read offset of the point array.
	 * @param length The number of valid points in the array.
	 * @return The next value of the interpolation.
	 */
    public abstract float interpolate(float t, float[] points, int offset, int length);

	public float interpolate(float t, float[] points) {
		return interpolate(t, points, 0, points.length);
	}
	
}
