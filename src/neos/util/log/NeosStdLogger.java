package neos.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NeosStdLogger implements NeosLogger {
	private final static String fmt="yyyy-MM-dd hh:mm:ss";
	private final static SimpleDateFormat df=new SimpleDateFormat(fmt);
	private final static NeosStdLogger logger=new NeosStdLogger();
	
	
	private NeosStdLogger(){
		
	}
	
	public static NeosStdLogger getInstance(){
		return logger;
	}
	
	@Override
	public void debug(String msg) {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		sb.append("DEBUG");
		sb.append("][");
		sb.append(df.format(new Date()));
		sb.append("] ");
		sb.append(msg);
		System.out.println(sb.toString());

	}

	@Override
	public void error(String msg) {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		sb.append("ERROR");
		sb.append("][");
		sb.append(df.format(new Date()));
		sb.append("] ");
		sb.append(msg);
		System.out.println(sb.toString());

	}

	@Override
	public void fatal(String msg) {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		sb.append("FATAL");
		sb.append("][");
		sb.append(df.format(new Date()));
		sb.append("] ");
		sb.append(msg);
		System.out.println(sb.toString());


	}

	@Override
	public void info(String msg) {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		sb.append("INFO");
		sb.append("][");
		sb.append(df.format(new Date()));
		sb.append("] ");
		sb.append(msg);
		System.out.println(sb.toString());


	}

	@Override
	public void warn(String msg) {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		sb.append("WARN");
		sb.append("][");
		sb.append(df.format(new Date()));
		sb.append("] ");
		sb.append(msg);
		System.out.println(sb.toString());


	}

}
