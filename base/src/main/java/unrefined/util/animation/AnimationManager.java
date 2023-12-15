package unrefined.util.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An AnimationManager updates all your tweens and timelines at once.
 * Its main interest is that it handles the tween/timeline life-cycles for you,
 * as well as the pooling constraints (if object pooling is enabled).
 * <p/>
 *
 * Just give it a bunch of tweens or timelines and call update() periodically,
 * you don't need to care for anything else! Relax and enjoy your animations.
 *
 * @see Tween
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class AnimationManager {

	// -------------------------------------------------------------------------
	// Static API
	// -------------------------------------------------------------------------

	/**
	 * Disables or enables the "auto remove" mode of any animation manager for a
	 * particular animation. This mode is activated by default. The
	 * interest of desactivating it is to prevent some tweens or timelines from
	 * being automatically removed from a manager once they are finished.
	 * Therefore, if you update a manager backwards, the tweens or timelines
	 * will be played again, even if they were finished.
	 */
	public static void setAutoRemove(Animation object, boolean value) {
		object.autoRemoveEnabled = value;
	}

	/**
	 * Disables or enables the "auto start" mode of any animation manager for a
	 * particular animation. This mode is activated by default. If it
	 * is not enabled, add an animation to any manager won't start it
	 * automatically, and you'll need to call .start() manually on your object.
	 */
	public static void setAutoStart(Animation object, boolean value) {
		object.autoStartEnabled = value;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	private final ArrayList<Animation> objects = new ArrayList<>(20);
	private boolean isPaused = false;

	/**
	 * Adds an animation to the manager and starts or restarts it.
	 *
	 * @return The manager, for instruction chaining.
	 */
	public AnimationManager add(Animation object) {
		if (!objects.contains(object)) objects.add(object);
		if (object.autoStartEnabled) object.start();
		return this;
	}

	/**
	 * Returns true if the manager contains any valid interpolation associated
	 * to the given target object.
	 */
	public boolean containsTarget(Object target) {
		for (int i=0, n=objects.size(); i<n; i++) {
			Animation obj = objects.get(i);
			if (obj.containsTarget(target)) return true;
		}
		return false;
	}

	/**
	 * Returns true if the manager contains any valid interpolation associated
	 * to the given target object and to the given tween type.
	 */
	public boolean containsTarget(Object target, int tweenType) {
		for (int i=0, n=objects.size(); i<n; i++) {
			Animation obj = objects.get(i);
			if (obj.containsTarget(target, tweenType)) return true;
		}
		return false;
	}

	/**
	 * Cancels every managed tweens and timelines.
	 */
	public void cancelAll() {
		for (int i=0, n=objects.size(); i<n; i++) {
			Animation obj = objects.get(i);
			obj.stop();
		}
	}

	/**
	 * Cancels every tweens associated to the given target. Will also cancel every
	 * timelines containing a tween associated to the given target.
	 */
	public void cancelTarget(Object target) {
		for (int i=0, n=objects.size(); i<n; i++) {
			Animation obj = objects.get(i);
			obj.cancelTarget(target);
		}
	}

	/**
	 * Cancels every tweens associated to the given target and tween type. Will
	 * also cancel every timelines containing a tween associated to the given
	 * target and tween type.
	 */
	public void cancelTarget(Object target, int tweenType) {
		for (int i=0, n=objects.size(); i<n; i++) {
			Animation obj = objects.get(i);
			obj.cancelTarget(target, tweenType);
		}
	}

	/**
	 * Increases the minimum capacity of the manager. Defaults to 20.
	 */
	public void ensureCapacity(int minCapacity) {
		objects.ensureCapacity(minCapacity);
	}

	/**
	 * Pauses the manager. Further update calls won't have any effect.
	 */
	public void pause() {
		isPaused = true;
	}

	/**
	 * Resumes the manager, if paused.
	 */
	public void resume() {
		isPaused = false;
	}

	/**
	 * Updates every tweens with a delta time ang handles the tween life-cycles
	 * automatically. If a tween is finished, it will be removed from the
	 * manager. The delta time represents the elapsed time between now and the
	 * last update call. Each animation manages its local time, and adds
	 * this delta to its local time to update itself.
	 * <p/>
	 *
	 * Slow motion, fast motion and backward play can be easily achieved by
	 * tweaking this delta time. Multiply it by -1 to play the animation
	 * backward, or by 0.5 to play it twice slower than its normal speed.
	 */
	public void update(float delta) {
		for (int i=objects.size()-1; i>=0; i--) {
			Animation obj = objects.get(i);
			if (obj.isFinished() && obj.autoRemoveEnabled) {
				objects.remove(i);
				obj.reset();
			}
		}

		if (!isPaused) {
			if (delta >= 0) {
				for (int i=0, n=objects.size(); i<n; i++) objects.get(i).update(delta);
			} else {
				for (int i=objects.size()-1; i>=0; i--) objects.get(i).update(delta);
			}
		}
	}

	/**
	 * Gets the number of managed animations.
	 * Note that a timeline only counts for 1 object, since it
	 * manages its children itself.
	 * <p/>
	 * To get the count of running tweens, see {@link #getRunningTweensCount()}.
	 */
	public int size() {
		return objects.size();
	}

	/**
	 * Gets the number of running tweens. This number includes the tweens
	 * located inside timelines (and nested timelines).
	 * <p/>
	 * <b>Provided for debug purpose only.</b>
	 */
	public int getRunningTweensCount() {
		return getTweensCount(objects);
	}

	/**
	 * Gets the number of running timelines. This number includes the timelines
	 * nested inside other timelines.
	 * <p/>
	 * <b>Provided for debug purpose only.</b>
	 */
	public int getRunningTimelinesCount() {
		return getTimelinesCount(objects);
	}

	/**
	 * Gets an immutable list of every managed object.
	 * <p/>
	 * <b>Provided for debug purpose only.</b>
	 */
	public List<Animation> getObjects() {
		return Collections.unmodifiableList(objects);
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private static int getTweensCount(List<Animation> objs) {
		int count = 0;
		for (int i=0, n=objs.size(); i<n; i++) {
			Animation obj = objs.get(i);
			if (obj instanceof Tween) count += 1;
			else count += getTweensCount(((Timeline)obj).getChildren());
		}
		return count;
	}

	private static int getTimelinesCount(List<Animation> objs) {
		int count = 0;
		for (int i=0, n=objs.size(); i<n; i++) {
			Animation obj = objs.get(i);
			if (obj instanceof Timeline) {
				count += 1 + getTimelinesCount(((Timeline)obj).getChildren());
			}
		}
		return count;
	}

}
