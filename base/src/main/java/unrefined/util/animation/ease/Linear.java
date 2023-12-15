package unrefined.util.animation.ease;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Linear extends Equation {

	public static final Linear INOUT = new Linear() {
		@Override
		public float interpolate(float t) {
			return t;
		}
		@Override
		public String toString() {
			return "Linear.INOUT";
		}
	};

}
