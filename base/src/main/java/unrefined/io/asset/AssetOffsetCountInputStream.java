package unrefined.io.asset;

import unrefined.io.OffsetCountInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AssetOffsetCountInputStream extends OffsetCountInputStream {

    protected final Asset asset;
    protected InputStream stream = null;

    public AssetOffsetCountInputStream(Asset asset) {
        this.asset = Objects.requireNonNull(asset);
    }

    private void openStreamIfNull() throws IOException {
        if (stream == null) stream = asset.openStream();
    }

    @Override
    public int read(byte[] b) throws IOException {
        openStreamIfNull();
        int count = stream.read(b);
        offset += count;
        return count;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        openStreamIfNull();
        int count = stream.read(b, off, len);
        offset += count;
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        openStreamIfNull();
        long count = stream.skip(n);
        offset += count;
        return count;
    }

    @Override
    public int available() throws IOException {
        openStreamIfNull();
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        openStreamIfNull();
        stream.close();
    }

    @Override
    public int read() throws IOException {
        openStreamIfNull();
        int result = stream.read();
        offset ++;
        return result;
    }

}
