package neos.app.email.gui;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTabbedPane;

public class EmailTopicWin extends NeosStandardFrame {
	private static final long serialVersionUID = -2396111226701016723L;
	private final static SimpleDateFormat fmt = new SimpleDateFormat(
			"yyyy-MM-dd");
	private final static DecimalFormat df = new DecimalFormat("#.########");
	private final EmailMainWin parent;

	private JXDatePicker dpEnd;
	private JXDatePicker dpStart;

	public EmailTopicWin(EmailMainWin parent) {
		this.parent = parent;

		this.setBounds(100, 100, 800, 600);
		this.setTitle("邮件主题分析");
		
		JPanel panel = new JPanel();
        getCenterPanel().add(panel, BorderLayout.NORTH);

        JLabel label = new JLabel("\u8D77\u6B62\u65E5\u671F\uFF1A  ");
        panel.add(label);

        dpStart = new JXDatePicker(new Date());
        dpStart.setFormats(fmt);
        panel.add(dpStart);

        JLabel label_1 = new JLabel("  \u81F3  ");
        panel.add(label_1);

        dpEnd = new JXDatePicker(new Date());
        dpEnd.setFormats(fmt);
        panel.add(dpEnd);

        JLabel lblNewLabel = new JLabel("        ");
        panel.add(lblNewLabel);

        JButton btnAnalyse = new JButton("\u5206\u6790");
        btnAnalyse.addActionListener(new ActionListener() {
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
        panel.add(btnAnalyse);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getCenterPanel().add(tabbedPane, BorderLayout.CENTER);
        
        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("词汇云图", null, panel_1, null);
        
        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("主题聚类", null, panel_2, null);
	}
	
	
	private void analyze(){
		Date start = dpStart.getDate();
        Date end = dpEnd.getDate();
        
	}

}
