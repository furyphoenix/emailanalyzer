package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import neos.tool.ip.IPSeeker;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXDatePicker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import phoenix.visualization.StandardGuiUtil;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.text.ChoiceFormat;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JEditorPane;

public class EmailIPDistWin extends NeosStandardFrame {
    private final static String   dateRegex  = "\\d{4}-\\d{1,2}-\\d{1,2}";
    private final static String[] freqHeader = { "ID", "IP", "位置", "计数" };
    private final static String[] header     = { "ID", "IP", "日期", "时间", "位置" };
    private final static String[] hours      = {
        "00时", "01时", "02时", "03时", "04时", "05时", "06时", "07时", "08时", "09时", "10时", "11时", "12时", "13时", "14时", "15时",
        "16时", "17时", "18时", "19时", "20时", "21时", "22时", "23时"
    };
    private final static String[] idFreqHeader = { "ID", "计数" };
    private final static String   ipRegex      =
        "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])";
    private final static String[] monthdays = {
        "01日", "02日", "03日", "04日", "05日", "06日", "07日", "08日", "09日", "10日", "11日", "12日", "13日", "14日", "15日", "16日",
        "17日", "18日", "19日", "20日", "21日", "22日", "23日", "24日", "25日", "26日", "27日", "28日", "29日", "30日", "31日"
    };
    private final static String[]         preiodModes    = { "每日24时", "每周七天", "每月31日" };
    private final static String[]         timeModes      = { "按日期显示", "按小时显示" };
    private final static String           timeRegex      = "\\d{1,2}(:\\d{1,2}){0,2}";
    private final static String[]         weekdays       = {
        "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
    };
    private final static SimpleDateFormat sfmt           = new SimpleDateFormat("yyyy-MM-dd");
    private final static IPSeeker         seeker         = new IPSeeker();
    private final static SimpleDateFormat hfmt           = new SimpleDateFormat("yyyy-MM-dd HH时");
    private final static SimpleDateFormat ffmt           = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Comparator                    localStringCmp = Collator.getInstance(Locale.CHINA);
    private JCheckBox                     chckbxTimeMode;
    private JComboBox                     comboBoxPeriodMode;
    private JTable                        dataTable;
    private JXDatePicker                  dpEnd;
    private JXDatePicker                  dpStart;
    private DefaultListModel              emailListModel;
    private DefaultTableModel             freqTableModel;
    private DefaultTableModel             idFreqModel;
    private JTable                        idFreqTable;
    private JList                         listEmail;
    private JPanel                        panelFreq;
    private ChartPanel                    panelFreqChart;
    private ChartPanel                    panelPeriodChart;
    private ChartPanel                    panelTimeChart;
    private EmailMainWin                  parent;
    private JTable                        tableFreq;
    private DefaultTableModel             tableModel;
    private JTextField                    textFieldAdd;
    private JTextField                    textFieldEmail;
    private JTextField                    textFieldFind;

    // custom define
    private Hashtable<String, Hashtable<String, Integer>> uriIpFreqTab;
    private DefaultListModel                              uriListModel;
    private JEditorPane editorPanePeriod;

    /**
     * Create the frame.
     */
    public EmailIPDistWin(EmailMainWin parent) {
        setTitle("IP\u5206\u5E03\u5206\u6790");
        this.parent = parent;
        setBounds(100, 100, 800, 600);
        getCenterPanel().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();

        getCenterPanel().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        panel.add(tabbedPane, BorderLayout.CENTER);

        JPanel panelData = new JPanel();

        tabbedPane.addTab("\u539F\u59CB\u6570\u636E", null, panelData, null);
        panelData.setLayout(new BorderLayout(0, 0));

        JPanel panelDataList = new JPanel();

        panelDataList.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();

        panelDataList.add(scrollPane, BorderLayout.CENTER);
        tableModel = new DefaultTableModel(header, 0);
        dataTable  = new JTable(tableModel);
        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                updateIdFreqTabSel();
            }
        });

        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<DefaultTableModel>(tableModel);

        rowSorter.setComparator(0, localStringCmp);
        rowSorter.setComparator(4, localStringCmp);
        dataTable.setRowSorter(rowSorter);
        scrollPane.setViewportView(dataTable);

        JToolBar toolBar_1 = new JToolBar();

        panelData.add(toolBar_1, BorderLayout.NORTH);

        JButton btnImport = new JButton("\u5BFC\u5165");
        btnImport.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					importRecord();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });

        btnImport.setToolTipText("\u4ECE\u6587\u4EF6\u5BFC\u5165");
        btnImport.setIcon(
            new ImageIcon(EmailIPDistWin.class.getResource("/icon/1307955451_view-process-all-icon.png")));
        toolBar_1.add(btnImport);

        JButton btnExport = new JButton("\u5BFC\u51FA");
        btnExport.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					exportRecord();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });

        btnExport.setToolTipText("\u5BFC\u51FA\u5230\u6587\u4EF6\u4E2D");
        btnExport.setIcon(new ImageIcon(EmailIPDistWin.class.getResource("/icon/1307956007_document-save-icon.png")));
        toolBar_1.add(btnExport);

        JSeparator separator = new JSeparator();

        separator.setOrientation(SwingConstants.VERTICAL);
        toolBar_1.add(separator);

        JSeparator separator_1 = new JSeparator();

        separator_1.setOrientation(SwingConstants.VERTICAL);
        toolBar_1.add(separator_1);
        textFieldFind = new JTextField();
        textFieldFind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            find();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        StandardGuiUtil.addMouseMenu4TextComponent(textFieldFind);
        toolBar_1.add(textFieldFind);
        textFieldFind.setColumns(10);

        JButton btnFilter = new JButton("ID\u67E5\u8BE2");

        btnFilter.setToolTipText("\u6309\u7167ID\u9009\u53D6\u8BB0\u5F55");
        btnFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            find();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnFilter.setIcon(new ImageIcon(EmailIPDistWin.class.getResource("/icon/1307979872_edit-find-icon.png")));
        toolBar_1.add(btnFilter);

        JSeparator separator_2 = new JSeparator();

        separator_2.setOrientation(SwingConstants.VERTICAL);
        toolBar_1.add(separator_2);

        JButton btnAnalyze = new JButton("\u5206\u6790");

        btnAnalyze.addActionListener(new ActionListener() {
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
        btnAnalyze.setToolTipText("\u5BF9\u5217\u8868\u4E2D\u6570\u636E\u8FDB\u884C\u5206\u6790");
        btnAnalyze.setIcon(
            new ImageIcon(EmailIPDistWin.class.getResource("/icon/1307955269_applications-systemg-icon.png")));
        toolBar_1.add(btnAnalyze);

        JPanel panel_1 = new JPanel();

        panel_1.setPreferredSize(new Dimension(200, 10));
        panelData.add(panel_1, BorderLayout.WEST);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_11 = new JPanel();

        panel_1.add(panel_11, BorderLayout.CENTER);
        panel_11.setLayout(new MigLayout("", "[grow]", "[][][][][][][][][][grow][][grow][grow]"));

        JLabel label_2 = new JLabel("\u90AE\u4EF6\u7B5B\u9009\u6761\u4EF6");

        label_2.setHorizontalAlignment(SwingConstants.CENTER);
        panel_11.add(label_2, "cell 0 0,alignx center");

        JLabel lblNewLabel_1 = new JLabel("    ");

        panel_11.add(lblNewLabel_1, "cell 0 1");

        JLabel label = new JLabel("\u8D77\u59CB\u65E5\u671F");

        panel_11.add(label, "cell 0 2");
        dpStart = new JXDatePicker(new Date());
        dpStart.setFormats(sfmt);
        label.setLabelFor(dpStart);
        panel_11.add(dpStart, "cell 0 3,growx");

        JLabel lblNewLabel_2 = new JLabel("    ");

        panel_11.add(lblNewLabel_2, "cell 0 4");

        JLabel label_1 = new JLabel("\u7ED3\u675F\u65E5\u671F");

        panel_11.add(label_1, "cell 0 5");
        dpEnd = new JXDatePicker(new Date());
        dpEnd.setFormats(sfmt);
        label_1.setLabelFor(dpEnd);
        panel_11.add(dpEnd, "cell 0 6,growx");

        JLabel label_3 = new JLabel("    ");

        panel_11.add(label_3, "cell 0 7");

        JLabel lblEmail = new JLabel("Email\u5730\u5740");

        panel_11.add(lblEmail, "cell 0 8");

        JPanel panel_12 = new JPanel();

        panel_12.setPreferredSize(new Dimension(10, 100));
        panel_11.add(panel_12, "cell 0 9,grow");
        panel_12.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_3 = new JScrollPane();

        panel_12.add(scrollPane_3, BorderLayout.CENTER);
        emailListModel = new DefaultListModel();
        listEmail      = new JList(emailListModel);
        StandardGuiUtil.addMouseMenu4JList(listEmail);
        scrollPane_3.setViewportView(listEmail);
        textFieldEmail = new JTextField();
        StandardGuiUtil.addMouseMenu4TextComponent(textFieldEmail);
        panel_11.add(textFieldEmail, "cell 0 10,growx");
        textFieldEmail.setColumns(10);

        JPanel     panel_13   = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_13.getLayout();

        flowLayout.setHgap(20);
        panel_11.add(panel_13, "cell 0 11,grow");

        JButton btnAddEmail = new JButton("\u6DFB\u52A0");

        btnAddEmail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            addEmailAddr();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel_13.add(btnAddEmail);

        JButton btnDelEmail = new JButton("\u5220\u9664");

        btnDelEmail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            removeEmailAddr();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel_13.add(btnDelEmail);

        JPanel panel_14 = new JPanel();

        panel_11.add(panel_14, "cell 0 12,grow");

        JButton btnFilterEmail = new JButton("\u7B5B\u9009\u90AE\u4EF6\u6570\u636E");

        btnFilterEmail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            importEmailData();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel_14.add(btnFilterEmail);

        JPanel     panelIDList = new JPanel();
        JSplitPane splitPane   = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelDataList, panelIDList);

        panelIDList.setLayout(new BorderLayout(0, 0));
        splitPane.setDividerLocation(250);

        JPanel panel_20 = new JPanel();

        panelIDList.add(panel_20, BorderLayout.NORTH);

        JLabel lblId = new JLabel("ID\u9891\u6B21\u7EDF\u8BA1");

        panel_20.add(lblId);

        JPanel panel_21 = new JPanel();

        panelIDList.add(panel_21, BorderLayout.SOUTH);

        JButton btnid = new JButton("\u5206\u6790\u9009\u4E2D\u7684ID");

        btnid.addActionListener(new ActionListener() {
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
        panel_21.add(btnid);

        JScrollPane scrollPane_5 = new JScrollPane();

        panelIDList.add(scrollPane_5, BorderLayout.CENTER);
        idFreqModel = new DefaultTableModel(idFreqHeader, 0);
        idFreqTable = new JTable(idFreqModel);
        idFreqTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                updateDataTabSel();
            }
        });

        TableRowSorter<DefaultTableModel> idFreqSorter = new TableRowSorter<DefaultTableModel>(idFreqModel);

        idFreqSorter.setComparator(0, localStringCmp);
        idFreqSorter.setComparator(1, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return Integer.parseInt(o1.toString()) - Integer.parseInt(o2.toString());
            }
        });
        idFreqTable.setRowSorter(idFreqSorter);
        scrollPane_5.setViewportView(idFreqTable);

        JPanel panel_19 = new JPanel();

        panelDataList.add(panel_19, BorderLayout.NORTH);

        JLabel label_4 = new JLabel("\u539F\u59CB\u6570\u636E\u5217\u8868");

        panel_19.add(label_4);

        JPanel panel_4 = new JPanel();

        panelDataList.add(panel_4, BorderLayout.SOUTH);
        panel_4.setBorder(new EmptyBorder(3, 5, 3, 5));
        panel_4.setLayout(new BorderLayout(0, 0));
        textFieldAdd = new JTextField();
        textFieldAdd.setToolTipText("\u8BB0\u5F55\u683C\u5F0F\uFF1A ID IP yyyy-mm-dd hh:mm:ss");
        textFieldAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            addRecord();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        StandardGuiUtil.addMouseMenu4TextComponent(textFieldAdd);
        panel_4.add(textFieldAdd, BorderLayout.CENTER);
        textFieldAdd.setColumns(10);

        JPanel panel_5 = new JPanel();

        panel_4.add(panel_5, BorderLayout.EAST);

        JButton btnAdd = new JButton("\u6DFB\u52A0\u8BB0\u5F55");

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            addRecord();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel_5.add(btnAdd);
        panelData.add(splitPane, BorderLayout.CENTER);
        panelFreq = new JPanel();
        tabbedPane.addTab("\u9891\u7387\u5206\u6790", null, panelFreq, null);
        panelFreq.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();

        panelFreq.add(panel_2, BorderLayout.CENTER);
        panel_2.setLayout(new BorderLayout(0, 0));

        JPanel panel_6 = new JPanel();

        panel_6.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel_2.add(panel_6);
        panel_6.setLayout(new BorderLayout(0, 0));
        panelFreqChart = new ChartPanel(null);
        panel_6.add(panelFreqChart, BorderLayout.CENTER);

        JPanel panel_7 = new JPanel();

        panel_7.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel_7.setPreferredSize(new Dimension(10, 200));
        panel_2.add(panel_7, BorderLayout.SOUTH);
        panel_7.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();

        panel_7.add(scrollPane_1, BorderLayout.CENTER);
        freqTableModel = new DefaultTableModel(freqHeader, 0);
        tableFreq      = new JTable(freqTableModel);
        scrollPane_1.setViewportView(tableFreq);
        uriListModel = new DefaultListModel();

        JPanel panelTime = new JPanel();

        tabbedPane.addTab("\u65F6\u95F4\u5206\u6790", null, panelTime, null);
        panelTime.setLayout(new BorderLayout(0, 0));

        JPanel panel_9 = new JPanel();

        panelTime.add(panel_9, BorderLayout.CENTER);
        panel_9.setLayout(new BorderLayout(0, 0));
        panelTimeChart = new ChartPanel(null);
        panel_9.add(panelTimeChart, BorderLayout.CENTER);

        JPanel     panel_3      = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_3.getLayout();

        flowLayout_1.setHgap(20);
        panelTime.add(panel_3, BorderLayout.NORTH);

        JButton btnRedraw = new JButton("\u91CD\u753B");

        btnRedraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            buildTimeTable(chckbxTimeMode.isSelected());
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        chckbxTimeMode = new JCheckBox("\u5C0F\u65F6\u6A21\u5F0F");
        panel_3.add(chckbxTimeMode);
        panel_3.add(btnRedraw);

        JPanel panelPeriod = new JPanel();

        tabbedPane.addTab("\u5468\u671F\u5206\u6790", null, panelPeriod, null);
        panelPeriod.setLayout(new BorderLayout(0, 0));

        JPanel panel_8 = new JPanel();

        panelPeriod.add(panel_8, BorderLayout.NORTH);
        comboBoxPeriodMode = new JComboBox(preiodModes);
        panel_8.add(comboBoxPeriodMode);

        JButton button = new JButton("\u91CD\u753B");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            buildPeriodTable(comboBoxPeriodMode.getSelectedIndex());
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel_8.add(button);

        JPanel panel_10 = new JPanel();

        panelPeriod.add(panel_10, BorderLayout.CENTER);
        panel_10.setLayout(new BorderLayout(0, 0));
        panelPeriodChart = new ChartPanel(null);
        panel_10.add(panelPeriodChart, BorderLayout.CENTER);
        
        JPanel panel_15 = new JPanel();
        panel_15.setPreferredSize(new Dimension(10, 150));
        panelPeriod.add(panel_15, BorderLayout.SOUTH);
        panel_15.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane_2 = new JScrollPane();
        panel_15.add(scrollPane_2, BorderLayout.CENTER);
        
        editorPanePeriod = new JEditorPane();
        editorPanePeriod.setEditable(false);
        StandardGuiUtil.addMouseMenu4TextComponent(editorPanePeriod);
        scrollPane_2.setViewportView(editorPanePeriod);

        // custom code start here
        uriIpFreqTab = new Hashtable<String, Hashtable<String, Integer>>();
    }

    private void importRecord() {
    	JFileChooser fc=new JFileChooser();
    	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fc.setMultiSelectionEnabled(false);
    	FileNameExtensionFilter filter=new FileNameExtensionFilter("IP数据文件(*.idf)","idf");
    	fc.setFileFilter(filter);
    	int returnVal=fc.showOpenDialog(this);
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file=fc.getSelectedFile();
			if(file==null){
				return;
			}
			try {
				BufferedReader in =
				    new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile()),
				        "GB2312"));
				try{
					String s;
					while((s=in.readLine())!=null){
						addRecord(s);
					}
					updateIDFreqTab();
				}finally{
					in.close();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
		}
    }

    private void exportRecord() {
    	JFileChooser fc=new JFileChooser();
    	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fc.setMultiSelectionEnabled(false);
    	FileNameExtensionFilter filter=new FileNameExtensionFilter("IP数据文件(*.idf)","idf");
    	fc.setFileFilter(filter);
    	int returnVal=fc.showSaveDialog(this);
    	if(returnVal==JFileChooser.APPROVE_OPTION){
    		File file=fc.getSelectedFile();
			if(file==null){
				return;
			}
			StringBuffer sb=new StringBuffer();
			for(int i=0; i<dataTable.getRowCount(); i++){
				for(int j=0; j<4; j++){
					sb.append(dataTable.getValueAt(i, j));
					sb.append(" ");
				}
				sb.append("\r\n");
			}
			
			try {
	            BufferedWriter bw =
	                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()),
	                    "gb2312"));

	            try {
	                bw.write(sb.toString());
	            } finally {
	                bw.close();
	            }
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
    	}
    }
    

    private void importEmailData() {
        Date start = dpStart.getDate();
        Date end   = dpEnd.getDate();

        if (start.after(end)) {
            this.setMessage("错误：结束日期不可早于起始日期");

            return;
        }

        List<String> idList = new ArrayList<String>();

        for (int i = 0; i < emailListModel.size(); i++) {
            idList.add(emailListModel.get(i).toString().toLowerCase());
        }

        Connection emailConn = parent.getEmailDbConnect();
        String     sql       = buildEmailDataQuery(start, end, idList);

        try {
            Statement st = emailConn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String        id   = rs.getString("FromAddr");
                String        ip   = rs.getString("IP");
                Date          date = rs.getTimestamp("SendDate");
                StringBuilder sb   = new StringBuilder();

                sb.append(id);
                sb.append(" ");
                sb.append(ip);
                sb.append(" ");
                sb.append(ffmt.format(date));
                addRecord(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateIDFreqTab();
    }

    private String buildEmailDataQuery(Date start, Date end, List<String> lst) {
        String        dbName = parent.getEmailDbName();
        StringBuilder sb     = new StringBuilder();

        sb.append("Select `FromAddr`,`SendDate`,`IP` From `");
        sb.append(dbName);
        sb.append("`.`emailcontent` where `SendDate`>=\"");
        sb.append(sfmt.format(start));
        sb.append(" 00:00:00\" AND `SendDate`<=\"");
        sb.append(sfmt.format(end));
        sb.append(" 23:59:59\" ");

        if (lst.size() > 0) {
            sb.append("AND (");
            sb.append("`FromAddr`=\"");
            sb.append(lst.get(0));
            sb.append("\"");

            for (int i = 1; i < lst.size(); i++) {
                sb.append(" OR `FromAddr`=\"");
                sb.append(lst.get(i));
                sb.append("\"");
            }

            sb.append(")");
        }

        sb.append(" AND `IP`!=\"\"");

        return sb.toString();
    }

    private void addEmailAddr() {
        String email = textFieldEmail.getText().toLowerCase();

        if (email.length() > 0) {
            emailListModel.addElement(email);
            textFieldEmail.setText("");
        }
    }

    private void removeEmailAddr() {
        Object[] values = listEmail.getSelectedValues();

        for (int i = 0; i < values.length; i++) {
            emailListModel.removeElement(values[i]);
        }
    }

    public void addRecord() {
        String record = textFieldAdd.getText();

        addRecord(record);
        updateIDFreqTab();
    }

    public void addRecord(String record) {
        String[] segs = record.split("\\s+");
        String[] data = new String[5];

        if (validRecord(record)) {
            data[0] = segs[0];
            data[1] = segs[1];
            data[2] = segs[2];

            if (segs.length == 4) {
                data[3] = segs[3];
            } else {
                data[3] = "";
            }

            data[4] = seeker.getIPLocation(data[1]).toString();
            tableModel.addRow(data);
            textFieldAdd.setText("");
        }
    }

    private boolean validRecord(String record) {
        if (record.length() <= 0) {
            this.setMessage("添加记录为空数据。");

            return false;
        }

        String[] elems = record.split("\\s+");

        if ((elems.length < 3) || (elems.length > 4)) {
            this.setMessage("记录格式为：URI IP 日期（YYYY-MM-DD） 时间（hh:mm:ss），请检查记录格式。");

            return false;
        }

        if (!Pattern.matches(ipRegex, elems[1])) {
            this.setMessage("请检查IP地址格式。");

            return false;
        }

        if (!Pattern.matches(dateRegex, elems[2])) {
            this.setMessage("请检查日期格式。");

            return false;
        }

        if (elems.length == 4) {
            if (!Pattern.matches(timeRegex, elems[3])) {
                this.setMessage("请检查时间格式。");

                return false;
            }
        }

        return true;
    }

    private void updateIDFreqTab() {
        clearUriIpFreqTab();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String uri = tableModel.getValueAt(i, 0).toString();
            String ip  = tableModel.getValueAt(i, 1).toString();

            if (uriIpFreqTab.containsKey(uri)) {
                Hashtable<String, Integer> ipFreqTab = uriIpFreqTab.get(uri);
                int                        cnt       = 0;

                if (ipFreqTab.containsKey(ip)) {
                    cnt = ipFreqTab.get(ip);
                }

                ipFreqTab.put(ip, cnt + 1);
            } else {
                Hashtable<String, Integer> ipFreqTab = new Hashtable<String, Integer>();

                ipFreqTab.put(ip, 1);
                uriIpFreqTab.put(uri, ipFreqTab);
            }
        }

        ArrayList<String>   uriList = new ArrayList<String>();
        Enumeration<String> uriEnum = uriIpFreqTab.keys();

        while (uriEnum.hasMoreElements()) {
            uriList.add(uriEnum.nextElement());
        }

        Comparator cmp = Collator.getInstance(Locale.CHINA);

        Collections.sort(uriList, cmp);

        for (String uri : uriList) {
            String[] row = new String[2];

            row[0] = uri;
            row[1] = "" + uriIpFreqTab.get(uri).size();
            idFreqModel.addRow(row);
        }
    }

    private void updateIdFreqTabSel() {
        idFreqTable.clearSelection();

        int[] rows = dataTable.getSelectedRows();

        for (int i = 0; i < rows.length; i++) {
            String id = dataTable.getValueAt(rows[i], 0).toString();

            for (int j = 0; j < idFreqTable.getModel().getRowCount(); j++) {
                String cid = idFreqTable.getValueAt(j, 0).toString();

                if (cid.equals(id)) {
                    idFreqTable.addRowSelectionInterval(j, j);

                    break;
                }
            }
        }
    }

    private void updateDataTabSel() {
        dataTable.clearSelection();

        int[] rows = idFreqTable.getSelectedRows();

        for (int i = 0; i < rows.length; i++) {
            String id = idFreqTable.getValueAt(rows[i], 0).toString();

            for (int j = 0; j < dataTable.getModel().getRowCount(); j++) {
                String cid = dataTable.getValueAt(j, 0).toString();

                if (cid.equals(id)) {
                    dataTable.addRowSelectionInterval(j, j);
                }
            }
        }
    }

    private void editRecord() {}

    private void find() {}

    private void analyze() {
        analyzeFreq();
        buildTimeTable(chckbxTimeMode.isSelected());
        buildPeriodTable(comboBoxPeriodMode.getSelectedIndex());
    }

    private void analyzeFreq() {
        buildFreqTable();
    }

    private void buildPeriodTable(int mode) {
        int[] ids = dataTable.getSelectedRows();

        if (ids.length <= 0) {
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String                 fakeip  = "127.0.0.1";
        String[]               cates   = null;

        switch (mode) {
        case 0 :    // 24 hour in day
            cates = hours;

            break;

        case 1 :    // 7 day in week
            cates = weekdays;

            break;

        case 2 :    // 31 day in month
            cates = monthdays;

            break;
        }

        for (String cate : cates) {
            dataset.addValue(0, fakeip, cate);
        }

        Set<String> ipSet      = new HashSet<String>();
        String[]    ipvalues   = new String[ids.length];
        String[]    dayvalues  = new String[ids.length];
        String[]    timevalues = new String[ids.length];

        for (int i = 0; i < ids.length; i++) {
            String ip = dataTable.getValueAt(ids[i], 1).toString();

            ipvalues[i] = ip;

            int    idx = ip.lastIndexOf(".");
            String ipm = ip.substring(0, idx) + ".*";

            if (!ipSet.contains(ipm)) {
                ipSet.add(ipm);
            }

            dayvalues[i]  = dataTable.getValueAt(ids[i], 2).toString();
            timevalues[i] = dataTable.getValueAt(ids[i], 3).toString();

            String dateDesc = dayvalues[i] + " " + timevalues[i];
            Date   date     = null;

            try {
                date = ffmt.parse(dateDesc);
            } catch (ParseException e) {
                e.printStackTrace();

                continue;
            }

            Calendar cal = Calendar.getInstance();

            cal.setTime(date);

            int    cateId = 0;
            String cate   = null;
            int    val    = 0;

            switch (mode) {
            case 0 :    // 24 hour in day
                if (timevalues.length <= 0) {
                    continue;
                }

                cateId = cal.get(Calendar.HOUR_OF_DAY);
                cate   = hours[cateId];

                break;

            case 1 :    // 7 day in week
                cateId = cal.get(Calendar.DAY_OF_WEEK)-1;
                cate   = weekdays[cateId];

                break;

            case 2 :    // 31 day in month
                cateId = cal.get(Calendar.DAY_OF_MONTH)-1;
                cate   = monthdays[cateId];

                break;
            }

            try {
                val = dataset.getValue(ipm, cate).intValue();
            } catch (Exception e) {
                val = 0;
            }

            dataset.setValue(val + 1, ipm, cate);
        }

        dataset.removeRow(fakeip);

        JFreeChart chart = ChartFactory.createBarChart("周期分布", "时间", "数量", dataset, PlotOrientation.VERTICAL, true,
                               true, false);
        Font font      = new Font("宋体", Font.PLAIN, 12);    // 底部
        Font titleFont = new Font("隶书", Font.BOLD, 25);     // 图片标题

        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));
        chart.getLegend().setItemFont(font);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        plot.getDomainAxis().setLabelFont(font);
        plot.getDomainAxis().setTickLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        
        panelPeriodChart.setChart(chart);
        
        StringBuffer sb=new StringBuffer();
        List<?> colKeys=dataset.getColumnKeys();
        List<?> rowKeys=dataset.getRowKeys();
        
        for(Object oc:colKeys){
        	String cate=oc.toString();
        	sb.append("************");
        	sb.append(cate);
        	sb.append("************");
        	sb.append("\r\n");
        	for(Object or:rowKeys){
        		String ipm=or.toString();
        		
        		int val=0;
        		try{
        			val=dataset.getValue(ipm, cate).intValue();
        		}catch(Exception e){
        			continue;
        		}
        		
        		sb.append(cate);
        		sb.append("\t");
        		sb.append(ipm);
        		sb.append("\t");
        		sb.append(val);
        		sb.append("\r\n");
        	}
        	sb.append("\r\n");
        }
        
        editorPanePeriod.setText(sb.toString());
    }

    private void buildTimeTable(boolean hourmode) {
        int[] ids = dataTable.getSelectedRows();

        if (ids.length <= 0) {
            return;
        }

        TimeSeriesCollection coll = new TimeSeriesCollection();

        // MatrixSeriesCollection coll=new MatrixSeriesCollection();
        Map<String, TimeSeries> idSeriesMap = new Hashtable<String, TimeSeries>();

        // Map<String, MatrixSeries> idSeriesMap=new Hashtable<String, MatrixSeries> ();
        Set<String> ipSet      = new HashSet<String>();
        String[]    idvalues   = new String[ids.length];
        String[]    ipvalues   = new String[ids.length];
        String[]    dayvalues  = new String[ids.length];
        String[]    timevalues = new String[ids.length];

        // Date minDate=null;
        // Date maxDate=null;
        for (int i = 0; i < ids.length; i++) {
            idvalues[i] = dataTable.getValueAt(ids[i], 0).toString();

            if (!idSeriesMap.containsKey(idvalues[i])) {
                TimeSeries s = new TimeSeries(idvalues[i]);

                coll.addSeries(s);
                idSeriesMap.put(idvalues[i], s);
            }

            /*
             * if(!idSeriesMap.containsKey(idvalues[i])){
             *       MatrixSeries s=new MatrixSeries(idvalues[i],1,1);
             *       idSeriesMap.put(idvalues[i], s);
             * }
             */
            ipvalues[i] = dataTable.getValueAt(ids[i], 1).toString();

            if (!ipSet.contains(ipvalues[i])) {
                ipSet.add(ipvalues[i]);
            }

            dayvalues[i]  = dataTable.getValueAt(ids[i], 2).toString();
            timevalues[i] = dataTable.getValueAt(ids[i], 3).toString();

            // 寻早最早最晚时间

            /*
             * if(i==0){
             *       minDate=ffmt.parse(dayvalues[i]+" 00:00:00");
             *       maxDate=ffmt.parse(dayvalues[i]+" 23:59:59");
             * }else{
             *       String minDateDesc=dayvalues[i];
             *       String maxDateDesc=dayvalues[i];
             *       if(timevalues[i].length()>0){
             *               minDateDesc+=" 00:00:00";
             *               maxDateDesc+=" 23:59:59";
             *       }else{
             *               minDateDesc=dayvalues[i]+" "+timevalues[i];
             *               maxDateDesc=minDateDesc;
             *       }
             *       Date date=ffmt.parse(minDateDesc);
             *       if(date.before(minDate)){
             *               minDate=date;
             *       }
             *       date=ffmt.parse(maxDateDesc);
             *       if(date.after(maxDate)){
             *               maxDate=date;
             *       }
             * }
             */
        }

        /*
         * long minDateValue=minDate.getTime();
         * long maxDateValue=maxDate.getTime();
         * int timeRange=0;
         * if(hourmode){
         *       timeRange=(int)((maxDateValue-minDateValue)/(1000l*3600l))+1;
         * }else{
         *       timeRange=(int)((maxDateValue-minDateValue)/(1000l*3600l*24))+1;
         * }
         */
        List<String> ipList = new ArrayList<String>();

        for (String ip : ipSet) {
            ipList.add(ip);
        }

        Collections.sort(ipList, new IPComparator());

        Map<String, Integer> ipIdxMap = new Hashtable<String, Integer>();

        /*
         * for(String id:idSeriesMap.keySet()){
         *       MatrixSeries s=new MatrixSeries(id, timeRange, ipList.size());
         *       idSeriesMap.put(id, s);
         *       coll.addSeries(s);
         * }
         */
        for (int i = 0; i < ipList.size(); i++) {
            ipIdxMap.put(ipList.get(i), i);
        }

        String[] ipArray  = new String[ipList.size()];
        double[] idxArray = new double[ipList.size()];

        for (int i = 0; i < ipList.size(); i++) {
            ipArray[i]  = ipList.get(i);
            idxArray[i] = i;
        }

        ChoiceFormat cfmt = new ChoiceFormat(idxArray, ipArray);

        for (int i = 0; i < idvalues.length; i++) {
            String     id = idvalues[i];
            TimeSeries s  = idSeriesMap.get(id);

            // MatrixSeries s=idSeriesMap.get(id);
            String            ip     = ipvalues[i];
            int               ipIdx  = ipIdxMap.get(ip);
            RegularTimePeriod period = null;

            try {
                if (hourmode) {
                    if (timevalues[i].length() <= 0) {
                        continue;
                    }

                    String dateTime = dayvalues[i] + " " + timevalues[i];
                    Date   date     = ffmt.parse(dateTime);

                    period = new Hour(date);
                } else {
                    Date date = sfmt.parse(dayvalues[i]);

                    period = new Day(date);
                }
            } catch (Exception e) {
                continue;
            }

            s.addOrUpdate(period, ipIdx);
        }

        JFreeChart chart;

        if (hourmode) {
            chart = ChartFactory.createScatterPlot("按小时IP分布图", "小时", "IP", coll, PlotOrientation.VERTICAL, true, true,
                    false);
        } else {
            chart = ChartFactory.createScatterPlot("按日期IP分布图", "日期", "IP", coll, PlotOrientation.VERTICAL, true, true,
                    false);
        }

        setChart(chart, panelTimeChart, hourmode, cfmt);
    }

    private void setChart(JFreeChart chart, ChartPanel chartPanel, boolean isHourMode, ChoiceFormat cfmt) {
        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();

        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;

            if (isHourMode == false) {
                renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("【{1}】 {2}", sfmt, cfmt));
            } else {
                renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("【{1}】 {2}", hfmt, cfmt));
            }

            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
        }

//      DateAxis axis = (DateAxis) plot.getDomainAxis();
//      //axis.setAutoTickUnitSelection(false);
//      //axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
//      if(isHourMode==false){
//            axis.setDateFormatOverride(sfmt);
//      }else{
//            axis.setDateFormatOverride(hfmt);
//      }
        configFont(chart);
        chartPanel.setChart(chart);

//      chatPanel.setChart(chart);
//      
//      /*NumberAxis numAxis = (NumberAxis)plot.getRangeAxis();
//      NumberTickUnit numUnit=numAxis.getTickUnit();
//      double numUnitTickSize=numUnit.getSize();
//      numAxis.setTickUnit(new NumberTickUnit((int)(numUnitTickSize+1)));*/
//      NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }

    private void configFont(JFreeChart chart) {

        // 配置字体
        Font   xfont     = new Font("宋体", Font.PLAIN, 12);    // X轴
        Font   yfont     = new Font("宋体", Font.PLAIN, 12);    // Y轴
        Font   kfont     = new Font("宋体", Font.PLAIN, 12);    // 底部
        Font   titleFont = new Font("隶书", Font.BOLD, 25);     // 图片标题
        XYPlot plot      = chart.getXYPlot();                 // 图形的绘制结构对象

        // 图片标题
        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));

        // 底部
        chart.getLegend().setItemFont(kfont);
        plot.getDomainAxis().setLabelFont(xfont);
        plot.getDomainAxis().setTickLabelFont(xfont);
        plot.getRangeAxis().setLabelFont(yfont);
    }

    private void buildFreqTable() {
        int[] ids = idFreqTable.getSelectedRows();

        if (ids.length <= 0) {
            return;
        }

        Object[] values = new String[ids.length];

        for (int i = 0; i < ids.length; i++) {
            values[i] = idFreqTable.getValueAt(ids[i], 0);
        }

        freqTableModel.setRowCount(0);

        DefaultCategoryDataset dataset    = new DefaultCategoryDataset();
        ArrayList<FreqRecord>  recordList = new ArrayList<FreqRecord>();

        for (int i = 0; i < values.length; i++) {
            String                     uri       = values[i].toString().toLowerCase();
            Hashtable<String, Integer> ipFreqTab = uriIpFreqTab.get(uri);
            ArrayList<String>          ipList    = new ArrayList<String>();
            Enumeration<String>        ipEnum    = ipFreqTab.keys();

            while (ipEnum.hasMoreElements()) {
                ipList.add(ipEnum.nextElement());
            }

            Collections.sort(ipList, new IPComparator());

            for (int j = 0; j < ipList.size(); j++) {
                String[] freqRowData = new String[4];

                freqRowData[0] = uri;
                freqRowData[1] = ipList.get(j);
                freqRowData[2] = seeker.getIPLocation(freqRowData[1]).toString();
                freqRowData[3] = ipFreqTab.get(freqRowData[1]).toString();
                freqTableModel.addRow(freqRowData);
                recordList.add(new FreqRecord(freqRowData[0], freqRowData[1], ipFreqTab.get(freqRowData[1])));
            }
        }

        Collections.sort(recordList);

        for (FreqRecord rec : recordList) {
            dataset.addValue(rec.cnt, rec.uri, rec.ip);
        }

        JFreeChart chart = ChartFactory.createBarChart("频率分布图", "IP", "计数值", dataset, PlotOrientation.VERTICAL, true,
                               true, false);
        Font titleFont = new Font("隶书", Font.BOLD, 25);
        Font font      = new Font("宋体", Font.PLAIN, 12);

        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));
        chart.getLegend().setItemFont(font);

        CategoryPlot plot       = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();

        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        domainAxis.setLabelFont(font);
        domainAxis.setTickLabelFont(font);

        ValueAxis rangeAxis = plot.getRangeAxis();

        rangeAxis.setLabelFont(font);
        rangeAxis.setTickLabelFont(font);
        panelFreqChart.setChart(chart);
    }

    /*
     * private void listURI() {
     *   clearUriIpFreqTab();
     *
     *   for (int i = 0; i < tableModel.getRowCount(); i++) {
     *       String uri = tableModel.getValueAt(i, 0).toString();
     *       String ip  = tableModel.getValueAt(i, 1).toString();
     *
     *       if (uriIpFreqTab.containsKey(uri)) {
     *           Hashtable<String, Integer> ipFreqTab = uriIpFreqTab.get(uri);
     *           int                        cnt       = 0;
     *
     *           if (ipFreqTab.containsKey(ip)) {
     *               cnt = ipFreqTab.get(ip);
     *           }
     *
     *           ipFreqTab.put(ip, cnt + 1);
     *       } else {
     *           Hashtable<String, Integer> ipFreqTab = new Hashtable<String, Integer>();
     *
     *           ipFreqTab.put(ip, 1);
     *           uriIpFreqTab.put(uri, ipFreqTab);
     *       }
     *   }
     *
     *   ArrayList<String>   uriList = new ArrayList<String>();
     *   Enumeration<String> uriEnum = uriIpFreqTab.keys();
     *
     *   while (uriEnum.hasMoreElements()) {
     *       uriList.add(uriEnum.nextElement());
     *   }
     *
     *   Comparator cmp = Collator.getInstance(Locale.CHINA);
     *
     *   Collections.sort(uriList, cmp);
     *
     *   for (String uri : uriList) {
     *       uriListModel.addElement(uri);
     *   }
     * }
     */
    private void clean() {}

    private void clearUriIpFreqTab() {
        for (String uri : uriIpFreqTab.keySet()) {
            uriIpFreqTab.get(uri).clear();
        }

        uriIpFreqTab.clear();
    }

    private class FreqRecord implements Comparable<FreqRecord> {
        public final IPComparator cmp = new IPComparator();
        public int                cnt;
        public String             ip;
        public String             uri;

        public FreqRecord(String uri, String ip, int cnt) {
            this.uri = uri;
            this.ip  = ip;
            this.cnt = cnt;
        }

        @Override
        public int compareTo(FreqRecord rec) {
            int v1 = cmp.compare(ip, rec.ip);

            if (v1 != 0) {
                return v1;
            } else {
                return uri.compareTo(rec.uri);
            }
        }
    }


    private class IPComparator implements Comparator<String> {
        @Override
        public int compare(String ip1, String ip2) {
            if (ip2Long(ip1) - ip2Long(ip2) > 0) {
                return 1;
            } else if (ip2Long(ip1) - ip2Long(ip2) < 0) {
                return -1;
            } else {
                return 0;
            }
        }

        private long ip2Long(String ip) {
            String[] segs  = ip.split("\\.");
            long     value = 0l;

            for (int i = 0; i < 4; i++) {
                long partA = Long.parseLong(segs[0]);
                long partB = Long.parseLong(segs[1]);
                long partC = Long.parseLong(segs[2]);
                long partD = Long.parseLong(segs[3]);

                value = (partA << 24) + (partB << 16) + (partC << 8) + partD;
            }

            return value;
        }
    }
}
