package unrefined.util.animation.ease;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Karstian Lee
 */
public abstract class Back extends Equation {

	public static final Back IN = new Back(1.70158f) {
		@Override
		public float interpolate(float t) {
			float s = this.s;
			return t*t*((s+1)*t - s);
		}
		@Override
		public String toString() {
			return "Back.IN";
		}
	};

	public static final Back OUT = new Back(1.70158f) {
		@Override
		public float interpolate(float t) {
			float s = this.s;
			return (t-=1)*t*((s+1)*t + s) + 1;
		}
		@Override
		public String toString() {
			return "Back.OUT";
		}
	};

	public static final Back INOUT = new Back(1.70158f) {
		@Override
		public float interpolate(float t) {
			float s = this.s;
			if ((t*=2) < 1) return 0.5f*(t*t*(((s*=(1.525f))+1)*t - s));
			return 0.5f*((t-=2)*t*(((s*=(1.525f))+1)*t + s) + 2);
		}
		@Override
		public String toString() {
			return "Back.INOUT";
		}
	};

	// -------------------------------------------------------------------------

	final float s;

	public Back(float s) {
		this.s = s;
	}

}