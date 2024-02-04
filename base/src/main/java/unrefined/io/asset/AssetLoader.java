package unrefined.io.asset;

import unrefined.context.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AssetLoader {

    private static volatile AssetLoader DEFAULT_INSTANCE;
    private static final Object DEFAULT_INSTANCE_LOCK = new Object();
    public static AssetLoader defaultInstance() {
        if (DEFAULT_INSTANCE == null) synchronized (DEFAULT_INSTANCE_LOCK) {
            if (DEFAULT_INSTANCE == null) DEFAULT_INSTANCE = Environment.global.get("unrefined.runtime.assetLoader", AssetLoader.class);
        }
        return DEFAULT_INSTANCE;
    }

    public abstract boolean exists(String pathname);
    public abstract InputStream openStream(String pathname) throws IOException;
    public abstract URL toURL(String pathname) throws AssetNotFoundException, MalformedURLException;
    public abstract long length(String pathname) throws IOException;

}
