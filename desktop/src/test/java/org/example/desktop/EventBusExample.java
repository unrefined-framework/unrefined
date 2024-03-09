package org.example.desktop;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.event.Event;
import unrefined.util.event.EventBus;
import unrefined.util.event.Subscribe;

public class EventBusExample {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        EventBus bus = EventBus.defaultInstance();

        bus.register(new EventBusExample());
        bus.post(new StringEvent(EventBusExample.class, "This is an message"));
    }

    public static class StringEvent extends Event<Class<?>> {
        private final String message;
        public StringEvent(Class<?> source, String message) {
            super(source);
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }

    @Subscribe
    public void onStringEvent(StringEvent event) {
        Logger.defaultInstance().info("Unrefined EventBus", event.getMessage());
    }

}
