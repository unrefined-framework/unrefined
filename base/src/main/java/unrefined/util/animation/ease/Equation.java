package unrefined.util.animation.ease;

import unrefined.util.animation.Tween;

/**
 * Base class for every easing equation. You can create your own equations
 * and directly use them in the Tween engine by inheriting from this class.
 *
 * @see Tween
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Equation {

	/**
	 * Computes the next value of the interpolation.
	 *
	 * @param t The current time, between 0 and 1.
	 * @return The current value.
	 */
    public abstract float interpolate(float t);

}
