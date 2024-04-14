package unrefined.net;

import java.io.IOException;

public class SocketException extends IOException {

    private static final long serialVersionUID = -1036730761116400661L;

    public SocketException() {
        super();
    }

    public SocketException(String message) {
        super(message);
    }

}
