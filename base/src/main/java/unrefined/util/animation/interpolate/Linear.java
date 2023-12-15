package unrefined.util.animation.interpolate;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Karstian Lee
 */
public class Linear extends Interpolation {

	public static final Linear INSTANCE = new Linear();

	@Override
	public float interpolate(float t, float[] points, int offset, int length) {
		int segment = (int) Math.floor((length -1) * t);
		segment = Math.max(segment, 0);
		segment = Math.min(segment, length -2);

		t = t * (length -1) - segment;

		return points[offset + segment] + t * (points[offset + segment+1] - points[offset + segment]);
	}

}
