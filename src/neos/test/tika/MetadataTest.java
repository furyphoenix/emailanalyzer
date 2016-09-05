package neos.test.tika;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import neos.tool.tika.NeosTikaTool;

import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;

public class MetadataTest {
	public static void main(String[] args) {
		File file=new File("test.pdf");
//		NeosTikaTool     tikaTool = new NeosTikaTool(new AutoDetectParser());
//        Metadata         metadata = new Metadata();
//        MimeEntityConfig config   = new MimeEntityConfig();
//
//        config.setMaximalBodyDescriptor(true);
//        TikaInputStream stream=null;
//		try {
//			stream = TikaInputStream.get(file, metadata);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//        try {
//			tikaTool.importStream(stream, metadata);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println(tikaTool.getMetadata());
		
		PDDocument doc;
		try {
			doc = PDDocument.load(new File("test.pdf"));
			PDDocumentCatalog catalog = doc.getDocumentCatalog();
			PDMetadata metadata = catalog.getMetadata();
			
			PDDocumentInformation info=doc.getDocumentInformation();
			
			System.out.println("Page Count: "+doc.getNumberOfPages());
			System.out.println("Language: "+catalog.getLanguage());
			System.out.println("PDF Version: "+catalog.getVersion());
			
			System.out.println("PDF Author: "+info.getAuthor());
			System.out.println("PDF Creator: "+info.getCreator());
			System.out.println("PDF Creation Date: "+info.getCreationDate());
			System.out.println("PDF Title: "+info.getTitle());
			System.out.println("PDF Producer: "+info.getProducer());
			System.out.println("PDF Subject: "+info.getSubject());
			System.out.println("PDF KeyWords: "+info.getKeywords());
			System.out.println("PDF Trapped: "+info.getTrapped());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	private static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
}
