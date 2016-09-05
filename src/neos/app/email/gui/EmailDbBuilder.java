package neos.app.email.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class EmailDbBuilder {
    private static String dbDriver = "com.mysql.jdbc.Driver";
    
    private final static String EMAIL_ATTACHMENT_TAB = " `emailattach` ";
    private final static String EMAIL_CONTENT_TAB = " `emailcontent` ";
    private final static String EMAIL_FROM_TO_TAB = " `emailfromto` ";
    private final static String EMAIL_HEADER_TAB = " `emailheader` ";
    /*private final static String ENTITY_DATETIME_TAB = " `entitydatetime` ";
    private final static String ENTITY_LOCATOIN_TAB = " `entitylocation` ";
    private final static String ENTITY_PERSONNAME_TAB = " `entityperson` ";
    private final static String ENTITY_IDCARD_TAB = " `entityidcard` ";
    private final static String ENTITY_MOBILE_TAB = " `entitymobile` ";
    private final static String ENTITY_PHONE_TAB = " `entityphone` ";
    private final static String ENTITY_URL_TAB = " `entityurl` ";
    private final static String ENTITY_EMAIL_TAB = " `entityemail` ";
    private final static String INDEX_STRING_TAB = " `indexstring` ";*/
    //private final static String INDEX_EXPRESSION_TAB = " `indexexpression` ";
    //private final static String INDEX_TRANSFORM_TAB = " `indextransform` ";
    private final static String EMAIL_ENTITY_TAB=" `emailentity` ";
    private final static String NOTE_EMAIL_TAB = " `emailnote` ";
    private final static String NOTE_EMAILBOX_TAB = " `emailboxnote` ";
    private final static String NOTE_FREE_TAB = " `freenote` ";
    private final static String           jdbcEncStr  = "?useUnicode=true&characterEncoding=utf8";

    public static void main(String[] args){
    	/*int code=initDb("localhost", 3306, "root", "Iamabird", "email");
    	if(code==0){
    		System.out.println("Success!");
    	}*/
    	if(isDbExist("localhost", 3306, "root", "Iamabird","knowledgedb")){
    		System.out.println("Oh! Knowledge!");
    	}
    	if(isDbExist("localhost", 3306, "root", "Iamabird","knowl")){
    		System.out.println("Oh!No!");
    	}
    }
    
    public static boolean isDbExist(String location, int port, String user,
            String pass, String dbName){
    	Connection conn = initConnection(location, port, user, pass);

        if (conn == null) {
            return false;
        }
        
        String sql="Select Count(*) From `information_schema`.`SCHEMATA` Where lower(`SCHEMA_NAME`)=lower(\""+dbName+"\")";
        try{
        	Statement st=conn.createStatement();
        	ResultSet rs=st.executeQuery(sql);
        	while(rs.next()){
        		int cnt=rs.getInt(1);
        		if(cnt!=1){
        			st.close();
        			conn.close();
        			return false;
        		}else{
        			st.close();
        			conn.close();
        			return true;
        		}
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        return false;
    }
    
    public static int initDb(String location, int port, String user,
        String pass, String dbName) {
        Connection conn = initConnection(location, port, user, pass);

        if (conn == null) {
            return -1;
        }

        initDatabase(conn, dbName);
        initEmailHeaderTab(conn, dbName);
        initEmailFromToTab(conn, dbName);
        initEmailContentTab(conn, dbName);
        initEmailNoteTab(conn, dbName);
        initEmailBoxNoteTab(conn, dbName);
        initFreeNoteTab(conn, dbName);
        initEmailEntityTab(conn, dbName);
        initEmailAttachmentTab(conn, dbName);

        return 0;
    }

    private static Connection initConnection(String location, int port,
        String user, String pass) {
        try {
            Class.forName(dbDriver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        String dbUrl = "jdbc:mysql://" + location + ":" + port + "/"+jdbcEncStr;

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(dbUrl, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    private static void initDatabase(Connection conn, String dbName) {
        String sql = "Create Database IF NOT EXISTS `" + dbName + "`";

        try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initEmailHeaderTab(Connection conn, String dbName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(EMAIL_HEADER_TAB);
        sb.append("( ");
        sb.append("`ID` int(11) NOT NULL AUTO_INCREMENT,");
        sb.append("`FromAddr` varchar(100) NOT NULL,");
        sb.append("`SendDate` datetime NOT NULL,");
        sb.append("`MailSubject` tinytext DEFAULT NULL,");
        sb.append("PRIMARY KEY (`ID`),");
        sb.append("KEY `idxFromDate` (`FromAddr`,`SendDate`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");

        String sql = sb.toString();

        try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initEmailFromToTab(Connection conn, String dbName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(EMAIL_FROM_TO_TAB);
        sb.append("( ");
        sb.append("`id` int(11) NOT NULL AUTO_INCREMENT,");
        sb.append("`EmailID` int(11) NOT NULL,");
        sb.append("`FromAddr` varchar(100) NOT NULL,");
        sb.append("`SendDate` datetime NOT NULL,");
        sb.append("`ToAddr` varchar(100) NOT NULL,");
        sb.append(
            "`AddrType` enum('TO_TYPE','CC_TYPE','BCC_TYPE') DEFAULT NULL,");
        sb.append("PRIMARY KEY (`id`),");
        sb.append("KEY `idxFromTo` (`SendDate`,`FromAddr`,`ToAddr`),");
        sb.append("KEY `idxTo` (`ToAddr`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");

        String sql = sb.toString();

        try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initEmailContentTab(Connection conn, String dbName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(EMAIL_CONTENT_TAB);
        sb.append("( ");
        sb.append("`id` int(11) NOT NULL AUTO_INCREMENT,");
        sb.append("`EmailID` int(11) NOT NULL,");
        sb.append("`FromAddr` varchar(100) NOT NULL,");
        sb.append("`SendDate` datetime NOT NULL,");
        sb.append("`IP` varchar(15) DEFAULT NULL,");
        sb.append("`PlainMail` mediumtext,");
        sb.append("`HtmlMail` mediumtext,");
        sb.append("`TextMail` mediumtext,");
        sb.append("PRIMARY KEY (`id`),");
        sb.append("KEY `idxEmailId` (`EmailID`),");
        sb.append("KEY `idxFromDate` (`FromAddr`, `SendDate`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");

        String sql = sb.toString();

        try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void initEmailNoteTab(Connection conn, String dbName){
    	StringBuilder sb=new StringBuilder();
    	
    	sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(NOTE_EMAIL_TAB);
        sb.append("( ");
        sb.append("`id` int(11) NOT NULL AUTO_INCREMENT,");
        sb.append("`NoteDate` datetime NOT NULL,");
        sb.append("`Author` varchar(40),");
        sb.append("`EmailID` int(11) NOT NULL,");
        sb.append("`NoteContent` text,");
        sb.append("PRIMARY KEY (`id`),");
        sb.append("KEY `idxAuthor` (`Author`),");
        sb.append("KEY `idxEmail` (`EmailID`, `NoteDate`, `Author`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        
    	String sql=sb.toString();
    	try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void initEmailBoxNoteTab(Connection conn, String dbName){
    	StringBuilder sb=new StringBuilder();
    	
    	sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(NOTE_EMAILBOX_TAB);
        sb.append("( ");
        sb.append("`id` int(11) NOT NULL AUTO_INCREMENT,");
        sb.append("`NoteDate` datetime NOT NULL,");
        sb.append("`Author` varchar(40),");
        sb.append("`EmailBox` varchar(128) NOT NULL,");
        sb.append("`NoteContent` text,");
        sb.append("PRIMARY KEY (`id`),");
        sb.append("KEY `idxAuthor` (`Author`),");
        sb.append("KEY `idxEmailBox` (`EmailBox`, `NoteDate`, `Author`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        
    	String sql=sb.toString();
    	try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void initFreeNoteTab(Connection conn, String dbName){
    	StringBuilder sb=new StringBuilder();
    	
    	sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(NOTE_FREE_TAB);
        sb.append("( ");
        sb.append("`id` int(11) NOT NULL AUTO_INCREMENT,");
        sb.append("`NoteDate` datetime NOT NULL,");
        sb.append("`Author` varchar(40),");
        sb.append("`NoteContent` text,");
        sb.append("PRIMARY KEY (`id`),");
        sb.append("KEY `idxAuthor` (`Author`, `Notedate`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        
    	String sql=sb.toString();
    	try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void initEmailEntityTab(Connection conn, String dbName){
    	StringBuilder sb=new StringBuilder();
    	
    	sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(EMAIL_ENTITY_TAB);
        sb.append("(");
        sb.append("`id` INT NOT NULL AUTO_INCREMENT ,");
        sb.append("`EmailId` INT NOT NULL ,");
        sb.append("`Entity` VARCHAR(255) NOT NULL ,");
        sb.append("`EntityType` ENUM('DateTime', 'PersonName', 'LocationName', 'OrgnizationName', 'EmailAddress', 'URL', 'PhoneNumber', 'IDCardNumber', 'PostalCode', 'GeneralNumber') NOT NULL,");
        sb.append("`Offset` INT NOT NULL DEFAULT 0 ,");
        sb.append("PRIMARY KEY (`id`) ,");
        sb.append("INDEX `EidOffTypeIdx` (`EmailId` ASC, `Offset` ASC, `EntityType` ASC) ,");
        sb.append("INDEX `EntityIdx` (`Entity` ASC)");
        sb.append(")");
        sb.append("ENGINE = InnoDB DEFAULT CHARSET=utf8");
        
        String sql=sb.toString();
    	try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void initEmailAttachmentTab(Connection conn, String dbName){
    	StringBuilder sb=new StringBuilder();
    	
    	sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(EMAIL_ATTACHMENT_TAB);
        sb.append("(");
        sb.append("`id` INT NOT NULL AUTO_INCREMENT ,");
        sb.append("`EmailId` INT NOT NULL ,");
        sb.append("`FileName` VARCHAR(128) NOT NULL ,");
        sb.append("`StorePath` VARCHAR(255) NOT NULL ,");
        sb.append("`FileLen` BIGINT NOT NULL ,");
        sb.append("`FileMD5` VARCHAR(32) NOT NULL ,");
        sb.append("PRIMARY KEY (`id`) ,");
        sb.append("INDEX `eidIdx` (`EmailId` ASC) ,");
        sb.append("INDEX `md5LenIdx` (`FileMD5` ASC, `FileLen` ASC) ");
        sb.append(")");
        sb.append("ENGINE = InnoDB DEFAULT CHARACTER SET = utf8");
        
        String sql=sb.toString();
    	try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*private static void initIndexStringTab(Connection conn, String dbName){
    	StringBuilder sb=new StringBuilder();
    	
    	sb.append("Create Table IF NOT EXISTS `");
        sb.append(dbName);
        sb.append("`.");
        sb.append(INDEX_STRING_TAB);
        sb.append("( ");
        sb.append("`StringID` int(11) NOT NULL AUTO_INCREMENT,");
        sb.append("`StringExp` varchar(255) NOT NULL,");
        sb.append("PRIMARY KEY (`StringID`),");
        sb.append("KEY `idxString` (`StringExp`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        
    	String sql=sb.toString();
    	try {
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void initIndexExpressionTab(Connection conn, String dbName){
    	
    }*/
    
    
}
