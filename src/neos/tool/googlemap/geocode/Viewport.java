package neos.tool.googlemap.geocode;

public class Viewport {
	public final Location southwest;
	public final Location northeast;
	
	public Viewport(Location sw, Location ne){
		southwest=sw;
		northeast=ne;
	}
}
