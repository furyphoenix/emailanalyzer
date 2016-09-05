package neos.app.util;

import java.io.File;
import java.io.FileFilter;

public class FileAndFilter implements FileFilter {
	private final FileFilter filter1;
	private final FileFilter filter2;
	
	public FileAndFilter(FileFilter filter1, FileFilter filter2){
		this.filter1=filter1;
		this.filter2=filter2;
	}
	
	
	@Override
	public boolean accept(File pathname) {
		return filter1.accept(pathname)&&filter2.accept(pathname);
	}

}
