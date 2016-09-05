package neos.algorithm.edmonds;

//~--- non-JDK imports --------------------------------------------------------

import edu.uci.ics.jung.graph.UndirectedGraph;

//~--- JDK imports ------------------------------------------------------------

//import edu.uci.ics.jung.graph.DelegateTree;
//import edu.uci.ics.jung.graph.Tree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class BlossomAlgorithm<V, E> {
    private Map<E, Boolean>       eflags;
    private UndirectedGraph<V, E> graph;
    private Stack<Flower<V>>                 stack;
    private Map<V, Boolean>       vflags;
    
    public enum VertexLabel {INNER, OUTER, FREE};
    public enum EdgeLabel {SELECTED, UNSELECTED};

    public BlossomAlgorithm(UndirectedGraph<V, E> graph) {
        this.graph = graph;
        stack      = new Stack<Flower<V>>();
        vflags     = new HashMap<V, Boolean>();    // true for inner, false for outer;
        eflags     = new HashMap<E, Boolean>();    // true for marked, false for unmarked;
    }

    public void evaluate() {
        GraphMatcher<V, E> m       = new GraphMatcher<V, E>(graph);
        boolean            maximum = false;

        while (!maximum) {
            Set<V>  f       = m.findFreeVertics();
            boolean augment = false;
            Tree<V> tree;

            while ((f.iterator().hasNext()) && (!augment)) {
                stack.clear();
                vflags.clear();
                eflags.clear();

                V x = f.iterator().next();

                f.remove(x);
                tree = new Tree<V>(x);
                vflags.put(x, false);

                boolean hungarian = false;

                while (!augment) {
                    V y = findUnmarkedEdgeVertex(x);

                    if (y != null) {
                        E e = graph.findEdge(x, y);

                        eflags.put(e, false);
                    } else {
                        hungarian = true;

                        break;
                    }

                    if (vflags.containsKey(y)) {
                        boolean isInner = vflags.get(y);

                        if (isInner) {          // inner

                            // do nothing
                        } else {                // outer
                        	Flower<V> flower=new Flower<V> (tree, y, x);
                        	stack.push(flower);
                        }
                    } else {
                        if (f.contains(y)) {    // free
                            augment = true;
                            f.remove(y);
                        } else {
                            V z = m.getOppositVertex(y);

                            tree.extend(x, y);
                            tree.extend(y, z);
                            markEdge(x, y, false);
                            markEdge(y, z, true);
                            vflags.put(y, true);
                            vflags.put(z, false);
                        }
                    }
                }

                if (hungarian) {
                    for (V v : tree.getVertics()) {
                        graph.removeVertex(v);
                    }
                } else {
                    if (augment) {
                    	
                    }
                }
            }
        }
    }

    private V findUnmarkedEdgeVertex(V v) {
        Collection<V> cv = graph.getNeighbors(v);

        for (V nv : cv) {
            E e = graph.findEdge(v, nv);

            if (!eflags.containsKey(e)) {
                return nv;
            }
        }

        return null;
    }

    private void markEdge(V v1, V v2, boolean mark) {
        E e = graph.findEdge(v1, v2);

        eflags.put(e, mark);
    }
    
    private void shrinkFlower(Flower<V> flower){
    	Set<V> neighbours=new HashSet<V> ();
    	for(V v:flower.getVertics()){
    		Collection<V> nvs=graph.getNeighbors(v);
    		neighbours.addAll(nvs);
    	}
    	
    	
    }

    private class Flower<V>{
        private List<V> list;
        private Tree<V> t;
        private V       v1;
        private V       v2;

        public Flower(Tree<V> t, V v1, V v2) {
            this.t  = t;
            this.v1 = v1;
            this.v2 = v2;
            list=new ArrayList<V> ();
            TreeNode<V> v=t.getTreeNode(v1);
            list.add(v1);
            while((v.parent!=null)&&(!v.parent.v.equals(v2))){
            	list.add(v.parent.v);
            	v=v.parent;
            }
        }
        
        public Collection<V> getVertics(){
        	return list;
        }
        
        public List<V> expand(V start, V end){
        	int idx1=-1;
        	int idx2=-1;
        	
        	int from=-1;
        	int to=-1;
        	
        	List<V> path=new ArrayList<V> ();
        	
        	for(int i=0; i<list.size(); i++){
        		if(list.get(i).equals(start)){
        			idx1=i;
        			continue;
        		}
        		if(list.get(i).equals(end)){
        			idx2=i;
        		}
        		if(idx1>0&&idx2>0){
        			break;
        		}
        	}
        	
        	
        	if(idx2>idx1){
        		if((idx2-idx1)%2==0){
        			from=idx1;
        			to=idx2;
        			
        			for(int i=from; i<=to; i++){
            			path.add(list.get(i%list.size()));
            		}
        		}else{
        			from=idx1+list.size();
        			to=idx2;
        			
        			for(int i=from; i>=to; i--){
            			path.add(list.get(i%list.size()));
            		}
        		}
        		
        	}else{
        		if((idx1-idx2)%2==0){
        			from=idx2;
        			to=idx1;
        			
        			for(int i=from; i>=to; i++){
        				path.add(list.get(i%list.size()));
            		}
        		}else{
        			from=idx2+list.size();
        			to=idx1;
        			
        			for(int i=from; i<=to; i--){
        				path.add(list.get(i%list.size()));
            		}
        		}
        		
        		
        	}
        	
        	return path;
        }
    }


    private class Tree<V> {
        private Map<V, TreeNode<V>> nodeMap;
        private V                   root;

        public Tree(V v) {
            this.root = v;
            nodeMap   = new HashMap<V, TreeNode<V>>();
        }

        public boolean extend(V parent, V child) {
            if (nodeMap.containsKey(parent)) {
                TreeNode<V> pNode = nodeMap.get(parent);
                TreeNode<V> cNode = new TreeNode<V>(child);

                if (!pNode.children.contains(cNode)) {
                    pNode.children.add(cNode);

                    return true;
                }
            }

            return false;
        }

        public Collection<V> getVertics() {
            return nodeMap.keySet();
        }
        
        public TreeNode<V> getTreeNode(V v){
        	return nodeMap.get(v);
        }
        
    }


    private class TreeNode<V> {
        private Set<TreeNode<V>> children;
        private TreeNode<V>      parent;
        private V                v;

        public TreeNode(V v) {
            this.v   = v;
            children = new HashSet<TreeNode<V>>();
            parent   = null;
        }

        public void addChild(V child) {
            TreeNode<V> node = new TreeNode<V>(child);

            children.add(node);
            node.parent = this;
        }

        @Override
        public int hashCode() {
            return v.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TreeNode) {
                TreeNode ov = (TreeNode) o;

                return v.equals(ov.v);
            }

            return false;
        }
    }
}
