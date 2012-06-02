package org.aria.h1.Kinect;

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
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// Perform an internal update
			kinect.updateContext();

			// Get the depth image
			applet.setImage(kinect.getDepthImage());

			// Update skeletons
			applet.updateSkeletons();
		}
	}

}
