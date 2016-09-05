package neos.util.log;

public class NeosNullLogger implements NeosLogger {
	private final static NeosNullLogger log=new NeosNullLogger();
	
	private NeosNullLogger(){} 
	
	public static NeosNullLogger getInstance(){
		return log;
	}
	
	@Override
	public void debug(String msg) {
		

	}

	@Override
	public void error(String msg) {
		

	}

	@Override
	public void fatal(String msg) {
		

	}

	@Override
	public void info(String msg) {
		

	}

	@Override
	public void warn(String msg) {
		

	}

}
