/*
 * Copyright (c) 2002-2016, the original author(s).
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * https://opensource.org/licenses/BSD-3-Clause
 */
package unrefined.desktop.windows;

import unrefined.desktop.AnsiWriter;
import unrefined.desktop.OSInfo;
import unrefined.io.console.Ansi;
import unrefined.nio.Pointer;
import unrefined.util.UnexpectedError;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;

import static unrefined.desktop.UnsafeSupport.UNSAFE;

import static unrefined.desktop.windows.WindowsConsoleSupport.BACKGROUND_BLUE;
import static unrefined.desktop.windows.WindowsConsoleSupport.BACKGROUND_GREEN;
import static unrefined.desktop.windows.WindowsConsoleSupport.BACKGROUND_INTENSITY;
import static unrefined.desktop.windows.WindowsConsoleSupport.BACKGROUND_RED;
import static unrefined.desktop.windows.WindowsConsoleSupport.FOREGROUND_BLUE;
import static unrefined.desktop.windows.WindowsConsoleSupport.FOREGROUND_GREEN;
import static unrefined.desktop.windows.WindowsConsoleSupport.FOREGROUND_INTENSITY;
import static unrefined.desktop.windows.WindowsConsoleSupport.FOREGROUND_RED;

/**
 * A Windows ANSI escape processor, uses Unrefined FFI to access native platform
 * API's to change the console attributes.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author Joris Kuipers
 * @author Karstian Lee
 */
public final class WindowsAnsiWriter extends AnsiWriter {

    private static final short FOREGROUND_BLACK = 0;
    private static final short FOREGROUND_YELLOW = (short) (FOREGROUND_RED | FOREGROUND_GREEN);
    private static final short FOREGROUND_MAGENTA = (short) (FOREGROUND_BLUE | FOREGROUND_RED);
    private static final short FOREGROUND_CYAN = (short) (FOREGROUND_BLUE | FOREGROUND_GREEN);
    private static final short FOREGROUND_WHITE = (short) (FOREGROUND_RED | FOREGROUND_GREEN | FOREGROUND_BLUE);

    private static final short BACKGROUND_BLACK = 0;
    private static final short BACKGROUND_YELLOW = (short) (BACKGROUND_RED | BACKGROUND_GREEN);
    private static final short BACKGROUND_MAGENTA = (short) (BACKGROUND_BLUE | BACKGROUND_RED);
    private static final short BACKGROUND_CYAN = (short) (BACKGROUND_BLUE | BACKGROUND_GREEN);
    private static final short BACKGROUND_WHITE = (short) (BACKGROUND_RED | BACKGROUND_GREEN | BACKGROUND_BLUE);

    private static final short[] ANSI_FOREGROUND_COLOR_MAP = {
        FOREGROUND_BLACK,
        FOREGROUND_RED,
        FOREGROUND_GREEN,
        FOREGROUND_YELLOW,
        FOREGROUND_BLUE,
        FOREGROUND_MAGENTA,
        FOREGROUND_CYAN,
        FOREGROUND_WHITE,
    };

    private static final short[] ANSI_BACKGROUND_COLOR_MAP = {
        BACKGROUND_BLACK,
        BACKGROUND_RED,
        BACKGROUND_GREEN,
        BACKGROUND_YELLOW,
        BACKGROUND_BLUE,
        BACKGROUND_MAGENTA,
        BACKGROUND_CYAN,
        BACKGROUND_WHITE,
    };

    private final long console;

    private final long info = UNSAFE.allocateMemory(22);
    private final short originalColors;

    private boolean negative;
    private boolean bold;
    private boolean underline;
    private short savedX = -1;
    private short savedY = -1;

    public WindowsAnsiWriter(Writer out, long console) throws IOException {
        super(out);
        this.console = console;
        getConsoleInfo();
        originalColors = UNSAFE.getShort(info + 8);
    }

    private void getConsoleInfo() throws IOException {
        out.flush();
        WindowsConsole.GetConsoleScreenBufferInfo(console, info);
        if (negative) UNSAFE.putShort(info + 8, invertAttributeColors(UNSAFE.getShort(info + 8)));
    }

    private void applyAttribute() throws IOException {
        out.flush();
        short attributes = UNSAFE.getShort(info + 8);
        // bold is simulated by high foreground intensity
        if (bold) {
            attributes |= FOREGROUND_INTENSITY;
        }
        // underline is simulated by high foreground intensity
        if (underline) {
            attributes |= BACKGROUND_INTENSITY;
        }
        if (negative) {
            attributes = invertAttributeColors(attributes);
        }
        WindowsConsole.SetConsoleTextAttribute(console, attributes);
    }

    private short invertAttributeColors(short attributes) {
        // Swap the the Foreground and Background bits.
        int fg = 0x000F & attributes;
        fg <<= 4;
        int bg = 0X00F0 & attributes;
        bg >>= 4;
        attributes = (short) ((attributes & 0xFF00) | fg | bg);
        return attributes;
    }

    private void applyCursorPosition() {
        UNSAFE.putShort(info + 4, (short) Math.max(0, Math.min(Short.toUnsignedInt(UNSAFE.getShort(info)) - 1, Short.toUnsignedInt(UNSAFE.getShort(info + 4)))));
        UNSAFE.putShort(info + 6, (short) Math.max(0, Math.min(Short.toUnsignedInt(UNSAFE.getShort(info + 2)) - 1, Short.toUnsignedInt(UNSAFE.getShort(info + 6)))));
        WindowsConsole.SetConsoleCursorPosition(console, UNSAFE.getInt(info + 4));
    }

    protected void processEraseScreen(int eraseOption) throws IOException {
        getConsoleInfo();
        long written = UNSAFE.allocateMemory(4);
        try {
            switch (eraseOption) {
                case ERASE_SCREEN:
                    ByteBuffer topLeft = ByteBuffer.allocate(4);
                    topLeft.putShort(0, (short) 0);
                    topLeft.putShort(2, UNSAFE.getShort(info + 12));
                    int screenLength = (Short.toUnsignedInt(UNSAFE.getShort(info + 16)) - Short.toUnsignedInt(UNSAFE.getShort(info + 12))) * Short.toUnsignedInt(UNSAFE.getShort(info));
                    WindowsConsole.FillConsoleOutputCharacterW(console, ' ', screenLength, topLeft.getInt(0), written);
                    WindowsConsole.FillConsoleOutputAttribute(console, UNSAFE.getShort(info + 8), screenLength, topLeft.getInt(0), written);
                    break;
                case ERASE_SCREEN_TO_BEGINING:
                    ByteBuffer topLeft2 = ByteBuffer.allocate(4);
                    topLeft2.putShort(0, (short) 0);
                    topLeft2.putShort(2, UNSAFE.getShort(info + 12));
                    int lengthToCursor =
                            (Short.toUnsignedInt(UNSAFE.getShort(info + 6)) - Short.toUnsignedInt(UNSAFE.getShort(info + 12))) * Short.toUnsignedInt(UNSAFE.getShort(info)) + Short.toUnsignedInt(UNSAFE.getShort(info + 4));
                    WindowsConsole.FillConsoleOutputCharacterW(console, ' ', lengthToCursor, topLeft2.getInt(0), written);
                    WindowsConsole.FillConsoleOutputAttribute(
                            console, UNSAFE.getShort(info + 8), lengthToCursor, topLeft2.getInt(0), written);
                    break;
                case ERASE_SCREEN_TO_END:
                    int lengthToEnd = (Short.toUnsignedInt(UNSAFE.getShort(info + 16)) - Short.toUnsignedInt(UNSAFE.getShort(info + 6))) * Short.toUnsignedInt(UNSAFE.getShort(info))
                            + (Short.toUnsignedInt(UNSAFE.getShort(info)) - Short.toUnsignedInt(UNSAFE.getShort(info + 4)));
                    WindowsConsole.FillConsoleOutputCharacterW(console, ' ', lengthToEnd, UNSAFE.getInt(info + 4), written);
                    WindowsConsole.FillConsoleOutputAttribute(
                            console, UNSAFE.getShort(info + 8), lengthToEnd, UNSAFE.getInt(info + 4), written);
            }
        }
        finally {
            UNSAFE.freeMemory(written);
        }
    }

    protected void processEraseLine(int eraseOption) throws IOException {
        getConsoleInfo();
        long written = UNSAFE.allocateMemory(4);
        try {
            switch (eraseOption) {
                case ERASE_LINE:
                    ByteBuffer leftColCurrRow = ByteBuffer.allocate(4);
                    leftColCurrRow.putShort(0, (short) 0);
                    leftColCurrRow.putShort(2, UNSAFE.getShort(info + 6));
                    WindowsConsole.FillConsoleOutputCharacterW(console, ' ', UNSAFE.getShort(info), leftColCurrRow.getInt(0), written);
                    WindowsConsole.FillConsoleOutputAttribute(
                            console, UNSAFE.getShort(info + 8), UNSAFE.getShort(info), leftColCurrRow.getInt(0), written);
                    break;
                case ERASE_LINE_TO_BEGINING:
                    ByteBuffer leftColCurrRow2 = ByteBuffer.allocate(4);
                    leftColCurrRow2.putShort(0, (short) 0);
                    leftColCurrRow2.putShort(2, UNSAFE.getShort(info + 6));
                    WindowsConsole.FillConsoleOutputCharacterW(
                            console, ' ', UNSAFE.getShort(info + 4), leftColCurrRow2.getInt(0), written);
                    WindowsConsole.FillConsoleOutputAttribute(
                            console, UNSAFE.getShort(info + 8), UNSAFE.getShort(info + 4), leftColCurrRow2.getInt(0), written);
                    break;
                case ERASE_LINE_TO_END:
                    int lengthToLastCol = Short.toUnsignedInt(UNSAFE.getShort(info)) - Short.toUnsignedInt(UNSAFE.getShort(info + 4));
                    WindowsConsole.FillConsoleOutputCharacterW(
                            console, ' ', lengthToLastCol, UNSAFE.getInt(info + 4), written);
                    WindowsConsole.FillConsoleOutputAttribute(
                            console, UNSAFE.getShort(info + 8), lengthToLastCol, UNSAFE.getInt(info + 4), written);
            }
        }
        finally {
            UNSAFE.freeMemory(written);
        }
    }

    protected void processCursorUpLine(int count) throws IOException {
        getConsoleInfo();
        UNSAFE.putShort(info + 4, (short) 0);
        UNSAFE.putShort(info + 6, (short) (UNSAFE.getShort(info + 6) - count));
        applyCursorPosition();
    }

    protected void processCursorDownLine(int count) throws IOException {
        getConsoleInfo();
        UNSAFE.putShort(info + 4, (short) 0);
        UNSAFE.putShort(info + 6, (short) (UNSAFE.getShort(info + 6) + count));
        applyCursorPosition();
    }

    protected void processCursorLeft(int count) throws IOException {
        getConsoleInfo();
        UNSAFE.putShort(info + 4, (short) (UNSAFE.getShort(info + 4) - count));
        applyCursorPosition();
    }

    protected void processCursorRight(int count) throws IOException {
        getConsoleInfo();
        UNSAFE.putShort(info + 4, (short) (UNSAFE.getShort(info + 4) + count));
        applyCursorPosition();
    }

    protected void processCursorDown(int count) throws IOException {
        getConsoleInfo();
        int nb = Math.max(0, Short.toUnsignedInt(UNSAFE.getShort(info + 6)) + count - Short.toUnsignedInt(UNSAFE.getShort(info + 2)) + 1);
        if (nb != count) {
            UNSAFE.putShort(info + 6, (short) (Short.toUnsignedInt(UNSAFE.getShort(info + 6)) + count));
            applyCursorPosition();
        }
        if (nb > 0) {
            long scroll = UNSAFE.allocateMemory(8);
            long info = UNSAFE.allocateMemory(4);
            try {
                UNSAFE.copyMemory(this.info + 10, scroll, 8);
                UNSAFE.putShort(scroll + 2, (short) 0);
                ByteBuffer org = ByteBuffer.allocate(4);
                org.putShort(0, (short) 0);
                org.putShort(2, (short) -nb);
                UNSAFE.putChar(info, ' ');
                UNSAFE.putShort(info + 2, originalColors);
                WindowsConsole.ScrollConsoleScreenBufferW(console, scroll, scroll,
                        org.getInt(0), info);
            }
            finally {
                UNSAFE.freeMemory(scroll);
                UNSAFE.freeMemory(info);
            }
        }
    }

    protected void processCursorUp(int count) throws IOException {
        getConsoleInfo();
        UNSAFE.putShort(info + 6, (short) (Short.toUnsignedInt(UNSAFE.getShort(info + 6)) - count));
        applyCursorPosition();
    }

    protected void processCursorTo(int row, int col) throws IOException {
        getConsoleInfo();
        UNSAFE.putShort(info + 6, (short) (Short.toUnsignedInt(UNSAFE.getShort(info + 12)) + row - 1));
        UNSAFE.putShort(info + 4, (short) (col - 1));
        applyCursorPosition();
    }

    protected void processCursorToColumn(int x) throws IOException {
        getConsoleInfo();
        UNSAFE.putShort(info + 4, (short) (x - 1));
        applyCursorPosition();
    }

    @Override
    protected void processSetForegroundColorExt(int paletteIndex) throws IOException {
        int color = Ansi.Color256.pick(paletteIndex, 16);
        UNSAFE.putShort(info + 8, (short) ((UNSAFE.getShort(info + 8) & ~0x0007) | ANSI_FOREGROUND_COLOR_MAP[color & 0x07]));
        UNSAFE.putShort(info + 8, (short) ((UNSAFE.getShort(info + 8) & ~FOREGROUND_INTENSITY) | (color >= 8 ? FOREGROUND_INTENSITY : 0)));
        applyAttribute();
    }

    protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {
        int color = Ansi.Color256.pick(paletteIndex, 16);
        UNSAFE.putShort(info + 8, (short) ((UNSAFE.getShort(info + 8) & ~0x0070) | ANSI_BACKGROUND_COLOR_MAP[color & 0x07]));
        UNSAFE.putShort(info + 8, (short) ((UNSAFE.getShort(info + 8) & ~BACKGROUND_INTENSITY) | (color >= 8 ? BACKGROUND_INTENSITY : 0)));
        applyAttribute();
    }

    protected void processDefaultTextColor() throws IOException {
        UNSAFE.putShort(info + 8, (short) ((UNSAFE.getShort(info + 8) & ~0x000F) | (originalColors & 0x000F)));
        applyAttribute();
    }

    protected void processDefaultBackgroundColor() throws IOException {
        UNSAFE.putShort(info + 8, (short) ((UNSAFE.getShort(info + 8) & ~0x00F0) | (originalColors & 0x00F0)));
        applyAttribute();
    }

    protected void processAttributeRest() throws IOException {
        UNSAFE.putShort(info + 8, (short) ((UNSAFE.getShort(info + 8) & ~0x00FF) | originalColors));
        this.negative = false;
        this.bold = false;
        this.underline = false;
        applyAttribute();
    }

    protected void processSetAttribute(int attribute) throws IOException {
        switch (attribute) {
            case ATTRIBUTE_INTENSITY_BOLD:
                bold = true;
                applyAttribute();
                break;
            case ATTRIBUTE_INTENSITY_NORMAL:
                bold = false;
                applyAttribute();
                break;

            case ATTRIBUTE_UNDERLINE:
                underline = true;
                applyAttribute();
                break;
            case ATTRIBUTE_UNDERLINE_OFF:
                underline = false;
                applyAttribute();
                break;

            case ATTRIBUTE_NEGATIVE_ON:
                negative = true;
                applyAttribute();
                break;
            case ATTRIBUTE_NEGATIVE_OFF:
                negative = false;
                applyAttribute();
                break;
        }
    }

    protected void processSaveCursorPosition() throws IOException {
        getConsoleInfo();
        savedX = UNSAFE.getShort(info + 4);
        savedY = UNSAFE.getShort(info + 6);
    }

    protected void processRestoreCursorPosition() throws IOException {
        // restore only if there was a save operation first
        if (savedX != -1 && savedY != -1) {
            out.flush();
            UNSAFE.putShort(info + 4, savedX);
            UNSAFE.putShort(info + 6, savedY);
            applyCursorPosition();
        }
    }

    @Override
    protected void processInsertLine(int optionInt) throws IOException {
        getConsoleInfo();
        long scroll = UNSAFE.allocateMemory(8);
        long info = UNSAFE.allocateMemory(4);
        try {
            UNSAFE.copyMemory(this.info + 10, scroll, 8);
            UNSAFE.putShort(scroll + 2, UNSAFE.getShort(this.info + 6));
            ByteBuffer org = ByteBuffer.allocate(4);
            org.putShort(0, (short) 0);
            org.putShort(2, (short) (Short.toUnsignedInt(UNSAFE.getShort(this.info + 6)) + optionInt));
            UNSAFE.putChar(info, ' ');
            UNSAFE.putShort(info + 2, originalColors);
            WindowsConsole.ScrollConsoleScreenBufferW(console, scroll, scroll,
                    org.getInt(0), info);
        }
        finally {
            UNSAFE.freeMemory(scroll);
            UNSAFE.freeMemory(info);
        }
    }

    @Override
    protected void processDeleteLine(int optionInt) throws IOException {
        getConsoleInfo();
        long scroll = UNSAFE.allocateMemory(8);
        long info = UNSAFE.allocateMemory(4);
        try {
            UNSAFE.copyMemory(this.info + 10, scroll, 8);
            UNSAFE.putShort(scroll + 2, UNSAFE.getShort(this.info + 6));
            ByteBuffer org = ByteBuffer.allocate(4);
            org.putShort(0, (short) 0);
            org.putShort(2, (short) (Short.toUnsignedInt(UNSAFE.getShort(this.info + 6)) - optionInt));
            UNSAFE.putChar(info, ' ');
            UNSAFE.putShort(info + 2, originalColors);
            WindowsConsole.ScrollConsoleScreenBufferW(console, scroll, scroll,
                    org.getInt(0), info);
        }
        finally {
            UNSAFE.freeMemory(scroll);
            UNSAFE.freeMemory(info);
        }
    }

    protected void processChangeWindowTitle(String label) {
        try (Pointer pointer = Pointer.allocateDirect(label, OSInfo.WIDE_CHARSET)) {
            WindowsConsole.SetConsoleTitleW(pointer.address());
        } catch (IOException e) {
            throw new UnexpectedError(e);
        }
    }

}
