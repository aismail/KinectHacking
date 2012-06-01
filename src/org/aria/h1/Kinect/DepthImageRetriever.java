package org.aria.h1.Kinect;

public class DepthImageRetriever extends Thread {
	private DisplayApplet applet;
	private Kinect kinect;
	
	public DepthImageRetriever(DisplayApplet applet, Kinect kinect) {
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
			
			kinect.updateContext();
			applet.setImage(kinect.getDepthImage());
		}
	}
}