package unrefined.util.signal;

import unrefined.util.NotInstantiableError;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Opaque object which represents a connection of a {@link Signal} and a slot.
 */
public final class Connection {

    /**
     * The constants of supported connection types.
     * <p>
     * To actuate a slot by a {@link Dispatcher} use
     * {@link Signal#connect(Object, Dispatcher)}.
     *
     * @see Dispatcher
     */
    public static final class Type {

        private Type() {
            throw new NotInstantiableError(Type.class);
        }

        /**
         * (Default) If the receiver lives in the thread that emits the signal,
         * {@link Type#DIRECT} is used.
         * Otherwise, {@link Type#QUEUED} is used.
         * The connection type is determined when the signal is emitted.
         */
        public static final int AUTO = 0;

        /**
         * The slot is invoked immediately when the signal is emitted. The slot is executed in the signalling thread.
         */
        public static final int DIRECT = 1;

        /**
         * The slot is invoked when control returns to the event loop of the receiver's thread. The slot is executed in the receiver's thread.
         * This connection will be treated as {@link Type#BLOCKING_QUEUED} for any signal which marked not discard the return value.
         */
        public static final int QUEUED = 2;

        /**
         * Same as {@link Type#QUEUED}, except that the signalling thread blocks until the slot returns.
         * This connection must not be used if the receiver lives in the signalling thread, or else the application will deadlock.
         */
        public static final int BLOCKING_QUEUED = 3;

        /**
         * This is a flag that can be combined with any one of the above connection types, using a bitwise OR.
         * When {@code UNIQUE} is set, {@link Signal#connect(Object, int)} will fail if the connection already exists
         * (i.e. if the same signal is already connected to the same slot for the same pair of objects).
         * <p><b>
         * Note: {@code UNIQUE} do not work for lambdas and functors;
         * they only apply to connecting to slot objects.
         * </b></p>
         */
        public static final int UNIQUE = 0x80;

        /**
         * This is a flag that can be combined with any one of the above connection types, using a bitwise OR.
         * When {@code SINGLE_SHOT} is set, the slot is going to be called only once;
         * the connection will be automatically broken when the signal is emitted.
         */
        public static final int SINGLE_SHOT = 0x100;

        public static int unmask(int type) {
            return type << 29 >>> 29;
        }

    }

    /**
     * The signal to actuate.
     */
    final Signal<?> signal;

    /**
     * The slot to actuate.
     */
    final Object slot;

    /**
     * The original connection type.
     */
    private final int original;

    /**
     * The connection type.
     */
    final int type;

    /**
     * Whether the connection is {@link Type#SINGLE_SHOT}.
     */
    final boolean singleShot;

    /**
     * The dispatcher to dispatch the connection.
     */
    final Dispatcher dispatcher;

    /**
     * Whether the connection is broken.
     *
     * @see Type#SINGLE_SHOT
     */
    private final AtomicBoolean broken;

    Connection(Signal<?> signal, Object slot, int original, int type, Dispatcher dispatcher, boolean singleShot, boolean broken) {
        this.signal = signal;
        this.slot = slot;
        this.dispatcher = dispatcher;
        this.original = original;
        this.type = type;
        this.singleShot = singleShot;
        this.broken = new AtomicBoolean(broken);
    }

    void broke() {
        this.broken.set(true);
    }

    /**
     * The result.
     */
    final AtomicReference<Object> result = new AtomicReference<>();
    void actuate(Object... args) throws Throwable {
        result.set(signal.actuateUnchecked(slot, args));
        if (singleShot) {
            broke();
            signal.connections.remove(this);
        }
    }

    boolean isBroken() {
        return broken.get();
    }

    public Signal<?> getSignal() {
        return signal;
    }

    public Object getSlot() {
        return slot;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public int getType() {
        return original;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (type != that.type) return false;
        if (singleShot != that.singleShot) return false;
        if (!signal.equals(that.signal)) return false;
        if (!slot.equals(that.slot)) return false;
        return dispatcher.equals(that.dispatcher);
    }

    @Override
    public int hashCode() {
        int result = signal.hashCode();
        result = 31 * result + slot.hashCode();
        result = 31 * result + type;
        result = 31 * result + (singleShot ? 1 : 0);
        result = 31 * result + dispatcher.hashCode();
        return result;
    }

}
