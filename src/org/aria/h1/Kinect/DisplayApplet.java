package org.aria.h1.Kinect;

import processing.core.PApplet;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.PImage;
import java.util.Map;
import java.util.TreeMap;

public class DisplayApplet extends PApplet {

	private static final long serialVersionUID = -4077388692790706464L;

	private PImage image;
	private SimpleOpenNI context;
	private Map<Integer, Skeleton> skeletons = new TreeMap<Integer, Skeleton>();

	public void setup() {
		size(640, 480);
		background(250, 0, 0);
		smooth();
	}

	public void draw() {
		pushStyle();
		noFill();
		setLocation(0, 0);
		if (image != null)
			image(image, 0, 0, image.width, image.height);

		synchronized (skeletons) {
			for (Skeleton skeleton : skeletons.values())
				if (skeleton.isTracking())
					skeleton.draw(this);
		}

		popStyle();
		smooth();
	}

	public void dispose() {
	}

	public void setImage(PImage image) {
		this.image = image;
	}

	public void setContext(SimpleOpenNI context) {
		this.context = context;
	}

	private void createSkeleton(int userId) {
		synchronized (skeletons) {
			skeletons.put(userId, new Skeleton(context, userId));
		}
	}

	private void removeSkeleton(int userId) {
		synchronized (skeletons) {
			skeletons.remove(userId);
		}
	}

	public void updateSkeletons() {
		synchronized (skeletons) {
			for (Skeleton skeleton : skeletons.values())
				skeleton.update();
		}
	}

	public void onNewUser(int userId) {
		System.out.println("onNewUser - userId: " + userId);
		createSkeleton(userId);
		System.out.println("  Starting pose detection.");
		context.startPoseDetection("Psi", userId);
	}

	public void onLostUser(int userId) {
		System.out.println("onLostUser - userId: " + userId);
		removeSkeleton(userId);
	}

	public void onStartPose(String pose, int userId) {
		System.out.println("onStartPose - userId: " + userId + ", pose: " + pose);
		System.out.println("  Pose detected. Calibrating skeleton.");
		context.stopPoseDetection(userId);
		context.requestCalibrationSkeleton(userId, true);
	}

	public void onEndPose(String pose, int userId) {
		System.out.println("onEndPose - userId: " + userId + ", pose: " + pose);
	}

	public void onStartCalibration(int userId) {
		System.out.println("onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		System.out.println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);
		if (successfull) {
			System.out.println("  Skeleton calibrated.");
			context.startTrackingSkeleton(userId);
		} else {
			System.out.println("  Failed to calibrate skeleton.");
			System.out.println("  Restarting pose detection.");
			context.startPoseDetection("Psi", userId);
		}
	}

}
