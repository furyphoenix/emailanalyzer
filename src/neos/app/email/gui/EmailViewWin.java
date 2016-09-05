package neos.app.email.gui;

import neos.app.gui.ProgressMornitor;
import neos.app.util.HtmlParser;
import neos.component.ner.NeosNamedEntity.NamedEntityType;
import neos.tool.fudannlp.NeosFudanTimeTool;
import neos.tool.mime4j.NeosMime4JTool;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.james.mime4j.message.Message;
import org.htmlparser.util.ParserException;
import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;

import edu.fudan.nlp.chinese.ner.TimeUnit;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import javax.swing.JTextField;

public class EmailViewWin extends JFrame implements ProgressMornitor, TreeNodeInfoViewer {
    private JPanel             contentPane;
    private JEditorPane        editorPaneOriginInfo;
    private JEditorPane        editorPaneMailHead;
    private JEditorPane        editorPaneSourceCode;
    //private JEditorPane        editorPaneTextHtml;
    private JEditorPane        editorPaneTextPlain;
    private JEditorPane        editorPaneTreeNode;
    private JLabel             label;
    private final JLabel       labelStatus;
    private JPanel             panel_10;
    private JPanel             panel_11;
    private JPanel             panel_12;
    private JPanel             panel_13;
    private JPanel             panel_14;
    private JPanel             panel_2;
    private JPanel             panel_3;
    private JPanel             panel_5;
    private JPanel             panel_6;
    private JPanel             panel_7;
    private JPanel             panel_8;
    private JPanel             panel_9;
    private final JProgressBar progressBar;
    private JScrollPane        scrollPane;
    private JScrollPane        scrollPane_1;
    //private JScrollPane        scrollPane_2;
    private JScrollPane        scrollPane_3;
    private JScrollPane        scrollPane_5;
    private JScrollPane        scrollPane_6;
    private JScrollPane        scrollPane_7;
    private JTabbedPane        tabbedPane;
    private JTabbedPane        tabbedPane_1;
    private JTree              tree;
    private JPanel panel_15;
    private JButton buttonViewSource;
    //private HtmlPanel htmlPanel;
    private JEditorPane htmlPanel;
    
    private final Message m_msg;
    private JPanel panel_16;
    private JScrollPane scrollPane_2;
    private JEditorPane editorPaneHtml2Text;
    
    private final EmailMainWin parent;
    
    
    private JPanel panel_4;
    private JPanel panel_17;
    private JScrollPane scrollPane_4;
    private JEditorPane editorPaneAttachments;
    private JPanel panel_18;
    private JButton btnExport;
    private NeosMime4JTool mjt;
    private JPanel panel_19;
    private JScrollPane scrollPane_8;
    private JEditorPane editorPaneEntityInfo;
    private JPanel panel_20;
    private JPanel panel_22;
    private JScrollPane scrollPane_9;
    private JEditorPane editorPaneViewNote;
    private JPanel panel_21;
    private JPanel panel_23;
    private JPanel panel_24;
    private JPanel panel_25;
    private JPanel panel_26;
    private JLabel lblNewLabel;
    private JTextField textFieldNoteAuthor;
    private JPanel panel_27;
    private JButton btnAddNote;
    private JScrollPane scrollPane_10;
    private JEditorPane editorPaneNote;
    
    private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final int emailId;
    private JPanel panel_28;
    private JButton btnViewElemInfo;
    private JPanel panel_29;
    private JButton btnViewMap;
    
    private String plainMailText;
    private String htmlMailHtml;
    private String htmlMailText;
    private JScrollPane scrollPane_11;

    /**
     * Create the frame.
     */
    public EmailViewWin(Message msg, final EmailMainWin parent, int id) {
    	setIconImage(Toolkit.getDefaultToolkit().getImage(EmailViewWin.class.getResource("/icon/1307955269_applications-systemg-icon.png")));
        setTitle("\u90AE\u4EF6\u67E5\u770B");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        this.parent=parent;

        JPanel panel = new JPanel();

        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        panel.add(tabbedPane, BorderLayout.CENTER);
        //
        panel_2 = new JPanel();
        tabbedPane.addTab("\u57FA\u672C\u4FE1\u606F", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        //
        panel_6 = new JPanel();
        panel_6.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel_6.setPreferredSize(new Dimension(10, 200));
        panel_2.add(panel_6, BorderLayout.NORTH);
        panel_6.setLayout(new BorderLayout(0, 0));
        //
        panel_7 = new JPanel();
        panel_6.add(panel_7, BorderLayout.NORTH);
        label = new JLabel("\u90AE\u4EF6\u5934\u4FE1\u606F");
        panel_7.add(label);
        //
        panel_8 = new JPanel();
        panel_6.add(panel_8, BorderLayout.CENTER);
        panel_8.setLayout(new BorderLayout(0, 0));
        scrollPane = new JScrollPane();
        panel_8.add(scrollPane, BorderLayout.CENTER);
        //
        editorPaneMailHead = new JEditorPane();
        editorPaneMailHead.setContentType("text/html");
        editorPaneMailHead.setEditable(false);
        scrollPane.setViewportView(editorPaneMailHead);
        //
        panel_9 = new JPanel();
        panel_9.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel_2.add(panel_9, BorderLayout.CENTER);
        panel_9.setLayout(new BorderLayout(0, 0));
        //
        tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
        panel_9.add(tabbedPane_1, BorderLayout.CENTER);
        panel_10 = new JPanel();
        tabbedPane_1.addTab("纯文本邮件体", null, panel_10, null);
        panel_10.setLayout(new BorderLayout(0, 0));
        scrollPane_1 = new JScrollPane();
        panel_10.add(scrollPane_1, BorderLayout.CENTER);
        editorPaneTextPlain = new JEditorPane();
        editorPaneTextPlain.setContentType("text/html");
        editorPaneTextPlain.setEditable(false);
        scrollPane_1.setViewportView(editorPaneTextPlain);
        panel_11 = new JPanel();
        tabbedPane_1.addTab("HTML邮件体", null, panel_11, null);
        panel_11.setLayout(new BorderLayout(0, 0));
        
        scrollPane_11 = new JScrollPane();
        panel_11.add(scrollPane_11, BorderLayout.CENTER);
        //scrollPane_2 = new JScrollPane();
        //panel_11.add(scrollPane_2, BorderLayout.CENTER);
        //editorPaneTextHtml = new JEditorPane();
        //editorPaneTextHtml.setContentType("text/html");
        //editorPaneTextHtml.setEditable(false);
        //scrollPane_2.setViewportView(editorPaneTextHtml);
        //htmlPanel=new HtmlPanel();
        htmlPanel=new JEditorPane();
        htmlPanel.setContentType("text/html");
        htmlPanel.setEditable(false);
        scrollPane_11.setViewportView(htmlPanel);
        
        
        panel_16 = new JPanel();
        tabbedPane_1.addTab("HTML\u90AE\u4EF6\u6587\u672C", null, panel_16, null);
        panel_16.setLayout(new BorderLayout(0, 0));
        
        scrollPane_2 = new JScrollPane();
        panel_16.add(scrollPane_2, BorderLayout.CENTER);
        
        editorPaneHtml2Text = new JEditorPane();
        editorPaneHtml2Text.setContentType("text/html");
        editorPaneHtml2Text.setEditable(false);
        scrollPane_2.setViewportView(editorPaneHtml2Text);
        panel_3 = new JPanel();
        panel_3.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("\u6765\u6E90\u8FFD\u8E2A", null, panel_3, null);
        panel_3.setLayout(new BorderLayout(0, 0));
        scrollPane_3 = new JScrollPane();
        panel_3.add(scrollPane_3, BorderLayout.CENTER);
        editorPaneOriginInfo = new JEditorPane();
        editorPaneOriginInfo.setContentType("text/html");
        editorPaneOriginInfo.setEditable(false);
        scrollPane_3.setViewportView(editorPaneOriginInfo);
        
        panel_29 = new JPanel();
        panel_3.add(panel_29, BorderLayout.SOUTH);
        
        btnViewMap = new JButton("\u67E5\u770B\u6765\u6E90\u5730\u56FE\u4F4D\u7F6E");
        btnViewMap.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					MessageViewHelper.showMailOrigMapWin(m_msg, parent.getKnowledgeDbConnect());
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_29.add(btnViewMap);
        
        panel_19 = new JPanel();
        panel_19.setBorder(new EmptyBorder(10,10,10,10));
        tabbedPane.addTab("\u8981\u7D20\u4FE1\u606F", null, panel_19, null);
        panel_19.setLayout(new BorderLayout(0, 0));
        
        scrollPane_8 = new JScrollPane();
        panel_19.add(scrollPane_8, BorderLayout.CENTER);
        
        editorPaneEntityInfo = new JEditorPane();
        editorPaneEntityInfo.setContentType("text/html");
        editorPaneEntityInfo.setEditable(false);
        scrollPane_8.setViewportView(editorPaneEntityInfo);
        
        panel_28 = new JPanel();
        panel_19.add(panel_28, BorderLayout.SOUTH);
        
        btnViewElemInfo = new JButton("\u663E\u793A");
        btnViewElemInfo.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					displayMailEntityInfo();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_28.add(btnViewElemInfo);
        panel_5 = new JPanel();
        panel_5.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("\u90AE\u4EF6\u6E90\u7801", null, panel_5, null);
        panel_5.setLayout(new BorderLayout(0, 0));
        scrollPane_5 = new JScrollPane();
        panel_5.add(scrollPane_5, BorderLayout.CENTER);
        editorPaneSourceCode = new JEditorPane();
        editorPaneSourceCode.setEditable(false);
        scrollPane_5.setViewportView(editorPaneSourceCode);
        
        panel_15 = new JPanel();
		panel_5.add(panel_15, BorderLayout.SOUTH);
		
		buttonViewSource = new JButton("\u67E5\u770B\u6E90\u7801");
		buttonViewSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							viewSourceCode();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});
		panel_15.add(buttonViewSource);
		
        panel_12 = new JPanel();
        panel_12.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("\u7ED3\u6784\u89E3\u6790", null, panel_12, null);
        panel_12.setLayout(new BorderLayout(0, 0));
        panel_13 = new JPanel();
        panel_13.setPreferredSize(new Dimension(250, 10));
        panel_12.add(panel_13, BorderLayout.WEST);
        panel_13.setLayout(new BorderLayout(0, 0));
        scrollPane_6 = new JScrollPane();
        panel_13.add(scrollPane_6, BorderLayout.CENTER);
        tree = MessageViewHelper.createJTree(msg, this);
        scrollPane_6.setViewportView(tree);
        panel_14 = new JPanel();
        panel_14.setBorder(new EmptyBorder(0, 10, 0, 0));
        panel_12.add(panel_14, BorderLayout.CENTER);
        panel_14.setLayout(new BorderLayout(0, 0));
        scrollPane_7 = new JScrollPane();
        panel_14.add(scrollPane_7, BorderLayout.CENTER);
        editorPaneTreeNode = new JEditorPane();
        editorPaneTreeNode.setEditable(false);
        scrollPane_7.setViewportView(editorPaneTreeNode);
        
        panel_4 = new JPanel();
        tabbedPane.addTab("\u9644\u4EF6\u4FE1\u606F", null, panel_4, null);
        panel_4.setLayout(new BorderLayout(0, 0));
        
        panel_17 = new JPanel();
        panel_4.add(panel_17, BorderLayout.CENTER);
        panel_17.setLayout(new BorderLayout(0, 0));
        
        scrollPane_4 = new JScrollPane();
        panel_17.add(scrollPane_4);
        
        editorPaneAttachments = new JEditorPane();
        editorPaneAttachments.setEditable(false);
        scrollPane_4.setViewportView(editorPaneAttachments);
        
        panel_18 = new JPanel();
        panel_17.add(panel_18, BorderLayout.SOUTH);
        panel_18.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        btnExport = new JButton("\u5BFC\u51FA");
        btnExport.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					exportAttachments();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        		
        	}
        });
        panel_18.add(btnExport);
        
        panel_20 = new JPanel();
        tabbedPane.addTab("\u5907\u6CE8\u4FE1\u606F", null, panel_20, null);
        panel_20.setLayout(new BorderLayout(0, 0));
        
        panel_22 = new JPanel();
        panel_20.add(panel_22, BorderLayout.CENTER);
        panel_22.setLayout(new BorderLayout(0, 0));
        
        scrollPane_9 = new JScrollPane();
        panel_22.add(scrollPane_9, BorderLayout.CENTER);
        
        editorPaneViewNote = new JEditorPane();
        editorPaneViewNote.setContentType("text/html");
        editorPaneViewNote.setEditable(false);
        scrollPane_9.setViewportView(editorPaneViewNote);
        
        panel_21 = new JPanel();
        panel_21.setPreferredSize(new Dimension(10, 150));
        panel_20.add(panel_21, BorderLayout.SOUTH);
        panel_21.setLayout(new BorderLayout(0, 0));
        
        panel_23 = new JPanel();
        panel_21.add(panel_23, BorderLayout.NORTH);
        
        panel_24 = new JPanel();
        panel_21.add(panel_24, BorderLayout.CENTER);
        panel_24.setLayout(new BorderLayout(0, 0));
        
        scrollPane_10 = new JScrollPane();
        panel_24.add(scrollPane_10, BorderLayout.CENTER);
        
        editorPaneNote = new JEditorPane();
        scrollPane_10.setViewportView(editorPaneNote);
        
        panel_25 = new JPanel();
        panel_21.add(panel_25, BorderLayout.SOUTH);
        panel_25.setLayout(new BorderLayout(0, 0));
        
        panel_26 = new JPanel();
        panel_25.add(panel_26, BorderLayout.WEST);
        
        lblNewLabel = new JLabel("    \u5907\u6CE8\u4EBA\uFF1A");
        panel_26.add(lblNewLabel);
        
        textFieldNoteAuthor = new JTextField();
        panel_26.add(textFieldNoteAuthor);
        textFieldNoteAuthor.setColumns(10);
        
        panel_27 = new JPanel();
        panel_25.add(panel_27, BorderLayout.CENTER);
        
        btnAddNote = new JButton("\u6DFB\u52A0");
        btnAddNote.addActionListener(new ActionListener() {
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
        panel_27.add(btnAddNote);

        JPanel panel_1 = new JPanel();

        contentPane.add(panel_1, BorderLayout.SOUTH);
        panel_1.setLayout(new BorderLayout(0, 0));
        labelStatus = new JLabel("    ");
        panel_1.add(labelStatus, BorderLayout.CENTER);
        progressBar = new JProgressBar();
        panel_1.add(progressBar, BorderLayout.EAST);
        
        
        
        //Custom Code Start Here
        Date date1=new Date();
        emailId=id;
        m_msg=msg;
        
        init();
        
        
    }
    
    private void init(){
    	displayMailHeader(MessageViewHelper.getHeaderDescription(m_msg));
        mjt=new NeosMime4JTool(m_msg);
        try{
        	new Thread(){
        		public void run(){
        			displayMailOriginInfo(MessageViewHelper.getMailOrigin(m_msg, parent.getKnowledgeDbConnect()));
        		}
        	}.start();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
//        displayMailEntityInfo(text);
        plainMailText=mjt.getTextPlainContent();
        displayMailPlainText(plainMailText);
        
        htmlMailHtml=mjt.getTextHtmlContent();
        displayMailHtml(htmlMailHtml);
        htmlMailText=mjt.getTextHtmlText();
        displayMailHtmlText(htmlMailText);
        
        StringBuilder sb=new StringBuilder();
        for(String fileName:mjt.getAttachmentFileNames()){
        	sb.append(fileName);
        	sb.append("\r\n");
        }
        displayMailAttachmentsInfo(sb.toString());
        viewNotes();
    }
    
    private void displayMailHeader(String text){
    	editorPaneMailHead.setText(text);
    }
    
    private String annoteText(String src, HashMap<String, NamedEntityType> emap){
    	for(String vip:parent.getUserDictionary().getVipWordList()){
    		src=MessageViewHelper.annote(src, vip, Color.red);
    	}
    	
    	//HashMap<String, NamedEntityType> emap=parent.getNerTool().locate(src);
    	
    	for(String exp:emap.keySet()){
    		switch(emap.get(exp)){
    		case DateTime:
    			src=MessageViewHelper.annote(src, exp, Color.yellow);
    			break;
    		case LocationName:
    			src=MessageViewHelper.annote(src, exp, Color.blue);
    			break;
    		case PersonName:
    			src=MessageViewHelper.annote(src, exp, Color.green);
    			break;
    		case EmailAddress:
    			src=MessageViewHelper.annote(src, exp, Color.cyan);
    			break;
    		case OrgnizationName:
    			src=MessageViewHelper.annote(src, exp, Color.blue);
    			break;
    		case URL:
    			src=MessageViewHelper.annote(src, exp, Color.cyan);
    			break;
    		case PhoneNumber:
    			src=MessageViewHelper.annote(src, exp, Color.pink);
    			break;
    		case MobilePhoneNumber:
    			src=MessageViewHelper.annote(src, exp, Color.pink);
    			break;
    		case IDCardNumber:
    			src=MessageViewHelper.annote(src, exp, Color.pink);
    			break;
    		case PostalCode:
    			src=MessageViewHelper.annote(src, exp, Color.pink);
    			break;
    		case GeneralNumber:
    			src=MessageViewHelper.annote(src, exp, Color.pink);
    			break;
    		}
    	}
    	
    	return src;
    }
    
    private void displayMailPlainText(String text){
    	String src=text.replace("\r\n", "<br>");
	
    	editorPaneTextPlain.setText(src);
    }
    
    private void displayMailHtml(String text){
    	//editorPaneTextHtml.setText(text);
    	/*String src=text;
    	try{
    		UserAgentContext ucontext=new SimpleUserAgentContext();
    		SimpleHtmlRendererContext rcontext=new SimpleHtmlRendererContext(htmlPanel, ucontext);
    		htmlPanel.setHtml(src, "file:///", rcontext);
		}catch(Exception e){
			//e.printStackTrace();
		}*/
    	
    	int idxA=text.toUpperCase().indexOf("<BODY");
    	int idx1=text.indexOf(">", idxA);
    	int idx2=text.toUpperCase().lastIndexOf("</BODY>");
    	if((idx1>0)&&(idx2>idx1)){
    		htmlPanel.setText(text.substring(idx1+1, idx2));
    	}
    	
    	
    }
    
    private void displayMailHtmlText(String text){
    	String src=text.replace("\r\n", "<br>");
    	
    	editorPaneHtml2Text.setText(src);
//    	try {
//			HtmlParser parser=new HtmlParser(html);
//			String text=parser.getPageText();
//			String src=annoteText(text);
//			editorPaneHtml2Text.setText(src.replace("\r\n", "<br>"));
//		} catch (ParserException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    
    private void displayMailOriginInfo(String text){
    	editorPaneOriginInfo.setText(text);
    }
    
    private void displayMailEntityInfo(){
    	//StringBuilder sb=new StringBuilder();
    	
    	//NeosFudanTimeTool timeTool=parent.getTimeTool();
    	
//    	try{
//        	sb.append("<p><b>文本邮件中包含的日期时间：</b><br>");
//        	TimeUnit[] unitsPlain=timeTool.parse(plainMailText, m_msg.getDate());
//        	for(int i=0; i<unitsPlain.length; i++){
//        		sb.append(unitsPlain[i].toString());
//        		sb.append("<br>");
//        	}
//        	sb.append("</p>");
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
//    	
//    	
//    	try{
//    		sb.append("<p><b>HTML邮件中包含的日期时间：</b><br>");
//        	TimeUnit[] unitsHtml=timeTool.parse(htmlMailText, m_msg.getDate());
//        	for(int i=0; i<unitsHtml.length; i++){
//        		sb.append(unitsHtml[i].toString());
//        		sb.append("<br>");
//        	}
//        	sb.append("</p>");
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
    	
    	
    	//editorPaneEntityInfo.setText(sb.toString());
    	
    	HashMap<String, NamedEntityType> pemap=parent.getNerTool().locate(plainMailText);
    	editorPaneTextPlain.setText(annoteText(plainMailText, pemap));
    	HashMap<String, NamedEntityType> hemap=parent.getNerTool().locate(htmlMailText);
    	editorPaneHtml2Text.setText(annoteText(htmlMailText, hemap));
    	StringBuilder sb=new StringBuilder();
    	sb.append("<center><h2>要素信息分析结果：</h2></center>");
    	sb.append("<center><h3>文本邮件部分：</h3></center>");
    	sb.append(entityMap2Html(pemap));
    	sb.append("<center><h3>HTML邮件部分：</h3></center>");
    	sb.append(entityMap2Html(hemap));
    	editorPaneEntityInfo.setText(sb.toString());
    	
    }
    
    private String entityMap2Html(HashMap<String, NamedEntityType> map){
    	StringBuilder sb=new StringBuilder();
    	
    	List[] entityList=new ArrayList[NamedEntityType.values().length];
    	for(int i=0; i<entityList.length; i++){
    		entityList[i]=new ArrayList<String> ();
    	}
    	for(String entity:map.keySet()){
    		NamedEntityType type=map.get(entity);
    		entityList[type.ordinal()].add(entity);
    	}
    	
    	for(int i=0; i<entityList.length; i++){
    		NamedEntityType type=NamedEntityType.values()[i];
    		switch(type){
    		case DateTime:
    			sb.append("<p><b>日期时间</b><br>");
    			break;
    		case LocationName:
    			sb.append("<p><b>地名</b><br>");
    			break;
    		case PersonName:
    			sb.append("<p><b>人名</b><br>");
    			break;
    		case OrgnizationName:
    			sb.append("<p><b>组织机构</b><br>");
    			break;
    		case EmailAddress:
    			sb.append("<p><b>邮件地址</b><br>");
    			break;
    		case URL:
    			sb.append("<p><b>网址链接</b><br>");
    			break;
    		case PhoneNumber:
    			sb.append("<p><b>电话号码</b><br>");
    			break;
    		case MobilePhoneNumber:
    			sb.append("<p><b>手机号码</b><br>");
    			break;
    		case IDCardNumber:
    			sb.append("<p><b>身份证号</b><br>");
    			break;
    		case PostalCode:
    			sb.append("<p><b>邮政编码</b><br>");
    			break;
    		case GeneralNumber:
    			sb.append("<p><b>其它</b><br>");
    			break;
    		
    		}
    		
    		for(Object entity:entityList[i]){
				sb.append(entity.toString()+"<br>");
			}
			sb.append("</p>");
    	}
    	
    	return sb.toString();
    }
    
    private void displayMailAttachmentsInfo(String text){
    	editorPaneAttachments.setText(text);
    }
    
    private void viewSourceCode(){
    	ByteArrayOutputStream baos=new ByteArrayOutputStream();
        try {
			m_msg.writeTo(baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
        	final String sourceCode=baos.toString(m_msg.getCharset());
        	if(SwingUtilities.isEventDispatchThread()){
        		editorPaneSourceCode.setText(sourceCode);
        	}else{
        		SwingUtilities.invokeLater(new Runnable(){
        			public void run(){
        				editorPaneSourceCode.setText(sourceCode);
        			}
        		});
        	}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }

    
    private void exportAttachments(){
    	JFileChooser fc=new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		int returnVal=fc.showOpenDialog(null);
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file=fc.getSelectedFile();
			try {
				mjt.saveAttachmentsToPath(file.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    private void addNote(){
    	if(editorPaneNote.getText().length()<=0){
    		return;
    	}
    	
    	String author;
    	if(textFieldNoteAuthor.getText().length()>0){
    		author=sqlEncode(textFieldNoteAuthor.getText());
    	}else{
    		author=" ";
    	}
    	String note=sqlEncode(editorPaneNote.getText());
    	String date=fmt.format(new Date());
    	
    	StringBuilder sb=new StringBuilder();
    	sb.append("Insert INTO `");
    	sb.append(parent.getEmailDbName());
    	sb.append("`.`emailnote` ");
    	sb.append(" (`NoteDate`, `Author`, `EmailID`, `NoteContent`) Values ('");
    	sb.append(date);
    	sb.append("','");
    	sb.append(author);
    	sb.append("',");
    	sb.append(emailId);
    	sb.append(",'");
    	sb.append(note);
    	sb.append("')");
    	
    	
    	String sql=sb.toString();
    	try{
    		Statement st  = parent.getEmailDbConnect().createStatement();

            st.executeUpdate(sql);
            st.close();
    	}catch(Exception e){
    		this.setMessage("添加备注失败");
    	}
    	
    	viewNotes();
    }
    
    private void viewNotes(){
    	StringBuilder sb=new StringBuilder();
    	
    	sb.append("Select `NoteDate`, `Author`, `EmailID`, `NoteContent` From `");
    	sb.append(parent.getEmailDbName());
    	sb.append("`.`emailnote` ");
    	sb.append("Where `EmailID`=");
    	sb.append(this.emailId);
    	sb.append(" Order By `NoteDate` DESC");
    	
    	String sql=sb.toString();
    	StringBuilder sbHtml=new StringBuilder();
    	try{
    		Statement st  = parent.getEmailDbConnect().createStatement();
            ResultSet rs=st.executeQuery(sql);
            
            while(rs.next()){
            	Date date=rs.getTimestamp("NoteDate");
            	String author=rs.getString("Author");
            	String note=rs.getString("NoteContent").replaceAll("\r\n", "<br>");
            	sbHtml.append("<p>");
            	sbHtml.append("<b>添加日期：</b>");
            	sbHtml.append(fmt.format(date));
            	sbHtml.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            	sbHtml.append("<b>备注人：</b>");
            	sbHtml.append(author);
            	sbHtml.append("<br>");
            	sbHtml.append(note);
            	sbHtml.append("</p>");
            	
            }

            st.close();
    	}catch(Exception e){
    		e.printStackTrace();
    		this.setMessage("查询备注失败");
    	}
    	editorPaneViewNote.setText(sbHtml.toString());
    	
    }
    
    private static String sqlEncode(String text) {
        return text.replace("\\", "\\\\").replace("'", "\\'");
    }
    
    @Override
    public void setTreeNodeInfo(String str){
    	editorPaneTreeNode.setText(str);
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
