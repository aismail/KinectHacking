package org.aria.h1.gestures.processing.features;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author alex
 * Will store a tuple of type x-y-z, denoting the x-y-z position of a
 * skeleton point at a given time
 */
public class RawKinectFeature implements IFeature, Serializable {
	private static final long serialVersionUID = 1L;
	private Point3D point;
	private double[] vector = new double[3];
	
	public RawKinectFeature() {
	}

	public RawKinectFeature(Point3D point) {
		this.point = point;
		vector[0] = point.x;
		vector[1] = point.y;
		vector[2] = point.z;
	}
	
	public RawKinectFeature(double[] vector) {
		this.vector = vector;
		this.point = new Point3D(vector);
	}
	
	public RawKinectFeature(double x, double y, double z) {
		point = new Point3D(x, y, z);
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(vector);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RawKinectFeature other = (RawKinectFeature) obj;
		if (!Arrays.equals(vector, other.toVector())) return false;
		return true;
	}

	@Override
	public String toString() {
		return "RawKinectFeature [point=" + Arrays.toString(vector) + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		double[] pointClone = vector.clone();
		RawKinectFeature rfc = new RawKinectFeature(pointClone);
		return rfc;
	}

	public Point3D getPoint() {
		return point;
	}
	
	@Override
	public int getDimension() {
		return 3;
	}
	
	@Override
	public double[] toVector() {
		return vector;
	}

	@Override
	public void setVector(double[] vals) {
		this.vector = vals;
		point = new Point3D(vals);
	}
}
