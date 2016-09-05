package neos.app.gui;

public class SimpleNeosEdge implements NeosEdge<SimpleNeosVertex> {
	final SimpleNeosVertex from;
	final SimpleNeosVertex to;
	double val;
	
	public SimpleNeosEdge(SimpleNeosVertex v1, SimpleNeosVertex v2, double val){
		this.from=v1;
		this.to=v2;
		this.val=val;
	}
	
	
	@Override
	public SimpleNeosVertex getFrom() {
		return from;
	}

	@Override
	public SimpleNeosVertex getTo() {
		return to;
	}

	@Override
	public double getWeight() {
		return val;
	}

	@Override
	public void setWeight(double val) {
		this.val=val;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof SimpleNeosEdge)){
			return false;
		}
		SimpleNeosEdge e=(SimpleNeosEdge)o;
		if((from.equals(e.from))&&(to.equals(to)&&(val==e.val))){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		int result=17;
		
		result=result*31+from.hashCode();
		result=result*31+to.hashCode();
		long f=Double.doubleToLongBits(val);
		result=result*31+(int)(f^(f>>>32));
		
		return result;
	}
	
	@Override
	public String toString(){
		return "("+from.toString()+", "+to.toString()+") "+val;
	}

}
