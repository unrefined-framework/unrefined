package unrefined.android;

import android.app.Activity;
import unrefined.util.NotInstantiableError;

import java.util.concurrent.atomic.AtomicReference;

public class ActivityHolder {

    private ActivityHolder() {
        throw new NotInstantiableError(ActivityHolder.class);
    }

    private static final AtomicReference<Activity> mHolder = new AtomicReference<>(null);

    public static Activity get() {
        return mHolder.get();
    }

    public static void attach(Activity activity) {
        mHolder.set(activity);
    }

    public static void detach() {
        mHolder.set(null);
    }

}
