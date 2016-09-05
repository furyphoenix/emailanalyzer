package neos.util.log;

public interface NeosLogger {
	void debug(String msg);
	void info(String msg);
	void warn(String msg);
	void error(String msg);
	void fatal(String msg);
}
