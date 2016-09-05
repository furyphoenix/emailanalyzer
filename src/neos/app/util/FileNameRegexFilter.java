package neos.app.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class FileNameRegexFilter implements FileFilter {
	private String regex;
	
	public FileNameRegexFilter(String regex){
		this.regex=regex;
	}
	
	@Override
	public boolean accept(File file) {
		if(file.isDirectory()){
			return false;
		}
		
		String fileName=file.getName();
		if(Pattern.matches(regex, fileName)){
			return true;
		}
		
		return false;
	}

}
