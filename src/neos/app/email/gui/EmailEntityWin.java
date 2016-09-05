package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import neos.algorithm.charm.Charm;

import neos.app.gui.SimpleNeosEdge;
import neos.app.gui.SimpleNeosVertex;

import neos.component.ner.NeosNamedEntity;
import neos.component.ner.NeosNamedEntity.NamedEntityType;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;

import org.jdesktop.swingx.JXDatePicker;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class EmailEntityWin extends NeosStandardFrame {
    private final static SimpleDateFormat                      fmt            = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat                      fullFmt        =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String[]                              tabHeader      = { "计数值", "信息要素集" };
    private final Map<Integer, String>                         idTitleMap     = new Hashtable<Integer, String>();
    private final Map<Integer, Set<String>>                    idEntitySetMap = new Hashtable<Integer, Set<String>>();
    private final Map<String, NeosNamedEntity.NamedEntityType> entityTypeMap  = new Hashtable<String,
                                                                                    NeosNamedEntity.NamedEntityType>();
    //private final Map<String, Set<Integer>> entityIdSetMap=new Hashtable<String, Set<Integer>> ();
    private final Set<String> topEntitySet=new HashSet<String> ();
    private final DefaultListModel                  emailListModel = new DefaultListModel();
    private boolean                                 isNewAnalyze   = false;
    private final JCheckBox                         chckbxKeep;
    private final JCheckBox                         checkBoxEmail;
    private final JCheckBox                         checkBoxLocation;
    private final JCheckBox                         checkBoxNumber;
    private final JCheckBox                         checkBoxOrgnization;
    private final JCheckBox                         checkBoxPerson;
    private final JCheckBox                         checkBoxPhone;
    private final JCheckBox                         checkBoxQulity;
    private final JCheckBox                         checkBoxTime;
    private final JCheckBox                         checkBoxUri;
    private final JXDatePicker                      dpEnd;
    private final JXDatePicker                      dpStart;
    private final EntityCountPanel                  emailCountPanel;
    private final DefaultTableModel                 entitySetModel;
    private Transformer<SimpleNeosEdge, Double>     et;
    private final SpinnerNumberModel                fsm;
    private final JList                             listEmail;
    private final EntityCountPanel                  locationCountPanel;
    private final EntityCountPanel                  numberCountPanel;
    private final EntityCountPanel                  orgnizationCountPanel;
    private Graph<SimpleNeosVertex, SimpleNeosEdge> origGraph;
    private final NetworkOperatePanel               panelGraph;
    private final EmailMainWin                      parent;
    private final EntityCountPanel                  personCountPanel;
    private final EntityCountPanel                  phoneCountPanel;
    private final EntityCountPanel                  qulityCountPanel;
    private final SpinnerNumberModel                sm;
    private final JSpinner                          spinnerMinFreq;
    private final JTable                            tableEntitySet;
    private final JTextField                        textFieldFrom;
    private final JTextField                        textFieldSearchList;
    private final JTextField                        textFieldTo;
    private final EntityCountPanel                  timeCountPanel;
    private final EntityCountPanel                  uriCountPanel;
    private Transformer<SimpleNeosVertex, Double>   vt;

    /**
     * Create the frame.
     */
    public EmailEntityWin(EmailMainWin parent) {
        setTitle("\u8981\u7D20\u4FE1\u606F\u5206\u6790");
        setBounds(100, 100, 800, 600);
        this.parent = parent;
        sm          = new SpinnerNumberModel(1, 1, 100000, 1);
        fsm         = new SpinnerNumberModel(1, 1, 100000, 1);

        JPanel panel_5 = new JPanel();

        getCenterPanel().add(panel_5, BorderLayout.NORTH);

        JLabel label_1 = new JLabel("\u8D77\u6B62\u65E5\u671F\uFF1A");

        panel_5.add(label_1);
        dpStart = new JXDatePicker(new Date());
        dpStart.setFormats(fmt);
        panel_5.add(dpStart);

        JLabel label_2 = new JLabel(" \u81F3 ");

        panel_5.add(label_2);
        dpEnd = new JXDatePicker(new Date());
        dpEnd.setFormats(fmt);
        panel_5.add(dpEnd);

        JLabel label_9 = new JLabel("    \u53D1\u4EF6\u4EBA");

        panel_5.add(label_9);
        textFieldFrom = new JTextField();
        panel_5.add(textFieldFrom);
        textFieldFrom.setColumns(10);

        JLabel label_10 = new JLabel("    \u6536\u4EF6\u4EBA");

        panel_5.add(label_10);
        textFieldTo = new JTextField();
        panel_5.add(textFieldTo);
        textFieldTo.setColumns(10);

        JLabel label_3 = new JLabel("      ");

        panel_5.add(label_3);

        JButton buttonAnalyze = new JButton("\u5206\u6790");

        buttonAnalyze.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            analyze();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel_5.add(buttonAnalyze);

        JPanel panel_13 = new JPanel();

        getCenterPanel().add(panel_13, BorderLayout.CENTER);
        panel_13.setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);

        panel_13.add(tabbedPane_1, BorderLayout.CENTER);

        JPanel panel_4 = new JPanel();

        tabbedPane_1.addTab("\u8981\u7D20\u5217\u8868", null, panel_4, null);
        panel_4.setPreferredSize(new Dimension(300, 10));
        panel_4.setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        tabbedPane.setPreferredSize(new Dimension(300, 5));
        panel_4.add(tabbedPane, BorderLayout.WEST);

        JPanel panel_7 = new JPanel();

        tabbedPane.addTab("\u65F6\u95F4", null, panel_7, null);
        panel_7.setLayout(new BorderLayout(0, 0));
        timeCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_7.add(timeCountPanel, BorderLayout.CENTER);

        JPanel panel_8 = new JPanel();

        tabbedPane.addTab("\u4EBA\u540D", null, panel_8, null);
        panel_8.setLayout(new BorderLayout(0, 0));
        personCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_8.add(personCountPanel, BorderLayout.CENTER);

        JPanel panel_9 = new JPanel();

        tabbedPane.addTab("\u5730\u540D", null, panel_9, null);
        panel_9.setLayout(new BorderLayout(0, 0));
        locationCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_9.add(locationCountPanel, BorderLayout.CENTER);

        JPanel panel_10 = new JPanel();

        tabbedPane.addTab("\u673A\u6784", null, panel_10, null);
        panel_10.setLayout(new BorderLayout(0, 0));
        orgnizationCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_10.add(orgnizationCountPanel, BorderLayout.CENTER);

        JPanel panel_20 = new JPanel();

        tabbedPane.addTab("\u7535\u8BDD", null, panel_20, null);
        panel_20.setLayout(new BorderLayout(0, 0));
        phoneCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_20.add(phoneCountPanel, BorderLayout.CENTER);

        JPanel panel_11 = new JPanel();

        tabbedPane.addTab("\u53F7\u7801", null, panel_11, null);
        panel_11.setLayout(new BorderLayout(0, 0));
        numberCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_11.add(numberCountPanel, BorderLayout.CENTER);

        JPanel panel_21 = new JPanel();

        tabbedPane.addTab("\u90AE\u4EF6", null, panel_21, null);
        panel_21.setLayout(new BorderLayout(0, 0));
        emailCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_21.add(emailCountPanel, BorderLayout.CENTER);

        JPanel panel_12 = new JPanel();

        tabbedPane.addTab("\u7F51\u5740", null, panel_12, null);
        panel_12.setLayout(new BorderLayout(0, 0));
        uriCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_12.add(uriCountPanel, BorderLayout.CENTER);

        JPanel panel_22 = new JPanel();

        tabbedPane.addTab("\u6570\u91CF", null, panel_22, null);
        panel_22.setLayout(new BorderLayout(0, 0));
        qulityCountPanel = new EntityCountPanel(emailListModel, idTitleMap);
        panel_22.add(qulityCountPanel, BorderLayout.CENTER);

        JPanel panel_6 = new JPanel();

        panel_4.add(panel_6, BorderLayout.CENTER);
        panel_6.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();

        panel_6.add(scrollPane, BorderLayout.CENTER);
        listEmail = new JList(emailListModel);
        scrollPane.setViewportView(listEmail);

        JPanel panel_18 = new JPanel();

        panel_6.add(panel_18, BorderLayout.NORTH);

        JPanel panel_19 = new JPanel();

        panel_6.add(panel_19, BorderLayout.SOUTH);

        JButton buttonViewEmail = new JButton("\u67E5\u770B");
        buttonViewEmail.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					showEmail();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });

        panel_19.add(buttonViewEmail);

        JPanel panel_2 = new JPanel();

        tabbedPane_1.addTab("\u5173\u7CFB\u7F51\u7EDC", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        panelGraph = new NetworkOperatePanel();
        panel_2.add(panelGraph, BorderLayout.CENTER);

        JPanel panel = new JPanel();

        panel_2.add(panel, BorderLayout.NORTH);

        JLabel label_7 = new JLabel("\u4FE1\u606F\u7C7B\u578B");

        panel.add(label_7);
        checkBoxTime = new JCheckBox("\u65F6\u95F4");
        panel.add(checkBoxTime);
        checkBoxPerson = new JCheckBox("\u4EBA\u540D");
        checkBoxPerson.setSelected(true);
        panel.add(checkBoxPerson);
        checkBoxLocation = new JCheckBox("\u5730\u540D");
        checkBoxLocation.setSelected(true);
        panel.add(checkBoxLocation);
        checkBoxOrgnization = new JCheckBox("\u673A\u6784");
        checkBoxOrgnization.setSelected(true);
        panel.add(checkBoxOrgnization);
        checkBoxPhone = new JCheckBox("\u7535\u8BDD");
        panel.add(checkBoxPhone);
        checkBoxNumber = new JCheckBox("\u53F7\u7801");
        panel.add(checkBoxNumber);
        checkBoxEmail = new JCheckBox("\u90AE\u4EF6");
        panel.add(checkBoxEmail);
        checkBoxUri = new JCheckBox("\u7F51\u5740");
        panel.add(checkBoxUri);
        checkBoxQulity = new JCheckBox("\u6570\u91CF");
        panel.add(checkBoxQulity);

        JLabel label_8 = new JLabel("        ");

        panel.add(label_8);

        JButton buttonGraphAnalyze = new JButton("\u5206\u6790");

        buttonGraphAnalyze.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            generateNetwork();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel.add(buttonGraphAnalyze);

        JPanel panel_14 = new JPanel();

        tabbedPane_1.addTab("\u76F8\u5173\u5206\u6790", null, panel_14, null);
        panel_14.setLayout(new BorderLayout(0, 0));

        JPanel panel_15 = new JPanel();

        panel_14.add(panel_15, BorderLayout.CENTER);
        panel_15.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();

        panel_15.add(scrollPane_1, BorderLayout.CENTER);
        entitySetModel = new DefaultTableModel(tabHeader, 0);
        tableEntitySet = new JTable(entitySetModel);
        scrollPane_1.setViewportView(tableEntitySet);

        JPanel     panel_16   = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_16.getLayout();

        flowLayout.setHgap(20);
        panel_14.add(panel_16, BorderLayout.NORTH);

        JLabel label_12 = new JLabel("\u6700\u4F4E\u9891\u5EA6");

        panel_16.add(label_12);
        spinnerMinFreq = new JSpinner(fsm);
        spinnerMinFreq.setPreferredSize(new Dimension(60, 22));
        panel_16.add(spinnerMinFreq);

        JButton buttonFreqSetAnalyze = new JButton("\u5206\u6790");

        buttonFreqSetAnalyze.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new Thread() {
                        public void run() {
                            generateFreqEntitySet(fsm.getNumber().intValue());
                        }
                    }.start();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
        panel_16.add(buttonFreqSetAnalyze);

        JPanel panel_17 = new JPanel();

        panel_14.add(panel_17, BorderLayout.SOUTH);

        JLabel label_4 = new JLabel("\u67E5\u627E  ");

        panel_17.add(label_4);
        textFieldSearchList = new JTextField();
        textFieldSearchList.setPreferredSize(new Dimension(150, 21));
        panel_17.add(textFieldSearchList);
        textFieldSearchList.setColumns(10);

        JLabel label_5 = new JLabel("    ");

        panel_17.add(label_5);
        chckbxKeep = new JCheckBox("\u4FDD\u6301\u9009\u62E9");
        panel_17.add(chckbxKeep);

        JLabel label_6 = new JLabel("    ");

        panel_17.add(label_6);

        JButton buttonSearch = new JButton("\u67E5\u627E");

        panel_17.add(buttonSearch);

        JLabel label_11 = new JLabel("      ");

        panel_17.add(label_11);

        JButton buttonCopy = new JButton("\u590D\u5236");

        panel_17.add(buttonCopy);

        // custom code start here
        vt = new Transformer<SimpleNeosVertex, Double>() {
            @Override
            public Double transform(SimpleNeosVertex v) {
                return 1.0;
            }
        };
        et = new Transformer<SimpleNeosEdge, Double>() {
            @Override
            public Double transform(SimpleNeosEdge e) {
                return e.getWeight();
            }
        };
    }

    void analyzeDocs(Collection<Integer> ids) {
    	clearData();
        isNewAnalyze = true;
        
        setMessage("分析中……");
        setProgress(true);
        
    	for(int docId:ids){
    		prepareIdTitle(docId);
    		addEmail(docId);
    	}
    	
    	updateCountPanels();
        setProgress(0);
        setMessage("");
    }
    
    private void prepareIdTitle(int id){
    	StringBuffer sb=new StringBuffer();
    	sb.append("Select `SendDate`, `FromAddr`, `MailSubject` From `");
    	sb.append(parent.getEmailDbName());
    	sb.append("`.`emailheader` Where `ID`=");
    	sb.append(id);
    	
    	String     sql  = sb.toString();
        Connection conn = parent.getEmailDbConnect();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while(rs.next()){
            	String date=fullFmt.format(rs.getTimestamp("SendDate"));
            	String fromAddr=rs.getString("FromAddr");
            	String subject=rs.getString("MailSubject");
            	
            	 StringBuffer tsb      = new StringBuffer();
            	 
            	 tsb.append("[#");
                 tsb.append(id);
                 tsb.append("]");
                 tsb.append("[");
                 tsb.append(date);
                 tsb.append("] [");
                 tsb.append(fromAddr);
                 tsb.append("] ");
                 tsb.append(subject);
                 idTitleMap.put(id, tsb.toString());
            }
            
            st.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    private void addEmail(int id) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("Select `Entity`, `EntityType` From `");
        sb.append(parent.getEmailDbName());
        sb.append("`.`emailentity` Where `EmailID`=");
        sb.append(id);
        
        String     sql  = sb.toString();
        Connection conn = parent.getEmailDbConnect();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while(rs.next()){
            	String           entity     = rs.getString("Entity").trim();
                String           entityType = rs.getString("EntityType");
                NamedEntityType  type       = null;
                EntityCountPanel ecPanel    = null;

                if (entityType.equals(NamedEntityType.DateTime.toString())) {
                    type    = NamedEntityType.DateTime;
                    ecPanel = timeCountPanel;
                } else if (entityType.equals(NamedEntityType.LocationName.toString())) {
                    type    = NamedEntityType.LocationName;
                    ecPanel = locationCountPanel;
                } else if (entityType.equals(NamedEntityType.PersonName.toString())) {
                    type    = NamedEntityType.PersonName;
                    ecPanel = personCountPanel;
                } else if (entityType.equals(NamedEntityType.OrgnizationName.toString())) {
                    type    = NamedEntityType.OrgnizationName;
                    ecPanel = orgnizationCountPanel;
                } else if (entityType.equals(NamedEntityType.PhoneNumber.toString())) {
                    type    = NamedEntityType.PhoneNumber;
                    ecPanel = phoneCountPanel;
                } else if (entityType.equals(NamedEntityType.MobilePhoneNumber.toString())) {
                    type    = NamedEntityType.MobilePhoneNumber;
                    ecPanel = phoneCountPanel;
                } else if (entityType.equals(NamedEntityType.EmailAddress.toString())) {
                    type    = NamedEntityType.EmailAddress;
                    ecPanel = emailCountPanel;
                } else if (entityType.equals(NamedEntityType.URL.toString())) {
                    type    = NamedEntityType.URL;
                    ecPanel = uriCountPanel;
                } else if (entityType.equals(NamedEntityType.PostalCode.toString())) {
                    type    = NamedEntityType.PostalCode;
                    ecPanel = numberCountPanel;
                } else if (entityType.equals(NamedEntityType.IDCardNumber.toString())) {
                    type    = NamedEntityType.IDCardNumber;
                    ecPanel = numberCountPanel;
                } else if (entityType.equals(NamedEntityType.GeneralNumber.toString())) {
                    type    = NamedEntityType.GeneralNumber;
                    ecPanel = qulityCountPanel;
                } else {
                    type    = NamedEntityType.GeneralNumber;
                    ecPanel = qulityCountPanel;
                }

                entityTypeMap.put(entity, type);
                ecPanel.addData(entity, id);
                
                Set<String> entitySet;

                if (idEntitySetMap.containsKey(id)) {
                    entitySet = idEntitySetMap.get(id);
                } else {
                    entitySet = new HashSet<String>();
                    idEntitySetMap.put(id, entitySet);
                }

                entitySet.add(entity);
                
//                Set<Integer> idSet;
//                if(entityIdSetMap.containsKey(entity)){
//                	idSet=entityIdSetMap.get(entity);
//                }else{
//                	idSet=new HashSet<Integer> ();
//                	entityIdSetMap.put(entity, idSet);
//                }
//                idSet.add(id);
            }
            
            
            
            
            
            st.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

    private void analyze() {
        clearData();
        isNewAnalyze = true;

        Date   start = dpStart.getDate();
        Date   end   = dpEnd.getDate();
        String from  = textFieldFrom.getText();
        String to    = textFieldTo.getText();

        if (start.after(end)) {
            setMessage("错误：结束日期不可早于起始日期。");

            return;
        }

        setMessage("分析中……");
        setProgress(true);
        prepareIdTitleMap(start, end, from, to);
        prepareData(start, end, from, to);
        updateCountPanels();
        setProgress(0);
        setMessage("");
    }

    private void prepareIdTitleMap(Date start, Date end, String from, String to) {
        StringBuffer sb = new StringBuffer();

        sb.append("Select `ID`, `emailheader`.`SendDate`, `emailheader`.`FromAddr`, `MailSubject` From `");
        sb.append(parent.getEmailDbName());
        sb.append("`.`emailheader` Inner Join (");
        sb.append("Select Distinct `EmailID` From `");
        sb.append(parent.getEmailDbName());
        sb.append("`.`emailfromto` Where `SendDate`>=\"");
        sb.append(fmt.format(start));
        sb.append(" 00:00:00\" AND `SendDate`<=\"");
        sb.append(fmt.format(end));
        sb.append(" 23:59:59\" ");

        if ((from != null) && (from.length() > 0)) {
            sb.append("AND FromAddr='");
            sb.append(from);
            sb.append("' ");
        }

        if ((to != null) && (to.length() > 0)) {
            sb.append("AND ToAddr='");
            sb.append(to);
            sb.append("'");
        }

        sb.append(") AS `tab` ON `emailheader`.`id`=`tab`.`EmailID`");

        String     sql  = sb.toString();
        Connection conn = parent.getEmailDbConnect();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                int          id       = rs.getInt("ID");
                Date         date     = rs.getTimestamp("SendDate");
                String       fromAddr = rs.getString("FromAddr");
                String       subject  = rs.getString("MailSubject");
                StringBuffer tsb      = new StringBuffer();

                tsb.append("[#");
                tsb.append(id);
                tsb.append("]");
                tsb.append("[");
                tsb.append(fullFmt.format(date));
                tsb.append("] [");
                tsb.append(fromAddr);
                tsb.append("] ");
                tsb.append(subject);
                idTitleMap.put(id, tsb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareData(Date start, Date end, String from, String to) {
        StringBuffer sb = new StringBuffer();

        sb.append("Select `emailentity`.`EmailID`, `Entity`, `EntityType` From `");
        sb.append(parent.getEmailDbName());
        sb.append("`.`emailentity` Inner Join (");
        sb.append("Select Distinct `EmailID` From `");
        sb.append(parent.getEmailDbName());
        sb.append("`.`emailfromto` Where `SendDate`>=\"");
        sb.append(fmt.format(start));
        sb.append(" 00:00:00\" AND `SendDate`<=\"");
        sb.append(fmt.format(end));
        sb.append(" 23:59:59\" ");

        if ((from != null) && (from.length() > 0)) {
            sb.append("AND FromAddr='");
            sb.append(from);
            sb.append("' ");
        }

        if ((to != null) && (to.length() > 0)) {
            sb.append("AND ToAddr='");
            sb.append(to);
            sb.append("'");
        }

        sb.append(") AS `tab` ON `emailentity`.`EmailID`=`tab`.`EmailID`");

        String     sql  = sb.toString();
        Connection conn = parent.getEmailDbConnect();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                int              id         = rs.getInt("EmailID");
                String           entity     = rs.getString("Entity").trim();
                String           entityType = rs.getString("EntityType");
                NamedEntityType  type       = null;
                EntityCountPanel ecPanel    = null;

                if (entityType.equals(NamedEntityType.DateTime.toString())) {
                    type    = NamedEntityType.DateTime;
                    ecPanel = timeCountPanel;
                } else if (entityType.equals(NamedEntityType.LocationName.toString())) {
                    type    = NamedEntityType.LocationName;
                    ecPanel = locationCountPanel;
                } else if (entityType.equals(NamedEntityType.PersonName.toString())) {
                    type    = NamedEntityType.PersonName;
                    ecPanel = personCountPanel;
                } else if (entityType.equals(NamedEntityType.OrgnizationName.toString())) {
                    type    = NamedEntityType.OrgnizationName;
                    ecPanel = orgnizationCountPanel;
                } else if (entityType.equals(NamedEntityType.PhoneNumber.toString())) {
                    type    = NamedEntityType.PhoneNumber;
                    ecPanel = phoneCountPanel;
                } else if (entityType.equals(NamedEntityType.MobilePhoneNumber.toString())) {
                    type    = NamedEntityType.MobilePhoneNumber;
                    ecPanel = phoneCountPanel;
                } else if (entityType.equals(NamedEntityType.EmailAddress.toString())) {
                    type    = NamedEntityType.EmailAddress;
                    ecPanel = emailCountPanel;
                } else if (entityType.equals(NamedEntityType.URL.toString())) {
                    type    = NamedEntityType.URL;
                    ecPanel = uriCountPanel;
                } else if (entityType.equals(NamedEntityType.PostalCode.toString())) {
                    type    = NamedEntityType.PostalCode;
                    ecPanel = numberCountPanel;
                } else if (entityType.equals(NamedEntityType.IDCardNumber.toString())) {
                    type    = NamedEntityType.IDCardNumber;
                    ecPanel = numberCountPanel;
                } else if (entityType.equals(NamedEntityType.GeneralNumber.toString())) {
                    type    = NamedEntityType.GeneralNumber;
                    ecPanel = qulityCountPanel;
                } else {
                    type    = NamedEntityType.GeneralNumber;
                    ecPanel = qulityCountPanel;
                }

                entityTypeMap.put(entity, type);
                ecPanel.addData(entity, id);

                Set<String> entitySet;

                if (idEntitySetMap.containsKey(id)) {
                    entitySet = idEntitySetMap.get(id);
                } else {
                    entitySet = new HashSet<String>();
                    idEntitySetMap.put(id, entitySet);
                }
                
                /*Set<Integer> idSet;
                if(entityIdSetMap.containsKey(entity)){
                	idSet=entityIdSetMap.get(entity);
                }else{
                	idSet=new HashSet<Integer> ();
                	entityIdSetMap.put(entity, idSet);
                }
                idSet.add(id);*/

                entitySet.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    

    private void updateCountPanels() {
        timeCountPanel.updateEntityCountTable(topEntitySet);
        locationCountPanel.updateEntityCountTable(topEntitySet);
        personCountPanel.updateEntityCountTable(topEntitySet);
        orgnizationCountPanel.updateEntityCountTable(topEntitySet);
        emailCountPanel.updateEntityCountTable(topEntitySet);
        phoneCountPanel.updateEntityCountTable(topEntitySet);
        numberCountPanel.updateEntityCountTable(topEntitySet);
        uriCountPanel.updateEntityCountTable(topEntitySet);
        qulityCountPanel.updateEntityCountTable(topEntitySet);
    }

    private void generateNetwork() {
        setMessage("生成网络图中......");
        setProgress(true);

        if (isNewAnalyze) {
            this.origGraph = buildRelationGraph();
            isNewAnalyze   = false;
        }

        setMessage("优化显示......");
        System.out.println("total vertex: " + origGraph.getVertexCount() + "\t edge: " + origGraph.getEdgeCount());
        updateNetwork();
        setMessage("");
        setProgress(0);
    }

    private Graph<SimpleNeosVertex, SimpleNeosEdge> buildRelationGraph() {
        Graph<SimpleNeosVertex, SimpleNeosEdge> graph    = new UndirectedSparseGraph<SimpleNeosVertex,
                                                               SimpleNeosEdge>();
        Set<String>                             entities = new HashSet<String>();

        int n=0;
        
        for (int id : idEntitySetMap.keySet()) {
            setProgress((n+1)*100/idEntitySetMap.keySet().size());
            n++;
        	
        	Set<String>  entitySet  = idEntitySetMap.get(id);
            List<String> entityList = new ArrayList<String>();

            for (String entity : entitySet) {
            	/*if(entityTypeMap.get(entity)==NamedEntityType.DateTime){
            		continue;
            	}
            	if(entityIdSetMap.get(entity).size()>1){
            		entityList.add(entity);
            	}*/
            	if(topEntitySet.contains(entity)){
            		entityList.add(entity);
            	}
            }
            
            //System.out.println(id+": "+entityList.size());

            for (int i = 1; i < entityList.size(); i++) {
                String entityA = entityList.get(i);

                if (!entities.contains(entityA)) {
                    entities.add(entityA);
                    graph.addVertex(new SimpleNeosVertex(entityA));
                }

                SimpleNeosVertex v1 = new SimpleNeosVertex(entityA);

                for (int j = 0; j < i; j++) {
                    String entityB = entityList.get(j);

                    if (!entities.contains(entityB)) {
                        entities.add(entityB);
                        graph.addVertex(new SimpleNeosVertex(entityB));
                    }

                    SimpleNeosVertex v2 = new SimpleNeosVertex(entityB);
                    SimpleNeosEdge   e  = graph.findEdge(v1, v2);

                    if (e == null) {
                        graph.addEdge(new SimpleNeosEdge(v1, v2, 1.0), v1, v2, EdgeType.UNDIRECTED);
                    } else {
                        double val = e.getWeight();

                        graph.removeEdge(e);
                        graph.addEdge(new SimpleNeosEdge(v1, v2, val + 1.0), v1, v2, EdgeType.UNDIRECTED);
                    }
                }
            }
        }

        return graph;
    }

    private void updateNetwork() {
        Set<NamedEntityType> typeSet = new HashSet<NamedEntityType>();

        if (this.checkBoxTime.isSelected()) {
            typeSet.add(NamedEntityType.DateTime);
        }

        if (this.checkBoxPerson.isSelected()) {
            typeSet.add(NamedEntityType.PersonName);
        }

        if (this.checkBoxLocation.isSelected()) {
            typeSet.add(NamedEntityType.LocationName);
        }

        if (this.checkBoxOrgnization.isSelected()) {
            typeSet.add(NamedEntityType.OrgnizationName);
        }

        if (this.checkBoxPhone.isSelected()) {
            typeSet.add(NamedEntityType.PhoneNumber);
            typeSet.add(NamedEntityType.MobilePhoneNumber);
        }

        if (this.checkBoxNumber.isSelected()) {
            typeSet.add(NamedEntityType.IDCardNumber);
            typeSet.add(NamedEntityType.PostalCode);
        }

        if (this.checkBoxEmail.isSelected()) {
            typeSet.add(NamedEntityType.EmailAddress);
        }

        if (this.checkBoxUri.isSelected()) {
            typeSet.add(NamedEntityType.URL);
        }

        if (this.checkBoxQulity.isSelected()) {
            typeSet.add(NamedEntityType.GeneralNumber);
        }

        EntityTypePredicate                                     pred   = new EntityTypePredicate(entityTypeMap,
                                                                             typeSet);
        VertexPredicateFilter<SimpleNeosVertex, SimpleNeosEdge> filter = new VertexPredicateFilter<SimpleNeosVertex,
                                                                             SimpleNeosEdge>(pred);
        Graph<SimpleNeosVertex, SimpleNeosEdge> fgraph = filter.transform(origGraph);

        panelGraph.setGraph(fgraph, vt, et);
    }

    private void generateFreqEntitySet(int minSupport) {
        entitySetModel.setRowCount(0);
        
        setProgress(true);

        List<List<String>> database = new ArrayList<List<String>>();

        for (int id : idEntitySetMap.keySet()) {
            Set<String>  entitySet  = idEntitySetMap.get(id);
            List<String> entityList = new ArrayList<String>();

            for (String entity : entitySet) {
                entityList.add(entity);
            }

            database.add(entityList);
        }

        Charm charm = new Charm();

        try {
            Map<Integer, List<List<String>>> res     = charm.runAlgorithm(database, minSupport, 100000);
            List<Integer>                    supList = new ArrayList<Integer>();

            for (Integer sup : res.keySet()) {
                supList.add(sup);
            }

            Collections.sort(supList);

            for (int i = supList.size() - 1; i >= 0; i--) {
                Integer            sup  = supList.get(i);
                List<List<String>> sets = res.get(sup);

                for (List<String> elist : sets) {
                    if (elist.size() < 2) {
                        continue;
                    }

                    StringBuffer sb = new StringBuffer();

                    Collections.sort(elist);

                    for (String email : elist) {
                        sb.append(email);
                        sb.append(";    ");
                    }

                    String[] row = new String[2];

                    row[0] = sup.toString();
                    row[1] = sb.toString();
                    entitySetModel.addRow(row);
                }
            }
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        setProgress(false);
    }

    private void clearData() {
        timeCountPanel.clearData();
        locationCountPanel.clearData();
        personCountPanel.clearData();
        orgnizationCountPanel.clearData();
        emailCountPanel.clearData();
        phoneCountPanel.clearData();
        numberCountPanel.clearData();
        uriCountPanel.clearData();
        qulityCountPanel.clearData();
        idTitleMap.clear();
        idEntitySetMap.clear();
        topEntitySet.clear();
        //entityIdSetMap.clear();
        entityTypeMap.clear();
        emailListModel.clear();
        entitySetModel.setRowCount(0);
    }
    
    private void showEmail(){
    	int idx=listEmail.getSelectedIndex();
    	if(idx>=0){
    		String title=listEmail.getSelectedValue().toString();
    		int idxB=title.indexOf("]");
    		final int emailId=Integer.parseInt(title.substring("[#".length(), idxB));
    		
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
    }

    private class EntityTypePredicate implements Predicate<SimpleNeosVertex> {
        private final Collection<NamedEntityType>  coll;
        private final Map<String, NamedEntityType> map;

        public EntityTypePredicate(Map<String, NamedEntityType> map, Collection<NamedEntityType> coll) {
            this.map  = map;
            this.coll = coll;
        }

        @Override
        public boolean evaluate(SimpleNeosVertex v) {
            String name = v.toString();

            if (map.containsKey(name)) {
                NamedEntityType type = map.get(name);

                if (coll.contains(type)) {
                    return true;
                }
            }

            return false;
        }
    }

    
}
