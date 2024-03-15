/*
 * Copyright (c) 2009-2023, the original author(s).
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * https://opensource.org/licenses/BSD-3-Clause
 */
package unrefined.io.console;

import unrefined.util.NotInstantiableError;
import unrefined.util.function.Slot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Provides a fluent API for generating
 * <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#CSI_sequences">ANSI escape sequences</a>.
 */
public class Ansi implements Appendable {

    public static final String BEGIN_TOKEN = "@|";
    public static final String END_TOKEN = "|@";
    public static final String CODE_TEXT_SEPARATOR = " ";
    public static final String CODE_LIST_SEPARATOR = ",";

    public static class Mode {
        private Mode() {
            throw new NotInstantiableError(Mode.class);
        }
        public static final int AUTO  = 0;
        public static final int STRIP = 1;
        public static final int FORCE = 2;
        public static boolean isValid(int mode) {
            return mode >= AUTO && mode <= FORCE;
        }
        public static int checkValid(int mode) {
            if (mode < AUTO || mode > FORCE) throw new IllegalArgumentException("Illegal ansi print mode: " + mode);
            return mode;
        }
        public static String toString(int mode) {
            switch (mode) {
                case AUTO: return "AUTO";
                case STRIP:   return "STRIP";
                case FORCE:   return "FORCE";
                default: throw new IllegalArgumentException("Illegal ansi print mode: " + mode);
            }
        }
    }

    public static class Type {
        private Type() {
            throw new NotInstantiableError(Mode.class);
        }
        public static final int UNSUPPORTED = 0;
        public static final int NATIVE      = 1;
        public static final int EMULATION   = 2;
        public static final int REDIRECTED  = 3;
        public static boolean isValid(int type) {
            return type >= UNSUPPORTED && type <= REDIRECTED;
        }
        public static int checkValid(int type) {
            if (type < UNSUPPORTED || type > REDIRECTED) throw new IllegalArgumentException("Illegal ansi supplier type: " + type);
            return type;
        }
        public static String toString(int type) {
            switch (type) {
                case UNSUPPORTED: return "UNSUPPORTED";
                case NATIVE:      return "NATIVE";
                case EMULATION:   return "EMULATION";
                case REDIRECTED:  return "REDIRECTED";
                default: throw new IllegalArgumentException("Illegal ansi supplier type: " + type);
            }
        }
    }

    /**
     * <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#Colors">ANSI 8 colors</a> for fluent API
     */
    public static class Index {
        private Index() {
            throw new NotInstantiableError(Index.class);
        }
        public static final int BLACK   = 0;
        public static final int RED     = 1;
        public static final int GREEN   = 2;
        public static final int YELLOW  = 3;
        public static final int BLUE    = 4;
        public static final int MAGENTA = 5;
        public static final int CYAN    = 6;
        public static final int WHITE   = 7;
        public static final int DEFAULT = 9;
        public static boolean isValid(int index) {
            return index >= BLACK && index <= DEFAULT && index != 8;
        }
        public static int checkValid(int index) {
            if (index < BLACK || index > DEFAULT || index == 8) throw new IllegalArgumentException("Illegal ansi color index: " + index);
            return index;
        }
        public static String toString(int index) {
            switch (index) {
                case BLACK:   return "BLACK";
                case RED:     return "RED";
                case GREEN:   return "GREEN";
                case YELLOW:  return "YELLOW";
                case BLUE:    return "BLUE";
                case MAGENTA: return "MAGENTA";
                case CYAN:    return "CYAN";
                case WHITE:   return "WHITE";
                case DEFAULT: return "DEFAULT";
                default: throw new IllegalArgumentException("Illegal ansi color index: " + index);
            }
        }
    }

    private static int fgIndex0(int index) {
        return Index.checkValid(index) + 30;
    }

    private static int bgIndex0(int index) {
        return Index.checkValid(index) + 40;
    }

    private static int fgBrightIndex0(int index) {
        return Index.checkValid(index) + 90;
    }

    private static int bgBrightIndex0(int index) {
        return Index.checkValid(index) + 100;
    }

    /**
     * Display attributes, also know as
     * <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#SGR_(Select_Graphic_Rendition)_parameters">SGR
     * (Select Graphic Rendition) parameters</a>.
     */
    public static final class Attribute {

        private Attribute() {
            throw new NotInstantiableError(Attribute.class);
        }

        public static final int RESET = 0;
        public static final int INTENSITY_BOLD = 1;
        public static final int INTENSITY_FAINT = 2;
        public static final int ITALIC = 3;
        public static final int UNDERLINE = 4;
        public static final int BLINK_SLOW = 5;
        public static final int BLINK_FAST = 6;
        public static final int NEGATIVE_ON = 7;
        public static final int CONCEAL_ON = 8;
        public static final int STRIKETHROUGH_ON = 9;
        public static final int UNDERLINE_DOUBLE = 21;
        public static final int INTENSITY_BOLD_OFF = 22;
        public static final int ITALIC_OFF = 23;
        public static final int UNDERLINE_OFF = 24;
        public static final int BLINK_OFF = 25;
        public static final int NEGATIVE_OFF = 27;
        public static final int CONCEAL_OFF = 28;
        public static final int STRIKETHROUGH_OFF = 29;

        public static boolean isValid(int attribute) {
            return (attribute >= RESET && attribute <= STRIKETHROUGH_ON) ||
                    (attribute >= UNDERLINE_DOUBLE && attribute <= STRIKETHROUGH_OFF && attribute != 26);
        }

        public static int checkValid(int attribute) {
            if (attribute < RESET || attribute > STRIKETHROUGH_OFF || attribute == 26 ||
                    (attribute > STRIKETHROUGH_ON && attribute < UNDERLINE_DOUBLE))
                throw new IllegalArgumentException("Illegal ansi display attribute: " + attribute);
            else return attribute;
        }

        public static String toString(int attribute) {
            switch (attribute) {
                case RESET: return "RESET";
                case INTENSITY_BOLD: return "INTENSITY_BOLD";
                case INTENSITY_FAINT: return "INTENSITY_FAINT";
                case ITALIC: return "ITALIC";
                case UNDERLINE: return "UNDERLINE";
                case BLINK_SLOW: return "BLINK_SLOW";
                case BLINK_FAST: return "BLINK_FAST";
                case NEGATIVE_ON: return "NEGATIVE_ON";
                case CONCEAL_ON: return "CONCEAL_ON";
                case STRIKETHROUGH_ON: return "STRIKETHROUGH_ON";
                case UNDERLINE_DOUBLE: return "UNDERLINE_DOUBLE";
                case INTENSITY_BOLD_OFF: return "INTENSITY_BOLD_OFF";
                case ITALIC_OFF: return "ITALIC_OFF";
                case UNDERLINE_OFF: return "UNDERLINE_OFF";
                case BLINK_OFF: return "BLINK_OFF";
                case NEGATIVE_OFF: return "NEGATIVE_OFF";
                case CONCEAL_OFF: return "CONCEAL_OFF";
                case STRIKETHROUGH_OFF: return "STRIKETHROUGH_OFF";
                default: throw new IllegalArgumentException("Illegal ansi display attribute: " + attribute);
            }
        }

    }

    /**
     * ED (Erase in Display) / EL (Erase in Line) parameter (see
     * <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#CSI_sequences">CSI sequence J and K</a>)
     * @see Ansi#eraseScreen(int)
     * @see Ansi#eraseLine(int)
     */
    public static final class Erase {

        public static final int FORWARD = 0;
        public static final int BACKWARD = 1;
        public static final int ALL = 2;

        public static boolean isValid(int erase) {
            return erase >= FORWARD && erase <= ALL;
        }

        public static int checkValid(int erase) {
            if (erase < FORWARD || erase > ALL) throw new IllegalArgumentException("Illegal ansi erase kind: " + erase);
            else return erase;
        }

        public static String toString(int erase) {
            switch (erase) {
                case FORWARD: return "FORWARD";
                case BACKWARD: return "BACKWARD";
                case ALL: return "ALL";
                default: throw new IllegalArgumentException("Illegal ansi erase kind: " + erase);
            }
        }

    }

    private static final char FIRST_ESC_CHAR = 27;
    private static final char SECOND_ESC_CHAR = '[';

    private final StringBuilder builder;
    private final ArrayList<Integer> attributeOptions = new ArrayList<>(5);

    public Ansi() {
        this(new StringBuilder(80));
    }

    public Ansi(Ansi ansi) {
        this(new StringBuilder(ansi.builder));
        attributeOptions.addAll(ansi.attributeOptions);
    }

    public Ansi(int size) {
        this(new StringBuilder(size));
    }

    public Ansi(StringBuilder builder) {
        this.builder = builder;
    }

    public Ansi fgIndex(int color) {
        attributeOptions.add(fgIndex0(color));
        return this;
    }

    public Ansi fg(int color) {
        attributeOptions.add(38);
        attributeOptions.add(5);
        attributeOptions.add(color & 0xff);
        return this;
    }

    public Ansi fgRgb(int color) {
        return fgRgb(color >> 16, color >> 8, color);
    }

    public Ansi fgRgb(int r, int g, int b) {
        attributeOptions.add(38);
        attributeOptions.add(2);
        attributeOptions.add(r & 0xff);
        attributeOptions.add(g & 0xff);
        attributeOptions.add(b & 0xff);
        return this;
    }

    public Ansi fgBlack() {
        return this.fgIndex(Index.BLACK);
    }

    public Ansi fgBlue() {
        return this.fgIndex(Index.BLUE);
    }

    public Ansi fgCyan() {
        return this.fgIndex(Index.CYAN);
    }

    public Ansi fgWhite() {
        return this.fgIndex(Index.WHITE);
    }

    public Ansi fgDefault() {
        return this.fgIndex(Index.DEFAULT);
    }

    public Ansi fgGreen() {
        return this.fgIndex(Index.GREEN);
    }

    public Ansi fgMagenta() {
        return this.fgIndex(Index.MAGENTA);
    }

    public Ansi fgRed() {
        return this.fgIndex(Index.RED);
    }

    public Ansi fgYellow() {
        return this.fgIndex(Index.YELLOW);
    }

    public Ansi bgIndex(int color) {
        attributeOptions.add(bgIndex0(color));
        return this;
    }

    public Ansi bg(int color) {
        attributeOptions.add(48);
        attributeOptions.add(5);
        attributeOptions.add(color & 0xff);
        return this;
    }

    public Ansi bgRgb(int color) {
        return bgRgb(color >> 16, color >> 8, color);
    }

    public Ansi bgRgb(int r, int g, int b) {
        attributeOptions.add(48);
        attributeOptions.add(2);
        attributeOptions.add(r & 0xff);
        attributeOptions.add(g & 0xff);
        attributeOptions.add(b & 0xff);
        return this;
    }

    public Ansi bgBlack() {
        return this.bgIndex(Index.BLACK);
    }

    public Ansi bgBlue() {
        return this.bgIndex(Index.BLUE);
    }

    public Ansi bgCyan() {
        return this.bgIndex(Index.CYAN);
    }

    public Ansi bgWhite() {
        return this.bgIndex(Index.WHITE);
    }

    public Ansi bgDefault() {
        return this.bgIndex(Index.DEFAULT);
    }

    public Ansi bgGreen() {
        return this.bgIndex(Index.GREEN);
    }

    public Ansi bgMagenta() {
        return this.bgIndex(Index.MAGENTA);
    }

    public Ansi bgRed() {
        return this.bgIndex(Index.RED);
    }

    public Ansi bgYellow() {
        return this.bgIndex(Index.YELLOW);
    }

    public Ansi fgBrightIndex(int color) {
        attributeOptions.add(fgBrightIndex0(color));
        return this;
    }

    public Ansi fgBrightBlack() {
        return this.fgBrightIndex(Index.BLACK);
    }

    public Ansi fgBrightBlue() {
        return this.fgBrightIndex(Index.BLUE);
    }

    public Ansi fgBrightCyan() {
        return this.fgBrightIndex(Index.CYAN);
    }

    public Ansi fgBrightWhite() {
        return this.fgBrightIndex(Index.WHITE);
    }

    public Ansi fgBrightDefault() {
        return this.fgBrightIndex(Index.DEFAULT);
    }

    public Ansi fgBrightGreen() {
        return this.fgBrightIndex(Index.GREEN);
    }

    public Ansi fgBrightMagenta() {
        return this.fgBrightIndex(Index.MAGENTA);
    }

    public Ansi fgBrightRed() {
        return this.fgBrightIndex(Index.RED);
    }

    public Ansi fgBrightYellow() {
        return this.fgBrightIndex(Index.YELLOW);
    }

    public Ansi bgBrightIndex(int color) {
        attributeOptions.add(bgBrightIndex0(color));
        return this;
    }

    public Ansi bgBrightBlack() {
        return this.bgBrightIndex(Index.BLACK);
    }

    public Ansi bgBrightBlue() {
        return this.bgBrightIndex(Index.BLUE);
    }

    public Ansi bgBrightCyan() {
        return this.bgBrightIndex(Index.CYAN);
    }

    public Ansi bgBrightWhite() {
        return this.bgBrightIndex(Index.WHITE);
    }

    public Ansi bgBrightDefault() {
        return this.bgBrightIndex(Index.DEFAULT);
    }

    public Ansi bgBrightGreen() {
        return this.bgBrightIndex(Index.GREEN);
    }

    public Ansi bgBrightMagenta() {
        return this.bgBrightIndex(Index.MAGENTA);
    }

    public Ansi bgBrightRed() {
        return this.bgBrightIndex(Index.RED);
    }

    public Ansi bgBrightYellow() {
        return this.bgBrightIndex(Index.YELLOW);
    }

    public Ansi attribute(int attribute) {
        attributeOptions.add(Attribute.checkValid(attribute));
        return this;
    }

    /**
     * Moves the cursor to row n, column m. The values are 1-based.
     * Any values less than 1 are mapped to 1.
     *
     * @param row    row (1-based) from top
     * @param column column (1 based) from left
     * @return this Ansi instance
     */
    public Ansi cursor(int row, int column) {
        return appendEscapeSequence('H', Math.max(1, row), Math.max(1, column));
    }

    /**
     * Moves the cursor to column n. The parameter n is 1-based.
     * If n is less than 1 it is moved to the first column.
     *
     * @param x the index (1-based) of the column to move to
     * @return this Ansi instance
     */
    public Ansi cursorToColumn(int x) {
        return appendEscapeSequence('G', Math.max(1, x));
    }

    /**
     * Moves the cursor up. If the parameter y is negative it moves the cursor down.
     *
     * @param y the number of lines to move up
     * @return this Ansi instance
     */
    public Ansi cursorUp(int y) {
        return y > 0 ? appendEscapeSequence('A', y) : y < 0 ? cursorDown(-y) : this;
    }

    /**
     * Moves the cursor down. If the parameter y is negative it moves the cursor up.
     *
     * @param y the number of lines to move down
     * @return this Ansi instance
     */
    public Ansi cursorDown(int y) {
        return y > 0 ? appendEscapeSequence('B', y) : y < 0 ? cursorUp(-y) : this;
    }

    /**
     * Moves the cursor right. If the parameter x is negative it moves the cursor left.
     *
     * @param x the number of characters to move right
     * @return this Ansi instance
     */
    public Ansi cursorRight(int x) {
        return x > 0 ? appendEscapeSequence('C', x) : x < 0 ? cursorLeft(-x) : this;
    }

    /**
     * Moves the cursor left. If the parameter x is negative it moves the cursor right.
     *
     * @param x the number of characters to move left
     * @return this Ansi instance
     */
    public Ansi cursorLeft(int x) {
        return x > 0 ? appendEscapeSequence('D', x) : x < 0 ? cursorRight(-x) : this;
    }

    /**
     * Moves the cursor relative to the current position. The cursor is moved right if x is
     * positive, left if negative and down if y is positive and up if negative.
     *
     * @param x the number of characters to move horizontally
     * @param y the number of lines to move vertically
     * @return this Ansi instance
     */
    public Ansi cursorMove(int x, int y) {
        return cursorRight(x).cursorDown(y);
    }

    /**
     * Moves the cursor to the beginning of the line below.
     *
     * @return this Ansi instance
     */
    public Ansi cursorDownLine() {
        return appendEscapeSequence('E');
    }

    /**
     * Moves the cursor to the beginning of the n-th line below. If the parameter n is negative it
     * moves the cursor to the beginning of the n-th line above.
     *
     * @param n the number of lines to move the cursor
     * @return this Ansi instance
     */
    public Ansi cursorDownLine(int n) {
        return n < 0 ? cursorUpLine(-n) : appendEscapeSequence('E', n);
    }

    /**
     * Moves the cursor to the beginning of the line above.
     *
     * @return this Ansi instance
     */
    public Ansi cursorUpLine() {
        return appendEscapeSequence('F');
    }

    /**
     * Moves the cursor to the beginning of the n-th line above. If the parameter n is negative it
     * moves the cursor to the beginning of the n-th line below.
     *
     * @param n the number of lines to move the cursor
     * @return this Ansi instance
     */
    public Ansi cursorUpLine(int n) {
        return n < 0 ? cursorDownLine(-n) : appendEscapeSequence('F', n);
    }

    public Ansi eraseScreen() {
        return appendEscapeSequence('J', Erase.ALL);
    }

    public Ansi eraseScreen(int kind) {
        return appendEscapeSequence('J', Erase.checkValid(kind));
    }

    public Ansi eraseLine() {
        return appendEscapeSequence('K');
    }

    public Ansi eraseLine(int kind) {
        return appendEscapeSequence('K', Erase.checkValid(kind));
    }

    public Ansi scrollUp(int rows) {
        if (rows == Integer.MIN_VALUE) {
            return scrollDown(Integer.MAX_VALUE);
        }
        return rows > 0 ? appendEscapeSequence('S', rows) : rows < 0 ? scrollDown(-rows) : this;
    }

    public Ansi scrollDown(int rows) {
        if (rows == Integer.MIN_VALUE) {
            return scrollUp(Integer.MAX_VALUE);
        }
        return rows > 0 ? appendEscapeSequence('T', rows) : rows < 0 ? scrollUp(-rows) : this;
    }

    public Ansi saveCursorPosition() {
        saveCursorPositionSCO();
        return saveCursorPositionDEC();
    }

    // SCO command
    public Ansi saveCursorPositionSCO() {
        return appendEscapeSequence('s');
    }

    // DEC command
    public Ansi saveCursorPositionDEC() {
        builder.append(FIRST_ESC_CHAR);
        builder.append('7');
        return this;
    }

    public Ansi restoreCursorPosition() {
        restoreCursorPositionSCO();
        return restoreCursorPositionDEC();
    }

    // SCO command
    public Ansi restoreCursorPositionSCO() {
        return appendEscapeSequence('u');
    }

    // DEC command
    public Ansi restoreCursorPositionDEC() {
        builder.append(FIRST_ESC_CHAR);
        builder.append('8');
        return this;
    }

    public Ansi reset() {
        return attribute(Attribute.RESET);
    }

    public Ansi bold() {
        return attribute(Attribute.INTENSITY_BOLD);
    }

    public Ansi boldOff() {
        return attribute(Attribute.INTENSITY_BOLD_OFF);
    }

    public Ansi italic() {
        return attribute(Attribute.ITALIC);
    }

    public Ansi italicOff() {
        return attribute(Attribute.ITALIC_OFF);
    }

    public Ansi underline() {
        return attribute(Attribute.UNDERLINE);
    }

    public Ansi underlineDouble() {
        return attribute(Attribute.UNDERLINE_DOUBLE);
    }

    public Ansi underlineOff() {
        return attribute(Attribute.UNDERLINE_OFF);
    }

    public Ansi strikeThrough() {
        return attribute(Attribute.STRIKETHROUGH_ON);
    }

    public Ansi strikeThroughOff() {
        return attribute(Attribute.STRIKETHROUGH_OFF);
    }

    public Ansi append(String value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi append(boolean value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    @Override
    public Ansi append(char value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi append(char[] value, int offset, int len) {
        flushAttributes();
        builder.append(value, offset, len);
        return this;
    }

    public Ansi append(char[] value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    @Override
    public Ansi append(CharSequence value, int start, int end) {
        flushAttributes();
        builder.append(value, start, end);
        return this;
    }

    @Override
    public Ansi append(CharSequence value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi append(double value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi append(float value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi append(int value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi append(long value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi append(Object value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi append(StringBuffer value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi newLine() {
        flushAttributes();
        builder.append(System.lineSeparator());
        return this;
    }

    public Ansi format(String pattern, Object... args) {
        flushAttributes();
        builder.append(String.format(pattern, args));
        return this;
    }

    /**
     * Applies another function to this Ansi instance.
     *
     * @param proc the function to apply
     * @return this Ansi instance
     */
    public Ansi append(Slot<Ansi> proc) {
        proc.accept(this);
        return this;
    }

    /**
     * Renders ANSI color escape-codes in strings by parsing out some special syntax to pick up the correct fluff to use.
     *
     * The syntax for embedded ANSI codes is:
     *
     * <pre>
     *   &#64;|<em>code</em>(,<em>code</em>)* <em>text</em>|&#64;
     * </pre>
     *
     * Examples:
     *
     * <pre>
     *   &#64;|bold Hello|&#64;
     * </pre>
     *
     * <pre>
     *   &#64;|bold,red Warning!|&#64;
     * </pre>
     *
     * @param text text
     * @return this
     */
    public Ansi render(String text) {
        append(Renderer.render(text));
        return this;
    }

    /**
     * String formats and renders the supplied arguments.
     *
     * @see #render(String)
     *
     * @param text format
     * @param args arguments
     * @return this
     */
    public Ansi render(String text, Object... args) {
        append(String.format(Renderer.render(text), args));
        return this;
    }

    @Override
    public String toString() {
        flushAttributes();
        return builder.toString();
    }

    ///////////////////////////////////////////////////////////////////
    // Private Helper Methods
    ///////////////////////////////////////////////////////////////////

    private Ansi appendEscapeSequence(char command) {
        flushAttributes();
        builder.append(FIRST_ESC_CHAR);
        builder.append(SECOND_ESC_CHAR);
        builder.append(command);
        return this;
    }

    private Ansi appendEscapeSequence(char command, int option) {
        flushAttributes();
        builder.append(FIRST_ESC_CHAR);
        builder.append(SECOND_ESC_CHAR);
        builder.append(option);
        builder.append(command);
        return this;
    }

    private Ansi appendEscapeSequence(char command, Object... options) {
        flushAttributes();
        return appendEscapeSequence0(command, options);
    }

    private void flushAttributes() {
        if (attributeOptions.isEmpty()) return;
        if (attributeOptions.size() == 1 && attributeOptions.get(0) == 0) {
            builder.append(FIRST_ESC_CHAR);
            builder.append(SECOND_ESC_CHAR);
            builder.append('m');
        } else {
            appendEscapeSequence0('m', attributeOptions.toArray());
        }
        attributeOptions.clear();
    }

    private Ansi appendEscapeSequence0(char command, Object... options) {
        builder.append(FIRST_ESC_CHAR);
        builder.append(SECOND_ESC_CHAR);
        int size = options.length;
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                builder.append(';');
            }
            if (options[i] != null) {
                builder.append(options[i]);
            }
        }
        builder.append(command);
        return this;
    }

    private static final class Renderer {

        private static final int BEGIN_TOKEN_LEN = 2;

        private static final int END_TOKEN_LEN = 2;

        public static String render(String input) throws IllegalArgumentException {
            try {
                return render(input, new StringBuilder()).toString();
            } catch (IOException e) {
                // Cannot happen because StringBuilder does not throw IOException
                throw new IllegalArgumentException(e);
            }
        }

        /**
         * Renders the given input to the target Appendable.
         *
         * @param input
         *            source to render
         * @param target
         *            render onto this target Appendable.
         * @return the given Appendable
         * @throws IOException
         *             If an I/O error occurs
         */
        public static Appendable render(String input, Appendable target) throws IOException {

            int i = 0;
            int j, k;

            while (true) {
                j = input.indexOf(BEGIN_TOKEN, i);
                if (j == -1) {
                    if (i == 0) {
                        target.append(input);
                        return target;
                    }
                    target.append(input.substring(i));
                    return target;
                }
                target.append(input.substring(i, j));
                k = input.indexOf(END_TOKEN, j);

                if (k == -1) {
                    target.append(input);
                    return target;
                }
                j += BEGIN_TOKEN_LEN;

                // Check for invalid string with END_TOKEN before BEGIN_TOKEN
                if (k < j) {
                    throw new IllegalArgumentException("Invalid input string found.");
                }
                String spec = input.substring(j, k);

                String[] items = spec.split(CODE_TEXT_SEPARATOR, 2);
                if (items.length == 1) {
                    target.append(input);
                    return target;
                }
                String replacement = render(items[1], items[0].split(CODE_LIST_SEPARATOR));

                target.append(replacement);

                i = k + END_TOKEN_LEN;
            }
        }

        public static String render(String text, String... codes) {
            return render(new Ansi(), codes).append(text).reset().toString();
        }

        /**
         * Renders {@link Code} names as an ANSI escape string.
         * @param codes The code names to render
         * @return an ANSI escape string.
         */
        public static String renderCodes(String... codes) {
            return render(new Ansi(), codes).toString();
        }

        /**
         * Renders {@link Code} names as an ANSI escape string.
         * @param codes A space separated list of code names to render
         * @return an ANSI escape string.
         */
        public static String renderCodes(String codes) {
            return renderCodes(codes.split("\\s"));
        }

        private static Ansi render(Ansi ansi, String... names) {
            for (String name : names) {
                Code code = Code.valueOf(name.toUpperCase(Locale.ENGLISH));
                if (code.isColor()) {
                    if (code.isBackground()) {
                        ansi.bgIndex(code.getValue());
                    } else {
                        ansi.fgIndex(code.getValue());
                    }
                } else if (code.isAttribute()) {
                    ansi.attribute(code.getValue());
                }
            }
            return ansi;
        }

        public static boolean test(String text) {
            return text != null && text.contains(BEGIN_TOKEN);
        }

        private enum Code {

            //
            // TODO: Find a better way to keep Code in sync with Color/Attribute/Erase
            //

            // Colors
            BLACK(Index.BLACK, true),
            RED(Index.RED, true),
            GREEN(Index.GREEN, true),
            YELLOW(Index.YELLOW, true),
            BLUE(Index.BLUE, true),
            MAGENTA(Index.MAGENTA, true),
            CYAN(Index.CYAN, true),
            WHITE(Index.WHITE, true),
            DEFAULT(Index.DEFAULT, true),

            // Foreground Colors
            FG_BLACK(Index.BLACK, true, false),
            FG_RED(Index.RED, true, false),
            FG_GREEN(Index.GREEN, true, false),
            FG_YELLOW(Index.YELLOW, true, false),
            FG_BLUE(Index.BLUE, true, false),
            FG_MAGENTA(Index.MAGENTA, true, false),
            FG_CYAN(Index.CYAN, true, false),
            FG_WHITE(Index.WHITE, true, false),
            FG_DEFAULT(Index.DEFAULT, true, false),

            // Background Colors
            BG_BLACK(Index.BLACK, true, true),
            BG_RED(Index.RED, true, true),
            BG_GREEN(Index.GREEN, true, true),
            BG_YELLOW(Index.YELLOW, true, true),
            BG_BLUE(Index.BLUE, true, true),
            BG_MAGENTA(Index.MAGENTA, true, true),
            BG_CYAN(Index.CYAN, true, true),
            BG_WHITE(Index.WHITE, true, true),
            BG_DEFAULT(Index.DEFAULT, true, true),

            // Attributes
            RESET(Attribute.RESET),
            INTENSITY_BOLD(Attribute.INTENSITY_BOLD),
            INTENSITY_FAINT(Attribute.INTENSITY_FAINT),
            ITALIC(Attribute.ITALIC),
            UNDERLINE(Attribute.UNDERLINE),
            BLINK_SLOW(Attribute.BLINK_SLOW),
            BLINK_FAST(Attribute.BLINK_FAST),
            BLINK_OFF(Attribute.BLINK_OFF),
            NEGATIVE_ON(Attribute.NEGATIVE_ON),
            NEGATIVE_OFF(Attribute.NEGATIVE_OFF),
            CONCEAL_ON(Attribute.CONCEAL_ON),
            CONCEAL_OFF(Attribute.CONCEAL_OFF),
            UNDERLINE_DOUBLE(Attribute.UNDERLINE_DOUBLE),
            UNDERLINE_OFF(Attribute.UNDERLINE_OFF),

            // Aliases
            BOLD(Attribute.INTENSITY_BOLD),
            FAINT(Attribute.INTENSITY_FAINT);

            private final int n;
            private final boolean color;
            private final boolean background;

            Code(int n, boolean color, boolean background) {
                this.n = n;
                this.color = color;
                this.background = background;
            }

            Code(int n, boolean color) {
                this(n, color, false);
            }

            Code(int n) {
                this(n, false);
            }

            public int getValue() {
                return n;
            }

            public boolean isColor() {
                return color;
            }

            public boolean isBackground() {
                return background;
            }

            public boolean isAttribute() {
                return !color && !background;
            }

        }

    }

    /**
     * Helper class for dealing with color rounding.
     * This is a simplified version of the 
     *   <a href="https://github.com/jline/jline3/blob/a24636dc5de83baa6b65049e8215fb372433b3b1/terminal/src/main/java/org/jline/utils/Colors.java">JLine's one</a>.
     */
    static class Colors {
    
        /**
         * Default 256 colors palette
         */
        // spotless:off
        public static final int[] DEFAULT_COLORS_256 = {
                // 16 ansi
                0x000000, 0x800000, 0x008000, 0x808000, 0x000080, 0x800080, 0x008080, 0xc0c0c0,
                0x808080, 0xff0000, 0x00ff00, 0xffff00, 0x0000ff, 0xff00ff, 0x00ffff, 0xffffff,
    
                // 6x6x6 color cube
                0x000000, 0x00005f, 0x000087, 0x0000af, 0x0000d7, 0x0000ff,
                0x005f00, 0x005f5f, 0x005f87, 0x005faf, 0x005fd7, 0x005fff,
                0x008700, 0x00875f, 0x008787, 0x0087af, 0x0087d7, 0x0087ff,
                0x00af00, 0x00af5f, 0x00af87, 0x00afaf, 0x00afd7, 0x00afff,
                0x00d700, 0x00d75f, 0x00d787, 0x00d7af, 0x00d7d7, 0x00d7ff,
                0x00ff00, 0x00ff5f, 0x00ff87, 0x00ffaf, 0x00ffd7, 0x00ffff,
    
                0x5f0000, 0x5f005f, 0x5f0087, 0x5f00af, 0x5f00d7, 0x5f00ff,
                0x5f5f00, 0x5f5f5f, 0x5f5f87, 0x5f5faf, 0x5f5fd7, 0x5f5fff,
                0x5f8700, 0x5f875f, 0x5f8787, 0x5f87af, 0x5f87d7, 0x5f87ff,
                0x5faf00, 0x5faf5f, 0x5faf87, 0x5fafaf, 0x5fafd7, 0x5fafff,
                0x5fd700, 0x5fd75f, 0x5fd787, 0x5fd7af, 0x5fd7d7, 0x5fd7ff,
                0x5fff00, 0x5fff5f, 0x5fff87, 0x5fffaf, 0x5fffd7, 0x5fffff,
    
                0x870000, 0x87005f, 0x870087, 0x8700af, 0x8700d7, 0x8700ff,
                0x875f00, 0x875f5f, 0x875f87, 0x875faf, 0x875fd7, 0x875fff,
                0x878700, 0x87875f, 0x878787, 0x8787af, 0x8787d7, 0x8787ff,
                0x87af00, 0x87af5f, 0x87af87, 0x87afaf, 0x87afd7, 0x87afff,
                0x87d700, 0x87d75f, 0x87d787, 0x87d7af, 0x87d7d7, 0x87d7ff,
                0x87ff00, 0x87ff5f, 0x87ff87, 0x87ffaf, 0x87ffd7, 0x87ffff,
    
                0xaf0000, 0xaf005f, 0xaf0087, 0xaf00af, 0xaf00d7, 0xaf00ff,
                0xaf5f00, 0xaf5f5f, 0xaf5f87, 0xaf5faf, 0xaf5fd7, 0xaf5fff,
                0xaf8700, 0xaf875f, 0xaf8787, 0xaf87af, 0xaf87d7, 0xaf87ff,
                0xafaf00, 0xafaf5f, 0xafaf87, 0xafafaf, 0xafafd7, 0xafafff,
                0xafd700, 0xafd75f, 0xafd787, 0xafd7af, 0xafd7d7, 0xafd7ff,
                0xafff00, 0xafff5f, 0xafff87, 0xafffaf, 0xafffd7, 0xafffff,
    
                0xd70000, 0xd7005f, 0xd70087, 0xd700af, 0xd700d7, 0xd700ff,
                0xd75f00, 0xd75f5f, 0xd75f87, 0xd75faf, 0xd75fd7, 0xd75fff,
                0xd78700, 0xd7875f, 0xd78787, 0xd787af, 0xd787d7, 0xd787ff,
                0xd7af00, 0xd7af5f, 0xd7af87, 0xd7afaf, 0xd7afd7, 0xd7afff,
                0xd7d700, 0xd7d75f, 0xd7d787, 0xd7d7af, 0xd7d7d7, 0xd7d7ff,
                0xd7ff00, 0xd7ff5f, 0xd7ff87, 0xd7ffaf, 0xd7ffd7, 0xd7ffff,
    
                0xff0000, 0xff005f, 0xff0087, 0xff00af, 0xff00d7, 0xff00ff,
                0xff5f00, 0xff5f5f, 0xff5f87, 0xff5faf, 0xff5fd7, 0xff5fff,
                0xff8700, 0xff875f, 0xff8787, 0xff87af, 0xff87d7, 0xff87ff,
                0xffaf00, 0xffaf5f, 0xffaf87, 0xffafaf, 0xffafd7, 0xffafff,
                0xffd700, 0xffd75f, 0xffd787, 0xffd7af, 0xffd7d7, 0xffd7ff,
                0xffff00, 0xffff5f, 0xffff87, 0xffffaf, 0xffffd7, 0xffffff,
    
                // 24 grey ramp
                0x080808, 0x121212, 0x1c1c1c, 0x262626, 0x303030, 0x3a3a3a, 0x444444, 0x4e4e4e,
                0x585858, 0x626262, 0x6c6c6c, 0x767676, 0x808080, 0x8a8a8a, 0x949494, 0x9e9e9e,
                0xa8a8a8, 0xb2b2b2, 0xbcbcbc, 0xc6c6c6, 0xd0d0d0, 0xdadada, 0xe4e4e4, 0xeeeeee,
        };
        // spotless:on
    
        public static int roundColor(int col, int max) {
            if (col >= max) {
                int c = DEFAULT_COLORS_256[col];
                col = roundColor(c, DEFAULT_COLORS_256, max);
            }
            return col;
        }
    
        public static int roundRgbColor(int r, int g, int b, int max) {
            return roundColor((r << 16) + (g << 8) + b, DEFAULT_COLORS_256, max);
        }
    
        private static int roundColor(int color, int[] colors, int max) {
            double best_distance = Integer.MAX_VALUE;
            int best_index = Integer.MAX_VALUE;
            for (int idx = 0; idx < max; idx++) {
                double d = cie76(color, colors[idx]);
                if (d <= best_distance) {
                    best_index = idx;
                    best_distance = d;
                }
            }
            return best_index;
        }
    
        private static double cie76(int c1, int c2) {
            return scalar(rgb2cielab(c1), rgb2cielab(c2));
        }
    
        private static double scalar(double[] c1, double[] c2) {
            return sqr(c1[0] - c2[0]) + sqr(c1[1] - c2[1]) + sqr(c1[2] - c2[2]);
        }
    
        private static double[] rgb(int color) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color >> 0) & 0xFF;
            return new double[] {r / 255.0, g / 255.0, b / 255.0};
        }
    
        private static double[] rgb2cielab(int color) {
            return rgb2cielab(rgb(color));
        }
    
        private static double[] rgb2cielab(double[] rgb) {
            return xyz2lab(rgb2xyz(rgb));
        }
    
        private static double[] rgb2xyz(double[] rgb) {
            double vr = pivotRgb(rgb[0]);
            double vg = pivotRgb(rgb[1]);
            double vb = pivotRgb(rgb[2]);
            // http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
            double x = vr * 0.4124564 + vg * 0.3575761 + vb * 0.1804375;
            double y = vr * 0.2126729 + vg * 0.7151522 + vb * 0.0721750;
            double z = vr * 0.0193339 + vg * 0.1191920 + vb * 0.9503041;
            return new double[] {x, y, z};
        }
    
        private static double pivotRgb(double n) {
            return n > 0.04045 ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92;
        }
    
        private static double[] xyz2lab(double[] xyz) {
            double fx = pivotXyz(xyz[0]);
            double fy = pivotXyz(xyz[1]);
            double fz = pivotXyz(xyz[2]);
            double l = 116.0 * fy - 16.0;
            double a = 500.0 * (fx - fy);
            double b = 200.0 * (fy - fz);
            return new double[] {l, a, b};
        }
    
        private static final double epsilon = 216.0 / 24389.0;
        private static final double kappa = 24389.0 / 27.0;
    
        private static double pivotXyz(double n) {
            return n > epsilon ? Math.cbrt(n) : (kappa * n + 16) / 116;
        }
    
        private static double sqr(double n) {
            return n * n;
        }
    }
    
}
