package unrefined.io.asset;

import java.io.IOException;

public class AssetNotFoundException extends IOException {

    private static final long serialVersionUID = -5900894350521601584L;

    public AssetNotFoundException() {
        super();
    }

    public AssetNotFoundException(String message) {
        super(message);
    }

    public AssetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssetNotFoundException(Throwable cause) {
        super(cause);
    }

}
