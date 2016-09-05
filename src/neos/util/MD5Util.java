package neos.util;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 * @author Li Xiaoyu
 *
 */
public class MD5Util {
    protected static char          hexDigits[]   = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
            nsaex.printStackTrace();
        }
    }

    /**
     * 获取文件的MD5值
     * @param file 待计算的文件
     * @return 文件的MD5值
     * @throws IOException 文件读取错误
     */
    public synchronized static String getFileMD5String(File file) throws IOException {
        FileInputStream in     = new FileInputStream(file);
        byte[]          buffer = new byte[1024 * 1024 * 10];
        int             len    = 0;

        while ((len = in.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, len);
        }

        in.close();

        return bufferToHex(messagedigest.digest());
    }

    /**
     * 获取字符串的MD5值
     * @param s 待计算的字符串
     * @return 字符串的MD5值
     */
    public synchronized static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    /**
     * 获取字节数组的MD5值
     * @param bytes 待计算的字节数组
     * @return 字节数组的MD5值
     */
    public synchronized static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);

        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int          k            = m + n;

        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }

        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];

        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
