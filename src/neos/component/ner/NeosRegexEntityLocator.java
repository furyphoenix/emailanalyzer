package neos.component.ner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NeosRegexEntityLocator implements NeosEntityLocator {
	private final Pattern pat;
    private final String  regex;
    
    public NeosRegexEntityLocator(String regex){
    	this.regex = regex;
        this.pat   = Pattern.compile(regex);
    }
	
    public String getRegex(){
    	return regex;
    }
	
	@Override
	public List<IndexRange> locate(String src) {
		List<IndexRange> resultList = new ArrayList<IndexRange>();
        Matcher          mat        = pat.matcher(src);

        while (mat.find()) {
            resultList.add(new IndexRange(mat.start(), mat.end()));
        }

        return resultList;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof NeosRegexEntityLocator)){
			return false;
		}
		
		NeosRegexEntityLocator l=(NeosRegexEntityLocator)o;
		if(regex.equals(l.regex)){
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
