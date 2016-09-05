package neos.app.gui;

import org.apache.commons.collections15.Transformer;

public class StaticEdgeWeightsTransformer<E> implements Transformer<E, Double> {
	public StaticEdgeWeightsTransformer(){
		
	}
	
	@Override
	public Double transform(E arg0) {
		return 1.0;
	}
}
