package unrefined.util.animation.interpolate;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Karstian Lee
 */
public class CatmullRom extends Interpolation {

	public static final CatmullRom INSTANCE = new CatmullRom();

	@Override
	public float interpolate(float t, float[] points, int offset, int length) {
		int segment = (int) Math.floor((length -1) * t);
		segment = Math.max(segment, 0);
		segment = Math.min(segment, length -2);

		t = t * (length -1) - segment;

		if (segment == 0) {
			return catmullRomSpline(points[offset], points[offset], points[offset + 1], points[offset + 2], t);
		}

		if (segment == length -2) {
			return catmullRomSpline(points[offset + length -3], points[offset + length -2], points[offset + length -1], points[offset + length -1], t);
		}

		return catmullRomSpline(points[offset + segment-1], points[offset + segment], points[offset + segment+1], points[offset + segment+2], t);
	}

	private float catmullRomSpline(float a, float b, float c, float d, float t) {
		float t1 = (c - a) * 0.5f;
		float t2 = (d - b) * 0.5f;

		float h1 = +2 * t * t * t - 3 * t * t + 1;
		float h2 = -2 * t * t * t + 3 * t * t;
		float h3 = t * t * t - 2 * t * t + t;
		float h4 = t * t * t - t * t;

		return b * h1 + c * h2 + t1 * h3 + t2 * h4;
	}

}
