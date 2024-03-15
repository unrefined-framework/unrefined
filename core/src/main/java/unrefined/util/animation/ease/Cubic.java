package unrefined.util.animation.ease;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Cubic extends Equation {

	public static final Cubic IN = new Cubic() {
		@Override
		public float interpolate(float t) {
			return t*t*t;
		}
		@Override
		public String toString() {
			return "Cubic.IN";
		}
	};

	public static final Cubic OUT = new Cubic() {
		@Override
		public float interpolate(float t) {
			return (t-=1)*t*t + 1;
		}
		@Override
		public String toString() {
			return "Cubic.OUT";
		}
	};

	public static final Cubic INOUT = new Cubic() {
		@Override
		public float interpolate(float t) {
			if ((t*=2) < 1) return 0.5f*t*t*t;
			return 0.5f * ((t-=2)*t*t + 2);
		}
		@Override
		public String toString() {
			return "Cubic.INOUT";
		}
	};

}