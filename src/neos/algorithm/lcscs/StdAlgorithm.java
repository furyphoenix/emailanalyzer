package neos.algorithm.lcscs;

import java.util.*;
import java.math.*;

public class StdAlgorithm {
	//quick sort algorithm, used with LinkedList<SortableElem>
	public static void quickSort(LinkedList lst){
		recQuickSort(lst,0,lst.size()-1);
	}
	
	private static void recQuickSort(LinkedList<SortableElem> lst, int left, int right){
		if(right-left<=0){
			return;
		}else{
			int pivot=lst.get(right).sortElem;
			int partition=partitionIt(lst,left,right,pivot);
			recQuickSort(lst,left,partition-1);
    		recQuickSort(lst,partition+1,right);
		}
	}
	
	private static int partitionIt(LinkedList<SortableElem> lst, int left, int right, int pivot){
		int leftPtr=left-1;
    	int rightPtr=right;
    	
    	while(true){
    		while((lst.get(++leftPtr)).sortElem<pivot);
    		while((rightPtr>0)&&((lst.get(--rightPtr)).sortElem>pivot));
    		if(leftPtr>=rightPtr){
    			break;
    		}
    		else{
    			swapListElem(lst,leftPtr,rightPtr);
    		}
    	}
    	swapListElem(lst,leftPtr,right);
    	return leftPtr;
	}
	
	private static void swapListElem(LinkedList<SortableElem> lst, int idx1, int idx2){
		SortableElem tempElem1,tempElem2;
		
		tempElem1=lst.get(idx1);
		tempElem2=lst.get(idx2);
		lst.add(idx1,tempElem2);
		lst.remove(idx1+1);
		lst.add(idx2,tempElem1);
		lst.remove(idx2+1);
	}
	
	//remove and merge duplicate string from LinkedList<SortableIdxStr>
	public static void removeDup(LinkedList<SortableIdxStr> lst){
		String srcStr;
		
		for(int i=0;i<lst.size();i++){
			srcStr=lst.get(i).strElem;
			for(int j=i+1;j<lst.size();j++){
				if(lst.get(j).strElem.equals(srcStr)){
					lst.get(i).sortElem+=lst.get(j).sortElem;
					lst.remove(j);
					j--;
				}
			}
		}
	}
	
	
	public static StatResult statistic(double[] Data){
		StatResult res=new StatResult();
		double resMean=0.0, resStdErr=0.0;
		
		for(int i=0;i<Data.length;i++){
			resMean+=Data[i];
		}
		resMean=resMean/Data.length;
		
		for(int i=0;i<Data.length;i++){
			resStdErr+=(Data[i]-resMean)*(Data[i]-resMean);
		}
		resStdErr=Math.sqrt(resStdErr/(Data.length-1));
		
		res.mean=resMean;
		res.stdErr=resStdErr;
		
		return res;
	}

}
