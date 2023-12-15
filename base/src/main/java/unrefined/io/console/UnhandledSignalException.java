package unrefined.io.console;

import java.io.IOException;

public class UnhandledSignalException extends IOException {

    private static final long serialVersionUID = 6590459242474420065L;

    public UnhandledSignalException(String signal) {
        super(signal);
    }
    
}
