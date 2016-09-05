package neos.component.ner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndexRange implements Comparable<IndexRange>{
	private final int start;
	private final int end;
	
	public IndexRange(int start, int end){
		if(start>end){
			throw new IllegalArgumentException("end must no less than start!");
		}
		
		this.start=start;
		this.end=end;
	}
	
	public int getStart(){
		return start;
	}
	
	public int getEnd(){
		return end;
	}

	public boolean isContain(IndexRange r){
		if((start<=r.start)&&(end>=r.end)){
			return true;
		}
		return false;
	}
	
	public boolean isSeperate(IndexRange r){
		if((start>r.end)||(end<r.start)){
			return true;
		}
		return false;
	}
	
	public static boolean isSeperate(IndexRange r1, IndexRange r2){
		if((r1.start>r2.end)||(r1.end<r2.start)){
			return true;
		}
		return false;
	}
	
	public static boolean isContain(IndexRange r1, IndexRange r2){
		if((r1.start<=r2.start)&&(r1.end>=r2.end)){
			return true;
		}
		return false;
	}
	
	public static IndexRange intersection(IndexRange r1, IndexRange r2){
		if(r1.isSeperate(r2)){
			return null;
		}
		
		int idx1=(r1.start>r2.start?r1.start:r2.start);
		int idx2=(r1.end<r2.end?r1.end:r2.end);
		
		return new IndexRange(idx1, idx2);
	}
	
	public static List<IndexRange> union(IndexRange r1, IndexRange r2){
		List<IndexRange> lst=new ArrayList<IndexRange> ();
		
		if(r1.isSeperate(r2)){
			lst.add(r1);
			lst.add(r2);
		}else{
			int idx1=(r1.start<r2.start?r1.start:r2.start);
			int idx2=(r1.end>r2.end?r1.end:r2.end);
			lst.add(new IndexRange(idx1, idx2));
		}
		
		return lst;
	}
	
	public static List<IndexRange> union(List<IndexRange> rs){
		List<IndexRange> rl=new ArrayList<IndexRange> ();
		
		if(rs.size()==0){
			return rl;
		}
		
		Collections.sort(rs);
		rl.add(rs.get(0));
		
		for(int i=1; i<rs.size(); i++){
			int idx=rl.size()-1;
			IndexRange or=rl.remove(idx);
			IndexRange cr=rs.get(i);
			if(cr.isSeperate(or)){
				rl.add(or);
				rl.add(cr);
			}else{
				if(cr.isContain(or)){
					rl.add(cr);
				}else if(or.isContain(cr)){
					rl.add(or);
				}else{
					int idx1=or.start;
					int idx2=or.end>cr.end?or.end:cr.end;
					rl.add(new IndexRange(idx1, idx2));
				}
			}
		}
		
		return rl;
		
		
	}
		
	@Override
	public boolean equals(Object o){
		if(!(o instanceof IndexRange)){
			return false;
		}
		
		IndexRange r=(IndexRange)o;
		if((r.start==start)&&(r.end==end)){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int result=17;
		result=result*31+start;
		result=result*31+end;
		
		return result;
	}

	@Override
	public int compareTo(IndexRange o) {
		if(start!=o.start){
			return start-o.start;
		}else{
			return end-o.end;
		}
	}
	
	@Override
	public String toString(){
		return "("+start+","+end+")";
	}
	
	public static void main(String[] args){
		int[][] data={{1,5},{2,7},{2,3},{7,12},{3,4},{7,11},{19,33},{35,101}};
		
		List<IndexRange> list=new ArrayList<IndexRange> ();
		for(int[] row:data){
			list.add(new IndexRange(row[0], row[1]));
		}
		
		List<IndexRange> nlist=union(list);
		for(IndexRange r:nlist){
			System.out.println(r);
		}
	}
	
	
}
