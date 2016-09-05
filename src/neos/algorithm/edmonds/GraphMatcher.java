package neos.algorithm.edmonds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphMatcher<V,E> {
	private final Set<E> edges;
	private final Set<V> vertics;
	private final UndirectedGraph<V, E> graph;
	private final Map<V,V> map;
	
	public GraphMatcher(UndirectedGraph<V, E> graph){
		edges=new HashSet<E> ();
		vertics=new HashSet<V> ();
		map=new HashMap<V, V> ();
		this.graph=graph;
	}
	
	public void add(E e){
		if(edges.contains(e)){
			throw new IllegalArgumentException("Edge "+e.toString()+" already existed!");
		}
		
		Pair<V> vp=graph.getEndpoints(e);
		V v1=vp.getFirst();
		V v2=vp.getSecond();
		if(vertics.contains(v1)){
			throw new IllegalArgumentException("Vertex "+v1.toString()+" already existed!");
		}
		if(vertics.contains(v2)){
			throw new IllegalArgumentException("Vertex "+v2.toString()+" already existed!");
		}
		vertics.add(v1);
		vertics.add(v2);
		
		map.put(v1, v2);
		map.put(v2, v1);
	}
	
	public Set<V> findFreeVertics(){
		Set<V> f=new HashSet<V> ();
		
		for(V v:graph.getVertices()){
			if(!vertics.contains(v)){
				f.add(v);
			}
		}
		
		return f;
	}
	
	public void addAugmentPath(List<V> path){
		for(int i=1; i<path.size()-2; i=i+2){
			V v1=path.get(i);
			V v2=path.get(i+1);
			E e=graph.findEdge(v1, v2);
			edges.remove(e);
			vertics.remove(v1);
			vertics.remove(v2);
			map.put(v1, v2);
			map.put(v2, v1);
		}
		
		for(int i=0; i<path.size()-1; i=i+2){
			V v1=path.get(i);
			V v2=path.get(i+1);
			E e=graph.findEdge(v1, v2);
			edges.add(e);
			vertics.add(v1);
			vertics.add(v2);
		}
	}
	
	public V getOppositVertex(V v){
		return map.get(v);
	}
	
	public Set<E> getEdgeSet(){
		return edges;
	}
	
	public boolean isEmpty(){
		return edges.size()>0;
	}
}
