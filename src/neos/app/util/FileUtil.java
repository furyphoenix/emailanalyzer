package neos.app.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	public static List<File> listFile(File dir, FileFilter filter, boolean iter){
		List<File> lst=new ArrayList<File> ();
		if(!dir.isDirectory()){
			return lst;
		}
		File[] children=dir.listFiles();
		for(int i=0; i<children.length; i++){
			File file=children[i];
			if(filter.accept(file)){
				lst.add(file);
			}
			if(file.isDirectory()&&iter){
				lst.addAll(listFile(file, filter, true));
			}
		}
		return lst;
	}
}
