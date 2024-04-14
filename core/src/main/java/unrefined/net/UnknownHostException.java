package unrefined.net;

import java.io.IOException;

public class UnknownHostException extends IOException {

    private static final long serialVersionUID = -7643958980572777440L;

    public UnknownHostException() {
        super();
    }

    public UnknownHostException(String message) {
        super(message);
    }

}
