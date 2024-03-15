package unrefined.util.event;

import unrefined.util.function.Functor;

@FunctionalInterface
public interface EventFunctor<E extends Event<?>, R> extends EventListener<E>, Functor<E, R> {
}
