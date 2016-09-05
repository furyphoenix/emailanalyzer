package neos.app.email.gui;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

public class EmailAboutWin extends NeosStandardFrame {
	private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy年MM月dd日");

    /**
     * Create the frame.
     */
    public EmailAboutWin() {
        super();
        setTitle("\u5173\u4E8E");
        setBounds(100, 100, 450, 300);

        JScrollPane scrollPane = new JScrollPane();

        getCenterPanel().add(scrollPane, BorderLayout.CENTER);

        JEditorPane editorPane = new JEditorPane();

        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        scrollPane.setViewportView(editorPane);
        editorPane.setText(prepareHtml());
    }

    private String prepareHtml() {
        StringBuilder sb = new StringBuilder();

        sb.append("<center><h2>电子邮件智能检索分析系统 1.0.1</h2></center><br>");
        sb.append("<center><h3>西北工业大学</h3></center></br>");

        if (LicenseChecker.checkLicense()) {
            sb.append("本软件注册给：");
            sb.append(LicenseChecker.getUserName());
            sb.append("<br>");
        } else {
            sb.append("本机机器码：");
            sb.append(LicenseChecker.getMachineId());
            sb.append("<br>");
            sb.append("请向软件作者leexynwpu@gmail.com发送邮件，注明机器码与注册用户名称，以获取授权文件。<br>");
        }

        return sb.toString();
    }
}
