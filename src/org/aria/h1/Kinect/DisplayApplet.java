package org.aria.h1.Kinect;

import processing.core.PApplet;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.PImage;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.awt.geom.Point2D;

public class DisplayApplet extends PApplet {
	/**
	 * Generated serial UID
	 */
	private static final long serialVersionUID = -4077388692790706464L;
	private PImage image;
	private ConcurrentLinkedQueue<Skeleton> skeletons;
	private Map<Long, LinkedList<Point2D.Float>> handCoordinates;
	private SimpleOpenNI context;
	
	public DisplayApplet(){
		skeletons = new ConcurrentLinkedQueue<Skeleton>();
	}


	public void setup() {
		size(640, 480);
		background(250,0,0);
		smooth();
	}
	
	public void draw() {
		pushStyle();
		noFill();
		setLocation(0, 0);
		if (image != null)
			image(image, 0, 0, image.width, image.height);

		for(Skeleton skeleton : skeletons)
			if (skeleton != null && skeleton != Constants.NULL_SKELETON)
				skeleton.draw(this);
		popStyle();
		smooth();
		
		/*
		int idx = 0;
		if (handCoordinates != null) {
			strokeWeight(2);
			for (Long handId: handCoordinates.keySet()) {
				beginShape();
				switch(idx % 4) {
					case 0: stroke(255, 0, 0); break;
					case 1: stroke(0, 255, 0); break;
					case 2: stroke(0, 0, 255); break;
					case 3: stroke(255, 255, 0); break;
				}
				int pointNum = 0;
				for (Point2D.Float handPosition: handCoordinates.get(handId)) {
					if (pointNum == 0)
						strokeWeight(8);
					else
						strokeWeight(2);
					vertex(handPosition.x, handPosition.y);
					pointNum++;
				}
				endShape();
				idx++;
			}
		}
		popStyle();
		
		if (skeleton != null && skeleton != Constants.NULL_SKELETON)
			skeleton.draw(this);
		
		if (displayRightArrow) {
			PImage arrow = loadImage("/home/aismail/Desktop/right-arrow-inv.png");
			image(arrow, 200, 200);
			numTimesDisplayedRightArrow++;
			if (numTimesDisplayedRightArrow == 3) {
				displayRightArrow = false;
			}
		}
		*/
	}
	
	public void dispose() {
		
	}
	
	public void setImage(PImage image) {
		this.image = image;
	}

	public void addSkeleton(Skeleton skeleton){
		this.skeletons.add(skeleton);
	}
	
	public void clearSkeletons(){
		this.skeletons.clear();
	}

	public void setHandCoordinates(Map<Long, LinkedList<Point2D.Float>> handCoordinates) {
		this.handCoordinates = handCoordinates;
	}
	
	public void setContext(SimpleOpenNI context) {
		this.context = context;
	}

	//--------------------------------------------------------------------------
	
	public void onNewUser(int userId)
	{
	  System.out.println("onNewUser - userId: " + userId);
	  System.out.println("  start pose detection");
	  
	  context.startPoseDetection("Psi",userId);
	}

	public void onLostUser(int userId)
	{
	  System.out.println("onLostUser - userId: " + userId);
	}

	public void onStartCalibration(int userId)
	{
	  System.out.println("onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull)
	{
	  System.out.println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);
	  
	  if (successfull) 
	  { 
	    System.out.println("  User calibrated !!!");
	    context.startTrackingSkeleton(userId); 
	  } 
	  else 
	  { 
	    System.out.println("  Failed to calibrate user !!!");
	    System.out.println("  Start pose detection");
	    context.startPoseDetection("Psi",userId);
	  }
	}

	public void onStartPose(String pose,int userId)
	{
	  System.out.println("onStartPose - userId: " + userId + ", pose: " + pose);
	  System.out.println(" stop pose detection");
	  
	  context.stopPoseDetection(userId); 
	  context.requestCalibrationSkeleton(userId, true);
	}

	public void onEndPose(String pose,int userId)
	{
	  System.out.println("onEndPose - userId: " + userId + ", pose: " + pose);
	}
}