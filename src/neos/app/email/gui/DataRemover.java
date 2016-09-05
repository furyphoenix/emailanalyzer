package neos.app.email.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import neos.app.util.FileOperate;

public class DataRemover {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost/", "root", "Iamabird");
            Statement st=conn.createStatement();
            st.executeUpdate("Drop Schema IF EXISTS `email`");
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileOperate.delAllFile(".\\email\\");
        FileOperate.delAllFile(".\\index\\attach\\");
        FileOperate.delAllFile(".\\attachments\\");

	}

}
