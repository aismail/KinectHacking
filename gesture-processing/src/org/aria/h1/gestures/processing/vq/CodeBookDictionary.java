/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.aria.h1.gestures.processing.vq;
import java.io.Serializable;

/**
 * @author Ganesh Tiwari 
 * @author Alex Sorici - modified June 1, 2012
 *
 */
public class CodeBookDictionary implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected int dimension;
	protected Centroid[] centroids;
	
	public CodeBookDictionary(){
	}
	
	public CodeBookDictionary(int dimension, Centroid[] centroids){
		this.dimension = dimension;
		this.centroids = centroids;
	}
	
	
	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public Centroid[] getCentroids() {
		return centroids;
	}

	public void setCentroids(Centroid[] centroids) {
		this.centroids = centroids;
	}
}
