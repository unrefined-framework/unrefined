package unrefined.runtime;

import unrefined.util.signal.Dispatcher;

import java.awt.EventQueue;

public class DesktopDispatcher extends Dispatcher {

    @Override
    protected boolean isDispatchThread() {
        return EventQueue.isDispatchThread();
    }

    @Override
    protected void preActuation() {}

    @Override
    protected void switchContext() {
        EventQueue.invokeLater(this::dispatch);
    }

    @Override
    protected void postActuation() {}

    @Override
    protected boolean hasQueueThread() {
        return false;
    }

}
