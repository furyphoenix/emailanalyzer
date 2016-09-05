package neos.tool.googlemap.staticmap;

//~--- JDK imports ------------------------------------------------------------

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Mark {
    private String         color        = "";
    private String         label        = "";
    private String         size         = "";
    private List<Location> locationList = new ArrayList<Location>();

    public Mark() {

        //
    }

    public Mark(String color, String label, String size, List<Location> locationList) {
        this.color        = color;
        try {
			this.label        = URLEncoder.encode(label,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.size         = size;
        this.locationList = locationList;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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
        Location l = new Location(latitude, longitude);

        this.locationList.add(l);
    }

    public void addLocation(String address) {
        Location l = new Location(address);

        this.locationList.add(l);
    }
}
