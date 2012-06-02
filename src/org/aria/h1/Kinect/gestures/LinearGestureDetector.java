package org.aria.h1.Kinect.gestures;

import java.util.Iterator;

import javax.vecmath.Vector3d;

public class LinearGestureDetector extends GestureDetector {

	public static final long MAX_DURATION = 1500;
	public static final long MIN_REDECTION_TIME = 1000;
	public static final int MIN_POINTS = 5;
	public static final double MIN_GESTURE_SIZE = 400.0;
	public static final double TOLERANCE = 0.1;

	private long lastDetection;

	public LinearGestureDetector(String id, PointTracker pointTracker) {
		super(id, pointTracker);
		lastDetection = 0;
	}

	@Override
	public boolean detect() {
		long now = System.currentTimeMillis();
		if (now - lastDetection < MIN_REDECTION_TIME) {
			points.clear();
			return false;
		}
		Iterator<Point> it = points.iterator();
		while (it.hasNext()) {
			Point pt = it.next();
			if (now - pt.getTimestamp() > MAX_DURATION)
				it.remove();
		}
		if (points.size() < MIN_POINTS)
			return false;
		Vector3d first = points.get(0).getPosition();
		Vector3d last = points.get(points.size() - 1).getPosition();
		Vector3d direction = new Vector3d();
		Vector3d unity = new Vector3d(1.0, 0.0, 0.0);
		direction.sub(last, first);
		double angle = direction.angle(unity);
		double size = direction.length();
		if (size < MIN_GESTURE_SIZE)
			return false;
		for (int i = 1; i < points.size() - 1; i++) {
			Vector3d p = points.get(i).getPosition();
			Vector3d v1 = new Vector3d();
			v1.sub(p, first);
			Vector3d v2 = new Vector3d();
			v1.sub(p, last);
			Vector3d cross = new Vector3d();
			cross.cross(v1, v2);
			double dist = cross.length() / direction.length();
			if (dist / size > TOLERANCE)
				return false;
		}
		System.out.println(id + ": size=" + size + " angle=" + angle / Math.PI * 180.0);
		points.clear();
		lastDetection = now;
		return true;
	}

}
