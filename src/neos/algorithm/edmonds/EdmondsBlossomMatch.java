package neos.algorithm.edmonds;

//~--- non-JDK imports --------------------------------------------------------

import edu.uci.ics.jung.graph.UndirectedGraph;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EdmondsBlossomMatch<V, E> {
    private Map<V, Boolean>       vertexFlagMap = new HashMap<V, Boolean>();    // true for inner, false for outer, null for free
    private Set<E>                markedEdgeSet = new HashSet<E>();
    private UndirectedGraph<V, E> graph;
    private List<E>               match;

    public EdmondsBlossomMatch(UndirectedGraph<V, E> graph) {
        this.graph = graph;
    }

    public void evaluate() {
        List<V> m       = new ArrayList<V>();
        boolean maximum = false;

        while (!maximum) {
            Set<V>  f       = getFreeVertics(m);
            
            boolean augment = false;

            while ((f.size() > 0) && (!augment)) {
                vertexFlagMap.clear();
                markedEdgeSet.clear();

                while (f.iterator().hasNext()) {
                    V v = f.iterator().next();
                    f.remove(v);

                    vertexFlagMap.put(v, false);

                    boolean hungrian = false;

                    while (!augment) {
                        V nv = findUnmarkedEdge(v);

                        if (nv != null) {
                            markEdge(v, nv);
                        } else {
                            hungrian = true;

                            break;
                        }

                        if (vertexFlagMap.containsKey(nv)) {        
                            if (vertexFlagMap.get(nv) == true) {    // inner
                            	
                            } else {                                // outer
                            	
                            }
                        } else {                                    
                        	if(f.contains(nv)){						// free
                        		augment = true;
                        		f.remove(nv);
                        	}else{
                        		
                        	}
                            
                        }
                    }
                }
            }
        }
    }

    private Set<V> getFreeVertics(List<V> m) {
        return null;
    }

    private V findUnmarkedEdge(V v) {
        Collection<V> vset = graph.getNeighbors(v);

        for (V nv : vset) {
            E e = graph.findEdge(v, nv);

            if (markedEdgeSet.contains(e)) {
                continue;
            } else {
                return nv;
            }
        }

        return null;
    }

    private void markEdge(V v1, V v2) {
        E e = graph.findEdge(v1, v2);

        markedEdgeSet.add(e);
    }
    
    private V getNextVertexInMatch(V v, List<V> m){
    	for(int i=0; i<m.size()-1; i++){
    		if(m.get(i).equals(v)){
    			return m.get(i+1);
    		}
    	}
    	return null;
    }
}
