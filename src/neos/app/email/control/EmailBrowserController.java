package neos.app.email.control;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neos.app.email.gui.KeywordSearchResult;
import neos.lang.zh.NeosChineseConverter;

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

public class EmailBrowserController extends AbstractEmailController {
	private final static SimpleDateFormat fmt = new SimpleDateFormat(
			"yyyy-MM-dd");

	private final static SimpleDateFormat fullfmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private final static String[] EMAIL_FIELDS = { "Date", "From", "To", "CC",
			"BCC", "Subject", "PlainMail", "Textmail" };
	private final static String[] ATTACH_FIELDS = { "Date", "From", "To", "CC",
			"BCC", "Subject", "Content", };

	private final static SmartChineseAnalyzer scAnalyzer = new SmartChineseAnalyzer(
			Version.LUCENE_30);
	private final static Analyzer ana = getAnalyzer();
	private final static MultiFieldQueryParser emailParser = new MultiFieldQueryParser(
			Version.LUCENE_30, EMAIL_FIELDS, ana);
	private final static MultiFieldQueryParser AttachParser = new MultiFieldQueryParser(
			Version.LUCENE_30, ATTACH_FIELDS, ana);

	public EmailBrowserController(Connection conn, String dbName) {
		super(conn, dbName);
		// TODO Auto-generated constructor stub
	}

	private static Analyzer getAnalyzer() {
		Map<String, Analyzer> map = new HashMap<String, Analyzer>();
		map.put("Date", new KeywordAnalyzer());
		map.put("From", new WhitespaceAnalyzer(Version.LUCENE_30));
		map.put("To", new WhitespaceAnalyzer(Version.LUCENE_30));
		map.put("CC", new WhitespaceAnalyzer(Version.LUCENE_30));
		map.put("BCC", new WhitespaceAnalyzer(Version.LUCENE_30));
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(
				new SmartChineseAnalyzer(Version.LUCENE_30), map);

		return analyzer;
	}

	private String buildEmailQueryString(String from, String to, Date start,
			Date end, String keyword, boolean isCheckContent) {
		StringBuilder sb = new StringBuilder();

		if ((to != null) && (to.length() > 0)) {
			String toExp = to.replaceAll("\\s+", " OR ");

			if (toExp.endsWith(" OR ")) {
				toExp = toExp.substring(0, toExp.length() - " OR ".length());
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

		if ((from != null) && (from.length() > 0)) {
			String fromExp = from.replaceAll("\\s+", " OR ");

			if (fromExp.endsWith(" OR ")) {
				fromExp = fromExp.substring(0,
						fromExp.length() - " OR ".length());
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

		if (keyword.length() > 0) {
			TokenStream ts = scAnalyzer.tokenStream("*", new StringReader(
					keyword));
			ts.addAttribute(CharTermAttribute.class);
			StringBuffer ksb = new StringBuffer();
			try {
				while (ts.incrementToken()) {
					CharTermAttribute attr = ts
							.getAttribute(CharTermAttribute.class);
					ksb.append(attr.toString());
					ksb.append(" AND ");
				}

				keyword = ksb.toString();
				int idx = keyword.lastIndexOf(" AND ");
				if (idx >= 0) {
					keyword = keyword.substring(0, idx);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			String skeyword = NeosChineseConverter.tc2sc(keyword);
			String tkeyword = NeosChineseConverter.sc2tc(keyword);
			keyword = "(" + skeyword + ") OR (" + tkeyword + ")";

			sb.append("+(");

			sb.append("Subject:(");
			sb.append(keyword);
			sb.append(") ");

			if (isCheckContent) {
				sb.append("TextMail:(");
				sb.append(keyword);
				sb.append(") ");
				sb.append("PlainMail:(");
				sb.append(keyword);
				sb.append(") ");
			}

			sb.append(")");
		}

		return sb.toString();
	}
	
	private String buildAttachQueryString(String from, String to, Date start,
			Date end, String keyword) {
        StringBuilder sb = new StringBuilder();

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

        if (from.length() > 0) {
            String fromExp = from.replaceAll("\\s+", " OR ");

            if (fromExp.endsWith(" OR ")) {
                fromExp.substring(0, fromExp.length() - " OR ".length());
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

        return sb.toString();
    }
	
	private static String highlight(String content, Query query, Analyzer analyzer) throws IOException, InvalidTokenOffsetsException {  
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"background-color: red\"><big><big>", "</big></big></span>");  
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));  
        highlighter.setTextFragmenter(new SimpleFragmenter(25));  
        String resultString = highlighter.getBestFragments(analyzer.tokenStream("", new StringReader(content)), content, 3, "...");  
        return resultString + "...";  
    }

	public KeywordSearchResult searchEmail(String from, String to, Date start,
			Date end, String keyword, boolean isCheckContent){
		String query=buildEmailQueryString(from, to, start, end, keyword, isCheckContent);
		
		try {
			Query emailQuery = emailParser.parse(query);
			FSDirectory dir=FSDirectory.open(new File(".\\index\\content\\"+dbName));
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
		            String fromF=doc.get("From");
		            String toF=doc.get("To");
		            String dateF=fullfmt.format(DateTools.stringToDate(doc.get("Date")));
		            String subjectF=doc.get("Subject");
		            String plainF=doc.get("PlainMail");
		            String textF=doc.get("TextMail");
		            String previewF="";
		            if(keyword.length()>0){
		            	String content=subjectF+""+plainF+""+textF;
			            previewF=highlight(content, emailQuery, ana);
		            }
		            String[] data={fromF, toF, dateF, subjectF};
		            dataList.add(data);
		            previews.add(previewF);
	        	}catch(Exception e){
	        		e.printStackTrace();
	        		System.err.println("parse error for email id: "+doc.get("EmailID"));
	        	}
	            
	        }
	        
	        searcher.close();
	        
	        List<String[]> attDataList=new ArrayList<String[]> ();
	        List<Integer> attIdList=new ArrayList<Integer> ();
	        List<String> attPreviews=new ArrayList<String> ();
	        FSDirectory attDir=FSDirectory.open(new File(".\\index\\attach\\"+dbName));
	        IndexSearcher attSearcher=new IndexSearcher(IndexReader.open(attDir));
	        if(isCheckContent){
	        	QueryParser attParser=new QueryParser(Version.LUCENE_30, "Content", ana);
	        	Query attQuery=attParser.parse(buildAttachQueryString(from, to, start, end, keyword));
	        	TopDocs attTopDocs = attSearcher.search(attQuery, 1000);  
		        ScoreDoc[] attScoreDocs = attTopDocs.scoreDocs;
		        for (ScoreDoc scoreDoc : attScoreDocs) {  
		            Document doc = attSearcher.doc(scoreDoc.doc);  
		            int id=Integer.parseInt(doc.get("EmailID"));
		            attIdList.add(id);
		            String fromF=doc.get("From");
		            String toF=doc.get("To");
		            String dateF=fullfmt.format(DateTools.stringToDate(doc.get("Date")));
		            String subjectF=doc.get("Subject");
		            String fileNameF=doc.get("FileName");
		            String[] data={fromF, toF, dateF, subjectF, fileNameF};
		            String previewF="";
		            if(keyword.length()>0){
		            	String content=doc.get("Content");
			            previewF=highlight(content, attQuery, ana);
		            }
		            
		            attDataList.add(data);
		            attPreviews.add(previewF);
		        }
	        }
	        
	        attSearcher.close();
	        
	        KeywordSearchResult result=new KeywordSearchResult(idList, dataList, previews, attIdList, attDataList, attPreviews);
	        return result;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
