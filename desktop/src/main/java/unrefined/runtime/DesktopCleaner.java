package unrefined.runtime;

import unrefined.util.ref.Cleaner;

public class DesktopCleaner extends Cleaner {

    private static final java.lang.ref.Cleaner CLEANER = java.lang.ref.Cleaner.create();

    @Override
    public Runnable register(Object object, Runnable cleanProc) {
        return CLEANER.register(object, cleanProc)::clean;
    }

}
