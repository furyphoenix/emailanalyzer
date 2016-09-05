package neos.app.email.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EmailFileNameRegexWin extends NeosStandardFrame {
	private EmailMainWin parent;
	private JTextField textFieldRegex;
	

	

	/**
	 * Create the frame.
	 */
	public EmailFileNameRegexWin(EmailMainWin parent) {
		setTitle("\u6B63\u5219\u8868\u8FBE\u5F0F");
		setBounds(100, 100, 450, 150);
		
		
		
		//custom code start here
		this.parent=parent;
		getCenterPanel().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getCenterPanel().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[72px][66px,grow][]", "[21px][]"));
		
		JLabel lblNewLabel = new JLabel("\u6B63\u5219\u8868\u8FBE\u5F0F\uFF1A  ");
		panel.add(lblNewLabel, "cell 0 1,alignx trailing");
		
		textFieldRegex = new JTextField();
		panel.add(textFieldRegex, "cell 1 1,growx");
		textFieldRegex.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		getCenterPanel().add(panel_1, BorderLayout.SOUTH);
		
		JButton btnSelDir = new JButton("\u9009\u53D6\u6587\u4EF6\u5939");
		btnSelDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							addDirWithRegex();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_1.add(btnSelDir);

	}
	
	private void addDirWithRegex(){
		String regex=textFieldRegex.getText();
		
		if(regex.length()<1){
			setMessage("错误：请输入正则表达式。");
			return;
		}
		
		
		this.dispose();
		parent.addDirWithRegex(regex);
	}

}
