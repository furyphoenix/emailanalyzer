package neos.component.ner;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NeosDateTimeEntity implements NeosNamedEntity {

	private final Date m_date;
	private final Calendar m_cal;
	
	private int m_flag;
	public final static int SecondFlag=0x0001;
	public final static int MinuteFlag=0x0002;
	public final static int HourFlag=0x0004;
	public final static int DayFlag=0x0008;
	public final static int MonthFlag=0x0010;
	public final static int YearFlag=0x0020;
	
	public final static int DefaultFlag=YearFlag|MonthFlag|DayFlag;
	public final static int FullFlag=YearFlag|MonthFlag|DayFlag|HourFlag|MinuteFlag|SecondFlag;
	
	public NeosDateTimeEntity(Date date, int flag){
		m_date=date;
		m_flag=flag;
		m_cal=Calendar.getInstance(Locale.US);
		m_cal.setTime(m_date);
	}
	
	public NeosDateTimeEntity(Date date){
		this(date, DefaultFlag);
	}
	
	public Date getDateTime(){
		return m_date;
	}
	
	public int getYear(){
		return m_cal.get(Calendar.YEAR);
	}
	
	public int getMonth(){
		return m_cal.get(Calendar.MONTH);
	}
	
	public int getDay(){
		return m_cal.get(Calendar.DAY_OF_MONTH);
	}
	
	public int getHour(){
		return m_cal.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinute(){
		return m_cal.get(Calendar.MINUTE);
	}
	
	public int getSecond(){
		return m_cal.get(Calendar.SECOND);
	}
	
	@Override
	public NamedEntityType getType() {
		return NamedEntityType.DateTime;
	}

	@Override
	public int getFieldValidFlag() {
		return m_flag;
	}

}
