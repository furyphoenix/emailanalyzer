package neos.app.email.control;

import java.sql.Connection;

public abstract class AbstractEmailController {
	protected Connection conn;
	protected String dbName;
	
	public AbstractEmailController(Connection conn, String dbName){
		this.conn=conn;
		this.dbName=dbName;
	}
	
	
}
