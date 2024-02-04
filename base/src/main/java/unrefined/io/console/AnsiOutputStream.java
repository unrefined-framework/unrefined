/*
 * Copyright (c) 2009-2023, the original author(s).
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * https://opensource.org/licenses/BSD-3-Clause
 */
package unrefined.io.console;

import unrefined.io.IOFactory;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static unrefined.io.console.Ansi.Colors.roundColor;
import static unrefined.io.console.Ansi.Colors.roundRgbColor;

/**
 * An ANSI print stream extracts ANSI escape codes written to an output stream.
 *
 * <p>For more information about ANSI escape codes, see
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia article</a>
 */
public class AnsiOutputStream extends FilterOutputStream {

    private static final int LOOKING_FOR_FIRST_ESC_CHAR = 0;
    private static final int LOOKING_FOR_SECOND_ESC_CHAR = 1;
    private static final int LOOKING_FOR_NEXT_ARG = 2;
    private static final int LOOKING_FOR_STR_ARG_END = 3;
    private static final int LOOKING_FOR_INT_ARG_END = 4;
    private static final int LOOKING_FOR_OSC_COMMAND = 5;
    private static final int LOOKING_FOR_OSC_COMMAND_END = 6;
    private static final int LOOKING_FOR_OSC_PARAM = 7;
    private static final int LOOKING_FOR_ST = 8;
    private static final int LOOKING_FOR_CHARSET = 9;

    private static final int FIRST_ESC_CHAR = 27;
    private static final int SECOND_ESC_CHAR = '[';
    private static final int SECOND_OSC_CHAR = ']';
    private static final int BEL = 7;
    private static final int SECOND_ST_CHAR = '\\';
    private static final int SECOND_CHARSET0_CHAR = '(';
    private static final int SECOND_CHARSET1_CHAR = ')';

    private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
    private final byte[] buffer = new byte[MAX_ESCAPE_SEQUENCE_LENGTH];
    private int pos = 0;
    private int startOfValue;
    private final List<Object> options = new ArrayList<>();
    private int state = LOOKING_FOR_FIRST_ESC_CHAR;
    private final Charset charset;

    private final int type;
    private final int colors;
    private int mode;

    private final Object writeLock = new Object();

    private static int checkColors(int colors) {
        if (colors == 16 || colors == 256 || colors == 16777216) return colors;
        else throw new IllegalArgumentException("Illegal colors; expected one of 16, 256, 16777216");
    }

    public AnsiOutputStream(OutputStream os, int mode, int type, int colors, Charset charset) {
        super(os);
        Ansi.Type.checkValid(type);
        if (!IOFactory.isStandardStream(os)) {
            if (mode == Ansi.Mode.AUTO) mode = Ansi.Mode.STRIP;
            type = Ansi.Type.REDIRECTED;
        }
        this.type = type;
        this.colors = checkColors(colors);
        this.charset = charset == null ? charset : Charset.defaultCharset();
        setMode(mode);
    }

    public int getType() {
        return type;
    }

    public int getMaximumColors() {
        return colors;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        synchronized (writeLock) {
            this.mode = Ansi.Mode.checkValid(mode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        synchronized (writeLock) {
            write0(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        synchronized (writeLock) {
            for (int i = 0 ; i < len ; i++) {
                write0(b[off + i]);
            }
        }
    }

    private void write0(int b) throws IOException {
        switch (state) {
            case LOOKING_FOR_FIRST_ESC_CHAR:
                if (b == FIRST_ESC_CHAR) {
                    buffer[pos++] = (byte) b;
                    state = LOOKING_FOR_SECOND_ESC_CHAR;
                } else {
                    out.write(b);
                }
                break;

            case LOOKING_FOR_SECOND_ESC_CHAR:
                buffer[pos++] = (byte) b;
                if (b == SECOND_ESC_CHAR) {
                    state = LOOKING_FOR_NEXT_ARG;
                } else if (b == SECOND_OSC_CHAR) {
                    state = LOOKING_FOR_OSC_COMMAND;
                } else if (b == SECOND_CHARSET0_CHAR) {
                    options.add(0);
                    state = LOOKING_FOR_CHARSET;
                } else if (b == SECOND_CHARSET1_CHAR) {
                    options.add(1);
                    state = LOOKING_FOR_CHARSET;
                } else {
                    reset(false);
                }
                break;

            case LOOKING_FOR_NEXT_ARG:
                buffer[pos++] = (byte) b;
                if ('"' == b) {
                    startOfValue = pos - 1;
                    state = LOOKING_FOR_STR_ARG_END;
                } else if ('0' <= b && b <= '9') {
                    startOfValue = pos - 1;
                    state = LOOKING_FOR_INT_ARG_END;
                } else if (';' == b) {
                    options.add(null);
                } else if ('?' == b) {
                    options.add('?');
                } else if ('=' == b) {
                    options.add('=');
                } else {
                    processEscapeCommand(b);
                }
                break;
            default:
                break;

            case LOOKING_FOR_INT_ARG_END:
                buffer[pos++] = (byte) b;
                if (!('0' <= b && b <= '9')) {
                    String strValue = new String(buffer, startOfValue, (pos - 1) - startOfValue);
                    Integer value = Integer.valueOf(strValue);
                    options.add(value);
                    if (b == ';') {
                        state = LOOKING_FOR_NEXT_ARG;
                    } else {
                        processEscapeCommand(b);
                    }
                }
                break;

            case LOOKING_FOR_STR_ARG_END:
                buffer[pos++] = (byte) b;
                if ('"' != b) {
                    String value = new String(buffer, startOfValue, (pos - 1) - startOfValue, charset);
                    options.add(value);
                    if (b == ';') {
                        state = LOOKING_FOR_NEXT_ARG;
                    } else {
                        processEscapeCommand(b);
                    }
                }
                break;

            case LOOKING_FOR_OSC_COMMAND:
                buffer[pos++] = (byte) b;
                if ('0' <= b && b <= '9') {
                    startOfValue = pos - 1;
                    state = LOOKING_FOR_OSC_COMMAND_END;
                } else {
                    reset(false);
                }
                break;

            case LOOKING_FOR_OSC_COMMAND_END:
                buffer[pos++] = (byte) b;
                if (';' == b) {
                    String strValue = new String(buffer, startOfValue, (pos - 1) - startOfValue);
                    Integer value = Integer.valueOf(strValue);
                    options.add(value);
                    startOfValue = pos;
                    state = LOOKING_FOR_OSC_PARAM;
                } else if ('0' <= b && b <= '9') {
                    // already pushed digit to buffer, just keep looking
                } else {
                    // oops, did not expect this
                    reset(false);
                }
                break;

            case LOOKING_FOR_OSC_PARAM:
                buffer[pos++] = (byte) b;
                if (BEL == b) {
                    String value = new String(buffer, startOfValue, (pos - 1) - startOfValue, charset);
                    options.add(value);
                    reset(false);
                } else if (FIRST_ESC_CHAR == b) {
                    state = LOOKING_FOR_ST;
                } else {
                    // just keep looking while adding text
                }
                break;

            case LOOKING_FOR_ST:
                buffer[pos++] = (byte) b;
                if (SECOND_ST_CHAR == b) {
                    String value = new String(buffer, startOfValue, (pos - 2) - startOfValue, charset);
                    options.add(value);
                    reset(false);
                } else {
                    state = LOOKING_FOR_OSC_PARAM;
                }
                break;

            case LOOKING_FOR_CHARSET:
                options.add((char) b);
                reset(false);
                break;
        }

        // Is it just too long?
        if (pos >= buffer.length) {
            reset(false);
        }
    }

    /**
     * Resets all states to continue with regular parsing
     * @param skip if current buffer should be skipped or written to out
     */
    private void reset(boolean skip) throws IOException {
        if (!skip) out.write(buffer, 0, pos);
        pos = 0;
        startOfValue = 0;
        options.clear();
        state = LOOKING_FOR_FIRST_ESC_CHAR;
    }

    /**
     * Helper for processEscapeCommand() to iterate over integer options
     * @param  optionsIterator  the underlying iterator
     */
    private static int getNextOptionInt(Iterator<Object> optionsIterator) {
        while (true) {
            if (!optionsIterator.hasNext()) throw new IllegalArgumentException();
            Object arg = optionsIterator.next();
            if (arg != null) return (Integer) arg;
        }
    }

    private void processEscapeCommand(int command) throws IOException {
        try {
            reset(processEscapeCommand0(command));
        } catch (RuntimeException e) {
            reset(true);
            throw e;
        }
    }

    private boolean stripEscapeCommand(int command) {
        try {
            switch (command) {
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'f':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'S':
                case 'T':
                case 's':
                case 'u':
                    return true;
                case 'm':
                    // Validate all options are ints...
                    for (Object next : options) {
                        if (next != null && next.getClass() != Integer.class) {
                            throw new IllegalArgumentException();
                        }
                    }

                    Iterator<Object> optionsIterator = options.iterator();
                    while (optionsIterator.hasNext()) {
                        Object next = optionsIterator.next();
                        if (next != null) {
                            int value = (Integer) next;
                            if (value == 38 || value == 48) {
                                if (!optionsIterator.hasNext()) {
                                    continue;
                                }
                                // extended color like `esc[38;5;<index>m` or `esc[38;2;<r>;<g>;<b>m`
                                int arg2or5 = getNextOptionInt(optionsIterator);
                                if (arg2or5 == 2) {
                                    // 24 bit color style like `esc[38;2;<r>;<g>;<b>m`
                                    int r = getNextOptionInt(optionsIterator);
                                    int g = getNextOptionInt(optionsIterator);
                                    int b = getNextOptionInt(optionsIterator);
                                    if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                                        throw new IllegalArgumentException();
                                    }
                                } else if (arg2or5 == 5) {
                                    // 256 color style like `esc[38;5;<index>m`
                                    int paletteIndex = getNextOptionInt(optionsIterator);
                                    if (paletteIndex < 0 || paletteIndex > 255) {
                                        throw new IllegalArgumentException();
                                    }
                                } else {
                                    throw new IllegalArgumentException();
                                }
                            }
                        }
                    }
                    return true;
                default:
                    if ('a' <= command && command <= 'z') {
                        return true;
                    }
                    else if ('A' <= command && command <= 'Z') {
                        return true;
                    }
                    else {
                        return false;
                    }
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean enableEscapeCommand(int command) throws IOException {
        if (command == 'm' && (colors == 256 || colors == 16)) {
            // Validate all options are ints...
            boolean has38or48 = false;
            for (Object next : options) {
                if (next != null && next.getClass() != Integer.class) {
                    throw new IllegalArgumentException();
                }
                Integer value = (Integer) next;
                has38or48 |= value == 38 || value == 48;
            }
            // SGR commands do not contain an extended color, so just transfer the buffer
            if (!has38or48) {
                return false;
            }
            StringBuilder builder = new StringBuilder(32);
            builder.append('\033').append('[');
            boolean first = true;
            Iterator<Object> optionsIterator = options.iterator();
            while (optionsIterator.hasNext()) {
                Object next = optionsIterator.next();
                if (next != null) {
                    int value = (Integer) next;
                    if (value == 38 || value == 48) {
                        // extended color like `esc[38;5;<index>m` or `esc[38;2;<r>;<g>;<b>m`
                        int arg2or5 = getNextOptionInt(optionsIterator);
                        if (arg2or5 == 2) {
                            // 24 bit color style like `esc[38;2;<r>;<g>;<b>m`
                            int r = getNextOptionInt(optionsIterator);
                            int g = getNextOptionInt(optionsIterator);
                            int b = getNextOptionInt(optionsIterator);
                            if (colors == 256) {
                                int col = roundRgbColor(r, g, b, 256);
                                if (!first) {
                                    builder.append(';');
                                }
                                first = false;
                                builder.append(value);
                                builder.append(';');
                                builder.append(5);
                                builder.append(';');
                                builder.append(col);
                            } else {
                                int col = roundRgbColor(r, g, b, 16);
                                if (!first) {
                                    builder.append(';');
                                }
                                first = false;
                                builder.append(
                                        value == 38
                                                ? col >= 8 ? 90 + col - 8 : 30 + col
                                                : col >= 8 ? 100 + col - 8 : 40 + col);
                            }
                        } else if (arg2or5 == 5) {
                            // 256 color style like `esc[38;5;<index>m`
                            int paletteIndex = getNextOptionInt(optionsIterator);
                            if (colors == 256) {
                                if (!first) {
                                    builder.append(';');
                                }
                                first = false;
                                builder.append(value);
                                builder.append(';');
                                builder.append(5);
                                builder.append(';');
                                builder.append(paletteIndex);
                            } else {
                                int col = roundColor(paletteIndex, 16);
                                if (!first) {
                                    builder.append(';');
                                }
                                first = false;
                                builder.append(
                                        value == 38
                                                ? col >= 8 ? 90 + col - 8 : 30 + col
                                                : col >= 8 ? 100 + col - 8 : 40 + col);
                            }
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } else {
                        if (!first) {
                            builder.append(';');
                        }
                        first = false;
                        builder.append(value);
                    }
                }
            }
            builder.append('m');
            out.write(builder.toString().getBytes(charset));
            return true;

        } else {
            return false;
        }
    }

    private boolean processEscapeCommand0(int command) throws IOException {
        if (mode == Ansi.Mode.STRIP || (mode == Ansi.Mode.AUTO && (type == Ansi.Type.UNSUPPORTED || type == Ansi.Type.REDIRECTED))) {
            return stripEscapeCommand(command);
        }
        else return enableEscapeCommand(command);
    }

}
