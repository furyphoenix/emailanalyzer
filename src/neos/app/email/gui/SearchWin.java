package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import neos.app.gui.ProgressMornitor;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SearchWin extends JFrame implements ProgressMornitor{

	private JPanel contentPane;
	private JProgressBar progressBar;
	private JTextField textFieldKeyword;
	private JCheckBox chckbxSubject;
	private JCheckBox chckbxContent;
	private JCheckBox chckbxAttachment;
	private EmailMainWin parent;
	private JLabel lblStatus;

	

	/**
	 * Create the frame.
	 */
	public SearchWin(EmailMainWin parent) {
		setTitle("\u5173\u952E\u8BCD\u67E5\u8BE2");
		setIconImage(Toolkit.getDefaultToolkit().getImage(SearchWin.class.getResource("/icon/1307955269_applications-systemg-icon.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		lblStatus = new JLabel("    ");
		panel.add(lblStatus, BorderLayout.CENTER);
		
		progressBar = new JProgressBar();
		panel.add(progressBar, BorderLayout.EAST);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.SOUTH);
		
		JButton btnSearch = new JButton("\u67E5\u8BE2");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							doSearch();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_2.add(btnSearch);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new MigLayout("", "[][][][grow][grow][grow][]", "[][][]"));
		
		JLabel lblNewLabel = new JLabel("\u5173\u952E\u8BCD\uFF1A");
		panel_3.add(lblNewLabel, "cell 1 1");
		
		textFieldKeyword = new JTextField();
		panel_3.add(textFieldKeyword, "cell 3 1 3 1, growx");
		textFieldKeyword.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("\u67E5\u8BE2\u8303\u56F4\uFF1A");
		panel_3.add(lblNewLabel_1, "cell 1 2");
		
		chckbxSubject = new JCheckBox("\u90AE\u4EF6\u4E3B\u9898");
		chckbxSubject.setEnabled(false);
		chckbxSubject.setSelected(true);
		panel_3.add(chckbxSubject, "cell 3 2");
		
		chckbxContent = new JCheckBox("\u90AE\u4EF6\u6B63\u6587");
		chckbxContent.setSelected(true);
		panel_3.add(chckbxContent, "cell 4 2");
		
		chckbxAttachment = new JCheckBox("\u90AE\u4EF6\u9644\u4EF6");
		panel_3.add(chckbxAttachment, "cell 5 2");
		
		this.parent=parent;
	}
	
	private void doSearch(){
		String keywords=textFieldKeyword.getText();
		if(keywords.length()<=0){
			return;
		}
		
		setMessage("检索中……");
		setProgress(true);
		
		try{
			Analyzer analyzer=new SmartChineseAnalyzer(Version.LUCENE_30);
			QueryParser parser;
			String[] scf={"Subject","TextMail","PlainMail"};
			if(chckbxContent.isSelected()){
				parser=new MultiFieldQueryParser(Version.LUCENE_30, scf, analyzer);
			}else{
				parser=new QueryParser(Version.LUCENE_30, "Subject", analyzer);
			}
			Query query = parser.parse(keywords);
			IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(".\\index\\content")));
			TopDocs topDocs = searcher.search(query, 1000);  
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	        List<String[]> dataList=new ArrayList<String[]> ();
	        List<Integer> idList=new ArrayList<Integer> ();
	        List<String> previews=new ArrayList<String> ();
	        for (ScoreDoc scoreDoc : scoreDocs) {  
	            Document doc = searcher.doc(scoreDoc.doc);  
	            int id=Integer.parseInt(doc.get("EmailID"));
	            idList.add(id);
	            String from=doc.get("From");
	            String to=doc.get("To");
	            String date=doc.get("Date");
	            String subject=doc.get("Subject");
	            String plain=doc.get("PlainMail");
	            String text=doc.get("TextMail");
	            String content=subject+""+plain+""+text;
	            String preview=highlight(content, query, analyzer);
	            String[] data={from, to, date, subject};
	            dataList.add(data);
	            previews.add(preview);
	        }
	        
	        List<String[]> attDataList=new ArrayList<String[]> ();
	        List<Integer> attIdList=new ArrayList<Integer> ();
	        List<String> attPreviews=new ArrayList<String> ();
	        IndexSearcher attSearcher=new IndexSearcher(FSDirectory.open(new File(".\\index\\attach")));
	        if(chckbxAttachment.isSelected()){
	        	QueryParser attParser=new QueryParser(Version.LUCENE_30, "Content", analyzer);
	        	Query attQuery=attParser.parse(keywords);
	        	TopDocs attTopDocs = attSearcher.search(attQuery, 1000);  
		        ScoreDoc[] attScoreDocs = attTopDocs.scoreDocs;
		        for (ScoreDoc scoreDoc : attScoreDocs) {  
		            Document doc = attSearcher.doc(scoreDoc.doc);  
		            int id=Integer.parseInt(doc.get("EmailID"));
		            attIdList.add(id);
		            String from=doc.get("From");
		            String to=doc.get("To");
		            String date=doc.get("Date");
		            String subject=doc.get("Subject");
		            String fileName=doc.get("FileName");
		            String[] data={from, to, date, subject, fileName};
		            String content=doc.get("Content");
		            String preview=highlight(content, attQuery, analyzer);
		            attDataList.add(data);
		            attPreviews.add(preview);
		        }
	        }
	        
	        KeywordSearchResult result=new KeywordSearchResult(idList, dataList, previews, attIdList, attDataList, attPreviews);
	        EmailListWin win=new EmailListWin(parent, result);
	        win.setVisible(true);
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
		setMessage("");
		setProgress(false);
	}
	
	private static String highlight(String content, Query query, Analyzer analyzer) throws IOException, InvalidTokenOffsetsException {  
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"background-color: red\"><big><big>", "</big></big></span>");  
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));  
        highlighter.setTextFragmenter(new SimpleFragmenter(25));  
        String resultString = highlighter.getBestFragments(analyzer.tokenStream("", new StringReader(content)), content, 3, "...");  
        return resultString + "...";  
    }  
	
	@Override
    public void setMessage(String mess) {
        lblStatus.setText("    " + mess);
    }

    @Override
    public void setProgress(boolean indetermin) {
        progressBar.setIndeterminate(indetermin);
    }

    @Override
    public void setProgress(int n) {
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }

        progressBar.setValue(n);
    }


	
	

}
