package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import java.awt.Toolkit;

public class MapWin extends JFrame {

	private JPanel contentPane;
	private HtmlPanel htmlPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MapWin frame = new MapWin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MapWin() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MapWin.class.getResource("/icon/1307955269_applications-systemg-icon.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 560, 560);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		htmlPanel=new HtmlPanel();
		contentPane.add(htmlPanel, BorderLayout.CENTER);
	}
	
	public void setMapUrl(String url){
		String src="<html><head>google static map</head><body><img src=\""+url+"\"></body><html>";
		UserAgentContext ucontext=new SimpleUserAgentContext();
		SimpleHtmlRendererContext rcontext=new SimpleHtmlRendererContext(htmlPanel, ucontext);
		htmlPanel.setHtml(src, "file:///", rcontext);
		System.out.println(url);
	}

}
