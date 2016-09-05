package neos.util;

import java.io.File;
import java.util.Date;

public class Md5Test {
	public static void main(String[] args){
		try{
			File file=new File("E:\\workspace\\neos\\attachments\\emailtest\\393\\ÐÂÎÅ³ö°æÊð½ØÍ¼.png");
			
			Date start=new Date();
			String str=MD5Util.getFileMD5String(file);
			Date end=new Date();
			
			long runtime=end.getTime()-start.getTime();
			System.out.println("MD5: "+str);
			System.out.println("File Name: "+file.getName()+"\tFile Length: "+file.length()+"byte\t run time: "+runtime+"ms");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
