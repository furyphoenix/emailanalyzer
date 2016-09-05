package neos.component.ner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NeosDateTimeExtractor extends AbstractNeosEntityExtractor{
	private final static SimpleDateFormat standardFmt=new SimpleDateFormat("yyyy-MM-dd");
	private static char[] ChineseDigitsArray={'零','一','二','三','四','五','六','七','八','九','〇','O','十'};
	private static char[] DigitArray={'0','1','2','3','4','5','6','7','8','9','0','0','+'};
	private static String chineseDigit="[零一二三四五六七八九十]";
	private static String chineseWeekDigit="[一二三四五六日天]";
	
	
	private static List<NeosEntityLocator> locList=new ArrayList<NeosEntityLocator> ();
	private static List<NeosEntityTranslator> tranList=new ArrayList<NeosEntityTranslator> ();
	
	public NeosDateTimeExtractor(){
		super();
		addDefaultProcessor();
	}
	
	private static void addDefaultProcessor(){
		locList.add(new NeosRegexEntityLocator("\\d{2}-\\d{1,2}-\\d{1,2}"));
		tranList.add(new NeosSimpleDateTimeTranslator("yy-MM-dd", NeosDateTimeEntity.DefaultFlag));
		
		locList.add(new NeosRegexEntityLocator("\\d{4}-\\d{1,2}-\\d{1,2}"));
		tranList.add(new NeosSimpleDateTimeTranslator("yyyy-MM-dd", NeosDateTimeEntity.DefaultFlag));
		
		locList.add(new NeosRegexEntityLocator("\\d{2}年\\d{1,2}月\\d{1,2}日"));
		tranList.add(new NeosSimpleDateTimeTranslator("yy年MM月dd日", NeosDateTimeEntity.DefaultFlag));
		
		locList.add(new NeosRegexEntityLocator("\\d{4}年\\d{1,2}月\\d{1,2}日"));
		tranList.add(new NeosSimpleDateTimeTranslator("yyyy年MM月dd日", NeosDateTimeEntity.DefaultFlag));
		
		locList.add(new NeosRegexEntityLocator(chineseDigit+"{2}年"+chineseDigit+"{1,3}月"+chineseDigit+"{1,3}日"));
		tranList.add(new NeosSimpleChineseDateTimeTranslator("yy年MM月dd日", NeosDateTimeEntity.DefaultFlag));
		
		locList.add(new NeosRegexEntityLocator(chineseDigit+"{4}年"+chineseDigit+"{1,3}月"+chineseDigit+"{1,3}日"));
		tranList.add(new NeosSimpleChineseDateTimeTranslator("yyyy年MM月dd日", NeosDateTimeEntity.DefaultFlag));
		
		locList.add(new NeosRegexEntityLocator("\\d{4}-\\d{1,2}"));
		tranList.add(new NeosSimpleDateTimeTranslator("yyyy-MM", NeosDateTimeEntity.YearFlag|NeosDateTimeEntity.MonthFlag));
		
		locList.add(new NeosRegexEntityLocator("\\d{2}年\\d{1,2}月"));
		tranList.add(new NeosSimpleDateTimeTranslator("yy年MM月", NeosDateTimeEntity.YearFlag|NeosDateTimeEntity.MonthFlag));
		
		locList.add(new NeosRegexEntityLocator("\\d{4}年\\d{1,2}月"));
		tranList.add(new NeosSimpleDateTimeTranslator("yyyy年MM月", NeosDateTimeEntity.YearFlag|NeosDateTimeEntity.MonthFlag));
		
		locList.add(new NeosRegexEntityLocator("d{1,2}月\\d{1,2}日"));
		tranList.add(new NeosSimpleDateTimeTranslator("MM月dd日", NeosDateTimeEntity.MonthFlag|NeosDateTimeEntity.DayFlag));
	}
}
