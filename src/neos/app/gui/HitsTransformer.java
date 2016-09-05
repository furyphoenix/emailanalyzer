package neos.app.gui;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.Graph;

public class HitsTransformer<V,E> implements Transformer<V, Point2D>{
	private HITS<V,E> hits;
	private double maxAuth=0.0;
	private double maxHub=0.0;
	private Dimension size=new Dimension(500,500);
	
	public HitsTransformer(Graph<V,E> g){
		this(g, new StaticEdgeWeightsTransformer<E>(), 0.0);
	}
	
	public HitsTransformer(Graph<V, E> g, Transformer<E, Double> t, double a){
		hits=new HITS<V, E>(g,t,a);
		hits.evaluate();
		for(V v:g.getVertices()){
			HITS.Scores score=hits.getVertexScore(v);
			if(score.authority>maxAuth){
				maxAuth=score.authority;
			}
			if(score.hub>maxHub){
				maxHub=score.hub;
			}
		}
	}
	
	public void setDimension(Dimension dim){
		this.size=dim;
	}
	
	@Override
	public Point2D transform(V v) {
		HITS.Scores score=hits.getVertexScore(v);
		double x=size.getWidth()*0.05+size.getWidth()*score.hub/maxHub*0.9;
		double y=size.getHeight()*0.05+size.getHeight()*(1.0-score.authority/maxAuth)*0.9;
		return new Point2D.Double(x,y);
	}
	
}