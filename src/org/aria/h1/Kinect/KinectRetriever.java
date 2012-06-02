package org.aria.h1.Kinect;

import SimpleOpenNI.IntVector;

public class KinectRetriever extends Thread {
	private DisplayApplet applet;
	private Kinect kinect;
	
	public KinectRetriever(DisplayApplet applet, Kinect kinect) {
		this.applet = applet;
		this.kinect = kinect;
	}
	
	public void run() {
		while (true) {
			try {
				// Sleep for 100 ms to avoid 100% CPU usage
				// and trigger a process re-schedule.
				Thread.sleep(100);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			// Perform an internal update
			kinect.updateContext();
			
			// Get the depth image
			applet.setImage(kinect.getDepthImage());
			
			// Get skeleton data
			IntVector users = new IntVector();
			kinect.getContext().getUsers(users);
			Skeleton skeleton = Constants.NULL_SKELETON;
			
			applet.clearSkeletons();

			if (users.size() > 0) {
				System.out.println("I can haz user!");
				for (int i = 0; i < users.size(); i++) {
					int user_id = users.get(i);
					if (kinect.getContext().isTrackingSkeleton(user_id)) {
						System.out.println("We are tracking skeleton");
						skeleton = new Skeleton(kinect, user_id);
						applet.addSkeleton(skeleton);
					} else {
						System.out.println("We are NOT tracking skeleton");
					}
				}
			}
			
		}
	}
}