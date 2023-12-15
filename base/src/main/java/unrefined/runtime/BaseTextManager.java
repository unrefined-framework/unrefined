package unrefined.runtime;

import unrefined.io.ChannelFile;
import unrefined.io.RandomAccessDataInputStream;
import unrefined.io.asset.Asset;
import unrefined.util.TextManager;
import unrefined.util.concurrent.ConcurrentHashSet;
import unrefined.util.function.Functor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BaseTextManager extends TextManager {

    private final Map<Locale, Properties> localeProperties = new ConcurrentHashMap<>();
    private final Set<Functor<Locale, Locale>> localeMappers = new ConcurrentHashSet<>();

    @Override
    public void loadAll(File input, Charset charset) throws IOException {
        Objects.requireNonNull(input);
        String basename = input.getName();
        List<Locale> locales = new ArrayList<>();
        File[] files = input.getParentFile().listFiles(pathname -> {
            if (!pathname.isFile()) return false;
            String name = pathname.getName();
            if (!(name.endsWith(".properties") || name.endsWith(".prop"))) return false;
            if (!name.startsWith(basename)) return false;
            String suffix = name.replaceFirst(basename, "");
            suffix = suffix.substring(0, suffix.length() - (name.endsWith(".properties") ? ".properties".length() : ".prop".length()));
            switch (suffix.length()) {
                case 0:
                    locales.add(null);
                    return true;
                case 6:
                    if (suffix.charAt(0) != '_' || suffix.charAt(3) != '_') return false;
                    locales.add(new Locale(suffix.substring(1, 3), suffix.substring(4, 6)));
                    return true;
                case 3:
                    if (suffix.charAt(0) != '_') return false;
                    locales.add(new Locale(suffix.replaceFirst("_", "")));
                    return true;
                default:
                    return false;
            }
        });
        if (files != null) {
            for (int i = 0; i < files.length; i ++) {
                load(locales.get(i), files[i], charset);
            }
        }
    }

    private void load0(Locale locale, InputStream input, Charset charset) throws IOException {
        if (locale == null) locale = Locale.getDefault();
        if (charset == null) charset = Charset.defaultCharset();
        Properties buffer = new Properties();
        try (InputStreamReader reader = new InputStreamReader(input, charset);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            buffer.load(bufferedReader);
        }
        Properties properties = localeProperties.get(locale);
        if (properties == null) localeProperties.put(locale, buffer);
        else properties.putAll(buffer);
    }

    @Override
    public void load(Locale locale, File input, Charset charset) throws IOException {
        load0(locale, new RandomAccessDataInputStream(new ChannelFile(input, ChannelFile.Mode.READ)), charset);
    }

    @Override
    public void load(Locale locale, InputStream input, Charset charset) throws IOException {
        load0(locale, input, charset);
    }

    @Override
    public void load(Locale locale, URL input, Charset charset) throws IOException {
        load0(locale, input.openStream(), charset);
    }

    @Override
    public void load(Locale locale, Asset input, Charset charset) throws IOException {
        load0(locale, input.openStream(), charset);
    }

    @Override
    public Properties unload(Locale locale) {
        return localeProperties.remove(Objects.requireNonNull(locale));
    }

    @Override
    public void addLocaleMapper(Functor<Locale, Locale> mapper) {
        localeMappers.add(Objects.requireNonNull(mapper));
    }

    @Override
    public void removeLocaleMapper(Functor<Locale, Locale> mapper) {
        localeMappers.remove(Objects.requireNonNull(mapper));
    }

    @Override
    public void clearLocaleMappers() {
        localeMappers.clear();
    }

    @Override
    public Locale getMappedLocale(Locale locale) {
        synchronized (localeMappers) {
            Locale replacement = null;
            for (Functor<Locale, Locale> mapper : localeMappers) {
                replacement = mapper.apply(locale);
                if (replacement != null) break;
            }
            return replacement;
        }
    }

    @Override
    public String get(Locale locale, String key, Object... args) {
        if (locale == null) locale = Locale.getDefault();
        Properties properties = localeProperties.get(getMappedLocale(locale));
        if (properties == null) return null;
        else {
            String value = properties.getProperty(key);
            if (value == null) return null;
            else return String.format(value, args);
        }
    }

    @Override
    public String get(Locale locale, String key, Collection<Object> args) {
        return get(locale, key, args.toArray(new Object[0]));
    }

}
