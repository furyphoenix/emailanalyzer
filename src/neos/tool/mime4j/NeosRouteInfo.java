package neos.tool.mime4j;

import java.net.InetAddress;
import java.util.Date;

public class NeosRouteInfo {
	public final InetAddress fromAddr;
	public final InetAddress toAddr;
	public final Date date;
	
	public NeosRouteInfo(InetAddress from, InetAddress to, Date d){
		fromAddr=from;
		toAddr=to;
		date=d;
	}
}
