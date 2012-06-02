package org.aria.h1.Kinect.gestures;

import java.util.LinkedList;
import java.util.List;

public abstract class GestureDetector {

	protected String id;
	protected PointTracker pointTracker;

	protected List<Point> points;

	public GestureDetector(String id, PointTracker pointTracker) {
		this.id = id;
		this.pointTracker = pointTracker;
		points = new LinkedList<Point>();
	}

	public String getId() {
		return id;
	}

	public void sample() {
		points.add(pointTracker.samplePoint());
	}

	public abstract boolean detect();

}
