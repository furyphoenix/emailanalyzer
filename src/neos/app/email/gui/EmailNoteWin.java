package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EmailNoteWin extends NeosStandardFrame {
	private final JTextField textFieldKeyWord;
	private final JTextField textFieldNoteAuthor;
	private final DefaultTableModel model;
	private final JTable table;
	private final EmailMainWin parent;

	private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static String[] headers={"邮件ID","邮件日期","邮件标题","发件人","备注日期","备注人","备注内容"};
	

	/**
	 * Create the frame.
	 */
	public EmailNoteWin(EmailMainWin parent) {
		setTitle("\u90AE\u4EF6\u5907\u6CE8\u67E5\u8BE2");
		
		setBounds(100, 100, 800, 600);
		
		JPanel panel = new JPanel();
		getCenterPanel().add(panel, BorderLayout.NORTH);
		
		JLabel label = new JLabel("\u5173\u952E\u8BCD");
		panel.add(label);
		
		textFieldKeyWord = new JTextField();
		panel.add(textFieldKeyWord);
		textFieldKeyWord.setColumns(10);
		
		JLabel label_1 = new JLabel("          ");
		panel.add(label_1);
		
		JLabel label_2 = new JLabel("\u5907\u6CE8\u4EBA");
		panel.add(label_2);
		
		textFieldNoteAuthor = new JTextField();
		panel.add(textFieldNoteAuthor);
		textFieldNoteAuthor.setColumns(10);
		
		JLabel label_3 = new JLabel("          ");
		panel.add(label_3);
		
		JButton buttonQuery = new JButton("\u67E5\u8BE2");
		buttonQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							doQuery();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel.add(buttonQuery);
		
		JPanel panel_1 = new JPanel();
		getCenterPanel().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		model=new DefaultTableModel(headers, 0);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		
		JPanel panel_2 = new JPanel();
		getCenterPanel().add(panel_2, BorderLayout.SOUTH);
		
		JButton buttonView = new JButton("\u67E5\u770B");
		buttonView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							viewMail();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_2.add(buttonView);
		
		//
		this.parent=parent;
	}
	
	private String buildSql(){
		String keyword=textFieldKeyWord.getText();
		String author=textFieldNoteAuthor.getText();
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("SELECT `EmailID`, `SendDate`, `MailSubject`, `FromAddr`, `NoteDate`, `Author`, `NoteContent` From `");
		sb.append(parent.getEmailDbName());
		sb.append("`.`emailnote` inner join `");
		sb.append(parent.getEmailDbName());
		sb.append("`.`emailheader` ON emailnote.id=emailheader.id Where ");
		if(author.length()>0){
			sb.append("`Author`=\"");
			sb.append(author);
			sb.append("\" AND ");
		}
		sb.append("`NoteContent` like \"%");
		sb.append(keyword);
		sb.append("%\" Order By `SendDate`");
		
		return sb.toString();
	}
	
	private void doQuery(){
		String sql=buildSql();
		model.setRowCount(0);
		
		try{
			Statement st=parent.getEmailDbConnect().createStatement();
			ResultSet rs=st.executeQuery(sql);
			
			while(rs.next()){
				String[] row=new String[7];
				row[0]=rs.getInt("EmailID")+"";
				row[1]=fmt.format(rs.getTimestamp("SendDate"));
				row[2]=rs.getString("MailSubject");
				row[3]=rs.getString("FromAddr");
				row[4]=fmt.format(rs.getTimestamp("NoteDate"));
				row[5]=rs.getString("Author");
				row[6]=rs.getString("NoteContent");
				model.addRow(row);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void viewMail(){
		int idx=table.getSelectedRow();
		if(idx<0){
			return;
		}
		final int emailId=Integer.parseInt(table.getValueAt(idx, 0).toString());
		MimeEntityConfig config=new MimeEntityConfig();
		config.setMaximalBodyDescriptor(true);
		String fileName=".\\email\\"+parent.getEmailDbName()+"\\"+emailId+".eml";
		try {
			final Message msg=new Message(new FileInputStream(fileName), config);
			if(SwingUtilities.isEventDispatchThread()){
				EmailViewWin win=new EmailViewWin(msg, parent, emailId);
				win.setVisible(true);
			}else{
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						EmailViewWin win=new EmailViewWin(msg, parent, emailId);
						win.setVisible(true);
					}
				});
			}
			
		} catch (MimeIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
