package neos.tool.mime4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import neos.app.util.HtmlParser;
import neos.lang.zh.NeosChineseConverter;

import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.field.UnstructuredField;
import org.apache.james.mime4j.message.BinaryBody;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.Entity;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.apache.james.mime4j.message.TextBody;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.parser.MimeEntityConfig;

public class NeosMime4JTool {
	private final StringBuffer txtBody;
	private final StringBuffer htmlBody;
	private final List<BodyPart> attachments;
	private final Message msg;
	//private final String sourceCode;
	private final static String BASE64="BASE64";
	
	public NeosMime4JTool(Message msg){
		this.msg=msg;
		/*ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			msg.writeTo(baos);
		} catch (IOException e) {
			
			e.printStackTrace();
		}*/
		//sourceCode=baos.toString();
		
		txtBody=new StringBuffer();
		htmlBody=new StringBuffer();
		attachments=new ArrayList<BodyPart> ();
		parse();
	}
	
	/*public String getMessageSourceCode(){
		return sourceCode;
	}*/
	
	public String getTextPlainContent(){
		return NeosChineseConverter.tc2sc(txtBody.toString());
	}
	
	public String getTextHtmlContent(){
		return NeosChineseConverter.tc2sc(htmlBody.toString());
	}
	
	public String getTextHtmlText(){
		HtmlParser parser=null;
		try{
			parser=new HtmlParser(htmlBody.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(parser!=null){
			return parser.getPageText();
		}else{
			return "";
		}
	}
	
	public List<String> getAttachmentFileNames(){
		List<String> files=new ArrayList<String> ();
		
		for(BodyPart part:attachments){
			//System.out.println(part.getCharset());
			//System.out.println(part.getContentTransferEncoding());
			if(part.getFilename()!=null){
				String fileName=DecoderUtil.decodeEncodedWords(part.getFilename());
				files.add(fileName);
			}
		}
		
		return files;
	}
	
	public void saveAttachmentsToPath(String path) throws IOException{
		for(BodyPart part:attachments){
			if(part.getFilename()!=null){
				String attName=DecoderUtil.decodeEncodedWords(part.getFilename());
				File file=new File(path, attName);
				file.getParentFile().mkdirs();
				FileOutputStream fos=new FileOutputStream(file);
				try{
					BinaryBody bb = (BinaryBody) part.getBody();
					bb.writeTo(fos);
				}finally{
					fos.close();
				}
			}
		}
	}
	
	public void saveAttachmentAs(int n, File file) throws IOException{
		BodyPart part=attachments.get(n);
		FileOutputStream fos=new FileOutputStream(file);
		try{
			BinaryBody bb = (BinaryBody) part.getBody();
			bb.writeTo(fos);
		}finally{
			fos.close();
		}
	}
	
	public void saveAttachmentAs(String attachmentName, File file) throws IOException{
		for(BodyPart part:attachments){
			String attName=DecoderUtil.decodeEncodedWords(part.getFilename());
			if(attName.equals(attachmentName)){
				FileOutputStream fos=new FileOutputStream(file);
				try{
					BinaryBody bb = (BinaryBody) part.getBody();
					bb.writeTo(fos);
				}finally{
					fos.close();
				}
			}
		}
	}
	
	private void parse(){
		try{
			if(msg.isMultipart()){
				Multipart multipart=(Multipart)msg.getBody();
				parseBodyParts(multipart);
			}else{
				String txt=getTxtPart(msg);
				String mtype=msg.getMimeType();
				if(mtype.equals("text/plain")){
					txtBody.append(txt);
				}else if(mtype.equals("text/html")){
					htmlBody.append(txt);
				}else {
					//maybe attachment
				}
				
			}
		}catch(IOException ie){
			ie.printStackTrace();
		}
	}
	
	private void parseBodyParts(Multipart multipart) throws IOException{
		for (BodyPart part : multipart.getBodyParts()) {
            if (part.isMimeType("text/plain")) {
                String txt = getTxtPart(part);
                txtBody.append(txt);
            } else if (part.isMimeType("text/html")) {
                String html = getTxtPart(part);
                htmlBody.append(html);
            } else if (part.getDispositionType() != null && !part.getDispositionType().equals("")) {
                //If DispositionType is null or empty, it means that it's multipart, not attached file
                attachments.add(part);
            }
            //If current part contains other, parse it again by recursion
            if (part.isMultipart()) {
                parseBodyParts((Multipart) part.getBody());
            }
        }
	}
	
	private String getTxtPart(Entity part) throws IOException {
        //Get content from body
        TextBody tb = (TextBody) part.getBody();
        String charset=tb.getMimeCharset();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tb.writeTo(baos);
        return new String(baos.toByteArray(),charset);
    }
	
	
	public static List<NeosRouteInfo> getMessageRoute(Message msg){
		List<NeosRouteInfo> lst=new ArrayList<NeosRouteInfo> ();
		
		List<Field> recvFields=msg.getHeader().getFields(ReceiveRouteField.FieldName);
		for(int i=recvFields.size()-1; i>=0; i--){
			ReceiveRouteField field= new ReceiveRouteField((UnstructuredField)recvFields.get(i));
			NeosRouteInfo info=new NeosRouteInfo(field.getFromAddress(), field.getByAddress(), field.getDateTime());
			lst.add(info);
		}
		
		return lst;
	}
	
	public String getSenderIP(){
		List<NeosRouteInfo> infos=getMessageRoute(msg);
		if(infos.size()>0){
			InetAddress addr=infos.get(0).fromAddr;
			if(addr==null){
				return "";
			}
			String host=addr.getHostAddress();
			return host;
		}
		return "";
	}
	
	public Date getSendDate(){
		List<NeosRouteInfo> infos=getMessageRoute(msg);
		if(infos.size()>0){
			return infos.get(0).date;
		}
		
		return null;
	}
	
	
	public static void main(String[] args){
		try{
			MimeEntityConfig config=new MimeEntityConfig();
			config.setMaximalBodyDescriptor(true);
			Message msg=new Message(new FileInputStream(".\\email\\5.eml"), config);
			NeosMime4JTool tool=new NeosMime4JTool(msg);
			List<String> attachments=tool.getAttachmentFileNames();
			for(String file:attachments){
				System.out.println(file);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
