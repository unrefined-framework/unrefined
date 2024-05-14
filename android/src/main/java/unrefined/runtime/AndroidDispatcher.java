package unrefined.runtime;

import android.os.Looper;
import unrefined.android.ActivityHolder;
import unrefined.util.signal.Dispatcher;

public class AndroidDispatcher extends Dispatcher {

    @Override
    public boolean isDispatchThread(Thread thread) {
        return Looper.getMainLooper().getThread() == thread;
    }

    @Override
    public void invokeLater(Runnable runnable) {
        ActivityHolder.get().runOnUiThread(runnable);
    }

}
