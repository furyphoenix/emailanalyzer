package neos.tool.stanfordnlp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import neos.component.ner.IndexRange;
import neos.component.ner.NeosNamedEntity.NamedEntityType;
import neos.component.ner.NeosNerTool;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

public class NeosStanfordNerTool implements NeosNerTool{
	private final static String model="./data/muc.7class.distsim.crf.ser.gz";
	private final static NeosStanfordNerTool tool=new NeosStanfordNerTool();
	public static enum LabelType{TIME, LOCATION, ORGANIZATION, PERSON, MONEY, PERCENT, DATE};
	private AbstractSequenceClassifier classifier;
	
	private NeosStanfordNerTool(){
		try {
			classifier=CRFClassifier.getClassifier(model);
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static NeosStanfordNerTool getInstance(){
		return tool;
	}
	
	private String annoteWithInlineXML(String text){
		return classifier.classifyWithInlineXML(text);
	}
	
	@Override
	public HashMap<String, NamedEntityType> locate(String text){
		HashMap<String, NamedEntityType> map=new HashMap<String, NamedEntityType> ();
		
		String ltext=this.annoteWithInlineXML(text);
		
		LabelType[] types=LabelType.values();
		for(int i=0; i<types.length; i++){
			String patStart="<"+types[i].name()+">";
			String patEnd="</"+types[i].name()+">";
			int offset=0;
			int idxA, idxB;
			while((idxA=ltext.indexOf(patStart, offset))>=0){
				idxB=ltext.indexOf(patEnd, idxA);
				if(idxB>=0){
					String exp=ltext.substring(idxA+patStart.length(), idxB);
					if((exp!=null)&&(exp.length()>0)){
						map.put(exp, typeTransform(types[i]));
					}
					offset=idxB+patEnd.length();
				}else{
					break;
				}
			}
		}
		
		return map;
	}
	
	private static NamedEntityType typeTransform(LabelType type){
		switch(type){
		case TIME:
			return NamedEntityType.DateTime;
		case LOCATION:
			return NamedEntityType.LocationName;
		case ORGANIZATION:
			return NamedEntityType.OrgnizationName;
		case PERSON:
			return NamedEntityType.PersonName;
		case MONEY:
			return NamedEntityType.GeneralNumber;
		case PERCENT:
			return NamedEntityType.GeneralNumber;
		case DATE:
			return NamedEntityType.DateTime;
		default:
			return NamedEntityType.GeneralNumber;	
		}
	}
	
	public static void main(String[] args){
		String text="U.S. and South Korean troops began a 10-day military exercise around Seoul on Tuesday, maneuvers the U.S. commander called \"defense-oriented\" during a period of heightened tensions with the communist North."+
		"The annual \"Ulchi Freedom Guardian\" exercise involves about 530,000 troops from South Korea, the United States and seven other countries, as well as computer-aided simulations, the U.S. command in Seoul reported.\""+
		"Dr. John said it was a big event in recent years. The UN annouces support to this report.";
		
		HashMap<String, NamedEntityType> map=tool.locate(text);
		System.out.println(map);
	}
}
