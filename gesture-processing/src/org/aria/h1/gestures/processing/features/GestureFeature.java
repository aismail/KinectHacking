package org.aria.h1.gestures.processing.features;

import java.io.Serializable;

/**
 * A 8 dimensional feature vector that includes <br/>
 * <li> distance from center of gravity of the gesture </li>
 * <li> angle formed by line between current point and the center of gravity in xOz plane </li>
 * <li> angle formed by line between current point and the center of gravity in xOy plane </li>
 * <li> angle formed by line between current point and start point in xOz plane </li>
 * <li> angle formed by line between current point and start point in xOy plane </li>
 * <li> angle formed by line between current point and end point in xOz plane </li>
 * <li> angle formed by line between current point and end point in xOy plane </li>
 * <li> velocity: speed of transition from two consecutive points </li>
 * 
 * @author alex
 */
public class GestureFeature implements IFeature, Serializable {
	private static final long serialVersionUID = 1L;
	//private double location;
	//private double angleWithCG;
	//private double angleWithInitialPt;
	//private double angleWithEndPt;
	//private double velocity;
	
	private double[] vector = new double[8];
	private int dimension = 8;
	
	public GestureFeature() {
	}
	
	public GestureFeature(double[] vector) {
		this.vector = vector;
	}
	
	public GestureFeature(double location, 
			double angleCGxoz, double angleCGxoy, 
			double angleInitPtxoz, double angleInitPtxoy,
			double angleEndPtxoz, double angleEndPtxoy,
			double velocity
			) {
		vector[0] = location;
		vector[1] = angleCGxoz;
		vector[2] = angleCGxoy;
		vector[3] = angleInitPtxoz;
		vector[4] = angleInitPtxoy;
		vector[5] = angleEndPtxoz;
		vector[6] = angleEndPtxoy;
		vector[7] = velocity;
	}
	
	public double[] toVector() {
		return vector;
	}
	
	@Override
	public String toString() {
		return "GestureFeature [Loc_rel2CG=" + vector[0] + ", Dist_betnSuccPts="
				+ ", Angle_withCG_xoz=" + vector[1] + ", Angle_withCG_xoy=" + vector[2] 
				+ ", AngleWithInitPt_xoz=" + vector[3] + ", AngleWithInitPt_xoy=" + vector[4]
				+ ", AngleWithEndPt_xoz=" + vector[5] + ", AngleWithEndPt_xoy=" + vector[6] 
				+ ", Velocity=" + vector[4] + "]";
	}

	@Override
	public int getDimension() {
		return dimension;
	}

	@Override
	public void setVector(double[] vals) {
		this.vector = vals;
	}

	public double getLocation() {
		return vector[0];
	}

	public void setLocation(double location) {
		vector[0] = location;
	}

	public double getAngleWithCGxoz() {
		return vector[1];
	}

	public double getAngleWithCGxoy() {
		return vector[2];
	}
	
	public void setAngleWithCGxoz(double angle) {
		vector[1] = angle;
	}

	public void setAngleWithCGxoy(double angle) {
		vector[2] = angle;
	}
	
	public double getAngleWithInitPtxoz() {
		return vector[3];
	}
	
	public double getAngleWithInitPtxoy() {
		return vector[4];
	}
	
	public void setAngleWithInitPtxoz(double angle) {
		vector[3] = angle;
	}

	public void setAngleWithInitPtxoy(double angle) {
		vector[4] = angle;
	}
	
	public double getAngleWithEndPtxoz() {
		return vector[5];
	}
	
	public double getAngleWithEndPtxoy() {
		return vector[6];
	}
	
	public void setAngleWithEndPtxoz(double angle) {
		vector[5] = angle;
	}
	
	public void setAngleWithEndPtxoy(double angle) {
		vector[6] = angle;
	}
	
	public double getVelocity() {
		return vector[7];
	}

	public void setVelocity(double velocity) {
		this.vector[7] = velocity;
	}
}
