package unrefined.desktop;

import unrefined.internal.OperatingSystem;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

public final class KeyEventParser {

    private KeyEventParser() {
        throw new NotInstantiableError(KeyEventParser.class);
    }

    private static final Field rawCodeField;
    private static final Field scancodeField;
    static {
        try {
            rawCodeField = KeyEvent.class.getDeclaredField("rawCode");
            if (OperatingSystem.IS_WINDOWS) scancodeField = KeyEvent.class.getDeclaredField("scancode");
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

    private interface Parser {
        String parseKey(KeyEvent event);
        String parseCode(KeyEvent event);
    }

    private static final Parser WINDOWS_PARSER = new Parser() {
        @Override
        public String parseKey(KeyEvent event) {
            switch (getRawCode(event)) {
                case 0x12: case 0xA4: case 0xA5: return "Alt";
                case 0x14: return "CapsLock";
                case 0x11: case 0xA2: case 0xA3: return "Control";
                case 0x5B: case 0x5C: return "Meta";
                case 0x90: return "NumLock";
                case 0x91: return "ScrollLock";
                case 0x10: case 0xA0: case 0xA1: return "Shift";
                case 0x0D: return "Enter";
                case 0x09: return "Tab";
                case 0x20: return " ";
                case 0x28: return "ArrowDown";
                case 0x25: return "ArrowLeft";
                case 0x27: return "ArrowRight";
                case 0x26: return "ArrowUp";
                case 0x23: return "End";
                case 0x24: return "Home";
                case 0x22: return "PageDown";
                case 0x21: return "PageUp";
                case 0x08: return "Backspace";
                case 0x0C: case 0xFE: return "Clear";
                case 0xF7: return "CrSel";
                case 0x2E: return "Delete";
                case 0xF9: return "EraseEof";
                case 0xF8: return "ExSel";
                case 0x2D: return "Insert";
                case 0x1E: return "Accept";
                case 0x5D: return "ContextMenu";
                case 0x1B: return "Escape";
                case 0x2B: return "Execute";
                case 0x2F: return "Help";
                case 0x13: return "Pause";
                case 0xFA: return "Play";
                case 0x29: return "Select";
                case 0x2C: return "PrintScreen";
                case 0x5F: return "Standby";
                case 0xF0: return "Alphanumeric";
                case 0x1C: return "Convert";
                case 0x18: return "FinalMode";
                case 0x1F: return "ModeChange";
                case 0x1D: return "NonConvert";
                case 0xE5: return "Process";
                case 0x17: return "JunjaMode";
                case 0xF3: return "Hankaku";
                case 0xF2: return "Hiragana";
                case 0x15: case 0xF6: return "KanaMode";
                case 0x19: return "KanjiMode";
                case 0xF1: return "Katakana";
                case 0xF5: return "Romaji";
                case 0xF4: return "Zenkaku";
                case 0x70: return "F1";
                case 0x71: return "F2";
                case 0x72: return "F3";
                case 0x73: return "F4";
                case 0x74: return "F5";
                case 0x75: return "F6";
                case 0x76: return "F7";
                case 0x77: return "F8";
                case 0x78: return "F9";
                case 0x79: return "F10";
                case 0x7A: return "F11";
                case 0x7B: return "F12";
                case 0x7C: return "F13";
                case 0x7D: return "F14";
                case 0x7E: return "F15";
                case 0x7F: return "F16";
                case 0x80: return "F17";
                case 0x81: return "F18";
                case 0x82: return "F19";
                case 0x83: return "F20";
                case 0xB3: return "MediaPlayPause";
                case 0xB2: return "MediaStop";
                case 0xB0: return "MediaTrackNext";
                case 0xB1: return "MediaTrackPrevious";
                case 0xAE: return "AudioVolumeDown";
                case 0xAD: return "AudioVolumeMute";
                case 0xAF: return "AudioVolumeUp";
                case 0xFB: return "ZoomToggle";
                case 0xB4: return "LaunchMail";
                case 0xB5: return "LaunchMediaPlayer";
                case 0xB6: return "LaunchApplication1";
                case 0xB7: return "LaunchApplication2";
                case 0xA6: return "BrowserBack";
                case 0xAB: return "BrowserFavorites";
                case 0xA7: return "BrowserForward";
                case 0xAC: return "BrowserHome";
                case 0xA8: return "BrowserRefresh";
                case 0xAA: return "BrowserSearch";
                case 0xA9: return "BrowserStop";
                case 0x6A: return "*";
                case 0x6B: return "+";
                case 0x6F: return "/";
                case 0x6D: return "-";
                case 0x6C: return "Separator";
                default: return event.getKeyChar() == KeyEvent.CHAR_UNDEFINED ? null : String.valueOf(event.getKeyChar());
            }
        }
        @Override
        public String parseCode(KeyEvent event) {
            switch (getScancode(event)) {
                case 0x0001: return "Escape";
                case 0x0002: return "Digit1";
                case 0x0003: return "Digit2";
                case 0x0004: return "Digit3";
                case 0x0005: return "Digit4";
                case 0x0006: return "Digit5";
                case 0x0007: return "Digit6";
                case 0x0008: return "Digit7";
                case 0x0009: return "Digit8";
                case 0x000A: return "Digit9";
                case 0x000B: return "Digit0";
                case 0x000C: return "Minus";
                case 0x000D: return "Equal";
                case 0x000E: return "Backspace";
                case 0x000F: return "Tab";
                case 0x0010: return "KeyQ";
                case 0x0011: return "KeyW";
                case 0x0012: return "KeyE";
                case 0x0013: return "KeyR";
                case 0x0014: return "KeyT";
                case 0x0015: return "KeyY";
                case 0x0016: return "KeyU";
                case 0x0017: return "KeyI";
                case 0x0018: return "KeyO";
                case 0x0019: return "KeyP";
                case 0x001A: return "BracketLeft";
                case 0x001B: return "BracketRight";
                case 0x001C: return "Enter";
                case 0x001D: return "ControlLeft";
                case 0x001E: return "KeyA";
                case 0x001F: return "KeyS";
                case 0x0020: return "KeyD";
                case 0x0021: return "KeyF";
                case 0x0022: return "KeyG";
                case 0x0023: return "KeyH";
                case 0x0024: return "KeyJ";
                case 0x0025: return "KeyK";
                case 0x0026: return "KeyL";
                case 0x0027: return "Semicolon";
                case 0x0028: return "Quote";
                case 0x0029: return "Backquote";
                case 0x002A: return "ShiftLeft";
                case 0x002B: return "Backslash";
                case 0x002C: return "KeyZ";
                case 0x002D: return "KeyX";
                case 0x002E: return "KeyC";
                case 0x002F: return "KeyV";
                case 0x0030: return "KeyB";
                case 0x0031: return "KeyN";
                case 0x0032: return "KeyM";
                case 0x0033: return "Comma";
                case 0x0034: return "Period";
                case 0x0035: return "Slash";
                case 0x0036: return "ShiftRight";
                case 0x0037: return "NumpadMultiply";
                case 0x0038: return "AltLeft";
                case 0x0039: return "Space";
                case 0x003A: return "CapsLock";
                case 0x003B: return "F1";
                case 0x003C: return "F2";
                case 0x003D: return "F3";
                case 0x003E: return "F4";
                case 0x003F: return "F5";
                case 0x0040: return "F6";
                case 0x0041: return "F7";
                case 0x0042: return "F8";
                case 0x0043: return "F9";
                case 0x0044: return "F10";
                case 0x0045: return "Pause";
                case 0x0046: return "ScrollLock";
                case 0x0047: return "Numpad7";
                case 0x0048: return "Numpad8";
                case 0x0049: return "Numpad9";
                case 0x004A: return "NumpadSubtract";
                case 0x004B: return "Numpad4";
                case 0x004C: return "Numpad5";
                case 0x004D: return "Numpad6";
                case 0x004E: return "NumpadAdd";
                case 0x004F: return "Numpad1";
                case 0x0050: return "Numpad2";
                case 0x0051: return "Numpad3";
                case 0x0052: return "Numpad0";
                case 0x0053: return "NumpadDecimal";
                case 0x0054: return "PrintScreen";
                case 0x0056: return "IntlBackslash";
                case 0x0057: return "F11";
                case 0x0058: return "F12";
                case 0x0059: return "NumpadEqual";
                case 0x0064: return "F13";
                case 0x0065: return "F14";
                case 0x0066: return "F15";
                case 0x0067: return "F16";
                case 0x0068: return "F17";
                case 0x0069: return "F18";
                case 0x006A: return "F19";
                case 0x006B: return "F20";
                case 0x006C: return "F21";
                case 0x006D: return "F22";
                case 0x006E: return "F23";
                case 0x0070: return "KanaMode";
                case 0x0071: return "Lang2";
                case 0x0072: return "Lang1";
                case 0x0073: return "IntlRo";
                case 0x0076: return "F24";
                case 0x0077: return "Lang4";
                case 0x0078: return "Lang3";
                case 0x0079: return "Convert";
                case 0x007B: return "NonConvert";
                case 0x007D: return "IntlYen";
                case 0x007E: return "NumpadComma";
                case 0xE008: return "Undo";
                case 0xE00A: return "Paste";
                case 0xE010: return "MediaTrackPrevious";
                case 0xE017: return "Cut";
                case 0xE018: return "Copy";
                case 0xE019: return "MediaTrackNext";
                case 0xE01C: return "NumpadEnter";
                case 0xE01D: return "ControlRight";
                case 0xE020: return "AudioVolumeMute";
                case 0xE021: return "LaunchApp2";
                case 0xE022: return "MediaPlayPause";
                case 0xE024: return "MediaStop";
                case 0xE02C: return "Eject";
                case 0xE02E: return "AudioVolumeDown";
                case 0xE030: return "AudioVolumeUp";
                case 0xE032: return "BrowserHome";
                case 0xE035: return "NumpadDivide";
                case 0xE037: return "PrintScreen";
                case 0xE038: return "AltRight";
                case 0xE03B: return "Help";
                case 0xE045: return "NumLock";
                case 0xE046: return "Pause";
                case 0xE047: return "Home";
                case 0xE048: return "ArrowUp";
                case 0xE049: return "PageUp";
                case 0xE04B: return "ArrowLeft";
                case 0xE04D: return "ArrowRight";
                case 0xE04F: return "End";
                case 0xE050: return "ArrowDown";
                case 0xE051: return "PageDown";
                case 0xE052: return "Insert";
                case 0xE053: return "Delete";
                case 0xE05B: return "MetaLeft";
                case 0xE05C: return "MetaRight";
                case 0xE05D: return "ContextMenu";
                case 0xE05E: return "Power";
                case 0xE05F: return "Sleep";
                case 0xE063: return "WakeUp";
                case 0xE065: return "BrowserSearch";
                case 0xE066: return "BrowserFavorites";
                case 0xE067: return "BrowserRefresh";
                case 0xE068: return "BrowserStop";
                case 0xE069: return "BrowserForward";
                case 0xE06A: return "BrowserBack";
                case 0xE06B: return "LaunchApp1";
                case 0xE06C: return "LaunchMail";
                case 0xE06D: return "MediaSelect";
                case 0xE0F1: return "Lang2";
                case 0xE0F2: return "Lang1";
                default: return null;
            }
        }
    };

    private static final Parser MAC_PARSER = new Parser() {
        @Override
        public String parseKey(KeyEvent event) {
            switch (getRawCode(event)) {
                case 0x3A: case 0x3D: return "Alt";
                case 0x39: return "CapsLock";
                case 0x3B: case 0x3E: return "Control";
                case 0x3F: return "Fn";
                case 0x37: case 0x36: return "Meta";
                case 0x38: case 0x3C: return "Shift";
                case 0x24: case 0x4C: case 0x34: return "Enter";
                case 0x30: return "Tab";
                case 0x31: return " ";
                case 0x7D: return "ArrowDown";
                case 0x7B: return "ArrowLeft";
                case 0x7C: return "ArrowRight";
                case 0x7E: return "ArrowUp";
                case 0x77: return "End";
                case 0x73: return "Home";
                case 0x79: return "PageDown";
                case 0x74: return "PageUp";
                case 0x33: return "Backspace";
                case 0x47: return "Clear";
                case 0x75: return "Delete";
                case 0x6E: return "ContextMenu";
                case 0x35: return "Escape";
                case 0x72: return "Help";
                case 0x66: return "Eisu";
                case 0x68: return "KanjiMode";
                case 0x7A: return "F1";
                case 0x78: return "F2";
                case 0x63: return "F3";
                case 0x76: return "F4";
                case 0x60: return "F5";
                case 0x61: return "F6";
                case 0x62: return "F7";
                case 0x64: return "F8";
                case 0x65: return "F9";
                case 0x6D: return "F10";
                case 0x67: return "F11";
                case 0x6F: return "F12";
                case 0x69: return "F13";
                case 0x6B: return "F14";
                case 0x71: return "F15";
                case 0x6A: return "F16";
                case 0x40: return "F17";
                case 0x4F: return "F18";
                case 0x50: return "F19";
                case 0x5A: return "F20";
                case 0x49: return "AudioVolumeDown";
                case 0x4A: return "AudioVolumeMute";
                case 0x48: return "AudioVolumeUp";
                case 0x43: return "*";
                case 0x45: return "+";
                case 0x4B: return "/";
                case 0x4E: return "-";
                case 0x5F: return "Separator";
                default: return event.getKeyChar() == KeyEvent.CHAR_UNDEFINED ? null : String.valueOf(event.getKeyChar());
            }
        }

        @Override
        public String parseCode(KeyEvent event) {
            switch (getRawCode(event)) {
                case 0x00: return "KeyA";
                case 0x01: return "KeyS";
                case 0x02: return "KeyD";
                case 0x03: return "KeyF";
                case 0x04: return "KeyH";
                case 0x05: return "KeyG";
                case 0x06: return "KeyZ";
                case 0x07: return "KeyX";
                case 0x08: return "KeyC";
                case 0x09: return "KeyV";
                case 0x0A: return "IntlBackslash";
                case 0x0B: return "KeyB";
                case 0x0C: return "KeyQ";
                case 0x0D: return "KeyW";
                case 0x0E: return "KeyE";
                case 0x0F: return "KeyR";
                case 0x10: return "KeyY";
                case 0x11: return "KeyT";
                case 0x12: return "Digit1";
                case 0x13: return "Digit2";
                case 0x14: return "Digit3";
                case 0x15: return "Digit4";
                case 0x16: return "Digit6";
                case 0x17: return "Digit5";
                case 0x18: return "Equal";
                case 0x19: return "Digit9";
                case 0x1A: return "Digit7";
                case 0x1B: return "Minus";
                case 0x1C: return "Digit8";
                case 0x1D: return "Digit0";
                case 0x1E: return "BracketRight";
                case 0x1F: return "KeyO";
                case 0x20: return "KeyU";
                case 0x21: return "BracketLeft";
                case 0x22: return "KeyI";
                case 0x23: return "KeyP";
                case 0x24: return "Enter";
                case 0x25: return "KeyL";
                case 0x26: return "KeyJ";
                case 0x27: return "Quote";
                case 0x28: return "KeyK";
                case 0x29: return "Semicolon";
                case 0x2A: return "Backslash";
                case 0x2B: return "Comma";
                case 0x2C: return "Slash";
                case 0x2D: return "KeyN";
                case 0x2E: return "KeyM";
                case 0x2F: return "Period";
                case 0x30: return "Tab";
                case 0x31: return "Space";
                case 0x32: return "Backquote";
                case 0x33: return "Backspace";
                case 0x34: return "NumpadEnter";
                case 0x35: return "Escape";
                case 0x36: return "MetaRight";
                case 0x37: return "MetaLeft";
                case 0x38: return "ShiftLeft";
                case 0x39: return "CapsLock";
                case 0x3A: return "AltLeft";
                case 0x3B: return "ControlLeft";
                case 0x3C: return "ShiftRight";
                case 0x3D: return "AltRight";
                case 0x3E: return "ControlRight";
                case 0x3F: return "Fn";
                case 0x40: return "F17";
                case 0x41: return "NumpadDecimal";
                case 0x43: return "NumpadMultiply";
                case 0x45: return "NumpadAdd";
                case 0x47: return "NumLock";
                case 0x48: return "AudioVolumeUp";
                case 0x49: return "AudioVolumeDown";
                case 0x4A: return "AudioVolumeMute";
                case 0x4B: return "NumpadDivide";
                case 0x4C: return "NumpadEnter";
                case 0x4E: return "NumpadSubtract";
                case 0x4F: return "F18";
                case 0x50: return "F19";
                case 0x51: return "NumpadEqual";
                case 0x52: return "Numpad0";
                case 0x53: return "Numpad1";
                case 0x54: return "Numpad2";
                case 0x55: return "Numpad3";
                case 0x56: return "Numpad4";
                case 0x57: return "Numpad5";
                case 0x58: return "Numpad6";
                case 0x59: return "Numpad7";
                case 0x5A: return "F20";
                case 0x5B: return "Numpad8";
                case 0x5C: return "Numpad9";
                case 0x5D: return "IntlYen";
                case 0x5E: return "IntlRo";
                case 0x5F: return "NumpadComma";
                case 0x60: return "F5";
                case 0x61: return "F6";
                case 0x62: return "F7";
                case 0x63: return "F3";
                case 0x64: return "F8";
                case 0x65: return "F9";
                case 0x66: return "Lang2";
                case 0x67: return "F11";
                case 0x68: return "Lang1";
                case 0x69: return "F13";
                case 0x6A: return "F16";
                case 0x6B: return "F14";
                case 0x6D: return "F10";
                case 0x6E: return "ContextMenu";
                case 0x6F: return "F12";
                case 0x71: return "F15";
                case 0x72: return "Help";
                case 0x73: return "Home";
                case 0x74: return "PageUp";
                case 0x75: return "Delete";
                case 0x76: return "F4";
                case 0x77: return "End";
                case 0x78: return "F2";
                case 0x79: return "PageDown";
                case 0x7A: return "F1";
                case 0x7B: return "ArrowLeft";
                case 0x7C: return "ArrowRight";
                case 0x7D: return "ArrowDown";
                case 0x7E: return "ArrowUp";
                default: return null;
            }
        }
    };

    private static final Parser X11_PARSER = new Parser() {
        @Override
        public String parseKey(KeyEvent event) {
            switch (event.getExtendedKeyCode()) {
                case KeyEvent.VK_ALT: return "Alt";
                case KeyEvent.VK_ALT_GRAPH: return "AltGraph";
                case KeyEvent.VK_CAPS_LOCK: return "CapsLock";
                case KeyEvent.VK_CONTROL: return "Control";
                case KeyEvent.VK_WINDOWS: return "Meta";
                case KeyEvent.VK_NUM_LOCK: return "NumLock";
                case KeyEvent.VK_SCROLL_LOCK: return "ScrollLock";
                case KeyEvent.VK_SHIFT: return "Shift";
                case KeyEvent.VK_ENTER: return "Enter";
                case KeyEvent.VK_TAB: return "Tab";
                case KeyEvent.VK_SPACE: return " ";
                case KeyEvent.VK_DOWN: case KeyEvent.VK_KP_DOWN: return "ArrowDown";
                case KeyEvent.VK_LEFT: case KeyEvent.VK_KP_LEFT: return "ArrowLeft";
                case KeyEvent.VK_RIGHT: case KeyEvent.VK_KP_RIGHT: return "ArrowRight";
                case KeyEvent.VK_UP: case KeyEvent.VK_KP_UP: return "ArrowUp";
                case KeyEvent.VK_END: return "End";
                case KeyEvent.VK_HOME: return "Home";
                case KeyEvent.VK_PAGE_DOWN: return "PageDown";
                case KeyEvent.VK_PAGE_UP: return "PageUp";
                case KeyEvent.VK_BACK_SPACE: return "Backspace";
                case KeyEvent.VK_CLEAR: return "Clear";
                case KeyEvent.VK_COPY: return "Copy";
                case KeyEvent.VK_CUT: return "Cut";
                case KeyEvent.VK_DELETE: return "Delete";
                case KeyEvent.VK_INSERT: return "Insert";
                case KeyEvent.VK_PASTE: return "Paste";
                case KeyEvent.VK_UNDO: return "Undo";
                case KeyEvent.VK_ACCEPT: return "Accept";
                case KeyEvent.VK_AGAIN: return "Again";
                case KeyEvent.VK_CANCEL: return "Cancel";
                case KeyEvent.VK_CONTEXT_MENU: return "ContextMenu";
                case KeyEvent.VK_ESCAPE: return "Escape";
                case KeyEvent.VK_FIND: return "Find";
                case KeyEvent.VK_HELP: return "Help";
                case KeyEvent.VK_PAUSE: return "Pause";
                case KeyEvent.VK_PROPS: return "Props";
                case KeyEvent.VK_PRINTSCREEN: return "PrintScreen";
                case KeyEvent.VK_ALL_CANDIDATES: return "AllCandidates";
                case KeyEvent.VK_ALPHANUMERIC: return "Alphanumeric";
                case KeyEvent.VK_CODE_INPUT: return "CodeInput";
                case KeyEvent.VK_COMPOSE: return "Compose";
                case KeyEvent.VK_CONVERT: return "Convert";
                /*
                case KeyEvent.VK_DEAD_ABOVEDOT:
                case KeyEvent.VK_DEAD_ABOVERING:
                case KeyEvent.VK_DEAD_ACUTE:
                case KeyEvent.VK_DEAD_BREVE:
                case KeyEvent.VK_DEAD_CARON:
                case KeyEvent.VK_DEAD_CEDILLA:
                case KeyEvent.VK_DEAD_CIRCUMFLEX:
                case KeyEvent.VK_DEAD_DIAERESIS:
                case KeyEvent.VK_DEAD_DOUBLEACUTE:
                case KeyEvent.VK_DEAD_GRAVE:
                case KeyEvent.VK_DEAD_IOTA:
                case KeyEvent.VK_DEAD_MACRON:
                case KeyEvent.VK_DEAD_OGONEK:
                case KeyEvent.VK_DEAD_SEMIVOICED_SOUND:
                case KeyEvent.VK_DEAD_TILDE:
                case KeyEvent.VK_DEAD_VOICED_SOUND: return "Dead";
                 */
                /*
                case KeyEvent.VK_DEAD_GRAVE: return "`";
                case KeyEvent.VK_DEAD_ACUTE: return "´";
                case KeyEvent.VK_DEAD_CIRCUMFLEX: return "ˆ";
                case KeyEvent.VK_DEAD_TILDE: return "˜";
                case KeyEvent.VK_DEAD_MACRON: return "¯";
                case KeyEvent.VK_DEAD_BREVE: return "˘";
                case KeyEvent.VK_DEAD_ABOVEDOT: return "˙";
                case KeyEvent.VK_DEAD_DIAERESIS: return "¨";
                case KeyEvent.VK_DEAD_ABOVERING: return "˚";
                case KeyEvent.VK_DEAD_DOUBLEACUTE: return "˝";
                case KeyEvent.VK_DEAD_CARON: return "ˇ";
                case KeyEvent.VK_DEAD_CEDILLA: return "¸";
                case KeyEvent.VK_DEAD_OGONEK: return "˛";
                case KeyEvent.VK_DEAD_IOTA: return "ͅ";
                case KeyEvent.VK_DEAD_VOICED_SOUND: return "゙";
                case KeyEvent.VK_DEAD_SEMIVOICED_SOUND: return "゚";
                 */
                case KeyEvent.VK_FINAL: return "FinalMode";
                case KeyEvent.VK_MODECHANGE: return "ModeChange";
                case KeyEvent.VK_NONCONVERT: return "NonConvert";
                case KeyEvent.VK_PREVIOUS_CANDIDATE: return "PreviousCandidate";
                case KeyEvent.VK_HIRAGANA: case KeyEvent.VK_JAPANESE_HIRAGANA: return "Hiragana";
                case KeyEvent.VK_KANA: case KeyEvent.VK_KANA_LOCK: return "KanaMode";
                case KeyEvent.VK_KANJI: case KeyEvent.VK_INPUT_METHOD_ON_OFF: return "KanjiMode";
                case KeyEvent.VK_KATAKANA: case KeyEvent.VK_JAPANESE_KATAKANA: return "Katakana";
                case KeyEvent.VK_ROMAN_CHARACTERS: case KeyEvent.VK_JAPANESE_ROMAN: return "Romaji";
                case KeyEvent.VK_FULL_WIDTH: return "Zenkaku";
                case KeyEvent.VK_HALF_WIDTH: return "Hankaku";
                case KeyEvent.VK_F1: return "F1";
                case KeyEvent.VK_F2: return "F2";
                case KeyEvent.VK_F3: return "F3";
                case KeyEvent.VK_F4: return "F4";
                case KeyEvent.VK_F5: return "F5";
                case KeyEvent.VK_F6: return "F6";
                case KeyEvent.VK_F7: return "F7";
                case KeyEvent.VK_F8: return "F8";
                case KeyEvent.VK_F9: return "F9";
                case KeyEvent.VK_F10: return "F10";
                case KeyEvent.VK_F11: return "F11";
                case KeyEvent.VK_F12: return "F12";
                case KeyEvent.VK_F13: return "F13";
                case KeyEvent.VK_F14: return "F14";
                case KeyEvent.VK_F15: return "F15";
                case KeyEvent.VK_F16: return "F16";
                case KeyEvent.VK_F17: return "F17";
                case KeyEvent.VK_F18: return "F18";
                case KeyEvent.VK_F19: return "F19";
                case KeyEvent.VK_F20: return "F20";
                case KeyEvent.VK_F21: return "Soft1";
                case KeyEvent.VK_F22: return "Soft2";
                case KeyEvent.VK_F23: return "Soft3";
                case KeyEvent.VK_F24: return "Soft4";
                case KeyEvent.VK_NUMPAD0: return "0";
                case KeyEvent.VK_NUMPAD1: return "1";
                case KeyEvent.VK_NUMPAD2: return "2";
                case KeyEvent.VK_NUMPAD3: return "3";
                case KeyEvent.VK_NUMPAD4: return "4";
                case KeyEvent.VK_NUMPAD5: return "5";
                case KeyEvent.VK_NUMPAD6: return "6";
                case KeyEvent.VK_NUMPAD7: return "7";
                case KeyEvent.VK_NUMPAD8: return "8";
                case KeyEvent.VK_NUMPAD9: return "9";
                case KeyEvent.VK_MULTIPLY: return "*";
                case KeyEvent.VK_ADD: return "+";
                case KeyEvent.VK_SUBTRACT: return "-";
                case KeyEvent.VK_DIVIDE: return "/";
                case KeyEvent.VK_SEPARATOR: return "Separator";
                default: return event.getKeyChar() == KeyEvent.CHAR_UNDEFINED ? parseCode(event) : String.valueOf(event.getKeyChar());
            }
        }
        @Override
        public String parseCode(KeyEvent event) {
            switch (getRawCode(event)) {
                case 0x0009: return "Escape";
                case 0x000A: return "Digit1";
                case 0x000B: return "Digit2";
                case 0x000C: return "Digit3";
                case 0x000D: return "Digit4";
                case 0x000E: return "Digit5";
                case 0x000F: return "Digit6";
                case 0x0010: return "Digit7";
                case 0x0011: return "Digit8";
                case 0x0012: return "Digit9";
                case 0x0013: return "Digit0";
                case 0x0014: return "Minus";
                case 0x0015: return "Equal";
                case 0x0016: return "Backspace";
                case 0x0017: return "Tab";
                case 0x0018: return "KeyQ";
                case 0x0019: return "KeyW";
                case 0x001A: return "KeyE";
                case 0x001B: return "KeyR";
                case 0x001C: return "KeyT";
                case 0x001D: return "KeyY";
                case 0x001E: return "KeyU";
                case 0x001F: return "KeyI";
                case 0x0020: return "KeyO";
                case 0x0021: return "KeyP";
                case 0x0022: return "BracketLeft";
                case 0x0023: return "BracketRight";
                case 0x0024: return "Enter";
                case 0x0025: return "ControlLeft";
                case 0x0026: return "KeyA";
                case 0x0027: return "KeyS";
                case 0x0028: return "KeyD";
                case 0x0029: return "KeyF";
                case 0x002A: return "KeyG";
                case 0x002B: return "KeyH";
                case 0x002C: return "KeyJ";
                case 0x002D: return "KeyK";
                case 0x002E: return "KeyL";
                case 0x002F: return "Semicolon";
                case 0x0030: return "Quote";
                case 0x0031: return "Backquote";
                case 0x0032: return "ShiftLeft";
                case 0x0033: return "Backslash";
                case 0x0034: return "KeyZ";
                case 0x0035: return "KeyX";
                case 0x0036: return "KeyC";
                case 0x0037: return "KeyV";
                case 0x0038: return "KeyB";
                case 0x0039: return "KeyN";
                case 0x003A: return "KeyM";
                case 0x003B: return "Comma";
                case 0x003C: return "Period";
                case 0x003D: return "Slash";
                case 0x003E: return "ShiftRight";
                case 0x003F: return "NumpadMultiply";
                case 0x0040: return "AltLeft";
                case 0x0041: return "Space";
                case 0x0042: return "CapsLock";
                case 0x0043: return "F1";
                case 0x0044: return "F2";
                case 0x0045: return "F3";
                case 0x0046: return "F4";
                case 0x0047: return "F5";
                case 0x0048: return "F6";
                case 0x0049: return "F7";
                case 0x004A: return "F8";
                case 0x004B: return "F9";
                case 0x004C: return "F10";
                case 0x004D: return "NumLock";
                case 0x004E: return "ScrollLock";
                case 0x004F: return "Numpad7";
                case 0x0050: return "Numpad8";
                case 0x0051: return "Numpad9";
                case 0x0052: return "NumpadSubtract";
                case 0x0053: return "Numpad4";
                case 0x0054: return "Numpad5";
                case 0x0055: return "Numpad6";
                case 0x0056: return "NumpadAdd";
                case 0x0057: return "Numpad1";
                case 0x0058: return "Numpad2";
                case 0x0059: return "Numpad3";
                case 0x005A: return "Numpad0";
                case 0x005B: return "NumpadDecimal";
                case 0x005D: return "Lang5";
                case 0x005E: return "IntlBackslash";
                case 0x005F: return "F11";
                case 0x0060: return "F12";
                case 0x0061: return "IntlRo";
                case 0x0062: return "Lang3";
                case 0x0063: return "Lang4";
                case 0x0064: return "Convert";
                case 0x0065: return "KanaMode";
                case 0x0066: return "NonConvert";
                case 0x0068: return "NumpadEnter";
                case 0x0069: return "ControlRight";
                case 0x006A: return "NumpadDivide";
                case 0x006B: return "PrintScreen";
                case 0x006C: return "AltRight";
                case 0x006E: return "Home";
                case 0x006F: return "ArrowUp";
                case 0x0070: return "PageUp";
                case 0x0071: return "ArrowLeft";
                case 0x0072: return "ArrowRight";
                case 0x0073: return "End";
                case 0x0074: return "ArrowDown";
                case 0x0075: return "PageDown";
                case 0x0076: return "Insert";
                case 0x0077: return "Delete";
                case 0x0079: return "AudioVolumeMute";
                case 0x007A: return "AudioVolumeDown";
                case 0x007B: return "AudioVolumeUp";
                case 0x007D: return "NumpadEqual";
                case 0x007F: return "Pause";
                case 0x0081: return "NumpadComma";
                case 0x0082: return "Lang1";
                case 0x0083: return "Lang2";
                case 0x0084: return "IntlYen";
                case 0x0085: return "MetaLeft";
                case 0x0086: return "MetaRight";
                case 0x0087: return "ContextMenu";
                case 0x0088: return "BrowserStop";
                case 0x0089: return "Again";
                case 0x008A: return "Props";
                case 0x008B: return "Undo";
                case 0x008C: return "Select";
                case 0x008D: return "Copy";
                case 0x008E: return "Open";
                case 0x008F: return "Paste";
                case 0x0090: return "Find";
                case 0x0091: return "Cut";
                case 0x0092: return "Help";
                case 0x0094: return "LaunchApp2";
                case 0x0096: return "Sleep";
                case 0x0097: return "WakeUp";
                case 0x00A3: return "LaunchMail";
                case 0x00A4: return "BrowserFavorites";
                case 0x00A6: return "BrowserBack";
                case 0x00A7: return "BrowserForward";
                case 0x00A9: return "Eject";
                case 0x00AB: return "MediaTrackNext";
                case 0x00AC: return "MediaPlayPause";
                case 0x00AD: return "MediaTrackPrevious";
                case 0x00AE: return "MediaStop";
                case 0x00B3: return "MediaSelect";
                case 0x00B4: return "BrowserHome";
                case 0x00B5: return "BrowserRefresh";
                case 0x00BB: return "NumpadParenLeft";
                case 0x00BC: return "NumpadParenRight";
                case 0x00BF: return "F13";
                case 0x00C0: return "F14";
                case 0x00C1: return "F15";
                case 0x00C2: return "F16";
                case 0x00C3: return "F17";
                case 0x00C4: return "F18";
                case 0x00C5: return "F19";
                case 0x00C6: return "F20";
                case 0x00C7: return "F21";
                case 0x00C8: return "F22";
                case 0x00C9: return "F23";
                case 0x00CA: return "F24";
                case 0x00E1: return "BrowserSearch";
                default: return null;
            }
        }
    };

    private static final Parser PARSER;
    static {
        if (OperatingSystem.IS_WINDOWS) PARSER = WINDOWS_PARSER;
        else if (OperatingSystem.IS_MAC) PARSER = MAC_PARSER;
        else PARSER = X11_PARSER;
    }

    public static String parseKey(KeyEvent event) {
        return PARSER.parseKey(event);
    }

    public static String parseCode(KeyEvent event) {
        return PARSER.parseCode(event);
    }

}
