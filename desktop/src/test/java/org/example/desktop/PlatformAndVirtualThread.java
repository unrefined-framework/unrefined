package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.util.Threading;

public class PlatformAndVirtualThread {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        Log log = Log.defaultInstance();

        Threading threading = Threading.getInstance();

        if (threading.isPlatformThreadSupported()) {
            log.info("Unrefined Threading", "Platform thread supported!");
            Thread platformThread = threading.createPlatformThread(() ->
                    log.info("Unrefined Threading", "Log from platform thread!"));
            log.info("Unrefined Threading", "Thread is platform thread: " + threading.isPlatformThread(platformThread));
            platformThread.start();
        }
        if (threading.isVirtualThreadSupported()) {
            log.info("Unrefined Threading", "Virtual thread supported!");
            Thread virtualThread = threading.createVirtualThread(() ->
                    log.info("Unrefined Threading", "Log from virtual thread!"));
            log.info("Unrefined Threading", "Thread is virtual thread: " + threading.isVirtualThread(virtualThread));
            virtualThread.start();
        }

    }

}
