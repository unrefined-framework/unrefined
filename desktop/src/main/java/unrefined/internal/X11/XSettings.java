package unrefined.internal.X11;

import unrefined.desktop.StandardDirectories;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;
import unrefined.util.Strings;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class XSettings {

    private XSettings() {
        throw new NotInstantiableError(XSettings.class);
    }

    private static final Map<String, Object> XSETTINGS;
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT;

    private static void reload(File file, Path path, boolean notify) {
        if (!file.exists()) {
            synchronized (XSETTINGS) {
                if (notify) {
                    for (Map.Entry<String, Object> entry : XSETTINGS.entrySet()) {
                        PROPERTY_CHANGE_SUPPORT.firePropertyChange(entry.getKey(), entry.getValue(), null);
                    }
                }
                XSETTINGS.clear();
            }
        }
        else if (file.canRead()) {
            try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                synchronized (XSETTINGS) {
                    Map<String, Object> buffer;
                    if (notify) buffer = new HashMap<>();
                    else {
                        XSETTINGS.clear();
                        buffer = null;
                    }
                    // File format see https://codeberg.org/derat/xsettingsd#configuration
                    reader.lines().forEach(line -> {
                        int comment = line.indexOf('#');
                        if (comment != -1) line = line.substring(0, comment);
                        if (!Strings.isBlank(line)) {
                            String key = line.substring(0, line.indexOf(' '));
                            String value = line.substring(line.indexOf(' ')).trim();
                            if (Strings.isBlank(value)) return;
                            Object current = null;
                            if (value.startsWith("\"") && value.endsWith("\"")) {
                                // String value
                                current = value.substring(1, value.length() - 1);
                            }
                            else if (value.startsWith("(") && value.endsWith(")")) {
                                // Color value
                                try {
                                    value = value.substring(1, value.length() - 1);
                                    String[] rgba16 = value.split(",");
                                    float[] rgba8 = new float[rgba16.length];
                                    for (int i = 0; i < rgba16.length; i ++) {
                                        rgba8[i] = Integer.parseInt(rgba16[i].trim()) / 65535f;
                                    }
                                    if (rgba8.length == 4) current = new Color(rgba8[0], rgba8[1], rgba8[2], rgba8[3]);
                                    else if (rgba8.length == 3) current = new Color(rgba8[0], rgba8[1], rgba8[2]);
                                }
                                catch (NumberFormatException ignored) {
                                }
                            }
                            else {
                                // Integer value
                                try {
                                    current = Integer.parseInt(value);
                                }
                                catch (NumberFormatException ignored) {
                                }
                            }
                            if (notify) {
                                Object previous = XSETTINGS.get(key);
                                if (previous != current) PROPERTY_CHANGE_SUPPORT.firePropertyChange(key, previous, current);
                                buffer.put(key, current);
                            }
                            else XSETTINGS.put(key, current);
                        }
                    });
                    if (notify) {
                        XSETTINGS.keySet().removeAll(buffer.keySet());
                        for (Map.Entry<String, Object> entry : XSETTINGS.entrySet()) {
                            PROPERTY_CHANGE_SUPPORT.firePropertyChange(entry.getKey(), entry.getValue(), null);
                        }
                        XSETTINGS.clear();
                        XSETTINGS.putAll(buffer);
                    }
                }
            } catch (IOException ignored) {
            }
        }
    }

    static {
        PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(Toolkit.getDefaultToolkit());
        if (OSInfo.IS_X11 && !GraphicsEnvironment.isHeadless()) {
            File settingsHome = new File(StandardDirectories.CONFIG_HOME, "xsettingsd");
            File settingsFile = new File(settingsHome, "xsettingsd.conf");
            Path settingsPath = settingsFile.toPath();
            XSETTINGS = new HashMap<>();
            reload(settingsFile, settingsPath, false);
            Thread thread = new Thread(() -> {
                FileSystem fileSystem = FileSystems.getDefault();
                try {
                    WatchService watcher = fileSystem.newWatchService();
                    Path dir = settingsHome.toPath();
                    dir.register(watcher, ENTRY_MODIFY);
                    while (true) {
                        WatchKey key;
                        try {
                            key = watcher.take();
                        } catch (InterruptedException e) {
                            continue;
                        }
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            if (kind == OVERFLOW) continue;
                            @SuppressWarnings("unchecked")
                            Path path = ((WatchEvent<Path>) event).context();
                            if (path.getFileName().equals(settingsPath.getFileName())) {
                                reload(settingsFile, settingsPath, true);
                            }
                        }
                        if (!key.reset()) break;
                    }
                } catch (IOException ignored) {
                }
            }, "Unrefined XSettings Daemon");
            thread.setDaemon(true);
            thread.start();
        }
        else {
            XSETTINGS = null;
        }
    }

    public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(propertyName, listener);
    }

    public static void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(propertyName, listener);
    }

    public static PropertyChangeListener[] getPropertyChangeListeners() {
        return PROPERTY_CHANGE_SUPPORT.getPropertyChangeListeners();
    }

    public static PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return PROPERTY_CHANGE_SUPPORT.getPropertyChangeListeners(propertyName);
    }

    public static Object getProperty(String propertyName) {
        return XSETTINGS == null ? null : XSETTINGS.get(propertyName);
    }

}
