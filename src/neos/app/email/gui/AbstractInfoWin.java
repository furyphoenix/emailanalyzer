package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;

import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JButton;

import neos.app.gui.ProgressMornitor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.Format;

public abstract class AbstractInfoWin extends JFrame implements ProgressMornitor{

	private JPanel contentPane;
	private JLabel lblStatus;
	private JProgressBar progressBar;
	private JLabel lblInfo;
	private JFormattedTextField textField;
	private JEditorPane editorPane;

	

	/**
	 * Create the frame.
	 */
	public AbstractInfoWin(Format fmt) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(AbstractInfoWin.class.getResource("/icon/1307955269_applications-systemg-icon.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 10, 5, 10));
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(20, 0));
		
		lblInfo = new JLabel("    ");
		panel.add(lblInfo, BorderLayout.WEST);
		
		textField = new JFormattedTextField(fmt);
		panel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("\u67E5\u8BE2");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							onQuery();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel.add(btnNewButton, BorderLayout.EAST);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		lblStatus = new JLabel("    ");
		panel_1.add(lblStatus);
		
		progressBar = new JProgressBar();
		panel_1.add(progressBar, BorderLayout.EAST);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		scrollPane.setViewportView(editorPane);
	}
	
	public void setLabelInfo(String info){
		lblInfo.setText(info);
	}
	
	public void setText(String text){
		editorPane.setText(text);
	}
	
	public String getInput(){
		return textField.getText();
	}
	
	
	public abstract void onQuery();
	
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
