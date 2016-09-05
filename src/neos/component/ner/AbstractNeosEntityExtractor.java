package neos.component.ner;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neos.util.ConvertResult;

public abstract class AbstractNeosEntityExtractor{
	private Map<NeosEntityLocator, NeosEntityTranslator> locatorTranslatorMap;
	private List<IndexRange> rlist;
	private Map<IndexRange, List<ConvertResult<NeosNamedEntity>>> rangeResultMap;
	
	
	public AbstractNeosEntityExtractor(){
		locatorTranslatorMap=new Hashtable<NeosEntityLocator, NeosEntityTranslator> ();
		rangeResultMap=new Hashtable<IndexRange, List<ConvertResult<NeosNamedEntity>>> ();
		rlist=new ArrayList<IndexRange> ();
	}
	
	public void addProcessor(NeosEntityLocator loc, NeosEntityTranslator tran){
		locatorTranslatorMap.put(loc, tran);
	}
	
	public void clearProcessor(){
		locatorTranslatorMap.clear();
	}
	
	public void extract(String src, boolean isMergeRange){
		clear();
		
		Set<Map.Entry<NeosEntityLocator, NeosEntityTranslator>> entrySet=locatorTranslatorMap.entrySet();
		Iterator<Map.Entry<NeosEntityLocator, NeosEntityTranslator>> entryIter=entrySet.iterator();
		while(entryIter.hasNext()){
			Map.Entry<NeosEntityLocator, NeosEntityTranslator> entry=entryIter.next();
			NeosEntityLocator loc=entry.getKey();
			NeosEntityTranslator tran=entry.getValue();
			List<IndexRange> rangeList=loc.locate(src);
			loop: for(IndexRange range:rangeList){
				if(isMergeRange){
					for(int i=0; i<rlist.size(); i++){
						if(range.isContain(rlist.get(i))){
							IndexRange currRange=rlist.remove(i);
							rangeResultMap.remove(currRange);
							i--;
						}else if(rlist.get(i).isContain(range)){
							continue loop;
						}
					}
				}
				rlist.add(range);
				String seq=src.substring(range.getStart(), range.getEnd());
				List<ConvertResult<NeosNamedEntity>> resultList=tran.translate(seq);
				rangeResultMap.put(range, resultList);
			}
		}
	}
	
	
	
	private void clear(){
		rlist.clear();
		rangeResultMap.clear();
	}
	
	
}
