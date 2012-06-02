package org.aria.h1.Kinect.gestures;

import SimpleOpenNI.XnSkeletonJointPosition;

public class SkeletonJointPositionTracker implements PointTracker {

	private XnSkeletonJointPosition skeletonJointPosition;

	public SkeletonJointPositionTracker(XnSkeletonJointPosition skeletonJointPosition) {
		this.skeletonJointPosition = skeletonJointPosition;
	}

	@Override
	public Point samplePoint() {
		return new Point(skeletonJointPosition.getPosition(), System.currentTimeMillis());
	}

}
