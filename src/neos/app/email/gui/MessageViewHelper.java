package neos.app.email.gui;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import neos.tool.googlemap.NeosStaticMapTool;
import neos.tool.ip.DbIPLocation;
import neos.tool.ip.DbIPLocator;
import neos.tool.ip.IPSeeker;
import neos.tool.mime4j.NeosMime4JTool;
import neos.tool.mime4j.NeosRouteInfo;

import org.apache.james.mime4j.field.AddressListField;
import org.apache.james.mime4j.field.ContentTypeField;
import org.apache.james.mime4j.field.DateTimeField;
import org.apache.james.mime4j.field.UnstructuredField;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.field.address.MailboxList;
import org.apache.james.mime4j.message.BinaryBody;
import org.apache.james.mime4j.message.Body;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.Entity;
import org.apache.james.mime4j.message.Header;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.apache.james.mime4j.message.TextBody;
import org.apache.james.mime4j.parser.Field;

public class MessageViewHelper {
    private final static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private final static String redBgHtml="<span style=\"background:red\">";
    private final static String grayBgHtml="<span style=\"background:gray\">";
    private final static String bgEndHtml="</span>";

    public static String annote(String src, String text, Color color){
    	int value=(color.getRed()<<16)+(color.getGreen()<<8)+color.getBlue();
    	String rep="<span style=\"background-color: #"+Integer.toHexString(value)+"\"><big><big>"+text+"</big></big>"+bgEndHtml;
    	return src.replace(text, rep);
    }
    
    
    
    public static JTree createJTree(Message msg, TreeNodeInfoViewer viewer) {
        DefaultMutableTreeNode root = createNode(msg);
        JTree                  tree = new JTree(root);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new MessageTreeSelListener(tree,viewer));

        return tree;
    }

    private static DefaultMutableTreeNode createNode(Header header) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ObjectWrapper("Header", header));

        for (Field field : header.getFields()) {
            String name = field.getName();

            node.add(new DefaultMutableTreeNode(new ObjectWrapper(name, field)));
        }

        return node;
    }

    private static DefaultMutableTreeNode createNode(Multipart multipart) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ObjectWrapper("Multipart", multipart));

        node.add(new DefaultMutableTreeNode(new ObjectWrapper("Preamble", multipart.getPreamble())));

        for (BodyPart part : multipart.getBodyParts()) {
            node.add(createNode(part));
        }

        node.add(new DefaultMutableTreeNode(new ObjectWrapper("Epilogue", multipart.getEpilogue())));

        return node;
    }

    private static DefaultMutableTreeNode createNode(Entity entity) {

        /*
         * Create the root node for the entity. It's either a
         * Message or a Body part.
         */
        String type = "Message";

        if (entity instanceof BodyPart) {
            type = "Body part";
        }

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ObjectWrapper(type, entity));

        /*
         * Add the node encapsulating the entity Header.
         */
        node.add(createNode(entity.getHeader()));

        Body body = entity.getBody();

        if (body instanceof Multipart) {

            /*
             * The body of the entity is a Multipart.
             */
            node.add(createNode((Multipart) body));
        } else if (body instanceof Message) {

            /*
             * The body is another Message.
             */
            node.add(createNode((Message) body));
        } else {

            /*
             * Discrete Body (either of type TextBody or BinaryBody).
             */
            type = "Text body";

            if (body instanceof BinaryBody) {
                type = "Binary body";
            }

            type += " (" + entity.getMimeType() + ")";
            node.add(new DefaultMutableTreeNode(new ObjectWrapper(type, body)));
        }

        return node;
    }

    public static String getHeaderDescription(Message msg) {
        StringBuilder sb = new StringBuilder();

        sb.append(grayBgHtml+"<b>收件人：</b>"+bgEndHtml);
        sb.append(msg.getTo());
        sb.append("<br>");
        sb.append(grayBgHtml+"<b>发件人：</b>"+bgEndHtml);
        sb.append(convertToString(msg.getFrom()));
        sb.append("<br>");
        sb.append(grayBgHtml+"<b>主题：</b>"+bgEndHtml);
        sb.append(n2s(msg.getSubject()));
        sb.append("<br>");
        sb.append(grayBgHtml+"<b>抄送：</b>"+bgEndHtml);
        sb.append(n2s(msg.getCc()));
        sb.append("<br>");
        sb.append(grayBgHtml+"<b>暗送：</b>"+bgEndHtml);
        sb.append(n2s(msg.getBcc()));
        sb.append("<br>");
        sb.append(grayBgHtml+"<b>回复地址：</b>"+bgEndHtml);
        sb.append(n2s(msg.getReplyTo()));
        sb.append("<br>");
        sb.append(grayBgHtml+"<b>发送时间：</b>"+bgEndHtml);
        sb.append(fmt.format(msg.getDate()));
        sb.append("<br>");

        return sb.toString();
    }
    
    public static String getMailOrigin(Message msg, Connection conn){
    	StringBuilder sb=new StringBuilder();
    	
    	List<NeosRouteInfo> rinfos=NeosMime4JTool.getMessageRoute(msg);
    	
    	IPSeeker seeker =new IPSeeker();
    	DbIPLocator dloc=new DbIPLocator(conn);
    	
    	
    	
    	sb.append("<big><b>邮件来源追踪</b></big><br>");
    	int idx=0;
    	for(NeosRouteInfo info:rinfos){
    		NeosStaticMapTool mt=new NeosStaticMapTool();
    		sb.append("<p>");
    		
			sb.append("<b>日期时间：</b>");
			if(info.date!=null){
        		sb.append(fmt.format(info.date));
			}
			sb.append("<br>");
    		
    		
    		sb.append("<b>发送方：</b>");
    		if(info.fromAddr!=null){
    			sb.append(info.fromAddr.getHostName());
        		sb.append(" (");
        		sb.append(info.fromAddr.getHostAddress());
        		sb.append(") ");
        		sb.append(seeker.getIPLocation(info.fromAddr.getHostAddress()));
        		sb.append(" ");
        		DbIPLocation loc=dloc.locate(info.fromAddr.getHostAddress());
        		sb.append("    纬度："+loc.latitude+"  经度："+loc.longitude);
        		mt.addMark(loc.latitude, loc.longitude, "A", "red");
//        		if(idx==0){
//        			String mapUrl=mt.getUrl();
//        			String mapTitle=info.fromAddr.getHostAddress();
//        			showMapWin(mapUrl, mapTitle);
//        		}
    		}
    		sb.append("<br>");
    		
    		sb.append("<b>接收方：</b>");
    		if(info.toAddr!=null){
    			sb.append(info.toAddr.getHostName());
    			sb.append(" (");
        		sb.append(info.toAddr.getHostAddress());
        		sb.append(")");
    		}
    		sb.append("<br>");
    		
    		sb.append("</p>");
    		idx++;
    	}
    	
    	
    	return sb.toString();
    }
    
    public static void showMailOrigMapWin(Message msg, Connection conn){
    	List<NeosRouteInfo> rinfos=NeosMime4JTool.getMessageRoute(msg);
    	
    	IPSeeker seeker =new IPSeeker();
    	DbIPLocator dloc=new DbIPLocator(conn);
    	
    	if(rinfos.size()<=0){
    		return;
    	}
    	
    	NeosRouteInfo info=rinfos.get(0);
    	if(info.fromAddr==null){
    		return;
    	}
    	
    	DbIPLocation loc=dloc.locate(info.fromAddr.getHostAddress());
    	NeosStaticMapTool mt=new NeosStaticMapTool();
    	mt.addMark(loc.latitude, loc.longitude, "A", "red");
    	
    	final String url=mt.getUrl();
    	final String title=info.fromAddr.getHostAddress();
    	try{
			new Thread(){
				public void run(){
					if(SwingUtilities.isEventDispatchThread()){
						MapWin mw=new MapWin();
	            		mw.setMapUrl(url);
	            		mw.setTitle(title);
	            		mw.setVisible(true);
					}else{
						SwingUtilities.invokeLater(new Runnable(){
							public void run(){
								MapWin mw=new MapWin();
			            		mw.setMapUrl(url);
			            		mw.setTitle(title);
			            		mw.setVisible(true);
							}
						});
					}
					
				}
			}.start();
		}catch(Exception e){
			e.printStackTrace();
		}
    }

    public static String convertToString(MailboxList list) {
        if (list != null) {
            StringBuilder sb = new StringBuilder();

            sb.append('[');

            for (Mailbox box : list) {
                sb.append(convertToString(box));
                sb.append(" ");
            }

            sb.append(']');

            return sb.toString();
        } else {
            return null;
        }
    }

    public static String convertToString(Mailbox box) {
        if (box != null) {
            StringBuilder sb = new StringBuilder();

            if (box.getName() != null) {
                sb.append("<");
                sb.append(box.getName());
                sb.append("> ");
            }

            if (box.getAddress() != null) {
                sb.append(box.getAddress());
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    public static String n2s(Object obj) {
        if (obj == null) {
            return " ";
        }

        String str = obj.toString();

        return (str == null)
               ? " "
               : str;
    }

    private static class MessageTreeSelListener implements TreeSelectionListener {
        private final JTree              m_tree;
        private final TreeNodeInfoViewer m_viewer;

        public MessageTreeSelListener(JTree tree, TreeNodeInfoViewer viewer) {
            m_tree   = tree;
            m_viewer = viewer;
        }

        @Override
        public void valueChanged(TreeSelectionEvent arg0) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();

            m_viewer.setTreeNodeInfo("");

            if (node == null) {
                return;
            }

            Object o = ((ObjectWrapper) node.getUserObject()).getObject();

            if (node.isLeaf()) {
                if (o instanceof TextBody) {

                    /*
                     * A text body. Display its contents.
                     */
                    TextBody      body = (TextBody) o;
                    StringBuilder sb   = new StringBuilder();

                    try {
                        Reader r = body.getReader();
                        int    c;

                        while ((c = r.read()) != -1) {
                            sb.append((char) c);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    m_viewer.setTreeNodeInfo(sb.toString());
                } else if (o instanceof BinaryBody) {

                    /*
                     * A binary body. Display its MIME type and length in bytes.
                     */
                    BinaryBody body = (BinaryBody) o;
                    int        size = 0;

                    try {
                        InputStream is = body.getInputStream();

                        while ((is.read()) != -1) {
                            size++;
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    m_viewer.setTreeNodeInfo("Binary body\n" + "MIME type: " + body.getParent().getMimeType() + "\n"
                                             + "Size of decoded data: " + size + " bytes");
                } else if (o instanceof ContentTypeField) {

                    /*
                     * Content-Type field.
                     */
                    ContentTypeField field = (ContentTypeField) o;
                    StringBuilder    sb    = new StringBuilder();

                    sb.append("MIME type: " + field.getMimeType() + "\n");

                    Map<String, String> params = field.getParameters();

                    for (String name : params.keySet()) {
                        sb.append(name + " = " + params.get(name) + "\n");
                    }

                    m_viewer.setTreeNodeInfo(sb.toString());
                } else if (o instanceof AddressListField) {

                    /*
                     * An address field (From, To, Cc, etc)
                     */
                    AddressListField field = (AddressListField) o;
                    MailboxList      list  = field.getAddressList().flatten();
                    StringBuilder    sb    = new StringBuilder();

                    for (int i = 0; i < list.size(); i++) {
                        Mailbox mb = list.get(i);

                        sb.append(mb.getDisplayString() + "\n");
                    }

                    m_viewer.setTreeNodeInfo(sb.toString());
                } else if (o instanceof DateTimeField) {
                    Date date = ((DateTimeField) o).getDate();

                    m_viewer.setTreeNodeInfo(fmt.format(date));
                } else if (o instanceof UnstructuredField) {
                    m_viewer.setTreeNodeInfo(((UnstructuredField) o).getValue());
                } else if (o instanceof Field) {
                    m_viewer.setTreeNodeInfo(((Field) o).getBody());
                } else {

                    /*
                     * The Object should be a Header or a String containing a
                     * Preamble or Epilogue.
                     */
                    m_viewer.setTreeNodeInfo(o.toString());
                }
            }
        }
    }


    public static class ObjectWrapper {
        private Object object = null;
        private String text   = "";

        public ObjectWrapper(String text, Object object) {
            this.text   = text;
            this.object = object;
        }

        @Override
        public String toString() {
            return text;
        }

        public Object getObject() {
            return object;
        }
    }
    
    
}