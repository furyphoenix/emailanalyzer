package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import neos.app.gui.ProgressMornitor;
import neos.tool.googlemap.NeosStaticMapTool;
import neos.tool.ip.DbIPLocation;
import neos.tool.ip.DbIPLocator;
import neos.tool.ip.IPLocation;
import neos.tool.ip.IPSeeker;
import java.awt.Toolkit;

public class IpLocationWin extends JFrame implements ProgressMornitor{

	private JPanel contentPane;
	private final JLabel labelStatus;
	private final JProgressBar progressBar;
	private final JTextField textField;
	private final JEditorPane editorPane;
	
	private final static String ipRegex=
		"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	
	private DbIPLocator dloc;
	private IPSeeker seeker;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IpLocationWin frame = new IpLocationWin();
					frame.setIPSeeker(new IPSeeker());
					frame.setDbIPLocator(new DbIPLocator(getDefaultConnection()));
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static Connection getDefaultConnection(){
		String driver = "com.mysql.jdbc.Driver";
		try {
            Class.forName(driver).newInstance();
            String dbKnowledgeUrl="jdbc:mysql://localhost/knowledgedb";
            Connection conn=DriverManager.getConnection(dbKnowledgeUrl, "root", "Iamabird");
            return conn;
        } catch (Exception ee) {
        	ee.printStackTrace();
        	return null;
        }
	}

	/**
	 * Create the frame.
	 */
	public IpLocationWin() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(IpLocationWin.class.getResource("/icon/1307955269_applications-systemg-icon.png")));
		setTitle("IP\u5730\u7406\u4FE1\u606F\u67E5\u8BE2");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		labelStatus = new JLabel("    ");
		panel.add(labelStatus);
		
		progressBar = new JProgressBar();
		panel.add(progressBar, BorderLayout.EAST);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(5, 10, 5, 10));
		panel_1.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BorderLayout(20, 0));
		
		JLabel lblIp = new JLabel("IP\u5730\u5740\uFF1A");
		panel_2.add(lblIp, BorderLayout.WEST);
		
		JButton button = new JButton("\u67E5\u8BE2");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							query();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_2.add(button, BorderLayout.EAST);
		
		textField = new JTextField();
		panel_2.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_3.add(scrollPane, BorderLayout.CENTER);
		
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		scrollPane.setViewportView(editorPane);
	}
	
	public void setIPSeeker(IPSeeker seeker){
		this.seeker=seeker;
	}
	
	public void setDbIPLocator(DbIPLocator loc){
		this.dloc=loc;
	}
	
	public static long ipValue(String ipStr){
		String[] segs=ipStr.split("\\.");
		long segA=Long.parseLong(segs[0]);
		long segB=Long.parseLong(segs[1]);
		long segC=Long.parseLong(segs[2]);
		long segD=Long.parseLong(segs[3]);
		long ipValue=(segA<<24)+(segB<<16)+(segC<<8)+segD;
		
		return ipValue;
	}
	
	private void query(){
		if(!Pattern.matches(ipRegex, textField.getText())){
			setMessage("错误：IP地址格式无效");
			return;
		}
		
		String ipStr=textField.getText();
		long ipVal=ipValue(ipStr);
		
		//editorPane.setText("");
		StringBuilder sb=new StringBuilder();
		if(seeker!=null){
			sb.append("国内IP数据库查询结果如下：\r\n");
			IPLocation location=seeker.getIPLocation(textField.getText());
			sb.append("国家："+location.getCountry()+"\r\n");
			sb.append("地区："+location.getArea()+"\r\n");
			sb.append("\r\n");
		}
		if(dloc!=null){
			sb.append("国外IP数据库查询结果如下：\r\n");
			DbIPLocation location=dloc.locate(ipVal);
			sb.append("国家："+location.countryName+"\r\n");
			sb.append("地区："+location.regionName+"\r\n");
			sb.append("城市："+location.cityName+"\r\n");
			sb.append("经度："+location.longitude+"\r\n");
			sb.append("维度："+location.latitude+"\r\n");
			sb.append("\r\n");
			
			NeosStaticMapTool tool=new NeosStaticMapTool();
			tool.addMark(location.latitude, location.longitude, "A", "red");
			System.out.println(tool.getUrl());
			MapWin mapWin=new MapWin();
			mapWin.setMapUrl(tool.getUrl());
			mapWin.setTitle(location.countryName+" "+location.regionName+" "+location.cityName);
			mapWin.setVisible(true);
		}
		
		editorPane.setText(sb.toString());
		
		setMessage("");
	}
	
	@Override
    public void setMessage(String mess) {
        labelStatus.setText("    " + mess);
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
