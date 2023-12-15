package unrefined.internal.windows;

import unrefined.desktop.FontFactory;
import unrefined.internal.NativeStringUtils;
import unrefined.internal.SystemUtils;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.awt.Font;
import java.awt.Toolkit;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.foreign.ValueLayout.*;
import static unrefined.internal.windows.WindowsLibrary.*;

public final class WindowsFontUtils {

    private WindowsFontUtils() {
        throw new NotInstantiableError(WindowsFontUtils.class);
    }

    private static final Linker LINKER = Linker.nativeLinker();
    private static final MethodHandle SystemParametersInfoWMethodHandle;
    private static final MemoryLayout LOGFONTWMemoryLayout;
    private static final MemorySegment LOGFONTW_BUFFER;
    private static final MethodHandle RegOpenKeyExWMethodHandle;
    private static final MemorySegment hKeyAccessibility;
    private static final MemorySegment hEvent;
    private static final MethodHandle RegQueryValueExWMethodHandle;
    private static final MethodHandle CreateEventWMethodHandle;
    private static final MethodHandle RegNotifyChangeKeyValueMethodHandle;
    private static final MethodHandle WaitForSingleObjectMethodHandle;
    private static final MethodHandle RegCloseKeyMethodHandle;
    private static final MethodHandle CloseHandleMethodHandle;

    static {
        if (SystemUtils.IS_WINDOWS) {
            SystemParametersInfoWMethodHandle = USER32_LOOKUP.find("SystemParametersInfoW")
                    .map(symbolSegment ->
                            LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT, ADDRESS, JAVA_INT)))
                    .orElseThrow(UnexpectedError::new);
            LOGFONTWMemoryLayout = MemoryLayout.structLayout(
                    JAVA_INT.withName("lfHeight"),
                    JAVA_INT.withName("lfWidth"),
                    JAVA_INT.withName("lfEscapement"),
                    JAVA_INT.withName("lfOrientation"),
                    JAVA_INT.withName("lfWeight"),
                    JAVA_BYTE.withName("lfItalic"),
                    JAVA_BYTE.withName("lfUnderline"),
                    JAVA_BYTE.withName("lfStrikeOut"),
                    JAVA_BYTE.withName("lfCharSet"),
                    JAVA_BYTE.withName("lfOutPrecision"),
                    JAVA_BYTE.withName("lfClipPrecision"),
                    JAVA_BYTE.withName("lfQuality"),
                    JAVA_BYTE.withName("lfPitchAndFamily"),
                    MemoryLayout.sequenceLayout(32, JAVA_CHAR).withName("lfFaceName")
            );
            LOGFONTW_BUFFER = Arena.global().allocate(LOGFONTWMemoryLayout);
            RegOpenKeyExWMethodHandle = ADVAPI32_LOOKUP.find("RegOpenKeyExW")
                    .map(symbolSegment ->
                            LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, JAVA_INT, ADDRESS)))
                    .orElseThrow(UnexpectedError::new);
            RegQueryValueExWMethodHandle = ADVAPI32_LOOKUP.find("RegQueryValueExW")
                    .map(symbolSegment ->
                            LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS)))
                    .orElseThrow(UnexpectedError::new);
            RegNotifyChangeKeyValueMethodHandle = ADVAPI32_LOOKUP.find("RegNotifyChangeKeyValue")
                    .map(symbolSegment ->
                            LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, JAVA_INT, ADDRESS, JAVA_INT)))
                    .orElseThrow(UnexpectedError::new);
            RegCloseKeyMethodHandle = ADVAPI32_LOOKUP.find("RegCloseKey")
                    .map(symbolSegment ->
                            LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, ADDRESS)))
                    .orElseThrow(UnexpectedError::new);
            CreateEventWMethodHandle = KERNEL32_LOOKUP.find("CreateEventW")
                    .map(symbolSegment ->
                            LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_INT, JAVA_INT, ADDRESS)))
                    .orElseThrow(UnexpectedError::new);
            WaitForSingleObjectMethodHandle = KERNEL32_LOOKUP.find("WaitForSingleObject")
                    .map(symbolSegment ->
                            LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT)))
                    .orElseThrow(UnexpectedError::new);
            CloseHandleMethodHandle = KERNEL32_LOOKUP.find("CloseHandle")
                    .map(symbolSegment ->
                            LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, ADDRESS)))
                    .orElseThrow(UnexpectedError::new);
            MemorySegment hKeyBuffer;
            MemorySegment hEventBuffer;
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment buffer = arena.allocate(ADDRESS);
                if ((int) RegOpenKeyExWMethodHandle.invoke(MemorySegment.ofAddress(0x80000001L),
                        arena.allocateArray(JAVA_BYTE, "SOFTWARE\\Microsoft\\Accessibility".getBytes(StandardCharsets.UTF_16LE)),
                        0, 0x20019, buffer) == 0) {
                    hEventBuffer = Arena.global().allocate(ADDRESS);
                    hKeyBuffer = Arena.global().allocate(ADDRESS).copyFrom(buffer);
                }
                else {
                    hKeyBuffer = MemorySegment.NULL;
                    hEventBuffer = MemorySegment.NULL;
                }
            }
            catch (Throwable e) {
                hKeyBuffer = MemorySegment.NULL;
                hEventBuffer = MemorySegment.NULL;
            }
            hKeyAccessibility = hKeyBuffer;
            hEvent = hEventBuffer;
            daemon();
        }
        else {
            SystemParametersInfoWMethodHandle = null;
            LOGFONTWMemoryLayout = null;
            LOGFONTW_BUFFER = null;
            RegOpenKeyExWMethodHandle = null;
            hKeyAccessibility = null;
            hEvent = null;
            RegQueryValueExWMethodHandle = null;
            CreateEventWMethodHandle = null;
            RegNotifyChangeKeyValueMethodHandle = null;
            WaitForSingleObjectMethodHandle = null;
            RegCloseKeyMethodHandle = null;
            CloseHandleMethodHandle = null;
        }
    }

    public static final Font ICONTITLELOGFONT = Font.decode(getDefaultFontName());

    private static String getDefaultFontName() {
        try {
            if ((int) SystemParametersInfoWMethodHandle.invoke(0x001F, LOGFONTWMemoryLayout.byteSize(), LOGFONTW_BUFFER, 0) != 0) {
                boolean bold = (int) LOGFONTWMemoryLayout.varHandle(PathElement.groupElement("lfWeight")).get(LOGFONTW_BUFFER) > 500;
                boolean italic = (int) LOGFONTWMemoryLayout.varHandle(PathElement.groupElement("lfItalic")).get(LOGFONTW_BUFFER) != 0;
                StringBuilder builder = new StringBuilder(NativeStringUtils.getString(LOGFONTW_BUFFER.asSlice(28), StandardCharsets.UTF_16LE));
                if (bold || italic) builder.append(" ");
                if (bold) builder.append("Bold");
                if (italic) builder.append("Italic");
                return builder.toString();
            }
        } catch (Throwable ignored) {
        }
        Font font = (Font) Toolkit.getDefaultToolkit().getDesktopProperty(SystemUtils.IS_WINDOWS_PE ? "win.defaultGUI.font" : "win.messagebox.font");
        return font == null ? null : font.getFontName();
    }

    private static volatile float fontScale = 1;

    public static float getFontScale() {
        return fontScale;
    }

    private static int getTextScaleFactor() {
        if (!MemorySegment.NULL.equals(hKeyAccessibility)) {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment type = arena.allocate(JAVA_INT);
                MemorySegment dwBuffer = arena.allocate(JAVA_INT);
                if ((int) RegQueryValueExWMethodHandle.invoke(hKeyAccessibility,
                        arena.allocateArray(JAVA_BYTE, "TextScaleFactor".getBytes(StandardCharsets.UTF_16LE)),
                        0, type, dwBuffer, (int) JAVA_INT.byteSize()) == 0) {
                    int dw = dwBuffer.get(JAVA_INT, 0);
                    if (type.get(JAVA_INT, 0) == 0x00000004 && dw >= 100 && dw <= 225) return dw;
                }
            } catch (Throwable ignored) {
            }
        }
        return 100;
    }

    private static void daemon() {
        if (!MemorySegment.NULL.equals(hKeyAccessibility)) {
            Thread thread = new Thread(() -> {
                int dwFilter = 0x00000001 | 0x00000002 | 0x00000004 | 0x00000008;
                while (!shutdown.get()) {
                    try {
                        if ((int) RegNotifyChangeKeyValueMethodHandle.invoke(hKeyAccessibility, 1, dwFilter, hEvent, 1) != 0) continue;
                        if ((int) WaitForSingleObjectMethodHandle.invoke(hEvent, -1) != 0xFFFFFFFF)
                            fontScale = getTextScaleFactor() / 100f;
                    } catch (Throwable ignored) {
                    }
                }
            }, "UXGL Registry Daemon");
            thread.setDaemon(true);
            thread.start();
            Runtime.getRuntime().addShutdownHook(new Thread(WindowsFontUtils::shutdown, "UXGL Registry Daemon Cleanup"));
        }
    }

    private static final AtomicBoolean shutdown = new AtomicBoolean(false);

    private static void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            try {
                try {
                    RegCloseKeyMethodHandle.invoke(hKeyAccessibility);
                }
                finally {
                    CloseHandleMethodHandle.invoke(hEvent);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    public static void patch() {
    }

}
