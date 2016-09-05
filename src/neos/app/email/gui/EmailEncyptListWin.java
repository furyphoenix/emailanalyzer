package neos.app.email.gui;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;

import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EmailEncyptListWin extends NeosStandardFrame implements MessageContainer {
    private EmailMainWin parent;
    private JTable table;
    
    private final static String[] header={"内部ID","发件人","收件人","日期","主题"};
    private DefaultTableModel model=new DefaultTableModel(header,0);
    private final String dbName;
    
    private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Create the frame.
     */
    public EmailEncyptListWin(EmailMainWin parent) {
        setTitle("包含加密附件的邮件列表");
        setBounds(100, 100, 800, 300);
        this.parent = parent;
        this.dbName=parent.getEmailDbName();
        
        
        getCenterPanel().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();

        getCenterPanel().add(panel);
        panel.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);
        
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);

        JPanel panel_1 = new JPanel();

        getCenterPanel().add(panel_1, BorderLayout.SOUTH);

        JButton btnView = new JButton("\u67E5\u770B");
        btnView.addActionListener(new ActionListener() {
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

        panel_1.add(btnView);
    }
    
    @Override
    public void addMessage(int id, Message msg){
    	String[] data=new String[5];
    	data[0]=id+"";
    	data[1]=msg.getFrom().toString();
    	data[2]=msg.getTo().toString();
    	data[3]=fmt.format(msg.getDate());
    	data[4]=msg.getSubject();
    	model.addRow(data);
    	//table.updateUI();
    }
    
    public void viewMail(){
    	int idx=table.getSelectedRow();
    	if(idx<0){
    		return;
    	}
    	
    	int emailId=Integer.parseInt(model.getValueAt(idx, 0).toString());
    	MimeEntityConfig config=new MimeEntityConfig();
		config.setMaximalBodyDescriptor(true);
		String fileName=".\\email\\"+dbName+"\\"+emailId+".eml";
		try {
			Message msg=new Message(new FileInputStream(fileName), config);
			EmailViewWin win=new EmailViewWin(msg, parent, emailId);
			win.setVisible(true);
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
