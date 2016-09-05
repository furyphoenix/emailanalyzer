package neos.tool.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class QueryExpTest {
	public static void main(String[] args){
		Analyzer analyzer=new SmartChineseAnalyzer(Version.LUCENE_30);
		
		String[] scf={"Subject","TextMail","PlainMail"};
		MultiFieldQueryParser parser=new MultiFieldQueryParser(Version.LUCENE_30, scf, analyzer);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		String queryStr="Subject:(AAA OR BBB) 我们看到你了";
		try {
			Query query=parser.parse(queryStr);
			System.out.println(query.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
