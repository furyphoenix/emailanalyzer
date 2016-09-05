package neos.algorithm.afisa;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AfisaTool {
	private Afisa.ProximityMethod pm=Afisa.ProximityMethod.Complete;
	private Afisa.DistanceMethod dm=Afisa.DistanceMethod.D1;
	private boolean overlap=false;
	private int minSupport=10;
	
	public AfisaTool(int minSupport){
		this.minSupport=minSupport;
	}
	
	public void setProximityMethod(Afisa.ProximityMethod pm){
		this.pm=pm;
	}
	
	public void setDistanceMethod(Afisa.DistanceMethod dm){
		this.dm=dm;
	}
	
	public void setOverlap(boolean overlap){
		this.overlap=overlap;
	}
	
	public void setMinSupport(int minSupport){
		this.minSupport=minSupport;
	}
	
	public Map<Integer, List<Cluster>> getFrequentItemSet(BitSet[] BitVectors){
		Distance distance;
		switch(dm){
		case D1:
			distance=new D1(BitVectors);
			break;
		case XOR:
			distance=new DistanceXOR(BitVectors);
			break;
		default:
			distance=new D1(BitVectors);
		}
		
		Afisa afisa=new Afisa(BitVectors, distance.getMatrix(), pm, minSupport, overlap);
		return afisa.run();
	}
	
	public List<Map.Entry<Integer, List<Cluster>>> getSortedFrequentItemSet(BitSet[] BitVectors, boolean removeSingle){
		Map<Integer, List<Cluster>> map=getFrequentItemSet(BitVectors);
		
		Set<Integer> removeKeys=new HashSet<Integer> ();
		
		if(removeSingle){
			for(Integer sup:map.keySet()){
				List<Cluster> clusterList=map.get(sup);
				for(int i=clusterList.size()-1; i>=0; i--){
					Cluster cluster=clusterList.get(i);
					if(cluster.getItems().size()<=1){
						clusterList.remove(i);
					}
				}
				if(clusterList.size()<=0){
					removeKeys.add(sup);
				}
			}
			
			for(Integer key:removeKeys){
				map.remove(key);
			}
		}
		
		
		
		Set<Map.Entry<Integer, List<Cluster>>> entrySet=map.entrySet();
		List<Map.Entry<Integer, List<Cluster>>> entryList=new ArrayList<Map.Entry<Integer, List<Cluster>>> ();
		for(Map.Entry<Integer, List<Cluster>> entry:entrySet){
			entryList.add(entry);
		}
		
		Comparator<Map.Entry<Integer, List<Cluster>>> comp=new Comparator<Map.Entry<Integer, List<Cluster>>>(){
			@Override
			public int compare(Entry<Integer, List<Cluster>> entry1,
					Entry<Integer, List<Cluster>> entry2) {
				return entry2.getKey().intValue()-entry1.getKey().intValue();
			}
		};
		
		Collections.sort(entryList, comp);
		
		return entryList;
	}
}
