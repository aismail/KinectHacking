/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.aria.h1.gestures.processing.features;


public class GestureFeatureExtractor {

	private static final int QUANTIZE_ANGLE = 30;
	private GestureFeature[] extractedFeatures;
	private RawKinectFeature[] inputRawFeatures;
	private double[] timestamps;
	
	private double[] locationRelativeToCG;
	private double[] timeDiffs;
	private double[] angleWithCGxoz;
	private double[] angleWithCGxoy;
	
	private double[] angleWithInitPtxoz;
	private double[] angleWithInitPtxoy;
	
	private double[] angleWithEndPtxoz;
	private double[] angleWithEndPtxoy;
	
	private double[] velocities;
	
	private double xMin = 0;
	private double xMax = 0;
	private double yMin = 0;
	private double yMax = 0;
	private double zMin = 0;
	private double zMax = 0;
	
	private Point3D center;
	
	// private int[] chainCodeOfAngles;
	
	private int SAMPLE_PER_FRAME = 3;
	private int CHAIN_CODE_NUM_DIRECTIONS = 16;
	private double[] framedCurrentTime;
	private Point3D[] framedPoint;
	// private double[] chainCodeOfAngleWithCG;
	// private double[] chainCodeOfAngleWithSuccessivePts;
	private int framedSamples;

	public GestureFeatureExtractor(RawKinectFeature[] inputRawFeatures, double[] timestamps) {
		this.inputRawFeatures = inputRawFeatures;
		this.timestamps = timestamps;
		//computeFeatures();
	}

	public void computeFeatures() {
		// preprocess-- framing
		// new , from reference
		buildFramedSamples();
		computeCenterAndBounds();
		computePositionDistanceAngle();
		normalizeFeatures();

		// TODO: chain code is currently not implemented
		// chainCodeOfAngleWithCG=quantizeAngle(angleWithCG);
		// chainCodeOfAngleWithSuccessivePts=quantizeAngle(angleWithCG);
		
		composeFeatureVector();
		// printVectors();

	}

	private void printVectors() {
		// TODO
	}

	private void buildFramedSamples() {
		//
		int totalSample = timestamps.length;
		int framedSample = totalSample / SAMPLE_PER_FRAME;
		//framedSample = framedSample - framedSample % SAMPLE_PER_FRAME;
		framedCurrentTime = new double[framedSample];
		framedPoint = new Point3D[framedSample];
		
		// sample points every SAMPLE_PER_FRAME entries
		for (int i = 0; i < framedSample; i++) {
			framedCurrentTime[i] = timestamps[i * SAMPLE_PER_FRAME];
			framedPoint[i] = inputRawFeatures[i * SAMPLE_PER_FRAME].getPoint();
		}
		framedSamples = totalSample / SAMPLE_PER_FRAME;
		
		// define vars, with changed size after frameing
		locationRelativeToCG = new double[framedSamples];
		angleWithCGxoz = new double[framedSamples];
		angleWithCGxoy = new double[framedSamples];
		
		angleWithInitPtxoz = new double[framedSamples];
		angleWithInitPtxoy = new double[framedSamples];
		
		angleWithEndPtxoz = new double[framedSamples];
		angleWithEndPtxoy = new double[framedSamples];
		
		timeDiffs = new double[framedSamples];
		center = new Point3D();
		velocities = new double[framedSamples];
	}

	private void computeCenterAndBounds() {
		int sX = 0, sY = 0, sZ = 0;
		int n = inputRawFeatures.length;
		
		for (int i = 0; i < n; i++) {
			double[] xyz = inputRawFeatures[i].toVector();
			double curX = xyz[0];
			double curY = xyz[1];
			double curZ = xyz[2];
			
			if (curX < xMin) {
				xMin = curX;
			}
			else if (curX > xMax) {
				xMax = curX;
			}
			if (curY < yMin) {
				yMin = curY;
			}
			else if (curY > yMax) {
				yMax = curY;
			}
			if (curZ < zMin) {
				zMin = curZ;
			}
			else if (curZ > zMax) {
				zMax = curZ;
			}
			
			sX += curX;
			sY += curY;
			sZ += curZ;
		}
		
		center.x = sX / n;
		center.y = sY / n;
		center.z = sZ / n;
	}

	private void computePositionDistanceAngle() {
		double initXo = framedPoint[0].x;
		double initYo = framedPoint[0].y;
		double initZo = framedPoint[0].z;
		
		double initXn = framedPoint[framedPoint.length - 1].x;
		double initYn = framedPoint[framedPoint.length - 1].y;
		double initZn = framedPoint[framedPoint.length - 1].z;
		
		for (int i = 0; i < framedPoint.length - 1; i++) {
			/** Geometry **/
			// get values
			Point3D currentPt = framedPoint[i];
			
			double dxC = (currentPt.x - center.x);
			double dyC = (currentPt.y - center.y);
			double dzC = (currentPt.z - center.z);
			
			locationRelativeToCG[i] = Math.sqrt(dxC * dxC + dyC * dyC + dzC * dzC);
			angleWithCGxoz[i] = getAngleYbyX(dzC, dxC);
			angleWithCGxoy[i] = getAngleYbyX(dyC, dxC);
			
			angleWithInitPtxoz[i] = getAngleYbyX(currentPt.z - initZo, currentPt.x - initXo);
			angleWithInitPtxoy[i] = getAngleYbyX(currentPt.y - initYo, currentPt.x - initXo);
			
			angleWithEndPtxoz[i] = getAngleYbyX(currentPt.z - initZn, currentPt.x - initXn);
			angleWithEndPtxoy[i] = getAngleYbyX(currentPt.y - initYn, currentPt.x - initXn);
			
			/** Kinematics **/
			double r1 = framedCurrentTime[i];
			double r2 = framedCurrentTime[i + 1];
			// time diff in two readings
			timeDiffs[i] = r2 - r1;
			double distance = Point3D.distance(framedPoint[i + 1], framedPoint[i]);
			
			velocities[i] = divide(distance, timeDiffs[i]);
		}
		
		velocities[framedPoint.length - 1] = 0;
	}

	private double divide(double num, double denom) {
		if (denom == 0) {
			return 0.0;
		}
		else {
			return num / denom;
		}
	}

	/**
	 * also quantizes the angle
	 * 
	 * @param dy
	 * @param dx
	 * @return
	 */
	private double getAngleYbyX(double dy, double dx) {
		// quantize too
		double angleD = (Math.toDegrees(Math.atan(divide(dy, dx))) / QUANTIZE_ANGLE);
		//System.out.println(angleD + "         " + Math.floor(angleD));
		return Math.ceil(angleD);
	}

	/** post process **/
	private void normalizeFeatures() {
		// location and distances
		// time
		// velocity
		double maxLoc = findMax(locationRelativeToCG);
		double minLoc = findMin(locationRelativeToCG);
		
		double maxTimDiff = findMax(timeDiffs);
		double maxVelocity = findMax(velocities);
		
		for (int i = 0; i < velocities.length; i++) {
			// normalize
			locationRelativeToCG[i] = divide(locationRelativeToCG[i] - minLoc, maxLoc - minLoc);
			
			// simple div
			timeDiffs[i] = divide(timeDiffs[i], maxTimDiff);
			velocities[i] = divide(velocities[i], maxVelocity);
		}
	}

	// TODO: chain code is currently not used
	// simple quantization is done in
	private double[] quantizeAngle(double[] theta) {
		double phi = 360 / CHAIN_CODE_NUM_DIRECTIONS;
		double[] code = new double[theta.length];
		for (int j = 0; j < theta.length; j++) {
			for (int i = 0; i < CHAIN_CODE_NUM_DIRECTIONS; i++) {
				// lower bound
				double shiL = 360 / CHAIN_CODE_NUM_DIRECTIONS * (i - CHAIN_CODE_NUM_DIRECTIONS / 2);
				// upper bound
				double shiU = 360 / CHAIN_CODE_NUM_DIRECTIONS * (i + 1 - CHAIN_CODE_NUM_DIRECTIONS / 2);
				if (shiU >= theta[i] && theta[i] > shiL) {
					double delta = Math.abs(shiU - theta[i]);
					if (delta < phi / 2) {
						code[j] = i;
					}
					else {
						code[j] = i + 1;
					}
					System.out.println(code[j]);
					// go for next angle
					break;// code found break inner loop
				}
			}
		}

		return code;
	}

	private void composeFeatureVector() {
		// location, distance,angleCG,angleSucc,Velocity.....
		System.out.println("Composing...");
		
		extractedFeatures = new GestureFeature[framedSamples - 1];
		for (int i = 0; i < extractedFeatures.length; i++) {
			extractedFeatures[i] = new GestureFeature();
			
			extractedFeatures[i].setLocation(locationRelativeToCG[i]);
			extractedFeatures[i].setAngleWithCGxoz(angleWithCGxoz[i]);
			extractedFeatures[i].setAngleWithCGxoy(angleWithCGxoy[i]);
			
			extractedFeatures[i].setAngleWithInitPtxoz(angleWithInitPtxoz[i]);
			extractedFeatures[i].setAngleWithInitPtxoy(angleWithInitPtxoy[i]);
			extractedFeatures[i].setAngleWithEndPtxoz(angleWithEndPtxoz[i]);
			extractedFeatures[i].setAngleWithEndPtxoy(angleWithEndPtxoy[i]);
			extractedFeatures[i].setVelocity(velocities[i]);
		}
	}

	// **MATH UTILS**//
	private double findMax(double[] arr) {
		double max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		return max;
	}

	private double findMin(double[] arr) {
		double max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < max) {
				max = arr[i];
			}
		}
		return max;
	}

	// ** GETTER AND SETTER **//

	public void setInputRawFeatures(RawKinectFeature[] inputRawFeatures) {
		this.inputRawFeatures = inputRawFeatures;
	}

	public GestureFeature[] getExtractedFeatures() {
		return extractedFeatures;
	}

	public int getSamplePerFrame() {
		return SAMPLE_PER_FRAME;
	}

	public void setSamplePerFrame(int SAMPLE_PER_FRAME) {
		this.SAMPLE_PER_FRAME = SAMPLE_PER_FRAME;
	}

	public int getChainCodeNumDirections() {
		return CHAIN_CODE_NUM_DIRECTIONS;
	}

	public void setChainCodeNumDirections(int CHAIN_CODE_NUM_DIRECTIONS) {
		this.CHAIN_CODE_NUM_DIRECTIONS = CHAIN_CODE_NUM_DIRECTIONS;
	}

}
