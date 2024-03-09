package org.example.desktop.worker;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.Threading;
import unrefined.util.concurrent.worker.Worker;
import unrefined.util.signal.Connection;
import unrefined.util.signal.Dispatcher;

public class BackgroundWorker {

    public static void main(Worker worker) {
        Logger logger = Logger.defaultInstance();

        worker.onMessage().connect(message ->
                        logger.info(worker.getName(), message.toString()),
                Connection.Type.DIRECT);
    }

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        Threading threading = Threading.getInstance();
        Worker worker = threading.createWorker("Test", BackgroundWorker.class);

        Dispatcher.defaultInstance().invokeLater(() -> worker.post("Hello Unrefined Worker"));
    }

}
