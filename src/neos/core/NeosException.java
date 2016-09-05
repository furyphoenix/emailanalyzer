package neos.core;

public class NeosException extends Exception {
	public NeosException(){
		super();
	}
	
	public NeosException(String message){
		super(message);
	}
	
	public NeosException(String message, Throwable cause){
		super(message, cause);
	}
	
	public NeosException(Throwable cause){
		super(cause);
	}
}
