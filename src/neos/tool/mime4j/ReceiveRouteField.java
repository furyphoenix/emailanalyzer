package neos.tool.mime4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.field.AbstractField;
import org.apache.james.mime4j.field.UnstructuredField;
import org.apache.james.mime4j.util.ByteSequence;

public class ReceiveRouteField extends AbstractField {
	public final static String FieldName="Received";
	private static Log log = LogFactory.getLog(ReceiveRouteField.class);
	private boolean parsed = false;
	
	private InetAddress fromAddr;
	private InetAddress byAddr;
	private Date date;
	
	private final static SimpleDateFormat dateFmt=new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss", Locale.US);
	private final static String dateRegx="[A-Z][a-z]{2}, \\d{1,2} [A-Z][a-z]{2} \\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}";
	private final static Pattern datePat=Pattern.compile(dateRegx);
	private final static String urlRegx="[a-zA-Z][\\w-]+(\\.[\\w-]+)+";
	private final static Pattern urlPat=Pattern.compile(urlRegx);
	private final static String ipRegx="\\d{1,3}(\\.\\d{1,3}){3}";
	private final static Pattern ipPat=Pattern.compile(ipRegx);
	private final static String ipExtRegx="(\\d{1,3}(\\.\\d{1,3}){3})(\\:\\d{1,5})?";
	private final static Pattern ipExtPat=Pattern.compile(ipExtRegx);
	private final static String fromRegx="from\\s+("+"("+urlRegx+")|(\\["+ipRegx+"\\]))\\s+"+"(\\(\\["+ipExtRegx+"\\])?";
	private final static Pattern fromPat=Pattern.compile(fromRegx);
	private final static String byRegx="by\\s+(("+urlRegx+")|("+ipRegx+"))";
	private final static Pattern byPat=Pattern.compile(byRegx);
	
	public ReceiveRouteField(String name, String body, ByteSequence raw){
		super(name, body, raw);
	}
	
	public ReceiveRouteField(UnstructuredField uf){
		super(uf.getName(), uf.getBody(), uf.getRaw());
	}
	
	public InetAddress getFromAddress(){
		if(!parsed){
			parse();
		}
		return fromAddr;
	}
	
	public InetAddress getByAddress(){
		if(!parsed){
			parse();
		}
		return byAddr;
	}
	
	
	public Date getDateTime(){
		if(!parsed){
			parse();
		}
		return date;
	}
	
	private void parse(){
		String body=getBody();

		fromAddr=parseFromAddr(body);
		byAddr=parseByAddr(body);
		
		date=parseDate(body);
		
		parsed=true;

	}
	
	private static InetAddress parseFromAddr(String src){
		/*Matcher m=fromPat.matcher(src);
		
		String urlFromStr=null;
		String ipFromStr=null;
		
		while(m.find()){
			String fromStr=m.group();
			Matcher urlFromMatcher=urlPat.matcher(fromStr);
			
			while(urlFromMatcher.find()){
				urlFromStr=urlFromMatcher.group();
				break;
			}
			Matcher ipFromMatcher=ipPat.matcher(fromStr);
			
			while(ipFromMatcher.find()){
				ipFromStr=ipFromMatcher.group();
				break;
			}
			
			break;
		}
		
		try{
			if((urlFromStr==null)&&(ipFromStr==null)){
				return null;
			}else if((urlFromStr!=null)&&(ipFromStr==null)){
				return InetAddress.getByName(urlFromStr);
			}else if((urlFromStr==null)&&(ipFromStr!=null)){
				return InetAddress.getByAddress(ipString2ByteArray(ipFromStr));
			}else{
				return InetAddress.getByAddress(urlFromStr, ipString2ByteArray(ipFromStr));
			}
		}catch(UnknownHostException e){
			e.printStackTrace();
			return null;
		}*/
		int idxFrom=src.indexOf("from");
		int idxBy=src.indexOf("by");
		if((idxFrom>=0)&&(idxBy>idxFrom)){
			String seg=src.substring(idxFrom, idxBy);
			Matcher m=ipPat.matcher(seg);
			while(m.find()){
				String ipStr=m.group();
				try {
					return InetAddress.getByAddress(ipString2ByteArray(ipStr));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
		
	}
	
	private static InetAddress parseByAddr(String src){
		Matcher m=byPat.matcher(src);
		
		String urlByStr=null;
		String ipByStr=null;
		
		while(m.find()){
			String byStr=m.group();
			Matcher urlByMatcher=urlPat.matcher(byStr);
			
			while(urlByMatcher.find()){
				urlByStr=urlByMatcher.group();
				break;
			}
			Matcher ipByMatcher=ipPat.matcher(byStr);
			
			while(ipByMatcher.find()){
				ipByStr=ipByMatcher.group();
				break;
			}
			
			break;
		}
		
		try{
			if((urlByStr==null)&&(ipByStr==null)){
				return null;
			}else if((urlByStr!=null)&&(ipByStr==null)){
				return InetAddress.getByName(urlByStr);
			}else if((urlByStr==null)&&(ipByStr!=null)){
				return InetAddress.getByAddress(ipString2ByteArray(ipByStr));
			}else{
				return InetAddress.getByAddress(urlByStr, ipString2ByteArray(ipByStr));
			}
		}catch(UnknownHostException e){
			return null;
		}
	}
	
	
	
	public static byte[] ipString2ByteArray(String ip){
		byte[] bytes=new byte[4];
		
		String[] parts=ip.split("\\.");
		for(int i=0; i<parts.length; i++){
			bytes[i]=(byte)Integer.parseInt(parts[i]);
		}
		
		return bytes;
	}
	
	public static String ipByteArray2String(byte[] bytes){
		StringBuilder sb=new StringBuilder();
		for(int i=0; i<bytes.length-1; i++){
			sb.append(Byte.toString(bytes[i]));
			sb.append(".");
		}
		sb.append(Byte.toString(bytes[bytes.length-1]));
		
		return sb.toString();
		
	}
	
	private synchronized static Date parseDate(String src){
		Matcher m=datePat.matcher(src);
		Date date=null;
		while(m.find()){
			try {
				date=dateFmt.parse(m.group());
			} catch (Exception e) {
				
				e.printStackTrace();
				System.out.println("[src]"+src);
				System.out.println("[group]"+m.group());
			}
			break;
		}
		
		return date;
		
	}
	
	public static void main(String[] args){
		String info1="by 10.229.221.141 with HTTP; Tue, 22 Feb 2011 21:59:06 -0800 (PST)";
		String info2="by 10.229.99.143 with SMTP id u15mr53631qcn.206.1298440747145; Tue, 22 Feb 2011 21:59:07 -0800 (PST)";
		String info3="by mail-qy0-f175.google.com with SMTP id 35so2602941qyk.13        for <hmily_yu@hotmail.com>; Tue, 22 Feb 2011 21:59:07 -0800 (PST)";
		String info4="from mail-qy0-f175.google.com ([209.85.216.175]) by SNT0-MC1-F3.Snt0.hotmail.com with Microsoft SMTPSVC(6.0.3790.4675);	 Tue, 22 Feb 2011 21:59:07 -0800";
		String info5="from [10.92.17.3] ([10.92.17.3:16285] helo=ii113-11.friendfinderinc.com) by ii11-2.friendfinderinc.com (envelope-from <bounce-return-furyphoenix@bounce.cams.com>) (ecelerity 2.2.2.40 r(29895/29896)) with ESMTP id 49/0B-05857-B7E025D4; Tue, 08 Feb 2011 19:48:11 -0800";
		String[] infos={info1, info2, info3, info4, info5};
		for(int i=0; i<infos.length; i++){
			Date date=parseDate(infos[i]);
			if(date!=null){
				System.out.print("["+dateFmt.format(date)+"] ");
			}
			
			InetAddress fromAddr=parseFromAddr(infos[i]);
			if(fromAddr!=null){
				System.out.print("from: "+fromAddr.getHostName()+" ("+fromAddr.getHostAddress()+") ");
			}
			
			InetAddress byAddr=parseByAddr(infos[i]);
			if(byAddr!=null){
				System.out.print("to: "+byAddr.getHostName()+" ("+byAddr.getHostAddress()+") ");
			}
			
			System.out.println();
		}
		
	}
}
