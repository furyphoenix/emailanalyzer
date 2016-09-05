package neos.tool.googlemap.geocode;

import java.util.ArrayList;
import java.util.List;

public class GeocodeResponse {
	private final StatusCode status;
	private List<Result> resultList;
	
	public GeocodeResponse(StatusCode code){
		status=code;
		resultList=new ArrayList<Result> ();
	}
	
	public void addResult(Result result){
		resultList.add(result);
	}
	
	public StatusCode getStatusCode(){
		return status;
	}
	
	public List<Result> getResultList(){
		return resultList;
	}
}
