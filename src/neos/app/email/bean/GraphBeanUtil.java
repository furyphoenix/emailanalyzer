package neos.app.email.bean;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Graph转换为JavaBean的工具类
 * 
 * @author phoenix
 * 
 */
public class GraphBeanUtil<V, E> {
	private Transformer<V, String> nodeNameTransformer = null;
	private Transformer<V, Integer> nodeSizeTransformer = null;
	private Transformer<V, Integer> nodeColorTransformer = null;
	private Transformer<V, String> nodeShapeTransformer = null;
	private Transformer<V, Point2D> layoutTransformer = null;
	private Transformer<V, Boolean> nodePickTransformer = null;
	private Transformer<V, Boolean> nodeVisiableTransformer = null;
	private Transformer<E, Integer> edgeWidthTransformer = null;
	private Transformer<E, String> edgeStrokeTransformer = null;
	private Transformer<E, Integer> edgeColorTransformer = null;
	private Transformer<E, Boolean> edgeVisiableTransformer = null;

	public GraphBeanUtil() {

	}

	/**
	 * 设置由节点获取节点名称的转换器
	 * @param nodeNameTransformer
	 */
	public void setNodeNameTransformer(Transformer<V, String> nodeNameTransformer) {
		this.nodeNameTransformer = nodeNameTransformer;
	}

	/**
	 * 设置由节点获取节点大小的转换器
	 * @param nodeSizeTransformer
	 */
	public void setNodeSizeTransformer(Transformer<V, Integer> nodeSizeTransformer) {
		this.nodeSizeTransformer = nodeSizeTransformer;
	}

	/**
	 * 设置由节点获取节点颜色RGB值的转换器
	 * @param nodeColorTransformer
	 */
	public void setNodeColorTransformer(Transformer<V, Integer> nodeColorTransformer) {
		this.nodeColorTransformer = nodeColorTransformer;
	}

	/**
	 * 设置由节点获取节点形状的转换器
	 * @param nodeShapeTransformer
	 */
	public void setNodeShapeTransformer(Transformer<V, String> nodeShapeTransformer) {
		this.nodeShapeTransformer = nodeShapeTransformer;
	}

	/**
	 * 设置由节点获取节点布局的转换器
	 * @param layoutTransformer
	 */
	public void setLayoutTransformer(Transformer<V, Point2D> layoutTransformer) {
		this.layoutTransformer = layoutTransformer;
	}

	/**
	 * 设置由节点获取节点选取状态的转换器
	 * @param nodePickTransformer
	 */
	public void setNodePickTransformer(Transformer<V, Boolean> nodePickTransformer) {
		this.nodePickTransformer = nodePickTransformer;
	}

	/**
	 * 设置由节点获取节点是否可见的转换器
	 * @param nodeVisiableTransformer
	 */
	public void setNodeVisiableTransformer(
			Transformer<V, Boolean> nodeVisiableTransformer) {
		this.nodeVisiableTransformer = nodeVisiableTransformer;
	}

	/**
	 * 设置由连边获取连线宽度的转换器
	 * @param edgeWidthTransformer
	 */
	public void setEdgeWidthTransformer(Transformer<E, Integer> edgeWidthTransformer) {
		this.edgeWidthTransformer = edgeWidthTransformer;
	}

	/**
	 * 设置由连边获取连边线型的转换器
	 * @param edgeStrokeTransformer
	 */
	public void setEdgeStrokeTransformer(
			Transformer<E, String> edgeStrokeTransformer) {
		this.edgeStrokeTransformer = edgeStrokeTransformer;
	}

	/**
	 * 设置由连边获取连边颜色RGB值的转换器
	 * @param edgeColorTransformer
	 */
	public void setEdgeColorTransformer(Transformer<E, Integer> edgeColorTransformer) {
		this.edgeColorTransformer = edgeColorTransformer;
	}

	/**
	 * 设置获取连边是否可见的转换器
	 * @param edgeVisiableTransformer
	 */
	public void setEdgeVisiableTransformer(
			Transformer<E, Boolean> edgeVisiableTransformer) {
		this.edgeVisiableTransformer = edgeVisiableTransformer;
	}

	/**
	 * 把图Graph转换为GraphBean
	 * @param graph
	 * @return
	 */
	public GraphBean convert(Graph<V, E> graph) {
		GraphBean graphBean = new GraphBean();
		List<GraphNodeBean> nodeList = new ArrayList<GraphNodeBean>();
		List<GraphEdgeBean> edgeList = new ArrayList<GraphEdgeBean>();
		graphBean.setNodeList(nodeList);
		graphBean.setEdgeList(edgeList);

		Map<V, Integer> nodeIdMap = new Hashtable<V, Integer>();

		int i = 0;
		for (V v : graph.getVertices()) {
			GraphNodeBean nodeBean = new GraphNodeBean();

			nodeBean.setNodeId(i);
			nodeIdMap.put(v, i++);

			if (nodeNameTransformer != null) {
				nodeBean.setNodeName(nodeNameTransformer.transform(v));
			}
			if (nodeSizeTransformer != null) {
				nodeBean.setNodeSize(nodeSizeTransformer.transform(v));
			}
			if (nodeColorTransformer != null) {
				nodeBean.setNodeColor(nodeColorTransformer.transform(v));
			}
			if (nodeShapeTransformer != null) {
				nodeBean.setNodeShape(nodeShapeTransformer.transform(v));
			}

			if (layoutTransformer != null) {
				Point2D pos = layoutTransformer.transform(v);
				nodeBean.setNodePosX((int) pos.getX());
				nodeBean.setNodePosY((int) pos.getY());
			}

			if (nodePickTransformer != null) {
				nodeBean.setPicked(nodePickTransformer.transform(v));
			}

			if (nodeVisiableTransformer != null) {
				nodeBean.setVisible(nodeVisiableTransformer.transform(v));
			}

			nodeList.add(nodeBean);
		}

		i = 0;
		for (E e : graph.getEdges()) {
			GraphEdgeBean edgeBean = new GraphEdgeBean();

			edgeBean.setEdgeId(i++);

			if (graph.getEdgeType(e) == EdgeType.DIRECTED) {
				edgeBean.setDirected(true);
				V fnode = graph.getSource(e);
				V tnode = graph.getDest(e);
				edgeBean.setFromNodeId(nodeIdMap.get(fnode));
				edgeBean.setToNodeId(nodeIdMap.get(tnode));
			} else {
				edgeBean.setDirected(false);
				Pair<V> nodePair = graph.getEndpoints(e);
				V node1 = nodePair.getFirst();
				V node2 = nodePair.getSecond();
				if (node1.hashCode() > node2.hashCode()) {
					edgeBean.setFromNodeId(nodeIdMap.get(node1));
					edgeBean.setToNodeId(nodeIdMap.get(node2));
				} else {
					edgeBean.setFromNodeId(nodeIdMap.get(node2));
					edgeBean.setToNodeId(nodeIdMap.get(node1));
				}

			}

			if (edgeWidthTransformer != null) {
				edgeBean.setEdgeWidth(edgeWidthTransformer.transform(e));
			}

			if (edgeColorTransformer != null) {
				edgeBean.setEdgeColor(edgeColorTransformer.transform(e));
			}

			if (edgeStrokeTransformer != null) {
				edgeBean.setStrokeType(edgeStrokeTransformer.transform(e));
			}

			if (edgeVisiableTransformer != null) {
				edgeBean.setVisible(edgeVisiableTransformer.transform(e));
			}

		}

		return graphBean;
	}
}
