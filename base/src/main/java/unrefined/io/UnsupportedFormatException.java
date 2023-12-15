package unrefined.io;

import java.io.IOException;

public class UnsupportedFormatException extends IOException {

    private static final long serialVersionUID = -1478145753446626169L;

    public UnsupportedFormatException() {
        super();
    }

    public UnsupportedFormatException(String message) {
        super(message);
    }

}
