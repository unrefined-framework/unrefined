package org.example.desktop.worker;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.util.Threading;
import unrefined.util.concurrent.worker.Worker;
import unrefined.util.signal.Connection;
import unrefined.util.signal.Dispatcher;

public class BackgroundWorker {

    public static void main(Worker worker) {
        Log log = Log.defaultInstance();

        worker.onMessage().connect(message ->
                        log.info(worker.getName(), message.toString()),
                Connection.Type.DIRECT);
    }

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        Threading threading = Threading.getInstance();
        Worker worker = threading.createWorker("Test", BackgroundWorker.class);

        Dispatcher.defaultInstance().invokeLater(() -> worker.post("Hello Unrefined Worker"));
    }

}
