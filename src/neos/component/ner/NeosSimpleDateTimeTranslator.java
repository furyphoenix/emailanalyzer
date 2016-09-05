package neos.component.ner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import neos.util.ConvertResult;

public class NeosSimpleDateTimeTranslator implements NeosEntityTranslator {
	//private final String fmt;
	private final int flag;
	private final SimpleDateFormat sdf;
	
	public NeosSimpleDateTimeTranslator(String fmt, int flag){
		//this.fmt=fmt;
		this.flag=flag;
		this.sdf=new SimpleDateFormat(fmt, Locale.US);
	}

	@Override
	public boolean isCapable(String src) {
		try{
			sdf.parse(src);
		}catch(ParseException pe){
			return false;
		}
		
		return true;
	}

	@Override
	public List<ConvertResult<NeosNamedEntity>> translate(String src) {
		List<ConvertResult<NeosNamedEntity>> lst=new ArrayList<ConvertResult<NeosNamedEntity>> ();
		
		Date date;
		try {
			date = sdf.parse(src);
			NeosDateTimeEntity ne=new NeosDateTimeEntity(date, flag);
			ConvertResult<NeosNamedEntity> cr=new ConvertResult<NeosNamedEntity>(ne, 1.0f);
			lst.add(cr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return lst;
	}

}
