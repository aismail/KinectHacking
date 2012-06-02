package org.aria.h1.Kinect;

import processing.core.PApplet;
import javax.swing.JFrame;

public class Main {	
	public static void main(String[] args) {
		try {
			DisplayApplet applet = new DisplayApplet();
			
			JFrame frame = new JFrame("Hand Follower");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(applet);
			frame.setSize(640, 480);
			applet.init();
			applet.start();
			
			Kinect kinect = new Kinect(applet);
			KinectRetriever retriever = new KinectRetriever(applet, kinect);
			retriever.start();
			
			frame.setVisible(true);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}