package neos.app.gui;

public class SimpleNeosVertex implements NeosVertex {
	private String name;
	
	public SimpleNeosVertex(String name){
		this.name=name;
	}
	
	@Override
	public String getLabel() {
		return name;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof SimpleNeosVertex)){
			return false;
		}
		
		SimpleNeosVertex v=(SimpleNeosVertex) o;
		if(name.equals(v.name)){
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode(){
		int result=17;
		result=result*31+name.hashCode();
		
		return result;
	}

	@Override
	public String toString(){
		return name;
	}

}
