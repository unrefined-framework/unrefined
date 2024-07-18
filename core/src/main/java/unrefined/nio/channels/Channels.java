package unrefined.nio.channels;

import unrefined.util.NotInstantiableError;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

public final class Channels {

    private Channels() {
        throw new NotInstantiableError(Channels.class);
    }

    public static InputStream wrapInputStream(ReadableByteChannel channel) {
        return java.nio.channels.Channels.newInputStream(channel);
    }

    public static OutputStream wrapOutputStream(WritableByteChannel channel) {
        return java.nio.channels.Channels.newOutputStream(channel);
    }

    public static ReadableByteChannel wrapChannel(InputStream in) {
        return java.nio.channels.Channels.newChannel(in);
    }

    public static WritableByteChannel wrapChannel(OutputStream out) {
        return java.nio.channels.Channels.newChannel(out);
    }

    public static Reader wrapReader(ReadableByteChannel channel, String encoding) {
        return java.nio.channels.Channels.newReader(channel, encoding == null ? Charset.defaultCharset().name() : encoding);
    }

    public static Reader wrapReader(ReadableByteChannel channel, Charset charset) {
        return java.nio.channels.Channels.newReader(channel, (charset == null ? Charset.defaultCharset() : charset).newDecoder(), -1);
    }

    public static Writer wrapWriter(WritableByteChannel channel, String encoding) {
        return java.nio.channels.Channels.newWriter(channel, encoding == null ? Charset.defaultCharset().name() : encoding);
    }

    public static Writer wrapWriter(WritableByteChannel channel, Charset charset) {
        return java.nio.channels.Channels.newWriter(channel, (charset == null ? Charset.defaultCharset() : charset).newEncoder(), -1);
    }

    public static Reader wrapReader(ReadableByteChannel channel) {
        return wrapReader(channel, (Charset) null);
    }

    public static Writer wrapWriter(WritableByteChannel channel) {
        return wrapWriter(channel, (Charset) null);
    }

}
