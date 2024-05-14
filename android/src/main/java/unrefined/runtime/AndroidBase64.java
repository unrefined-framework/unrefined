package unrefined.runtime;

import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import unrefined.util.Base64;

import java.io.InputStream;
import java.io.OutputStream;

public class AndroidBase64 extends Base64 {

    @Override
    public byte[] encode(byte[] input, int offset, int length, int flags) {
        return android.util.Base64.encode(input, offset, length, flags);
    }

    @Override
    public OutputStream wrap(OutputStream out, int flags) {
        return new Base64OutputStream(out, Base64.Flag.removeUnusedBits(flags));
    }

    @Override
    public byte[] decode(byte[] input, int offset, int length, int flags) {
        return android.util.Base64.decode(input, offset, length, flags);
    }

    @Override
    public InputStream wrap(InputStream in, int flags) {
        return new Base64InputStream(in, flags);
    }

}
