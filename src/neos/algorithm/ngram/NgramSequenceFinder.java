package neos.algorithm.ngram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NgramSequenceFinder {
	private final static String spliterRegex="\\pP|\\pS";
	
	public NgramSequenceFinder(){
		
	}
	
	public Map<Integer, List<String>> runAlgorithm(List<String> docs, int n, int min){
		Map<Integer, List<String>> map=new Hashtable<Integer, List<String>> ();
		
		Map<String, Integer> ngramCntMap=new Hashtable<String, Integer> ();
		Map<String, Integer> ngramDocCntMap=new Hashtable<String, Integer> ();
		
		for(String doc:docs){
			String[] segs=doc.split(spliterRegex);
			Set<String> ngramSet=new HashSet<String> ();
			
			for(String seg:segs){
				for(int i=2; i<n; i++){
					for(int j=0; j<seg.length()-i; j++){
						String ngram=seg.substring(j, j+i);
						int cnt=0; 
						if(ngramCntMap.containsKey(seg)){
							cnt=ngramCntMap.get(ngram);
						}
						ngramCntMap.put(seg, cnt+1);
						if(!ngramSet.contains(ngram)){
							ngramSet.add(ngram);
						}
					}
				}
			}
			
			for(String ngram:ngramSet){
				int cnt=0;
				if(ngramDocCntMap.containsKey(ngram)){
					cnt=ngramDocCntMap.get(ngram);
				}
				ngramDocCntMap.put(ngram, cnt+1);
			}
		}
		
		return map;
	}
	
	private final static void shrinke(Map<String, Integer> map, int min){
		Set<String> strs=map.keySet();
		
		Map<Integer, List<String>> lenSeqListMap=new Hashtable<Integer, List<String>> ();
		
		for(String seq:strs){
			int len=seq.length();
			List<String> seqList=null;
			if(lenSeqListMap.containsKey(len)){
				seqList=lenSeqListMap.get(len);
			}else{
				seqList=new ArrayList<String> ();
			}
			seqList.add(seq);
		}
		
		List<Integer> lenList=new ArrayList<Integer> ();
		for(Integer len:lenSeqListMap.keySet()){
			lenList.add(len);
		}
		
		Collections.sort(lenList);
		
	} 
}
