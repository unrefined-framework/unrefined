package unrefined.desktop;

import unrefined.runtime.BaseDispatcher;
import unrefined.util.NotInstantiableError;

public class PreferencesSupport {

    private PreferencesSupport() {
        throw new NotInstantiableError(PreferencesSupport.class);
    }

    private static final BaseDispatcher dispatcher = new BaseDispatcher("Unrefined Desktop Preferences");

    public static void enqueueWrite(Runnable writeProc) {
        dispatcher.invokeLater(writeProc);
    }

}
