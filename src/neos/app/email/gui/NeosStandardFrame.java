package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import neos.app.gui.ProgressMornitor;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class NeosStandardFrame extends JFrame implements ProgressMornitor {
	private static final long serialVersionUID = -1337307411468551661L;
	private JPanel       contentPane;
    private JLabel       lblStatus;
    private JPanel       panelCenter;
    private JProgressBar progressBar;

    /**
     * Create the frame.
     */
    public NeosStandardFrame() {
        setIconImage(
            Toolkit.getDefaultToolkit().getImage(NeosStandardFrame.class.getResource("/icon/1307955269_applications-systemg-icon.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panel = new JPanel();

        contentPane.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));
        lblStatus = new JLabel("    ");
        panel.add(lblStatus, BorderLayout.CENTER);
        progressBar = new JProgressBar();
        panel.add(progressBar, BorderLayout.EAST);
        panelCenter = new JPanel();
        contentPane.add(panelCenter, BorderLayout.CENTER);
        panelCenter.setLayout(new BorderLayout(0, 0));
    }

    public JPanel getCenterPanel() {
        return panelCenter;
    }

    @Override
    public void setMessage(String mess) {
        lblStatus.setText("    " + mess);
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
