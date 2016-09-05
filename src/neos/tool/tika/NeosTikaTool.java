package neos.tool.tika;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.DocumentSelector;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JEditorPane;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

/**
 * 
 * @author Li Xiao Yu
 *
 */
public class NeosTikaTool {

    /**
     * Parsing context.
     */
    private final ParseContext context;
    private String             error;
    private String             html;

    /**
     * Captures requested embedded images
     */
    private final ImageSavingParser imageParser;
    private boolean                 isErrorOccured;
    private String                  meta;

    /**
     * Configured parser instance.
     */
    private final Parser parser;
    private String       text;
    private String       textMain;
    private String       xml;

    public NeosTikaTool(Parser parser) {
        this.context     = new ParseContext();
        this.parser      = parser;
        this.imageParser = new ImageSavingParser(parser);
        this.context.set(DocumentSelector.class, new ImageDocumentSelector());
        this.context.set(Parser.class, imageParser);
        clear();
        this.isErrorOccured = false;
    }

    public void clear() {
        this.html     = "";
        this.text     = "";
        this.textMain = "";
        this.xml      = "";
        this.meta     = "";
        this.error    = "";
    }

    public void importStream(InputStream input, Metadata md) throws IOException {
        try {
            StringWriter   htmlBuffer     = new StringWriter();
            StringWriter   textBuffer     = new StringWriter();
            StringWriter   textMainBuffer = new StringWriter();
            StringWriter   xmlBuffer      = new StringWriter();
            StringBuilder  metadataBuffer = new StringBuilder();
            ContentHandler handler        = new TeeContentHandler(getHtmlHandler(htmlBuffer),
                                                getTextContentHandler(textBuffer),
                                                getTextMainContentHandler(textMainBuffer),
                                                getXmlContentHandler(xmlBuffer));

            context.set(DocumentSelector.class, new ImageDocumentSelector());
            parser.parse(input, handler, md, context);

            String[] names = md.names();

            Arrays.sort(names);

            for (String name : names) {
                metadataBuffer.append(name);
                metadataBuffer.append(": ");
                metadataBuffer.append(md.get(name));
                metadataBuffer.append("\n");
            }

            html     = htmlBuffer.toString();
            text     = textBuffer.toString();
            textMain = textMainBuffer.toString();
            xml      = xmlBuffer.toString();
            meta     = metadataBuffer.toString();
        } catch (Exception e) {
            clear();
            isErrorOccured = true;

            StringWriter writer = new StringWriter();

            e.printStackTrace(new PrintWriter(writer));
            error = writer.toString();
        } finally {
            input.close();
        }
    }

    public String getHtml() {
        return html;
    }

    public String getText() {
        return text;
    }

    public String getMainText() {
        return textMain;
    }

    public String getXml() {
        return xml;
    }

    public String getMetadata() {
        return meta;
    }

    public String getError() {
        return error;
    }

    public boolean isParseError() {
        return isErrorOccured;
    }

    /**
     * Creates and returns a content handler that turns XHTML input to
     * simplified HTML output that can be correctly parsed and displayed
     * by {@link JEditorPane}.
     * <p>
     * The returned content handler is set to output <code>html</code>
     * to the given writer. The XHTML namespace is removed from the output
     * to prevent the serializer from using the &lt;tag/&gt; empty element
     * syntax that causes extra "&gt;" characters to be displayed.
     * The &lt;head&gt; tags are dropped to prevent the serializer from
     * generating a &lt;META&gt; content type tag that makes
     * {@link JEditorPane} fail thinking that the document character set
     * is inconsistent.
     * <p>
     * Additionally, it will use ImageSavingParser to re-write embedded:(image)
     * image links to be file:///(temporary file) so that they can be loaded.
     *
     * @param writer output writer
     * @return HTML content handler
     * @throws TransformerConfigurationException if an error occurs
     */
    private ContentHandler getHtmlHandler(Writer writer) throws TransformerConfigurationException {
        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler    handler = factory.newTransformerHandler();

        handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
        handler.setResult(new StreamResult(writer));

        return new ContentHandlerDecorator(handler) {
            @Override
            public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
                if (XHTMLContentHandler.XHTML.equals(uri)) {
                    uri = null;
                }

                if (!"head".equals(localName)) {
                    if ("img".equals(localName)) {
                        AttributesImpl newAttrs;

                        if (atts instanceof AttributesImpl) {
                            newAttrs = (AttributesImpl) atts;
                        } else {
                            newAttrs = new AttributesImpl(atts);
                        }

                        for (int i = 0; i < newAttrs.getLength(); i++) {
                            if ("src".equals(newAttrs.getLocalName(i))) {
                                String src = newAttrs.getValue(i);

                                if (src.startsWith("embedded:")) {
                                    String filename = src.substring(src.indexOf(':') + 1);

                                    try {
                                        File   img    = imageParser.requestSave(filename);
                                        String newSrc = img.toURI().toString();

                                        newAttrs.setValue(i, newSrc);
                                    } catch (IOException e) {
                                        System.err.println("Error creating temp image file " + filename);

                                        // The html viewer will show a broken image too to alert them
                                    }
                                }
                            }
                        }

                        super.startElement(uri, localName, name, newAttrs);
                    } else {
                        super.startElement(uri, localName, name, atts);
                    }
                }
            }
            @Override
            public void endElement(String uri, String localName, String name) throws SAXException {
                if (XHTMLContentHandler.XHTML.equals(uri)) {
                    uri = null;
                }

                if (!"head".equals(localName)) {
                    super.endElement(uri, localName, name);
                }
            }
            @Override
            public void startPrefixMapping(String prefix, String uri) {}
            @Override
            public void endPrefixMapping(String prefix) {}
        };
    }

    private ContentHandler getTextContentHandler(Writer writer) {
        return new BodyContentHandler(writer);
    }

    private ContentHandler getTextMainContentHandler(Writer writer) {
        return new BoilerpipeContentHandler(writer);
    }

    private ContentHandler getXmlContentHandler(Writer writer) throws TransformerConfigurationException {
        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler    handler = factory.newTransformerHandler();

        handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
        handler.setResult(new StreamResult(writer));

        return handler;
    }

    /**
     * A {@link DocumentSelector} that accepts only images.
     */
    private static class ImageDocumentSelector implements DocumentSelector {
        public boolean select(Metadata metadata) {
            String type = metadata.get(Metadata.CONTENT_TYPE);

            return (type != null) && type.startsWith("image/");
        }
    }


    /**
     * A recursive parser that saves certain images into the temporary
     *  directory, and delegates everything else to another downstream
     *  parser.
     */
    private static class ImageSavingParser implements Parser {
        private Map<String, File> wanted = new HashMap<String, File>();
        private Parser            downstreamParser;
        private File              tmpDir;

        private ImageSavingParser(Parser downstreamParser) {
            this.downstreamParser = downstreamParser;

            try {
                File t = File.createTempFile("tika", ".test");

                tmpDir = t.getParentFile();
            } catch (IOException e) {}
        }

        public File requestSave(String embeddedName) throws IOException {
            String suffix = embeddedName.substring(embeddedName.lastIndexOf('.'));
            File   tmp    = File.createTempFile("tika-embedded-", suffix);

            wanted.put(embeddedName, tmp);

            return tmp;
        }

        public Set<MediaType> getSupportedTypes(ParseContext context) {

            // Never used in an auto setup
            return null;
        }

        public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
                throws IOException, SAXException, TikaException {
            String name = metadata.get(Metadata.RESOURCE_NAME_KEY);

            if ((name != null) && wanted.containsKey(name)) {
                FileOutputStream out = new FileOutputStream(wanted.get(name));

                IOUtils.copy(stream, out);
                out.close();
            } else {
                if (downstreamParser != null) {
                    downstreamParser.parse(stream, handler, metadata, context);
                }
            }
        }

        public void parse(InputStream stream, ContentHandler handler, Metadata metadata)
                throws IOException, SAXException, TikaException {
            parse(stream, handler, metadata, new ParseContext());
        }
    }
    
}
