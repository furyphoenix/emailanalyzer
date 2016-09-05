package neos.app.gui;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.Graph;

public class HitsLayout<V, E> extends AbstractLayout<V, E> {
	

	public HitsLayout(Graph<V,E> graph){
		this(graph, new StaticEdgeWeightsTransformer<E>(), 0.0);
	}
	
	public HitsLayout(Graph<V, E> graph, Transformer<E, Double> edge_weights, double alpha) {
		super(graph);
		
	}
	
	public void setDimension(){
		
	}

	@Override
	public void initialize() {
		Dimension d = getSize();
		if(d!=null){
			double height = d.getHeight();
			double width = d.getWidth();
			
			
		}
		
	}

	@Override
	public void reset() {
		initialize();
		
	}
	
	
		
	
}
