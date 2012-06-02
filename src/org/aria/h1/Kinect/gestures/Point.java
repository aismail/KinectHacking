package org.aria.h1.Kinect.gestures;

import javax.vecmath.Vector3d;

import SimpleOpenNI.XnVector3D;

public class Point {

	private Vector3d position;
	private long timestamp;

	public Point(Vector3d position, long timestamp) {
		this.position = position;
		this.timestamp = timestamp;
	}

	public Point(XnVector3D position, long timestamp) {
		this(new Vector3d(position.getX(), position.getY(), position.getZ()), timestamp);
	}

	public Vector3d getPosition() {
		return position;
	}

	public double getTimestamp() {
		return timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (position == null && other.position != null)
			return false;
		if (position != null && !position.equals(other.position))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + position + "@" + timestamp + "]";
	}

}
