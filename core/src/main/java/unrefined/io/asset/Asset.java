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
    private volatile long length = -1;

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
        InputStream stream = assetLoader.openStream(pathname);
        if (length == -1) length = stream.available();
        return stream;
    }

    public boolean exists() {
        return assetLoader.exists(pathname);
    }

    public URL toURL() throws AssetNotFoundException, MalformedURLException {
        return assetLoader.toURL(pathname);
    }

    public long length() throws IOException {
        if (length == -1) length = assetLoader.length(pathname);
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
