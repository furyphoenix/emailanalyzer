package neos.app.email.gui;

import neos.lang.zh.NeosChineseConverter;
import net.miginfocom.swing.MigLayout;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
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

import com.steadystate.css.parser.Token;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;


public class EmailBrowserWin extends NeosStandardFrame {
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
    EmailMainWin parent;
    private JTextField textFieldTo;
    private JTextField textFieldFrom;
    private JXDatePicker dpStart;
    private JXDatePicker dpEnd;
    private JTextField textFieldKeyword;
    private JCheckBox chckbxContent;
    private JCheckBox chckbxAttachment;
    private final static SmartChineseAnalyzer scAnalyzer=new SmartChineseAnalyzer(Version.LUCENE_30);

    /**
     * Create the frame.
     */
    public EmailBrowserWin(EmailMainWin parent) {
        super();
        setTitle("\u90AE\u4EF6\u67E5\u8BE2");
        this.parent = parent;
        this.setBounds(100, 100, 800, 250);

        JPanel panel_1 = new JPanel();
        getCenterPanel().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel_1.add(panel, BorderLayout.CENTER);
        panel.setLayout(new MigLayout("", "[][][grow][][grow][]",
                "[][][][][grow]"));

        JLabel lblNewLabel = new JLabel("\u6536\u4EF6\u4EBA\uFF1A");
        panel.add(lblNewLabel, "cell 1 0,alignx trailing");

        textFieldTo = new JTextField();
        panel.add(textFieldTo, "cell 2 0 3 1,growx");
        textFieldTo.setColumns(10);

        JLabel lblNewLabel_1 = new JLabel("\u53D1\u4EF6\u4EBA\uFF1A");
        panel.add(lblNewLabel_1, "cell 1 1,alignx trailing");

        textFieldFrom = new JTextField();
        panel.add(textFieldFrom, "cell 2 1 3 1,growx");
        textFieldFrom.setColumns(10);

        JLabel lblNewLabel_2 = new JLabel("\u8D77\u6B62\u65E5\u671F\uFF1A");
        panel.add(lblNewLabel_2, "cell 1 2,alignx trailing");

        dpStart = new JXDatePicker(new Date());
        dpStart.setFormats(fmt);
        panel.add(dpStart, "flowx,cell 2 2,growx");

        JLabel lblNewLabel_3 = new JLabel("  \u81F3  ");
        panel.add(lblNewLabel_3, "cell 3 2,alignx trailing");

        dpEnd = new JXDatePicker(new Date());
        dpEnd.setFormats(fmt);
        panel.add(dpEnd, "cell 4 2,growx");

        JLabel lblNewLabel_4 = new JLabel("\u5173\u952E\u8BCD\uFF1A");
        panel.add(lblNewLabel_4, "cell 1 3,alignx trailing");

        textFieldKeyword = new JTextField();
        panel.add(textFieldKeyword, "cell 2 3 3 1,growx");
        textFieldKeyword.setColumns(10);

        JLabel lblNewLabel_5 = new JLabel("\u68C0\u7D22\u8303\u56F4\uFF1A");
        panel.add(lblNewLabel_5, "cell 1 4");

        JPanel panel_5 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
        flowLayout.setHgap(100);
        panel.add(panel_5, "cell 2 4 3 1,grow");

        JCheckBox chckbxSubject = new JCheckBox("\u4E3B\u9898");
        chckbxSubject.setEnabled(false);
        chckbxSubject.setSelected(true);
        panel_5.add(chckbxSubject);

        chckbxContent = new JCheckBox("\u6B63\u6587");
        chckbxContent.setSelected(true);
        panel_5.add(chckbxContent);

        chckbxAttachment = new JCheckBox("\u9644\u4EF6");
        panel_5.add(chckbxAttachment);

        JPanel panel_2 = new JPanel();
        panel_1.add(panel_2, BorderLayout.SOUTH);

        JButton btnQuery = new JButton("\u68C0\u7D22");
        btnQuery.addActionListener(new ActionListener() {
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
        panel_2.add(btnQuery);

        //
        this.parent = parent;
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
	        	try{
		            int id=Integer.parseInt(doc.get("EmailID"));
		            idList.add(id);
		            String from=doc.get("From");
		            String to=doc.get("To");
		            String date=fullfmt.format(DateTools.stringToDate(doc.get("Date")));
		            String subject=doc.get("Subject");
		            String plain=doc.get("PlainMail");
		            String text=doc.get("TextMail");
		            String preview="";
		            if(textFieldKeyword.getText().length()>0){
		            	String content=subject+""+plain+""+text;
			            preview=highlight(content, emailQuery, ana);
		            }
		            String[] data={from, to, date, subject};
		            dataList.add(data);
		            previews.add(preview);
	        	}catch(Exception e){
	        		e.printStackTrace();
	        		System.err.println("parse error for email id: "+doc.get("EmailID"));
	        	}
	            
	        }
	        
	        List<String[]> attDataList=new ArrayList<String[]> ();
	        List<Integer> attIdList=new ArrayList<Integer> ();
	        List<String> attPreviews=new ArrayList<String> ();
	        FSDirectory attDir=FSDirectory.open(new File(".\\index\\attach\\"+parent.getEmailDbName()));
	        IndexSearcher attSearcher=new IndexSearcher(IndexReader.open(attDir));
	        if(chckbxAttachment.isSelected()){
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
		            if(textFieldKeyword.getText().length()>0){
		            	String content=doc.get("Content");
			            preview=highlight(content, attQuery, ana);
		            }
		            
		            attDataList.add(data);
		            attPreviews.add(preview);
		        }
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
    
    private String buildEmailQueryString() {
        StringBuilder sb = new StringBuilder();

        String to = textFieldTo.getText();

        if (to.length() > 0) {
            String toExp = to.replaceAll("\\s+", " OR ");

            if (toExp.endsWith(" OR ")) {
                toExp=toExp.substring(0, toExp.length() - " OR ".length());
            }

            sb.append("+(");
            sb.append("To:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append("CC:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append("BCC:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append(") ");
        }

        String from = textFieldFrom.getText();

        if (from.length() > 0) {
            String fromExp = from.replaceAll("\\s+", " OR ");

            if (fromExp.endsWith(" OR ")) {
                fromExp=fromExp.substring(0, fromExp.length() - " OR ".length());
            }

            sb.append("+From:(");
            sb.append(fromExp);
            sb.append(") ");
        }

        Date start = dpStart.getDate();
        Date end = dpEnd.getDate();
        sb.append("+Date:[");
        sb.append(DateTools.dateToString(start, DateTools.Resolution.SECOND));
        sb.append(" TO ");
        sb.append(DateTools.dateToString(end, DateTools.Resolution.SECOND));
        sb.append("] ");

        String keyword = textFieldKeyword.getText();
        
        //keyword=(keyword+" "+NeosChineseConverter.sc2tc(keyword)).replaceAll("\\s+", " OR ");

        if (keyword.length() > 0) {
        	TokenStream ts=scAnalyzer.tokenStream("*", new StringReader(keyword));
        	ts.addAttribute(CharTermAttribute.class);
        	StringBuffer ksb=new StringBuffer();
        	try {
				while(ts.incrementToken()){
					CharTermAttribute attr = ts.getAttribute(CharTermAttribute.class);
					ksb.append(attr.toString());
					ksb.append(" AND ");
				}
				
				keyword=ksb.toString();
				int idx=keyword.lastIndexOf(" AND ");
				if(idx>=0){
					keyword=keyword.substring(0, idx);
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	String skeyword=NeosChineseConverter.tc2sc(keyword);
            String tkeyword=NeosChineseConverter.sc2tc(keyword);
            keyword="("+skeyword+") OR ("+tkeyword+")";
            
            sb.append("+(");

            sb.append("Subject:(");
            sb.append(keyword);
            sb.append(") ");

            if (chckbxContent.isSelected()) {
                sb.append("TextMail:(");
                sb.append(keyword);
                sb.append(") ");
                sb.append("PlainMail:(");
                sb.append(keyword);
                sb.append(") ");
            }

            sb.append(")");
        }
        
        System.out.println(sb.toString());

        return sb.toString();
    }

    private String buildAttachQueryString() {
        StringBuilder sb = new StringBuilder();

        String to = textFieldTo.getText();

        if (to.length() > 0) {
            String toExp = to.replaceAll("\\s+", " OR ");

            if (toExp.endsWith(" OR ")) {
                toExp.substring(0, toExp.length() - " OR ".length());
            }

            sb.append("+(");
            sb.append("To:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append("CC:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append("BCC:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append(") ");
        }

        String from = textFieldFrom.getText();

        if (from.length() > 0) {
            String fromExp = from.replaceAll("\\s+", " OR ");

            if (fromExp.endsWith(" OR ")) {
                fromExp.substring(0, fromExp.length() - " OR ".length());
            }

            sb.append("+From:(");
            sb.append(fromExp);
            sb.append(") ");
        }

        Date start = dpStart.getDate();
        Date end = dpEnd.getDate();
        sb.append("+Date:[");
        sb.append(DateTools.dateToString(start, DateTools.Resolution.SECOND));
        sb.append(" TO ");
        sb.append(DateTools.dateToString(end, DateTools.Resolution.SECOND));
        sb.append("] ");

        String keyword = textFieldKeyword.getText();
        if (keyword.length() > 0) {
        	TokenStream ts=scAnalyzer.tokenStream("*", new StringReader(keyword));
        	ts.addAttribute(CharTermAttribute.class);
        	StringBuffer ksb=new StringBuffer();
        	try {
				while(ts.incrementToken()){
					CharTermAttribute attr = ts.getAttribute(CharTermAttribute.class);
					ksb.append(attr.toString());
					ksb.append(" AND ");
				}
				
				keyword=ksb.toString();
				int idx=keyword.lastIndexOf(" AND ");
				if(idx>=0){
					keyword=keyword.substring(0, idx);
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	String skeyword=NeosChineseConverter.tc2sc(keyword);
            String tkeyword=NeosChineseConverter.sc2tc(keyword);
            keyword="("+skeyword+") OR ("+tkeyword+")";
            
            sb.append("+(");

            sb.append("Subject:(");
            sb.append(keyword);
            sb.append(") ");
            
            sb.append("Content:(");
            sb.append(keyword);
            sb.append(") ");

            sb.append(")");
        }
        
        System.out.println(sb.toString());

        return sb.toString();
    }
    
    private static String highlight(String content, Query query, Analyzer analyzer) throws IOException, InvalidTokenOffsetsException {  
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"background-color: red\"><big><big>", "</big></big></span>");  
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));  
        highlighter.setTextFragmenter(new SimpleFragmenter(25));  
        String resultString = highlighter.getBestFragments(analyzer.tokenStream("", new StringReader(content)), content, 3, "...");  
        return resultString + "...";  
    }
    
    private static String buildEmailQueryString(String from, String to, Date start, Date end, String keyword, boolean checkContent){
    	StringBuilder sb = new StringBuilder();

        

        if ((to!=null)&&(to.length() > 0)) {
            String toExp = to.replaceAll("\\s+", " OR ");

            if (toExp.endsWith(" OR ")) {
                toExp=toExp.substring(0, toExp.length() - " OR ".length());
            }

            sb.append("+(");
            sb.append("To:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append("CC:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append("BCC:(");
            sb.append(toExp);
            sb.append(") ");
            sb.append(") ");
        }

        

        if ((from!=null)&&(from.length() > 0)) {
            String fromExp = from.replaceAll("\\s+", " OR ");

            if (fromExp.endsWith(" OR ")) {
                fromExp=fromExp.substring(0, fromExp.length() - " OR ".length());
            }

            sb.append("+From:(");
            sb.append(fromExp);
            sb.append(") ");
        }

        
        sb.append("+Date:[");
        sb.append(DateTools.dateToString(start, DateTools.Resolution.SECOND));
        sb.append(" TO ");
        sb.append(DateTools.dateToString(end, DateTools.Resolution.SECOND));
        sb.append("] ");

        
        
        //keyword=(keyword+" "+NeosChineseConverter.sc2tc(keyword)).replaceAll("\\s+", " OR ");

        if ((keyword!=null)&&(keyword.length() > 0)) {
        	TokenStream ts=scAnalyzer.tokenStream("*", new StringReader(keyword));
        	ts.addAttribute(CharTermAttribute.class);
        	StringBuffer ksb=new StringBuffer();
        	try {
				while(ts.incrementToken()){
					CharTermAttribute attr = ts.getAttribute(CharTermAttribute.class);
					ksb.append(attr.toString());
					ksb.append(" AND ");
				}
				
				keyword=ksb.toString();
				int idx=keyword.lastIndexOf(" AND ");
				if(idx>=0){
					keyword=keyword.substring(0, idx);
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	String skeyword=NeosChineseConverter.tc2sc(keyword);
            String tkeyword=NeosChineseConverter.sc2tc(keyword);
            keyword="("+skeyword+") OR ("+tkeyword+")";
            
            sb.append("+(");

            sb.append("Subject:(");
            sb.append(keyword);
            sb.append(") ");

            if (checkContent) {
                sb.append("TextMail:(");
                sb.append(keyword);
                sb.append(") ");
                sb.append("PlainMail:(");
                sb.append(keyword);
                sb.append(") ");
            }

            sb.append(")");
        }
        
        System.out.println(sb.toString());

        return sb.toString();
    }
    
    public static void main(String[] args){
    	String from="a@123.com b@123.com";
    	String to="x@abc.com y@abc.com";
    	Date start=new Date();
    	Date end=new Date();
    	boolean chkcontent=true;
    	String keyword1="hello world";
    	String keyword2="我是中国人";
    	String keyword3="中国 人民 解放军";
    	String keyword4="\"中国人民\" 解放军 \"south china sea\"";
    	
    	System.out.println(buildEmailQueryString(from, to, start, end, keyword1, chkcontent));
    	System.out.println(buildEmailQueryString(from, to, start, end, keyword2, chkcontent));
    	System.out.println(buildEmailQueryString(from, to, start, end, keyword3, chkcontent));
    	System.out.println(buildEmailQueryString(from, to, start, end, keyword4, chkcontent));
    	
    }
}
