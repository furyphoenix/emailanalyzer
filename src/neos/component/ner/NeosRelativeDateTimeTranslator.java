package neos.component.ner;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import neos.util.ConvertResult;

public class NeosRelativeDateTimeTranslator implements NeosEntityTranslator {
	private final static Map<String, String> map=new Hashtable<String, String> ();	
	
	private final static String[] ThisDays={"今天","今日","今晨","今晚","今儿","today","tonight"};
	private final static String[] NextDays={"明天","明日","明晨","明晚","明儿","tomorrow"};
	private final static String[] Next2Days={"后天","后儿","day after tomorrow"};
	private final static String[] Next3Days={"大后天"};
	private final static String[] PrevDays={"昨天","昨晚","昨日","yesterday"};
	private final static String[] Prev2Days={"前天","前日","day before yesterday"};
	private final static String[] Prev3Days={"大前天"};
	private final static String[] NextDaysPattern={"\\d+天后","\\d+天以后","\\d+天之后"};
	private final static String[] PrevDaysPattern={"\\d+天前","\\d+天以前","\\d+天之前"};
	private final static String[] ThisWeekPattern={"本周\\d{1}","这周\\d{1}","这礼拜\\d{1}","本周\\d{1}","周\\d{1}","礼拜\\d{1}","星期\\d{1}"};
	private final static String[] NextWeekPattern={"下周\\d{1}","下礼拜\\d{1}","下个礼拜\\d{1}","下星期\\d{1}","下个星期\\d{1}"};
	private final static String[] PrevWeekPattern={"上周\\d{1}","上礼拜\\d{1}","上个礼拜\\d{1}","上星期\\d{1}","上个星期\\d{1}"};
	
	
	public NeosRelativeDateTimeTranslator(){
		this(new Date());
	}
	
	public NeosRelativeDateTimeTranslator(Date refDate){
		this(refDate, NeosDateTimeEntity.DefaultFlag);
	}
	
	public NeosRelativeDateTimeTranslator(Date refDate, int flag){
		
	}

	@Override
	public boolean isCapable(String src) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ConvertResult<NeosNamedEntity>> translate(String src) {
		// TODO Auto-generated method stub
		return null;
	}

}
