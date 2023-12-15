package unrefined.util.event;

import java.util.Objects;

public abstract class Event<T> {

    private final T source;

    public Event(T source) {
        this.source = Objects.requireNonNull(source);
    }

    public T getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "source=" + source
                + '}';
    }

}
