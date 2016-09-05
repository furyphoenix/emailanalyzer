package neos.component.ner;

//~--- JDK imports ------------------------------------------------------------

import java.util.regex.Pattern;

public abstract class AbstractNeosRegexEntityTranslator implements NeosEntityTranslator {
    protected final String regex;

    public AbstractNeosRegexEntityTranslator(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean isCapable(String src) {
        return Pattern.matches(regex, src);
    }
    
    @Override
	public boolean equals(Object o){
		if(!(o instanceof AbstractNeosRegexEntityTranslator)){
			return false;
		}
		
		AbstractNeosRegexEntityTranslator t=(AbstractNeosRegexEntityTranslator)o;
		if(regex.equals(t.regex)){
			return true;
		}
		return false;
		
	}
	
	@Override
	public int hashCode(){
		int result=17;
		result=31*result+regex.hashCode();
		return result;
	}
}
