package neos.algorithm.lcscs;

public class SortableIdxStr extends SortableElem implements Cloneable {
	protected int index;
	protected String strElem;
	
	public SortableIdxStr(String str, int idx){
		strElem=new String(str);
		index=idx;
		sortElem=1;
	}
	
	public SortableIdxStr(String str, int idx, int sortNum){
		strElem=new String(str);
		index=idx;
		sortElem=sortNum;
	}
	
	protected Object clone(){
		SortableIdxStr newElem=new SortableIdxStr(this.strElem,this.index);
		newElem.sortElem=this.sortElem;
		return newElem;
	}
}
