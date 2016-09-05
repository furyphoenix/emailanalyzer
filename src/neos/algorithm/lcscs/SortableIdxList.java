package neos.algorithm.lcscs;
import java.util.*;

public class SortableIdxList extends SortableElem implements Cloneable {
	protected int index;
	protected LinkedList<SortableIdxStr> list;
	
	public SortableIdxList(int idx){
		list=new LinkedList<SortableIdxStr>();
		index=idx;
		sortElem=1;
	}
	
	protected Object clone(){
		SortableIdxList newList=new SortableIdxList(this.index);
		Iterator iter=this.list.iterator();
		while(iter.hasNext()){
			newList.list.add((SortableIdxStr)((SortableIdxStr)iter.next()).clone());
		}
		return newList;
	}

}
