package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JButton;

import org.jdesktop.swingx.JXDatePicker;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;

public class EmailFromToWin extends NeosStandardFrame {
	private JTable table;
	private JTextField textFieldEmail;
	private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
	private JXDatePicker dpStart;
	private JXDatePicker dpEnd;
	private JComboBox comboBoxType;
	private final static String[] header={"Email地址", "计数"};
	private final static String[] types={"收件","发件"};
	private DefaultTableModel model=new DefaultTableModel(header,0);
	private final EmailMainWin parent;

	

	/**
	 * Create the frame.
	 */
	public EmailFromToWin(EmailMainWin parent) {
		super();
		this.setBounds(100, 100, 800, 600);
		this.setTitle("邮件往来关系");
		
		JPanel panel = new JPanel();
		getCenterPanel().add(panel, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("\u7535\u5B50\u90AE\u7BB1\u5730\u5740\uFF1A");
		panel.add(lblNewLabel);
		
		textFieldEmail = new JTextField();
		textFieldEmail.setPreferredSize(new Dimension(120, 21));
		textFieldEmail.setMinimumSize(new Dimension(120, 21));
		panel.add(textFieldEmail);
		textFieldEmail.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("    ");
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("\u65E5\u671F\u8303\u56F4\uFF1A");
		panel.add(lblNewLabel_2);
		
		dpStart = new JXDatePicker(new Date());
		dpStart.setFormats(fmt);
		panel.add(dpStart);
		
		JLabel lblNewLabel_3 = new JLabel("\u81F3");
		panel.add(lblNewLabel_3);
		
		dpEnd = new JXDatePicker(new Date());
		dpEnd.setFormats(fmt);
		panel.add(dpEnd);
		
		JLabel lblNewLabel_4 = new JLabel("    ");
		panel.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("\u7C7B\u578B");
		panel.add(lblNewLabel_5);
		
		comboBoxType = new JComboBox(types);
		panel.add(comboBoxType);
		
		JLabel lblNewLabel_6 = new JLabel("        ");
		panel.add(lblNewLabel_6);
		
		JButton btnNewButton = new JButton("\u67E5\u8BE2");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							doSearch();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel.add(btnNewButton);
		
		JPanel panel_1 = new JPanel();
		getCenterPanel().add(panel_1, BorderLayout.SOUTH);
		
		JButton btnNewButton_1 = new JButton("\u6765\u5F80\u89C4\u5F8B\u5206\u6790");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
        			new Thread(){
        				public void run(){
        					showActiveWin();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
			}
		});
		panel_1.add(btnNewButton_1);
		
		JPanel panel_2 = new JPanel();
		getCenterPanel().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		
		//custom code start here
		this.parent=parent;
	}
	
	private void doSearch(){
		String email=textFieldEmail.getText();
		if(email.length()<=0){
			setMessage("错误：请输入邮件地址");
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
		
		Connection emailConn=parent.getEmailDbConnect();
		String dbName=parent.getEmailDbName();
		
		StringBuilder sb=new StringBuilder();
		switch(comboBoxType.getSelectedIndex()){
		case 0:		//收件
			sb.append("SELECT `FromAddr` AS ADDR, Count(`FromAddr`) AS CNT FROM `");
			sb.append(dbName);
			sb.append("`.`emailfromto` Where `ToAddr`=\"");
			sb.append(email);
			sb.append("\" AND `SendDate`>=\"");
			sb.append(fmt.format(start));
			sb.append(" 00:00:00\" AND `SendDate`<=\"");
			sb.append(fmt.format(end));
			sb.append(" 23:59:59\"");
			sb.append(" group by `FromAddr` Order By CNT DESC");
			break;
		case 1:		//发件
			sb.append("SELECT `ToAddr` AS ADDR, Count(`ToAddr`) AS CNT FROM `");
			sb.append(dbName);
			sb.append("`.`emailfromto` Where `FromAddr`=\"");
			sb.append(email);
			sb.append("\" AND `SendDate`>=\"");
			sb.append(fmt.format(start));
			sb.append(" 00:00:00\" AND `SendDate`<=\"");
			sb.append(fmt.format(end));
			sb.append(" 23:59:59\"");
			sb.append(" group by `ToAddr` Order By CNT DESC");
		}
		
		String sql=sb.toString();
		
		try{
			Statement st=emailConn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			while(rs.next()){
				String addr=rs.getString("ADDR");
				int cnt=rs.getInt("CNT");
				String[] row={addr, cnt+""};
				model.addRow(row);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		setMessage("");
		
	}
	
	private void showActiveWin(){
		int row=table.getSelectedRow();
		
		if(row<0){
			return;
		}
		
		EmailActiveRuleWin win=new EmailActiveRuleWin(parent);
		win.setVisible(true);
		
		String from=null;
		String to=null;
		switch(comboBoxType.getSelectedIndex()){
		case 0:	//
			from=table.getValueAt(row, 0).toString();
			to=textFieldEmail.getText();
			break;
		case 1:
			from=textFieldEmail.getText();
			to=table.getValueAt(row, 0).toString();
			break;
		}
		win.computeWithInfo(from, to, dpStart.getDate(), dpEnd.getDate());
		
    }

}
