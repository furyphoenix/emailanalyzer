package neos.app.email.bean;

public class GraphNodeBean {
	private int nodeId;
	private String nodeName;
	private int nodeSize;
	private String nodeShape;
	private int nodeColor;
	private int nodePosX;
	private int nodePosY;
	private boolean picked;
	private boolean visible;

	public GraphNodeBean() {

	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getNodeSize() {
		return nodeSize;
	}

	public void setNodeSize(int nodeSize) {
		this.nodeSize = nodeSize;
	}

	public String getNodeShape() {
		return nodeShape;
	}

	public void setNodeShape(String nodeShape) {
		this.nodeShape = nodeShape;
	}

	public int getNodeColor() {
		return nodeColor;
	}

	public void setNodeColor(int nodeColor) {
		this.nodeColor = nodeColor;
	}

	public int getNodePosX() {
		return nodePosX;
	}

	public void setNodePosX(int nodePosX) {
		this.nodePosX = nodePosX;
	}

	public int getNodePosY() {
		return nodePosY;
	}

	public void setNodePosY(int nodePosY) {
		this.nodePosY = nodePosY;
	}

	public boolean isPicked() {
		return picked;
	}

	public void setPicked(boolean picked) {
		this.picked = picked;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
