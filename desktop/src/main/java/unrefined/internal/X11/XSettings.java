package unrefined.internal.X11;

import unrefined.desktop.StandardDirectories;
import unrefined.internal.SystemUtils;
import unrefined.util.NotInstantiableError;

import java.awt.Color;
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

    private static final File XSETTINGS_HOME;
    private static final String XSETTINGS_NAME;
    private static final File XSETTINGS_FILE;
    private static final Path XSETTINGS_PATH;
    private static final Map<String, Object> XSETTINGS;
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT;

    private static void reload(boolean notify) {
        if (!XSETTINGS_FILE.exists()) {
            synchronized (XSETTINGS) {
                if (notify) {
                    for (Map.Entry<String, Object> entry : XSETTINGS.entrySet()) {
                        PROPERTY_CHANGE_SUPPORT.firePropertyChange(entry.getKey(), entry.getValue(), null);
                    }
                }
                XSETTINGS.clear();
            }
        }
        else if (XSETTINGS_FILE.canRead()) {
            try (BufferedReader reader = Files.newBufferedReader(XSETTINGS_PATH)) {
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
                        if (!line.isBlank()) {
                            String key = line.substring(0, line.indexOf(' '));
                            String value = line.substring(line.indexOf(' ')).trim();
                            if (value.isBlank()) return;
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

    private static void daemon() {
        if (!SystemUtils.IS_X11) return;
        reload(false);
        Thread thread = new Thread(() -> {
            FileSystem fileSystem = FileSystems.getDefault();
            try {
                WatchService watcher = fileSystem.newWatchService();
                Path dir = XSETTINGS_HOME.toPath();
                dir.register(watcher, ENTRY_MODIFY);
                while (true) {

                    // wait for key to be signaled
                    WatchKey key;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException e) {
                        return;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        // This key is registered only
                        // for ENTRY_CREATE events,
                        // but an OVERFLOW event can
                        // occur regardless if events
                        // are lost or discarded.
                        if (kind == OVERFLOW) continue;

                        // The filename is the
                        // context of the event.
                        @SuppressWarnings("unchecked")
                        Path path = ((WatchEvent<Path>) event).context();

                        // Verify that the new file is the xsettingsd.conf file.
                        if (path.getFileName().equals(XSETTINGS_PATH.getFileName())) {
                            reload(true);
                        }
                    }

                    // Reset the key -- this step is critical if you want to
                    // receive further watch events.  If the key is no longer valid,
                    // the directory is inaccessible so exit the loop.
                    if (!key.reset()) break;
                }
            } catch (IOException ignored) {
            }
        }, "UXGL XSettings Daemon");
        thread.setDaemon(true);
        thread.start();
    }

    static {
        PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(Toolkit.getDefaultToolkit());
        if (SystemUtils.IS_X11) {
            XSETTINGS_HOME = new File(StandardDirectories.CONFIG_HOME, "xsettingsd");
            XSETTINGS_NAME = "xsettingsd.conf";
            XSETTINGS_FILE = new File(XSETTINGS_HOME, XSETTINGS_NAME);
            XSETTINGS_PATH = XSETTINGS_FILE.toPath();
            XSETTINGS = new HashMap<>();
            daemon();
        }
        else {
            XSETTINGS_HOME = null;
            XSETTINGS_NAME = null;
            XSETTINGS_FILE = null;
            XSETTINGS_PATH = null;
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
