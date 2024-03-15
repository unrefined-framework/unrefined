package unrefined.media.graphics;

import java.io.IOException;

public class FontNotFoundException extends IOException {

    private static final long serialVersionUID = -6308862551547769844L;

    public FontNotFoundException() {
        super();
    }

    public FontNotFoundException(String message) {
        super(message);
    }

    public FontNotFoundException(String familyName, int style) {
        super(familyName + " " + Font.Style.toString(style));
    }

}
