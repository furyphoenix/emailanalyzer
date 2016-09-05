package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;
import javax.swing.JEditorPane;

import phoenix.visualization.StandardGuiUtil;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmailBoxNoteWin extends NeosStandardFrame {
	private final JTextField textFieldEmail;
	private final JTextField textFieldAuthor;
	private final JTextField textFieldKeyWord;
	private final JTable table;

	
	private final DefaultTableModel model;
	private final static String[] headers={"邮箱","备注人","备注时间","备注内容"};
	private JTextField textFieldEmailAdd;
	private JTextField textFieldAuthorAdd;
	private JEditorPane editorPaneNote;
	
	private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final EmailMainWin parent;
	

	/**
	 * Create the frame.
	 */
	public EmailBoxNoteWin(EmailMainWin parent) {
		setTitle("\u90AE\u7BB1\u5907\u6CE8\u7BA1\u7406");
		setBounds(100, 100, 800, 600);
		
		JPanel panel_1 = new JPanel();
		getCenterPanel().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.NORTH);
		
		JLabel label = new JLabel("\u90AE\u7BB1\u540D");
		panel_2.add(label);
		
		textFieldEmail = new JTextField();
		StandardGuiUtil.addMouseMenu4TextComponent(textFieldEmail);
		textFieldEmail.setPreferredSize(new Dimension(80, 21));
		panel_2.add(textFieldEmail);
		textFieldEmail.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("  ");
		panel_2.add(lblNewLabel);
		
		JLabel label_1 = new JLabel("\u5907\u6CE8\u4EBA");
		panel_2.add(label_1);
		
		textFieldAuthor = new JTextField();
		StandardGuiUtil.addMouseMenu4TextComponent(textFieldAuthor);
		panel_2.add(textFieldAuthor);
		textFieldAuthor.setColumns(10);
		
		JLabel label_2 = new JLabel("  ");
		panel_2.add(label_2);
		
		JLabel label_3 = new JLabel("\u5173\u952E\u8BCD");
		panel_2.add(label_3);
		
		textFieldKeyWord = new JTextField();
		StandardGuiUtil.addMouseMenu4TextComponent(textFieldKeyWord);
		panel_2.add(textFieldKeyWord);
		textFieldKeyWord.setColumns(10);
		
		JLabel label_4 = new JLabel("  ");
		panel_2.add(label_4);
		
		JButton buttonQuery = new JButton("\u67E5\u8BE2");
		buttonQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							queryNote();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_2.add(buttonQuery);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_3.add(scrollPane, BorderLayout.CENTER);
		
		model=new DefaultTableModel(headers,0);
		table = new JTable(model);
		scrollPane.setViewportView(table);
		
		JPanel panel_4 = new JPanel();
		panel_1.add(panel_4, BorderLayout.SOUTH);
		
		JButton buttonCopyEmailList = new JButton("\u590D\u5236\u90AE\u7BB1\u5217\u8868");
		panel_4.add(buttonCopyEmailList);
		
		JButton buttonCopyTable = new JButton("\u590D\u5236\u5B8C\u6574\u8868\u683C");
		panel_4.add(buttonCopyTable);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(200, 10));
		getCenterPanel().add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_5 = new JPanel();
		panel.add(panel_5, BorderLayout.NORTH);
		
		JLabel label_5 = new JLabel("\u6DFB\u52A0\u5907\u6CE8");
		panel_5.add(label_5);
		
		JPanel panel_6 = new JPanel();
		panel.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_8 = new JPanel();
		panel_8.setPreferredSize(new Dimension(10, 80));
		panel_6.add(panel_8, BorderLayout.NORTH);
		panel_8.setLayout(new MigLayout("", "[][][grow]", "[][][]"));
		
		JLabel label_7 = new JLabel("\u90AE\u7BB1\u540D");
		panel_8.add(label_7, "cell 0 1");
		
		textFieldEmailAdd = new JTextField();
		StandardGuiUtil.addMouseMenu4TextComponent(textFieldEmailAdd);
		panel_8.add(textFieldEmailAdd, "cell 2 1,growx");
		textFieldEmailAdd.setColumns(10);
		
		JLabel label_8 = new JLabel("\u5907\u6CE8\u4EBA");
		panel_8.add(label_8, "cell 0 2");
		
		textFieldAuthorAdd = new JTextField();
		StandardGuiUtil.addMouseMenu4TextComponent(textFieldAuthorAdd);
		panel_8.add(textFieldAuthorAdd, "cell 2 2,growx");
		textFieldAuthorAdd.setColumns(10);
		
		JPanel panel_9 = new JPanel();
		panel_6.add(panel_9, BorderLayout.CENTER);
		panel_9.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_10 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_10.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_9.add(panel_10, BorderLayout.NORTH);
		
		JLabel label_6 = new JLabel("\u5907\u6CE8\u5185\u5BB9");
		panel_10.add(label_6);
		
		JPanel panel_11 = new JPanel();
		panel_9.add(panel_11, BorderLayout.CENTER);
		panel_11.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_11.add(scrollPane_1, BorderLayout.CENTER);
		
		editorPaneNote = new JEditorPane();
		StandardGuiUtil.addMouseMenu4TextComponent(editorPaneNote);
		scrollPane_1.setViewportView(editorPaneNote);
		
		JPanel panel_7 = new JPanel();
		panel.add(panel_7, BorderLayout.SOUTH);
		
		JButton buttonAddNote = new JButton("\u6DFB\u52A0");
		buttonAddNote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							addNote();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_7.add(buttonAddNote);
		
		//
		this.parent=parent;
	}
	
	private void addNote(){
		if((textFieldEmailAdd.getText().length()<=0)||(editorPaneNote.getText().length()<=0)){
			return;
		}
		
		String addSql=buildAddSql();
		try{
			Statement st=parent.getEmailDbConnect().createStatement();
			st.executeUpdate(addSql);
			st.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String buildAddSql(){
		StringBuilder sb=new StringBuilder();
		
		sb.append("Insert Into `");
		sb.append(parent.getEmailDbName());
		sb.append("`.`emailboxnote` (`NoteDate`, `Author`, `EmailBox`, `NoteContent`) Values ('");
		sb.append(fmt.format(new Date()));
		sb.append("', '");
		sb.append(textFieldAuthorAdd.getText());
		sb.append("', '");
		sb.append(textFieldEmailAdd.getText());
		sb.append("', '");
		sb.append(editorPaneNote.getText());
		sb.append("')");
		
		return sb.toString();
	}
	
	private void queryNote(){
		model.setRowCount(0);
		String querySql=buildQuerySql();
		
		try{
			Statement st=parent.getEmailDbConnect().createStatement();
			ResultSet rs=st.executeQuery(querySql);
			
			while(rs.next()){
				String row[]=new String[4];
				row[0]=rs.getString("EmailBox");
				row[1]=rs.getString("Author");
				row[2]=fmt.format(rs.getTimestamp("NoteDate"));
				row[3]=rs.getString("NoteContent");
				model.addRow(row);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String buildQuerySql(){
		StringBuilder sb=new StringBuilder();
		
		//"邮箱","备注人","备注时间","备注内容"
		String email=textFieldEmail.getText();
		String author=textFieldAuthor.getText();
		String keyword=textFieldKeyWord.getText();
		
		sb.append("Select `EmailBox`, `Author`, `NoteDate`, `NoteContent` From `");
		sb.append(parent.getEmailDbName());
		sb.append("`.`emailboxnote` ");
		
		sb.append(" Where ");
		
		if(email.length()>0){
			sb.append("`EmailBox` like '%");
			sb.append(email);
			sb.append("%' ");
		}else{
			sb.append(" True ");
		}
		
		sb.append(" AND ");
		
		if(author.length()>0){
			sb.append("`Author`='");
			sb.append(author);
			sb.append("' ");
		}else{
			sb.append("TRUE");
		}
		
		sb.append(" AND ");
		if(keyword.length()>0){
			sb.append("`NoteContent` LIKE '%");
			sb.append(keyword);
			sb.append("%' ");
		}else{
			sb.append("TRUE");
		}
		
		return sb.toString();
	}

}
