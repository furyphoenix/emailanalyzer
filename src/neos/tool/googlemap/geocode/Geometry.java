package neos.tool.googlemap.geocode;

public class Geometry {
	public final Location location;
	public final LocationType locationType;
	public final Viewport viewport;
	
	public Geometry(Location l, LocationType t, Viewport v){
		location=l;
		locationType=t;
		viewport=v;
	}
}
