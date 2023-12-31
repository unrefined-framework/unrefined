package unrefined.util.animation;

import unrefined.util.NotInstantiableError;
import unrefined.util.Resettable;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.Signal;

import static unrefined.util.animation.Animation.Flag.*;

/**
 * Animation is the base class of Tween and Timeline. It defines the
 * iteration engine used to play animations for any number of times, and in
 * any direction, at any speed.
 * <p/>
 *
 * It is responsible for calling the different callbacks at the right moments,
 * and for making sure that every callbacks are triggered, even if the update
 * engine gets a big delta time at once.
 *
 * @see Tween
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Karstian Lee
 */
public abstract class Animation implements Resettable {

	public static final class Flag {
		private Flag() {
			throw new NotInstantiableError(Flag.class);
		}
		public static final int BEGIN = 1;
		public static final int START = 1 << 1;
		public static final int END = 1 << 2;
		public static final int COMPLETE = 1 << 3;
		public static final int BACK_BEGIN = 1 << 4;
		public static final int BACK_START = 1 << 5;
		public static final int BACK_END = 1 << 6;
		public static final int BACK_COMPLETE = 1 << 7;
		public static final int ANY_FORWARD = 0x0F;
		public static final int ANY_BACKWARD = 0xF0;
		public static final int ANY = 0xFF;
		public static int removeUnusedBits(int flags) {
			return flags << 8 >>> 8;
		}
		public static String toString(int flags) {
			flags = removeUnusedBits(flags);
			StringBuilder builder = new StringBuilder("[");
			if ((flags & BEGIN) != 0) builder.append("BEGIN, ");
			if ((flags & START) != 0) builder.append("START, ");
			if ((flags & END) != 0) builder.append("END, ");
			if ((flags & COMPLETE) != 0) builder.append(", COMPLETE, ");
			if ((flags & BACK_BEGIN) != 0) builder.append("BACK_BEGIN, ");
			if ((flags & BACK_START) != 0) builder.append("BACK_START, ");
			if ((flags & BACK_END) != 0) builder.append("BACK_END, ");
			if ((flags & BACK_COMPLETE) != 0) builder.append("BACK_COMPLETE, ");
			int length = builder.length();
			if (length > 1) builder.setLength(length - 2);
			builder.append("]");
			return builder.toString();
		}
	}

	public static final class StateChangeEvent extends Event<Animation> {

		private final int flags;

		public StateChangeEvent(Animation source, int flags) {
			super(source);
			this.flags = flags;
		}

		public int getFlags() {
			return flags;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;

			StateChangeEvent that = (StateChangeEvent) o;

            return flags == that.flags;
        }

		@Override
		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + flags;
			return result;
		}

		@Override
		public String toString() {
			return getClass().getName()
					+ '{' +
					"source=" + getSource() +
					", flags=" + Flag.toString(flags) +
					'}';
		}

	}

	// General
	private int step;
	private int repeatCount;
	private boolean iterationStep;
	private boolean yoyo;

	// Timings
	float delay;
	float duration;
	private float repeatDelay;
	private float currentTime;
	private float deltaTime;
	private boolean started; // true when the object is started
	private boolean initialized; // true after the delay
	private boolean finished; // true when all repetitions are done
	private boolean cancelled; // true if stop() was called
	private boolean paused; // true if pause() was called

	// Misc
	private final Signal<EventSlot<StateChangeEvent>> onStateChange = Signal.ofSlot();
	public Signal<EventSlot<StateChangeEvent>> onStateChange() {
		return onStateChange;
	}
	private int stateChangeTriggers;

	// Package access
	boolean autoRemoveEnabled;
	boolean autoStartEnabled;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Starts or restarts the object unmanaged. You will need to take care of
	 * its life-cycle. If you want the tween to be managed for you, use a
	 * {@link AnimationManager}.
	 */
	public void start() {
		unstarted();
		currentTime = 0;
		started = true;
	}

	/**
	 * Convenience method to add an object to a manager. Its life-cycle will be
	 * handled for you. Relax and enjoy the animation.
	 */
	public void start(AnimationManager manager) {
		manager.add(this);
	}

	/**
	 * Sets the delay of the animation.
	 *
	 * @param delay A duration.
	 */
	public void setDelay(float delay) {
		this.delay = delay;
	}

	/**
	 * Stops the animation. If you are using an AnimationManager, this object
	 * will be removed automatically.
	 */
	public void stop() {
		cancelled = true;
	}

	/**
	 * Stops and resets the animation for possible later reuse.
	 * Note that if you use a {@link AnimationManager}, this method
	 * is automatically called once the animation is finished.
	 */
	@Override
	public void reset() {
		step = -2;
		repeatCount = 0;
		iterationStep = yoyo = false;

		delay = duration = repeatDelay = currentTime = deltaTime = 0;
		started = initialized = finished = cancelled = paused = false;

		onStateChange.clear();
		stateChangeTriggers = COMPLETE;

		autoRemoveEnabled = autoStartEnabled = true;
	}

	@Override
	public boolean isIdentity() {
		return step == -2 && repeatCount == 0 && !iterationStep && !yoyo &&
				delay == 0 && duration == 0 && repeatDelay == 0 && currentTime == 0 && deltaTime == 0 &&
				!started && !initialized && !finished && !cancelled && !paused &&
				onStateChange.isEmpty() && stateChangeTriggers == COMPLETE &&
				autoRemoveEnabled && autoStartEnabled;
	}

	/**
	 * Pauses the animation. Further update calls won't have any effect.
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * Resumes the animation. Has no effect is it was not already paused.
	 */
	public void resume() {
		paused = false;
	}

	/**
	 * Repeats the animation for a given number of times.
	 *
	 * @param count The number of repetitions. For infinite repetition, pass '-1'.
	 */
	public void setRepeatCount(int count) {
		if (started) throw new IllegalStateException("You can't change the repetitions of an animation once it is started");
		repeatCount = count;
	}

	/**
	 * Sets the delay of each repetition.
	 *
	 * @param delay A delay between each repetition.
	 */
	public void setRepeatDelay(int delay) {
		if (started) throw new IllegalStateException("You can't change the repetitions of an animation once it is started");
		repeatDelay = Math.max(delay, 0);
	}

	/**
	 * If true, the animation will be played backwards every two repetitions.
	 *
	 * @param yoyo Whether the animation repeats 'yoyo'.
	 */
	public void setYoyo(boolean yoyo) {
		if (started) throw new IllegalStateException("You can't change the repetitions of an animation once it is started");
		this.yoyo = yoyo;
	}

	/**
	 * Repeats the animation for a given number of times.
	 * If yoyo = true, every two repetitions the animation will be played backwards.
	 *
	 * @param count The number of repetitions. For infinite repetition, pass '-1'.
	 * @param delay A delay before each repetition.
	 * @param yoyo Whether the animation repeats 'yoyo'.
	 */
	public void setRepeat(int count, float delay, boolean yoyo) {
		if (started) throw new IllegalStateException("You can't change the repetitions of an animation once it is started");
		repeatCount = count;
		repeatDelay = Math.max(delay, 0);
		this.yoyo = yoyo;
	}

	/**
	 * Changes the triggers of the callback. The available triggers, listed as
	 * members of the {@link Flag} class, are:
	 * <p/>
	 *
	 * <b>BEGIN</b>: right after the delay (if any)<br/>
	 * <b>START</b>: at each iteration beginning<br/>
	 * <b>END</b>: at each iteration ending, before the repeat delay<br/>
	 * <b>COMPLETE</b>: at last END event<br/>
	 * <b>BACK_BEGIN</b>: at the beginning of the first backward iteration<br/>
	 * <b>BACK_START</b>: at each backward iteration beginning, after the repeat delay<br/>
	 * <b>BACK_END</b>: at each backward iteration ending<br/>
	 * <b>BACK_COMPLETE</b>: at last BACK_END event
	 * <p/>
	 *
	 * <pre> {@code
	 * forward :      BEGIN                                   COMPLETE
	 * forward :      START    END      START    END      START    END
	 * |--------------[XXXXXXXXXX]------[XXXXXXXXXX]------[XXXXXXXXXX]
	 * backward:      bEND  bSTART      bEND  bSTART      bEND  bSTART
	 * backward:      bCOMPLETE                                 bBEGIN
	 * }</pre>
	 *
	 * @param flags one or more triggers, separated by the '|' operator.
	 * @see Flag
	 */
	public void setStateChangeTriggers(int flags) {
		this.stateChangeTriggers = flags;
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	/**
	 * Gets the delay of the animation. Nothing will happen before
	 * this delay.
	 */
	public float getDelay() {
		return delay;
	}

	/**
	 * Gets the duration of a single iteration.
	 */
	public float getDuration() {
		return duration;
	}

	/**
	 * Gets the number of iterations that will be played.
	 */
	public int getRepeatCount() {
		return repeatCount;
	}

	/**
	 * Gets the delay occuring between two iterations.
	 */
	public float getRepeatDelay() {
		return repeatDelay;
	}

	/**
	 * Returns the complete duration, including initial delay and repetitions.
	 * The formula is as follows:
	 * <pre>
	 * fullDuration = delay + duration + (repeatDelay + duration) * repeatCount
	 * </pre>
	 */
	public float getFullDuration() {
		if (repeatCount < 0) return -1;
		return delay + duration + (repeatDelay + duration) * repeatCount;
	}

	/**
	 * Gets the id of the current step. Values are as follows:<br/>
	 * <ul>
	 * <li>even numbers mean that an iteration is playing,<br/>
	 * <li>odd numbers mean that we are between two iterations,<br/>
	 * <li>-2 means that the initial delay has not ended,<br/>
	 * <li>-1 means that we are before the first iteration,<br/>
	 * <li>repeatCount*2 + 1 means that we are after the last iteration
	 */
	public int getStep() {
		return step;
	}

	/**
	 * Gets the local time.
	 */
	public float getCurrentTime() {
		return currentTime;
	}

	/**
	 * Returns true if the animation has been started.
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Returns true if the animation has been initialized. Starting
	 * values for tweens are stored at initialization time. This initialization
	 * takes place right after the initial delay, if any.
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Returns true if the tween is finished (i.e. if the animation has reached
	 * its end or has been cancelled). If you don't use a AnimationManager, you may
	 * want to call {@link #reset()} to reuse the object later.
	 */
	public boolean isFinished() {
		return finished || cancelled;
	}

	/**
	 * Returns true if the iterations are played as yoyo. Yoyo means that
	 * every two iterations, the animation will be played backwards.
	 */
	public boolean isYoyo() {
		return yoyo;
	}

	/**
	 * Returns true if the animation is currently paused.
	 */
	public boolean isPaused() {
		return paused;
	}

	// -------------------------------------------------------------------------
	// Abstract API
	// -------------------------------------------------------------------------

	protected abstract void unstarted();

	protected abstract void forceStartValues();
	protected abstract void forceEndValues();

	protected abstract boolean containsTarget(Object target);
	protected abstract boolean containsTarget(Object target, int tweenType);

	// -------------------------------------------------------------------------
	// Protected API
	// -------------------------------------------------------------------------

	protected void setDuration(float duration) {
		this.duration = duration;
	}

	protected void initializeOverride() {
	}

	protected void updateOverride(int step, int lastStep, boolean isIterationStep, float delta) {
	}

	protected void forceToStart() {
		currentTime = -delay;
		step = -1;
		iterationStep = false;
		if (isReverse(0)) forceEndValues();
		else forceStartValues();
	}

	protected void forceToEnd(float time) {
		currentTime = time - getFullDuration();
		step = repeatCount *2 + 1;
		iterationStep = false;
		if (isReverse(repeatCount *2)) forceStartValues();
		else forceEndValues();
	}

	protected void onStateChangeEvent(int flags) {
		if ((stateChangeTriggers & flags) > 0 && !onStateChange.isEmpty()) {
			onStateChange.emit(new StateChangeEvent(this, flags));
		}
	}

	protected boolean isReverse(int step) {
		return yoyo && Math.abs(step%4) == 2;
	}

	protected boolean isValid(int step) {
		return (step >= 0 && step <= repeatCount *2) || repeatCount < 0;
	}

	protected void cancelTarget(Object target) {
		if (containsTarget(target)) stop();
	}

	protected void cancelTarget(Object target, int tweenType) {
		if (containsTarget(target, tweenType)) stop();
	}

	// -------------------------------------------------------------------------
	// Update engine
	// -------------------------------------------------------------------------

	/**
	 * Updates the animation state. <b>You may want to use a
	 * AnimationManager to update objects for you.</b>
	 * <p>
	 * Slow motion, fast motion and backward play can be easily achieved by
	 * tweaking this delta time. Multiply it by -1 to play the animation
	 * backward, or by 0.5 to play it twice slower than its normal speed.
	 *
	 * @param delta A delta time between now and the last call.
	 */
	public void update(float delta) {
		if (!started || paused || cancelled) return;

		deltaTime = delta;

		if (!initialized) {
			initialize();
		}

		if (initialized) {
			testRelaunch();
			updateStep();
			testCompletion();
		}

		currentTime += deltaTime;
		deltaTime = 0;
	}

	private void initialize() {
		if (currentTime+deltaTime >= delay) {
			initializeOverride();
			initialized = true;
			iterationStep = true;
			step = 0;
			deltaTime -= delay-currentTime;
			currentTime = 0;
			onStateChangeEvent(BEGIN);
			onStateChangeEvent(START);
		}
	}

	private void testRelaunch() {
		if (!iterationStep && repeatCount >= 0 && step < 0 && currentTime+deltaTime >= 0) {
			assert step == -1;
			iterationStep = true;
			step = 0;
			float delta = 0-currentTime;
			deltaTime -= delta;
			currentTime = 0;
			onStateChangeEvent(BEGIN);
			onStateChangeEvent(START);
			updateOverride(step, step-1, iterationStep, delta);

		} else if (!iterationStep && repeatCount >= 0 && step > repeatCount *2 && currentTime+deltaTime < 0) {
			assert step == repeatCount *2 + 1;
			iterationStep = true;
			step = repeatCount *2;
			float delta = 0-currentTime;
			deltaTime -= delta;
			currentTime = duration;
			onStateChangeEvent(BACK_BEGIN);
			onStateChangeEvent(BACK_START);
			updateOverride(step, step+1, iterationStep, delta);
		}
	}

	private void updateStep() {
		while (isValid(step)) {
			if (!iterationStep && currentTime+deltaTime <= 0) {
				iterationStep = true;
				step -= 1;

				float delta = 0-currentTime;
				deltaTime -= delta;
				currentTime = duration;

				if (isReverse(step)) forceStartValues(); else forceEndValues();
				onStateChangeEvent(BACK_START);
				updateOverride(step, step+1, iterationStep, delta);

			} else if (!iterationStep && currentTime+deltaTime >= repeatDelay) {
				iterationStep = true;
				step += 1;

				float delta = repeatDelay-currentTime;
				deltaTime -= delta;
				currentTime = 0;

				if (isReverse(step)) forceEndValues(); else forceStartValues();
				onStateChangeEvent(START);
				updateOverride(step, step-1, iterationStep, delta);

			} else if (iterationStep && currentTime+deltaTime < 0) {
				iterationStep = false;
				step -= 1;

				float delta = 0-currentTime;
				deltaTime -= delta;
				currentTime = 0;

				updateOverride(step, step+1, iterationStep, delta);
				onStateChangeEvent(BACK_END);

				if (step < 0 && repeatCount >= 0) onStateChangeEvent(BACK_COMPLETE);
				else currentTime = repeatDelay;

			} else if (iterationStep && currentTime+deltaTime > duration) {
				iterationStep = false;
				step += 1;

				float delta = duration-currentTime;
				deltaTime -= delta;
				currentTime = duration;

				updateOverride(step, step-1, iterationStep, delta);
				onStateChangeEvent(END);

				if (step > repeatCount *2 && repeatCount >= 0) onStateChangeEvent(COMPLETE);
				currentTime = 0;

			} else if (iterationStep) {
				float delta = deltaTime;
				deltaTime -= delta;
				currentTime += delta;
				updateOverride(step, step, iterationStep, delta);
				break;

			} else {
				float delta = deltaTime;
				deltaTime -= delta;
				currentTime += delta;
				break;
			}
		}
	}

	private void testCompletion() {
		finished = repeatCount >= 0 && (step > repeatCount *2 || step < 0);
	}

}
