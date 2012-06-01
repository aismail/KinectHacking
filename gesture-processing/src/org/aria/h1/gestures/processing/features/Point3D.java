package org.aria.h1.gestures.processing.features;

public class Point3D {
	double x;
	double y;
	double z;
	
	public Point3D() {
	}
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D(double[] coordinates) {
		this.x = coordinates[0];
		this.y = coordinates[1];
		this.z = coordinates[2];
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public static Point3D subtract(Point3D a, Point3D b) {
		return new Point3D(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	
	public static Point3D add(Point3D a, Point3D b) {
		return new Point3D(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	
	public static double distance(Point3D a, Point3D b) {
		Point3D dist = subtract(a, b);
		return Math.sqrt(dist.x * dist.x + dist.y * dist.y + dist.z * dist.z);
	}
}
