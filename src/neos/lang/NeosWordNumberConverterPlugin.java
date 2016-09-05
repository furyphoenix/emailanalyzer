package neos.lang;

public interface NeosWordNumberConverterPlugin {
	boolean isCapable(String str);
	int convert(String str);
	String convert(int val);
}
