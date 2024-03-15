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
 * periodically call {@link #dispatch()} in a thread of
 * your choice to handle the actuation of slots.
 */
public abstract class Dispatcher {

	/**
	 * The default {@link Dispatcher} of {@link Connection.Type#QUEUED} connected slots.
	 */
	private static volatile Dispatcher DEFAULT_INSTANCE;
	private static final Object DEFAULT_INSTANCE_LOCK = new Object();
	public static Dispatcher defaultInstance() {
		if (DEFAULT_INSTANCE == null) synchronized (DEFAULT_INSTANCE_LOCK) {
			if (DEFAULT_INSTANCE == null) DEFAULT_INSTANCE = Environment.global.get("unrefined.runtime.dispatcher", Dispatcher.class);
		}
		return DEFAULT_INSTANCE;
	}

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
	 * thrown by the slot actuation itself.
	 */
	private final Signal<Slot<Throwable>> onException = Signal.ofSlot();

	public abstract boolean isDispatchThread(Thread thread);

	public boolean isDispatchThread() {
		return isDispatchThread(Thread.currentThread());
	}

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
			connection.dispatcher.invokeLater(connection.dispatcher::dispatch);
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
				connection.actuate(args);
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
	 * Returns the signal which gets emitted if actuating a slot failed.
	 *
	 * @return The signal which gets emitted if actuating a slot failed.
	 */
	public Signal<Slot<Throwable>> onException() {
		return onException;
	}

	public abstract void invokeLater(Runnable runnable);

}
