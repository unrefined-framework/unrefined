package unrefined.util.signal;

import unrefined.context.Environment;
import unrefined.util.function.Slot;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import static unrefined.util.signal.Connection.Type.DIRECT;

/**
 * This class allows to actuate dispatched slots.
 *
 * <p>A dispatched slot is actuated in a separate 'dispatcher' thread, e.g., the
 * GUI thread, the database thread, and so on. One way of doing this is to
 * periodically call {@link #waitFor()} and {@link #dispatch()} in a thread of
 * your choice to handle the actuation of slots.
 *
 * <p>You may also use {@link #switchContext()} in a loop until the current thread gets
 * interrupted. {@link #switchContext()}, in turn, may be subclassed to
 * delegate {@link #dispatch()} to another dispatcher thread, for example, the
 * JavaFX thread. By default, {@link #switchContext()} calls
 * {@link #dispatch()} in the same context, which is a useful
 * default in most cases.
 */
public abstract class Dispatcher {

	/**
	 * The default {@link Dispatcher} of {@link Connection.Type#QUEUED} connected slots.
	 */
	private static volatile Dispatcher DEFAULT_INSTANCE;
	private static final Object DEFAULT_INSTANCE_LOCK = new Object();
	public static Dispatcher defaultDispatcher() {
		if (DEFAULT_INSTANCE == null) synchronized (DEFAULT_INSTANCE_LOCK) {
			if (DEFAULT_INSTANCE == null) DEFAULT_INSTANCE = Environment.global().get("unrefined.runtime.dispatcher", Dispatcher.class);
		}
		return DEFAULT_INSTANCE;
	}

	/**
	 * Is used to block the dispatcher thread until an associated signal has
	 * been emitted.
	 */
	private final Semaphore semaphore = new Semaphore(0);

	/**
	 * Is used to block the signalling thread
	 * if connection type is {@link Connection.Type#BLOCKING_QUEUED}
	 * until an associated signal has been emitted.
	 */
	private final Semaphore blocking = new Semaphore(0);

	/**
	 * Is used to prevent the deadlock of {@link #blocking}
	 */
	private final AtomicBoolean blockingReleased = new AtomicBoolean(true);

	/**
	 * The queue of slots to actuate. Needs to be thread safe without locking.
	 */
	private final Queue<Connection> connectionQueue = new ConcurrentLinkedQueue<>();
	private final Queue<Object[]> argsQueue = new ConcurrentLinkedQueue<>();

	/**
	 * Is emitted by {@link #dispatch()} if a {@link Throwable} has been
	 * thrown by either {@link #preActuation()}, {@link #postActuation()},
	 * or a slot actuation itself.
	 */
	private final Signal<Slot<Throwable>> onException = Signal.ofSlot();

	protected abstract boolean isDispatchThread();

	/**
	 * Adds the given {@link Connection} to the event queue. The slot itself
	 * gets actuated by the next call of {@link #dispatch()}.
	 *
	 * @param connection The {@link Connection} to add.
	 * @throws NullPointerException If {@code actuation} is {@code null}.
	 */
	final Object actuate(Connection connection, Object... args) {
		int connectionType = connection.type;
		if (connectionType == Connection.Type.AUTO) {
			if (isDispatchThread()) connectionType = DIRECT;
			else connectionType = Connection.Type.QUEUED;
		}
		if (connectionType == Connection.Type.QUEUED && !connection.signal.isDiscardReturnValue()) connectionType = Connection.Type.BLOCKING_QUEUED;
		if (connectionType == DIRECT) {
			try {
				connection.actuate(args);
			}
			catch (Throwable e) {
				handleException(e);
			}
		}
		else {
			connectionQueue.add(connection);
			argsQueue.add(args);
			if (connectionType == Connection.Type.BLOCKING_QUEUED) blockingReleased.set(false);
			if (connection.dispatcher.hasQueueThread()) semaphore.release();
			else connection.dispatcher.switchContext();
			if (connectionType == Connection.Type.BLOCKING_QUEUED) {
				if (!blockingReleased.get()) {
					try {
						blocking.acquire();
					} catch (InterruptedException e) {
						handleException(e);
					}
				}
			}
		}
		return connection.result.get();
	}

	private void handleException(Throwable e) {
		if (onException.isEmpty()) {
			e.printStackTrace();
			System.exit(1);
		}
		else onException.emit(e);
	}

	/**
	 * Blocks the current thread until a slot needs to be actuated. Throws an
	 * {@link InterruptedException} if the threads gets interrupted while
	 * waiting.
	 *
	 * @throws InterruptedException If the current thread was interrupted.
	 */
	protected final void waitFor() throws InterruptedException {
		semaphore.acquire();
	}

	/**
	 * Polls the next {@link Connection} from the event queue and actuates
	 * it. Does nothing if the event queue is empty. This function will never
	 * throw a {@link RuntimeException}, but emit {@link #onException()}.
	 */
	protected final void dispatch() {
		Connection connection = null;
		try {
			connection = connectionQueue.poll();
			Object[] args = argsQueue.poll();
			if (connection != null && args != null) {
				preActuation();
				try {
					connection.actuate(args);
				}
				finally {
					postActuation();
				}
			}
		}
		catch (Throwable e) {
			handleException(e);
		}
		finally {
			if (connection != null) {
				int connectionType = connection.type;
				if (connectionType == Connection.Type.AUTO) connectionType = Connection.Type.QUEUED;
				if (connectionType == Connection.Type.QUEUED && !connection.signal.isDiscardReturnValue()) connectionType = Connection.Type.BLOCKING_QUEUED;
				if (connectionType == Connection.Type.BLOCKING_QUEUED) {
					blocking.release();
					blockingReleased.set(true);
				}
			}
		}
	}

	/**
	 * Allows subclasses to switch the thread context before actuating a slot
	 * by calling {@link #dispatch()} within the desired context. The default
	 * implementation calls {@link #dispatch()} within the caller context.
	 */
	protected void switchContext() {
		dispatch();
	}

	/**
	 * This is a callback which gets executed by {@link #dispatch()} right
	 * before a slot is actuated. Override it to add some custom code. If a
	 * {@link RuntimeException} is thrown by this callback, {@link #dispatch()}
	 * will catch it and emit the signal returned by {@link #onException()}. If
	 * there is no slot to actuate {@link #dispatch()} omits this callback.
	 */
	protected abstract void preActuation();

	/**
	 * This is a callback which gets executed by {@link #dispatch()} right
	 * after a slot has been actuated. Override it to add some custom code. If
	 * a {@link RuntimeException} is thrown by this callback,
	 * {@link #dispatch()} will catch it and emit the signal returned by
	 * {@link #onException()}. If there is no slot to actuate {@link #dispatch()}
	 * omits this callback.
	 */
	protected abstract void postActuation();

	/**
	 * Returns the signal which gets emitted if actuating a slot failed.
	 *
	 * @return The signal which gets emitted if actuating a slot failed.
	 */
	public Signal<Slot<Throwable>> onException() {
		return onException;
	}

	protected abstract boolean hasQueueThread();

}
