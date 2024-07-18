package unrefined.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class HierarchicalProperties extends FilterProperties {

    private static final long serialVersionUID = 3409657164258296167L;

    public HierarchicalProperties(Properties properties) {
        super(properties);
    }

    public static void storeToIni(Properties properties, Writer writer, String comment) throws IOException {
        IniSupport.save(Objects.requireNonNull(properties), Objects.requireNonNull(writer), comment, false);
    }

    public static void storeToIni(Properties properties, OutputStream os, String comment) throws IOException {
        IniSupport.save(Objects.requireNonNull(properties), Objects.requireNonNull(os), comment, "ISO-8859-1");
    }

    public static void storeToIni(Properties properties, OutputStream os, String comment, Charset charset) throws IOException {
        IniSupport.save(Objects.requireNonNull(properties), Objects.requireNonNull(os), comment, charset == null ? "ISO-8859-1" : charset.displayName());
    }

    public static void storeToIni(Properties properties, OutputStream os, String comment, String encoding) throws IOException {
        IniSupport.save(Objects.requireNonNull(properties), Objects.requireNonNull(os), comment, encoding == null ? "ISO-8859-1" : encoding);
    }

    public static void loadFromIni(Properties properties, InputStream input) throws IOException {
        IniSupport.load(properties, new InputStreamReader(input, "ISO-8859-1"));
    }

    public static void loadFromIni(Properties properties, InputStream input, Charset charset) throws IOException {
        IniSupport.load(properties, new InputStreamReader(input, charset));
    }

    public static void loadFromIni(Properties properties, InputStream input, String encoding) throws IOException {
        IniSupport.load(properties, new InputStreamReader(input, encoding));
    }

    public static void mergeTree(Properties properties, String root, Properties tree) {
        for (String propertyName : tree.stringPropertyNames()) {
            properties.setProperty(root == null ? propertyName : (root + "." + propertyName),
                    tree.getProperty(propertyName));
        }
    }

    public static Properties removeTree(Properties properties, String tree) {
        Properties result = new Properties();
        if (tree == null) {
            for (String propertyName : properties.stringPropertyNames()) {
                result.setProperty(propertyName, properties.getProperty(propertyName));
            }
            properties.clear();
        }
        else {
            for (String propertyName : properties.stringPropertyNames()) {
                int dot = propertyName.lastIndexOf('.');
                if (dot == -1) continue;
                String parentName = propertyName.substring(0, dot);
                if (parentName.length() < tree.length()) continue;
                if (parentName.equals(tree))
                    result.setProperty(propertyName.substring(tree.length()), (String) properties.remove(propertyName));
            }
        }
        return result;
    }

    public static boolean containsTree(Properties properties, String tree) {
        if (tree == null) return true;
        else {
            for (String propertyName : properties.stringPropertyNames()) {
                int dot = propertyName.lastIndexOf('.');
                if (dot == -1) continue;
                String parentName = propertyName.substring(0, dot);
                if (parentName.length() < tree.length()) continue;
                if (parentName.equals(tree)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static Properties getTree(Properties properties, String tree) {
        Properties result = new Properties();
        if (tree == null) {
            for (String propertyName : properties.stringPropertyNames()) {
                result.put(propertyName, properties.getProperty(propertyName));
            }
        }
        else {
            for (String propertyName : properties.stringPropertyNames()) {
                int dot = propertyName.lastIndexOf('.');
                if (dot == -1) continue;
                String parentName = propertyName.substring(0, dot);
                if (parentName.length() < tree.length()) continue;
                if (parentName.equals(tree))
                    result.put(propertyName.substring(tree.length()), properties.getProperty(propertyName));
            }
        }
        return result;
    }

    public static int treeSize(Properties properties, String tree) {
        if (tree == null) return properties.size();
        else {
            int size = 0;
            for (String propertyName : properties.stringPropertyNames()) {
                int dot = propertyName.lastIndexOf('.');
                if (dot == -1) continue;
                String parentName = propertyName.substring(0, dot);
                if (parentName.length() < tree.length()) continue;
                if (parentName.equals(tree)) size ++;
            }
            return size;
        }
    }

    @Override
    public void store(Writer writer, String comments) throws IOException {
        IniSupport.save(this, Objects.requireNonNull(writer), comments, false);
    }

    @Override
    public void store(OutputStream out, String comments) throws IOException {
        IniSupport.save(this, out, comments, "ISO-8859-1");
    }

    @Override
    public synchronized void load(Reader reader) throws IOException {
        IniSupport.load(this, reader);
    }

    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        IniSupport.load(this, new InputStreamReader(inStream, "ISO-8859-1"));
    }

    public void mergeTree(String root, Properties tree) {
        mergeTree(this, root, tree);
    }

    public Properties removeTree(String tree) {
        return removeTree(this, tree);
    }

    public boolean containsTree(String tree) {
        return containsTree(this, tree);
    }

    public Properties getTree(String tree) {
        return getTree(this, tree);
    }

    public int treeSize(String tree) {
        return treeSize(this, tree);
    }

    private static final class IniSupport {

        private IniSupport() {
            throw new NotInstantiableError(IniSupport.class);
        }

        public static void save(Properties properties, OutputStream os, String comment, String encoding) throws IOException {
            save(properties, new OutputStreamWriter(os, encoding), comment, !encoding.toLowerCase(Locale.ENGLISH).startsWith("utf"));
        }

        public static void save(Properties properties, Writer writer, String comment, boolean escapeUnicode) throws IOException {
            if (!(writer instanceof BufferedWriter)) writer = new BufferedWriter(writer);
            if (comment != null) {
                writeComment(writer, comment);
                writer.write(System.lineSeparator());
            }

            Map<String, String> nosection = new HashMap<>();
            Map<String, Map<String, String>> sections = new HashMap<>();
            for (String propertyName : properties.stringPropertyNames()) {
                int dot = propertyName.lastIndexOf('.');
                if (dot == -1) nosection.put(propertyName, properties.getProperty(propertyName));
                else {
                    String sectionName = propertyName.substring(0, dot);
                    String keyName = propertyName.substring(dot + 1);
                    if (!sections.containsKey(sectionName)) sections.put(sectionName, new HashMap<>());
                    sections.get(sectionName).put(keyName, properties.getProperty(propertyName));
                }
            }

            if (!nosection.isEmpty()) writeProperties(nosection, writer, escapeUnicode);
            boolean first = true;
            for (Map.Entry<String, Map<String, String>> entry : sections.entrySet()) {
                if (entry.getValue().isEmpty()) continue;
                if (first) first = false;
                else writer.write(System.lineSeparator());
                writer.write('[');
                dumpString(writer, entry.getKey(), escapeUnicode);
                writer.write(']');
                writer.write(System.lineSeparator());
                writeProperties(entry.getValue(), writer, escapeUnicode);
            }
            writer.flush();
        }

        public static void writeComment(Writer writer, String comment) throws IOException {
            char[] chars = comment.toCharArray();
            if (chars.length > 0 && chars[0] != '#' && chars[0] != ';')
                writer.write('#');
            for (int index = 0; index < chars.length; index ++) {
                if (chars[index] == '\r' || chars[index] == '\n') {
                    int indexPlusOne = index + 1;
                    if (chars[index] == '\r' && indexPlusOne < chars.length && chars[indexPlusOne] == '\n') {
                        // "\r\n"
                        continue;
                    }
                    writer.write(System.lineSeparator());
                    if (indexPlusOne < chars.length && (chars[indexPlusOne] == '#' || chars[indexPlusOne] == ';')) {
                        // return char with either comment sign afterward
                        continue;
                    }
                    writer.write('#');
                } else {
                    writer.write(chars[index]);
                }
            }
            writer.write(System.lineSeparator());
        }

        public static void writeProperties(Map<String, String> properties, Writer writer, boolean escapeUnicode) throws IOException {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                dumpString(writer, key, escapeUnicode);
                writer.write('=');
                dumpString(writer, value, escapeUnicode);
                writer.write(System.lineSeparator());
            }
        }

        public static void dumpString(Writer writer, String string, boolean escapeUnicode) throws IOException {
            int index = 0, length = string.length();
            for (; index < length; index ++) {
                char ch = string.charAt(index);
                switch (ch) {
                    case '\t':
                        writer.write("\\t");
                        break;
                    case '\n':
                        writer.write("\\n");
                        break;
                    case '\f':
                        writer.write("\\f");
                        break;
                    case '\r':
                        writer.write("\\r");
                        break;
                    default:
                        if ("\\".indexOf(ch) >= 0 || ch == '#' || ch == ';' || ch == '=' || ch == ':') {
                            writer.write('\\');
                        }
                        if (ch >= ' ' && ch <= '~') {
                            writer.write(ch);
                        }
                        else if (escapeUnicode) {
                            writer.write(toHexDecimalUnicode(ch));
                        }
                        else writer.write(ch);
                        break;
                }
            }
        }

        public static char[] toHexDecimalUnicode(int ch) {
            char[] hexChars = { '\\', 'u', '0', '0', '0', '0' };
            int hexChar, index = hexChars.length, copyOfCh = ch;
            do {
                hexChar = copyOfCh & 15;
                if (hexChar > 9) {
                    hexChar = hexChar - 10 + 'A';
                }
                else {
                    hexChar += '0';
                }
                hexChars[-- index] = (char) hexChar;
            } while ((copyOfCh >>>= 4) != 0);
            return hexChars;
        }

        private static final int NONE = 0, SLASH = 1, UNICODE = 2, CONTINUE = 3, IGNORE = 4;
        public static void load(Properties properties, Reader reader) throws IOException {
            int mode = NONE, unicode = 0, count = 0;
            char current; char[] buffer = new char[40];
            int offset = 0, keyLength = -1, intValue;
            String lastSectionName = null;
            int sectionNameBegin = -1, sectionNameEnd = -1;
            boolean readingSectionName = false, gotDelimiter = false;

            while (true) {
                intValue = reader.read();
                if (intValue == -1) {
                    // if mode is UNICODE but has less than 4 hex digits, should
                    // throw an IllegalArgumentException
                    if (mode == UNICODE && count < 4) {
                        throw new IllegalArgumentException("Invalid Unicode sequence: expected format \\uxxxx");
                    }
                    // if mode is SLASH and no data is read, should append '\u0000'
                    // to buffer
                    if (mode == SLASH) {
                        buffer[offset ++] = '\u0000';
                    }
                    break;
                }
                current = (char) (intValue & 0xff);

                if (offset == buffer.length) {
                    char[] newBuf = new char[buffer.length * 2];
                    System.arraycopy(buffer, 0, newBuf, 0, offset);
                    buffer = newBuf;
                }
                if (mode == UNICODE) {
                    int digit = Character.digit(current, 16);
                    if (digit >= 0) {
                        unicode = (unicode << 4) + digit;
                        if (++ count < 4) {
                            continue;
                        }
                    } else if (count <= 4) {
                        throw new IllegalArgumentException("Invalid Unicode sequence: illegal character");
                    }
                    mode = NONE;
                    buffer[offset ++] = (char) unicode;
                    if (current != '\n') {
                        continue;
                    }
                }
                if (mode == SLASH) {
                    mode = NONE;
                    switch (current) {
                        case '\r':
                            mode = CONTINUE; // Look for a following \n
                            continue;
                        case '\n':
                            mode = IGNORE; // Ignore whitespace on the next line
                            continue;
                        case 'b':
                            current = '\b';
                            break;
                        case 'f':
                            current = '\f';
                            break;
                        case 'n':
                            current = '\n';
                            break;
                        case 'r':
                            current = '\r';
                            break;
                        case 't':
                            current = '\t';
                            break;
                        case 'u':
                            mode = UNICODE;
                            unicode = count = 0;
                            continue;
                    }
                } else {
                    switch (current) {
                        case '[':
                            if (!readingSectionName) {
                                readingSectionName = true;
                                sectionNameBegin = offset;
                                continue;
                            }
                            else break;
                        case ']':
                            sectionNameEnd = offset;
                            continue;
                        default:
                            if (current == '#' || current == ';') {
                                while (true) {
                                    intValue = reader.read();
                                    if (intValue == -1) {
                                        break;
                                    }
                                    // & 0xff not required
                                    current = (char) intValue;
                                    if (current == '\r' || current == '\n') {
                                        mode = NONE;
                                        if (offset >= 0) {
                                            if (keyLength == -1) {
                                                keyLength = offset;
                                            }
                                            String s = new String(buffer, 0, offset);
                                            if (readingSectionName && sectionNameBegin != -1 && sectionNameEnd != -1) {
                                                String sectionName = s.substring(sectionNameBegin, sectionNameEnd);
                                                if (sectionName.startsWith(".")) sectionName = lastSectionName == null ?
                                                        sectionName.substring(1) : lastSectionName + sectionName;
                                                lastSectionName = sectionName;
                                                readingSectionName = false;
                                                sectionNameBegin = sectionNameEnd = -1;
                                            }
                                            else if (!readingSectionName) {
                                                if (gotDelimiter) {
                                                    String key = s.substring(0, keyLength);
                                                    key = (lastSectionName == null ? key : lastSectionName + "." + key);
                                                    String value = (s.substring(keyLength));
                                                    properties.put(key, value);
                                                }
                                            }
                                        }
                                        keyLength = -1;
                                        offset = 0;
                                        gotDelimiter = false;
                                        break;
                                    }
                                }
                                continue;
                            }
                            else if (!readingSectionName && (current == '=' || current == ':')) {
                                gotDelimiter = true;
                                if (keyLength == -1) { // if parsing the key
                                    mode = NONE;
                                    keyLength = offset;
                                    continue;
                                }
                            }
                            break;
                        case '\n':
                            if (mode == CONTINUE) { // Part of a \r\n sequence
                                mode = IGNORE; // Ignore whitespace on the next line
                                continue;
                            }
                            // fall into the next case
                        case '\r':
                            mode = NONE;
                            if (offset >= 0) {
                                if (keyLength == -1) {
                                    keyLength = offset;
                                }
                                String s = new String(buffer, 0, offset);
                                if (readingSectionName && sectionNameBegin != -1 && sectionNameEnd != -1) {
                                    String sectionName = s.substring(sectionNameBegin, sectionNameEnd);
                                    if (sectionName.startsWith(".")) sectionName = lastSectionName == null ?
                                            sectionName.substring(1) : lastSectionName + sectionName;
                                    lastSectionName = sectionName;
                                    readingSectionName = false;
                                    sectionNameBegin = sectionNameEnd = -1;
                                }
                                else if (!readingSectionName) {
                                    if (gotDelimiter) {
                                        String key = s.substring(0, keyLength);
                                        key = lastSectionName == null ? key : lastSectionName + "." + key;
                                        String value = s.substring(keyLength);
                                        properties.put(key, value);
                                    }
                                }
                            }
                            keyLength = -1;
                            offset = 0;
                            gotDelimiter = false;
                            continue;
                        case '\\':
                            mode = SLASH;
                            continue;
                    }
                    if (current != ' ' && Character.isWhitespace(current)) {
                        if (mode == CONTINUE) {
                            mode = IGNORE;
                        }
                        // if key length == 0 or value length == 0
                        if (offset == 0 || offset == keyLength || mode == IGNORE) {
                            continue;
                        }
                    }
                    if (mode == IGNORE || mode == CONTINUE) {
                        mode = NONE;
                    }
                }
                buffer[offset ++] = current;
            }
            if (keyLength == -1 && offset > 0) {
                keyLength = offset;
            }
            if (keyLength >= 0) {
                String s = new String(buffer, 0, offset);
                if (!readingSectionName) {
                    if (gotDelimiter) {
                        String key = s.substring(0, keyLength);
                        key = lastSectionName == null ? key : lastSectionName + "." + key;
                        String value = s.substring(keyLength);
                        properties.put(key, value);
                    }
                }
            }
        }

    }

}
