package unrefined.internal;

import unrefined.desktop.ReflectionSupport;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

public final class KeyUtils {

    private KeyUtils() {
        throw new NotInstantiableError(KeyUtils.class);
    }

    private static final Field rawCodeField;
    private static final Field scancodeField;
    static {
        try {
            rawCodeField = KeyEvent.class.getDeclaredField("rawCode");
            if (SystemUtils.IS_WINDOWS) scancodeField = KeyEvent.class.getDeclaredField("scancode");
            else scancodeField = null;
        } catch (NoSuchFieldException e) {
            throw new UnexpectedError(e);
        }
    }

    public static int getRawCode(KeyEvent event) {
        return (int) ReflectionSupport.getLongField(event, rawCodeField);
    }

    public static int getScancode(KeyEvent event) {
        if (scancodeField == null) throw new IllegalStateException("No scancode");
        else return (int) ReflectionSupport.getLongField(event, scancodeField);
    }

    private interface KeyParser {
        String parseKey(KeyEvent event);
        String parseCode(KeyEvent event);
    }

    private static final KeyParser WINDOWS_KEY_PARSER = new KeyParser() {
        @Override
        public String parseKey(KeyEvent event) {
            return switch (getRawCode(event)) {
                case 0x12, 0xA4, 0xA5 -> "Alt";
                case 0x14 -> "CapsLock";
                case 0x11, 0xA2, 0xA3 -> "Control";
                case 0x5B, 0x5C -> "Meta";
                case 0x90 -> "NumLock";
                case 0x91 -> "ScrollLock";
                case 0x10, 0xA0, 0xA1 -> "Shift";
                case 0x0D -> "Enter";
                case 0x09 -> "Tab";
                case 0x20 -> " ";
                case 0x28 -> "ArrowDown";
                case 0x25 -> "ArrowLeft";
                case 0x27 -> "ArrowRight";
                case 0x26 -> "ArrowUp";
                case 0x23 -> "End";
                case 0x24 -> "Home";
                case 0x22 -> "PageDown";
                case 0x21 -> "PageUp";
                case 0x08 -> "Backspace";
                case 0x0C, 0xFE -> "Clear";
                case 0xF7 -> "CrSel";
                case 0x2E -> "Delete";
                case 0xF9 -> "EraseEof";
                case 0xF8 -> "ExSel";
                case 0x2D -> "Insert";
                case 0x1E -> "Accept";
                case 0x5D -> "ContextMenu";
                case 0x1B -> "Escape";
                case 0x2B -> "Execute";
                case 0x2F -> "Help";
                case 0x13 -> "Pause";
                case 0xFA -> "Play";
                case 0x29 -> "Select";
                case 0x2C -> "PrintScreen";
                case 0x5F -> "Standby";
                case 0xF0 -> "Alphanumeric";
                case 0x1C -> "Convert";
                case 0x18 -> "FinalMode";
                case 0x1F -> "ModeChange";
                case 0x1D -> "NonConvert";
                case 0xE5 -> "Process";
                case 0x17 -> "JunjaMode";
                case 0xF3 -> "Hankaku";
                case 0xF2 -> "Hiragana";
                case 0x15, 0xF6 -> "KanaMode";
                case 0x19 -> "KanjiMode";
                case 0xF1 -> "Katakana";
                case 0xF5 -> "Romaji";
                case 0xF4 -> "Zenkaku";
                case 0x70 -> "F1";
                case 0x71 -> "F2";
                case 0x72 -> "F3";
                case 0x73 -> "F4";
                case 0x74 -> "F5";
                case 0x75 -> "F6";
                case 0x76 -> "F7";
                case 0x77 -> "F8";
                case 0x78 -> "F9";
                case 0x79 -> "F10";
                case 0x7A -> "F11";
                case 0x7B -> "F12";
                case 0x7C -> "F13";
                case 0x7D -> "F14";
                case 0x7E -> "F15";
                case 0x7F -> "F16";
                case 0x80 -> "F17";
                case 0x81 -> "F18";
                case 0x82 -> "F19";
                case 0x83 -> "F20";
                case 0xB3 -> "MediaPlayPause";
                case 0xB2 -> "MediaStop";
                case 0xB0 -> "MediaTrackNext";
                case 0xB1 -> "MediaTrackPrevious";
                case 0xAE -> "AudioVolumeDown";
                case 0xAD -> "AudioVolumeMute";
                case 0xAF -> "AudioVolumeUp";
                case 0xFB -> "ZoomToggle";
                case 0xB4 -> "LaunchMail";
                case 0xB5 -> "LaunchMediaPlayer";
                case 0xB6 -> "LaunchApplication1";
                case 0xB7 -> "LaunchApplication2";
                case 0xA6 -> "BrowserBack";
                case 0xAB -> "BrowserFavorites";
                case 0xA7 -> "BrowserForward";
                case 0xAC -> "BrowserHome";
                case 0xA8 -> "BrowserRefresh";
                case 0xAA -> "BrowserSearch";
                case 0xA9 -> "BrowserStop";
                case 0x6A -> "*";
                case 0x6B -> "+";
                case 0x6F -> "/";
                case 0x6D -> "-";
                case 0x6C -> "Separator";
                default -> event.getKeyChar() == KeyEvent.CHAR_UNDEFINED ? null : String.valueOf(event.getKeyChar());
            };
        }
        @Override
        public String parseCode(KeyEvent event) {
            return switch (getScancode(event)) {
                case 0x0001 -> "Escape";
                case 0x0002 -> "Digit1";
                case 0x0003 -> "Digit2";
                case 0x0004 -> "Digit3";
                case 0x0005 -> "Digit4";
                case 0x0006 -> "Digit5";
                case 0x0007 -> "Digit6";
                case 0x0008 -> "Digit7";
                case 0x0009 -> "Digit8";
                case 0x000A -> "Digit9";
                case 0x000B -> "Digit0";
                case 0x000C -> "Minus";
                case 0x000D -> "Equal";
                case 0x000E -> "Backspace";
                case 0x000F -> "Tab";
                case 0x0010 -> "KeyQ";
                case 0x0011 -> "KeyW";
                case 0x0012 -> "KeyE";
                case 0x0013 -> "KeyR";
                case 0x0014 -> "KeyT";
                case 0x0015 -> "KeyY";
                case 0x0016 -> "KeyU";
                case 0x0017 -> "KeyI";
                case 0x0018 -> "KeyO";
                case 0x0019 -> "KeyP";
                case 0x001A -> "BracketLeft";
                case 0x001B -> "BracketRight";
                case 0x001C -> "Enter";
                case 0x001D -> "ControlLeft";
                case 0x001E -> "KeyA";
                case 0x001F -> "KeyS";
                case 0x0020 -> "KeyD";
                case 0x0021 -> "KeyF";
                case 0x0022 -> "KeyG";
                case 0x0023 -> "KeyH";
                case 0x0024 -> "KeyJ";
                case 0x0025 -> "KeyK";
                case 0x0026 -> "KeyL";
                case 0x0027 -> "Semicolon";
                case 0x0028 -> "Quote";
                case 0x0029 -> "Backquote";
                case 0x002A -> "ShiftLeft";
                case 0x002B -> "Backslash";
                case 0x002C -> "KeyZ";
                case 0x002D -> "KeyX";
                case 0x002E -> "KeyC";
                case 0x002F -> "KeyV";
                case 0x0030 -> "KeyB";
                case 0x0031 -> "KeyN";
                case 0x0032 -> "KeyM";
                case 0x0033 -> "Comma";
                case 0x0034 -> "Period";
                case 0x0035 -> "Slash";
                case 0x0036 -> "ShiftRight";
                case 0x0037 -> "NumpadMultiply";
                case 0x0038 -> "AltLeft";
                case 0x0039 -> "Space";
                case 0x003A -> "CapsLock";
                case 0x003B -> "F1";
                case 0x003C -> "F2";
                case 0x003D -> "F3";
                case 0x003E -> "F4";
                case 0x003F -> "F5";
                case 0x0040 -> "F6";
                case 0x0041 -> "F7";
                case 0x0042 -> "F8";
                case 0x0043 -> "F9";
                case 0x0044 -> "F10";
                case 0x0045 -> "Pause";
                case 0x0046 -> "ScrollLock";
                case 0x0047 -> "Numpad7";
                case 0x0048 -> "Numpad8";
                case 0x0049 -> "Numpad9";
                case 0x004A -> "NumpadSubtract";
                case 0x004B -> "Numpad4";
                case 0x004C -> "Numpad5";
                case 0x004D -> "Numpad6";
                case 0x004E -> "NumpadAdd";
                case 0x004F -> "Numpad1";
                case 0x0050 -> "Numpad2";
                case 0x0051 -> "Numpad3";
                case 0x0052 -> "Numpad0";
                case 0x0053 -> "NumpadDecimal";
                case 0x0054 -> "PrintScreen";
                case 0x0056 -> "IntlBackslash";
                case 0x0057 -> "F11";
                case 0x0058 -> "F12";
                case 0x0059 -> "NumpadEqual";
                case 0x0064 -> "F13";
                case 0x0065 -> "F14";
                case 0x0066 -> "F15";
                case 0x0067 -> "F16";
                case 0x0068 -> "F17";
                case 0x0069 -> "F18";
                case 0x006A -> "F19";
                case 0x006B -> "F20";
                case 0x006C -> "F21";
                case 0x006D -> "F22";
                case 0x006E -> "F23";
                case 0x0070 -> "KanaMode";
                case 0x0071 -> "Lang2";
                case 0x0072 -> "Lang1";
                case 0x0073 -> "IntlRo";
                case 0x0076 -> "F24";
                case 0x0077 -> "Lang4";
                case 0x0078 -> "Lang3";
                case 0x0079 -> "Convert";
                case 0x007B -> "NonConvert";
                case 0x007D -> "IntlYen";
                case 0x007E -> "NumpadComma";
                case 0xE008 -> "Undo";
                case 0xE00A -> "Paste";
                case 0xE010 -> "MediaTrackPrevious";
                case 0xE017 -> "Cut";
                case 0xE018 -> "Copy";
                case 0xE019 -> "MediaTrackNext";
                case 0xE01C -> "NumpadEnter";
                case 0xE01D -> "ControlRight";
                case 0xE020 -> "AudioVolumeMute";
                case 0xE021 -> "LaunchApp2";
                case 0xE022 -> "MediaPlayPause";
                case 0xE024 -> "MediaStop";
                case 0xE02C -> "Eject";
                case 0xE02E -> "AudioVolumeDown";
                case 0xE030 -> "AudioVolumeUp";
                case 0xE032 -> "BrowserHome";
                case 0xE035 -> "NumpadDivide";
                case 0xE037 -> "PrintScreen";
                case 0xE038 -> "AltRight";
                case 0xE03B -> "Help";
                case 0xE045 -> "NumLock";
                case 0xE046 -> "Pause";
                case 0xE047 -> "Home";
                case 0xE048 -> "ArrowUp";
                case 0xE049 -> "PageUp";
                case 0xE04B -> "ArrowLeft";
                case 0xE04D -> "ArrowRight";
                case 0xE04F -> "End";
                case 0xE050 -> "ArrowDown";
                case 0xE051 -> "PageDown";
                case 0xE052 -> "Insert";
                case 0xE053 -> "Delete";
                case 0xE05B -> "MetaLeft";
                case 0xE05C -> "MetaRight";
                case 0xE05D -> "ContextMenu";
                case 0xE05E -> "Power";
                case 0xE05F -> "Sleep";
                case 0xE063 -> "WakeUp";
                case 0xE065 -> "BrowserSearch";
                case 0xE066 -> "BrowserFavorites";
                case 0xE067 -> "BrowserRefresh";
                case 0xE068 -> "BrowserStop";
                case 0xE069 -> "BrowserForward";
                case 0xE06A -> "BrowserBack";
                case 0xE06B -> "LaunchApp1";
                case 0xE06C -> "LaunchMail";
                case 0xE06D -> "MediaSelect";
                case 0xE0F1 -> "Lang2";
                case 0xE0F2 -> "Lang1";
                default -> null;
            };
        }
    };

    private static final KeyParser MAC_KEY_PARSER = new KeyParser() {
        @Override
        public String parseKey(KeyEvent event) {
            return switch (getRawCode(event)) {
                case 0x3A, 0x3D -> "Alt";
                case 0x39 -> "CapsLock";
                case 0x3B, 0x3E -> "Control";
                case 0x3F -> "Fn";
                case 0x37, 0x36 -> "Meta";
                case 0x38, 0x3C -> "Shift";
                case 0x24, 0x4C, 0x34 -> "Enter";
                case 0x30 -> "Tab";
                case 0x31 -> " ";
                case 0x7D -> "ArrowDown";
                case 0x7B -> "ArrowLeft";
                case 0x7C -> "ArrowRight";
                case 0x7E -> "ArrowUp";
                case 0x77 -> "End";
                case 0x73 -> "Home";
                case 0x79 -> "PageDown";
                case 0x74 -> "PageUp";
                case 0x33 -> "Backspace";
                case 0x47 -> "Clear";
                case 0x75 -> "Delete";
                case 0x6E -> "ContextMenu";
                case 0x35 -> "Escape";
                case 0x72 -> "Help";
                case 0x66 -> "Eisu";
                case 0x68 -> "KanjiMode";
                case 0x7A -> "F1";
                case 0x78 -> "F2";
                case 0x63 -> "F3";
                case 0x76 -> "F4";
                case 0x60 -> "F5";
                case 0x61 -> "F6";
                case 0x62 -> "F7";
                case 0x64 -> "F8";
                case 0x65 -> "F9";
                case 0x6D -> "F10";
                case 0x67 -> "F11";
                case 0x6F -> "F12";
                case 0x69 -> "F13";
                case 0x6B -> "F14";
                case 0x71 -> "F15";
                case 0x6A -> "F16";
                case 0x40 -> "F17";
                case 0x4F -> "F18";
                case 0x50 -> "F19";
                case 0x5A -> "F20";
                case 0x49 -> "AudioVolumeDown";
                case 0x4A -> "AudioVolumeMute";
                case 0x48 -> "AudioVolumeUp";
                case 0x43 -> "*";
                case 0x45 -> "+";
                case 0x4B -> "/";
                case 0x4E -> "-";
                case 0x5F -> "Separator";
                default -> event.getKeyChar() == KeyEvent.CHAR_UNDEFINED ? null : String.valueOf(event.getKeyChar());
            };
        }

        @Override
        public String parseCode(KeyEvent event) {
            return switch (getRawCode(event)) {
                case 0x00 -> "KeyA";
                case 0x01 -> "KeyS";
                case 0x02 -> "KeyD";
                case 0x03 -> "KeyF";
                case 0x04 -> "KeyH";
                case 0x05 -> "KeyG";
                case 0x06 -> "KeyZ";
                case 0x07 -> "KeyX";
                case 0x08 -> "KeyC";
                case 0x09 -> "KeyV";
                case 0x0A -> "IntlBackslash";
                case 0x0B -> "KeyB";
                case 0x0C -> "KeyQ";
                case 0x0D -> "KeyW";
                case 0x0E -> "KeyE";
                case 0x0F -> "KeyR";
                case 0x10 -> "KeyY";
                case 0x11 -> "KeyT";
                case 0x12 -> "Digit1";
                case 0x13 -> "Digit2";
                case 0x14 -> "Digit3";
                case 0x15 -> "Digit4";
                case 0x16 -> "Digit6";
                case 0x17 -> "Digit5";
                case 0x18 -> "Equal";
                case 0x19 -> "Digit9";
                case 0x1A -> "Digit7";
                case 0x1B -> "Minus";
                case 0x1C -> "Digit8";
                case 0x1D -> "Digit0";
                case 0x1E -> "BracketRight";
                case 0x1F -> "KeyO";
                case 0x20 -> "KeyU";
                case 0x21 -> "BracketLeft";
                case 0x22 -> "KeyI";
                case 0x23 -> "KeyP";
                case 0x24 -> "Enter";
                case 0x25 -> "KeyL";
                case 0x26 -> "KeyJ";
                case 0x27 -> "Quote";
                case 0x28 -> "KeyK";
                case 0x29 -> "Semicolon";
                case 0x2A -> "Backslash";
                case 0x2B -> "Comma";
                case 0x2C -> "Slash";
                case 0x2D -> "KeyN";
                case 0x2E -> "KeyM";
                case 0x2F -> "Period";
                case 0x30 -> "Tab";
                case 0x31 -> "Space";
                case 0x32 -> "Backquote";
                case 0x33 -> "Backspace";
                case 0x34 -> "NumpadEnter";
                case 0x35 -> "Escape";
                case 0x36 -> "MetaRight";
                case 0x37 -> "MetaLeft";
                case 0x38 -> "ShiftLeft";
                case 0x39 -> "CapsLock";
                case 0x3A -> "AltLeft";
                case 0x3B -> "ControlLeft";
                case 0x3C -> "ShiftRight";
                case 0x3D -> "AltRight";
                case 0x3E -> "ControlRight";
                case 0x3F -> "Fn";
                case 0x40 -> "F17";
                case 0x41 -> "NumpadDecimal";
                case 0x43 -> "NumpadMultiply";
                case 0x45 -> "NumpadAdd";
                case 0x47 -> "NumLock";
                case 0x48 -> "AudioVolumeUp";
                case 0x49 -> "AudioVolumeDown";
                case 0x4A -> "AudioVolumeMute";
                case 0x4B -> "NumpadDivide";
                case 0x4C -> "NumpadEnter";
                case 0x4E -> "NumpadSubtract";
                case 0x4F -> "F18";
                case 0x50 -> "F19";
                case 0x51 -> "NumpadEqual";
                case 0x52 -> "Numpad0";
                case 0x53 -> "Numpad1";
                case 0x54 -> "Numpad2";
                case 0x55 -> "Numpad3";
                case 0x56 -> "Numpad4";
                case 0x57 -> "Numpad5";
                case 0x58 -> "Numpad6";
                case 0x59 -> "Numpad7";
                case 0x5A -> "F20";
                case 0x5B -> "Numpad8";
                case 0x5C -> "Numpad9";
                case 0x5D -> "IntlYen";
                case 0x5E -> "IntlRo";
                case 0x5F -> "NumpadComma";
                case 0x60 -> "F5";
                case 0x61 -> "F6";
                case 0x62 -> "F7";
                case 0x63 -> "F3";
                case 0x64 -> "F8";
                case 0x65 -> "F9";
                case 0x66 -> "Lang2";
                case 0x67 -> "F11";
                case 0x68 -> "Lang1";
                case 0x69 -> "F13";
                case 0x6A -> "F16";
                case 0x6B -> "F14";
                case 0x6D -> "F10";
                case 0x6E -> "ContextMenu";
                case 0x6F -> "F12";
                case 0x71 -> "F15";
                case 0x72 -> "Help";
                case 0x73 -> "Home";
                case 0x74 -> "PageUp";
                case 0x75 -> "Delete";
                case 0x76 -> "F4";
                case 0x77 -> "End";
                case 0x78 -> "F2";
                case 0x79 -> "PageDown";
                case 0x7A -> "F1";
                case 0x7B -> "ArrowLeft";
                case 0x7C -> "ArrowRight";
                case 0x7D -> "ArrowDown";
                case 0x7E -> "ArrowUp";
                default -> null;
            };
        }
    };

    private static final KeyParser X11_KEY_PARSER = new KeyParser() {
        @Override
        public String parseKey(KeyEvent event) {
            return switch (event.getExtendedKeyCode()) {
                case KeyEvent.VK_ALT -> "Alt";
                case KeyEvent.VK_ALT_GRAPH -> "AltGraph";
                case KeyEvent.VK_CAPS_LOCK -> "CapsLock";
                case KeyEvent.VK_CONTROL -> "Control";
                case KeyEvent.VK_WINDOWS -> "Meta";
                case KeyEvent.VK_NUM_LOCK -> "NumLock";
                case KeyEvent.VK_SCROLL_LOCK -> "ScrollLock";
                case KeyEvent.VK_SHIFT -> "Shift";
                case KeyEvent.VK_ENTER -> "Enter";
                case KeyEvent.VK_TAB -> "Tab";
                case KeyEvent.VK_SPACE -> " ";
                case KeyEvent.VK_DOWN, KeyEvent.VK_KP_DOWN -> "ArrowDown";
                case KeyEvent.VK_LEFT, KeyEvent.VK_KP_LEFT -> "ArrowLeft";
                case KeyEvent.VK_RIGHT, KeyEvent.VK_KP_RIGHT -> "ArrowRight";
                case KeyEvent.VK_UP, KeyEvent.VK_KP_UP -> "ArrowUp";
                case KeyEvent.VK_END -> "End";
                case KeyEvent.VK_HOME -> "Home";
                case KeyEvent.VK_PAGE_DOWN -> "PageDown";
                case KeyEvent.VK_PAGE_UP -> "PageUp";
                case KeyEvent.VK_BACK_SPACE -> "Backspace";
                case KeyEvent.VK_CLEAR -> "Clear";
                case KeyEvent.VK_COPY -> "Copy";
                case KeyEvent.VK_CUT -> "Cut";
                case KeyEvent.VK_DELETE -> "Delete";
                case KeyEvent.VK_INSERT -> "Insert";
                case KeyEvent.VK_PASTE -> "Paste";
                case KeyEvent.VK_UNDO -> "Undo";
                case KeyEvent.VK_ACCEPT -> "Accept";
                case KeyEvent.VK_AGAIN -> "Again";
                case KeyEvent.VK_CANCEL -> "Cancel";
                case KeyEvent.VK_CONTEXT_MENU -> "ContextMenu";
                case KeyEvent.VK_ESCAPE -> "Escape";
                case KeyEvent.VK_FIND -> "Find";
                case KeyEvent.VK_HELP -> "Help";
                case KeyEvent.VK_PAUSE -> "Pause";
                case KeyEvent.VK_PROPS -> "Props";
                case KeyEvent.VK_PRINTSCREEN -> "PrintScreen";
                case KeyEvent.VK_ALL_CANDIDATES -> "AllCandidates";
                case KeyEvent.VK_ALPHANUMERIC -> "Alphanumeric";
                case KeyEvent.VK_CODE_INPUT -> "CodeInput";
                case KeyEvent.VK_COMPOSE -> "Compose";
                case KeyEvent.VK_CONVERT -> "Convert";
                /*
                case KeyEvent.VK_DEAD_ABOVEDOT,
                        KeyEvent.VK_DEAD_ABOVERING,
                        KeyEvent.VK_DEAD_ACUTE,
                        KeyEvent.VK_DEAD_BREVE,
                        KeyEvent.VK_DEAD_CARON,
                        KeyEvent.VK_DEAD_CEDILLA,
                        KeyEvent.VK_DEAD_CIRCUMFLEX,
                        KeyEvent.VK_DEAD_DIAERESIS,
                        KeyEvent.VK_DEAD_DOUBLEACUTE,
                        KeyEvent.VK_DEAD_GRAVE,
                        KeyEvent.VK_DEAD_IOTA,
                        KeyEvent.VK_DEAD_MACRON,
                        KeyEvent.VK_DEAD_OGONEK,
                        KeyEvent.VK_DEAD_SEMIVOICED_SOUND,
                        KeyEvent.VK_DEAD_TILDE,
                        KeyEvent.VK_DEAD_VOICED_SOUND -> "Dead";
                 */
                /*
                case KeyEvent.VK_DEAD_GRAVE -> "`";
                case KeyEvent.VK_DEAD_ACUTE -> "´";
                case KeyEvent.VK_DEAD_CIRCUMFLEX -> "ˆ";
                case KeyEvent.VK_DEAD_TILDE -> "˜";
                case KeyEvent.VK_DEAD_MACRON -> "¯";
                case KeyEvent.VK_DEAD_BREVE -> "˘";
                case KeyEvent.VK_DEAD_ABOVEDOT -> "˙";
                case KeyEvent.VK_DEAD_DIAERESIS -> "¨";
                case KeyEvent.VK_DEAD_ABOVERING -> "˚";
                case KeyEvent.VK_DEAD_DOUBLEACUTE -> "˝";
                case KeyEvent.VK_DEAD_CARON -> "ˇ";
                case KeyEvent.VK_DEAD_CEDILLA -> "¸";
                case KeyEvent.VK_DEAD_OGONEK -> "˛";
                case KeyEvent.VK_DEAD_IOTA -> "ͅ";
                case KeyEvent.VK_DEAD_VOICED_SOUND -> "゙";
                case KeyEvent.VK_DEAD_SEMIVOICED_SOUND -> "゚";
                 */
                case KeyEvent.VK_FINAL -> "FinalMode";
                case KeyEvent.VK_MODECHANGE -> "ModeChange";
                case KeyEvent.VK_NONCONVERT -> "NonConvert";
                case KeyEvent.VK_PREVIOUS_CANDIDATE -> "PreviousCandidate";
                case KeyEvent.VK_HIRAGANA, KeyEvent.VK_JAPANESE_HIRAGANA -> "Hiragana";
                case KeyEvent.VK_KANA, KeyEvent.VK_KANA_LOCK -> "KanaMode";
                case KeyEvent.VK_KANJI, KeyEvent.VK_INPUT_METHOD_ON_OFF -> "KanjiMode";
                case KeyEvent.VK_KATAKANA, KeyEvent.VK_JAPANESE_KATAKANA -> "Katakana";
                case KeyEvent.VK_ROMAN_CHARACTERS, KeyEvent.VK_JAPANESE_ROMAN -> "Romaji";
                case KeyEvent.VK_FULL_WIDTH -> "Zenkaku";
                case KeyEvent.VK_HALF_WIDTH -> "Hankaku";
                case KeyEvent.VK_F1 -> "F1";
                case KeyEvent.VK_F2 -> "F2";
                case KeyEvent.VK_F3 -> "F3";
                case KeyEvent.VK_F4 -> "F4";
                case KeyEvent.VK_F5 -> "F5";
                case KeyEvent.VK_F6 -> "F6";
                case KeyEvent.VK_F7 -> "F7";
                case KeyEvent.VK_F8 -> "F8";
                case KeyEvent.VK_F9 -> "F9";
                case KeyEvent.VK_F10 -> "F10";
                case KeyEvent.VK_F11 -> "F11";
                case KeyEvent.VK_F12 -> "F12";
                case KeyEvent.VK_F13 -> "F13";
                case KeyEvent.VK_F14 -> "F14";
                case KeyEvent.VK_F15 -> "F15";
                case KeyEvent.VK_F16 -> "F16";
                case KeyEvent.VK_F17 -> "F17";
                case KeyEvent.VK_F18 -> "F18";
                case KeyEvent.VK_F19 -> "F19";
                case KeyEvent.VK_F20 -> "F20";
                case KeyEvent.VK_F21 -> "Soft1";
                case KeyEvent.VK_F22 -> "Soft2";
                case KeyEvent.VK_F23 -> "Soft3";
                case KeyEvent.VK_F24 -> "Soft4";
                case KeyEvent.VK_NUMPAD0 -> "0";
                case KeyEvent.VK_NUMPAD1 -> "1";
                case KeyEvent.VK_NUMPAD2 -> "2";
                case KeyEvent.VK_NUMPAD3 -> "3";
                case KeyEvent.VK_NUMPAD4 -> "4";
                case KeyEvent.VK_NUMPAD5 -> "5";
                case KeyEvent.VK_NUMPAD6 -> "6";
                case KeyEvent.VK_NUMPAD7 -> "7";
                case KeyEvent.VK_NUMPAD8 -> "8";
                case KeyEvent.VK_NUMPAD9 -> "9";
                case KeyEvent.VK_MULTIPLY -> "*";
                case KeyEvent.VK_ADD -> "+";
                case KeyEvent.VK_SUBTRACT -> "-";
                case KeyEvent.VK_DIVIDE -> "/";
                case KeyEvent.VK_SEPARATOR -> "Separator";
                default -> event.getKeyChar() == KeyEvent.CHAR_UNDEFINED ? parseCode(event) : String.valueOf(event.getKeyChar());
            };
        }
        @Override
        public String parseCode(KeyEvent event) {
            return switch (getRawCode(event)) {
                case 0x0009 -> "Escape";
                case 0x000A -> "Digit1";
                case 0x000B -> "Digit2";
                case 0x000C -> "Digit3";
                case 0x000D -> "Digit4";
                case 0x000E -> "Digit5";
                case 0x000F -> "Digit6";
                case 0x0010 -> "Digit7";
                case 0x0011 -> "Digit8";
                case 0x0012 -> "Digit9";
                case 0x0013 -> "Digit0";
                case 0x0014 -> "Minus";
                case 0x0015 -> "Equal";
                case 0x0016 -> "Backspace";
                case 0x0017 -> "Tab";
                case 0x0018 -> "KeyQ";
                case 0x0019 -> "KeyW";
                case 0x001A -> "KeyE";
                case 0x001B -> "KeyR";
                case 0x001C -> "KeyT";
                case 0x001D -> "KeyY";
                case 0x001E -> "KeyU";
                case 0x001F -> "KeyI";
                case 0x0020 -> "KeyO";
                case 0x0021 -> "KeyP";
                case 0x0022 -> "BracketLeft";
                case 0x0023 -> "BracketRight";
                case 0x0024 -> "Enter";
                case 0x0025 -> "ControlLeft";
                case 0x0026 -> "KeyA";
                case 0x0027 -> "KeyS";
                case 0x0028 -> "KeyD";
                case 0x0029 -> "KeyF";
                case 0x002A -> "KeyG";
                case 0x002B -> "KeyH";
                case 0x002C -> "KeyJ";
                case 0x002D -> "KeyK";
                case 0x002E -> "KeyL";
                case 0x002F -> "Semicolon";
                case 0x0030 -> "Quote";
                case 0x0031 -> "Backquote";
                case 0x0032 -> "ShiftLeft";
                case 0x0033 -> "Backslash";
                case 0x0034 -> "KeyZ";
                case 0x0035 -> "KeyX";
                case 0x0036 -> "KeyC";
                case 0x0037 -> "KeyV";
                case 0x0038 -> "KeyB";
                case 0x0039 -> "KeyN";
                case 0x003A -> "KeyM";
                case 0x003B -> "Comma";
                case 0x003C -> "Period";
                case 0x003D -> "Slash";
                case 0x003E -> "ShiftRight";
                case 0x003F -> "NumpadMultiply";
                case 0x0040 -> "AltLeft";
                case 0x0041 -> "Space";
                case 0x0042 -> "CapsLock";
                case 0x0043 -> "F1";
                case 0x0044 -> "F2";
                case 0x0045 -> "F3";
                case 0x0046 -> "F4";
                case 0x0047 -> "F5";
                case 0x0048 -> "F6";
                case 0x0049 -> "F7";
                case 0x004A -> "F8";
                case 0x004B -> "F9";
                case 0x004C -> "F10";
                case 0x004D -> "NumLock";
                case 0x004E -> "ScrollLock";
                case 0x004F -> "Numpad7";
                case 0x0050 -> "Numpad8";
                case 0x0051 -> "Numpad9";
                case 0x0052 -> "NumpadSubtract";
                case 0x0053 -> "Numpad4";
                case 0x0054 -> "Numpad5";
                case 0x0055 -> "Numpad6";
                case 0x0056 -> "NumpadAdd";
                case 0x0057 -> "Numpad1";
                case 0x0058 -> "Numpad2";
                case 0x0059 -> "Numpad3";
                case 0x005A -> "Numpad0";
                case 0x005B -> "NumpadDecimal";
                case 0x005D -> "Lang5";
                case 0x005E -> "IntlBackslash";
                case 0x005F -> "F11";
                case 0x0060 -> "F12";
                case 0x0061 -> "IntlRo";
                case 0x0062 -> "Lang3";
                case 0x0063 -> "Lang4";
                case 0x0064 -> "Convert";
                case 0x0065 -> "KanaMode";
                case 0x0066 -> "NonConvert";
                case 0x0068 -> "NumpadEnter";
                case 0x0069 -> "ControlRight";
                case 0x006A -> "NumpadDivide";
                case 0x006B -> "PrintScreen";
                case 0x006C -> "AltRight";
                case 0x006E -> "Home";
                case 0x006F -> "ArrowUp";
                case 0x0070 -> "PageUp";
                case 0x0071 -> "ArrowLeft";
                case 0x0072 -> "ArrowRight";
                case 0x0073 -> "End";
                case 0x0074 -> "ArrowDown";
                case 0x0075 -> "PageDown";
                case 0x0076 -> "Insert";
                case 0x0077 -> "Delete";
                case 0x0079 -> "AudioVolumeMute";
                case 0x007A -> "AudioVolumeDown";
                case 0x007B -> "AudioVolumeUp";
                case 0x007D -> "NumpadEqual";
                case 0x007F -> "Pause";
                case 0x0081 -> "NumpadComma";
                case 0x0082 -> "Lang1";
                case 0x0083 -> "Lang2";
                case 0x0084 -> "IntlYen";
                case 0x0085 -> "MetaLeft";
                case 0x0086 -> "MetaRight";
                case 0x0087 -> "ContextMenu";
                case 0x0088 -> "BrowserStop";
                case 0x0089 -> "Again";
                case 0x008A -> "Props";
                case 0x008B -> "Undo";
                case 0x008C -> "Select";
                case 0x008D -> "Copy";
                case 0x008E -> "Open";
                case 0x008F -> "Paste";
                case 0x0090 -> "Find";
                case 0x0091 -> "Cut";
                case 0x0092 -> "Help";
                case 0x0094 -> "LaunchApp2";
                case 0x0096 -> "Sleep";
                case 0x0097 -> "WakeUp";
                case 0x00A3 -> "LaunchMail";
                case 0x00A4 -> "BrowserFavorites";
                case 0x00A6 -> "BrowserBack";
                case 0x00A7 -> "BrowserForward";
                case 0x00A9 -> "Eject";
                case 0x00AB -> "MediaTrackNext";
                case 0x00AC -> "MediaPlayPause";
                case 0x00AD -> "MediaTrackPrevious";
                case 0x00AE -> "MediaStop";
                case 0x00B3 -> "MediaSelect";
                case 0x00B4 -> "BrowserHome";
                case 0x00B5 -> "BrowserRefresh";
                case 0x00BB -> "NumpadParenLeft";
                case 0x00BC -> "NumpadParenRight";
                case 0x00BF -> "F13";
                case 0x00C0 -> "F14";
                case 0x00C1 -> "F15";
                case 0x00C2 -> "F16";
                case 0x00C3 -> "F17";
                case 0x00C4 -> "F18";
                case 0x00C5 -> "F19";
                case 0x00C6 -> "F20";
                case 0x00C7 -> "F21";
                case 0x00C8 -> "F22";
                case 0x00C9 -> "F23";
                case 0x00CA -> "F24";
                case 0x00E1 -> "BrowserSearch";
                default -> null;
            };
        }
    };

    private static final KeyParser KEY_PARSER;
    static {
        if (SystemUtils.IS_WINDOWS) KEY_PARSER = WINDOWS_KEY_PARSER;
        else if (SystemUtils.IS_MAC) KEY_PARSER = MAC_KEY_PARSER;
        else KEY_PARSER = X11_KEY_PARSER;
    }

    public static String parseKey(KeyEvent event) {
        return KEY_PARSER.parseKey(event);
    }

    public static String parseCode(KeyEvent event) {
        return KEY_PARSER.parseCode(event);
    }

}
