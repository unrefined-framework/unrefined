package unrefined.runtime;

import unrefined.desktop.CleanerSupport;
import unrefined.util.ref.Cleaner;

public class DesktopCleaner extends Cleaner {

    @Override
    public Runnable register(Object object, Runnable cleanProc) {
        return CleanerSupport.register(object, cleanProc);
    }

}
