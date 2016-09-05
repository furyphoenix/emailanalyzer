package neos.tool.htmlparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import neos.util.log.Loggable;
import neos.util.log.NeosLogger;
import neos.util.log.NeosStdLogger;

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
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class NeosHtmlParser implements Loggable{
	public static enum NodeType { REMARK_NODE, TAG_NODE, TEXT_NODE }
	private final static String DEFAULT_ENCODE="GBK";
	
	private NodeList m_list;
	private Parser m_parser;
	
	private NeosLogger logger=NeosStdLogger.getInstance();
	
	
	public NeosHtmlParser(String html){
		
	}
	
	public static NeosHtmlParser getParserByUrl(String url){
		return getParserByUrl(url, DEFAULT_ENCODE);
	}
	
	public static NeosHtmlParser getParserByUrl(String url, String encode){
		return null;
	}
	
	public static String getHtmlByUrl(String url, String encode){
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
				//throw new ParserException(response.getStatusLine().getReasonPhrase());
				//logger.
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
	
	public static String getHtmlByUrl(String url){
		return getHtmlByUrl(url, DEFAULT_ENCODE);
	}

	@Override
	public void setLogger(NeosLogger logger) {
		this.logger=logger;
		
	}
}
