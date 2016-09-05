package neos.app.email.gui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.Format;

public class MobileInfoWin extends AbstractInfoWin {
	private Connection knowledgeConn;
	private final static Format fmt=new DecimalFormat("00000000000");
	
	public MobileInfoWin(EmailMainWin parent){
		super(fmt);
		this.setTitle("手机归属地查询");
		this.setLabelInfo("手机号码：");
		knowledgeConn=parent.getKnowledgeDbConnect();
	}
	
	@Override
	public void onQuery() {
		String text=this.getInput();
		if((text!=null)&&(text.length()>0)){
			query(Integer.parseInt(text.substring(0,7)));
		}else{
			this.setMessage("输入格式无效。请重新输入。");
		}

	}
	
	private void query(int num){
		String sql="Select * From `mobilephone` Where num="+num;
		
		try{
			Statement st=knowledgeConn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			StringBuffer sb=new StringBuffer();
			while(rs.next()){
				sb.append(rs.getString("city"));
				sb.append("(0");
				sb.append(rs.getInt("areacode"));
				sb.append(")    "); 
				sb.append(rs.getString("cardtype"));
				sb.append("\r\n");
			}
			this.setText(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
			this.setMessage("错误：数据库查询错误。");
		}
	}

}
