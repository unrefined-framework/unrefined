package unrefined.runtime;

import unrefined.desktop.AWTSupport;
import unrefined.util.signal.Dispatcher;

import java.awt.EventQueue;

public class DesktopDispatcher extends Dispatcher {

    @Override
    public boolean isDispatchThread(Thread thread) {
        return AWTSupport.isDispatchThread(thread);
    }

    @Override
    public boolean isDispatchThread() {
        return EventQueue.isDispatchThread();
    }

    @Override
    public void invokeLater(Runnable runnable) {
        EventQueue.invokeLater(runnable);
    }

}
