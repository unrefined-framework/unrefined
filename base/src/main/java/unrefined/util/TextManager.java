package unrefined.util;

import unrefined.context.Environment;
import unrefined.io.asset.Asset;
import unrefined.util.function.Functor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

public abstract class TextManager {

    private static volatile TextManager DEFAULT_INSTANCE;
    private static final Object DEFAULT_INSTANCE_LOCK = new Object();
    public static TextManager defaultTextManager() {
        if (DEFAULT_INSTANCE == null) synchronized (DEFAULT_INSTANCE_LOCK) {
            if (DEFAULT_INSTANCE == null) DEFAULT_INSTANCE = Environment.global().get("unrefined.runtime.textManager", TextManager.class);
        }
        return DEFAULT_INSTANCE;
    }

    public void loadAll(File input) throws IOException {
        loadAll(input, null);
    }
    public void load(Locale locale, File input) throws IOException {
        load(locale, input, null);
    }
    public void load(Locale locale, InputStream input) throws IOException {
        load(locale, input, null);
    }
    public void load(Locale locale, URL input) throws IOException {
        load(locale, input, null);
    }
    public void load(Locale locale, Asset input) throws IOException {
        load(locale, input, null);
    }

    public abstract void loadAll(File input, Charset charset) throws IOException;
    public abstract void load(Locale locale, File input, Charset charset) throws IOException;
    public abstract void load(Locale locale, InputStream input, Charset charset) throws IOException;
    public abstract void load(Locale locale, URL input, Charset charset) throws IOException;
    public abstract void load(Locale locale, Asset input, Charset charset) throws IOException;

    public abstract Properties unload(Locale locale);

    public abstract void addLocaleMapper(Functor<Locale, Locale> mapper);
    public abstract void removeLocaleMapper(Functor<Locale, Locale> mapper);
    public abstract void clearLocaleMappers();
    public abstract Locale getMappedLocale(Locale locale);

    public abstract String get(Locale locale, String key, Object... args);
    public abstract String get(Locale locale, String key, Collection<Object> args);

}
