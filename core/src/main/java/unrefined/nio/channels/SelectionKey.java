package unrefined.nio.channels;

import unrefined.util.NotInstantiableError;

import java.util.concurrent.atomic.AtomicReference;

import static unrefined.nio.channels.SelectionKey.Operation.*;

public abstract class SelectionKey {

    public static final class Operation {
        private Operation() {
            throw new NotInstantiableError(Operation.class);
        }
        public static final int READ = 1 << 0;
        public static final int WRITE = 1 << 2;
        public static final int CONNECT = 1 << 3;
        public static final int ACCEPT = 1 << 4;
        public static int removeUnusedBits(int operations) {
            return operations << 27 >>> 27 & ~(1 << 1);
        }
        public static String toString(int operations) {
            operations = removeUnusedBits(operations);
            if (operations == READ) return "[READ]";
            else {
                StringBuilder builder = new StringBuilder("[READ");
                if ((operations & WRITE) != 0) builder.append(", WRITE");
                if ((operations & CONNECT) != 0) builder.append(", CONNECT");
                if ((operations & ACCEPT) != 0) builder.append(", ACCEPT");
                builder.append("]");
                return builder.toString();
            }
        }
    }

    public abstract SelectableChannel getChannel();
    public abstract Selector getSelector();
    public abstract boolean isValid();
    public abstract void cancel();

    public abstract int getInterestOperations();
    public abstract void setInterestOperations(int operations);
    public int interestOperationsOr(int operations) {
        synchronized (this) {
            int oldValue = getInterestOperations();
            setInterestOperations(oldValue | operations);
            return oldValue;
        }
    }
    public int interestOperationsAnd(int operations) {
        synchronized (this) {
            int oldValue = getInterestOperations();
            setInterestOperations(oldValue & operations);
            return oldValue;
        }
    }
    public int interestOperationsXor(int operations) {
        synchronized (this) {
            int oldValue = getInterestOperations();
            setInterestOperations(oldValue ^ operations);
            return oldValue;
        }
    }

    public abstract int getReadyOperations();

    public final boolean isReadable() {
        return (getReadyOperations() & READ) != 0;
    }
    public final boolean isWritable() {
        return (getReadyOperations() & WRITE) != 0;
    }
    public final boolean isConnectable() {
        return (getReadyOperations() & CONNECT) != 0;
    }
    public final boolean isAcceptable() {
        return (getReadyOperations() & ACCEPT) != 0;
    }

    private final AtomicReference<Object> attachment = new AtomicReference<>();

    public final Object attach(Object object) {
        return attachment.getAndSet(object);
    }
    public final Object attachment() {
        return attachment.get();
    }

}
