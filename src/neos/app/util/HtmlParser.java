package neos.app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlParser {
    private NodeList m_list;
    private Parser   m_parser;
    private String   m_url;

    // private int cnt=0;
    public static enum NodeType { REMARK_NODE, TAG_NODE, TEXT_NODE };
    
    public HtmlParser(String html) throws ParserException{
    	this(html,"gbk");
    }
    
    
    
    public HtmlParser(String html,  String encode)throws ParserException{
    	Node currNode;
    	m_parser=Parser.createParser(html, encode);

        try {
            m_list = new NodeList();

            for (NodeIterator ni = m_parser.elements(); ni.hasMoreNodes(); ) {
                currNode = ni.nextNode();
                m_list.add(currNode);
            }
        } catch (EncodingChangeException ece) {
            m_parser.reset();
            m_list = new NodeList();

            for (NodeIterator ni = m_parser.elements(); ni.hasMoreNodes(); ) {
                currNode = ni.nextNode();
                m_list.add(currNode);
            }
        }
    }
    
    public static HtmlParser parseUrl(String url) throws ParserException {
        return parseUrl(url, "gbk");
    }

    public static HtmlParser parseUrl(String url, String encode) throws ParserException {
        String html=getHtmlByClient(url, encode);
        return new HtmlParser(html, encode);
    }
    
    public static HtmlParser parseFile(File file) throws ParserException, IOException{
    	BufferedReader br=new BufferedReader(new FileReader(file));
    	StringBuffer sb=new StringBuffer();
    	String str;
    	while((str=br.readLine())!=null){
    		sb.append(str);
    	}
    	
    	return new HtmlParser(sb.toString());
    }
    
    private static String getHtmlByClient(String url, String encode) throws ParserException{
    	HttpClient client=new DefaultHttpClient();
    	client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    	//client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
    	HttpGet httpGet=new HttpGet(url);
    	httpGet.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    	httpGet.setHeader("Accept", "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-shockwave-flash, */*");  
        httpGet.setHeader("Accept-Language","zh-CN");  
        httpGet.setHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; QQWubi 87; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)");  
        httpGet.setHeader("Accept-Encoding","gzip, deflate");  
        //httpGet.setHeader("Host","you never be know");  
        httpGet.setHeader("Connection","Keep-Alive");
    	try {
			HttpResponse response=client.execute(httpGet);
			if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
				throw new ParserException(response.getStatusLine().getReasonPhrase());
			}
			
			HttpEntity entity=response.getEntity();
			if(entity!=null){
				InputStream input=entity.getContent();

				String enc=EntityUtils.getContentCharSet(entity);
				if(enc==null){
					enc=encode;
				}
				
				GZIPInputStream gzin=new GZIPInputStream(input);
				BufferedReader bfr=new BufferedReader(new InputStreamReader(gzin,enc));
				StringBuffer sb=new StringBuffer();
				String line=null;
				while((line=bfr.readLine())!=null){
					sb.append(line);
				}
				String html=sb.toString();
				
				input.close();
				client.getConnectionManager().shutdown();
				
				return html;
			}else{
				client.getConnectionManager().shutdown();
				return null;
			}
		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
			return null;
		} 
    }

    public String getUrl() {
        return m_url;
    }

    public Vector<Node> getNodeChildrenByType(Node node, String type) {
        String       currFullName, currName;
        int          idx;
        Node         currNode;
        NodeList     list     = node.getChildren();
        Vector<Node> children = new Vector<Node>();

        for (int i = 0; i < list.size(); i++) {
            currNode     = list.elementAt(i);
            currFullName = currNode.getClass().getName();
            idx          = currFullName.lastIndexOf(".");
            currName     = currFullName.substring(idx + 1);

            if (currName.equals(type)) {
                children.add(currNode);
            }
        }

        return children;
    }

    public void filterNodeChildrenByType(Node node, Class<? extends Node> type, boolean isIter) {
        NodeClassFilter filter = new NodeClassFilter(type);

        node.getChildren().keepAllNodesThatMatch(new NotFilter(filter), isIter);
    }

    public NodeList getNodeList() {
        return m_list;
    }

    // 根据绝对路径来取得节点
    // 路径形式为类似"Html(1)->HeadTag(1)->TitleTag(1)->TextNode(1)"
    // 若后面数字为负数，则代表倒数第几个
    public Node getNode(String pathDesc) {
        return getNode(m_list, pathDesc);
    }

    // 根据起始节点和相对路径取得节点
    public static Node getNode(Node startNode, String relPathDesc) {
        return getNode(startNode.getChildren(), relPathDesc);
    }

    // 根据起始节点列表和相对路径取得节点
    public static Node getNode(NodeList list, String relPathDesc) {
        Node         currNode = null;
        int          offset   = 0;
        int          idxA, idxB;
        NodeList     currList = list;
        String       name, currName, currFullName;
        int          count, currCount, idx;
        Vector<Node> nodeVector;

        if (list == null) {
            return null;
        }

        do {
            idxA       = relPathDesc.indexOf("(", offset);
            idxB       = relPathDesc.indexOf(")", offset);
            name       = relPathDesc.substring(offset, idxA);
            count      = Integer.valueOf(relPathDesc.substring(idxA + 1, idxB));
            offset     = idxB + 3;
            currCount  = 0;
            nodeVector = new Vector<Node>();

            if (count >= 0) {
            	if(currList==null){	//路径参数有问题
            		return null;
            	}
            	
                for (int i = 0; i < currList.size(); i++) {
                    currNode     = currList.elementAt(i);
                    currFullName = currNode.getClass().getName();
                    idx          = currFullName.lastIndexOf(".");
                    currName     = currFullName.substring(idx + 1);

                    if (currName.equals(name)) {
                        currCount++;
                        nodeVector.add(currNode);

                        if (currCount == count) {
                            currList = currNode.getChildren();

                            break;
                        }
                    }
                }

                if (count > nodeVector.size()) {    // 即路径参数有问题，大于存在的该类型节点个数
                    return null;
                }
            } else {
                for (int i = 0; i < currList.size(); i++) {
                    currNode     = currList.elementAt(i);
                    currFullName = currNode.getClass().getName();
                    idx          = currFullName.lastIndexOf(".");
                    currName     = currFullName.substring(idx + 1);

                    if (currName.equals(name)) {
                        currCount++;
                        nodeVector.add(currNode);
                    }
                }

                int nth = nodeVector.size() + count;

                if (nth < 0) {
                    return null;                    // 路径参数有问题，倒数第nth个该类型节点不存在
                }

                currNode = nodeVector.get(nth);
                currList = currNode.getChildren();
            }
        } while (offset < relPathDesc.length());

        return currNode;
    }

    public Node findNode(NodeType type, String query, int nth) {
        Vector<Node> nodeList = findNodes(m_list, type, query, true);

        if ((nth <= 0) || (nth > nodeList.size())) {
            return null;
        }

        return nodeList.elementAt(nth - 1);
    }

    public static Vector<Node> findNodes(NodeList list, NodeType type, String query, boolean isIter) {
        Vector<Node> foundNodes = new Vector<Node>();

        if (list == null) {
            return foundNodes;
        }

        for (int i = 0; i < list.size(); i++) {
            Node currNode = list.elementAt(i);

            switch (type) {
            case TEXT_NODE :
                if (currNode instanceof TextNode) {
                    TextNode textNode = (TextNode) currNode;

                    if (textNode.getText().indexOf(query) >= 0) {
                        synchronized (foundNodes) {
                            foundNodes.add(textNode);
                        }
                    }
                }

                break;

            case REMARK_NODE :
                if (currNode instanceof RemarkNode) {
                    RemarkNode remarkNode = (RemarkNode) currNode;

                    if (remarkNode.getText().indexOf(query) >= 0) {
                        synchronized (foundNodes) {
                            foundNodes.add(remarkNode);
                        }
                    }
                }

                break;

            case TAG_NODE :
                if (currNode instanceof TagNode) {
                    TagNode tagNode = (TagNode) currNode;

                    if (tagNode.getTagName().toUpperCase().equals(query.toUpperCase())) {
                        synchronized (foundNodes) {
                            foundNodes.add(tagNode);
                        }
                    }

                    if (isIter) {
                        Vector<Node> childNodes = findNodes(tagNode.getChildren(), type, query, isIter);

                        synchronized (foundNodes) {
                            foundNodes.addAll(childNodes);
                        }
                    }
                }

                break;
            }
        }

        return foundNodes;
    }

    public static String getCleanText(NodeList list) {
        if (list == null) {
            return new String();
        }

        String text = new String();

        for (int i = 0; i < list.size(); i++) {
            Node currNode = list.elementAt(i);

            if (currNode instanceof TextNode) {
                String nodeText = currNode.getText();

                if (nodeText.replaceAll("\\s+", "").length() > 0) {
                    text += currNode.getText();
                } else {
                    text += " ";
                }

                continue;
            }

            if (currNode instanceof TagNode) {
                TagNode tag     = (TagNode) currNode;
                String  tagName = tag.getTagName().toUpperCase();

                if(tagName.equals("P")){
                	text+="\r\n\r\n";
                	text+=getCleanText(tag.getChildren());
                	continue;
                }
                
                if(tagName.equals("BR")){
                	text+="\r\n\r\n";
                	continue;
                }
                
                if ((!tagName.equals("SCRIPT")) && (!tagName.equals("STYLE"))) {
                    String childText = getCleanText(tag.getChildren());

                    text += childText;
                }

                continue;
            }
        }

        return HtmlDecoder.decode(text);
    }

    public String getPageTitle() {
        Node titleNode = findNode(HtmlParser.NodeType.TAG_NODE, "Title", 1);

        if (titleNode != null) {
            return titleNode.toPlainTextString();
        } else {
            return new String();
        }
    }

    public String getPageHtml() {
        if (m_list == null) {
            return new String();
        }

        String html = new String();

        for (int i = 0; i < m_list.size(); i++) {
            html += m_list.elementAt(i).toHtml();
        }

        return html;
    }

    public String getPageText() {
        return getCleanText(m_list);
    }

    public String getBodyHtml() {
        Node bodyNode = findNode(NodeType.TAG_NODE, "Body", 1);

        if (bodyNode == null) {
            return new String();
        }

        return bodyNode.toHtml();
    }

    public String getBodyText() {
        Node bodyNode = findNode(NodeType.TAG_NODE, "Body", 1);

        if (bodyNode == null) {
            return new String();
        }

        return getCleanText(bodyNode.getChildren());
    }

    public String getContentText() {
        Node bodyNode = findNode(NodeType.TAG_NODE, "Body", 1);

        if (bodyNode == null) {
            return new String();
        }

        filterNodeChildrenByType(bodyNode, LinkTag.class, true);

        return getCleanText(bodyNode.getChildren());
    }

    public static void main(String[] args) {
        String     url = "http://news.sina.com.cn/c/2011-03-29/202722203034.shtml";
        //String url="http://www.sina.com";
        //String url="http://www.google.com/search?q=httpclient+simulate+internet+explorer&hl=en&safe=off&tbo=1&output=search&tbs=qdr:y&sa=X&ei=DRHdTJODK4bZcZnM3Y8N&ved=0CA4QpwU4FA";
    	HtmlParser p   = null;

        try {
            p = HtmlParser.parseUrl(url);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        
        /*NodeList list=p.getNodeList();
        
        Vector<Node> nodes=HtmlParser.findNodes(list, NodeType.TAG_NODE, "A", true);
        
        for(Node tag:nodes){
        	if(tag instanceof LinkTag){
        		System.out.println(((LinkTag) tag).getLinkText());
        	}
        }*/

        /*Node pnode = p.getNode("Html(1)->BodyTag(1)->Div(3)->Div(1)->Div(8)->Div(2)");

        if (pnode == null) {
            System.out.println("err!");
        } else {
            System.out.println("ok!");
        }*/

        System.out.println("Title:" + p.getPageTitle());
        System.out.println(p.getBodyText());
        System.out.println(p.getContentText());
    }
}
