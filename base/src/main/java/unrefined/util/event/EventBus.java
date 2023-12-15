package unrefined.util.event;

import unrefined.context.Environment;
import unrefined.util.function.Slot;
import unrefined.util.signal.Connection;
import unrefined.util.signal.Dispatcher;
import unrefined.util.signal.Signal;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static unrefined.util.signal.Connection.Type.AUTO;

public abstract class EventBus {

    private static volatile EventBus DEFAULT_INSTANCE;
    private static final Object DEFAULT_INSTANCE_LOCK = new Object();
    public static EventBus defaultEventBus() {
        if (DEFAULT_INSTANCE == null) synchronized (DEFAULT_INSTANCE_LOCK) {
            if (DEFAULT_INSTANCE == null) DEFAULT_INSTANCE = Environment.global().get("unrefined.runtime.eventBus", EventBus.class);
        }
        return DEFAULT_INSTANCE;
    }

    private final Map<Object, Connection> connectionMap = new ConcurrentHashMap<>();
    private final Signal<EventSlot<Event<?>>> signal = Signal.ofSlot();
    private final Signal<Slot<Throwable>> onException = Signal.ofSlot();

    protected abstract void dispatch(Object subscriber, Method method, Event<?> event) throws Throwable;

    public void register(Object subscriber) {
        register(subscriber, null, AUTO);
    }

    public void register(Object subscriber, int type) {
        register(subscriber, null, type);
    }

    public void register(Object subscriber, Dispatcher dispatcher) {
        register(subscriber, dispatcher, AUTO);
    }

    public void register(Object subscriber, Dispatcher dispatcher, int type) {
        if (connectionMap.containsKey(subscriber)) return;
        connectionMap.put(subscriber, signal.connect(event -> {
            for (Method method : subscriber.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Subscribe.class)) {
                    if (method.getParameterTypes()[0].isInstance(event)) {
                        try {
                            dispatch(subscriber, method, event);
                        }
                        catch (Throwable e) {
                            if (onException.isEmpty()) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                            else onException.emit(e);
                        }
                    }
                }
            }
        }, dispatcher, type));
    }

    public void unregister() {
        signal.disconnect();
    }

    public void unregister(Dispatcher dispatcher) {
        signal.disconnect(dispatcher);
    }

    public void unregister(Object subscriber) {
        signal.disconnect(connectionMap.remove(subscriber));
    }

    public void unregister(Object subscriber, Dispatcher dispatcher) {
        if (signal.disconnect(connectionMap.get(subscriber), dispatcher)) connectionMap.remove(subscriber);
    }

    public void post(Event<?> event) {
        signal.emit(event);
    }

    public Signal<Slot<Throwable>> onException() {
        return onException;
    }

}
