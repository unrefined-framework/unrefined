package unrefined.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Properties;

public class PrettyPrintProperties extends FilterProperties {

    private static final long serialVersionUID = 7863835022796727910L;

    public PrettyPrintProperties(Properties properties) {
        super(properties);
    }

    public static void prettyPrintToXML(Properties properties, OutputStream os, String comment) throws IOException {
        XMLSupport.save(Objects.requireNonNull(properties), Objects.requireNonNull(os), comment, "UTF-8");
    }

    public static void prettyPrintToXML(Properties properties, OutputStream os, String comment, Charset charset) throws IOException {
        XMLSupport.save(Objects.requireNonNull(properties), Objects.requireNonNull(os), comment, charset == null ? "UTF-8" : charset.displayName());
    }

    public static void prettyPrintToXML(Properties properties, OutputStream os, String comment, String encoding) throws IOException {
        XMLSupport.save(Objects.requireNonNull(properties), Objects.requireNonNull(os), comment, encoding == null ? "UTF-8" : encoding);
    }

    @Override
    public void storeToXML(OutputStream os, String comment) throws IOException {
        XMLSupport.save(this, Objects.requireNonNull(os), comment, "UTF-8");
    }

    public void storeToXML(OutputStream os, String comment, Charset charset) throws IOException {
        XMLSupport.save(this, Objects.requireNonNull(os), comment, Objects.requireNonNull(charset).displayName());
    }

    @Override
    public void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
        XMLSupport.save(this, Objects.requireNonNull(os), comment, Objects.requireNonNull(encoding));
    }

    /**
     * A class used to aid in Properties save in XML.
     *
     * @author Michael McCloskey
     * @author Karstian Lee
     */
    private static final class XMLSupport {

        private XMLSupport() {
            throw new NotInstantiableError(XMLSupport.class);
        }

        // XML saving methods for Properties

        // The required DTD URI for exported properties
        private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";

        public static void save(Properties props, OutputStream os, String comment,
                         String encoding)
            throws IOException
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException pce) {
                //assert(false);
                throw new UnexpectedError(pce);
            }
            Document doc = db.newDocument();
            doc.setXmlStandalone(true);
            Element properties =  (Element)
                doc.appendChild(doc.createElement("properties"));

            if (comment != null) {
                Element comments = (Element)properties.appendChild(
                    doc.createElement("comment"));
                comments.appendChild(doc.createTextNode(comment));
            }

            synchronized (props) {
                for (String key : props.stringPropertyNames()) {
                    Element entry = (Element)properties.appendChild(
                        doc.createElement("entry"));
                    entry.setAttribute("key", key);
                    entry.appendChild(doc.createTextNode(props.getProperty(key)));
                }
            }
            emitDocument(doc, os, encoding);
        }

        public static void emitDocument(Document doc, OutputStream os, String encoding)
            throws IOException
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", 4);
            Transformer t;
            try {
                t = tf.newTransformer();
                t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, PROPS_DTD_URI);
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.setOutputProperty(OutputKeys.METHOD, "xml");
                t.setOutputProperty(OutputKeys.ENCODING, encoding);
            } catch (TransformerConfigurationException tce) {
                //assert(false);
                throw new UnexpectedError(tce);
            }
            DOMSource doms = new DOMSource(doc);
            StreamResult sr = new StreamResult(os);
            try {
                t.transform(doms, sr);
            } catch (TransformerException te) {
                throw new IOException(te);
            }
        }

    }

}
