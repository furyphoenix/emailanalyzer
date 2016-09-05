package neos.tool.googlemap.staticmap;

import java.util.ArrayList;
import java.util.List;

public class Path {

	private String color = "blue";
	private int weight = 5;
	private String fillcolor = "0xFFFF0033";
	private List<Location> locationList = new ArrayList<Location>();
	
	public Path() {
		//
	}
	
	public Path(String color, int weight, String fillcolor, ArrayList<Location> locationList) {
		this.color = color;
		this.weight = weight;
		this.fillcolor = fillcolor;
		this.locationList = locationList;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getFillcolor() {
		return fillcolor;
	}

	public void setFillcolor(String fillcolor) {
		this.fillcolor = fillcolor;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}
	
	public void addLocation(Location l) {
		this.locationList.add(l);
	}
	
	public void addLocation(float latitude, float longitude) {
		Location l = new Location(latitude,longitude);
		this.locationList.add(l);
	}
	
	public void addLocation(String address) {
		Location l = new Location(address);
		this.locationList.add(l);
	}
	
}

