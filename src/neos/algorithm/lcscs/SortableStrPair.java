package neos.algorithm.lcscs;

public class SortableStrPair extends SortableElem implements Cloneable{
	public String strA;
	public String strB;
	
	public SortableStrPair(){
		strA=new String("");
		strB=new String("");
		sortElem=1;
	}
	
	public SortableStrPair(String srcA, String srcB){
		strA=new String(srcA);
		strB=new String(strB);
		sortElem=1;
	}
	
	public SortableStrPair(String srcA, String srcB, int num){
		if(srcA!=null){
			strA=new String(srcA);
		}else{
			strA=new String("");
		}
		if(srcB!=null){
			strB=new String(srcB);
		}else{
			strB=new String("");
		}
		sortElem=num;
	}
	
	protected Object clone(){
		SortableStrPair newElem=new SortableStrPair(this.strA,this.strB);
		newElem.sortElem=this.sortElem;
		return newElem;
	}
}

