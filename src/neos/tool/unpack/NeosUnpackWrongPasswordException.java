package neos.tool.unpack;

import java.io.File;

import neos.core.NeosException;

public class NeosUnpackWrongPasswordException extends NeosException {
	/*public NeosUnpackWrongPasswordException(){
		super();
	}
	
	public NeosUnpackWrongPasswordException(String message){
		super(message);
	}*/
	
	private File file;
	
	
	public NeosUnpackWrongPasswordException(File file){
		super();
		this.file=file;
	}
	
	public File getFile(){
		return file;
	}
}
