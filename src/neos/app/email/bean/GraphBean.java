package neos.app.email.bean;

import java.util.List;

public class GraphBean {
	private List<GraphNodeBean> nodeList;
	private List<GraphEdgeBean> edgeList;
	
	public GraphBean(){
		
	}

	public List<GraphNodeBean> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<GraphNodeBean> nodeList) {
		this.nodeList = nodeList;
	}

	public List<GraphEdgeBean> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(List<GraphEdgeBean> edgeList) {
		this.edgeList = edgeList;
	}
	
	

}
