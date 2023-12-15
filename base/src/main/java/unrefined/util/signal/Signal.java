package unrefined.util.signal;

import unrefined.internal.BitwiseUtils;
import unrefined.util.concurrent.ConcurrentHashSet;
import unrefined.util.function.VarFunctor;
import unrefined.util.function.VarSlot;
import unrefined.util.reflect.Reflection;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.reflect.Modifier.isStatic;
import static unrefined.util.signal.Connection.Type.*;

/**
 * The base class of all signals. Note: Connecting and emitting slots
 * concurrently is thread-safe without blocking.
 *
 * @param <T> the slot type which this signal supports
 */
public abstract class Signal<T> {

	public static <T> Connection connect(Signal<T> signal, T slot) {
		return signal.connect(slot);
	}

	public static <T> Connection connect(Signal<T> signal, T slot, int type) {
		return signal.connect(slot, type);
	}

	public static <T> Connection connect(Signal<T> signal, T slot, Dispatcher dispatcher) {
		return signal.connect(slot, dispatcher);
	}
	
	public static <T> Connection connect(Signal<T> signal, T slot, Dispatcher dispatcher, int type) {
		return signal.connect(slot, dispatcher, type);
	}

	public static <T> boolean disconnect(Signal<T> signal, Connection connection) {
		return signal.disconnect(connection);
	}

	public static <T> boolean disconnect(Signal<T> signal) {
		return signal.disconnect();
	}

	public static <T> boolean disconnect(Signal<T> signal, Dispatcher dispatcher) {
		return signal.disconnect(dispatcher);
	}

	public static <T> boolean disconnect(Signal<T> signal, T slot) {
		return signal.disconnect(slot);
	}

	public static <T> boolean disconnect(Signal<T> signal, T slot, Dispatcher dispatcher) {
		return signal.disconnect(slot, dispatcher);
	}

	private final boolean discardReturnValue;

	protected Signal(boolean discardReturnValue) {
		this.discardReturnValue = discardReturnValue;
	}

	public static <T> Signal<T> ofMethod(Class<T> clazz, Method method) {
		if (method.getDeclaringClass() != clazz) throw new IllegalArgumentException("method.getDeclaringClass() != clazz");
		else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
		return new Signal<T>(method.getReturnType() == void.class) {
			@Override
			protected Object actuate(T slot, Object... args) throws Exception {
				return Reflection.getReflection().invokeMethod(slot, method, args);
			}
		};
	}

	public static <T extends VarFunctor<?>> Signal<T> ofFunctor() {
		return new Signal<T>(false) {
			@Override
			protected Object actuate(T slot, Object... args) throws Throwable {
				return slot.actuate(args);
			}
		};
	}

	public static <T extends VarSlot> Signal<T> ofSlot() {
		return new Signal<T>(true) {
			@Override
			protected Object actuate(T slot, Object... args) throws Throwable {
				slot.actuate(args);
				return null;
			}
		};
	}

	public static <T extends Runnable> Signal<T> ofRunnable() {
		return new Signal<T>(true) {
			@Override
			protected Object actuate(T slot, Object... args) throws Throwable {
				slot.run();
				return null;
			}
		};
	}

	public static <T extends Callable<?>> Signal<T> ofCallable() {
		return new Signal<T>(true) {
			@Override
			protected Object actuate(T slot, Object... args) throws Throwable {
				return slot.call();
			}
		};
	}

	public boolean isDiscardReturnValue() {
		return discardReturnValue;
	}

	/**
	 * Indicates whether a signal is enabled/disabled.
	 * @see #enable()
	 * @see #disable()
	 */
	private final AtomicBoolean enabled = new AtomicBoolean(true);

	/**
	 * The queue of dispatched connections.
	 * @see Dispatcher
	 */
	final Queue<Connection> connections = new ConcurrentLinkedQueue<>();

	public boolean isEmpty() {
		return connections.isEmpty();
	}

	public int connected() {
		return connections.size();
	}

	/**
	 * The set of unique slots.
	 * @see Connection.Type#UNIQUE
	 */
	private final Set<T> uniques = new ConcurrentHashSet<>();

	/**
	 * Returns whether the signal is enabled.
	 * @see #enable()
	 * @see #disable()
	 * @return true if enabled, false opposite
	 */
	public boolean isEnabled() {
		return enabled.get();
	}

	/**
	 * Sets whether the signal is enabled.
	 * @see #enable()
	 * @see #disable()
	 * @param enabled true if enabled, false opposite
	 */
	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	/**
	 * Enables this signal.
	 * @see #disable()
	 */
	public void enable() {
		enabled.set(true);
	}

	/**
	 * Disables this signal. A disabled signal will not actuate its connected
	 * slots.
	 * @see #enable()
	 */
	public void disable() {
		enabled.set(false);
	}

	/**
	 * Removes all connected slots. Clearing a signal is not an atomic
	 * operation and may result in a non-empty slot queue if one of the
	 * 'connect' methods is used concurrently.
	 */
	public void clear() {
		connections.clear();
		uniques.clear();
	}

	/**
	 * Connects the given slot using {@link Connection.Type#AUTO}. This method is
	 * equivalent to {@code connect(slot, AUTO)}.
	 *
	 * @see #connect(Object, int)
	 * @param slot The slot to connect.
	 * @throws NullPointerException If {@code slot} is {@code null}.
	 */
	public Connection connect(T slot) {
		return connect(slot, AUTO);
	}

	/**
	 * Connects the given slot according to {@link Connection.Type}. This method is
	 * equivalent to {@code connect(slot, type, null)}.
	 *
	 * @see #connect(Object, Dispatcher, int)
	 * @param slot The slot to connect.
	 * @param type The connection type.
	 * @throws NullPointerException If {@code slot} is {@code null}.
	 */
	public Connection connect(T slot, int type) {
		return connect(slot, null, type);
	}

	/**
	 * Connects the given slot according to {@link Connection.Type#AUTO}
	 * and actuates it within the thread context of the
	 * given {@link Dispatcher} if the signal is emitted.
	 * If {@link Dispatcher} is null, the default {@link Dispatcher} will be used.
	 * This method is equivalent to {@code connect(slot, dispatcher, AUTO)}.
	 *
	 * @see #connect(Object, Dispatcher, int)
	 * @param slot The slot to connect.
	 * @param dispatcher The {@link Dispatcher} to use.
	 * @throws NullPointerException If {@code slot} is {@code null}.
	 */
	public Connection connect(T slot, Dispatcher dispatcher) {
		return connect(slot, dispatcher, AUTO);
	}

	/**
	 * Connects the given slot according to {@link Connection.Type}
	 * and actuates it within the thread context of the
	 * given {@link Dispatcher} if the signal is emitted.
	 * If {@link Dispatcher} is null, the default {@link Dispatcher} will be used.
	 *
	 * @param slot The slot to connect.
	 * @param type The connection type.
	 * @param dispatcher The {@link Dispatcher} to use.
	 * @throws NullPointerException If {@code slot} is {@code null}.
	 */
	public Connection connect(T slot, Dispatcher dispatcher, int type) throws IllegalArgumentException, NullPointerException {
		Objects.requireNonNull(slot);
		if (dispatcher == null) dispatcher = Dispatcher.defaultDispatcher();
		final boolean unique = (type & UNIQUE) == UNIQUE;
		final boolean singleShot = (type & SINGLE_SHOT) == SINGLE_SHOT;
		type = BitwiseUtils.removeUnusedBits(type, 3);
		boolean broken = false;
		if (uniques.contains(slot)) broken = true;
		else if (unique) uniques.add(slot);
		Connection connection;
		switch (type) {
			case AUTO:
			case DIRECT:
			case QUEUED:
			case BLOCKING_QUEUED:
				connections.add((connection = new Connection(this, slot, type, dispatcher, singleShot, broken)));
				break;
			default:
				throw new IllegalArgumentException("Illegal connection type: " + type);
		}
		return connection;
	}

	public boolean disconnect(Connection connection) {
		if (connection == null) return false;
		if (!connections.contains(connection)) return false;
		connection.broke();
		connections.remove(connection);
		return true;
	}

	public boolean disconnect(Connection connection, Dispatcher dispatcher) {
		if (connection == null) return false;
		if (!connections.contains(connection)) return false;
		if (dispatcher == null) dispatcher = Dispatcher.defaultDispatcher();
		if (connection.dispatcher == dispatcher) {
			connection.broke();
			connections.remove(connection);
			return true;
		}
		return false;
	}

	public boolean disconnect(T slot) {
		if (slot == null) return disconnect();
		for (Connection connection : connections) {
			if (connection.slot == slot) {
				connection.broke();
				connections.remove(connection);
				return true;
			}
		}
		return false;
	}

	public boolean disconnect(Dispatcher dispatcher) {
		if (dispatcher == null) dispatcher = Dispatcher.defaultDispatcher();
		for (Connection connection : connections) {
			if (connection.dispatcher == dispatcher) {
				connection.broke();
				connections.remove(connection);
				return true;
			}
		}
		return false;
	}

	public boolean disconnect(T slot, Dispatcher dispatcher) {
		if (slot == null) return disconnect(dispatcher);
		for (Connection connection : connections) {
			if (connection.dispatcher == dispatcher && connection.slot == slot) {
				connection.broke();
				connections.remove(connection);
				return true;
			}
		}
		return false;
	}

	public boolean disconnect() {
		for (Connection connection : connections) {
			connection.broke();
			connections.remove(connection);
			return true;
		}
		return false;
	}

	/**
	 * Emits this signal with the given arguments.
	 *
	 * @param args The arguments to use pass to the connected slots.
	 */
	public Object emit(Object... args) {
		if (enabled.get()) {
			Object result = null;
			for (Connection connection : connections) {
				if (connection.isBroken()) connections.remove(connection);
				else result = connection.dispatcher.actuate(connection, args);
			}
			return result;
		}
		else return null;
	}

	/**
	 * A callback method used for slot actuation.
	 *
	 * <p>The implementation of this method do not need to create any thread, but
	 * cast down the given slot and actuate it with the given arguments.
	 *
	 * <p>This method should not have any side effects to this class.
	 *
	 * @param slot The slot to actuate.
	 * @param args The arguments of the actuated slot.
	 */
	protected abstract Object actuate(T slot, Object... args) throws Throwable;

	@SuppressWarnings("unchecked")
	Object actuateUnchecked(Object slot, Object... args) throws Throwable {
		return actuate((T) slot, args);
	}

}
