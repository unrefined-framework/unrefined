package unrefined.util.animation;

import unrefined.util.animation.ease.Equation;
import unrefined.util.animation.ease.Quad;
import unrefined.util.animation.interpolate.CatmullRom;
import unrefined.util.animation.interpolate.Interpolation;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.SignalSlot;

import java.util.HashMap;
import java.util.Map;

/**
 * Core class of the Tween Engine. A Tween is basically an interpolation
 * between two values of an object attribute. However, the main interest of a
 * Tween is that you can apply an easing formula on this interpolation, in
 * order to smooth the transitions or to achieve cool effects like springs or
 * bounces.
 * <p/>
 *
 * The Universal Tween Engine is called "universal" because it is able to apply
 * interpolations on every attribute from every possible object. Therefore,
 * every object in your application can be animated with cool effects: it does
 * not matter if your application is a game, a desktop interface or even a
 * console program! If it makes sense to animate something, then it can be
 * animated through this engine.
 * <p/>
 *
 * This class contains many static factory methods to create and instantiate
 * new interpolations easily. The common way to create a Tween is by using one
 * of these factories:
 * <p/>
 *
 * - Tween.to(...)<br/>
 * - Tween.from(...)<br/>
 * - Tween.set(...)<br/>
 * - Tween.call(...)
 * <p/>
 *
 * <h2>Example - firing a Tween</h2>
 *
 * The following example will move the target horizontal position from its
 * current value to x=200 and y=300, during 500ms, but only after a delay of
 * 1000ms. The animation will also be repeated 2 times (the starting position
 * is registered at the end of the delay, so the animation will automatically
 * restart from this registered position).
 * <p/>
 *
 * <pre> {@code
 * Tween.to(myObject, POSITION_XY, 0.5f)
 *      .target(200, 300)
 *      .ease(Quad.INOUT)
 *      .delay(1.0f)
 *      .repeat(2, 0.2f)
 *      .start(myManager);
 * }</pre>
 *
 * Tween life-cycles can be automatically managed for you, thanks to the
 * {@link AnimationManager} class. If you choose to manage your tween when you start
 * it, then you don't need to care about it anymore. <b>Tweens are
 * <i>fire-and-forget</i>: don't think about them anymore once you started
 * them (if they are managed of course).</b>
 * <p/>
 *
 * You need to periodicaly update the tween engine, in order to compute the new
 * values. If your tweens are managed, only update the manager; else you need
 * to call {@link #update(float)} on your tweens periodically.
 * <p/>
 *
 * <h2>Example - setting up the engine</h2>
 *
 * The engine cannot directly change your objects attributes, since it doesn't
 * know them. Therefore, you need to tell him how to get and set the different
 * attributes of your objects: <b>you need to implement the {@link
 * Animator} interface for each object class you will animate</b>. Once
 * done, don't forget to register these implementations, using the static method
 * {@link #registerAnimator(Class, Animator)}, when you start your application.
 *
 * @see Animator
 * @see AnimationManager
 * @see Equation
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Karstian Lee
 */
public final class Tween extends Animation {

	// -------------------------------------------------------------------------
	// Static -- animators
	// -------------------------------------------------------------------------

	private static final Map<Class<?>, Animator<?>> registeredAnimators = new HashMap<>();

	/**
	 * Registers an animator with the class of an object. This animator will be
	 * used by tweens applied to every objects implementing the registered
	 * class, or inheriting from it.
	 *
	 * @param someClass An object class.
	 * @param defaultAnimator The animator that will be used to tween any
	 * object of class "someClass".
	 */
	public static void registerAnimator(Class<?> someClass, Animator<?> defaultAnimator) {
		registeredAnimators.put(someClass, defaultAnimator);
	}

	/**
	 * Gets the registered Animator associated with the given object class.
	 *
	 * @param someClass An object class.
	 */
	public static Animator<?> getRegisteredAnimator(Class<?> someClass) {
		return registeredAnimators.get(someClass);
	}

	// -------------------------------------------------------------------------
	// Static -- factories
	// -------------------------------------------------------------------------

	public final static class Editor {
		private final Tween tween;
		private Editor(Tween tween) {
			this.tween = tween;
		}
		public Editor onStateChange(SignalSlot<EventSlot<StateChangeEvent>> consumer) {
			consumer.accept(tween.onStateChange());
			return this;
		}
		/**
		 * Sets the easing equation of the tween. Existing equations are located in
		 * <i>aurelienribon.tweenengine.equations</i> package, but you can of course
		 * implement your owns, see {@link Equation}. Default equation is Quad.INOUT.
		 * <p/>
		 *
		 * <b>Proposed equations are:</b><br/>
		 * - Linear.INOUT,<br/>
		 * - Quad.IN | OUT | INOUT,<br/>
		 * - Cubic.IN | OUT | INOUT,<br/>
		 * - Quart.IN | OUT | INOUT,<br/>
		 * - Quint.IN | OUT | INOUT,<br/>
		 * - Circ.IN | OUT | INOUT,<br/>
		 * - Sine.IN | OUT | INOUT,<br/>
		 * - Expo.IN | OUT | INOUT,<br/>
		 * - Back.IN | OUT | INOUT,<br/>
		 * - Bounce.IN | OUT | INOUT,<br/>
		 * - Elastic.IN | OUT | INOUT
		 *
		 * @see Equation
		 */
		public Editor equation(Equation equation) {
			tween.setEquation(equation);
			return this;
		}
		/**
		 * Forces the tween to use the Animator registered with the given
		 * target class. Useful if you want to use a specific animator associated
		 * to an interface, for instance.
		 *
		 * @param targetClass A class registered with an animator.
		 */
		public Editor targetClass(Class<?> targetClass) {
			tween.setTargetClass(targetClass);
			return this;
		}
		/**
		 * Sets the target value of the interpolation.
		 * <p/>
		 *
		 * @param index The index of the interpolation values.
		 * @param targetValue The target value of the interpolation.
		 */
		public Editor target(int index, float targetValue) {
			tween.setTarget(index, targetValue);
			return this;
		}
		/**
		 * Sets the target values of the interpolation. The interpolation will run
		 * from the <b>values at start time (after the delay, if any)</b> to these
		 * target values.
		 * <p/>
		 *
		 * To sum-up:<br/>
		 * - start values: values at start time, after delay<br/>
		 * - end values: params
		 *
		 * @param targetValues The target values of the interpolation.
		 */
		public Editor target(float... targetValues) {
			tween.setTarget(targetValues);
			return this;
		}
		/**
		 * Sets the target value of the interpolation, relatively to the <b>value
		 * at start time (after the delay, if any)</b>.
		 * <p/>
		 *
		 * @param targetValue The relative target value of the interpolation.
		 */
		public Editor rTarget(int index, float targetValue) {
			tween.rTarget(index, targetValue);
			return this;
		}
		/**
		 * Sets the target values of the interpolation, relatively to the <b>values
		 * at start time (after the delay, if any)</b>.
		 * <p/>
		 *
		 * To sum-up:<br/>
		 * - start values: values at start time, after delay<br/>
		 * - end values: params + values at start time, after delay
		 *
		 * @param targetValues The relative target values of the interpolation.
		 */
		public Editor rTarget(float... targetValues) {
			tween.rTarget(targetValues);
			return this;
		}
		/**
		 * Adds a waypoint to the interpolation path. The default path runs from the start values
		 * to the end values linearly. If you add waypoints, the default path will
		 * use a smooth catmull-rom spline to navigate between the waypoints, but
		 * you can change this behavior by using the {@link #setInterpolation(Interpolation)}
		 * method.
		 * <p/>
		 * Note that if you want waypoints relative to the start values, use one of
		 * the .targetRelative() methods to define your target.
		 *
		 * @param targetValues The targets of this waypoint.
		 */
		public Editor addWaypoint(float... targetValues) {
			tween.addWaypoint(targetValues);
			return this;
		}
		/**
		 * Sets the algorithm that will be used to navigate through the waypoints,
		 * from the start values to the end values. Default is a catmull-rom spline.
		 *
		 * @param interpolation A Interpolation implementation.
		 * @see Interpolation
		 */
		public Editor interpolate(Interpolation interpolation) {
			tween.setInterpolation(interpolation);
			return this;
		}
		/**
		 * Sets the delay of the animation.
		 *
		 * @param delay A duration.
		 */
		public Editor delay(float delay) {
			tween.setDelay(delay);
			return this;
		}
		/**
		 * Repeats the animation for a given number of times.
		 *
		 * @param count The number of repetitions. For infinite repetition, pass '-1'.
		 */
		public Editor repeatCount(int count) {
			tween.setRepeatCount(count);
			return this;
		}
		/**
		 * Sets the delay of each repetition.
		 *
		 * @param delay A delay between each repetition.
		 */
		public Editor repeatDelay(int delay) {
			tween.setRepeatDelay(delay);
			return this;
		}
		/**
		 * If true, the animation will be played backwards every two repetitions.
		 *
		 * @param yoyo Whether the animation repeats 'yoyo'.
		 */
		public Editor yoyo(boolean yoyo) {
			tween.setYoyo(yoyo);
			return this;
		}
		/**
		 * Repeats the animation for a given number of times.
		 * If yoyo = true, every two repetitions the animation will be played backwards.
		 *
		 * @param count The number of repetitions. For infinite repetition, pass '-1'.
		 * @param delay A delay before each repetition.
		 * @param yoyo Whether the animation repeats 'yoyo'.
		 */
		public Editor repeat(int count, float delay, boolean yoyo) {
			tween.setRepeat(count, delay, yoyo);
			return this;
		}
		/**
		 * Gets the animation, leave it unstarted.
		 */
		public Tween unstarted() {
			tween.unstarted();
			return tween;
		}
		/**
		 * Starts or restarts the object unmanaged. You will need to take care of
		 * its life-cycle. If you want the tween to be managed for you, use a
		 * {@link AnimationManager}.
		 */
		public Tween start() {
			tween.start();
			return tween;
		}
		/**
		 * Convenience method to add an object to a manager. Its life-cycle will be
		 * handled for you. Relax and enjoy the animation.
		 */
		public Tween start(AnimationManager manager) {
			tween.start(manager);
			return tween;
		}
		/**
		 * Factory creating a new standard interpolation. This is the most common
		 * type of interpolation. The starting values are retrieved automatically
		 * after the delay (if any).
		 * <br/><br/>
		 *
		 * <b>You need to set the target values of the interpolation by using one
		 * of the target() methods</b>. The interpolation will run from the
		 * starting values to these target values.
		 * <br/><br/>
		 *
		 * The common use of Tweens is "fire-and-forget": you do not need to care
		 * for tweens once you added them to a AnimationManager, they will be updated
		 * automatically, and cleaned once finished. Common call:
		 * <br/><br/>
		 *
		 * <pre> {@code
		 * Tween.build().to(myObject, POSITION, 1.0f)
		 *      .target(50, 70)
		 *      .ease(Quad.INOUT)
		 *      .start(myManager);
		 * }</pre>
		 *
		 * Several options such as delay, repetitions and callbacks can be added to
		 * the tween.
		 *
		 * @param target The target object of the interpolation.
		 * @param tweenType The desired type of interpolation.
		 * @param duration The duration of the interpolation, in milliseconds.
		 * @return The generated Tween.
		 */
		public Editor to(Object target, int tweenType, float duration) {
			tween.setup(target, tweenType, duration);
			tween.setEquation(Quad.INOUT);
			tween.setInterpolation(CatmullRom.INSTANCE);
			return this;
		}
		/**
		 * Factory creating a new reversed interpolation. The ending values are
		 * retrieved automatically after the delay (if any).
		 * <br/><br/>
		 *
		 * <b>You need to set the starting values of the interpolation by using one
		 * of the target() methods</b>. The interpolation will run from the
		 * starting values to these target values.
		 * <br/><br/>
		 *
		 * The common use of Tweens is "fire-and-forget": you do not need to care
		 * for tweens once you added them to a AnimationManager, they will be updated
		 * automatically, and cleaned once finished. Common call:
		 * <br/><br/>
		 *
		 * <pre> {@code
		 * Tween.build().from(myObject, POSITION, 1.0f)
		 *      .target(0, 0)
		 *      .ease(Quad.INOUT)
		 *      .start(myManager);
		 * }</pre>
		 *
		 * Several options such as delay, repetitions and callbacks can be added to
		 * the tween.
		 *
		 * @param target The target object of the interpolation.
		 * @param tweenType The desired type of interpolation.
		 * @param duration The duration of the interpolation, in milliseconds.
		 * @return The generated Tween.
		 */
		public Editor from(Object target, int tweenType, float duration) {
			tween.setup(target, tweenType, duration);
			tween.setEquation(Quad.INOUT);
			tween.setInterpolation(CatmullRom.INSTANCE);
			tween.from = true;
			return this;
		}
		/**
		 * Factory creating a new instantaneous interpolation (thus this is not
		 * really an interpolation).
		 * <br/><br/>
		 *
		 * <b>You need to set the target values of the interpolation by using one
		 * of the target() methods</b>. The interpolation will set the target
		 * attribute to these values after the delay (if any).
		 * <br/><br/>
		 *
		 * The common use of Tweens is "fire-and-forget": you do not need to care
		 * for tweens once you added them to a AnimationManager, they will be updated
		 * automatically, and cleaned once finished. Common call:
		 * <br/><br/>
		 *
		 * <pre> {@code
		 * Tween.build().set(myObject, POSITION)
		 *      .target(50, 70)
		 *      .delay(1.0f)
		 *      .start(myManager);
		 * }</pre>
		 *
		 * Several options such as delay, repetitions and callbacks can be added to
		 * the tween.
		 *
		 * @param target The target object of the interpolation.
		 * @param tweenType The desired type of interpolation.
		 * @return The generated Tween.
		 */
		public Editor set(Object target, int tweenType) {
			tween.setup(target, tweenType, 0);
			tween.setEquation(Quad.INOUT);
			return this;
		}
		/**
		 * Factory creating a new timer. The given slot will be triggered on
		 * each iteration start, after the delay.
		 * <br/><br/>
		 *
		 * The common use of Tweens is "fire-and-forget": you do not need to care
		 * for tweens once you added them to a AnimationManager, they will be updated
		 * automatically, and cleaned once finished. Common call:
		 * <br/><br/>
		 *
		 * <pre> {@code
		 * Tween.build().call(myCallback)
		 *      .delay(1.0f)
		 *      .repeat(10, 1000)
		 *      .start(myManager);
		 * }</pre>
		 *
		 * @return The generated Tween.
		 */
		public Editor call(SignalSlot<EventSlot<StateChangeEvent>> consumer) {
			tween.setup(null, -1, 0);
			onStateChange(consumer);
			tween.setStateChangeTriggers(Flag.START);
			return this;
		}
		/**
		 * Convenience method to create an empty tween. Such object is only useful
		 * when placed inside animation sequences (see {@link Timeline}), in which
		 * it may act as a beacon, so you can set a callback on it in order to
		 * trigger some action at the right moment.
		 *
		 * @return The generated Tween.
		 * @see Timeline
		 */
		public Editor mark() {
			tween.setup(null, -1, 0);
			return this;
		}
	}

	/**
	 * Creates a new tween editor.
	 */
	public static Editor build() {
		return new Editor(new Tween());
	}

	/**
	 * Creates a new tween editor with specified limits.
	 */
	public static Editor build(int combinedAttrsLimit, int waypointsLimit) {
		return new Editor(new Tween(combinedAttrsLimit, waypointsLimit));
	}

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	// Main
	private Object target;
	private Class<?> targetClass;
	private Animator<Object> animator;
	private int type;
	private Equation equation;
	private Interpolation interpolation;

	// General
	private boolean from;
	private boolean relative;
	private int combinedAttrsCount;
	private int waypointsCount;

	// Values
	private final int combinedAttrsLimit;
	private final int waypointsLimit;

	private final float[] startValues;
	private final float[] targetValues;
	private final float[] waypoints;

	// Buffers
	private float[] animatorBuffer;
	private float[] interpolateBuffer;

	// -------------------------------------------------------------------------
	// Setup
	// -------------------------------------------------------------------------

	/**
	 * Constructs a tween.
	 * @param combinedAttrsLimit The limit for combined attributes. At least 3.
	 * @param waypointsLimit The limit of allowed waypoints for each tween. At least 0.
	 */
	public Tween(int combinedAttrsLimit, int waypointsLimit) {
		this.combinedAttrsLimit = Math.max(3, combinedAttrsLimit);
		this.waypointsLimit = Math.max(0, waypointsLimit);

		startValues = new float[combinedAttrsLimit];
		targetValues = new float[combinedAttrsLimit];
		waypoints = new float[waypointsLimit * combinedAttrsLimit];

		animatorBuffer = new float[combinedAttrsLimit];
		interpolateBuffer = new float[(2+waypointsLimit)*combinedAttrsLimit];

		reset();
	}

	/**
	 * Constructs a tween. Equivalent as {@code Tween(3, 0)}.
	 *
	 * @see Tween#Tween(int, int)
	 */
	public Tween() {
		this(3, 0);
	}

	private void setup(Object target, int tweenType, float duration) {
		if (duration < 0) throw new IllegalArgumentException("Duration can't be negative");

		onStateChange().clear();
		this.target = target;
		this.targetClass = target != null ? findTargetClass() : null;
		this.type = tweenType;
		this.duration = duration;
	}

	private Class<?> findTargetClass() {
		if (registeredAnimators.containsKey(target.getClass())) return target.getClass();
		if (target instanceof Animator) return target.getClass();

		Class<?> parentClass = target.getClass().getSuperclass();
		while (parentClass != null && !registeredAnimators.containsKey(parentClass))
			parentClass = parentClass.getSuperclass();

		return parentClass;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Creates a new tween editor of this tween.
	 */
	public Editor edit() {
		return new Editor(this);
	}

	/**
	 * Sets the easing equation of the tween. Existing equations are located in
	 * <i>aurelienribon.tweenengine.equations</i> package, but you can of course
	 * implement your owns, see {@link Equation}. Default equation is Quad.INOUT.
	 * <p/>
	 *
	 * <b>Proposed equations are:</b><br/>
	 * - Linear.INOUT,<br/>
	 * - Quad.IN | OUT | INOUT,<br/>
	 * - Cubic.IN | OUT | INOUT,<br/>
	 * - Quart.IN | OUT | INOUT,<br/>
	 * - Quint.IN | OUT | INOUT,<br/>
	 * - Circ.IN | OUT | INOUT,<br/>
	 * - Sine.IN | OUT | INOUT,<br/>
	 * - Expo.IN | OUT | INOUT,<br/>
	 * - Back.IN | OUT | INOUT,<br/>
	 * - Bounce.IN | OUT | INOUT,<br/>
	 * - Elastic.IN | OUT | INOUT
	 *
	 * @see Equation
	 */
	public void setEquation(Equation equation) {
		this.equation = equation;
	}

	/**
	 * Forces the tween to use the Animator registered with the given
	 * target class. Useful if you want to use a specific animator associated
	 * to an interface, for instance.
	 *
	 * @param targetClass A class registered with an animator.
	 */
	public void setTargetClass(Class<?> targetClass) {
		if (isStarted()) throw new IllegalStateException("You can't set the target class of a tween once it is started");
		this.targetClass = targetClass;
	}

	/**
	 * Sets the target value of the interpolation.
	 * <p/>
	 *
	 * @param index The index of the interpolation values.
	 * @param targetValue The target value of the interpolation.
	 */
	public void setTarget(int index, float targetValue) {
		targetValues[index] = targetValue;
	}

	/**
	 * Sets the target values of the interpolation. The interpolation will run
	 * from the <b>values at start time (after the delay, if any)</b> to these
	 * target values.
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params
	 *
	 * @param targetValues The target values of the interpolation.
	 */
	public void setTarget(float... targetValues) {
		if (targetValues.length > combinedAttrsLimit) throwCombinedAttrsLimitReached();
		System.arraycopy(targetValues, 0, this.targetValues, 0, targetValues.length);
	}

	/**
	 * Sets the target value of the interpolation, relatively to the <b>value
	 * at start time (after the delay, if any)</b>.
	 * <p/>
	 *
	 * @param targetValue The relative target value of the interpolation.
	 */
	public void rTarget(int index, float targetValue) {
		relative = true;
		targetValues[index] = isInitialized() ? targetValue + startValues[index] : targetValue;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>values
	 * at start time (after the delay, if any)</b>.
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at start time, after delay
	 *
	 * @param targetValues The relative target values of the interpolation.
	 */
	public void rTarget(float... targetValues) {
		if (targetValues.length > combinedAttrsLimit) throwCombinedAttrsLimitReached();
		for (int i=0; i<targetValues.length; i++) {
			this.targetValues[i] = isInitialized() ? targetValues[i] + startValues[i] : targetValues[i];
		}

		relative = true;
	}

	/**
	 * Adds a waypoint to the interpolation path. The default path runs from the start values
	 * to the end values linearly. If you add waypoints, the default path will
	 * use a smooth catmull-rom spline to navigate between the waypoints, but
	 * you can change this behavior by using the {@link #setInterpolation(Interpolation)}
	 * method.
	 * <p/>
	 * Note that if you want waypoints relative to the start values, use one of
	 * the .targetRelative() methods to define your target.
	 *
	 * @param targetValues The targets of this waypoint.
	 */
	public void addWaypoint(float... targetValues) {
		if (waypointsCount == waypointsLimit) throwWaypointsLimitReached();
		System.arraycopy(targetValues, 0, waypoints, waypointsCount *targetValues.length, targetValues.length);
		waypointsCount += 1;
	}

	/**
	 * Sets the algorithm that will be used to navigate through the waypoints,
	 * from the start values to the end values. Default is a catmull-rom spline.
	 *
	 * @param interpolation A Interpolation implementation.
	 * @see Interpolation
	 */
	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	/**
	 * Gets the limit for combined attributes.
	 */
	public int getCombinedAttrsLimit() {
		return combinedAttrsLimit;
	}

	/**
	 * Gets the limit of allowed waypoints for each tween.
	 */
	public int getWaypointsLimit() {
		return waypointsLimit;
	}

	/**
	 * Gets the target object.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Gets the type of the tween.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the easing equation.
	 */
	public Equation getEasing() {
		return equation;
	}

	/**
	 * Gets the target values. The returned buffer is as long as the maximum
	 * allowed combined values. Therefore, you're surely not interested in all
	 * its content. Use {@link #getCombinedAttributesCount()} to get the number of
	 * interesting slots.
	 */
	public float[] getTargetValues() {
		return targetValues;
	}

	/**
	 * Gets the number of combined animations.
	 */
	public int getCombinedAttributesCount() {
		return combinedAttrsCount;
	}

	/**
	 * Gets the Animator used with the target.
	 */
	public Animator<?> getAnimator() {
		return animator;
	}

	/**
	 * Gets the class that was used to find the associated Animator.
	 */
	public Class<?> getTargetClass() {
		return targetClass;
	}

	// -------------------------------------------------------------------------
	// Overrides
	// -------------------------------------------------------------------------

	@Override
	public void reset() {
		super.reset();

		target = null;
		targetClass = null;
		animator = null;
		type = -1;
		equation = null;
		interpolation = null;

		from = relative = false;
		combinedAttrsCount = waypointsCount = 0;

		if (animatorBuffer.length != combinedAttrsLimit) {
			animatorBuffer = new float[combinedAttrsLimit];
		}

		if (interpolateBuffer.length != (2+waypointsLimit)*combinedAttrsLimit) {
			interpolateBuffer = new float[(2+waypointsLimit)*combinedAttrsLimit];
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void unstarted() {
		if (target == null) return;

		animator = (Animator<Object>) registeredAnimators.get(targetClass);
		if (animator == null && target instanceof Animator) animator = (Animator<Object>) target;
		if (animator != null) combinedAttrsCount = animator.retrieve(target, type, animatorBuffer);
		else throw new IllegalStateException("No Animator was found for the target");

		if (combinedAttrsCount > combinedAttrsLimit) throwCombinedAttrsLimitReached();
	}

	@Override
	protected void initializeOverride() {
		if (target == null) return;

		animator.retrieve(target, type, startValues);

		for (int i = 0; i< combinedAttrsCount; i++) {
			targetValues[i] += relative ? startValues[i] : 0;

			for (int ii = 0; ii< waypointsCount; ii++) {
				waypoints[ii* combinedAttrsCount +i] += relative ? startValues[i] : 0;
			}

			if (from) {
				float tmp = startValues[i];
				startValues[i] = targetValues[i];
				targetValues[i] = tmp;
			}
		}
	}

	@Override
	protected void updateOverride(int step, int lastStep, boolean isIterationStep, float delta) {
		if (target == null || equation == null) return;

		// Case iteration end has been reached

		if (!isIterationStep && step > lastStep) {
			animator.animate(target, type, isReverse(lastStep) ? startValues : targetValues);
			return;
		}

		if (!isIterationStep && step < lastStep) {
			animator.animate(target, type, isReverse(lastStep) ? targetValues : startValues);
			return;
		}

		// Validation

		assert isIterationStep;
		assert getCurrentTime() >= 0;
		assert getCurrentTime() <= duration;

		// Case duration equals zero

		if (duration < 0.00000000001f && delta > -0.00000000001f) {
			animator.animate(target, type, isReverse(step) ? targetValues : startValues);
			return;
		}

		if (duration < 0.00000000001f && delta < 0.00000000001f) {
			animator.animate(target, type, isReverse(step) ? startValues : targetValues);
			return;
		}

		// Normal behavior

		float time = isReverse(step) ? duration - getCurrentTime() : getCurrentTime();
		float t = equation.interpolate(time/duration);

		if (waypointsCount == 0 || interpolation == null) {
			for (int i = 0; i< combinedAttrsCount; i++) {
				animatorBuffer[i] = startValues[i] + t * (targetValues[i] - startValues[i]);
			}

		} else {
			for (int i = 0; i< combinedAttrsCount; i++) {
				interpolateBuffer[0] = startValues[i];
				interpolateBuffer[1+ waypointsCount] = targetValues[i];
				for (int ii = 0; ii< waypointsCount; ii++) {
					interpolateBuffer[ii+1] = waypoints[ii* combinedAttrsCount +i];
				}

				animatorBuffer[i] = interpolation.interpolate(t, interpolateBuffer, 0, waypointsCount +2);
			}
		}

		animator.animate(target, type, animatorBuffer);
	}

	// -------------------------------------------------------------------------
	// Animation impl.
	// -------------------------------------------------------------------------

	@Override
	protected void forceStartValues() {
		if (target == null) return;
		animator.animate(target, type, startValues);
	}

	@Override
	protected void forceEndValues() {
		if (target == null) return;
		animator.animate(target, type, targetValues);
	}

	@Override
	protected boolean containsTarget(Object target) {
		return this.target == target;
	}

	@Override
	protected boolean containsTarget(Object target, int tweenType) {
		return this.target == target && this.type == tweenType;
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void throwCombinedAttrsLimitReached() {
		throw new IndexOutOfBoundsException("You cannot combine more than " + combinedAttrsLimit + " attributes in the tween.");
	}

	private void throwWaypointsLimitReached() {
		throw new IndexOutOfBoundsException("You cannot add more than " + waypointsLimit + " waypoints to the tween.");
	}

}
