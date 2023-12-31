package unrefined.runtime;

import unrefined.util.event.Event;
import unrefined.util.event.EventBus;
import unrefined.util.reflect.Reflection;

import java.lang.reflect.Method;

public class BaseEventBus extends EventBus {

    @Override
    protected void dispatch(Object subscriber, Method method, Event<?> event) throws Throwable {
        Reflection.getInstance().invokeMethod(subscriber, method, event);
    }

}
