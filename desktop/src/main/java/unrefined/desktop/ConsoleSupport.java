package unrefined.desktop;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Library;
import com.kenai.jffi.Type;
import unrefined.internal.posix.POSIXConsoleSupport;
import unrefined.internal.windows.WindowsConsoleSupport;
import unrefined.io.console.Ansi;
import unrefined.io.console.AnsiOutputStream;
import unrefined.media.graphics.Dimension;
import unrefined.util.UnexpectedError;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static unrefined.desktop.ForeignSupport.INVOKER;
import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.desktop.UnsafeSupport.UNSAFE;
import static unrefined.internal.windows.WindowsLibrary.Kernel32;

public final class ConsoleSupport {

    private static final String TERM = System.getenv("TERM");
    private static final String EMACS = System.getenv("INSIDE_EMACS");
    private static final String COLORTERM = System.getenv("COLORTERM");
    
    public static final Charset CHARSET;
    public static final int ANSI_TYPE;
    public static final int MAX_COLORS;
    public static final boolean IS_TERMINAL;
    
    private static final AnsiOutputStream ANSI_OUTPUT;
    private static final AnsiOutputStream ANSI_ERROR;

    private static final FileOutputStream SYSTEM_OUTPUT = new FileOutputStream(FileDescriptor.out);
    private static final FileOutputStream SYSTEM_ERROR = new FileOutputStream(FileDescriptor.err);

    private static final OutputStream FILTERED_OUTPUT;
    private static final OutputStream FILTERED_ERROR;

    public interface SizeProducer {
        int width();
        int height();
        void size(Dimension size);
    }

    public static final SizeProducer SIZE_PRODUCER;

    private static final int SIZE_PRODUCER_TYPE;
    private static final int SIZE_PRODUCER_TYPE_NULL = 0;
    private static final int SIZE_PRODUCER_TYPE_POSIX = 1;
    private static final int SIZE_PRODUCER_TYPE_WINDOWS = 2;
    private static final int SIZE_PRODUCER_TYPE_EXECUTOR = 3;

    static {
        CHARSET = Charset.forName(System.getProperty("stdout.encoding", System.getProperty("sun.stdout.encoding")));
        int colors = -1; String property;
        if ((property = System.getProperty("unrefined.io.console.colors")) != null) {
            try {
                colors = Integer.parseInt(property);
            }
            catch (NumberFormatException ignored) {
            }
        }
        if (colors == -1) {
            // If the COLORTERM env variable contains "truecolor" or "24bit", assume true color support
            // see https://gist.github.com/XVilka/8346728#true-color-detection
            if (COLORTERM != null && (COLORTERM.contains("truecolor") || COLORTERM.contains("24bit"))) {
                colors = 16777216;
            }
            // check the if TERM contains -direct
            else if (TERM != null && TERM.contains("-direct")) {
                colors = 16777216;
            }
            // check the if TERM contains -256color
            else if (TERM != null && TERM.contains("-256color")) colors = 256;
            // else defaults to 16 colors
            else colors = 16;
            System.setProperty("unrefined.io.console.colors", Integer.toString(colors));
        }
        MAX_COLORS = colors;

        long isatty = Library.getDefault().getSymbolAddress("isatty");
        if (isatty == 0) {
            IS_TERMINAL = WindowsConsoleSupport.IS_TERMINAL;
            if (IS_TERMINAL) {
                if (WindowsConsoleSupport.IS_ANSI_TERMINAL) ANSI_TYPE = Ansi.Type.NATIVE;
                else if (WindowsConsoleSupport.EMULATION_AVAILABLE) ANSI_TYPE = Ansi.Type.EMULATION;
                else ANSI_TYPE = Ansi.Type.UNSUPPORTED;
                if (WindowsConsoleSupport.IS_MINGW) SIZE_PRODUCER_TYPE = SIZE_PRODUCER_TYPE_EXECUTOR;
                else SIZE_PRODUCER_TYPE = SIZE_PRODUCER_TYPE_WINDOWS;
            }
            else {
                ANSI_TYPE = Ansi.Type.REDIRECTED;
                SIZE_PRODUCER_TYPE = SIZE_PRODUCER_TYPE_NULL;
            }
        }
        else {
            CallContext context = CallContext.getCallContext(Type.SINT, new Type[] {Type.SINT}, CallingConvention.DEFAULT, false);
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(context);
            if (ABI.I == 8) heapInvocationBuffer.putLong(1);
            else heapInvocationBuffer.putInt(1);
            boolean tty = ForeignSupport.NATIVE_INT_INVOKER.invoke(context, isatty, heapInvocationBuffer) != 0;
            if (tty && "dumb".equals(TERM) && EMACS != null && !EMACS.contains("comint")) tty = false;
            IS_TERMINAL = tty;
            if (IS_TERMINAL) {
                ANSI_TYPE = Ansi.Type.NATIVE;
                SIZE_PRODUCER_TYPE = POSIXConsoleSupport.IOCTL_SUPPORTED ? SIZE_PRODUCER_TYPE_POSIX : SIZE_PRODUCER_TYPE_EXECUTOR;
            }
            else {
                ANSI_TYPE = Ansi.Type.REDIRECTED;
                SIZE_PRODUCER_TYPE = SIZE_PRODUCER_TYPE_NULL;
            }
        }
        if (ANSI_TYPE == Ansi.Type.EMULATION) {
            // FIXME
            FILTERED_OUTPUT = null;
            FILTERED_ERROR = null;
        }
        else {
            FILTERED_OUTPUT = SYSTEM_OUTPUT;
            FILTERED_ERROR = SYSTEM_ERROR;
        }
        switch (SIZE_PRODUCER_TYPE) {
            case SIZE_PRODUCER_TYPE_EXECUTOR:
                String name = Executor.getConsoleName();
                if (name != null && !name.isEmpty()) {
                    SIZE_PRODUCER = new SizeProducer() {
                        @Override
                        public int width() {
                            return Executor.getTerminalWidth(name);
                        }
                        @Override
                        public int height() {
                            return Executor.getTerminalHeight(name);
                        }
                        @Override
                        public void size(Dimension size) {
                            Executor.getTerminalSize(name, size);
                        }
                    };
                    break;
                }
            case SIZE_PRODUCER_TYPE_NULL:
            default:
                SIZE_PRODUCER = new SizeProducer() {
                    @Override
                    public int width() {
                        return 0;
                    }
                    @Override
                    public int height() {
                        return 0;
                    }
                    @Override
                    public void size(Dimension size) {
                        size.setDimension(0, 0);
                    }
                };
                break;
            case SIZE_PRODUCER_TYPE_POSIX:
                long winsize = UNSAFE.allocateMemory(Type.USHORT.size() * 4L);
                final Function ioctl = new Function(Library.getDefault().getSymbolAddress("ioctl"),
                        CallContext.getCallContext(Type.SINT, new Type[] {Type.SINT, Type.SINT, Type.POINTER},
                                CallingConvention.DEFAULT, false));
                HeapInvocationBuffer ioctlBuffer = new HeapInvocationBuffer(ioctl);
                if (ABI.I == 8) {
                    ioctlBuffer.putLong(1);
                }
                else {
                    ioctlBuffer.putInt(1);
                }
                if (ABI.I == 8) {
                    ioctlBuffer.putLong(POSIXConsoleSupport.TIOCGWINSZ);
                }
                else {
                    ioctlBuffer.putInt(POSIXConsoleSupport.TIOCGWINSZ);
                }
                ioctlBuffer.putAddress(winsize);
                SIZE_PRODUCER = new SizeProducer() {
                    @Override
                    public int width() {
                        synchronized (ioctl) {
                            INVOKER.invokeInt(ioctl, ioctlBuffer);
                            return MEMORY_IO.getShort(winsize + Type.USHORT.size());
                        }
                    }
                    @Override
                    public int height() {
                        synchronized (ioctl) {
                            INVOKER.invokeInt(ioctl, ioctlBuffer);
                            return MEMORY_IO.getShort(winsize);
                        }
                    }
                    @Override
                    public void size(Dimension size) {
                        synchronized (ioctl) {
                            INVOKER.invokeInt(ioctl, ioctlBuffer);
                            size.setDimension(MEMORY_IO.getShort(winsize + Type.USHORT.size()), MEMORY_IO.getShort(winsize));
                        }
                    }
                };
                break;
            case SIZE_PRODUCER_TYPE_WINDOWS:
                long lpConsoleScreenBufferInfo = UNSAFE.allocateMemory(32);
                final Function GetConsoleScreenBufferInfo = new Function(Kernel32.getSymbolAddress("GetConsoleScreenBufferInfo"),
                        CallContext.getCallContext(Type.SINT, new Type[] {Type.POINTER, Type.POINTER}, CallingConvention.STDCALL, false));
                HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(GetConsoleScreenBufferInfo);
                heapInvocationBuffer.putAddress(WindowsConsoleSupport.OUTPUT);
                heapInvocationBuffer.putAddress(lpConsoleScreenBufferInfo);
                SIZE_PRODUCER = new SizeProducer() {
                    @Override
                    public int width() {
                        synchronized (GetConsoleScreenBufferInfo) {
                            if (INVOKER.invokeInt(GetConsoleScreenBufferInfo, heapInvocationBuffer) != 0) {
                                return MEMORY_IO.getShort(lpConsoleScreenBufferInfo + 20)
                                        - MEMORY_IO.getShort(lpConsoleScreenBufferInfo + 16) + 1;
                            }
                            else return 0;
                        }
                    }
                    @Override
                    public int height() {
                        synchronized (GetConsoleScreenBufferInfo) {
                            if (INVOKER.invokeInt(GetConsoleScreenBufferInfo, heapInvocationBuffer) != 0) {
                                return MEMORY_IO.getShort(lpConsoleScreenBufferInfo + 22)
                                        - MEMORY_IO.getShort(lpConsoleScreenBufferInfo + 18) + 1;
                            }
                            else return 0;
                        }
                    }
                    @Override
                    public void size(Dimension size) {
                        synchronized (GetConsoleScreenBufferInfo) {
                            if (INVOKER.invokeInt(GetConsoleScreenBufferInfo, heapInvocationBuffer) != 0) {
                                size.setDimension(MEMORY_IO.getShort(lpConsoleScreenBufferInfo + 20)
                                                - MEMORY_IO.getShort(lpConsoleScreenBufferInfo + 16) + 1,
                                        MEMORY_IO.getShort(lpConsoleScreenBufferInfo + 22)
                                                - MEMORY_IO.getShort(lpConsoleScreenBufferInfo + 18) + 1);
                            }
                            else size.setDimension(0, 0);
                        }
                    }
                };
                break;
        }
        try {
            System.setOut(new PrintStream(ANSI_OUTPUT = new AnsiOutputStream(new BufferedOutputStream(FILTERED_OUTPUT, 128),
                    Ansi.Mode.AUTO, ANSI_TYPE, MAX_COLORS, CHARSET),
                    true, CHARSET.displayName()));
            System.setErr(new PrintStream(ANSI_ERROR = new AnsiOutputStream(new BufferedOutputStream(FILTERED_ERROR, 128),
                    Ansi.Mode.AUTO, ANSI_TYPE, MAX_COLORS, CHARSET),
                    true, CHARSET.displayName()));
        } catch (UnsupportedEncodingException e) {
            throw new UnexpectedError(e);
        }
    }
    
    public static void setAnsiMode(int mode) {
        ANSI_OUTPUT.setMode(mode);
        ANSI_ERROR.setMode(mode);
    }

    public static int getAnsiMode() {
        return ANSI_OUTPUT.getMode();
    }

    public static class Executor {

        private static final String STTY_COMMAND;
        private static final String TTY_COMMAND;
        private static final Pattern ROWS_PATTERN;
        private static final Pattern COLUMNS_PATTERN;

        private static final String TTY_NAME = OSInfo.IS_WINDOWS ? "tty.exe" : "tty";
        private static final String STTY_NAME = OSInfo.IS_WINDOWS ? "stty.exe" : "stty";

        static {
            String tty = null;
            String stty = null;
            String path = System.getenv("PATH");
            if (path != null) {
                String[] paths = path.split(File.pathSeparator);
                for (String p : paths) {
                    File ttyFile = new File(p, TTY_NAME);
                    if (tty == null && ttyFile.canExecute()) {
                        tty = ttyFile.getAbsolutePath();
                    }
                    File sttyFile = new File(p, STTY_NAME);
                    if (stty == null && sttyFile.canExecute()) {
                        stty = sttyFile.getAbsolutePath();
                    }
                }
            }
            if (tty == null) {
                tty = TTY_NAME;
            }
            if (stty == null) {
                stty = STTY_NAME;
            }
            TTY_COMMAND = tty;
            STTY_COMMAND = stty;
            // Compute patterns
            ROWS_PATTERN = Pattern.compile("\\b" + "rows" + "\\s+(\\d+)\\b");
            COLUMNS_PATTERN = Pattern.compile("\\b" + "columns" + "\\s+(\\d+)\\b");
        }

        private static ProcessBuilder getProcessBuilder(String... command) {
            ProcessBuilder pb = new ProcessBuilder(command);
            if (OSInfo.IS_AIX) {
                Map<String, String> env = pb.environment();
                env.put("PATH", "/opt/freeware/bin:" + env.get("PATH"));
                env.put("LANG", "C");
                env.put("LC_ALL", "C");
            }
            return pb;
        }

        public static String getConsoleName() {
            try {
                Process p = getProcessBuilder(TTY_COMMAND)
                        .redirectInput(getRedirect(FileDescriptor.out))
                        .start();
                String result = waitAndCapture(p);
                if (p.exitValue() == 0) {
                    return result.trim();
                }
            } catch (IOException | NoSuchFieldException | ClassNotFoundException | InterruptedException |
                     InvocationTargetException | NoSuchMethodException | InstantiationException ignored) {
            }
            return null;
        }

        public static int getTerminalWidth(String name) {
            try {
                Process p = getProcessBuilder(STTY_COMMAND, "-F", name, "-a").start();
                String result = waitAndCapture(p);
                if (p.exitValue() != 0) {
                    throw new IOException("Error executing '" + STTY_COMMAND + "': " + result);
                }
                Matcher matcher = COLUMNS_PATTERN.matcher(result);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
                throw new IOException("Unable to parse columns");
            } catch (IOException | InterruptedException e) {
                throw new UnexpectedError(e);
            }
        }

        public static int getTerminalHeight(String name) {
            try {
                Process p = getProcessBuilder(STTY_COMMAND, "-F", name, "-a").start();
                String result = waitAndCapture(p);
                if (p.exitValue() != 0) {
                    throw new IOException("Error executing '" + STTY_COMMAND + "': " + result);
                }
                Matcher matcher = ROWS_PATTERN.matcher(result);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
                throw new IOException("Unable to parse rows");
            } catch (IOException | InterruptedException e) {
                throw new UnexpectedError(e);
            }
        }

        public static void getTerminalSize(String name, Dimension size) {
            try {
                Process p = getProcessBuilder(STTY_COMMAND, "-F", name, "-a").start();
                String result = waitAndCapture(p);
                if (p.exitValue() != 0) {
                    throw new IOException("Error executing '" + STTY_COMMAND + "': " + result);
                }
                Matcher rows = ROWS_PATTERN.matcher(result);
                Matcher columns = COLUMNS_PATTERN.matcher(result);
                if (rows.find() && columns.find()) {
                    size.setDimension(Integer.parseInt(columns.group(1)), Integer.parseInt(rows.group(1)));
                }
                throw new IOException("Unable to parse size");
            } catch (IOException | InterruptedException e) {
                throw new UnexpectedError(e);
            }
        }

        private static String waitAndCapture(Process p) throws IOException, InterruptedException {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (InputStream in = p.getInputStream();
                 InputStream err = p.getErrorStream()) {
                int c;
                while ((c = in.read()) != -1) {
                    bout.write(c);
                }
                while ((c = err.read()) != -1) {
                    bout.write(c);
                }
                p.waitFor();
            }
            return bout.toString();
        }

        private static ProcessBuilder.Redirect getRedirect(FileDescriptor fd) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchFieldException {
            // This is not really allowed, but this is the only way to redirect the output or error stream
            // to the input.  This is definitely not something you'd usually want to do, but in the case of
            // the `tty` utility, it provides a way to get
            Class<?> rpi = Class.forName("java.lang.ProcessBuilder$RedirectPipeImpl");
            Constructor<?> cns = rpi.getDeclaredConstructor();
            ProcessBuilder.Redirect input = (ProcessBuilder.Redirect) ReflectionSupport.newInstance(cns);
            Field f = rpi.getDeclaredField("fd");
            ReflectionSupport.setObjectField(input, f, fd);
            return input;
        }

    }

}
