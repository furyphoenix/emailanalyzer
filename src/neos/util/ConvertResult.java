package neos.util;

public class ConvertResult<T> {
	public final T result;
	public final float prob;
	
	public ConvertResult(T res, float p){
		this.result=res;
		this.prob=p;
	}
}
