package unrefined.runtime;

import unrefined.io.ChannelFile;
import unrefined.io.RandomAccessDataInputStream;
import unrefined.io.asset.Asset;
import unrefined.nio.charset.Charsets;
import unrefined.util.TextManager;
import unrefined.util.concurrent.ConcurrentHashSet;
import unrefined.util.function.Functor;
import unrefined.util.function.Operator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BaseTextManager extends TextManager {

    private final Map<Locale, Map<String, String>> localeMaps = new ConcurrentHashMap<>();
    private final Set<Functor<Locale, Locale>> localeMappers = new ConcurrentHashSet<>();

    private void load0(Locale locale, InputStream input, Charset charset) throws IOException {
        if (locale == null) locale = Locale.getDefault();
        if (charset == null) charset = Charsets.ISO_8859_1;
        Properties buffer = new Properties();
        try (InputStreamReader reader = new InputStreamReader(input, charset);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            buffer.load(bufferedReader);
        }
        Map<String, String> map = localeMaps.get(locale);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            localeMaps.put(locale, map);
        }
        for (String key : buffer.stringPropertyNames()) {
            map.put(key.toLowerCase(Locale.ENGLISH), buffer.getProperty(key));
        }
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
    public Map<String, String> unload(Locale locale) {
        return localeMaps.remove(Objects.requireNonNull(locale));
    }

    @Override
    public Set<Map<String, String>> unloadAll(Locale locale) {
        Set<Map<String, String>> result = Collections.unmodifiableSet(new HashSet<>(localeMaps.values()));
        localeMaps.clear();
        return result;
    }

    @Override
    public void addLocaleMapper(Operator<Locale> mapper) {
        localeMappers.add(Objects.requireNonNull(mapper));
    }

    @Override
    public void removeLocaleMapper(Operator<Locale> mapper) {
        localeMappers.remove(Objects.requireNonNull(mapper));
    }

    @Override
    public void clearLocaleMappers() {
        localeMappers.clear();
    }

    @Override
    public Locale getMappedLocale(Locale locale) {
        synchronized (localeMappers) {
            for (Functor<Locale, Locale> mapper : localeMappers) {
                Locale replacement = mapper.apply(locale);
                if (replacement != null) break;
            }
            return locale;
        }
    }

    @Override
    public String get(Locale locale, String key, Object... args) {
        if (locale == null) locale = Locale.getDefault();
        Map<String, String> map = localeMaps.get(getMappedLocale(locale));
        if (map == null) return null;
        else {
            String value = map.get(key);
            if (value == null) return null;
            else return String.format(value, args);
        }
    }

}
