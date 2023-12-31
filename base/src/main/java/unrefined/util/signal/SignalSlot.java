package unrefined.util.signal;

import unrefined.util.function.Slot;

@FunctionalInterface
public interface SignalSlot<T> extends Slot<Signal<T>> {
}
