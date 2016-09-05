package neos.tool.unpack;

import java.io.File;
import java.io.IOException;

public class FileTest {
	public static void main(String[] args) {
		/*File file=new File("D:\\icon");
		File iconFile=new File("d:\\icon\\1307955175_transform-move-icon.png");
		
		System.out.println("absolute path: "+file.getAbsolutePath());
		try {
			System.out.println("canonicalPat: "+file.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("name: "+file.getName());
		
		
		System.out.println("absolute path: "+iconFile.getAbsolutePath());
		try {
			System.out.println("canonicalPat: "+iconFile.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("name: "+iconFile.getName());*/
		File file=new File("D:\\icon.7z");
		File dest=new File("D:\\test");
		dest.mkdirs();
		NeosUnpackTool tool=new Neos7ZipTool();
		try {
			tool.unpack(file, dest, true, true);
		} catch (NeosUnpackWrongPasswordException e) {
			// TODO Auto-generated catch block
			System.out.println("encypted attach: "+e.getFile().getName());
			e.printStackTrace();
		}
		
	}
}
