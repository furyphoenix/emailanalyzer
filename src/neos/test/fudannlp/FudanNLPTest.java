package neos.test.fudannlp;

import java.util.HashMap;
import java.util.List;

import edu.fudan.nlp.chinese.ner.Address;
import edu.fudan.nlp.chinese.ner.TimeNormalizer;
import edu.fudan.nlp.chinese.ner.TimeUnit;
import edu.fudan.nlp.tag.NERTagger;
import edu.fudan.nlp.tag.POSTagger;

public class FudanNLPTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String target = "今天我想到了。08年北京申办奥运会，8月8号开幕式，九月十八号闭幕式。" +
			"1年后的7月21号发生了件大事。" +
			"今天我本想去世博会，但是人太多了，直到晚上9点人还是那么多。" +
			"这几天考虑到明天和后天人还是那么多，决定下周日再去。王老师说，地址就在友谊西路53号。"+
			"老张告诉李长春，说西开公司明天早上八点半就可以在莲湖小区动工了。联合国和国务院还有商业银行专门发布了通知办这件事。";
//		TimeNormalizer normalizer;
//		normalizer = new TimeNormalizer("./data/TimeExp.gz");
//		normalizer.parse(target,"2010-09-01-10-15-20");
//		TimeUnit[] unit = normalizer.getTimeUnit();
//		for(int i = 0; i < unit.length; i++){
//			System.out.println(unit[i]);
//		}
		
		try {
			NERTagger tag = new NERTagger("./data/ner.p111014.gz");
			HashMap<String, String> s = tag.tag(target);
			System.out.println(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*try {
			POSTagger ptag=new POSTagger("data/pos.c110722.gz");
			System.out.println(ptag.tag(target));
		} catch (Exception e) {
			
			e.printStackTrace();
		}*/
		
		Address addr=new Address();
		List<String> addrList=addr.tag(target);
		for(String address:addrList){
			System.out.println(address);
		}

	}

}
