package unrefined.io.asset;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Asset {

    private final AssetLoader assetLoader;
    private final String pathname;
    private final String name;
    private volatile Boolean exists = null;
    private final Object existsLock = new Object();
    private volatile Long length = null;
    private final Object lengthLock = new Object();

    public Asset(String pathname) {
        this(pathname, null);
    }

    public Asset(String pathname, AssetLoader assetLoader) {
        Objects.requireNonNull(pathname);
        this.assetLoader = assetLoader == null ? AssetLoader.defaultInstance() : assetLoader;
        this.pathname = pathname;
        this.name = pathname.substring(pathname.lastIndexOf('/') + 1);
    }

    public AssetLoader getAssetLoader() {
        return assetLoader;
    }

    public String getPathname() {
        return pathname;
    }

    public String getName() {
        return name;
    }

    public InputStream openStream() throws IOException {
        try {
            InputStream stream = assetLoader.openStream(pathname);
            if (length == null) synchronized (lengthLock) {
                if (length == null) length = (long) stream.available();
            }
            return stream;
        }
        catch (AssetNotFoundException e) {
            if (exists == null) synchronized (existsLock) {
                if (exists == null) exists = assetLoader.exists(pathname);
            }
            throw e;
        }
    }

    public boolean exists() {
        if (exists == null) synchronized (existsLock) {
            if (exists == null) exists = assetLoader.exists(pathname);
        }
        return exists;
    }

    public URL toURL() throws AssetNotFoundException, MalformedURLException {
        return assetLoader.toURL(pathname);
    }

    public long length() throws IOException {
        if (length == null) synchronized (lengthLock) {
            if (length == null) length = assetLoader.length(pathname);
        }
        return length;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "assetLoader=" + assetLoader +
                ", pathname='" + pathname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Asset asset = (Asset) o;

        if (!assetLoader.equals(asset.assetLoader)) return false;
        return pathname.equals(asset.pathname);
    }

    @Override
    public int hashCode() {
        int result = assetLoader.hashCode();
        result = 31 * result + pathname.hashCode();
        return result;
    }

}
