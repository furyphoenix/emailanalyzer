package neos.app.email.gui;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.ListSelectionModel;

public class EmailUserDictWin extends NeosStandardFrame {
    private final static String[] TYPES = { "关注词表", "停用词表", "用户词表" };
    private JComboBox             comboBoxType;
    private JList                 list;
    private DefaultListModel      model;
    private final EmailMainWin    parent;
    private JTextField            textField;

    /**
     * Create the frame.
     */
    public EmailUserDictWin(EmailMainWin parent) {
        super();
        setResizable(false);
        this.parent = parent;
        setBounds(100, 100, 250, 450);
        setTitle("用户词典");

        JPanel panel = new JPanel();

        getCenterPanel().add(panel, BorderLayout.NORTH);

        JLabel lblNewLabel = new JLabel("\u8BCD\u5178\u7C7B\u578B");

        panel.add(lblNewLabel);
        comboBoxType = new JComboBox(TYPES);
        comboBoxType.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					showDict();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel.add(comboBoxType);

        JPanel panel_1 = new JPanel();

        getCenterPanel().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_3 = new JPanel();

        panel_1.add(panel_3, BorderLayout.NORTH);
        textField = new JTextField();
        textField.setPreferredSize(new Dimension(100, 21));
        panel_3.add(textField);
        textField.setColumns(10);

        JButton btnAdd = new JButton("\u6DFB\u52A0");
        btnAdd.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					addWord();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });

        panel_3.add(btnAdd);

        JPanel panel_4 = new JPanel();

        panel_1.add(panel_4, BorderLayout.CENTER);
        panel_4.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();

        panel_4.add(scrollPane, BorderLayout.CENTER);
        model = new DefaultListModel();
        list  = new JList(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(list);

        JPanel panel_2 = new JPanel();

        getCenterPanel().add(panel_2, BorderLayout.SOUTH);

        JButton btnDelete = new JButton("\u5220\u9664");
        btnDelete.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					deleteWord();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });

        panel_2.add(btnDelete);
        
        //custom code start here
        showDict();
    }

    private void showDict() {
    	model.removeAllElements();
    	List<String> words=null;
    	switch(comboBoxType.getSelectedIndex()){
    	case 0:		//关注词
    		words=parent.getUserDictionary().getVipWordList();
    		break;
    	case 1:		//停用词
    		words=parent.getUserDictionary().getStopWordList();
    		break;
    	case 2:		//用户词
    		words=parent.getUserDictionary().getUserWordList();
    		break;
    	}
    	if(words!=null){
    		for(String word:words){
    			model.addElement(word);
    		}
    		if(SwingUtilities.isEventDispatchThread()){
    			list.updateUI();
    		}else{
    			SwingUtilities.invokeLater(new Runnable(){
    				public void run(){
    					list.updateUI();
    				}
    			});
    		}
    		
    	}
    }
    
    private void addWord(){
    	String word=textField.getText();
    	if(word.length()<=0){
    		return;
    	}
    	
    	switch(comboBoxType.getSelectedIndex()){
    	case 0:
    		parent.getUserDictionary().addVipWord(word);
    		break;
    	case 1:
    		parent.getUserDictionary().addStopWord(word);
    		break;
    	case 2:
    		parent.getUserDictionary().addUserWord(word);
    		break;
    	}
    	
    	showDict();
    }
    
    private void deleteWord(){
    	int idx=list.getSelectedIndex();
    	if(idx<0){
    		return;
    	}
    	
    	String word=list.getSelectedValue().toString();
    	
    	switch(comboBoxType.getSelectedIndex()){
    	case 0:
    		parent.getUserDictionary().removeVipWord(word);
    		break;
    	case 1:
    		parent.getUserDictionary().removeStopWord(word);
    		break;
    	case 2:
    		parent.getUserDictionary().removeUserWord(word);
    		break;
    	}
    	
    	showDict();
    }
}
