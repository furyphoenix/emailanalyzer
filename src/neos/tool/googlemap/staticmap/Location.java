package neos.tool.googlemap.staticmap;

public class Location {
	
	private String location = "";
	private float latitude = 0;
	private float longitude = 0;
	
	public Location() {
		//
	}
	
	public Location(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.location = "";
	}
	
	public Location(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	
}
