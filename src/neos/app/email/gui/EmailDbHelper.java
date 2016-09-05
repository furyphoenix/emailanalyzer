package neos.app.email.gui;

//~--- non-JDK imports --------------------------------------------------------

import neos.app.util.HtmlParser;
import neos.component.ner.NeosNamedEntity;
import neos.component.ner.NeosNerTool;

import neos.lang.zh.NeosChineseConverter;
import neos.tool.mime4j.NeosMime4JTool;
import neos.tool.tika.NeosTikaTool;
import neos.tool.unpack.Neos7ZipTool;
import neos.tool.unpack.NeosUnpackTool;
import neos.tool.unpack.NeosUnpackWrongPasswordException;
import neos.util.MD5Util;

import org.apache.james.mime4j.field.address.Address;
import org.apache.james.mime4j.field.address.AddressList;
import org.apache.james.mime4j.field.address.Group;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.apache.poi.poifs.property.Parent;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmailDbHelper {
    
    private final static String           EMAIL_ATTACHMENT_TAB = "`emailattach` ";
    private final static String           EMAIL_CONTENT_TAB    = "`emailcontent` ";
    private final static String			  EMAIL_ENTITY_TAB=" `emailentity` ";
    private final static String           EMAIL_FROM_TO_TAB    = "`emailfromto` ";
    private final static String           EMAIL_HEADER_TAB     = "`emailheader` ";
    
    private final static String           attachmentStorePath  = "attachments";
    private final static SimpleDateFormat fmt                  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String           idxAttachPath        = ".\\index\\attach\\";
    private final static String           idxContentPath       = ".\\index\\content\\";
    private final static String[]         toAddrType           = { "TO_TYPE", "CC_TYPE", "BCC_TYPE" };
    private final static NeosUnpackTool   unpacker             = new Neos7ZipTool();
    private MessageContainer encyptContainer;
    private final static SimpleDateFormat dateFmt=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);

    static {}

    private IndexWriter             aiw;
    //private AsynchronousIndexWriter aiw_asyn;
    private PerFieldAnalyzerWrapper analyzer;
    private Connection              conn;
    private String                  emailDbName;
    private IndexWriter             iw;
    //private AsynchronousIndexWriter iw_asyn;
    private NeosNerTool ntool;

    public EmailDbHelper(String dbName, Connection conn, NeosNerTool tool) {
        this.conn        = conn;
        this.emailDbName = dbName;
        this.ntool=tool;
        
        Map<String, Analyzer> map=new HashMap<String, Analyzer>();
    	map.put("Date", new KeywordAnalyzer());
    	map.put("From", new WhitespaceAnalyzer(Version.LUCENE_30));
    	map.put("To", new WhitespaceAnalyzer(Version.LUCENE_30));
    	map.put("CC", new WhitespaceAnalyzer(Version.LUCENE_30));
    	map.put("BCC", new WhitespaceAnalyzer(Version.LUCENE_30));
        
        analyzer         = new PerFieldAnalyzerWrapper(new SmartChineseAnalyzer(Version.LUCENE_30), map);
        try {
            FSDirectory emailFS = FSDirectory.open(new File(idxContentPath+dbName));
            
            IndexWriterConfig iwcc=new IndexWriterConfig(Version.LUCENE_30, analyzer);
            iwcc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            iw=new IndexWriter(emailFS, iwcc);
            //iw = new IndexWriter(emailFS, analyzer, !IndexReader.indexExists(emailFS), MaxFieldLength.LIMITED);
            //iw_asyn=new AsynchronousIndexWriter(iw);

            FSDirectory attachFS = FSDirectory.open(new File(idxAttachPath+dbName));
            IndexWriterConfig iwca=new IndexWriterConfig(Version.LUCENE_30, analyzer);
            iwca.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            aiw=new IndexWriter(attachFS, iwca);
            //aiw = new IndexWriter(attachFS, analyzer, !IndexReader.indexExists(attachFS), MaxFieldLength.LIMITED);
            //aiw_asyn=new AsynchronousIndexWriter(aiw);
        } catch (CorruptIndexException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (LockObtainFailedException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private String getFromAddress(Message msg){
    	String       from=null;
    	
    	try{
    		from = sqlEncode(msg.getFrom().get(0).getAddress());
    	}catch(Exception ee){
    		String fromField=msg.getHeader().getField("From").getBody();
    		int idx1=fromField.indexOf("<");
    		int idx2=fromField.indexOf(">");
    		if((idx1>=0)&&(idx2>idx1)){
    			from =sqlEncode(fromField.substring(idx1+1, idx2));
    		}
    	}
    	
    	return from;
    	
    }
    
    private String getSendDate(Message msg, NeosMime4JTool tool){
    	String date=null;
    	//NeosMime4JTool tool  = new NeosMime4JTool(msg);
    	try{
    		Date sendDate=msg.getDate();
    		date=fmt.format(sendDate);
    	}catch(Exception e){
    		Date sd=tool.getSendDate();
    		if(sd!=null){
    			date=fmt.format(sd);
    		}
    		
    	}
    	
    	if(date==null){
    		String dateDesc=msg.getHeader().getField("Date").getBody();
    		try {
				date=fmt.format(dateFmt.parse(dateDesc));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	return date;
    }

    public boolean isDuplicate(Message msg, NeosMime4JTool tool) {
        try {
        	String       from=getFromAddress(msg);
        	String date=getSendDate(msg, tool);
            
        	
            StringBuffer sb   = new StringBuffer();

            sb.append("Select Count(*) From `");
            sb.append(emailDbName);
            sb.append("`.");
            sb.append(EMAIL_HEADER_TAB);
            sb.append(" Where `FromAddr`='");
            sb.append(from);
            sb.append("' AND `SendDate`='");
            sb.append(date);
            sb.append("'");

            String    sql = sb.toString();
            Statement st  = conn.createStatement();
            ResultSet rs  = st.executeQuery(sql);

            while (rs.next()) {
                int cnt = rs.getInt(1);

                if (cnt == 0) {
                    st.close();

                    return false;
                } else {
                    st.close();

                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int addMessage(Message msg, NeosMime4JTool tool) {
    	if(msg==null){
    		System.out.println("Message is null!");
    		return -1;
    	}
    	
        if (isDuplicate(msg, tool)) {
            return -1;
        }

        try {
            Document doc = new Document();
            int      id  = addMessageToHeaderTab(msg, doc, tool);

            if (id >= 0) {
            	System.out.println("add #"+id+" to FromTo Tab");
                addMessageToFromToTab(id, msg, doc, tool);
                System.out.println("add #"+id+" to Content Tab");
                addMessageToContentTab(id, msg, doc, tool);
                System.out.println("add #"+id+" to Attach Tab");
                storeAttachments(id, msg);
                iw.addDocument(doc);
                iw.commit();
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
    
    
    public void setEncyptAttachmentMessageContainer(MessageContainer container){
    	encyptContainer=container;
    }

    public void writeIndex() {
        try {
            iw.close();
            aiw.close();
        } catch (CorruptIndexException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private int addMessageToHeaderTab(Message msg, Document doc, NeosMime4JTool tool) {

        // δ���?��FROM�ֶε��ʼ�
        try {
        	String       from=getFromAddress(msg);
        	String dateStr=getSendDate(msg, tool);
        	String date=null;
        	if(dateStr!=null){
        		date    = DateTools.dateToString(fmt.parse(dateStr), DateTools.Resolution.SECOND);
        	}
            String subject = NeosChineseConverter.tc2sc(msg.getSubject());

            if (subject.length() >= 255) {
                subject = subject.substring(0, 254);
            }

            subject = sqlEncode(subject);

            StringBuffer sb        = new StringBuffer();
            Field        fromField = new Field("From", getFromAddress(msg), Field.Store.YES,
                                               Field.Index.ANALYZED);
            if(date!=null){
            	Field dateField    = new Field("Date", date, Field.Store.YES, Field.Index.ANALYZED);
            	doc.add(dateField);
            }
            
            Field subjectField = new Field("Subject", subject, Field.Store.YES, Field.Index.ANALYZED);

            doc.add(fromField);
            
            doc.add(subjectField);
            sb.append("Insert Into `");
            sb.append(this.emailDbName);
            sb.append("`.");
            sb.append(EMAIL_HEADER_TAB);
            sb.append(" (`FromAddr`, `SendDate`, `MailSubject`) Values ('");
            sb.append(from);
            sb.append("', '");
            sb.append(getSendDate(msg, tool));
            sb.append("','");
            sb.append(subject);
            sb.append("')");

            String            sql = sb.toString();
            PreparedStatement ps  = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            while (rs.next()) {
                return rs.getInt(1);
            }

            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("subject: "+msg.getSubject());

            return -1;
        }
    }

    private void addMessageToFromToTab(int id, Message msg, Document doc, NeosMime4JTool tool) {
        try {
        	String       from=getFromAddress(msg);
            String        date    = DateTools.dateToString(fmt.parse(getSendDate(msg, tool)), DateTools.Resolution.SECOND);
            AddressList   toList  = msg.getTo();
            AddressList   ccList  = msg.getCc();
            AddressList   bccList = msg.getBcc();
            AddressList[] lst     = { toList, ccList, bccList };
            String        to;

            for (int i = 0; i < lst.length; i++) {
                AddressList  addrList = lst[i];
                StringBuffer lstSb    = new StringBuffer();

                if ((addrList != null) && (addrList.size() > 0)) {
                    List<Mailbox> mailboxList = new ArrayList<Mailbox>();

                    for (Address addr : addrList) {
                        if (addr instanceof Mailbox) {
                            mailboxList.add((Mailbox) addr);
                        } else if (addr instanceof Group) {
                            mailboxList.addAll(((Group) addr).getMailboxes());
                        }
                    }

                    for (int j = 0; j < mailboxList.size(); j++) {
                        to = sqlEncode(mailboxList.get(j).getAddress());
                        lstSb.append(to);
                        lstSb.append(" ");

                        StringBuffer sb = new StringBuffer();

                        sb.append("Insert Into `");
                        sb.append(this.emailDbName);
                        sb.append("`.");
                        sb.append(EMAIL_FROM_TO_TAB);
                        sb.append(" (`EmailID`, `FromAddr`, `SendDate`, `ToAddr`, `AddrType`) Values (");
                        sb.append(id);
                        sb.append(", '");
                        sb.append(from);
                        sb.append("','");
                        sb.append(getSendDate(msg, tool));
                        sb.append("','");
                        sb.append(to);
                        sb.append("','");
                        sb.append(toAddrType[i]);
                        sb.append("')");

                        String    sql = sb.toString();
                        Statement st  = conn.createStatement();

                        st.executeUpdate(sql);
                        st.close();
                    }

                    mailboxList.clear();
                }

                if (lstSb.length() == 0) {
                    continue;
                }

                switch (i) {
                case 0 :
                    Field toField = new Field("To", lstSb.toString(), Field.Store.YES, Field.Index.ANALYZED);

                    doc.add(toField);

                    break;

                case 1 :
                    Field ccField = new Field("CC", lstSb.toString(), Field.Store.YES, Field.Index.ANALYZED);

                    doc.add(ccField);

                    break;

                case 2 :
                    Field bccField = new Field("BCC", lstSb.toString(), Field.Store.YES, Field.Index.ANALYZED);

                    doc.add(bccField);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String addrList2Str(AddressList addrList) {
        if ((addrList != null) && (addrList.size() > 0)) {
            List<Mailbox> mailboxList = new ArrayList<Mailbox>();

            for (Address addr : addrList) {
                if (addr instanceof Mailbox) {
                    mailboxList.add((Mailbox) addr);
                } else if (addr instanceof Group) {
                    mailboxList.addAll(((Group) addr).getMailboxes());
                }
            }

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < mailboxList.size(); j++) {
                String box = mailboxList.get(j).getAddress();

                sb.append(box);
                sb.append(" ");
            }

            return sb.toString();
        }

        return "";
    }

    private void addMessageToContentTab(int id, Message msg, Document doc, NeosMime4JTool tool) {
        try {
        	String       from=getFromAddress(msg);
            String         date  = getSendDate(msg, tool);
            //NeosMime4JTool tool  = new NeosMime4JTool(msg);
            String         plain = tool.getTextPlainContent();
            String         plainMail;

            if (plain != null) {
                plainMail = sqlEncode(tool.getTextPlainContent());
            } else {
                plainMail = "";
            }

            String html = tool.getTextHtmlContent();
            String text = "";
            String htmlMail;
            String textMail;

            if ((html != null) && (html.length() > 0)) {
                htmlMail = sqlEncode(html);

                HtmlParser parser = new HtmlParser(html);

                text     = parser.getBodyText();
                textMail = sqlEncode(text);
            } else {
                htmlMail = "";
                textMail = "";
            }

            String ip = tool.getSenderIP();

            // String sourceCode=sqlEncode(tool.getMessageSourceCode());
            StringBuffer sb = new StringBuffer();

            sb.append("Insert Into `");
            sb.append(this.emailDbName);
            sb.append("`.");
            sb.append(EMAIL_CONTENT_TAB);
            sb.append("(`EmailID`, `FromAddr`, `SendDate`, `IP`, `PlainMail`, `HtmlMail`, `TextMail`) Values (");
            sb.append(id);
            sb.append(", '");
            sb.append(from);
            sb.append("','");
            sb.append(getSendDate(msg, tool));
            sb.append("','");
            sb.append(ip);
            sb.append("','");
            sb.append(plainMail);
            sb.append("','");
            sb.append(htmlMail);
            sb.append("','");
            sb.append(textMail);
            sb.append("')");

            String    sql = sb.toString();
            Statement st  = conn.createStatement();

            st.executeUpdate(sql);
            st.close();

            Field emailIdField    = new Field("EmailID", id + "", Field.Store.YES, Field.Index.NOT_ANALYZED);
            Field emailPlainField = new Field("PlainMail", plain, Field.Store.YES, Field.Index.ANALYZED);
            Field emailTextField  = new Field("TextMail", text, Field.Store.YES, Field.Index.ANALYZED);

            doc.add(emailIdField);
            doc.add(emailPlainField);
            doc.add(emailTextField);
            
            //�洢����ʵ����Ϣ
            String content=null;
            if(textMail.length()>plainMail.length()){
            	content=textMail;
            }else{
            	content=plainMail;
            }
            HashMap<String, NeosNamedEntity.NamedEntityType> emap=ntool.locate(content);
            for(String entity:emap.keySet()){
            	int offset=0;
            	int idx=-1;
            	NeosNamedEntity.NamedEntityType type=emap.get(entity);
            	while((idx=content.indexOf(entity, offset))>=0){
            		storeEntity(id, entity, type, idx);
            		offset=idx+entity.length();
            	}
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void storeEntity(int id, String entity, NeosNamedEntity.NamedEntityType type, int offset){
    	StringBuffer sb=new StringBuffer();
    	sb.append("Insert Into `");
    	sb.append(this.emailDbName);
        sb.append("`.");
        sb.append(this.EMAIL_ENTITY_TAB);
        sb.append("(`EmailId`, `Entity`, `EntityType`, `Offset`) Values (");
        sb.append(id);
        sb.append(", '");
        sb.append(sqlEncode(entity));
        sb.append("', '");
        sb.append(type.toString());
        sb.append("', ");
        sb.append(offset);
        sb.append(")");
        
        String    sql = sb.toString();
        
        try{
        	Statement st  = conn.createStatement();

            st.executeUpdate(sql);
            st.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
    }

    private void storeAttachments(int id, Message msg) {
        try {
            NeosMime4JTool tool = new NeosMime4JTool(msg);
            String         path = ".\\attachments\\" + emailDbName + "\\" + id;

            tool.saveAttachmentsToPath(path);

            File dir = new File(path);

            try {
                unpacker.unpack(dir, dir, true, true);
            } catch (NeosUnpackWrongPasswordException wpe) {
                System.out.println("Find Encypted Attachment in Email: " + getFromAddress(msg) + "\t\"" + msg.getSubject()
                                   + "\"");
                if(encyptContainer!=null){
                	encyptContainer.addMessage(id, msg);
                }
            }

            indexAttachments(id, msg, tool);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indexAttachments(int id, Message msg, NeosMime4JTool tool) {
        File dir = new File(".\\attachments\\" + emailDbName + "\\" + id);

        if (!dir.exists()) {
            return;
        }

        NeosTikaTool     tikaTool = new NeosTikaTool(new AutoDetectParser());
        Metadata         metadata = new Metadata();
        MimeEntityConfig config   = new MimeEntityConfig();

        config.setMaximalBodyDescriptor(true);

        List<File> fileList = getSupportFileList(dir);
        String     content;

        for (File file : fileList) {
            try {
                String filename = file.getAbsolutePath();

                if (filename.toLowerCase().endsWith(".eml") || filename.toLowerCase().endsWith(".msg")) {
                    Message        msgAttach = new Message(new FileInputStream(file), config);
                    NeosMime4JTool mimeTool  = new NeosMime4JTool(msgAttach);

                    content = mimeTool.getTextHtmlContent();
                } else if (filename.toLowerCase().endsWith(".htm") || filename.toLowerCase().endsWith(".html")) {
                    HtmlParser parser = HtmlParser.parseFile(file);

                    content = parser.getPageText();
                } else {
                    TikaInputStream stream = TikaInputStream.get(file, metadata);

                    tikaTool.importStream(stream, metadata);
                    content = tikaTool.getText();
                }
                content=NeosChineseConverter.tc2sc(content);

                Document doc            = new Document();
                Field    idField        = new Field("EmailID", id + "", Field.Store.YES, Field.Index.NOT_ANALYZED);
                Field    emailFromField = new Field("From", getFromAddress(msg), Field.Store.YES,
                                              Field.Index.ANALYZED);
                Field emailToField  = new Field("To", addrList2Str(msg.getTo()), Field.Store.YES, Field.Index.ANALYZED);
                Field emailCcField  = new Field("CC", addrList2Str(msg.getCc()), Field.Store.YES, Field.Index.ANALYZED);
                Field emailBccField = new Field("BCC", addrList2Str(msg.getBcc()), Field.Store.YES,
                                                Field.Index.ANALYZED);
                String dateStr           = DateTools.dateToString(fmt.parse(getSendDate(msg, tool)), DateTools.Resolution.SECOND);
                Field  emailDateField    = new Field("Date", dateStr, Field.Store.YES, Field.Index.ANALYZED);
                Field  emailSubjectField = new Field("Subject", msg.getSubject(), Field.Store.YES,
                                               Field.Index.NOT_ANALYZED);
                Field fileNameField = new Field("FileName", filename, Field.Store.YES, Field.Index.NOT_ANALYZED);
                Field contentField  = new Field("Content", content, Field.Store.YES, Field.Index.ANALYZED);

                doc.add(idField);
                doc.add(emailFromField);
                doc.add(emailToField);
                doc.add(emailCcField);
                doc.add(emailBccField);
                doc.add(emailDateField);
                doc.add(emailSubjectField);
                doc.add(fileNameField);
                doc.add(contentField);
                aiw.addDocument(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        storeEmailAttachmentInfo(id, dir);
    }

    private boolean isSupportIndex(File file) {
        String[] exts = {
            "txt", "html", "htm", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "eml", "msg", "odt", "pps", "ppsx"
        };

        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            return false;
        }

        String filename = file.getName().toLowerCase();

        for (int i = 0; i < exts.length; i++) {
            if (filename.endsWith("." + exts[i])) {
                return true;
            }
        }

        return false;
    }

    private List<File> getSupportFileList(File dir) {
        List<File> lst = new ArrayList<File>();

        if (!dir.isDirectory()) {
            return lst;
        }

        File[] children = dir.listFiles();

        for (int i = 0; i < children.length; i++) {
            if (isSupportIndex(children[i])) {
                lst.add(children[i]);
            } else {
                if (children[i].isDirectory()) {
                    lst.addAll(getSupportFileList(children[i]));
                }
            }
        }

        return lst;
    }
    
    private void storeEmailAttachmentInfo(int id, File dir){
    	if(dir.isDirectory()){
    		File[] children = dir.listFiles();
    		for(File file:children){
    			storeEmailAttachmentInfo(id, file);
    		}
    	}else{
    		try{
    			String fileName=dir.getName();
        		String filePath=dir.getAbsolutePath();
        		long fileLen=dir.length();
        		String md5=MD5Util.getFileMD5String(dir);
        		
        		StringBuffer sb=new StringBuffer();
        		sb.append("Insert Into `");
                sb.append(this.emailDbName);
                sb.append("`.");
                sb.append(this.EMAIL_ATTACHMENT_TAB);
                sb.append(" (`EmailId`, `FileName`, `StorePath`, `FileLen`, `FileMD5`) Values (");
                sb.append(id);
                sb.append(",'");
                sb.append(sqlEncode(fileName));
                sb.append("', '");
                sb.append(sqlEncode(filePath));
                sb.append("', ");
                sb.append(fileLen);
                sb.append(", '");
                sb.append(md5);
                sb.append("')");
                
                String    sql = sb.toString();
                Statement st  = conn.createStatement();

                st.executeUpdate(sql);
                st.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		
    	}
    }
    
    

    private static String sqlEncode(String text) {
        return text.replace("\\", "\\\\").replace("'", "\\'");
    }
    
    
}
