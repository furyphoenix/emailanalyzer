package neos.component.ner;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import neos.component.ner.NeosNamedEntity.NamedEntityType;

public class NeosRegexNerTool implements NeosNerTool {
	private HashMap<String, NamedEntityType> regexMap;
	
	public NeosRegexNerTool(){
		regexMap=new HashMap<String, NamedEntityType> ();
	}
	
	public void addRegex(String regex, NamedEntityType type){
		regexMap.put(regex, type);
	}

	@Override
	public HashMap<String, NamedEntityType> locate(String text) {
		HashMap<String, NamedEntityType> map=new HashMap<String, NamedEntityType> ();
		
		for(String regex:regexMap.keySet()){
			NamedEntityType type=regexMap.get(regex);
			Pattern pat=Pattern.compile(regex);
			Matcher m=pat.matcher(text);
			while(m.find()){
				String exp=m.group();
				if((exp!=null)&&(exp.length()>0)){
					map.put(exp, type);
				}
				
			}
		}
		
		return map;
	}

}
