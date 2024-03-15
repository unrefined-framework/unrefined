package unrefined.media.graphics;

import java.io.IOException;

public class CursorNotFoundException extends IOException {

    private static final long serialVersionUID = 2338402687585417549L;

    public CursorNotFoundException() {
        super();
    }

    public CursorNotFoundException(String message) {
        super(message);
    }

    public CursorNotFoundException(int type) {
        super(Cursor.Type.toString(type));
    }

}
