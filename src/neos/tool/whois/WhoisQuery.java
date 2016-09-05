package neos.tool.whois;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.InetAddress;
import java.net.Socket;

public class WhoisQuery {
    //public final static String CN_HOST      = "whois.cnnic.net.cn";
    //public final static String COM_HOST     = "whois.apnic.net";
    public final static int    DEFAULT_PORT = 43;
    
    /** 互联网信息中心 */
	public final static String DEFAULT_HOST = "whois.internic.net";
	
	/** 亚洲与太平洋地区网络信息中心(澳大利亚墨尔本) */
	public final static String APNIC_HOST   = "whois.apnic.net";
	
	/** 中国教育与科研计算机网网络信息中心(清华大学·中国北京) */
    public final static String CERNIC_HOST  = "whois.edu.cn";
    
    
    /** 中国互联网络信息中心(中国科学院计算技术研究所·中国北京) */
    public final static String CNNIC_HOST   = "whois.cnnic.net.cn";
    
    /** 台湾互联网络信息中心(中国台湾台北) */
    public final static String TWNIC_HOST   = "whois.twnic.net";
    
    /** 日本互联网络信息中心(日本东京) */
    public final static String JPNIC_HOST ="whois.nic.ad.jp";
    
    /** 韩国互联网络信息中心(韩国汉城) */
    public final static String KRNIC_HOST="whois.krnic.net";
    
    /** 欧州IP地址注册中心(荷兰阿姆斯特丹) */
    public final static String RIPE_HOST="whois.ripe.net";
    
    /** 拉丁美洲及加勒比互联网络信息中心(巴西圣保罗) */
    public final static String LACNIC_HOST="whois.lacnic.net";
    
    /** 美国Internet号码注册中心(美国弗吉尼亚州Chantilly市) */
    public final static String ARIN_HOST="whois.arin.net";
    
    /** 非洲互联网络信息中心(Cyber City, Ebène, Mauritius) */
    public final static String AFRINIC_HOST="www.afrinic.net";

    public WhoisQuery() {

        //
    }

    /**
     * 根据所给域名获取whois信息
     * @param name
     * @return
     */
    public SiteInfoBean getSiteInfo(String name) {
        SiteInfoBean siteInfo = new SiteInfoBean();
        InetAddress  server;
        int          port = DEFAULT_PORT;
        String       str  = "";
        String       type = getDomainType(name);
        String       host = "";

        try {
            if (type.equals("com")) {
                host = DEFAULT_HOST;
            } else if (type.equals("cn")) {
                host = CNNIC_HOST;
            } else {
                host = DEFAULT_HOST;
            }

            server = InetAddress.getByName(host);

            Socket theSocket = new Socket(server, port);
            Writer out       = new OutputStreamWriter(theSocket.getOutputStream(), "UTF-8");

            out.write(name + " " + "\r\n");
            out.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(theSocket.getInputStream(), "UTF-8"));

            while ((str = br.readLine()) != null) {
                setSiteInfo(siteInfo, str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return siteInfo;
    }

    /**
     * 根据所给域名判断是.com还是.cn
     * 不同类型域名查询系统不一样
     * @param name
     * @return
     */
    public String getDomainType(String name) {
        String type = "";

        if (name.endsWith("com")) {
            type = "com";
        } else if (name.endsWith("cn")) {
            type = "cn";
        } else {

            //
        }

        return type;
    }

    /**
     * 根据字符串表示的信息对应的赋给SiteInfoBean
     * @param siteInfo
     * @param str
     */
    public void setSiteInfo(SiteInfoBean siteInfo, String str) {
        String[] info = str.split(":");

        if (info[0].equals("Domain Name")) {
            siteInfo.setDomainName(info[1]);
        } else if (info[0].equals("ROID")) {
            siteInfo.setRoId(info[1]);
        } else if (info[0].equals("Domain Status")) {
            siteInfo.addDomainStatus(info[1]);
        } else if (info[0].equals("Registrant Organization")) {
            siteInfo.setOrganization(info[1]);
        } else if (info[0].equals("Registrant Name")) {
            siteInfo.setRegistrantName(info[1]);
        } else if (info[0].equals("Administrative Email")) {
            siteInfo.setEmail(info[1]);
        } else if (info[0].equals("Sponsoring Registrar")) {
            siteInfo.setSponsorRegistrar(info[1]);
        } else if (info[0].equals("Name Server")) {
            siteInfo.addNameServer(info[1]);
        } else if (info[0].equals("Registration Date")) {
            siteInfo.setRegistrationDate(info[1].substring(0, info[1].length() - 3));
        } else if (info[0].equals("Expiration Date")) {
            siteInfo.setExpirationDate(info[1].substring(0, info[1].length() - 3));
        }
    }
}
