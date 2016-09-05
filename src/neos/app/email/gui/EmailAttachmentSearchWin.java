package neos.app.email.gui;

import neos.util.MD5Util;

import net.miginfocom.swing.MigLayout;

import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.jdesktop.swingx.JXDatePicker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.ResultSet;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;


public class EmailAttachmentSearchWin extends NeosStandardFrame {
    private final static SimpleDateFormat fmt = new SimpleDateFormat(
            "yyyy-MM-dd");
    private final static SimpleDateFormat fullFmt = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private final static String[] headers = {
            "ID", "发件时间", "发件地址", "主题", "文件名", "保存路径"
        };
    private final EmailMainWin parent;
    private final JTextField textFieldFilePath;
    private final JTable tableByFile;
    private final JTable tableByAddr;
    private final DefaultTableModel tabFileModel;
    private final DefaultTableModel tabAddrModel;
    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;
    private final JXDatePicker dpStart;
    private final JXDatePicker dpEnd;
    private final DefaultListModel listModel;
    private JList listFile;
    private final Map<String, List<String[]>> listTabMap=new Hashtable<String, List<String[]>> ();
    private final List<String> md5List=new ArrayList<String> ();

    /**
     * Create the frame.
     */
    public EmailAttachmentSearchWin(EmailMainWin parent) {
        setTitle("\u9644\u4EF6\u68C0\u7D22");
        this.parent = parent;
        

        setBounds(100, 100, 800, 600);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getCenterPanel().add(tabbedPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        tabbedPane.addTab("\u6309\u90AE\u7BB1\u67E5\u8BE2", null, panel, null);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_5 = new JPanel();
        panel.add(panel_5, BorderLayout.NORTH);

        JLabel label_1 = new JLabel("\u53D1\u4EF6\u5730\u5740");
        panel_5.add(label_1);

        textFieldFrom = new JTextField();
        panel_5.add(textFieldFrom);
        textFieldFrom.setColumns(10);

        JLabel label_2 = new JLabel("     \u6536\u4EF6\u5730\u5740");
        panel_5.add(label_2);

        textFieldTo = new JTextField();
        panel_5.add(textFieldTo);
        textFieldTo.setColumns(10);

        JLabel label_3 = new JLabel("    \u8D77\u6B62\u65E5\u671F");
        panel_5.add(label_3);

        dpStart = new JXDatePicker(new Date());
        dpStart.setFormats(fmt);
        panel_5.add(dpStart);

        JLabel label_4 = new JLabel(" \u81F3 ");
        panel_5.add(label_4);

        dpEnd = new JXDatePicker(new Date());
        dpEnd.setFormats(fmt);
        panel_5.add(dpEnd);

        JLabel label_5 = new JLabel("    ");
        panel_5.add(label_5);

        JButton btnSearchByAddr = new JButton("\u68C0\u7D22");
        btnSearchByAddr.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					String from=textFieldFrom.getText();
        					String to=textFieldTo.getText();
        					Date start=dpStart.getDate();
        					Date end=dpEnd.getDate();
        					searchByAddr(from, to, start, end);
        				}
        			}.start();
        		}catch(Exception ee){
        			ee.printStackTrace();
        		}
        	}
        });
        panel_5.add(btnSearchByAddr);

        JPanel panel_6 = new JPanel();
        panel.add(panel_6, BorderLayout.CENTER);
        panel_6.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();
        panel_6.add(scrollPane_1, BorderLayout.CENTER);

        tabAddrModel = new DefaultTableModel(headers, 0);
        tableByAddr = new JTable(tabAddrModel);
        scrollPane_1.setViewportView(tableByAddr);

        JPanel panel_7 = new JPanel();
        panel.add(panel_7, BorderLayout.SOUTH);

        JButton btnNetworkAnalyseByAddr = new JButton(
                "\u7F51\u7EDC\u5206\u6790");
        btnNetworkAnalyseByAddr.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					networkAnalyze();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_7.add(btnNetworkAnalyseByAddr);

        JButton btnViewFile = new JButton("\u67E5\u770B\u6587\u4EF6");
        btnViewFile.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					showFile(tableByAddr);
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_7.add(btnViewFile);

        JButton btnViewEmailByAddr = new JButton("\u67E5\u770B\u90AE\u4EF6");
        btnViewEmailByAddr.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					showEmail(tableByAddr);
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_7.add(btnViewEmailByAddr);

        JPanel panel_8 = new JPanel();
        panel_8.setPreferredSize(new Dimension(200, 10));
        panel.add(panel_8, BorderLayout.WEST);
        panel_8.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_2 = new JScrollPane();
        panel_8.add(scrollPane_2, BorderLayout.CENTER);

        listModel = new DefaultListModel();
        listFile = new JList(listModel);
        listFile.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					updateAddrTab();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        listFile.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane_2.setViewportView(listFile);

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("\u6309\u6587\u4EF6\u67E5\u8BE2", null, panel_1, null);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();
        panel_1.add(panel_2, BorderLayout.NORTH);
        panel_2.setLayout(new MigLayout("", "[][][grow][][][]", "[]"));

        JLabel label = new JLabel("\u6253\u5F00\u6587\u4EF6");
        panel_2.add(label, "cell 1 0,alignx trailing");

        textFieldFilePath = new JTextField();
        textFieldFilePath.setEditable(false);
        panel_2.add(textFieldFilePath, "cell 2 0,growx");
        textFieldFilePath.setColumns(10);

        JButton buttonOpenFile = new JButton("");
        buttonOpenFile.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					searchByFile();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        buttonOpenFile.setToolTipText("\u6253\u5F00");
        buttonOpenFile.setIcon(new ImageIcon(
                EmailAttachmentSearchWin.class.getResource(
                    "/com/sun/java/swing/plaf/windows/icons/TreeOpen.gif")));
        panel_2.add(buttonOpenFile, "cell 3 0");

        JPanel panel_3 = new JPanel();
        panel_1.add(panel_3, BorderLayout.CENTER);
        panel_3.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        panel_3.add(scrollPane, BorderLayout.CENTER);

        tabFileModel = new DefaultTableModel(headers, 0);
        tableByFile = new JTable(tabFileModel);
        scrollPane.setViewportView(tableByFile);

        JPanel panel_4 = new JPanel();
        panel_1.add(panel_4, BorderLayout.SOUTH);

        JButton buttonFileNetWorkAnalyze = new JButton(
                "\u7F51\u7EDC\u5206\u6790");
        buttonFileNetWorkAnalyze.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					networkAnalyze(tableByFile);
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_4.add(buttonFileNetWorkAnalyze);

        JButton btnViewByFile = new JButton("\u67E5\u770B\u90AE\u4EF6");
        btnViewByFile.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					showEmail(tableByFile);
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_4.add(btnViewByFile);
    }
    
    private void searchByFile(){
    	JFileChooser            fc     = new JFileChooser();
    	fc.setMultiSelectionEnabled(false);

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File file=fc.getSelectedFile();
        	textFieldFilePath.setText(file.getAbsolutePath());
        	searchByFile(file);
        }
    }

    private void searchByFile(File file) {
        if ((file == null) || (!file.isFile())) {
            setMessage("请指定一个文件！");

            return;
        }

        tabFileModel.setRowCount(0);

        try {
            String md5 = MD5Util.getFileMD5String(file);
            long len = file.length();

            StringBuffer sb = new StringBuffer();
            sb.append(
                "Select `EmailID`, `SendDate`, `FromAddr`, `MailSubject`, `FileName`, `StorePath` From `");
            sb.append(parent.getEmailDbName());
            sb.append("`.`emailheader` inner join (");
            sb.append("Select `EmailID`,`FileName`,`StorePath` From `");
            sb.append(parent.getEmailDbName());
            sb.append("`.`emailattach` where `FileMD5`='");
            sb.append(md5);
            sb.append("' AND `FileLen`=");
            sb.append(len);
            sb.append(
                " ) AS `Tab` ON `emailheader`.`id`=`tab`.`emailid` Order By `SendDate` DESC");

            String sql = sb.toString();

            Statement st = parent.getEmailDbConnect().createStatement();
            ResultSet rs = st.executeQuery(sql);

            //"ID", "发件时间", "发件地址", "主题", "文件名", "保存路径"
            while (rs.next()) {
            	String[] row = new String[6];
                row[0] = rs.getInt("EmailID") + "";
                row[1] = fullFmt.format(rs.getTimestamp("SendDate"));
                row[2] = rs.getString("FromAddr");
                row[3] = rs.getString("MailSubject");
                row[4] = rs.getString("FileName");
                row[5] = rs.getString("StorePath");
                tabFileModel.addRow(row);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void networkAnalyze(JTable tab){
    	final Set<Integer> docIds=new HashSet<Integer> ();
    	int rowCnt=tab.getRowCount();
    	if(rowCnt>0){
    		for(int i=0; i<rowCnt; i++){
    			docIds.add(Integer.parseInt(tab.getValueAt(i, 0).toString()));
    		}
    	}
    	
    	if(SwingUtilities.isEventDispatchThread()){
    		EmailNetworkWin win=new EmailNetworkWin(parent);
        	win.setVisible(true);
        	win.analyzeSearchResult(docIds);
    	}else{
    		SwingUtilities.invokeLater(new Runnable(){
    			public void run(){
    				EmailNetworkWin win=new EmailNetworkWin(parent);
    		    	win.setVisible(true);
    		    	win.analyzeSearchResult(docIds);
    			}
    		});
    	}
    }
    
    private void networkAnalyze(){
    	Set<String> md5s=listTabMap.keySet();
    	if(md5s.size()>0){
    		final Set<Integer> docIds=new HashSet<Integer> ();
    		
    		for(String md5:md5s){
    			List<String[]> rows=listTabMap.get(md5);
    			for(String[] row:rows){
    				docIds.add(Integer.parseInt(row[0]));
    			}
    		}
    		
    		if(SwingUtilities.isEventDispatchThread()){
        		EmailNetworkWin win=new EmailNetworkWin(parent);
            	win.setVisible(true);
            	win.analyzeSearchResult(docIds);
        	}else{
        		SwingUtilities.invokeLater(new Runnable(){
        			public void run(){
        				EmailNetworkWin win=new EmailNetworkWin(parent);
        		    	win.setVisible(true);
        		    	win.analyzeSearchResult(docIds);
        			}
        		});
        	}
    	}
    	
    	
    }

    private void searchByAddr(String from, String to, Date start, Date end) {
        tabAddrModel.setRowCount(0);
        listModel.clear();
        listTabMap.clear();
        md5List.clear();
        
        setProgress(true);
        setMessage("检索中");

        try {
        	StringBuffer sb=new StringBuffer();
        	sb.append("Select Count(Distinct (`emailattach`.`EmailID`)) AS `CNT`, `FileMD5`, `FileName` From `");
        	sb.append(parent.getEmailDbName());
        	sb.append("`.`emailattach` Inner Join (");
        	sb.append("Select Distinct(`EmailID`) From `");
        	sb.append(parent.getEmailDbName());
        	sb.append("`.`emailfromto` Where `SendDate`>=\"");
        	sb.append(fmt.format(start));
        	sb.append(" 00:00:00\" AND `SendDate`<\"");
        	sb.append(fmt.format(end));
        	sb.append(" 23:59:59\" ");
        	if((from!=null)&&(from.length()>0)){
        		sb.append("AND `FromAddr`='");
        		sb.append(from);
        		sb.append("' ");
        	}
        	if((to!=null)&&(to.length()>0)){
        		sb.append("AND `ToAddr`='");
        		sb.append(to);
        		sb.append("' ");
        	}
        	sb.append(") AS `tab` ON `emailattach`.`EmailID`=`tab`.`EmailID` Group By `FileMD5` Having `CNT`>1");
        	
        	String sql = sb.toString();

            Statement st = parent.getEmailDbConnect().createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            final List<String> nameList=new ArrayList<String> ();
            
            
            
            while(rs.next()){
            	String md5=rs.getString("FileMD5");
            	String fileName=rs.getString("FileName");
            	
            	md5List.add(md5);
            	nameList.add(fileName);
            }
            
            if(SwingUtilities.isEventDispatchThread()){
            	for(String name:nameList){
            		listModel.addElement(name);
            	}
            }else{
            	SwingUtilities.invokeLater(new Runnable(){
            		public void run(){
            			for(String name:nameList){
                    		listModel.addElement(name);
                    	}
            		}
            	});
            }
            
            st.close();
        	
            for(String md5:md5List){
            	List<String[]> rowList=new ArrayList<String[]> ();
            	sb = new StringBuffer();
                sb.append(
                    "Select `EmailID`, `SendDate`, `FromAddr`, `MailSubject`, `FileName`, `StorePath` From `");
                sb.append(parent.getEmailDbName());
                sb.append("`.`emailheader` inner join (");
                sb.append("Select `EmailID`,`FileName`,`StorePath` From `");
                sb.append(parent.getEmailDbName());
                sb.append("`.`emailattach` where `FileMD5`='");
                sb.append(md5);
                sb.append("') AS `Tab` ON `emailheader`.`id`=`tab`.`emailid` Order By `SendDate` DESC");

                sql = sb.toString();

                st = parent.getEmailDbConnect().createStatement();
                rs = st.executeQuery(sql);

                //"ID", "发件时间", "发件地址", "主题", "文件名", "保存路径"
                while (rs.next()) {
                    String[] row = new String[6];
                    row[0] = rs.getInt("EmailID") + "";
                    row[1] = fullFmt.format(rs.getTimestamp("SendDate"));
                    row[2] = rs.getString("FromAddr");
                    row[3] = rs.getString("MailSubject");
                    row[4] = rs.getString("FileName");
                    row[5] = rs.getString("StorePath");
                    rowList.add(row);
                }
                listTabMap.put(md5, rowList);
                st.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setMessage("");
        setProgress(0);
    }
    
    private void updateAddrTab(){
    	int idx=listFile.getSelectedIndex();
    	if(idx>=0){
    		String md5=md5List.get(idx);
    		final List<String[]> rowList=listTabMap.get(md5);
    		
    		if(SwingUtilities.isEventDispatchThread()){
    			tabAddrModel.setRowCount(0);
        		for(String[] row:rowList){
        			tabAddrModel.addRow(row);
        		}
            }else{
            	SwingUtilities.invokeLater(new Runnable(){
            		public void run(){
            			tabAddrModel.setRowCount(0);
                		for(String[] row:rowList){
                			tabAddrModel.addRow(row);
                		}
            		}
            	});
            }
    		
    	}
    }
    
    private void showEmail(JTable tab){
    	int row=tab.getSelectedRow();
    	
    	if(row<0){
    		return;
    	}
    	
    	try{
    		final int id=Integer.parseInt(tab.getValueAt(row, 0).toString());
    		openEmail(id);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
    
    private void openEmail(final int emailId){
    	MimeEntityConfig config  = new MimeEntityConfig();

        config.setMaximalBodyDescriptor(true);

        String fileName = ".\\email\\" + parent.getEmailDbName() + "\\" + emailId + ".eml";

        try {
            final Message msg = new Message(new FileInputStream(fileName), config);

            if (SwingUtilities.isEventDispatchThread()) {
                EmailViewWin win = new EmailViewWin(msg, parent, emailId);

                win.setVisible(true);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailViewWin win = new EmailViewWin(msg, parent, emailId);

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
    
    private void showFile(JTable tab){
    	int row=tab.getSelectedRow();
    	
    	if(row<0){
    		return;
    	}
    	
    	try{
    		final String path=tab.getValueAt(row, 5).toString();
    		openFile(path);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private void openFile(String filePath){
    	String   cmd      = "cmd /c call \"" + filePath + "\"";
        String   s;
        Process  process;

        try {
            process = Runtime.getRuntime().exec(cmd);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((s = bufferedReader.readLine()) != null) {
                System.out.println(s);
            }

            process.waitFor();
        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
