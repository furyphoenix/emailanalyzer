package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import neos.app.gui.ProgressMornitor;
import neos.app.util.FileExtensionFilter;
import neos.app.util.FileNameRegexFilter;
import neos.app.util.FileOrFilter;
import neos.app.util.FileUtil;

import neos.component.ner.NeosDefaultNerTool;
import neos.component.ner.NeosNerTool;

import neos.tool.fudannlp.NeosFudanTimeTool;
import neos.tool.ip.DbIPLocator;
import neos.tool.ip.IPSeeker;
import neos.tool.mime4j.NeosMime4JTool;

import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import java.lang.management.ManagementFactory;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EmailMainWin implements ProgressMornitor {
    private final static String mailStorePath = ".\\email\\";
    private UserDictionary      dict;
    private JFrame              frame;
    private JLabel              lblStatus;
    private Connection          m_emailConnection;
    private String              m_emailDbName;
    private Connection          m_knowledgeConnection;
    private JMenu               mnAnalyze;
    private JMenu               mnConfig;
    private JMenu               mnFile;
    private JMenu               mnNote;
    private JMenu               mnSearch;
    private JMenu               mnTool;
    private JMenuItem           mntmMaiSource;
    private NeosNerTool         nerTool;
    private JProgressBar        progressBar;
    private final EmailMainWin  self;
    private NeosFudanTimeTool   timeTool;

    /**
     * Create the application.
     */
    public EmailMainWin() {
        self = this;
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
 
                    // UIManager.setLookAndFeel(new SubstanceSaharaLookAndFeel());
                    // JFrame.setDefaultLookAndFeelDecorated(true);
                    // JDialog.setDefaultLookAndFeelDecorated(true);
                    EmailMainWin window = new EmailMainWin();

                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("\u7535\u5B50\u90AE\u4EF6\u667A\u80FD\u68C0\u7D22\u5206\u6790\u7CFB\u7EDF");
        frame.setIconImage(
            Toolkit.getDefaultToolkit().getImage(
                EmailMainWin.class.getResource("/icon/1307979825_mail-mark-unread-envelope-icon.png")));
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        frame.setJMenuBar(menuBar);
        mnFile = new JMenu("\u6587\u4EF6");
        menuBar.add(mnFile);

        JMenuItem mntmAddEmail = new JMenuItem("\u6DFB\u52A0\u90AE\u4EF6");

        mntmAddEmail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            addEmail();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnFile.add(mntmAddEmail);

        JMenuItem mntmAddDir = new JMenuItem("\u6DFB\u52A0\u6587\u4EF6\u5939");

        mntmAddDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            addDir();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnFile.add(mntmAddDir);

        JMenuItem mntmRegex = new JMenuItem("\u6761\u4EF6\u6DFB\u52A0");

        mntmRegex.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showRegexWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnFile.add(mntmRegex);

        JSeparator separator = new JSeparator();

        mnFile.add(separator);

        JMenuItem mntmExit = new JMenuItem("\u9000\u51FA");

        mnFile.add(mntmExit);
        mnConfig = new JMenu("\u914D\u7F6E");
        menuBar.add(mnConfig);

        JMenuItem mntmConfigDb = new JMenuItem("\u6570\u636E\u5E93\u914D\u7F6E");

        mntmConfigDb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showDbConfigWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnConfig.add(mntmConfigDb);

        JMenuItem mntmUserDict = new JMenuItem("\u7528\u6237\u8BCD\u5178\u914D\u7F6E");

        mntmUserDict.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showUserDictWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnConfig.add(mntmUserDict);
        mnSearch = new JMenu("\u68C0\u7D22");
        menuBar.add(mnSearch);

        JMenuItem mntmKeywordSearch = new JMenuItem("\u90AE\u4EF6\u5168\u6587\u68C0\u7D22");

        mntmKeywordSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showSearchWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnSearch.add(mntmKeywordSearch);

        JMenuItem mntmCustomSearch = new JMenuItem("\u7528\u6237\u5B9A\u5236\u68C0\u7D22");

        mntmCustomSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showCustomSearchWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnSearch.add(mntmCustomSearch);
        
        JMenuItem mntmEntitySearch = new JMenuItem("\u4FE1\u606F\u8981\u7D20\u68C0\u7D22");
        mntmEntitySearch.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					showEntitySearchWin();
        				}
        			}.start();
        		}catch(Exception ee){
        			ee.printStackTrace();
        		}
        	}
        });
        mnSearch.add(mntmEntitySearch);
        
        JMenuItem mntmAttachmentSearch = new JMenuItem("\u9644\u4EF6\u5206\u53D1\u68C0\u7D22");
        mntmAttachmentSearch.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try {
                    new Thread() {
                        public void run() {
                        	showAttachmentSearchWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
        	}
        });
        mnSearch.add(mntmAttachmentSearch);
        mnAnalyze = new JMenu("\u5206\u6790");
        menuBar.add(mnAnalyze);
        mntmMaiSource = new JMenuItem("\u90AE\u4EF6\u6765\u6E90\u5206\u6790");
        mntmMaiSource.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showIpSourceWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnAnalyze.add(mntmMaiSource);

        JMenuItem mntmEmailComm = new JMenuItem("\u5F80\u6765\u5173\u7CFB\u5206\u6790");

        mntmEmailComm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showFromToWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnAnalyze.add(mntmEmailComm);

        JMenuItem mntmNetwork = new JMenuItem("\u90AE\u4EF6\u7F51\u7EDC\u5206\u6790");

        mntmNetwork.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showNetworkWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnAnalyze.add(mntmNetwork);

        JMenuItem mntmFromToRule = new JMenuItem("\u6765\u5F80\u89C4\u5F8B\u5206\u6790");

        mntmFromToRule.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showActiveRuleWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnAnalyze.add(mntmFromToRule);

        JMenuItem mntmIPDist = new JMenuItem("IP\u503C\u5206\u5E03\u5206\u6790");

        mntmIPDist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showIpDistWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnAnalyze.add(mntmIPDist);
        
        JMenuItem mntmEntity = new JMenuItem("\u4FE1\u606F\u8981\u7D20\u5206\u6790");
        mntmEntity.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try {
                    new Thread() {
                        public void run() {
                            showEntityWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	}
        });
        mnAnalyze.add(mntmEntity);
        mnTool = new JMenu("\u5DE5\u5177");
        menuBar.add(mnTool);

        JMenuItem mntmIPInfo = new JMenuItem("IP\u5730\u7406\u4FE1\u606F\u67E5\u8BE2");

        mntmIPInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showIpInfoWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnTool.add(mntmIPInfo);

        JMenuItem mntmZipcodeInfo = new JMenuItem("\u90AE\u653F\u7F16\u7801\u67E5\u8BE2");

        mntmZipcodeInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showZipInfoWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnTool.add(mntmZipcodeInfo);

        JMenuItem mntmIdcardInfo = new JMenuItem("\u8EAB\u4EFD\u8BC1\u5F52\u5C5E\u67E5\u8BE2");

        mntmIdcardInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showIdCardInfoWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnTool.add(mntmIdcardInfo);

        JMenuItem mntmAreaCodeInfo = new JMenuItem("\u957F\u9014\u533A\u53F7\u67E5\u8BE2");

        mntmAreaCodeInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showAreaCodeInfoWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnTool.add(mntmAreaCodeInfo);

        JMenuItem mntmMobileInfo = new JMenuItem("\u624B\u673A\u53F7\u7801\u67E5\u8BE2");

        mntmMobileInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showMobileInfoWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnTool.add(mntmMobileInfo);

        JMenuItem mntmLocationInfo = new JMenuItem("\u5730\u540D\u5730\u56FE\u67E5\u8BE2");

        mntmLocationInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showLocationInfoWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnTool.add(mntmLocationInfo);
        mnNote = new JMenu("\u5907\u6CE8");
        menuBar.add(mnNote);

        JMenuItem mntmEmailNote = new JMenuItem("\u90AE\u4EF6\u5907\u6CE8");

        mntmEmailNote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showEmailNoteWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnNote.add(mntmEmailNote);

        JMenuItem mntmBoxNote = new JMenuItem("\u90AE\u7BB1\u5907\u6CE8");

        mntmBoxNote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showEmailBoxNoteWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnNote.add(mntmBoxNote);

        JMenu mnHelp = new JMenu("\u5E2E\u52A9");

        menuBar.add(mnHelp);

        JMenuItem mntmUserGuide = new JMenuItem("\u7528\u6237\u624B\u518C");

        mntmUserGuide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showUserGuide();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnHelp.add(mntmUserGuide);

        JSeparator separator_1 = new JSeparator();

        mnHelp.add(separator_1);

        JMenuItem mntmAbout = new JMenuItem("\u5173\u4E8E");

        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            showAboutWin();
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnHelp.add(mntmAbout);

        JPanel panel = new JPanel();

        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));
        lblStatus = new JLabel("    ");
        panel.add(lblStatus, BorderLayout.CENTER);
        progressBar = new JProgressBar();
        panel.add(progressBar, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane();

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JEditorPane editorPane = new JEditorPane();

        editorPane.setEditable(false);
        scrollPane.setViewportView(editorPane);

        // custom code start here
        enableMenu(false);
        dict     = new UserDictionary();
        nerTool  = new NeosDefaultNerTool();
        timeTool = NeosFudanTimeTool.getInstance();
    }

    private void enableMenu(boolean enable) {

        //final boolean is=enable&LicenseChecker.checkLicense();
        final boolean is = enable;

        if (SwingUtilities.isEventDispatchThread()) {
            mnFile.setEnabled(is);
            mnSearch.setEnabled(is);
            mnAnalyze.setEnabled(is);
            mnTool.setEnabled(is);
            mnNote.setEnabled(is);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    mnFile.setEnabled(is);
                    mnSearch.setEnabled(is); 
                    mnAnalyze.setEnabled(is);
                    mnTool.setEnabled(is);
                    mnNote.setEnabled(is);
                }
            });
        }
    }

    private void addEmail() {
        if (m_emailConnection != null) {
            EmailDbHelper           helper = new EmailDbHelper(this.m_emailDbName, m_emailConnection,this.nerTool);
            JFileChooser            fc     = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("email文件(*.eml, *.msg)", "eml", "msg");

            fc.setFileFilter(filter);
            fc.setMultiSelectionEnabled(true);

            int returnVal = fc.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                EmailEncyptListWin listWin = new EmailEncyptListWin(this);

                listWin.setVisible(true);
                helper.setEncyptAttachmentMessageContainer(listWin);

                File[] files = fc.getSelectedFiles();

                setMessage("读取邮件中……");

                // byte[] buffer = new byte[1444];
                for (int i = 0; i < files.length; i++) {
                    setMessage("读取邮件中……正在读取" + (i + 1) + "/" + files.length + "封邮件");
                    setProgress((i + 1) * 100 / files.length);
                    addOneEmail(files[i], helper);
                }

                setMessage("");
                setProgress(0);
            }

            helper.writeIndex();
        }
    }

    private void addDir() {
        if (m_emailConnection != null) {
            EmailDbHelper helper = new EmailDbHelper(this.m_emailDbName, m_emailConnection, this.nerTool);
            JFileChooser  fc     = new JFileChooser();

            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setMultiSelectionEnabled(false);

            int returnVal = fc.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                EmailEncyptListWin listWin = new EmailEncyptListWin(this);

                listWin.setVisible(true);
                helper.setEncyptAttachmentMessageContainer(listWin);
                setMessage("读取邮件中……");

                File       file   = fc.getSelectedFile();
                FileFilter filter = new FileOrFilter(new FileExtensionFilter("eml"), new FileExtensionFilter("msg"));
                List<File> files  = FileUtil.listFile(file, filter, true);

                /*
                 * for(int i=0; i<files.size(); i++){
                 *       setMessage("读取邮件中……正在读取"+(i+1)+"/"+files.size()+"封邮件");
                 *       setProgress((i+1)*100/files.size());
                 *       addOneEmail(files.get(i), helper);
                 * }
                 */
                addEmailWithMultiThread(files, helper);
                setMessage("");
                setProgress(0);
            }

            helper.writeIndex();
        }
    }

    private void showRegexWin() {
        EmailFileNameRegexWin win = new EmailFileNameRegexWin(this);

        win.setVisible(true);
    }

    public void addDirWithRegex(String regex) {
        if (m_emailConnection != null) {
            EmailDbHelper helper = new EmailDbHelper(this.m_emailDbName, m_emailConnection, this.nerTool);
            JFileChooser  fc     = new JFileChooser();

            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setMultiSelectionEnabled(false);

            int returnVal = fc.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                EmailEncyptListWin listWin = new EmailEncyptListWin(this);

                listWin.setVisible(true);
                helper.setEncyptAttachmentMessageContainer(listWin);
                setMessage("读取邮件中……");

                File       file   = fc.getSelectedFile();
                FileFilter filter = new FileNameRegexFilter(regex);
                List<File> files  = FileUtil.listFile(file, filter, true);

                /*
                 * for(int i=0; i<files.size(); i++){
                 *       setMessage("读取邮件中……正在读取"+(i+1)+"/"+files.size()+"封邮件");
                 *       setProgress((i+1)*100/files.size());
                 *       addOneEmail(files.get(i), helper);
                 * }
                 */
                addEmailWithMultiThread(files, helper);
                setMessage("");
                setProgress(0);
            }

            helper.writeIndex();
        }
    }

    private void addEmailWithMultiThread(List<File> fileList, EmailDbHelper helper) {
        MonitorEmailTask montask = new MonitorEmailTask(fileList);
        Timer            timer   = new Timer();

        timer.schedule(montask, 1000, 1000);

        int                cpu   = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
        ThreadPoolExecutor exec  = (ThreadPoolExecutor) Executors.newFixedThreadPool(cpu);
        List<Future<?>>    flist = new ArrayList<Future<?>>();

        for (int i = 0; i < cpu; i++) {
            Future<?> t = exec.submit(new AddEmailTask(fileList, helper));

            flist.add(t);
        }

        exec.shutdown();

        for (Future<?> t : flist) {
            try {
                t.get();
            } catch (Exception e) {

                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        timer.cancel();
    }

    private void addOneEmail(File file, EmailDbHelper helper) {
        byte[] buffer = new byte[2048];

        try {
            MimeEntityConfig config = new MimeEntityConfig();

            config.setMaximalBodyDescriptor(true);
            config.setMaxLineLen(-1);

            Message msg    = new Message(new FileInputStream(file), config);
            String  dbName = m_emailDbName;

            // int id=addOneEmailToDb(msg);
            NeosMime4JTool tool = new NeosMime4JTool(msg);
            int            id   = helper.addMessage(msg, tool);

            if (id >= 0) {
                FileInputStream  fi       = new FileInputStream(file);
                FileOutputStream fo       = new FileOutputStream(mailStorePath + "\\" + dbName + "\\" + id + ".eml");
                int              bytesum  = 0;
                int              byteread = 0;

                while ((byteread = fi.read(buffer)) != -1) {
                    bytesum += byteread;    // 字节数     文件大小
                    fo.write(buffer, 0, byteread);
                }

                fi.close();
                fo.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(file.getName());
        }
    }

    private void showDbConfigWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailDbConfigWin win = new EmailDbConfigWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailDbConfigWin win = new EmailDbConfigWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showUserDictWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailUserDictWin win = new EmailUserDictWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailUserDictWin win = new EmailUserDictWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showSearchWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailBrowserWin win = new EmailBrowserWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailBrowserWin win = new EmailBrowserWin(self);

                    win.setVisible(true);
                }
            });
        }
    }
    
    private void showEntitySearchWin(){
    	if (SwingUtilities.isEventDispatchThread()) {
            EmailEntitySearchWin win = new EmailEntitySearchWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	EmailEntitySearchWin win = new EmailEntitySearchWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showCustomSearchWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailCustomSearchWin win = new EmailCustomSearchWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailCustomSearchWin win = new EmailCustomSearchWin(self);

                    win.setVisible(true);
                }
            });
        }
    }
    
    private void showAttachmentSearchWin(){
    	if (SwingUtilities.isEventDispatchThread()) {
            EmailAttachmentSearchWin win = new EmailAttachmentSearchWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailAttachmentSearchWin win = new EmailAttachmentSearchWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showActiveRuleWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailActiveRuleWin win = new EmailActiveRuleWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailActiveRuleWin win = new EmailActiveRuleWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showIpSourceWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailIpSourceWin win = new EmailIpSourceWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailIpSourceWin win = new EmailIpSourceWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showFromToWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailFromToWin win = new EmailFromToWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailFromToWin win = new EmailFromToWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showNetworkWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailNetworkWin win = new EmailNetworkWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailNetworkWin win = new EmailNetworkWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showIpDistWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailIPDistWin win = new EmailIPDistWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailIPDistWin win = new EmailIPDistWin(self);

                    win.setVisible(true);
                }
            });
        }
    }
    
    private void showEntityWin(){
    	if (SwingUtilities.isEventDispatchThread()) {
            EmailEntityWin win = new EmailEntityWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailEntityWin win = new EmailEntityWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showIpInfoWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            IpLocationWin win = new IpLocationWin();

            win.setIPSeeker(new IPSeeker());
            win.setDbIPLocator(new DbIPLocator(m_knowledgeConnection));
            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    IpLocationWin win = new IpLocationWin();

                    win.setIPSeeker(new IPSeeker());
                    win.setDbIPLocator(new DbIPLocator(m_knowledgeConnection));
                    win.setVisible(true);
                }
            });
        }
    }

    private void showZipInfoWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            ZipInfoWin win = new ZipInfoWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ZipInfoWin win = new ZipInfoWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showIdCardInfoWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            IdCardInfoWin win = new IdCardInfoWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    IdCardInfoWin win = new IdCardInfoWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showAreaCodeInfoWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            AreaCodeInfoWin win = new AreaCodeInfoWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    AreaCodeInfoWin win = new AreaCodeInfoWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showMobileInfoWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            MobileInfoWin win = new MobileInfoWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    MobileInfoWin win = new MobileInfoWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showLocationInfoWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            LocationInfoWin win = new LocationInfoWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    LocationInfoWin win = new LocationInfoWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showEmailNoteWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailNoteWin win = new EmailNoteWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailNoteWin win = new EmailNoteWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showEmailBoxNoteWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailBoxNoteWin win = new EmailBoxNoteWin(this);

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailBoxNoteWin win = new EmailBoxNoteWin(self);

                    win.setVisible(true);
                }
            });
        }
    }

    private void showUserGuide() {
        String  fileName = ".\\用户手册.pdf";
        String  cmd      = ".\\tool\\foxit.exe \"" + fileName + "\"";
        String  s;
        Process process;

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

    private void showAboutWin() {
        if (SwingUtilities.isEventDispatchThread()) {
            EmailAboutWin win = new EmailAboutWin();

            win.setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    EmailAboutWin win = new EmailAboutWin();

                    win.setVisible(true);
                }
            });
        }
    }

    public void setDbConnection(Connection emailConn, String db, Connection knowledgeConn) {
        m_emailConnection     = emailConn;
        m_emailDbName         = db;
        m_knowledgeConnection = knowledgeConn;

        if ((emailConn != null) && (knowledgeConn != null)) {
            enableMenu(true);
        }
    }

    public Connection getEmailDbConnect() {
        return m_emailConnection;
    }

    public String getEmailDbName() {
        return m_emailDbName;
    }

    public Connection getKnowledgeDbConnect() {
        return m_knowledgeConnection;
    }

    public UserDictionary getUserDictionary() {
        return dict;
    }

    public NeosNerTool getNerTool() {
        return nerTool;
    }

    public NeosFudanTimeTool getTimeTool() {
        return timeTool;
    }

    private void showStunWin() {
        StunWin win = new StunWin();

        win.setVisible(true);
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

    private class AddEmailTask implements Runnable {
        private final List<File>    fileList;
        private final EmailDbHelper helper;

        public AddEmailTask(List<File> fileList, EmailDbHelper helper) {
            this.fileList = fileList;
            this.helper   = helper;
        }

        @Override
        public void run() {
            while (fileList.size() > 0) {
                File file;

                synchronized (fileList) {
                    file = fileList.remove(0);
                }

                addOneEmail(file, helper);
            }
        }
    }


    private class MonitorEmailTask extends TimerTask {
        private final List<File> fileList;
        private final int        initSize;

        public MonitorEmailTask(List<File> fileList) {
            this.fileList = fileList;
            initSize      = fileList.size();
        }

        @Override
        public void run() {
            int currSize = fileList.size();

            setMessage("读取邮件中……正在读取" + (initSize - currSize) + "/" + initSize + "封邮件");
            setProgress((initSize - currSize) / initSize);
        }
    }
}
