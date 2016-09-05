package neos.tool.fudannlp;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.fudan.nlp.chinese.ner.TimeNormalizer;
import edu.fudan.nlp.chinese.ner.TimeUnit;

public class NeosFudanTimeTool {
	private final static String model="./data/TimeExp.gz";
	private final static NeosFudanTimeTool tool=new NeosFudanTimeTool();
	private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
	private TimeNormalizer normalizer;
	
	
	private NeosFudanTimeTool(){
		normalizer = new TimeNormalizer(model);
	}
	
	public static NeosFudanTimeTool getInstance(){
		return tool;
	}
	
	public TimeUnit[] parse(String text, Date date){
		String day=fmt.format(date);		
		normalizer.parse(text, day);		
		return normalizer.getTimeUnit();
	}
	
	public TimeUnit[] parse(String text){
		return parse(text, new Date());
	}
	
	public static void main(String[] args){
		String testStr="日9月11日，我们看到了兵马俑。兵马俑是1958年10月23日在临潼发现的。至一九四九年十月一日建国以来，很多游客都来到过此处。到今年的十一月二十三日为止。下周一见。上周三。";
		
		NeosFudanTimeTool t=new NeosFudanTimeTool();
		TimeUnit[] units=t.parse(testStr);
		
		for(TimeUnit u:units){
			System.out.println(u.toString());
		}
	}
}
