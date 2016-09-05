package neos.component.ner;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import neos.util.ConvertResult;

public class NeosSimpleChineseDateTimeTranslator extends
		NeosSimpleDateTimeTranslator {
	private final static char[] ChineseDigitArray={'零','一','二','三','四','五','六','七','八','九','〇','O','十','日','天'};
	private final static char[] DigitArray={'0','1','2','3','4','5','6','7','8','9','0','0','+','7','7'};
	private final static Map<Character, Character> tab=new Hashtable<Character, Character> ();
	
	static{
		for(int i=0; i<ChineseDigitArray.length; i++){
			tab.put(ChineseDigitArray[i], DigitArray[i]);
		}
	}

	public NeosSimpleChineseDateTimeTranslator(String fmt, int flag) {
		super(fmt, flag);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isCapable(String src) {
		String str=transform(src);
		
		return super.isCapable(str);
	}
	
	@Override
	public List<ConvertResult<NeosNamedEntity>> translate(String src){
		return super.translate(transform(src));
	}
	
	private static String transform(String src){
		char[] chs=src.toCharArray();
		StringBuffer sb=new StringBuffer();
		char prevCh=0;
		for(int i=0; i<chs.length; i++){
			if(tab.containsKey(chs[i])){
				char digit=tab.get(chs[i]);
				if(chs[i]=='十'){
					if((i<1)||(!Character.isDigit(prevCh))){
						sb.append('1');
					}
					prevCh=digit;
					continue;
				}
				sb.append(tab.get(chs[i]));
				prevCh=digit;
			}else{
				sb.append(chs[i]);
				prevCh=chs[i];
			}
		}
		
		return sb.toString();
		
	}
	
	public static void main(String[] args){
		String test="二零一一年十二月二十三日十一时三十三分57秒，试验成功！";
		
		System.out.println(transform(test));
	}
}
