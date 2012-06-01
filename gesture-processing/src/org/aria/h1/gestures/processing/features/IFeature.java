package org.aria.h1.gestures.processing.features;

public interface IFeature {
	public int getDimension();
	public double[] toVector();
	public void setVector(double[] vals);
}
