package neos.tool.tika;

//~--- non-JDK imports --------------------------------------------------------

import neos.tool.mime4j.NeosMime4JTool;
import neos.app.util.HtmlParser;

import org.apache.james.mime4j.message.Message;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.htmlparser.util.ParserException;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class NeosFileDocument {
    private NeosFileDocument() {}

    /**
     * Makes a document for a File.
     * <p>
     * The document has three fields:
     * <ul>
     * <li><code>path</code>--containing the pathname of the file, as a stored,
     * untokenized field;
     * <li><code>modified</code>--containing the last modified date of the file as
     * a field as created by <a
     * href="lucene.document.DateTools.html">DateTools</a>; and
     * <li><code>contents</code>--containing the full contents of the file, as a
     * Reader field;
     */
    public static Document Document(File f) throws java.io.FileNotFoundException {

        // make a new, empty document
        Document doc = new Document();

        // Add the path of the file as a field named "path".  Use a field that is
        // indexed (i.e. searchable), but don't tokenize the field into words.
        doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Add the last modified date of the file a field named "modified".  Use
        // a field that is indexed (i.e. searchable), but don't tokenize the field
        // into words.
        doc.add(new Field("modified", DateTools.timeToString(f.lastModified(), DateTools.Resolution.MINUTE),
                          Field.Store.YES, Field.Index.NOT_ANALYZED));

        //change file to text
        NeosTikaTool tool=new NeosTikaTool(new AutoDetectParser());
        Metadata metadata= new Metadata();
        TikaInputStream stream=TikaInputStream.get(f, metadata);
        try {
			tool.importStream(stream, metadata);
			
			String content;
			
			String fileName=f.getName();
			if((fileName.endsWith(".html"))||(fileName.endsWith(".htm"))){
				HtmlParser p=HtmlParser.parseFile(f);
				content=p.getBodyText();
			}else if((fileName.endsWith(".eml"))||(fileName.endsWith(".msg"))){
				NeosMime4JTool t=new NeosMime4JTool(new Message(new FileInputStream(f)));
				String html=t.getTextHtmlContent();
				
				if((html!=null)&&(html.length()>0)){
					content=new HtmlParser(html).getBodyText();
				}else{
					content=t.getTextPlainContent();
				}
			}else{
				content=tool.getText();
			}
			
			doc.add(new Field("contents", content, Field.Store.NO, Field.Index.ANALYZED));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ParserException e){
			e.printStackTrace();
		}
        
        
        
        // Add the contents of the file to a field named "contents".  Specify a Reader,
        // so that the text of the file is tokenized and indexed, but not stored.
        // Note that FileReader expects the file to be in the system's default encoding.
        // If that's not the case searching for special characters will fail.
        //doc.add(new Field("contents", new FileReader(f)));

        // return the document
        return doc;
    }
    
    private static String getFileExtension(File f){
    	String fileName=f.getName();
    	if(fileName.indexOf('.')<=0){
    		return null;
    	}
    	if(fileName.endsWith(".")){
    		return null;
    	}
    	int idx=fileName.lastIndexOf(".");
    	return fileName.substring(idx);
    }
}
