package neos.app.email.gui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.Format;

public class ZipInfoWin extends AbstractInfoWin {
	private static final long serialVersionUID = -8096288287890271663L;
	//private EmailMainWin parent;
	private Connection knowledgeConn;
	private final static Format fmt=new DecimalFormat("000000");
	
	public ZipInfoWin(EmailMainWin parent){
		super(fmt);
		this.setTitle("邮政编码查询");
		this.setLabelInfo("邮政编码");
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
	
	private void query(int zip){
		String sql="Select * From `zipcode` Where zip="+zip;
		
		try{
			Statement st=knowledgeConn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			StringBuffer sb=new StringBuffer();
			while(rs.next()){
				sb.append(rs.getString("province"));
				sb.append(" ");
				sb.append(rs.getString("city"));
				sb.append(" ");
				sb.append(rs.getString("address"));
				sb.append("\r\n");
			}
			this.setText(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
			this.setMessage("错误：数据库查询错误。");
		}
	}

}
