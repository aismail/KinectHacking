package org.aria.h1.Kinect;

import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.XnVFlowRouter;
import SimpleOpenNI.XnVSessionManager;
import processing.core.PImage;

public class Kinect {
	private SimpleOpenNI context;
	private XnVSessionManager sessionManager;
	private XnVFlowRouter flowRouter;
	
	/*
	 * Initialize a new Kinect, given an Applet.
	 * 
	 * TODO: analyze whether we really need
	 * the Applet dependency and ditch it.
	 */
	public Kinect(DisplayApplet applet) {
		SimpleOpenNI  context;
		XnVSessionManager sessionManager;
		XnVFlowRouter flowRouter;
		
		context = new SimpleOpenNI(applet, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
		context.setMirror(true); 
		context.enableDepth();
		context.enableRGB();
		context.enableGesture();
		context.enableHands();
		this.setContext(context);
		context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
		
		sessionManager = context.createSessionManager("Click,Wave", "RaiseHand");
		flowRouter = new XnVFlowRouter();
		sessionManager.AddListener(flowRouter);
		this.setFlowRouter(flowRouter);
		this.setSessionManager(sessionManager);
		applet.setContext(context);
	}
	
	/*
	 * Updates the Kinect context
	 */
	public void updateContext() {
		this.getContext().update();
		this.getContext().update(this.getSessionManager());
	}
	
	/*
	 * Retrieve the next depth image
	 */
	public PImage getDepthImage() {
		return this.getContext().depthImage();
	}
	
	// Auto-generated getters and setters
	public SimpleOpenNI getContext() {
		return context;
	}
	public void setContext(SimpleOpenNI context) {
		this.context = context;
	}
	public XnVSessionManager getSessionManager() {
		return sessionManager;
	}
	public void setSessionManager(XnVSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	public XnVFlowRouter getFlowRouter() {
		return flowRouter;
	}
	public void setFlowRouter(XnVFlowRouter flowRouter) {
		this.flowRouter = flowRouter;
	}
}