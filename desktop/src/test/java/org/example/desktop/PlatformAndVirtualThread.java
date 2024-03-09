package org.example.desktop;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.Threading;

public class PlatformAndVirtualThread {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        Logger logger = Logger.defaultInstance();

        Threading threading = Threading.getInstance();

        if (threading.isPlatformThreadSupported()) {
            logger.info("Unrefined Threading", "Platform thread supported!");
            Thread platformThread = threading.createPlatformThread(() ->
                    logger.info("Unrefined Threading", "Log from platform thread!"));
            logger.info("Unrefined Threading", "Thread is platform thread: " + threading.isPlatformThread(platformThread));
            platformThread.start();
        }
        if (threading.isVirtualThreadSupported()) {
            logger.info("Unrefined Threading", "Virtual thread supported!");
            Thread virtualThread = threading.createVirtualThread(() ->
                    logger.info("Unrefined Threading", "Log from virtual thread!"));
            logger.info("Unrefined Threading", "Thread is virtual thread: " + threading.isVirtualThread(virtualThread));
            virtualThread.start();
        }

    }

}
