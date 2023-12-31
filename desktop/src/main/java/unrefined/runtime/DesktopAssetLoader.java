package unrefined.runtime;

import unrefined.io.asset.AssetLoader;
import unrefined.io.asset.AssetNotFoundException;
import unrefined.util.concurrent.Producer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class DesktopAssetLoader extends AssetLoader {

    private static String removeStartSeparator(String text) {
        return text.charAt(0) == File.separatorChar ? text.substring(1) : text;
    }

    public static InputStream getResourceAsStream(ClassLoader classLoader, String pathname) {
        return classLoader.getResourceAsStream(removeStartSeparator(pathname));
    }

    public static URL getResource(ClassLoader classLoader, String pathname) {
        return classLoader.getResource(removeStartSeparator(pathname));
    }

    private final Producer<ClassLoader> classLoaderProducer;
    public DesktopAssetLoader(Producer<ClassLoader> classLoaderProducer) {
        this.classLoaderProducer = Objects.requireNonNull(classLoaderProducer);
    }
    public DesktopAssetLoader() {
        this(Thread.currentThread()::getContextClassLoader);
    }

    public Producer<ClassLoader> getClassLoaderProducer() {
        return classLoaderProducer;
    }

    public ClassLoader getClassLoader() {
        return classLoaderProducer.get();
    }

    @Override
    public boolean exists(String pathname) {
        InputStream stream = getResourceAsStream(getClassLoader(), pathname);
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
        InputStream stream = getResourceAsStream(getClassLoader(), pathname);
        if (stream == null) throw new AssetNotFoundException("Could not find asset " + pathname + " with ClassLoader '" + getClassLoader() + "'");
        else return stream;
    }

    @Override
    public URL toURL(String pathname) throws AssetNotFoundException, MalformedURLException {
        new URL("file://" + pathname);
        if (exists(pathname)) return getResource(getClassLoader(), pathname);
        else throw new AssetNotFoundException("Could not find asset " + pathname + " with ClassLoader '" + getClassLoader() + "'");
    }

    @Override
    public long length(String pathname) throws IOException {
        InputStream stream = getResourceAsStream(getClassLoader(), pathname);
        if (stream == null) throw new AssetNotFoundException("Could not find asset " + pathname + " with ClassLoader '" + getClassLoader() + "'");
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
