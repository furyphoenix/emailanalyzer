package neos.app.email.gui;

import java.sql.Connection;
import java.text.Format;

public class SiteInfoWin extends AbstractInfoWin {
	private Connection knowledgeConn;
	
	public SiteInfoWin(EmailMainWin parent){
		super(null);
		this.setTitle("域名信息查询");
		this.setLabelInfo("域名地址");
		knowledgeConn=parent.getKnowledgeDbConnect();
	}
	
	
	@Override
	public void onQuery() {
		

	}

}
