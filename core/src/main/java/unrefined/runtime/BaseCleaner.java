package unrefined.runtime;

import unrefined.util.ref.Cleaner;

public class BaseCleaner extends Cleaner {

    @Override
    public Runnable register(Object object, Runnable cleanProc) {
        return unrefined.core.Cleaner.getCleaner().register(object, cleanProc)::clean;
    }

}
