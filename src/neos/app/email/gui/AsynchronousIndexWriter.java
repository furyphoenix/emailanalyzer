package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author swiki swiki
 *
 *
 *
 */
public class AsynchronousIndexWriter implements Runnable {

/*

    * We need to set this to false if the document addition is completed. This

    * will not immediately stop the writing as there could be some documents in

    * the queue. It completes once all documents are written and the queue is

    * empty.

*/
    private boolean keepRunning = true;

/*

    * This flag is set to false once writer is done with the queue data

    * writing.

*/
    private boolean isRunning = true;

/*

    * Duration in miliseconds for which the writer should sleep when it finds

    * the queue empty and job is still not completed

*/
    private long sleepMilisecondOnEmpty = 100;

/*

    * A blocking queue of document to facilitate asynchronous writing.

*/
    private BlockingQueue documents;

/*

    * Instance of core index writer which does the actual writing task.

*/
    private IndexWriter writer;

/*

    * Thread which makes writing asynchronous

*/
    private Thread writerThread;

    /**
     *
     * Constructor with indexwriter as input. It Uses ArrayBlockingQueue with
     *
     * size 100 and sleepMilisecondOnEmpty is 100ms
     *
     *
     *
     * @param w
     *
     */
    public AsynchronousIndexWriter(IndexWriter w) {
        this(w, 100, 100);
    }

    /**
     *
     * Constructor with indexwriter and queue size as input. It Uses
     *
     * ArrayBlockingQueue with size queueSize and sleepMilisecondOnEmpty is
     *
     * 100ms
     *
     *
     *
     * @param w
     *
     * @param queueSize
     *
     */
    public AsynchronousIndexWriter(IndexWriter w, int queueSize) {
        this(w, queueSize, 100);
    }

    /**
     *
     * A implementation of BlockingQueue can be used
     *
     *
     *
     * @param w
     *
     * @param queueSize
     *
     * @param sleepMilisecondOnEmpty
     *
     */
    public AsynchronousIndexWriter(IndexWriter w, BlockingQueue queue, long sleepMilisecondOnEmpty) {
        writer                      = w;
        documents                   = queue;
        this.sleepMilisecondOnEmpty = sleepMilisecondOnEmpty;
        startWriting();
    }

    /**
     *
     * Constructor with indexwriter, queueSize as input. It Uses
     *
     * ArrayBlockingQueue with size queueSize
     *
     *
     *
     * @param w
     *
     * @param queueSize
     *
     * @param sleepMilisecondOnEmpty
     *
     */
    public AsynchronousIndexWriter(IndexWriter w, int queueSize, long sleepMilisecondOnEmpty) {
        this(w, new ArrayBlockingQueue(queueSize), sleepMilisecondOnEmpty);
    }

    /**
     *
     * This method should be used to add documents to index queue. If the queue
     *
     * is full it will wait for the queue to be available.
     *
     *
     *
     * @param doc
     *
     * @throws InterruptedException
     *
     */
    public void addDocument(Document doc) throws InterruptedException {
        documents.put(doc);
    }

    public void startWriting() {
        writerThread = new Thread(this, "AsynchronousIndexWriter");
        writerThread.start();
    }

/*

    * (non-Javadoc)

    *

    * @see java.lang.Runnable#run()

*/
    public void run() {
        while (keepRunning ||!documents.isEmpty()) {
            Document d = (Document) documents.poll();

            try {
                if (d != null) {
                    writer.addDocument(d);
                } else {

                    /*
                     *
                     * Nothing in queue so lets wait
                     *
                     */
                    Thread.sleep(sleepMilisecondOnEmpty);
                }
            } catch (ClassCastException e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            } catch (CorruptIndexException e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }

        isRunning = false;
    }

    /**
     *
     * Stop the thread gracefully, wait until its done writing.
     *
     */
    private void stopWriting() {
        this.keepRunning = false;

        try {
            while (isRunning) {

                // using the same sleep duration as writer uses
                Thread.sleep(sleepMilisecondOnEmpty);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void optimize() throws CorruptIndexException, IOException {
        writer.optimize();
    }

    public void close() throws CorruptIndexException, IOException {
        stopWriting();
        writer.close();
    }
}
