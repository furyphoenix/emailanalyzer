package neos.tool.googlemap.staticmap;

import java.util.HashMap;
import java.util.List;

public class GoogleMapUrlBuilder {
	
	private MapParameter parameter = new MapParameter();

	public GoogleMapUrlBuilder() {
		
	}
	
	public GoogleMapUrlBuilder(MapParameter parameter) {
		this.parameter = parameter;
	}
	
	
	
	public MapParameter getParameter() {
		return parameter;
	}

	public void setParameter(MapParameter parameter) {
		this.parameter = parameter;
	}

	public String generateUrl() {
		StringBuilder url = new StringBuilder();
		url.append("http://maps.google.com/maps/api/staticmap?");
		url.append(sizeCode());
		url.append("&");
		url.append(markCode());
		url.append("&");
		url.append(zoomCode());
		url.append("&");
		url.append(sensorCode());
		return url.toString();
	}
	
	// size形式为          size=512x512
	public String sizeCode() {
		StringBuilder sizecode = new StringBuilder();
		HashMap<String, String> sizeMap = this.parameter.getSize();
		String width = sizeMap.get("width");
		String height = sizeMap.get("height");
		
		sizecode.append("size=");
		sizecode.append(width);
		sizecode.append("x");
		sizecode.append(height);
		
		return new String(sizecode);
	}
	
	//markers=color:blue|label:S|40.702147,-74.015794&markers=color:green|label:G|40.711614,-74.012318
	//&markers=color:red|color:red|label:C|40.718217,-73.998284
	public String markCode() {
		StringBuilder markcode = new StringBuilder();
		
		List<Mark> markList = this.parameter.getMarkList();
		for(Mark m : markList) {
			String markStr = simpleMarkCode(m);
			markcode.append(markStr);
			markcode.append("&");
		}
		markcode.delete(markcode.length()-1, markcode.length());
		return new String(markcode);
		
	}
	
	//mark的形式为 markers=size:mid|color:red|San+Francisco,CA|Oakland,CA|San+Jose,CA
	//markers=color:blue|label:S|62.107733,-145.541936
	public String simpleMarkCode(Mark mark) {
		StringBuilder markString = new StringBuilder();
		markString.append("markers=");
		
		String size = mark.getSize();
		String color = mark.getColor();
		String label = mark.getLabel();
		if(!size.equals("")) {
			markString.append("size:");
			markString.append(size);
			markString.append("|");
		}
		if(!color.equals("")) {
			markString.append("color:");
			markString.append(color);
			markString.append("|");
		}
		if(!label.equals("")) {
			markString.append("label:");
			markString.append(label);
			markString.append("|");
		}
		
		List<Location> locationList = mark.getLocationList();
		for(Location l : locationList) {
			String locationStr = locationCode(l);
			markString.append(locationStr);
			markString.append("|");
		}
		markString.delete(markString.length()-1, markString.length());
		return new String(markString);
	}
	
	//San+Francisco,CA
	//62.107733,-145.541936
	public String locationCode(Location address) {
		StringBuilder locationCode = new StringBuilder();
		String location = address.getLocation();
		if(!location.equals("")) {
			locationCode.append(location);
		}
		else {
			String latitude = new Double(address.getLatitude()).toString();
			String longitude = new Double(address.getLongitude()).toString();
			locationCode.append(latitude);
			locationCode.append(",");
			locationCode.append(longitude);
		}
		return new String(locationCode);
	}
	
	public String zoomCode(){
		if(parameter.getMarkList().size()==1){
			System.out.println("auto set zoom level!");
			return "zoom="+parameter.getZoom();
			
		}else{
			return new String();
		}
	}
	
	//sensor 形式为   sensor=true   或者     sensor=false
	public String sensorCode() {
		boolean sensor = this.parameter.getSensor();
		StringBuilder sensorcode = new StringBuilder();
		
		sensorcode.append("sensor=");
		if(sensor) {
			sensorcode.append("true");
		}
		else {
			sensorcode.append("false");
		}
		
		return new String(sensorcode);
	}
	
	
}
