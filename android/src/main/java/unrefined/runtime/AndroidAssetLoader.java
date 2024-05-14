package unrefined.runtime;

import android.content.res.AssetManager;
import unrefined.android.ActivityHolder;
import unrefined.io.asset.AssetLoader;
import unrefined.io.asset.AssetNotFoundException;
import unrefined.util.concurrent.Producer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class AndroidAssetLoader extends AssetLoader {

    private static String removeStartSeparator(String text) {
        return text.charAt(0) == '/' ? text.substring(1) : text;
    }

    private final Producer<AssetManager> assetManagerProducer;
    public AndroidAssetLoader(Producer<AssetManager> assetManagerProducer) {
        this.assetManagerProducer = Objects.requireNonNull(assetManagerProducer);
    }
    public AndroidAssetLoader() {
        this(() -> ActivityHolder.get().getAssets());
    }

    public Producer<AssetManager> getAssetManagerProducer() {
        return assetManagerProducer;
    }

    public AssetManager getAssetManager() {
        return assetManagerProducer.get();
    }

    @Override
    public boolean exists(String pathname) {
        InputStream stream;
        try {
            stream = getAssetManager().open(removeStartSeparator(pathname));
        } catch (IOException e) {
            return false;
        }
        if (stream == null) return false;
        else {
            try {
                return true;
            }
            finally {
                try {
                    stream.close();
                }
                catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public InputStream openStream(String pathname) throws IOException {
        return getAssetManager().open(removeStartSeparator(pathname));
    }

    @Override
    public URL toURL(String pathname) throws AssetNotFoundException, MalformedURLException {
        URL url = new URL("file:///android_asset/" + removeStartSeparator(pathname));
        if (exists(pathname)) return url;
        else throw new AssetNotFoundException("Could not find asset " + pathname + " with AssetManager '" + getAssetManager() + "'");
    }

    @Override
    public long length(String pathname) throws IOException {
        InputStream stream = getAssetManager().open(removeStartSeparator(pathname));
        if (stream == null) throw new AssetNotFoundException("Could not find asset " + pathname + " with AssetManager '" + getAssetManager() + "'");
        else {
            try {
                return stream.available();
            }
            finally {
                try {
                    stream.close();
                }
                catch (IOException ignored) {
                }
            }
        }
    }

}
