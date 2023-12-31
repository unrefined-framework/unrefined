package unrefined.util.event;

import java.util.Objects;

public abstract class Event<T> {

    private final transient T source;

    public Event(T source) {
        this.source = Objects.requireNonNull(source);
    }

    public T getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event<?> event = (Event<?>) o;

        return Objects.equals(source, event.source);
    }

    @Override
    public int hashCode() {
        return source != null ? source.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "source=" + source +
                '}';
    }

}
