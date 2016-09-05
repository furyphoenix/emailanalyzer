package neos.tool.googlemap.staticmap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapParameter {
	private final static int DefaultZoomLevel=14;
	
	/**
	 * centerMap 存储地图中心位置信息
	 * 包含纬度，经度，和具体地名
	 * 实际过程中只需给出经度，纬度 或者地名信息
	 * float latitude , float longitude , float location
	 */
	private Location center = new Location();
	
	//zoom 地图缩放参数
	private int zoom = DefaultZoomLevel;
	
	//size 定义地图大小 width,height(必须设置)
	private HashMap<String,String> sizeMap = new HashMap<String,String>();
	
	//format 定义生成地图图片的格式 gif,png,jpeg
	private String format = "PNG";
	
	//maptype 定义地图类型  roadmap、satellite、hybrid 和 terrain
	private String maptype = "roadmap";
	
	//mobile 指定是否在移动设备上显示地图
	private boolean mobile = false;
	
	//language 设置地图图块上显示标签时所用的语言 ， 仅支持部分国家和地区
	private String language = "";
	
	//sensor 指定请求静态地图的应用程序是否使用传感器确定用户的位置(必须设置),默认为false
	private boolean sensor = false;
	
	//visible 指定一个或多个即使不显示标记或其他指示器也应该在地图上保持可见的位置
	private List<Location> visibleLocation = new LinkedList<Location>();
	
	//path path 参数用于定义一个位置集合（包含一个或多个位置），这些位置由一条覆盖在地图图像上的路径连接
	private Path path = new Path();
	
	//markers 参数用于为某组位置定义一个标记集合（包含一个或多个标记）
	private List<Mark> markList = new LinkedList<Mark>();

	public MapParameter() {
		zoom = DefaultZoomLevel;
		format = "PNG";
		maptype = "roadmap";
		mobile = false;
		sensor = false;
		sizeMap = new HashMap<String,String>();
		sizeMap.put("width", "600");
		sizeMap.put("height", "600");
	}
	

	 /************************ Location parameter:center**********************/

	/**
	 * Define the center of the map use the latitude and longitude
	 * @param latitude(纬度)
	 * @param longitude(经度)
	 * Latitude and longitude have a precision to 6 decimal places.
	 * For example "40.714728,-73.998672"
	 * Precision beyond the 6 decimal places is ignored
	 * Latitude value between -90 and 90
	 * Longitude value between -180 and 180
	 */
	public void setCenter(float latitude, float longitude) {
		Location l = new Location(latitude,longitude);
		this.center = l;
	}
	
	/**
	 * @param location
	 * Define the center of the map use the location
	 */
	public void setCenter(String location) {
		Location l = new Location(location);
		this.center = l;
	}
	
	public void setCenter(Location l) {
		this.center = l;
	}
	
	public Location getCenter() {
		return center;
	}
	
	 /************************ Location parameter:zoom**********************/
	/**
	 * Define the zoom level of the map
	 * 可以使用从 0（最低缩放级别，在地图上可以看到整个世界）到 21+（可以看到建筑物个体）的缩放级别
	 * 默认使用14
	 */
	public void setZoom(int zoom) {
		if(zoom>=0 && zoom<=21) {
			this.zoom = zoom;
		}
		else {
			zoom = DefaultZoomLevel;
		}
	}
	
	public int getZoom() {
		return zoom;
	}
	
	 /************************ Map parameter:size**********************/
	/**
	 * 定义地图大小，可以设置宽和高两个参数
	 */
	public void setSize(int width, int height) {
		Integer w = new Integer(width);
		Integer h = new Integer(height);
		getSize().put("width", w.toString());
		getSize().put("height", h.toString());
	}
	
	public HashMap<String,String> getSize() {
		return sizeMap;
	}
	
	/************************ Map parameter:format**********************/
	/**
	 * 定义生成地图图片格式，包含 GIF、JPEG 和 PNG 类型
	 * 默认使用PNG格式
	 */
	public void setFormat(String format) {
		if(checkformat(format)) {
			this.format = format;
		}
		else {
			this.format = "png";
		}
	}
	
	public String getFormat() {
		return format;
	}
	
	public boolean checkformat(String format) {
		if(format.equals("png") || format.equals("png8") || format.equals("png32") || 
				format.equals("gif") || format.equals("jpg") || format.equals("jpg-baseline")) {
			return true;
		}
		return false;
		
	}
	
	/************************ Map parameter:maptype**********************/
	/**
	 * 设置地图类型， 一共有roadmap、satellite、hybrid 和 terrain四种类型
	 * 默认使用roadmap类型
	 */
	public void setMapType(String maptype) {
		if(checktype(maptype)) {
			this.maptype = maptype;
		}
		else {
			this.maptype = "roadmap";
		}
	}
	
	public String getMapType() {
		return maptype;
	}
	
	public boolean checktype(String type) {
		if(type.equals("rodemap") || type.equals("satellite") || 
				type.equals("hybrid") || type.equals("terrain")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/************************ Map parameter:mobile**********************/
	/**
	 * 指定是否在移动设备上显示地图 默认为fasle
	 */
	public void setMobile(boolean bool) {
		this.mobile = bool;
	}
	
	public boolean getMobile() {
		return mobile;
	}
	
	/************************ Map parameter:language**********************/
	/**
	 * language 设置地图图块上显示标签时所用的语言 ， 仅支持部分国家和地区
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getLanguage() {
		return language;
	}
	
	/************************ Report parameter:sensor**********************/
	/**
	 * 指定请求静态地图的应用程序是否使用传感器确定用户的位置(必须设置),默认为false
	 */
	public void setSensor(boolean bool) {
		this.sensor = bool;
	}
	
	public boolean getSensor() {
		return sensor;
	}
	
	/************************ Feature parameter:visible**********************/
	/**
	 * visible 指定一个或多个即使不显示标记或其他指示器也应该在地图上保持可见的位置
	 */
	public void addVisibleLocation(float latitude, float longitude) {
		Location l = new Location(latitude,longitude);
		visibleLocation.add(l);
	}
	
	public void addVisibleLocation(String address) {
		Location l = new Location(address);
		visibleLocation.add(l);
	}
	
	public void addVisibleLocation(Location l) {
		visibleLocation.add(l);
	}
	
	public void setVisibleLocation(LinkedList<Location> locationList) {
		this.visibleLocation = locationList;
	}
	
	public List<Location> getVisibleLocation() {
		return visibleLocation;
	}
	
	/************************ Feature parameter:path**********************/
	/**
	 * path 参数用于定义一个位置集合（包含一个或多个位置），这些位置由一条覆盖在地图图像上的路径连接
	 */
	public void setPath(Path p) {
		this.path = p;
	}
	
	public Path getPath() {
		return path;
	}

	
	/************************ Feature parameter:mark**********************/
	/**
	 * markers 参数用于为某组位置定义一个标记集合（包含一个或多个标记）。
	 */
	public List<Mark> getMarkList() {
		return markList;
	}


	public void setMarkList(List<Mark> markList) {
		this.markList = markList;
	}
	
	public void addMark(Mark mark) {
		this.markList.add(mark);
	}
	
	
	
	
	
}
