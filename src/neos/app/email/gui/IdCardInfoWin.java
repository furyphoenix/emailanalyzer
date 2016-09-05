package neos.app.email.gui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.Format;

public class IdCardInfoWin extends AbstractInfoWin {
	private Connection knowledgeConn;
	private final static Format fmt=new DecimalFormat("000000");
	
	public IdCardInfoWin(EmailMainWin parent){
		super(fmt);
		this.setTitle("身份证归属地查询");
		this.setLabelInfo("身份证前六位：");
		knowledgeConn=parent.getKnowledgeDbConnect();
	}
	
	@Override
	public void onQuery() {
		String text=this.getInput();
		if((text!=null)&&(text.length()>0)){
			query(Integer.parseInt(text));
		}else{
			this.setMessage("输入格式无效。请重新输入。");
		}

	}
	
	private void query(int prefix){
		String sql="Select * From `idcard` Where idprefix="+prefix;
		
		try{
			Statement st=knowledgeConn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			StringBuffer sb=new StringBuffer();
			while(rs.next()){
				sb.append(rs.getString("area"));
				sb.append("\r\n");
			}
			this.setText(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
			this.setMessage("错误：数据库查询错误。");
		}
	}

}
