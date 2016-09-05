package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import neos.app.gui.ProgressMornitor;
import neos.app.util.FileOperate;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;

import java.sql.Connection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class EmailDbConfigWin extends JFrame implements ProgressMornitor{

	private JPanel contentPane;
	private JProgressBar progressBar;
	private JLabel lblStatus;
	private JPanel panel_1;
	private JPanel panel_2;
	private JButton btnConnect;
	private JPanel panel_3;
	private JLabel lblNewLabel;
	private JTextField textFieldDbLoc;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JTextField textFieldDbUser;
	private JLabel lblNewLabel_3;
	private JPasswordField passwordField;
	private JLabel lblNewLabel_4;
	private JTextField textFieldDbName;
	
	//custom code start here
	private Connection emailConn=null;
	private Connection knowledgeConn=null;
	private String databaseName=null;
	private EmailMainWin parent;
	private JButton btnCreate;
	private JButton btnClear;
	
	private final static String           jdbcEncStr  = "?useUnicode=true&characterEncoding=utf8";


	/**
	 * Create the frame.
	 */
	public EmailDbConfigWin(EmailMainWin win) {
		setTitle("\u6570\u636E\u5E93\u914D\u7F6E");
		setIconImage(Toolkit.getDefaultToolkit().getImage(EmailDbConfigWin.class.getResource("/icon/1307955269_applications-systemg-icon.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 260);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		lblStatus = new JLabel("    ");
		panel.add(lblStatus);
		
		progressBar = new JProgressBar();
		panel.add(progressBar, BorderLayout.EAST);
		
		panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setHgap(20);
		panel_1.add(panel_2, BorderLayout.SOUTH);
		
		btnConnect = new JButton("\u8FDE\u63A5");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							connectDb();
						}
					}.start();
				}catch(Exception ee){
					ee.printStackTrace();
				}
				connectDb();
			}
		});
		panel_2.add(btnConnect);
		
		btnCreate = new JButton("\u65B0\u5EFA");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							createDb();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_2.add(btnCreate);
		
		btnClear = new JButton("\u6E05\u7A7A");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							clearData();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_2.add(btnClear);
		
		panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new MigLayout("", "[][][][grow][]", "[][][][][]"));
		
		lblNewLabel = new JLabel("\u6570\u636E\u5E93\u5730\u5740\uFF1A");
		panel_3.add(lblNewLabel, "cell 1 1");
		
		textFieldDbLoc = new JTextField();
		textFieldDbLoc.setText("localhost");
		panel_3.add(textFieldDbLoc, "cell 3 1,growx");
		textFieldDbLoc.setColumns(10);
		
		lblNewLabel_1 = new JLabel("    ");
		panel_3.add(lblNewLabel_1, "cell 4 1");
		
		lblNewLabel_2 = new JLabel("\u6570\u636E\u5E93\u7528\u6237\uFF1A");
		panel_3.add(lblNewLabel_2, "cell 1 2");
		
		textFieldDbUser = new JTextField();
		textFieldDbUser.setText("root");
		panel_3.add(textFieldDbUser, "cell 3 2,growx");
		textFieldDbUser.setColumns(10);
		
		lblNewLabel_3 = new JLabel("\u6570\u636E\u5E93\u5BC6\u7801\uFF1A");
		panel_3.add(lblNewLabel_3, "cell 1 3");
		
		passwordField = new JPasswordField();
		panel_3.add(passwordField, "cell 3 3,growx");
		
		lblNewLabel_4 = new JLabel("\u6570\u636E\u5E93\u540D\u79F0\uFF1A");
		panel_3.add(lblNewLabel_4, "cell 1 4");
		
		textFieldDbName = new JTextField();
		textFieldDbName.setText("email");
		panel_3.add(textFieldDbName, "cell 3 4,growx");
		textFieldDbName.setColumns(10);
		
		//custom code start here
		parent=win;
	}
	
	private void createDb(){
		String dbLoc    = textFieldDbLoc.getText();
        String dbName   = textFieldDbName.getText();
        String dbUser   = textFieldDbUser.getText();
        String password = new String(passwordField.getPassword());
        
        if((dbLoc.length()==0)||(dbName.length()==0)||(dbUser.length()==0||(password.length()==0))){
        	setMessage("错误：请填写完整相关信息。");
        	return;
        }
        
        if(EmailDbBuilder.initDb(dbLoc, 3306, dbUser, password, dbName)<0){
        	setMessage("错误：创建数据库失败。");
        }
        
        if(!EmailDbBuilder.isDbExist(dbLoc, 3306, dbUser, password, "knowledgedb")){
        	setMessage("知识数据库导入中...");
            setProgress(true);
            String cmd="mysql.exe --host="+dbLoc+" --user="+dbUser+" --password="+password+" < \".\\data\\knowledgedb.sql\" ";
    		String s;
    		Process process;
    		try {
    			process = Runtime.getRuntime().exec(cmd);
    			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    			while((s=bufferedReader.readLine()) != null){
    				System.out.println(s);
    			}
    			process.waitFor();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }

		setProgress(false);
        setMessage("数据库初始化完成");
	}
	
	
	
	private void connectDb(){
		String dbLoc    = textFieldDbLoc.getText();
        String dbName   = textFieldDbName.getText();
        String dbUser   = textFieldDbUser.getText();
        String password = new String(passwordField.getPassword());
        String dbDriver = "com.mysql.jdbc.Driver";
        
        if (emailConn != null) {
            try {
                emailConn.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        
        if (knowledgeConn != null) {
            try {
                knowledgeConn.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        
        try {
            Class.forName(dbDriver).newInstance();
        } catch (Exception e) {
            lblStatus.setText("错误：无法载入JDBC驱动");
            clearConnection();

            return;
        }
        
        String emailDbUrl = "jdbc:mysql://" + dbLoc + "/" + dbName+jdbcEncStr;
        
        databaseName=dbName;

        lblStatus.setText("连接数据库中……");
        
        try {
            emailConn = DriverManager.getConnection(emailDbUrl, dbUser, password);
        } catch (SQLException se) {
            lblStatus.setText("错误：连接邮件数据库失败。");
            
            clearConnection();

            return;
        }

        if (emailConn == null) {
            lblStatus.setText("错误：连接邮件数据库失败。");
            
            clearConnection();

            return;
        }
        
        String knowledgeDbUrl = "jdbc:mysql://" + dbLoc + "/" + "knowledgedb";
        
        try {
            knowledgeConn = DriverManager.getConnection(knowledgeDbUrl, dbUser, password);
        } catch (SQLException se) {
            lblStatus.setText("错误：连接知识数据库失败。");
            
            clearConnection();

            return;
        }

        if (emailConn == null) {
            lblStatus.setText("错误：连接知识数据库失败。");
            
            clearConnection();

            return;
        }
        
        File dir=new File(".\\email\\"+dbName);
        if(!dir.exists()){
        	dir.mkdirs();
        }
        if(!dir.isDirectory()){
        	lblStatus.setText("错误：无法建立邮件备份目录。");
        	 clearConnection();
        	 
        	 return;
        }
        
        dir=new File(".\\index\\content\\"+dbName);
        if(!dir.exists()){
        	dir.mkdirs();
        }
        if(!dir.isDirectory()){
        	lblStatus.setText("错误：无法建立邮件正文索引目录。");
        	 clearConnection();
        	 
        	 return;
        }
        
        dir=new File(".\\index\\attach\\"+dbName);
        if(!dir.exists()){
        	dir.mkdirs();
        }
        if(!dir.isDirectory()){
        	lblStatus.setText("错误：无法建立邮件附件索引目录。");
        	 clearConnection();
        	 
        	 return;
        }
        
        dir=new File(".\\attachments\\"+dbName);
        if(!dir.exists()){
        	dir.mkdirs();
        }
        if(!dir.isDirectory()){
        	lblStatus.setText("错误：无法建立邮件附件目录。");
        	 clearConnection();
        	 
        	 return;
        }
        
        parent.setDbConnection(emailConn, databaseName, knowledgeConn);
        lblStatus.setText("连接成功。");
        this.dispose();
        
	}
	
	private void clearData(){
		if(!confirmClearDb()){
			return;
		}
		
		String dbLoc    = textFieldDbLoc.getText();
		String dbUser   = textFieldDbUser.getText();
        String password = new String(passwordField.getPassword());
		String dbName =textFieldDbName.getText();
		
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn=DriverManager.getConnection("jdbc:mysql://"+dbLoc+"/", dbUser, password);
            Statement st=conn.createStatement();
            st.executeUpdate("Drop Schema IF EXISTS `"+dbName+"`");
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("错误：删除数据失败。请检查数据库配置。");
            return;
        }
        FileOperate.delAllFile(".\\email\\"+dbName);
        FileOperate.delFile(".\\email\\"+dbName);
        FileOperate.delAllFile(".\\index\\attach\\"+dbName);
        FileOperate.delFile(".\\index\\attach\\"+dbName);
        FileOperate.delAllFile(".\\index\\content\\"+dbName);
        FileOperate.delFile(".\\index\\content\\"+dbName);
        FileOperate.delAllFile(".\\attachments\\"+dbName);
        FileOperate.delFile(".\\attachments\\"+dbName);
        lblStatus.setText("指定数据库及相关数据已清空。");
	}
	
	private void clearConnection(){
		emailConn=null;
        knowledgeConn=null;
        databaseName=null;
        parent.setDbConnection(null, null, null);
	}
	
	private boolean confirmClearDb(){
		JDialog.setDefaultLookAndFeelDecorated(true);
		int response=JOptionPane.showConfirmDialog(this, "即将清除指定数据库及其所有相关数据，确认操作？","警告",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(response==JOptionPane.YES_OPTION){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	@Override
    public void setMessage(String mess) {
        lblStatus.setText("    " + mess);
    }

    @Override
    public void setProgress(boolean indetermin) {
        progressBar.setIndeterminate(indetermin);
    }

    @Override
    public void setProgress(int n) {
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }

        progressBar.setValue(n);
    }

}
