package unrefined.util.animation.ease;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Sine extends Equation {

	private static final float PI = 3.14159265f;

	public static final Sine IN = new Sine() {
		@Override
		public float interpolate(float t) {
			return (float) -Math.cos(t * (PI/2)) + 1;
		}
		@Override
		public String toString() {
			return "Sine.IN";
		}
	};

	public static final Sine OUT = new Sine() {
		@Override
		public float interpolate(float t) {
			return (float) Math.sin(t * (PI/2));
		}
		@Override
		public String toString() {
			return "Sine.OUT";
		}
	};

	public static final Sine INOUT = new Sine() {
		@Override
		public float interpolate(float t) {
			return -0.5f * ((float) Math.cos(PI*t) - 1);
		}
		@Override
		public String toString() {
			return "Sine.INOUT";
		}
	};

}