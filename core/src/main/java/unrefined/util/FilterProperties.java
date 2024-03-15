package unrefined.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A {@code FilterProperties} contains
 * some other {@link Properties}, which it uses as
 * its basic source of data, possibly transforming
 * the data along the way or providing additional
 * functionality. The class {@code FilterProperties}
 * itself simply overrides all methods of
 * {@code Properties} with versions that
 * pass all requests to the contained {@code Properties}.
 * Subclasses of {@code FilterProperties}
 * may further override some of these methods
 * and may also provide additional methods and fields.
 *
 * @author Karstian Lee
 */
public abstract class FilterProperties extends Properties {

    private static final long serialVersionUID = -4688327632652070138L;

    /**
     * The {@code Properties} to be filtered.
     */
    protected volatile Properties properties;

    /**
     * Creates a {@code FilterProperties}
     * by assigning the argument {@code properties}
     * to the field {@link #properties} so as
     * to remember it for later use.
     *
     * @param properties the underlying {@link Properties}, or {@code null} if
     *                   this instance is to be created without an underlying {@code Properties}.
     *                   <p><b>Note: If the argument is null, you should override the {@link #properties()}
     *                   method to provide a valid {@code Properties}.</b></p>
     * @see #properties()
     */
    protected FilterProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Returns the underlying {@link Properties} to be filtered.
     * @return the {@code Properties} to be filtered
     */
    public Properties properties() {
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Object setProperty(String key, String value) {
        return properties().setProperty(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void load(Reader reader) throws IOException {
        properties().load(reader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        properties().load(inStream);
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public void save(OutputStream out, String comments) {
        properties().save(out, comments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store(Writer writer, String comments) throws IOException {
        properties().store(writer, comments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store(OutputStream out, String comments) throws IOException {
        properties().store(out, comments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
        properties().loadFromXML(in);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeToXML(OutputStream os, String comment) throws IOException {
        properties().storeToXML(os, comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
        properties().storeToXML(os, comment, encoding);
    }

    /**
     * Emits an XML document representing all of the properties contained
     * in this table, using the specified encoding.
     *
     * <p>The XML document will have the following DOCTYPE declaration:
     * <pre>
     * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
     * </pre>
     *
     * <p>If the specified comment is {@code null} then no comment
     * will be stored in the document.
     *
     * <p> An implementation is required to support writing of XML documents
     * that use the "{@code UTF-8}" or "{@code UTF-16}" encoding. An
     * implementation may support additional encodings.
     *
     * <p> Unmappable characters for the specified charset will be encoded as
     * numeric character references.
     *
     * <p>The specified stream remains open after this method returns.
     *
     * @param os        the output stream on which to emit the XML document.
     * @param comment   a description of the property list, or {@code null}
     *                  if no comment is desired.
     * @param charset   the charset
     *
     * @throws IOException if writing to the specified output stream
     *         results in an {@code IOException}.
     * @throws NullPointerException if {@code os} or {@code charset} is {@code null}.
     * @throws ClassCastException  if this {@code Properties} object
     *         contains any keys or values that are not {@code Strings}.
     * @see    #loadFromXML(InputStream)
     * @see    <a href="http://www.w3.org/TR/REC-xml/#charencoding">Character
     *         Encoding in Entities</a>
     */
    public void storeToXML(OutputStream os, String comment, Charset charset) throws IOException {
        storeToXML(os, comment, charset.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProperty(String key) {
        return properties().getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProperty(String key, String defaultValue) {
        return properties().getProperty(key, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<?> propertyNames() {
        return properties().propertyNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> stringPropertyNames() {
        return properties().stringPropertyNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void list(PrintStream out) {
        properties().list(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void list(PrintWriter out) {
        properties().list(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return properties().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return properties().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<Object> keys() {
        return properties().keys();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<Object> elements() {
        return properties().elements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object value) {
        return properties().contains(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        return properties().containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return properties().containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        return properties().get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Object put(Object key, Object value) {
        return properties().put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Object remove(Object key) {
        return properties().remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void putAll(Map<?, ?> t) {
        properties().putAll(t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void clear() {
        properties().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String toString() {
        return properties().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Object> keySet() {
        return properties().keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Object> values() {
        return properties().values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return properties().entrySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean equals(Object o) {
        return properties().equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int hashCode() {
        return properties().hashCode();
    }

}
