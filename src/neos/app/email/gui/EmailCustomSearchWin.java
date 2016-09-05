package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import neos.lang.zh.NeosChineseConverter;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
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
import org.jdesktop.swingx.JXDatePicker;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EmailCustomSearchWin extends NeosStandardFrame {
	private JTextField textField;
	
	private JXDatePicker dpStart;
    private JXDatePicker dpEnd;
    
    private final static SimpleDateFormat fmt = new SimpleDateFormat(
    "yyyy-MM-dd");

private final static SimpleDateFormat fullfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
private final static String[] EMAIL_FIELDS = {
    "Date", "From", "To", "CC", "BCC", "Subject", "PlainMail",
    "Textmail"
};
private final static String[] ATTACH_FIELDS = {
"Date", "From", "To", "CC", "BCC", "Subject", "Content",
};
private final static Analyzer ana = getAnalyzer();
private final static MultiFieldQueryParser emailParser = new MultiFieldQueryParser(Version.LUCENE_30,
	EMAIL_FIELDS, ana);
private final static MultiFieldQueryParser AttachParser = new MultiFieldQueryParser(Version.LUCENE_30,
	ATTACH_FIELDS, ana);
    
    private final EmailMainWin parent;
    private JCheckBox checkBox;
	
	/**
	 * Create the frame.
	 */
	public EmailCustomSearchWin(EmailMainWin parent) {
		setTitle("\u5B9A\u5236\u67E5\u8BE2");
		setBounds(100, 100, 450, 250);
		
		JPanel panel = new JPanel();
		getCenterPanel().add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("\u67E5\u8BE2");
		btnNewButton.addActionListener(new ActionListener() {
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
		panel.add(btnNewButton);
		
		JPanel panel_1 = new JPanel();
		getCenterPanel().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[][][][grow]", "[][][][]"));
		
		JLabel label = new JLabel("\u8D77\u59CB\u65E5\u671F");
		panel_1.add(label, "cell 1 0");
		
		dpStart=new JXDatePicker(new Date());
		dpStart.setFormats(fmt);
		panel_1.add(dpStart, "cell 3 0,growx");
		
		JLabel label_1 = new JLabel("\u7ED3\u675F\u65E5\u671F");
		panel_1.add(label_1, "cell 1 1");
		
		dpEnd=new JXDatePicker(new Date());
		dpEnd.setFormats(fmt);
		panel_1.add(dpEnd, "cell 3 1,growx");
		
		JLabel label_2 = new JLabel("\u5173\u6CE8\u8BCD\u5E93");
		panel_1.add(label_2, "cell 1 2");
		
		checkBox = new JCheckBox("\u4F7F\u7528\u7528\u6237\u5173\u6CE8\u8BCD\u5E93");
		panel_1.add(checkBox, "cell 3 2");
		
		JLabel label_3 = new JLabel("\u90AE\u7BB1\u6807\u6CE8");
		panel_1.add(label_3, "cell 1 3");
		
		textField = new JTextField();
		panel_1.add(textField, "cell 3 3,growx");
		textField.setColumns(10);
		
		//
		this.parent=parent;
	}
	
	private static Analyzer getAnalyzer() {
    	Map<String, Analyzer> map=new HashMap<String, Analyzer>();
    	map.put("Date", new KeywordAnalyzer());
    	map.put("From", new WhitespaceAnalyzer(Version.LUCENE_30));
    	map.put("To", new WhitespaceAnalyzer(Version.LUCENE_30));
    	map.put("CC", new WhitespaceAnalyzer(Version.LUCENE_30));
    	map.put("BCC", new WhitespaceAnalyzer(Version.LUCENE_30));
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new SmartChineseAnalyzer(
                    Version.LUCENE_30), map);

        return analyzer;
    }
	
	private void doSearch(){
    	setMessage("检索中……");
		setProgress(true);
		
		try {
			Query emailQuery = emailParser.parse(buildEmailQueryString());
			FSDirectory dir=FSDirectory.open(new File(".\\index\\content\\"+parent.getEmailDbName()));
			IndexSearcher searcher = new IndexSearcher(IndexReader.open(dir));
			TopDocs topDocs = searcher.search(emailQuery, 1000);  
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
	            String date=fullfmt.format(DateTools.stringToDate(doc.get("Date")));
	            String subject=doc.get("Subject");
	            String plain=doc.get("PlainMail");
	            String text=doc.get("TextMail");
	            String preview="";
	            
	            if(checkBox.isSelected()){
	            	String content=subject+""+plain+""+text;
		            preview=highlight(content, emailQuery, ana);
	            }
	            
	            String[] data={from, to, date, subject};
	            dataList.add(data);
	            previews.add(preview);
	        }
	        
	        List<String[]> attDataList=new ArrayList<String[]> ();
	        List<Integer> attIdList=new ArrayList<Integer> ();
	        List<String> attPreviews=new ArrayList<String> ();
	        FSDirectory attDir=FSDirectory.open(new File(".\\index\\attach\\"+parent.getEmailDbName()));
	        IndexSearcher attSearcher=new IndexSearcher(IndexReader.open(attDir));
	        QueryParser attParser=new QueryParser(Version.LUCENE_30, "Content", ana);
        	Query attQuery=attParser.parse(buildAttachQueryString());
        	TopDocs attTopDocs = attSearcher.search(attQuery, 1000);  
	        ScoreDoc[] attScoreDocs = attTopDocs.scoreDocs;
	        for (ScoreDoc scoreDoc : attScoreDocs) {  
	            Document doc = attSearcher.doc(scoreDoc.doc);  
	            int id=Integer.parseInt(doc.get("EmailID"));
	            attIdList.add(id);
	            String from=doc.get("From");
	            String to=doc.get("To");
	            String date=fullfmt.format(DateTools.stringToDate(doc.get("Date")));
	            String subject=doc.get("Subject");
	            String fileName=doc.get("FileName");
	            String[] data={from, to, date, subject, fileName};
	            String preview="";
	            if(checkBox.isSelected()){
	            	String content=doc.get("Content");
		            preview=highlight(content, attQuery, ana);
	            }
	            attDataList.add(data);
	            attPreviews.add(preview);
	        }
	        
	        KeywordSearchResult result=new KeywordSearchResult(idList, dataList, previews, attIdList, attDataList, attPreviews);
	        EmailListWin win=new EmailListWin(parent, result);
	        win.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		setMessage("");
		setProgress(false);
    }	
	
	private String buildAttachQueryString() {
        StringBuilder sb = new StringBuilder();

        Date start = dpStart.getDate();
        Date end = dpEnd.getDate();
        sb.append("+Date:[");
        sb.append(DateTools.dateToString(start, DateTools.Resolution.SECOND));
        sb.append(" TO ");
        sb.append(DateTools.dateToString(end, DateTools.Resolution.SECOND));
        sb.append("] ");

        if(checkBox.isSelected()){
        	List<String> wordList= getKeyWords();
        	if(wordList.size()>0){
        		StringBuilder ksb=new StringBuilder();
        		
        		ksb.append("\"");
        		ksb.append(wordList.get(0));
        		ksb.append("\" ");
        		
        		for(int i=1; i<wordList.size(); i++){
        			ksb.append(" OR \"");
        			ksb.append(wordList.get(i));
        			ksb.append("\" ");
        		}
        		String kwExp=ksb.toString();
        		String skeyword=NeosChineseConverter.tc2sc(kwExp);
                String tkeyword=NeosChineseConverter.sc2tc(kwExp);
                String keyword="("+skeyword+") OR ("+tkeyword+")";
                
                sb.append("+(");

                sb.append("Content:(");
                sb.append(keyword);
                sb.append(") ");
                
                sb.append(")");
        	}
        }
        
        System.out.println(sb.toString());

        return sb.toString();
    }
	
	private String buildEmailQueryString(){
		StringBuilder sb=new StringBuilder();
		
		if(textField.getText().length()>0){
			List<String> emailList=getEmailList();
			if(emailList.size()>0){
				StringBuilder esb=new StringBuilder();
				esb.append("\"");
				esb.append(emailList.get(0));
				esb.append("\" ");
				for(int i=1; i<emailList.size(); i++){
					esb.append(" OR \"");
					esb.append(emailList.get(i));
					esb.append("\" ");
				}
				String emExp=esb.toString();
				
				sb.append("+(");
				sb.append("From:(");
				sb.append(emExp);
				sb.append(") ");
				sb.append("To:(");
	            sb.append(emExp);
	            sb.append(") ");
	            sb.append("CC:(");
	            sb.append(emExp);
	            sb.append(") ");
	            sb.append("BCC:(");
	            sb.append(emExp);
	            sb.append(") ");
	            sb.append(") "); 
			}
		}
		
		Date start = dpStart.getDate();
        Date end = dpEnd.getDate();
        sb.append("+Date:[");
        sb.append(DateTools.dateToString(start, DateTools.Resolution.SECOND));
        sb.append(" TO ");
        sb.append(DateTools.dateToString(end, DateTools.Resolution.SECOND));
        sb.append("] ");
        
        if(checkBox.isSelected()){
        	List<String> wordList= getKeyWords();
        	if(wordList.size()>0){
        		StringBuilder ksb=new StringBuilder();
        		
        		ksb.append("\"");
        		ksb.append(wordList.get(0));
        		ksb.append("\" ");
        		
        		for(int i=1; i<wordList.size(); i++){
        			ksb.append(" OR \"");
        			ksb.append(wordList.get(i));
        			ksb.append("\" ");
        		}
        		String kwExp=ksb.toString();
        		String skeyword=NeosChineseConverter.tc2sc(kwExp);
                String tkeyword=NeosChineseConverter.sc2tc(kwExp);
                String keyword="("+skeyword+") OR ("+tkeyword+")";
                
                sb.append("+(");

                sb.append("Subject:(");
                sb.append(keyword);
                sb.append(") ");
                
                sb.append("TextMail:(");
                sb.append(keyword);
                sb.append(") ");
                sb.append("PlainMail:(");
                sb.append(keyword);
                sb.append(") ");
                
                sb.append(")");
        	}
        }
		
		return sb.toString();
	}
	
	private List<String> getEmailList(){
		List<String> emailList=new ArrayList<String> ();
		
		String query=buildQuerySql();
		
		if(textField.getText().length()>0){
			try{
				Statement st=parent.getEmailDbConnect().createStatement();
				ResultSet rs=st.executeQuery(query);
				
				while(rs.next()){
					emailList.add(rs.getString("EmailBox"));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		
		}
		
		return emailList;
	}
	
	private List<String> getKeyWords(){
		return parent.getUserDictionary().getVipWordList();
	}
	
	private String buildQuerySql(){
		String keyword=textField.getText();
		
		StringBuilder sb=new StringBuilder();
		
		sb.append("Select Distinct(`EmailBox`) From `");
		sb.append(parent.getEmailDbName());
		sb.append("`.`emailboxnote` ");
		sb.append(" Where ");
		sb.append("`NoteContent` LIKE '%");
		sb.append(keyword);
		sb.append("%' ");
		
		return sb.toString();
	}
	
	private static String highlight(String content, Query query, Analyzer analyzer) throws IOException, InvalidTokenOffsetsException {  
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"background-color: red\"><big><big>", "</big></big></span>");  
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));  
        highlighter.setTextFragmenter(new SimpleFragmenter(25));  
        String resultString = highlighter.getBestFragments(analyzer.tokenStream("", new StringReader(content)), content, 3, "...");  
        return resultString + "...";  
    }  

}
