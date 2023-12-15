package unrefined.util.animation.ease;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Karstian Lee
 */
public abstract class Elastic extends Equation {

	private static final float PI = 3.14159265f;

	public static final Elastic IN = new Elastic(0, .3f) {
		@Override
		public float interpolate(float t) {
			float a = this.a;
			float p = this.p;
			if (t==0) return 0;  if (t==1) return 1;
			float s;
			if (a < 1) { a=1; s=p/4; }
			else s = p/(2*PI) * (float)Math.asin(1/a);
			return -(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t-s)*(2*PI)/p ));
		}
		@Override
		public String toString() {
			return "Elastic.IN";
		}
	};

	public static final Elastic OUT = new Elastic(0, .3f) {
		@Override
		public float interpolate(float t) {
			float a = this.a;
			float p = this.p;
			if (t==0) return 0;  if (t==1) return 1;
			float s;
			if (a < 1) { a=1; s=p/4; }
			else s = p/(2*PI) * (float)Math.asin(1/a);
			return a*(float)Math.pow(2,-10*t) * (float)Math.sin( (t-s)*(2*PI)/p ) + 1;
		}
		@Override
		public String toString() {
			return "Elastic.OUT";
		}
	};

	public static final Elastic INOUT = new Elastic(0, .3f*1.5f) {
		@Override
		public float interpolate(float t) {
			float a = this.a;
			float p = this.p;
			if (t==0) return 0;  if ((t*=2)==2) return 1;
			float s;
			if (a < 1) { a=1; s=p/4; }
			else s = p/(2*PI) * (float)Math.asin(1/a);
			if (t < 1) return -.5f*(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t-s)*(2*PI)/p ));
			return a*(float)Math.pow(2,-10*(t-=1)) * (float)Math.sin( (t-s)*(2*PI)/p )*.5f + 1;
		}
		@Override
		public String toString() {
			return "Elastic.INOUT";
		}
	};

	// -------------------------------------------------------------------------

	final float a;
	final float p;

	public Elastic(float a, float p) {
		this.a = a;
		this.p = p;
	}

}