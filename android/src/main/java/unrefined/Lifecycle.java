package unrefined;

import android.app.Activity;
import unrefined.android.ActivityHolder;
import unrefined.util.NotInstantiableError;

public class Lifecycle {

    private Lifecycle() {
        throw new NotInstantiableError(Lifecycle.class);
    }

    public static void onCreate(Activity activity) {
        ActivityHolder.attach(activity);
    }

    public static void onDestroy(Activity activity) {
        ActivityHolder.detach();
    }

}
