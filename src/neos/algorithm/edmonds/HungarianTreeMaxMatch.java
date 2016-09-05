package neos.algorithm.edmonds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.UndirectedGraph;

public class HungarianTreeMaxMatch <V,E>{
	private UndirectedGraph<V,E> mgraph;
	private Set<V> innerVertexSet=new HashSet<V> ();
	private Set<V> outerVertexSet=new HashSet<V> ();
	private List<V> currPath=new ArrayList<V> ();
	
	
	public HungarianTreeMaxMatch (UndirectedGraph<V,E> graph){
		this.mgraph=graph;
		
	}
	public void evaluate(){
		
	}
	
	
	
	public List<V> getAugmentingPath(List<V> match){
		List<V> vlist=new ArrayList<V> ();
		
		
		
		return vlist;
	}
	
	private List<V> buildTree(V v, List<V> match){
		return null;
		
	}
	
	public List<V> getMaxMatch(){
		return null;
	}
}
