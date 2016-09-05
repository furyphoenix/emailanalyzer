package neos.app.gui;

public interface NeosEdge<V extends NeosVertex> {
	V getFrom();
	V getTo();
	double getWeight();
	void setWeight(double val);
}
