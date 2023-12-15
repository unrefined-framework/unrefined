package unrefined.io.console;

import java.io.IOException;

public class SignalNotFoundException extends IOException {

    private static final long serialVersionUID = 1940230514032183183L;

    public SignalNotFoundException(String signal) {
        super(signal);
    }
    
}
