package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Dimension;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JComboBox;

import neos.tool.googlemap.NeosStaticMapTool;
import neos.tool.ip.DbIPLocation;
import neos.tool.ip.DbIPLocator;
import neos.tool.ip.IPSeeker;
import neos.tool.mime4j.ReceiveRouteField;

import org.jdesktop.swingx.JXDatePicker;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EmailIpSourceWin extends NeosStandardFrame {
	private JTextField textFieldEmail;
	private JXDatePicker dpStart;
	private JXDatePicker dpEnd;
	private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
	private JTable table;
	private final static String[] header={"IP","域名","计数","国内数据库转换结果","国外数据库转换结果"};
	private DefaultTableModel model=new DefaultTableModel(header,0);
	private final static IPSeeker seeker=new IPSeeker();
	private DbIPLocator locator;
	private final EmailMainWin parent;
	private List<Float> lngList=new ArrayList<Float> ();
	private List<Float> latList=new ArrayList<Float> ();
	private final static String ipRegex=
		"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	

	/**
	 * Create the frame.
	 */
	public EmailIpSourceWin(EmailMainWin parent) {
		super();
		setTitle("\u90AE\u4EF6\u6765\u6E90\u8FFD\u8E2A");
		this.setBounds(100, 100, 800, 600);
		
		JPanel panel = new JPanel();
		getCenterPanel().add(panel, BorderLayout.NORTH);
		
		JLabel label = new JLabel("\u7535\u5B50\u90AE\u7BB1\u5730\u5740\uFF1A");
		panel.add(label);
		
		textFieldEmail = new JTextField();
		textFieldEmail.setMinimumSize(new Dimension(120, 21));
		textFieldEmail.setPreferredSize(new Dimension(120, 21));
		panel.add(textFieldEmail);
		textFieldEmail.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("    ");
		panel.add(lblNewLabel);
		
		JLabel label_1 = new JLabel("\u65E5\u671F\u8303\u56F4\uFF1A");
		panel.add(label_1);
		
		dpStart = new JXDatePicker(new Date());
		dpStart.setFormats(fmt);
		panel.add(dpStart);
		
		JLabel lblNewLabel_1 = new JLabel("\u81F3");
		panel.add(lblNewLabel_1);
		
		dpEnd = new JXDatePicker(new Date());
		dpEnd.setFormats(fmt);
		panel.add(dpEnd);
		
		JLabel label_2 = new JLabel("        ");
		panel.add(label_2);
		
		JButton btnQuery = new JButton("\u67E5\u8BE2");
		btnQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							findSource();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel.add(btnQuery);
		
		JPanel panel_1 = new JPanel();
		getCenterPanel().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		
		JPanel panel_2 = new JPanel();
		getCenterPanel().add(panel_2, BorderLayout.SOUTH);
		
		JButton btnMap = new JButton("\u67E5\u770B\u5730\u56FE");
		btnMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							showMap();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_2.add(btnMap);
		
		//custom code start here
		this.parent=parent;
		locator=new DbIPLocator(parent.getKnowledgeDbConnect());
	}
	
	private void findSource(){
		String email=textFieldEmail.getText();
		if(email.length()<=0){
			return;
		}
		
		Date start=dpStart.getDate();
		Date end=dpEnd.getDate();
		if(start.after(end)){
			setMessage("错误：结束日期不可早于起始日期。");
			return;
		}
		
		setMessage("查询中……");
		model.setRowCount(0);
		lngList.clear();
		latList.clear();
		
		Connection emailConn=parent.getEmailDbConnect();
		String dbName=parent.getEmailDbName();
		
		StringBuilder sb=new StringBuilder();
		sb.append("Select `IP`, Count(`IP`) as CNT from `");
		sb.append(dbName);
		sb.append("`.`emailcontent` where `FromAddr`=\"");
		sb.append(email);
		sb.append("\" AND `SendDate`>=\"");
		sb.append(fmt.format(start));
		sb.append(" 00:00:00\" AND `SendDate`<=\"");
		sb.append(fmt.format(end));
		sb.append(" 23:59:59\"");
		sb.append(" Group By `IP` Order By CNT DESC");
		String sql=sb.toString();
		
		try{
			Statement st=emailConn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			while(rs.next()){
				String ip=rs.getString("IP");
				if(!Pattern.matches(ipRegex, ip)){
					continue;
				}
				int cnt=rs.getInt("CNT");
				InetAddress inetAddr=InetAddress.getByAddress(ReceiveRouteField.ipString2ByteArray(ip));
				String name=inetAddr.getHostName();
				String addrA=seeker.getIPLocation(ip).toString();
				DbIPLocation loc=locator.locate(ip);
				String addrB=loc.toString();
				lngList.add(loc.longitude);
				latList.add(loc.latitude);
				String[] rowData={ip, name, cnt+"", addrA, addrB};
				model.addRow(rowData);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		setMessage("");
	}
	
	private void showMap(){
		int sel=table.getSelectedRow();
		if(sel>=0){
			NeosStaticMapTool tool=new NeosStaticMapTool();
			tool.addMark(latList.get(sel), lngList.get(sel), "A", "red");
			MapWin mapWin=new MapWin();
			mapWin.setMapUrl(tool.getUrl());
			String ip=model.getValueAt(sel, 0).toString();
			mapWin.setTitle(ip);
			mapWin.setVisible(true);
		}
	}

}
