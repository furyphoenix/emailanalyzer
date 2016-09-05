package neos.tool.ip;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbIPLocator {
	private Connection conn;
	
	public DbIPLocator(Connection conn){
		this.conn=conn;
	}
	
	public DbIPLocation locate(String ip){
		return locate(ipValue(ip));
	}
	
	private static long ipValue(String ipStr){
		String[] segs=ipStr.split("\\.");
		long segA=Long.parseLong(segs[0]);
		long segB=Long.parseLong(segs[1]);
		long segC=Long.parseLong(segs[2]);
		long segD=Long.parseLong(segs[3]);
		long ipValue=(segA<<24)+(segB<<16)+(segC<<8)+segD;
		
		return ipValue;
	}
	
	public DbIPLocation locate(long ip){
		String query=buildQuery(ip);
		
		try{
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(query);
			
			while(rs.next()){
				String country=rs.getString("country_name");
				String region=rs.getString("region_name");
				String city=rs.getString("city");
				float lat=rs.getFloat("latitude");
				float lng=rs.getFloat("longitude");
				DbIPLocation loc=new DbIPLocation(country,region,city,lat,lng);
				st.close();
				return loc;
			}
			st.close();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private String buildQuery(long ip){
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT * From `knowledgedb`.`ip_group_city` where `ip_start`<=");
		sb.append(ip);
		sb.append(" Order By `ip_start` Desc Limit 1;");
		return sb.toString();
	}
}
