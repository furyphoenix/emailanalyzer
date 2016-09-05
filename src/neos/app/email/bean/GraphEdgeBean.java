package neos.app.email.bean;

public class GraphEdgeBean {
	private int edgeId;
	private int fromNodeId;
	private int toNodeId;
	private boolean directed;
	private int edgeWidth;
	private String strokeType;
	private int edgeColor;
	private boolean visible;

	public GraphEdgeBean(){
		
	}

	public int getEdgeId() {
		return edgeId;
	}

	public void setEdgeId(int edgeId) {
		this.edgeId = edgeId;
	}

	public int getFromNodeId() {
		return fromNodeId;
	}

	public void setFromNodeId(int fromNodeId) {
		this.fromNodeId = fromNodeId;
	}

	public int getToNodeId() {
		return toNodeId;
	}

	public void setToNodeId(int toNodeId) {
		this.toNodeId = toNodeId;
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	public int getEdgeWidth() {
		return edgeWidth;
	}

	public void setEdgeWidth(int edgeWidth) {
		this.edgeWidth = edgeWidth;
	}

	public String getStrokeType() {
		return strokeType;
	}

	public void setStrokeType(String edgeType) {
		this.strokeType = edgeType;
	}

	public int getEdgeColor() {
		return edgeColor;
	}

	public void setEdgeColor(int edgeColor) {
		this.edgeColor = edgeColor;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
