package org.aria.h1.Kinect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.aria.h1.Kinect.gestures.GestureDetector;
import org.aria.h1.Kinect.gestures.LinearGestureDetector;
import org.aria.h1.Kinect.gestures.PointTracker;
import org.aria.h1.Kinect.gestures.SkeletonJointPositionTracker;

import processing.core.PApplet;
import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.XnSkeletonJointPosition;
import SimpleOpenNI.XnVector3D;

/*
 * Represents the position of a user's skeleton.
 */
public class Skeleton {

	public static Color SKELETON_COLORS[] = { null, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA };

	private SimpleOpenNI context;
	private int userId;
	private boolean tracking;

	private XnSkeletonJointPosition head = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition neck = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition leftShoulder = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition leftElbow = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition leftHand = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition rightShoulder = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition rightElbow = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition rightHand = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition torso = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition leftHip = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition leftKnee = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition leftFoot = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition rightHip = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition rightKnee = new XnSkeletonJointPosition();
	private XnSkeletonJointPosition rightFoot = new XnSkeletonJointPosition();

	private XnVector3D head_projective = new XnVector3D();
	private XnVector3D neck_projective = new XnVector3D();
	private XnVector3D leftShoulder_projective = new XnVector3D();
	private XnVector3D leftElbow_projective = new XnVector3D();
	private XnVector3D leftHand_projective = new XnVector3D();
	private XnVector3D rightShoulder_projective = new XnVector3D();
	private XnVector3D rightElbow_projective = new XnVector3D();
	private XnVector3D rightHand_projective = new XnVector3D();
	private XnVector3D torso_projective = new XnVector3D();
	private XnVector3D leftHip_projective = new XnVector3D();
	private XnVector3D leftKnee_projective = new XnVector3D();
	private XnVector3D leftFoot_projective = new XnVector3D();
	private XnVector3D rightHip_projective = new XnVector3D();
	private XnVector3D rightKnee_projective = new XnVector3D();
	private XnVector3D rightFoot_projective = new XnVector3D();

	private List<GestureDetector> gestures;

	public Skeleton(SimpleOpenNI context, int userId) {
		this.context = context;
		this.userId = userId;
		tracking = false;
		initGestures();
	}

	private void initGestures() {
		gestures = new ArrayList<GestureDetector>();
		PointTracker leftHandTracker = new SkeletonJointPositionTracker(leftHand);
		gestures.add(new LinearGestureDetector("left-hand-wave", leftHandTracker));
		//PointTracker rightHandTracker = new SkeletonJointPositionTracker(leftHand);
		//gestures.add(new LinearGestureDetector("right-hand-wave", rightHandTracker));
	}

	public int getUserId() {
		return userId;
	}

	public boolean isTracking() {
		return tracking;
	}

	public boolean update() {
		if (!context.isTrackingSkeleton(userId)) {
			tracking = false;
			return false;
		}
		extractPosition3D();
		extractPosition2D();
		for (GestureDetector gesture : gestures) {
			gesture.sample();
			if (gesture.detect())
				System.out.println("Detected gesture on skeleton " + userId + ": " + gesture.getId());
		}
		tracking = true;
		return true;
	}

	private void extractPosition3D() {
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_HEAD, head);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_NECK, neck);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, leftShoulder);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, leftElbow);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, rightShoulder);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, rightElbow);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_TORSO, torso);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HIP, leftHip);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_KNEE, leftKnee);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_FOOT, leftFoot);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HIP, rightHip);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, rightKnee);
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_FOOT, rightFoot);
	}

	private void extractPosition2D() {
		context.convertRealWorldToProjective(head.getPosition(), head_projective);
		context.convertRealWorldToProjective(neck.getPosition(), neck_projective);
		context.convertRealWorldToProjective(leftShoulder.getPosition(), leftShoulder_projective);
		context.convertRealWorldToProjective(leftElbow.getPosition(), leftElbow_projective);
		context.convertRealWorldToProjective(leftHand.getPosition(), leftHand_projective);
		context.convertRealWorldToProjective(rightShoulder.getPosition(), rightShoulder_projective);
		context.convertRealWorldToProjective(rightElbow.getPosition(), rightElbow_projective);
		context.convertRealWorldToProjective(rightHand.getPosition(), rightHand_projective);
		context.convertRealWorldToProjective(torso.getPosition(), torso_projective);
		context.convertRealWorldToProjective(leftHip.getPosition(), leftHip_projective);
		context.convertRealWorldToProjective(leftKnee.getPosition(), leftKnee_projective);
		context.convertRealWorldToProjective(leftFoot.getPosition(), leftFoot_projective);
		context.convertRealWorldToProjective(rightHip.getPosition(), rightHip_projective);
		context.convertRealWorldToProjective(rightKnee.getPosition(), rightKnee_projective);
		context.convertRealWorldToProjective(rightFoot.getPosition(), rightFoot_projective);
	}

	private void line(PApplet applet, XnVector3D p1, XnVector3D p2) {
		applet.line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public void draw(PApplet applet) {
		if (userId < SKELETON_COLORS.length)
			applet.stroke(SKELETON_COLORS[userId].getRed(), SKELETON_COLORS[userId].getGreen(), SKELETON_COLORS[userId].getBlue());
		else
			applet.stroke(0, 0, 0);

		line(applet, head_projective, neck_projective);

		line(applet, neck_projective, leftShoulder_projective);
		line(applet, leftShoulder_projective, leftElbow_projective);
		line(applet, leftElbow_projective, leftHand_projective);

		line(applet, neck_projective, rightShoulder_projective);
		line(applet, rightShoulder_projective, rightElbow_projective);
		line(applet, rightElbow_projective, rightHand_projective);

		line(applet, leftShoulder_projective, torso_projective);
		line(applet, rightShoulder_projective, torso_projective);

		line(applet, torso_projective, leftHip_projective);
		line(applet, leftHip_projective, leftKnee_projective);
		line(applet, leftKnee_projective, leftFoot_projective);

		line(applet, torso_projective, rightHip_projective);
		line(applet, rightHip_projective, rightKnee_projective);
		line(applet, rightKnee_projective, rightFoot_projective);
	}

	public XnSkeletonJointPosition getHead() {
		return head;
	}

	public void setHead(XnSkeletonJointPosition head) {
		this.head = head;
	}

	public XnSkeletonJointPosition getNeck() {
		return neck;
	}

	public void setNeck(XnSkeletonJointPosition neck) {
		this.neck = neck;
	}

	public XnSkeletonJointPosition getLeftShoulder() {
		return leftShoulder;
	}

	public void setLeftShoulder(XnSkeletonJointPosition leftShoulder) {
		this.leftShoulder = leftShoulder;
	}

	public XnSkeletonJointPosition getLeftElbow() {
		return leftElbow;
	}

	public void setLeftElbow(XnSkeletonJointPosition leftElbow) {
		this.leftElbow = leftElbow;
	}

	public XnSkeletonJointPosition getLeftHand() {
		return leftHand;
	}

	public void setLeftHand(XnSkeletonJointPosition leftHand) {
		this.leftHand = leftHand;
	}

	public XnSkeletonJointPosition getRightShoulder() {
		return rightShoulder;
	}

	public void setRightShoulder(XnSkeletonJointPosition rightShoulder) {
		this.rightShoulder = rightShoulder;
	}

	public XnSkeletonJointPosition getRightElbow() {
		return rightElbow;
	}

	public void setRightElbow(XnSkeletonJointPosition rightElbow) {
		this.rightElbow = rightElbow;
	}

	public XnSkeletonJointPosition getRightHand() {
		return rightHand;
	}

	public void setRightHand(XnSkeletonJointPosition rightHand) {
		this.rightHand = rightHand;
	}

	public XnSkeletonJointPosition getTorso() {
		return torso;
	}

	public void setTorso(XnSkeletonJointPosition torso) {
		this.torso = torso;
	}

	public XnSkeletonJointPosition getLeftHip() {
		return leftHip;
	}

	public void setLeftHip(XnSkeletonJointPosition leftHip) {
		this.leftHip = leftHip;
	}

	public XnSkeletonJointPosition getLeftKnee() {
		return leftKnee;
	}

	public void setLeftKnee(XnSkeletonJointPosition leftKnee) {
		this.leftKnee = leftKnee;
	}

	public XnSkeletonJointPosition getLeftFoot() {
		return leftFoot;
	}

	public void setLeftFoot(XnSkeletonJointPosition leftFoot) {
		this.leftFoot = leftFoot;
	}

	public XnSkeletonJointPosition getRightHip() {
		return rightHip;
	}

	public void setRightHip(XnSkeletonJointPosition rightHip) {
		this.rightHip = rightHip;
	}

	public XnSkeletonJointPosition getRightKnee() {
		return rightKnee;
	}

	public void setRightKnee(XnSkeletonJointPosition rightKnee) {
		this.rightKnee = rightKnee;
	}

	public XnSkeletonJointPosition getRightFoot() {
		return rightFoot;
	}

	public void setRightFoot(XnSkeletonJointPosition rightFoot) {
		this.rightFoot = rightFoot;
	}

}
