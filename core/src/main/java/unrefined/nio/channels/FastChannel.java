package unrefined.nio.channels;

import unrefined.util.NotInstantiableError;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

public final class FastChannel {

    private FastChannel() {
        throw new NotInstantiableError(FastChannel.class);
    }

    public static InputStream wrapInputStream(ReadableByteChannel channel) {
        return Channels.newInputStream(channel);
    }

    public static OutputStream wrapOutputStream(WritableByteChannel channel) {
        return Channels.newOutputStream(channel);
    }

    public static ReadableByteChannel wrapChannel(InputStream in) {
        return Channels.newChannel(in);
    }

    public static WritableByteChannel wrapChannel(OutputStream out) {
        return Channels.newChannel(out);
    }

    public static Reader wrapReader(ReadableByteChannel channel, String encoding) {
        return Channels.newReader(channel, encoding == null ? Charset.defaultCharset().name() : encoding);
    }

    public static Reader wrapReader(ReadableByteChannel channel, Charset charset) {
        return Channels.newReader(channel, (charset == null ? Charset.defaultCharset() : charset).newDecoder(), -1);
    }

    public static Writer wrapWriter(WritableByteChannel channel, String encoding) {
        return Channels.newWriter(channel, encoding == null ? Charset.defaultCharset().name() : encoding);
    }

    public static Writer wrapWriter(WritableByteChannel channel, Charset charset) {
        return Channels.newWriter(channel, (charset == null ? Charset.defaultCharset() : charset).newEncoder(), -1);
    }

    public static Reader wrapReader(ReadableByteChannel channel) {
        return wrapReader(channel, (Charset) null);
    }

    public static Writer wrapWriter(WritableByteChannel channel) {
        return wrapWriter(channel, (Charset) null);
    }

}
