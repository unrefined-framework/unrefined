package unrefined.util.animation;

import unrefined.util.NotInstantiableError;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.SignalSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static unrefined.util.animation.Timeline.Mode.PARALLEL;
import static unrefined.util.animation.Timeline.Mode.SEQUENCE;

/**
 * A Timeline can be used to create complex animations made of sequences and
 * parallel sets of Tweens.
 * <p/>
 *
 * The following example will create an animation sequence composed of 5 parts:
 * <p/>
 *
 * 1. First, opacity and scale are set to 0 (with Tween.set() calls).<br/>
 * 2. Then, opacity and scale are animated in parallel.<br/>
 * 3. Then, the animation is paused for 1s.<br/>
 * 4. Then, position is animated to x=100.<br/>
 * 5. Then, rotation is animated to 360°.
 * <p/>
 *
 * This animation will be repeated 5 times, with a 500ms delay between each
 * iteration:
 * <br/><br/>
 *
 * <pre> {@code
 * Timeline.createSequence()
 *     .push(Tween.set(myObject, OPACITY).target(0))
 *     .push(Tween.set(myObject, SCALE).target(0, 0))
 *     .beginParallel()
 *          .push(Tween.to(myObject, OPACITY, 0.5f).target(1).ease(Quad.INOUT))
 *          .push(Tween.to(myObject, SCALE, 0.5f).target(1, 1).ease(Quad.INOUT))
 *     .end()
 *     .pushPause(1.0f)
 *     .push(Tween.to(myObject, POSITION_X, 0.5f).target(100).ease(Quad.INOUT))
 *     .push(Tween.to(myObject, ROTATION, 0.5f).target(360).ease(Quad.INOUT))
 *     .repeat(5, 0.5f)
 *     .start(myManager);
 * }</pre>
 *
 * @see Tween
 * @see AnimationManager
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Karstian Lee
 */
public final class Timeline extends Animation {

	public static final class Mode {
		private Mode() {
			throw new NotInstantiableError(Mode.class);
		}
		/**
		 * The children of the timeline will
		 * be delayed so that they are triggered one after the other.
		 */
		public static final int SEQUENCE = 0;
		/**
		 * The children of the timeline will be
		 * triggered all at once.
		 */
		public static final int PARALLEL = 1;
		public static int checkValid(int mode) {
			if (mode < SEQUENCE || mode > PARALLEL) throw new IllegalArgumentException("Illegal timeline mode: " + mode);
			else return mode;
		}
		public static boolean isValid(int mode) {
			return mode >= SEQUENCE && mode <= PARALLEL;
		}
		public static String toString(int mode) {
			switch (mode) {
				case SEQUENCE: return "SEQUENCE";
				case PARALLEL: return "PARALLEL";
				default: throw new IllegalArgumentException("Illegal timeline mode: " + mode);
			}
		}
	}

	// -------------------------------------------------------------------------
	// Static -- factories
	// -------------------------------------------------------------------------

	public final static class Editor {
		private final Timeline timeline;
		private Editor(Timeline timeline, int mode) {
			this.timeline = timeline;
			timeline.setup(mode);
		}
		public Editor onStateChange(SignalSlot<EventSlot<StateChangeEvent>> consumer) {
			consumer.accept(timeline.onStateChange());
			return this;
		}
		/**
		 * Adds a Tween to the current timeline.
		 */
		public Editor push(Tween tween) {
			timeline.push(tween);
			return this;
		}
		/**
		 * Nests a Timeline in the current one.
		 */
		public Editor push(Timeline timeline) {
			timeline.push(timeline);
			return this;
		}
		/**
		 * Adds a pause to the timeline. The pause may be negative if you want to
		 * overlap the preceding and following children.
		 *
		 * @param time A positive or negative duration.
		 */
		public Editor pushPause(float time) {
			timeline.pushPause(time);
			return this;
		}
		/**
		 * Starts a nested timeline with the specified mode. Don't forget to
		 * call {@link #end()} to close this nested timeline.
		 */
		public Editor begin(int mode) {
			timeline.begin(mode);
			return this;
		}
		/**
		 * Starts a nested timeline with a 'sequence' behavior. Don't forget to
		 * call {@link #end()} to close this nested timeline.
		 */
		public Editor beginSequence() {
			timeline.beginSequence();
			return this;
		}
		/**
		 * Starts a nested timeline with a 'parallel' behavior. Don't forget to
		 * call {@link #end()} to close this nested timeline.
		 */
		public Editor beginParallel() {
			timeline.beginParallel();
			return this;
		}
		/**
		 * Closes the last nested timeline.
		 */
		public Editor end() {
			timeline.end();
			return this;
		}
		/**
		 * Sets the delay of the animation.
		 *
		 * @param delay A duration.
		 */
		public Editor delay(float delay) {
			timeline.setDelay(delay);
			return this;
		}
		/**
		 * Repeats the animation for a given number of times.
		 *
		 * @param count The number of repetitions. For infinite repetition, pass '-1'.
		 */
		public Editor repeatCount(int count) {
			timeline.setRepeatCount(count);
			return this;
		}
		/**
		 * Sets the delay of each repetition.
		 *
		 * @param delay A delay between each repetition.
		 */
		public Editor repeatDelay(int delay) {
			timeline.setRepeatDelay(delay);
			return this;
		}
		/**
		 * If true, the animation will be played backwards every two repetitions.
		 *
		 * @param yoyo Whether the animation repeats 'yoyo'.
		 */
		public Editor yoyo(boolean yoyo) {
			timeline.setYoyo(yoyo);
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
			timeline.setRepeat(count, delay, yoyo);
			return this;
		}
		/**
		 * Gets the animation, leave it unstarted.
		 */
		public Timeline unstarted() {
			timeline.unstarted();
			return timeline;
		}
		/**
		 * Starts or restarts the object unmanaged. You will need to take care of
		 * its life-cycle. If you want the tween to be managed for you, use a
		 * {@link AnimationManager}.
		 */
		public Timeline start() {
			timeline.start();
			return timeline;
		}
		/**
		 * Convenience method to add an object to a manager. Its life-cycle will be
		 * handled for you. Relax and enjoy the animation.
		 */
		public Timeline start(AnimationManager manager) {
			timeline.start(manager);
			return timeline;
		}
	}

	/**
	 * Creates a new timeline editor with the specified mode.
	 */
	public static Editor build(int mode) {
		return new Editor(new Timeline(), mode);
	}

	/**
	 * Creates a new timeline editor with a 'sequence' behavior.
	 * The children of the timeline will
	 * be delayed so that they are triggered one after the other.
	 */
	public static Editor sequence() {
		return new Editor(new Timeline(), SEQUENCE);
	}

	/**
	 * Creates a new timeline editor with a 'parallel' behavior.
	 * The children of the timeline will be
	 * triggered all at once.
	 */
	public static Editor parallel() {
		return new Editor(new Timeline(), PARALLEL);
	}

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	private final List<Animation> children = new ArrayList<>(10);
	private Timeline current;
	private Timeline parent;
	private int mode;
	private boolean built;

	// -------------------------------------------------------------------------
	// Setup
	// -------------------------------------------------------------------------

	private Timeline() {
		reset();
	}

	private void setup(int mode) {
		this.mode = Mode.checkValid(mode);
		this.current = this;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Creates a new timeline editor of this timeline with current mode.
	 */
	public Editor edit() {
		return new Editor(this, mode);
	}

	/**
	 * Creates a new timeline editor of this timeline with the specified mode.
	 */
	public Editor edit(int mode) {
		return new Editor(this, mode);
	}

	/**
	 * Creates a new timeline editor with a 'parallel behavior.
	 * The children of the timeline will be
	 * triggered all at once.
	 */
	public Editor editSequence() {
		return new Editor(this, mode);
	}

	/**
	 * Creates a new timeline editor with a 'parallel behavior.
	 * The children of the timeline will be
	 * triggered all at once.
	 */
	public Editor editParallel() {
		return new Editor(this, mode);
	}

	/**
	 * Adds a Tween to the current timeline.
	 */
	public void push(Tween tween) {
		if (built) throw new IllegalStateException("You can't push anything to a timeline once it is started");
		current.children.add(tween);
	}

	/**
	 * Nests a Timeline in the current one.
	 */
	public void push(Timeline timeline) {
		if (built) throw new IllegalStateException("You can't push anything to a timeline once it is started");
		if (timeline.current != timeline) throw new IllegalStateException("You forgot to call a few 'end()' statements in your pushed timeline");
		timeline.parent = current;
		current.children.add(timeline);
	}

	/**
	 * Adds a pause to the timeline. The pause may be negative if you want to
	 * overlap the preceding and following children.
	 *
	 * @param time A positive or negative duration.
	 */
	public void pushPause(float time) {
		if (built) throw new IllegalStateException("You can't push anything to a timeline once it is started");
		current.children.add(Tween.build().mark().delay(time).unstarted());
	}

	/**
	 * Starts a nested timeline with the specified mode. Don't forget to
	 * call {@link #end()} to close this nested timeline.
	 */
	public void begin(int mode) {
		if (built) throw new IllegalStateException("You can't push anything to a timeline once it is started");
		Timeline timeline = new Timeline();
		timeline.parent = current;
		timeline.mode = Mode.checkValid(mode);
		current.children.add(timeline);
		current = timeline;
	}

	/**
	 * Starts a nested timeline with a 'sequence' behavior. Don't forget to
	 * call {@link #end()} to close this nested timeline.
	 */
	public void beginSequence() {
		if (built) throw new IllegalStateException("You can't push anything to a timeline once it is started");
		Timeline timeline = new Timeline();
		timeline.parent = current;
		timeline.mode = SEQUENCE;
		current.children.add(timeline);
		current = timeline;
	}

	/**
	 * Starts a nested timeline with a 'parallel' behavior. Don't forget to
	 * call {@link #end()} to close this nested timeline.
	 */
	public void beginParallel() {
		if (built) throw new IllegalStateException("You can't push anything to a timeline once it is started");
		Timeline timeline = new Timeline();
		timeline.parent = current;
		timeline.mode = PARALLEL;
		current.children.add(timeline);
		current = timeline;
	}

	/**
	 * Closes the last nested timeline.
	 */
	public void end() {
		if (built) throw new IllegalStateException("You can't push anything to a timeline once it is started");
		if (current == this) throw new IllegalStateException("Nothing to end...");
		else current = current.parent;
	}

	/**
	 * Gets a list of the timeline children. If the timeline is started, the
	 * list will be immutable.
	 */
	public List<Animation> getChildren() {
		if (built) return Collections.unmodifiableList(current.children);
		else return current.children;
	}

	// -------------------------------------------------------------------------
	// Overrides
	// -------------------------------------------------------------------------

	@Override
	public void start() {
		super.start();

		for (int i=0; i<children.size(); i++) {
			Animation obj = children.get(i);
			obj.start();
		}
	}

	@Override
	public void reset() {
		for (int i=children.size()-1; i>=0; i--) {
			Animation obj = children.remove(i);
			obj.reset();
		}

		super.reset();

		children.clear();
		current = parent = null;

		built = false;
	}

	@Override
	public boolean isIdentity() {
		return super.isIdentity() && children.isEmpty() && current == null && parent == null && !built;
	}

	@Override
	protected void unstarted() {
		if (built) return;

		duration = 0;

		for (int i=0; i<children.size(); i++) {
			Animation obj = children.get(i);

			if (obj.getRepeatCount() < 0) throw new IllegalStateException("You can't push an object with infinite repetitions in a timeline");
			obj.unstarted();

			switch (mode) {
				case SEQUENCE:
					float tDelay = duration;
					duration += obj.getFullDuration();
					obj.delay += tDelay;
					break;

				case PARALLEL:
					duration = Math.max(duration, obj.getFullDuration());
					break;
			}
		}

		built = true;
	}

	@Override
	protected void updateOverride(int step, int lastStep, boolean isIterationStep, float delta) {
		if (!isIterationStep && step > lastStep) {
			assert delta >= 0;
			float dt = isReverse(lastStep) ? -delta-1 : delta+1;
			for (int i=0, n=children.size(); i<n; i++) children.get(i).update(dt);
			return;
		}

		if (!isIterationStep && step < lastStep) {
			assert delta <= 0;
			float dt = isReverse(lastStep) ? -delta-1 : delta+1;
			for (int i=children.size()-1; i>=0; i--) children.get(i).update(dt);
			return;
		}

		assert isIterationStep;

		if (step > lastStep) {
			if (isReverse(step)) {
				forceEndValues();
				for (int i=0, n=children.size(); i<n; i++) children.get(i).update(delta);
			} else {
				forceStartValues();
				for (int i=0, n=children.size(); i<n; i++) children.get(i).update(delta);
			}

		} else if (step < lastStep) {
			if (isReverse(step)) {
				forceStartValues();
				for (int i=children.size()-1; i>=0; i--) children.get(i).update(delta);
			} else {
				forceEndValues();
				for (int i=children.size()-1; i>=0; i--) children.get(i).update(delta);
			}

		} else {
			float dt = isReverse(step) ? -delta : delta;
			if (delta >= 0) for (int i=0, n=children.size(); i<n; i++) children.get(i).update(dt);
			else for (int i=children.size()-1; i>=0; i--) children.get(i).update(dt);
		}
	}

	// -------------------------------------------------------------------------
	// Animation impl.
	// -------------------------------------------------------------------------

	@Override
	protected void forceStartValues() {
		for (int i=children.size()-1; i>=0; i--) {
			Animation obj = children.get(i);
			obj.forceToStart();
		}
	}

	@Override
	protected void forceEndValues() {
		for (int i=0, n=children.size(); i<n; i++) {
			Animation obj = children.get(i);
			obj.forceToEnd(duration);
		}
	}

	@Override
	protected boolean containsTarget(Object target) {
		for (int i=0, n=children.size(); i<n; i++) {
			Animation obj = children.get(i);
			if (obj.containsTarget(target)) return true;
		}
		return false;
	}

	@Override
	protected boolean containsTarget(Object target, int tweenType) {
		for (int i=0, n=children.size(); i<n; i++) {
			Animation obj = children.get(i);
			if (obj.containsTarget(target, tweenType)) return true;
		}
		return false;
	}

}
