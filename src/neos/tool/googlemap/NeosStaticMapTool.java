package neos.tool.googlemap;

import java.util.ArrayList;
import java.util.List;

import neos.tool.googlemap.staticmap.GoogleMapUrlBuilder;
import neos.tool.googlemap.staticmap.Location;
import neos.tool.googlemap.staticmap.MapParameter;
import neos.tool.googlemap.staticmap.Mark;

public class NeosStaticMapTool {
	private MapParameter para;
	
	public NeosStaticMapTool(){
		para=new MapParameter();
		para.setLanguage("zh-CN");
		para.setSize(512, 512);
		para.setSensor(true);
	}
	
	public void addMark(float lat, float lng, String label, String color){
		Location loc=new Location(lat, lng);
		List<Location> locList=new ArrayList<Location>();
		locList.add(loc);
		Mark mark=new Mark(color, label, "normal", locList);
		para.addMark(mark);
	}
	
	public String getUrl(){
		GoogleMapUrlBuilder builder=new GoogleMapUrlBuilder(para);
		
		return builder.generateUrl();
	}
	
	
}
