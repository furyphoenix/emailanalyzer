package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;


/**
 *
 * @author swiki swiki
 *
 *
 *
 */
public class TestAsyncWriter {
    public static void main(String[] args) {
        try {
            Directory   fsdir = FSDirectory.open(new File("index"));
            IndexWriter w     = new IndexWriter(fsdir, new WhitespaceAnalyzer(),
                                    !IndexReader.indexExists(fsdir), MaxFieldLength.LIMITED);
            AsynchronousIndexWriter writer = new AsynchronousIndexWriter(w);

/*

            * This call can be replaced by the logic of reading

            * data using multiple threads

*/
            //addDocumentsInMultipleThreads(writer);
            AddDocumentsTest(writer);
            
            writer.optimize();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addDocumentsInMultipleThreads(AsynchronousIndexWriter writer) throws InterruptedException {

//      add here the code for adding document from multiple threads.
        Document doc = new Document();

        doc.add(new Field("content", "My Content", Field.Store.YES, Field.Index.NOT_ANALYZED));
        writer.addDocument(new Document());
    }
    
    private static void AddDocumentsTest(AsynchronousIndexWriter writer) throws InterruptedException{
    	final int[] data=new int[1024];
    	for(int i=0; i<data.length; i++){
    		data[i]=(int)(Math.random()*1024);
    	}
    	
    	int cpu=ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
    	ThreadPoolExecutor exec=(ThreadPoolExecutor)Executors.newFixedThreadPool(cpu);
    	List<Future<?>> flist=new ArrayList<Future<?>> ();
    	for(int i=0; i<1000; i++){
    		AddDocumentTask task=new AddDocumentTask(data, writer);
    		Future<?> t=exec.submit(task);
    		flist.add(t);
    	}
    	exec.shutdown();
    	for(Future<?> t:flist){
    		try {
				t.get();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    private static class AddDocumentTask implements Runnable{
    	final int[] data;
    	final AsynchronousIndexWriter writer;
    	
    	public AddDocumentTask(int[] data, AsynchronousIndexWriter writer){
    		this.data=data;
    		this.writer=writer;
    	}
    	
		@Override
		public void run() {
			int idx1=(int)Math.random()*1024;
			int idx2=(int)Math.random()*1024;
			int min=idx1<idx2?idx1:idx2;
			int max=idx1>idx2?idx1:idx2;
			StringBuffer sb=new StringBuffer();
			for(int i=min; i<max; i++){
				sb.append(data[i]);
				sb.append(" ");
			}
			
			Document doc = new Document();
			doc.add(new Field("content", sb.toString(), Field.Store.YES, Field.Index.ANALYZED));
			try {
				writer.addDocument(doc);
				writer.startWriting();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Document Added!");
			
		}
    	
    }
}
