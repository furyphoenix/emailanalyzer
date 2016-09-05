package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;

import phoenix.visualization.StandardGuiUtil;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class EmailListWin extends NeosStandardFrame {
    private final static String[] header       = { "发件人", "收件人", "日期", "主题" };
    private final static String[] headerAttach = { "发件人", "收件人", "日期", "主题", "文件" };
    private DefaultTableModel     model        = new DefaultTableModel(header, 0);
    private DefaultTableModel     modelAttach  = new DefaultTableModel(headerAttach, 0);
    private JEditorPane           editorPaneAttachPreview;
    private JEditorPane           editorPaneEmailPreview;
    private EmailMainWin          parent;
    private KeywordSearchResult   res;
    private JTable                table;
    private JTable                tableAttach;

    public EmailListWin(EmailMainWin parent, KeywordSearchResult res) {
        setTitle("\u90AE\u4EF6\u5217\u8868");
        this.parent = parent;
        this.res    = res;
        this.setBounds(100, 100, 800, 600);

        JPanel panel = new JPanel();

        getCenterPanel().add(panel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        getCenterPanel().add(tabbedPane, BorderLayout.CENTER);

        JPanel panel_1 = new JPanel();

        tabbedPane.addTab("\u90AE\u4EF6\u5185\u5BB9", null, panel_1, null);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_4 = new JPanel();

        panel_1.add(panel_4, BorderLayout.SOUTH);

        JButton btnView = new JButton("\u67E5\u770B");

        panel_4.add(btnView);

        JPanel panel_5 = new JPanel();

        panel_1.add(panel_5, BorderLayout.CENTER);
        panel_5.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();

        panel_5.add(scrollPane);
        table = new JTable(model);

        ListSelectionModel lsmodel = table.getSelectionModel();

        lsmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsmodel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                showEmailPreview();
            }
        });
        scrollPane.setViewportView(table);

        JPanel panel_6 = new JPanel();

        panel_6.setPreferredSize(new Dimension(10, 100));
        panel_6.setMinimumSize(new Dimension(10, 100));
        panel_5.add(panel_6, BorderLayout.SOUTH);
        panel_6.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_2 = new JScrollPane();

        panel_6.add(scrollPane_2, BorderLayout.CENTER);
        editorPaneEmailPreview = new JEditorPane();
        editorPaneEmailPreview.setContentType("text/html");
        editorPaneEmailPreview.setEditable(false);
        StandardGuiUtil.addMouseMenu4TextComponent(editorPaneEmailPreview);
        scrollPane_2.setViewportView(editorPaneEmailPreview);
        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            viewMail();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        JPanel panel_3 = new JPanel();

        tabbedPane.addTab("\u9644\u4EF6\u5185\u5BB9", null, panel_3, null);
        panel_3.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();

        panel_3.add(panel_2, BorderLayout.SOUTH);

        JButton btnOpen = new JButton("\u6253\u5F00");

        btnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            openAttach();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel_2.add(btnOpen);

        JPanel panel_7 = new JPanel();

        panel_3.add(panel_7, BorderLayout.CENTER);
        panel_7.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();

        panel_7.add(scrollPane_1);
        tableAttach = new JTable(modelAttach);

        ListSelectionModel attSelModel = tableAttach.getSelectionModel();

        attSelModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attSelModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                showAttachPreview();
            }
        });
        scrollPane_1.setViewportView(tableAttach);

        JPanel panel_8 = new JPanel();

        panel_8.setPreferredSize(new Dimension(10, 100));
        panel_8.setMinimumSize(new Dimension(10, 100));
        panel_7.add(panel_8, BorderLayout.SOUTH);
        panel_8.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_3 = new JScrollPane();

        panel_8.add(scrollPane_3, BorderLayout.NORTH);
        editorPaneAttachPreview = new JEditorPane();
        editorPaneAttachPreview.setContentType("text/html");
        editorPaneAttachPreview.setEditable(false);
        StandardGuiUtil.addMouseMenu4TextComponent(editorPaneAttachPreview);
        panel_8.add(editorPaneAttachPreview, BorderLayout.SOUTH);

        JPanel     panel_9    = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_9.getLayout();

        flowLayout.setHgap(20);
        getCenterPanel().add(panel_9, BorderLayout.SOUTH);

        JButton buttonNetWorkAnalyze = new JButton("\u7F51\u7EDC\u5206\u6790");

        buttonNetWorkAnalyze.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            doNetworkAnalyze();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel_9.add(buttonNetWorkAnalyze);

        JButton buttonEntityAnalyze = new JButton("\u8981\u7D20\u5206\u6790");
        buttonEntityAnalyze.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try {
                    new Thread() {
                        public void run() {
                            doEntityAnalyze();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	}
        });

        panel_9.add(buttonEntityAnalyze);

        List<String[]> dataList = res.docDataList;

        for (String[] data : dataList) {
            model.addRow(data);
        }

        List<String[]> attachList = res.attDataList;

        for (String[] attach : attachList) {
            modelAttach.addRow(attach);
        }

        setMessage("共计" + dataList.size() + "封邮件正文以及" + attachList.size() + "个邮件附件符合要求");
    }

    private void viewMail() {
        int idx = table.getSelectedRow();

        if (idx < 0) {
            return;
        }

        final int        emailId = res.docIdList.get(idx);
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

    private void showEmailPreview() {
        int idx = table.getSelectedRow();

        if (idx >= 0) {
            if (idx < res.previews.size()) {
                String preview = res.previews.get(idx);

                editorPaneEmailPreview.setText(preview);
            }
        }
    }

    private void openAttach() {
        int idx = tableAttach.getSelectedRow();

        if (idx < 0) {
            return;
        }

        String[] data     = res.attDataList.get(idx);
        String   fileName = data[4];
        String   cmd      = "cmd /c call \"" + fileName + "\"";
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

    private void showAttachPreview() {
        int idx = tableAttach.getSelectedRow();

        if (idx >= 0) {
            if (idx < res.attPreviews.size()) {
                String preview = res.attPreviews.get(idx);

                editorPaneAttachPreview.setText(preview);
            }
        }
    }

    private void doNetworkAnalyze() {
        final HashSet<Integer> docIds = new HashSet<Integer>();

        for (Integer id : res.docIdList) {
            docIds.add(id);
        }

        for (Integer id : res.attIdList) {
            if (!docIds.contains(id)) {
                docIds.add(id);
            }
        }

        if (SwingUtilities.isEventDispatchThread()) {
            EmailNetworkWin win = new EmailNetworkWin(parent);

            win.setVisible(true);
            win.analyzeSearchResult(docIds);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailNetworkWin win = new EmailNetworkWin(parent);

                    win.setVisible(true);
                    win.analyzeSearchResult(docIds);
                }
            });
        }
    }
    
    private void doEntityAnalyze(){
    	final List<Integer> docIds=res.docIdList;
    	
    	if (SwingUtilities.isEventDispatchThread()) {
            EmailEntityWin win = new EmailEntityWin(parent);

            win.setVisible(true);
            win.analyzeDocs(docIds);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	EmailEntityWin win = new EmailEntityWin(parent);

                    win.setVisible(true);
                    win.analyzeDocs(docIds);
                }
            });
        }
    }
}
