package org.aria.h1.gestures.recognition.training;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.aria.h1.gestures.processing.features.Point3D;

public class SampleDataLoader {
	public static String pathPrefix = "sampledata/train/";
	
	public static ArrayList<Point3D[]> loadGestureSamples(String symbol) {
		ArrayList<Point3D[]> rawGestureSequences = new ArrayList<Point3D[]>();
		BufferedReader bufReader = null;
		
		// ------------- read x data -------------
		String filename = pathPrefix + symbol + "_x.csv";
		int nrSeq = 0;
		int seqLen = 0;
		ArrayList<String[]> xtokenLists = new ArrayList<String[]>();
		ArrayList<String[]> ytokenLists = new ArrayList<String[]>();
		ArrayList<String[]> ztokenLists = new ArrayList<String[]>();
		
		try {
			bufReader = new BufferedReader(new FileReader(filename));
			String line = null;
			
			line = bufReader.readLine();
			String[] xvalStrings = line.split(",");
			nrSeq = xvalStrings.length;
			xtokenLists.add(xvalStrings);
			
			while( (line = bufReader.readLine()) != null ) {
				xvalStrings = line.split(",");
				xtokenLists.add(xvalStrings);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		seqLen = xtokenLists.size();
		
		// ------------- read y data -------------
		try {
			bufReader = new BufferedReader(new FileReader(filename));
			String line = null;
			
			while( (line = bufReader.readLine()) != null ) {
				String[] yvalStrings = line.split(",");
				yvalStrings = line.split(",");
				ytokenLists.add(yvalStrings);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// ------------- read z data -------------
		try {
			bufReader = new BufferedReader(new FileReader(filename));
			String line = null;

			while ((line = bufReader.readLine()) != null) {
				String[] zvalStrings = line.split(",");
				zvalStrings = line.split(",");
				ztokenLists.add(zvalStrings);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		for (int i = 0; i < nrSeq; i++) {
			Point3D[] seq = new Point3D[seqLen];
			for (int j = 0; j < seqLen; j++) {
				seq[j] = new Point3D();
			}
			
			rawGestureSequences.add(seq);
		}
		
		// build the raw Gesture Sequence
		for (int j = 0; j < seqLen; j++) {
			String[] xvalStrings = xtokenLists.get(j);
			String[] yvalStrings = ytokenLists.get(j);
			String[] zvalStrings = ztokenLists.get(j);
			
			for (int i = 0; i < nrSeq; i++) {
				double xVal = Double.parseDouble(xvalStrings[i]);
				double yVal = Double.parseDouble(yvalStrings[i]);
				double zVal = Double.parseDouble(zvalStrings[i]);
				
				rawGestureSequences.get(i)[j].setX(xVal);
				rawGestureSequences.get(i)[j].setY(yVal);
				rawGestureSequences.get(i)[j].setZ(zVal);
			}
		}
		
		return rawGestureSequences;
	}
	
	public static void main(String args[]) {
		ArrayList<Point3D[]> rawGestureSequences = loadGestureSamples("circle");
		for (int i = 0; i < rawGestureSequences.size(); i++) {
			System.out.println("Sequence " + (i + 1));
			Point3D[] seq = rawGestureSequences.get(i);
			for (int j = 0; j < seq.length; j++) {
				System.out.println(seq[j]);
			}
			
			System.out.println();
		}
	}
}
