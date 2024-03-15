package unrefined.util.event;

import unrefined.util.function.Slot;

@FunctionalInterface
public interface EventSlot<E extends Event<?>> extends EventListener<E>, Slot<E> {
}
