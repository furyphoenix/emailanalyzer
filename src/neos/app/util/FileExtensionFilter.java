package neos.app.util;

import java.io.File;
import java.io.FileFilter;

public class FileExtensionFilter implements FileFilter {
	private final String ext;
	
	public FileExtensionFilter(String ext){
		this.ext=ext.toLowerCase();
	}
	
	@Override
	public boolean accept(File pathname) {
		if(pathname.isDirectory()){
			return false;
		}
		
		String filename=pathname.getName().toLowerCase();
		
		if(filename.endsWith("."+ext)){
			return true;
		}
		
		return false;
	}

}
