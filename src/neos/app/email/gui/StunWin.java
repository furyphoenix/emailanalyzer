package neos.app.email.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StunWin extends NeosStandardFrame {
	private final StunWin self;

	/**
	 * Create the frame.
	 */
	public StunWin() {
		setBounds(100, 100, 450, 300);
		setTitle("消息");
		self=this;
		
		JPanel panel = new JPanel();
		getCenterPanel().add(panel, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("\u786E\u5B9A");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							self.dispose();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel.add(btnOK);
		
		JPanel panel_1 = new JPanel();
		getCenterPanel().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setText("\u9519\u8BEF\uFF1A\u672C\u529F\u80FD\u5728\u5F53\u524D\u6388\u6743\u4E0B\u65E0\u6CD5\u4F7F\u7528\uFF0C\u8BF7\u5347\u7EA7\u60A8\u7684\u6388\u6743\u7C7B\u578B\u3002");
		editorPane.setEditable(false);
		scrollPane.setViewportView(editorPane);

	}

}
