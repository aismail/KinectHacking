/*****************************************************************************
 * Copyright (c) 1998-2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: Model.java,v 1.8 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;

import com.bluecraft.hmm.util.*;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;


/**
 * Model implements IModel which represents a discrete Hidden Markov Model (HMM).
 * HMM is basically a Markov model with symbol-generation probabilities.
 * (Please refer to Rabiner 1989 for more information.)
 * 
 * NumStates: Number of distinct states in the embedded Markov model.
 * NumSymbols: Number of different observable symbols.
 * A: State-transition probabilities. (2D array: NumStates x NumStates)
 * B: Symbol-generation probabilities. (2D array: NumStates x NumSymbols)
 * Pi: State probabilities at time t (1D Array: 1 x NumStates)
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.8 $
 */
public class Model 
    implements IModel, Serializable 
{
    // Maximum number of states allowed in a model.
    private static final int MAX_NUM_STATES = 1000;
    // Maximum number of symbols allowed in a model.
    private static final int MAX_NUM_SYMBOLS = 1000;
    // Smallest value of non-zero probability allowed in a model.
    private static double MIN_PROB = 0.00001;  // Note that MIN_PROB should be smaller than 1.0/num_states!!!
    // Random number generator.
    private static Random rand = new Random();


    /**
     * Returns the maximum number of states allowed in a model.
     * 
     * @return Maximum number of states allowed in a model
     */
    public static int getMaxNumStates() 
    {
        return MAX_NUM_STATES;
    }

    /**
     * Returns the maximum number of symbols allowed in a model.
     * 
     * @return Maximum number of symbols allowed in a model
     */
    public static int getMaxNumSymbols() 
    {
        return MAX_NUM_SYMBOLS;
    }

    /**
     * Returns the smallest value of non-zero probability.
     * Any probability values (allowed to be non-zero) in a model should be bigger than this value.
     * 
     * @return Smallest value of non-zero probability allowed in a model
     */
    public static double getMinProb() 
    {
        return MIN_PROB;
    }

    /**
     * Sets the smallest value of non-zero probability.
     * This value should be bigger than 1.0/getMaxNumStates() and 1.0/getMaxNumSymbols().
     * TODO: Once a model is constructed, MIN_PROB cannot be changed.....???
     * 
     * @param p New value to be used as the smallest value of probability allowed in a model
     * @return Smallest value of non-zero probability allowed in a model
     */
    private static double resetMinProb(double p) 
    {
        double epsilon = (1.0/MAX_NUM_STATES < 1.0/MAX_NUM_SYMBOLS) ? 1.0/MAX_NUM_STATES : 1.0/MAX_NUM_SYMBOLS;
        if(p >= 0.0 && p <= epsilon) {
            MIN_PROB = p;
        }
        return MIN_PROB;
    }


    /**
     * @serial Number of states in the Markov model
     */
    private int  num_states = 0;
    
    /**
     * @serial Number of observable symbols
     */
    private int  num_symbols = 0;
    
    /**
     * @serial Non-negative integer. Lower-left bound of the non-zero band of the transition matrix (diagonal: 0)
     */
    private int  ll_bound = 0;

    /**
     * @serial Non-negative integer. Upper-right bound of the non-zero band of the transition matrix (diagonal: 0)
     */
    private int  ur_bound = 0;

    /**
     * @serial  State transition probability (num_states x num_states)
     */
    private double[][] a;

    /**
     * @serial  Symbol generation probability (num_states x num_symbols)
     */
    private double[][] b;

    /**
     * @serial  Initial state probability (1 x num_states)
     */
    private double[] pi;

    // a[][] has a band of non-zero elements.
    // For each row, columns below l_limit and above r_limit are zero.
    // Column index below which all elements are zero by definition.
    private transient int[] l_limit;
    // Column index above which all elements are zero by definition.
    private transient int[] r_limit;



    /**
     * Construct Model with a minimum size (number of states: 1, number of symbols: 1).
     * The elements of the matrices are initialized by random values.
     */
    public Model() 
        throws HmmConstructionException
    {
        this(true);
    }

    /**
     * Construct Model with a minimum size (number of states: 1, number of symbols: 1).
     * 
     * @param bRandomize If true, the elements of the matrices are initialized by random values
     */
    public Model(boolean bRandomize) 
        throws HmmConstructionException
    {
        this(1,1,bRandomize);
    }

    /**
     * Construct Model by specifying dimensions.
     * The elements of the matrices are initialized by random values.
     * 
     * @param nstates Number of states of a Markov model
     * @param nsymbols Number of symbols generated by the model
     */
    public Model(int nstates, int nsymbols) 
        throws HmmConstructionException
    {
        this(nstates, nsymbols, true);
    }

    /**
     * Construct Model by specifying dimensions.
     * 
     * @param nstates Number of states of a Markov model
     * @param nsymbols Number of symbols generated by the model
     * @param bRandomize If true, the elements of the matrices are initialized by random values
     */
    public Model(int nstates, int nsymbols, boolean bRandomize) 
        throws HmmConstructionException
    {
        this(nstates, nsymbols, nstates-1, nstates-1, bRandomize);
    }

    /**
     * Construct Model by specifying dimensions.
     * The elements of the matrices are initialized by random values.
     * 
     * @param nstates Number of states of a Markov model
     * @param nsymbols Number of symbols generated by the model
     * @param llb Lower-left bound of the non-zero band of the transition matrix (diagonal: 0)
     * @param urb Upper-right bound of the non-zero band of the transition matrix (diagonal: 0)
     */
    public Model(int nstates, int nsymbols, int llb, int urb) 
        throws HmmConstructionException
    {
        this(nstates, nsymbols, llb, urb, true);
    }
    
    /**
     * Construct Model by specifying dimensions.
     * 
     * @param nstates Number of states of a Markov model
     * @param nsymbols Number of symbols generated by the model
     * @param llb Lower-left bound of the non-zero band of the transition matrix (diagonal: 0)
     * @param urb Upper-right bound of the non-zero band of the transition matrix (diagonal: 0)
     * @param bRandomize If true, the elements of the matrices are initialized by random values
     */
    public Model(int nstates, int nsymbols, int llb, int urb, boolean bRandomize) 
        throws HmmConstructionException
    {
        num_states = (nstates < MAX_NUM_STATES) ? nstates : MAX_NUM_STATES;
        num_symbols = (nsymbols < MAX_NUM_SYMBOLS) ? nsymbols : MAX_NUM_SYMBOLS;

        a = new double[num_states][num_states];
        b = new double[num_states][num_symbols];
        pi = new double[num_states];
        l_limit = new int[num_states];
        r_limit = new int[num_states];
        
        ll_bound = (llb < num_states && llb >= 0) ? llb : (num_states-1);
        ur_bound = (urb < num_states && urb >= 0) ? urb : (num_states-1);
        setLimits();
        setProbability(bRandomize);
    }

    /**
     * Construct Model with the given probability matrices.
     * 
     * @param _a State transition probability
     * @param _b Symbol generation probability
     * @param _pi Initial state probability
     */
    private Model(double[][] _a, double[][] _b, double[] _pi)
        throws HmmConstructionException
    {
        // TODO: Make sure that
        // _a != null, _b != null, _pi != null
        // _a.length > 0, _b.length > 0
        // _a.length == _b.length == _pi.length
        // _a[0].length == _pi.length
        num_states = _a[0].length;
        num_symbols = _b[0].length;
        
        a = new double[num_states][num_states];
        b = new double[num_states][num_symbols];
        pi = new double[num_states];
        l_limit = new int[num_states];
        r_limit = new int[num_states];
        
        ll_bound = num_states-1;
        ur_bound = num_states-1;
        setLimits();
        
        for(int i=0;i<num_states;i++) {
            for(int j=0;j<num_states;j++) {
                a[i][j] = _a[i][j];
            }
            for(int k=0;k<num_symbols;k++) {
                b[i][k] = _b[i][k];
            }
            pi[i] = _pi[i];
        }
    }


    /**
     * Construct Model by loading model parameters from an XML file.
     * 
     * @param file Name of input file
     */
    public Model(String file) 
        throws HmmConstructionException
    {
        this(file, true);
    }

    /**
     * Construct Model by loading model parameters from a file.
     * 
     * @param file Name of input file
     * @param bXml If true, read input file in XML format
     */
    public Model(String file, boolean bXml) 
        throws HmmConstructionException
    {
        if(bXml) {
            loadModelXml(file);
        } else {
            loadModelAscii(file);
        }
    }


    /**
     * Sets the left and right column indices for each row beyond which all elements are assumed to be zero by definition.
     */
    private void setLimits() 
    {
        for(int i=0;i<num_states;i++) {
            l_limit[i] = (i-ll_bound >=0) ? (i-ll_bound) : 0;
            r_limit[i] = (i+ur_bound <= num_states-1) ? (i+ur_bound) : (num_states-1);
        }
    }

    /**
     * Sets the left and right band width.
     * 
     * @param llb Lower-left bound (Non-negative integer)
     * @param urb Upper-right bound (Non-negative integer)
     */
    private void setLeftRight(int llb, int urb) 
    {
        ll_bound = (llb < num_states && llb >= 0) ? llb : (num_states-1);
        ur_bound = (urb < num_states && urb >= 0) ? urb : (num_states-1);
        setLimits();
        resetA();
    }    

    /**
     * Sets all elements of A to zero.
     */
    private synchronized void clearA() 
    {
        for(int i=0;i<num_states;i++) {
            for(int j=0;j<num_states;j++) {
                a[i][j] = 0.0;
            }
        }
    }

    /**
     * Sets all elements of B to zero.
     */    
    private synchronized void clearB() 
    {
        for(int i=0;i<num_states;i++) {
            for(int k=0;k<num_symbols;k++) {
                b[i][k] = 0.0;
            }
        }
    }    

    /**
     * Sets all elements of Pi to zero.
     */    
    private synchronized void clearPi() 
    {
        for(int j=0;j<num_states;j++) {
            pi[j] = 0.0;
        }
    }    

    /**
     * Resets A with random probability values.
     */    
    private void resetA() 
    {
        resetA(true);
    }

    /**
     * Resets A with new probability values.
     * 
     * @param bRandomize If true, use random values.
     */
    private void resetA(boolean bRandomize) 
    {
        clearA();
        setA(bRandomize);
    }

    /**
     * Resets B with random probability values.
     */    
    private void resetB() 
    {
        resetB(true);
    }

    /**
     * Resets B with new probability values.
     * 
     * @param bRandomize If true, use random values.
     */
    private void resetB(boolean bRandomize) 
    {
        clearB();
        setB(bRandomize);
    }

    /**
     * Resets B with random probability values.
     */    
    private void resetPi() 
    {
        resetPi(true);
    }

    /**
     * Resets Pi with new probability values.
     * 
     * @param bRandomize If true, use random values.
     */
    private void resetPi(boolean bRandomize) 
    {
        clearPi();
        setPi(bRandomize);
    }


    /**
     * Normalizes the i-th row of the transition probability matrix, A.
     * If any of the probability values becomes smaller than MIN_PROB,
     * it is reset to MIN_PROB.
     * 
     * @param i Row index
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    private void normalizeRowA(int i)
    {
        // Check first if the argument is in the valid range.       
        if(i < 0 || i >= num_states) {
            throw new InvalidRangeException("A: Row " + i + " is out of vaild range!");
        }

        synchronized(a[i]) {
            double sum = 0.0;
            for(int j=l_limit[i];j<=r_limit[i];j++) {
                // The probability is a non-negative number.
                if(a[i][j] < 0.0) {
                    throw new InvalidProbabilityException("A: Row " + i + " contains negative probabilites!");
                }
                sum += a[i][j];
            }
            // This checking is unnecessary.
            if(sum <= 0.0) {
                throw new ProbabilityUnnormalizableException("A: Row " + i + " unnormalizable!");
            }
    
            // Normalize the row i.
            for(int j=l_limit[i];j<=r_limit[i];j++) {
                a[i][j] /= sum;
            }
    
            // Make sure that all elements are at least as big as MIN_PROB.
            double deficit = 0.0;
            for(int j=l_limit[i];j<=r_limit[i];j++) {
                if(a[i][j] < MIN_PROB) {
                    deficit += MIN_PROB - a[i][j]; 
                    a[i][j] = MIN_PROB;
                }
            }
    
            // Re-normalize the row if needed.
            if(deficit > 0.0) {
                //double sum2 = 0.0;
                //for(int j=l_limit[i];j<=r_limit[i];j++) {
                //    sum2 += a[i][j];
                //}
                double sum2 = 1.0 + deficit;
                sum2 -= (r_limit[i] - l_limit[i] + 1) * MIN_PROB;
                double factor = (1.0 - (r_limit[i] - l_limit[i] + 1) * MIN_PROB)/sum2;
                for(int j=l_limit[i];j<=r_limit[i];j++) {
                    a[i][j] *= factor;
                    a[i][j] += (1.0 - factor) * MIN_PROB;
                }
            }
        }

    }

    /**
     * Normalizes the transition probability matrix, A.
     * If any of the probability values becomes smaller than MIN_PROB,
     * it is reset to MIN_PROB.
     * 
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    private void normalizeRowA()
    {
        for(int i=0;i<num_states;i++) {
            normalizeRowA(i);
        }
    }

    /**
     * Normalizes the i-th row of the symbol-generation probability matrix, B.
     * If any of the probability values becomes smaller than MIN_PROB,
     * it is reset to MIN_PROB.
     * 
     * @param i Row index
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    private void normalizeRowB(int i)
    {        
        // Check first if the argument is in the valid range.       
        if(i < 0 || i >= num_states) {
            throw new InvalidRangeException("B: Row " + i + " is out of vaild range!");
        }

        synchronized(b[i]) {
            double sum = 0.0;
            for(int k=0;k<num_symbols;k++) {
                // The probability is a non-negative number.
                if(b[i][k] < 0.0) {
                    throw new InvalidProbabilityException("B: Row " + i + " contains negative probabilites!");
                }
                sum += b[i][k];
            }
            // This checking is unnecessary.
            if(sum <= 0.0) {
                throw new ProbabilityUnnormalizableException("B: Row " + i + " unnormalizable!");
            }
    
            // Normalize the row i.
            for(int k=0;k<num_symbols;k++) {
                b[i][k] /= sum;
            }
    
            // Make sure that all elements are at least as big as MIN_PROB.
            double deficit = 0.0;
            for(int k=0;k<num_symbols;k++) {
                if(b[i][k] < MIN_PROB) {
                    deficit += MIN_PROB - b[i][k]; 
                    b[i][k] = MIN_PROB;
                }
            }
    
            // Re-normalize the row if needed.
            if(deficit > 0.0) {
                //double sum2 = 0.0;
                //for(int k=0;k<num_symbols;k++) {
                //    sum2 += b[i][k];
                //}
                double sum2 = 1.0 + deficit;
                sum2 -= num_symbols * MIN_PROB;
                double factor = (1.0 - num_symbols * MIN_PROB)/sum;
                for(int k=0;k<num_symbols;k++) {
                    b[i][k] *= factor;
                    b[i][k] += (1.0 - factor) * MIN_PROB;
                }
            }
        }

    }

    /**
     * Normalizes the symbol-generation probability matrix, B.
     * If any of the probability values becomes smaller than MIN_PROB,
     * it is reset to MIN_PROB.
     * 
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    private void normalizeRowB()
    {
        for(int i=0;i<num_states;i++) {
            normalizeRowB(i);
        }
    }

    /**
     * Normalizes the initial state probability vector, Pi.
     * If any of the probability values becomes smaller than MIN_PROB,
     * it is reset to MIN_PROB.
     * 
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    private void normalizeRowPi()
    {        
        synchronized(pi) {
            double sum = 0.0;
            for(int j=0;j<num_states;j++) {
                // The probability is a non-negative number.
                if(pi[j] < 0.0) {
                    throw new InvalidProbabilityException("Pi: State-vector contains negative probabilites!");
                }
                sum += pi[j];
            }
            // This checking is unnecessary.
            if(sum <= 0.0) {
                throw new ProbabilityUnnormalizableException("B: State-vector unnormalizable!");
            }
    
            // Normalize the state-vector.
            for(int j=0;j<num_states;j++) {
                pi[j] /= sum;
            }
    
            // Make sure that all elements are at least as big as MIN_PROB.
            double deficit = 0.0;
            for(int j=0;j<num_states;j++) {
                if(pi[j] < MIN_PROB) {
                    deficit += MIN_PROB - pi[j]; 
                    pi[j] = MIN_PROB;
                }
            }
    
            // Re-normalize the row if needed.
            if(deficit > 0.0) {
                //double sum2 = 0.0;
                //for(int j=0;j<num_states;j++) {
                //    sum2 += pi[j];
                //}
                double sum2 = 1.0 + deficit;
                sum2 -= num_states * MIN_PROB;
                double factor = (1.0 - num_states * MIN_PROB)/sum;
                for(int j=0;j<num_states;j++) {
                    pi[j] *= factor;
                    pi[j] += (1.0 - factor) * MIN_PROB;
                }
            }
        }

    }


    /**
     * Gets the number of states in the embedded Markov model.
     * 
     * @return  Number of states in the Markov model 
     */
    public int getNumStates() 
    {
        return num_states;
    }

    /**
     * Gets the number of observable symbols.
     * 
     * @return  Number of observable symbols 
     */
    public int getNumSymbols() 
    {
        return num_symbols;
    }

    /**
     * Returns the column index below which all elements of the state-transition matrix A are zero by definition.
     * 
     * @param i Row number of the state-transition matrix A
     * @return  Left limit (column number) outside of which the transition probability is zero 
     */
    public int getLLimit(int i) 
    {
        return l_limit[i];
    }

    /**
     * Returns the column index above which all elements of the state-transition matrix A are zero by definition.
     * 
     * @param i Row number of the state-transition matrix A
     * @return  Right limit (column number) outside of which the transition probability is zero 
     */
    public int getRLimit(int i) 
    {
        return r_limit[i];
    }

    /**
     * Initializes the state-transition matrix A with random values.
     */
    public void setA() 
    {
        setA(true);
    }

    /**
     * Initializes the state-transition matrix A.
     * If the given argument, bRandomize, is true, then it initializes A with random values.
     * Otherwise, A will be initialized to the one close to the identity matrix 
     * with small non-zero transition probabilities between different states (when allowed by the model). 
     * 
     * @param bRandomize If true, initialize A with random values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setA(boolean bRandomize) 
    {
        if(bRandomize == true) {
            for(int i=0;i<num_states;i++) {
                for(int j=l_limit[i];j<=r_limit[i];j++) {
                    a[i][j] = rand.nextDouble() + MIN_PROB;
                }
            }
            normalizeRowA();
        }
        else {
            for(int i=0;i<num_states;i++) {
                for(int j=l_limit[i];j<=r_limit[i];j++) {
                    a[i][j] = MIN_PROB;
                }
                // TODO: Make sure that a >= 0.
                a[i][i] = 1.0 - (r_limit[i] - l_limit[i])*MIN_PROB;
            }
        }
    }

    /**
     * Sets the ij-th element of the state-transition matrix A with the given value, p.
     * If p is smaller than MIN_PROB, it is set to MIN_PROB.
     * 
     * @param i Row number of the state-transition matrix A
     * @param j Column number of state-transition matrix A
     * @param p Probability value    
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    private void setA(int i, int j, double p) 
    {
        if(p>1.0 || p<0.0) {
            throw new InvalidProbabilityException();
        }
        if(i<0 || i>=num_states) {
            throw new InvalidRangeException();
        }
        if(j<l_limit[i] || j>r_limit[i]) {
            if(j<0 || j>=num_states) {
                throw new InvalidRangeException();
            } else {
                // TODO: Is this necessary???
                if(p>0.0) {
                    throw new InvalidRangeException();
                } else {
                    a[i][j] = 0.0;
                    return;
                }
            }
        }

        // If p is smaller than MIN_PROB, then set it to MIN_PROB.
        a[i][j] = (p>MIN_PROB) ? p : MIN_PROB;
    }

    /**
     * Sets the i-th row of the state-transition matrix A with the given vector, p.
     * p is assumed to be properly normalized.
     * 
     * @param i Row number of the state-transition matrix A
     * @param p Row vector of probability values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    public void setA(int i, double[] p) 
    {
        setA(i, p, false);
    }

    /**
     * Sets the i-th row of the state-transition matrix A with the given vector, p.
     * 
     * @param i Row number of the state-transition matrix A
     * @param p Row vector of probability values
     * @param bNormalize If true, renormalize the row in case p is not normalized
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setA(int i, double[] p, boolean bNormalize)
    {
        synchronized(a[i]) {
            //for(int j=0;j<=num_states;j++) {
            for(int j=l_limit[i];j<=r_limit[i];j++) {
                setA(i, j, p[j]);
            }
        }
        if(bNormalize == true) {
            normalizeRowA(i);
        }
    }

    /**
     * Sets the state-transition matrix A with the given matrix, p.
     * p is assumed to be properly normalized.
     * 
     * @param p Matrix of probability values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    public void setA(double[][] p) 
    {
        setA(p, false);
    }

    /**
     * Sets the state-transition matrix A with the given matrix, p.
     * 
     * @param p Matrix of probability values
     * @param bNormalize If true, renormalize each row in case p is not normalized
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setA(double[][] p, boolean bNormalize) 
    {
        for(int i=0;i<num_states;i++) {
            setA(i, p[i], bNormalize);
        }
    }
    
    
    /**
     * Initializes the symbol-generation matrix B with random values.
     * 
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    public void setB() 
    {
        setB(true);
    }

    /**
     * Initializes the symbol-generation matrix B.
     * If the given argument, bRandomize, is true, then it initializes B with random values.
     * Otherwise, B will be initialized to a constant matrix. 
     * 
     * @param bRandomize If true, initialize B with random values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setB(boolean bRandomize) 
    {
        if(bRandomize == true) {
            for(int i=0;i<num_states;i++) {
                for(int k=0;k<num_symbols;k++) {
                    b[i][k] = rand.nextDouble() + MIN_PROB;
                }
            }    
            normalizeRowB();
        }
        else {
            for(int i=0;i<num_states;i++) {
                for(int k=0;k<num_symbols;k++) {
                    b[i][k] = 1.0/num_symbols;
                }
            }
        }
    }

    /**
     * Sets the ik-th element of the symbo-generation matrix B with the given value, p.
     * If p is smaller than MIN_PROB, it is set to MIN_PROB.
     * 
     * @param i Row number of the symbol-generation matrix B
     * @param k Column number of symbol-generation matrix B
     * @param p Probability value    
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    private void setB(int i, int k, double p) 
    {
        if(p>1.0 || p<0.0) {
            throw new InvalidProbabilityException();
        }
        if(i<0 || i>=num_states) {
            throw new InvalidRangeException();
        }
        if(k<0 || k>=num_symbols) {
            throw new InvalidRangeException();
        }

        // If p is smaller than MIN_PROB, then set it to MIN_PROB.
        b[i][k] = (p>MIN_PROB) ? p : MIN_PROB;
    }
    
    /**
     * Sets the i-th row of the symbol-generation matrix B with the given vector, p.
     * p is assumed to be properly normalized.
     * 
     * @param i Row number of the symbol-generation matrix B
     * @param p Row vector of probability values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    public void setB(int i, double[] p) 
    {
        setB(i, p, false);
    }

    /**
     * Sets the i-th row of the symbol-generation matrix B with the given vector, p.
     * 
     * @param i Row number of the symbol-generation matrix B
     * @param p Row vector of probability values
     * @param bNormalize If true, renormalize the row in case p is not normalized
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setB(int i, double[] p, boolean bNormalize) 
    {
        synchronized(b[i]) {
            for(int k=0;k<num_symbols;k++) {
                setB(i, k, p[k]);
            }
        }
        if(bNormalize == true) {
            normalizeRowB(i);
        }
    }
    
    
    /**
     * Sets the symbol-generation matrix B with the given matrix, p.
     * p is assumed to be properly normalized.
     * 
     * @param p Matrix of probability values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    public void setB(double[][] p) 
    {
        setB(p, false);
    }
    
    /**
     * Sets the state-transition matrix A with the given matrix, p.
     * 
     * @param p Matrix of probability values
     * @param bNormalize If true, renormalize each row in case p is not normalized
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setB(double[][] p, boolean bNormalize) 
    {
        for(int i=0;i<num_states;i++) {
            setB(i, p[i], bNormalize);
        }
    }
    
    
    /**
     * Initialize the initial-state row vector Pi with random values.
     * 
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    public void setPi() 
    {
        setPi(true);
    }
    
    /**
     * Initialize the initial-state row vector Pi.
     * If the given argument, bRandomize, is true, then it initializes Pi with random values.
     * Otherwise, Pi will be initialized to a constant matrix. 
     * 
     * @param bRandomize If true, initialize Pi with random values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setPi(boolean bRandomize) 
    {
        if(bRandomize == true) {
            for(int j=0;j<num_states;j++) {
                pi[j] = rand.nextDouble() + num_states*MIN_PROB;
            }    
            normalizeRowPi();
        }
        else {
            for(int j=0;j<num_states;j++) {
                pi[j] = 1.0/num_states;
            }    
        }
    }

    /**
     * Sets the j-th element of the state-vector Pi with the given value, p.
     * If p is smaller than MIN_PROB, it is set to MIN_PROB.
     * 
     * @param j Column number of initial-state row vector Pi
     * @param p Probability value
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    private void setPi(int j, double p) 
    {
        if(p>1.0 || p<0.0) {
            throw new InvalidProbabilityException();
        }
        if(j<0 || j>=num_states) {
            throw new InvalidRangeException();
        }

        // If p is smaller than MIN_PROB, then set it to MIN_PROB.
        pi[j] = (p>MIN_PROB) ? p : MIN_PROB;
    }

    /**
     * Sets the state-vector Pi with the given vector, p.
     * p is assumed to be properly normalized.
     * 
     * @param p Row vector of probability values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     */
    public void setPi(double[] p) 
    {
        setPi(p, false);
    }    
    
    /**
     * Sets the state-vector Pi with the given vector, p.
     * 
     * @param p Row vector of probability values
     * @param bNormalize If true, renormalize Pi in case p is not normalized
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setPi(double[] p, boolean bNormalize) 
    {
        synchronized(pi) {
            for(int j=0;j<num_states;j++) {
                setPi(j, p[j]);
            }
        }
        if(bNormalize == true) {
            normalizeRowPi();
        }
    }


    /**
     * Returns the ij-th element of A.
     * 
     * @param i Row number of the state-transition matrix A
     * @param j Column number of state-transition matrix A
     * @return Probability value of A[i][j]
     */
    public double getA(int i, int j) 
    {
        return a[i][j];
    }
    
    /**
     * Returns the i-th row of A.
     * 
     * @param i Row number of the state-transition matrix A
     * @return Row vector of probability values of A[i][]
     */
    public double[] getA(int i) 
    {
        return a[i];
    }
    
    /**
     * Returns A.
     * 
     * @return State-transition matrix A
     */
    public double[][] getA() 
    {
        return a;
    }
    
    /**
     * Returns ik-th element of B.
     * 
     * @param i Row number of the symbol-generation matrix B
     * @param k Column number of symbol-generation matrix B
     * @return Probability value of B[i][k]
     */
    public double getB(int i, int k) 
    {
        return b[i][k];
    }
    
    /**
     * Returns i-th row of B.
     * 
     * @param i Row number of the symbol-generation matrix B
     * @return Row vector of probability value of B[i][]
     */
    public double[] getB(int i) 
    {
        return b[i];
    }
    
    /**
     * Returns B.
     * 
     * @return Symbol-generation matrix B
     */
    public double[][] getB() 
    {
        return b;
    }
    
    /**
     * Returns the j-th element of Pi.
     * 
     * @param j Column number of inital-state row vector Pi
     * @return Probability value of Pi[j]
     */
    public double getPi(int j) 
    {
        return pi[j];
    }
    
    /**
     * Returns Pi.
     * 
     * @return Inital-state row vector Pi
     */
    public double[] getPi() 
    {
        return pi;
    }
    
    /**
     * Returns the j-th element of Pi at time t.
     * 
     * @param t Index of observation sequence
     * @param j Column number of state vector Pi[t][] at time t
     * @return Probability value of Pi[t][j]
     */
    public double getPi(int t, int j) 
    {
        if(t==0) {
            return pi[j];
        } else {
            double sum = 0.0;
            for(int i=0;i<num_states;i++) {
                sum += getPi(t-1,i) * a[i][j];
            }
            return sum;
        }    
    }


    /**
     * Initialize probability matrices, A, B, and Pi, with random values.
     * 
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setProbability() 
    {
        setProbability(true);
    }
    
    /**
     * Initialize probability matrices, A, B, and Pi.
     * 
     * @param bRandomize If true, initialize probability matrices with random values
     * @throws com.bluecraft.hmm.InvalidProbabilityException
     * @throws com.bluecraft.hmm.InvalidRangeException
     * @throws com.bluecraft.hmm.ProbabilityUnnormalizableException
     */
    public void setProbability(boolean bRandomize) 
    {
        setA(bRandomize);
        setB(bRandomize);
        setPi(bRandomize);
    }


    /**
     * Load model parameters from an XML file.
     * 
     * @param file Name of input file
     */
    public void loadModel(String file) 
        throws HmmConstructionException
    {
        loadModel(file, true);
    }

    /**
     * Load model parameters from a file.
     * 
     * @param file Name of input file
     * @param bXml If true, read file in XML format
     */
    public void loadModel(String file, boolean bXml) 
        throws HmmConstructionException
    {
        if(bXml) {
            loadModelXml(file);
        } else {
            loadModelAscii(file);
        }
    }
    
    /**
     * Load model parameters from an Ascii file.
     * 
     * @param file Name of input file
     */
    public void loadModelAscii(String file) 
        throws HmmConstructionException
    {
        try {
            BufferedReader in =
                new BufferedReader(
                    new FileReader(file));
                    
            num_states = Integer.valueOf(in.readLine()).intValue();
            num_symbols = Integer.valueOf(in.readLine()).intValue();
            ll_bound = Integer.valueOf(in.readLine()).intValue();
            ur_bound = Integer.valueOf(in.readLine()).intValue();
            // TODO: check validaty of these parameters

            a = new double[num_states][num_states];
            b = new double[num_states][num_symbols];
            pi = new double[num_states];
            l_limit = new int[num_states];
            r_limit = new int[num_states];
            setLimits();

            StringTokenizer stkn = null;
            for(int i=0;i<num_states;i++) {
                stkn = new StringTokenizer(in.readLine());
                int j = 0;
                while(stkn.hasMoreTokens()) {
                    a[i][j++] = Double.valueOf(stkn.nextToken()).doubleValue();
                }
            }    
            // check if j==num_states
            for(int i=0;i<num_states;i++) {
                stkn = new StringTokenizer(in.readLine());
                int k = 0;
                while(stkn.hasMoreTokens()) {
                    b[i][k++] = Double.valueOf(stkn.nextToken()).doubleValue();
                }
            }    
            // check if k==num_symbols
            stkn = new StringTokenizer(in.readLine());
            int j = 0;
            while(stkn.hasMoreTokens()) {
                pi[j++] = Double.valueOf(stkn.nextToken()).doubleValue();
            }
            // check if j==num_states
            
             in.close();
        } catch(FileNotFoundException fnfx) {
            throw new HmmConstructionException(fnfx);
        } catch(IOException iox) {
            throw new HmmConstructionException(iox);
        }
    }

    /**
     * Load model parameters from an XML file.
     * 
     * @param file Name of input file
     */
    public void loadModelXml(String file) 
        throws HmmConstructionException
    {
        // parse xml file!!!!
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch(ParserConfigurationException pcx) {
            throw new HmmConstructionException(pcx);
        }
        Document doc = null;
        try {
            doc = builder.parse(file); 
        } catch(SAXException sx) {
            throw new HmmConstructionException(sx);            
        } catch(IOException iox) {
            throw new HmmConstructionException(iox);
        }

        NodeList nodeList = null;
        NodeList childList = null;
        Node childNode = null;
        Element nodeElement = null;
        Element childElement = null;
        String strElement = null;
 
        nodeList = doc.getElementsByTagName( "states" );
        nodeElement = (Element) nodeList.item(0);
        strElement = nodeElement.getAttribute("count");
        num_states = Integer.valueOf(strElement).intValue();
                    
        nodeList = doc.getElementsByTagName( "symbols" );
        nodeElement = (Element) nodeList.item(0);
        strElement = nodeElement.getAttribute("count");
        num_symbols = Integer.valueOf(strElement).intValue();
                                
        nodeList = doc.getElementsByTagName( "bounds" );
        nodeElement = (Element) nodeList.item(0);
        strElement = nodeElement.getAttribute("ll_bound");
        ll_bound = Integer.valueOf(strElement).intValue();
        nodeElement = (Element) nodeList.item(0);
        strElement = nodeElement.getAttribute("ur_bound");
        ur_bound = Integer.valueOf(strElement).intValue();
        // check validaty of these parameters

        a = new double[num_states][num_states];
        b = new double[num_states][num_symbols];
        pi = new double[num_states];
        l_limit = new int[num_states];
        r_limit = new int[num_states];
        setLimits();

        nodeList = doc.getElementsByTagName( "transition_matrix" );
        childList = nodeList.item(0).getChildNodes(); // "element"
        for (int m = 0 ; m < childList.getLength() ; m++ ){
	          childNode = childList.item(m);
	          if(childNode.getNodeType() == Node.ELEMENT_NODE ){
		            childElement = (Element) childNode;
	          } else {
		            continue;
	          }
            String row = childElement.getAttribute("row");
            String col = childElement.getAttribute("col");
            String value = childElement.getAttribute("value");
            if( row != null && col != null ){
                int i = Integer.valueOf(row).intValue();
                int j = Integer.valueOf(col).intValue();
                a[i][j] = Double.valueOf(value).doubleValue();
            }
        }

        nodeList = doc.getElementsByTagName( "symbol_generation_matrix" );
        childList = nodeList.item(0).getChildNodes(); // "element"
        for (int m = 0 ; m < childList.getLength() ; m++ ){
	         childNode = childList.item(m);
	         if(childNode.getNodeType() == Node.ELEMENT_NODE ){
		           childElement = (Element) childNode;
	         } else {
		           continue;
	         }
            String row = childElement.getAttribute("row");
            String col = childElement.getAttribute("col");
            String value = childElement.getAttribute("value");
            if( row != null && col != null ){
                int i = Integer.valueOf(row).intValue();
                int k = Integer.valueOf(col).intValue();
                b[i][k] = Double.valueOf(value).doubleValue();
            }
        }

        nodeList = doc.getElementsByTagName( "initial_state_vector" );
        childList = nodeList.item(0).getChildNodes(); // "element"
        for (int m = 0 ; m < childList.getLength() ; m++ ){
	          childNode = childList.item(m);
	          if(childNode.getNodeType() == Node.ELEMENT_NODE ){
		            childElement = (Element) childNode;
	          } else {
		            continue;
	          }
            String row = childElement.getAttribute("row");
            String value = childElement.getAttribute("value");
            if( row != null){
                int j = Integer.valueOf(row).intValue();
                pi[j] = Double.valueOf(value).doubleValue();
            }
        }

    }


    /**
     * Save model parameters to a given file in XML format.
     * 
     * @param file Name of output file
     */
    public void saveModel(String file) 
    {
        saveModel(file, true);
    }
        
    /**
     * Save model parameters to a given file.
     * 
     * @param file Name of output file
     * @param bXml If true, save model in XML format
     */
    public void saveModel(String file, boolean bXml) 
    {
        if(bXml) {
            saveModelXml(file);
        } else {
            saveModelAscii(file);
        }
    }
      
    /**
     * Save model parameters to a given file in Ascii format.
     * 
     * @param file Name of output file
     */
    public void saveModelAscii(String file) 
    {
        try {
            PrintWriter out =
                new PrintWriter(
                    new BufferedWriter(
                        new FileWriter(file)));
                        
            out.println(num_states);
            out.println(num_symbols);
            out.println(ll_bound);
            out.println(ur_bound);

            for(int i=0;i<num_states;i++) {
                for(int j=0;j<num_states;j++) {
                    out.print(a[i][j]);
                    out.print("\t");
                }
                out.println();
            }    
            for(int i=0;i<num_states;i++) {
                for(int k=0;k<num_symbols;k++) {
                    out.print(b[i][k]);
                    out.print("\t");
                }
                out.println();
            }    
            for(int j=0;j<num_states;j++) {
                out.print(pi[j]);
                out.print("\t");
            }
            out.println();
            
            //
            //out.flush();
            out.close();
        } catch(FileNotFoundException exc) {
            System.out.println("File Not Found: " + file);
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }
    
    /**
     * Save model parameters to a given file in XML format.
     * 
     * @param file Name of output file
     */
    public void saveModelXml(String file) 
    {
        try {
            PrintWriter out =
                new PrintWriter(
                    new BufferedWriter(
                        new FileWriter(file)));
            
            out.println("<?xml version=\"1.0\"?>");
            out.println("<!DOCTYPE hmm SYSTEM \"hmm03.dtd\">");
            out.println(" ");
            out.println("<hmm version=\"0.3\">");
            out.println("<name>");
            out.println("Hidden Markov Model Test");
            out.println("</name>");
            out.println("<model>");
            out.print("<states count=\"");
            out.print(num_states);
            out.println("\">");
            out.println("</states>");
            out.print("<symbols count=\"");
            out.print(num_symbols);
            out.println("\">");
            out.println("</symbols>");
            out.print("<bounds ll_bound=\"");
            out.print(ll_bound);
            out.print("\" ur_bound=\"");
            out.print(ur_bound);
            out.println("\">");
            out.println("</bounds>");

            out.println("<transition_matrix>");
            for(int i=0;i<num_states;i++) {
                for(int j=0;j<num_states;j++) {
                    out.print("<element row=\"" + i + "\" " + "col=\"" + j + "\" "
                              + "value=\"" + a[i][j] + "\"/>"); 
                }
                out.println();
            }    
            out.println("</transition_matrix>");
            out.println("<symbol_generation_matrix>");
            for(int i=0;i<num_states;i++) {
                for(int k=0;k<num_symbols;k++) {
                    out.print("<element row=\"" + i + "\" " + "col=\"" + k + "\" "
                              + "value=\"" + b[i][k] + "\"/>"); 
                }
                out.println();
            }    
            out.println("</symbol_generation_matrix>");
            out.println("<initial_state_vector>");
            for(int j=0;j<num_states;j++) {
                out.print("<element row=\"" + j + "\" "
                          + "value=\"" + pi[j] + "\"/>"); 
            }
            out.println();
            out.println("</initial_state_vector>");
            out.println("</model>");
            out.println("</hmm>");

            //
            //out.flush();
            out.close();
        } catch(FileNotFoundException exc) {
            System.out.println("File Not Found: " + file);
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }



    /**
     * This function is provided for testing purposes.
     * Direct modification of this class is not recommended
     * unless there is a bug. (In which case please notify me.)
     * Please use inheritance or aggregation (composition).
     */
    public static void main(String[] args) 
    {
        try {
            Model node = new Model(4, 5, 0, 1, false);
            node.setProbability(true);
            
            for(int i=0;i<node.num_states;i++) {
                for(int j=0;j<node.num_states;j++) {
                    System.out.print(node.getA(i,j) + "\t");
                }
                System.out.println();
            }
            System.out.println();
            for(int j=0;j<node.num_states;j++) {
                System.out.print(node.getPi(j) + "\t");
            }
            System.out.println();
            System.out.println();
            /*
            for(int i=0;i<node.num_states;i++) {
                for(int k=0;k<node.num_symbols;k++) {
                    System.out.print(node.getB(i,k) + "\t");
                }
                System.out.println();
            }
            */
            
            try {
                ObjectOutputStream out =
                    new ObjectOutputStream(
                        new FileOutputStream("model.hmm"));
                out.writeObject(node);
                out.close();
            } catch(Exception exc) {
                exc.printStackTrace();
            }
            
            Model newNode = null;
            try {
                ObjectInputStream in =
                    new ObjectInputStream(
                        new FileInputStream("model.hmm"));
                newNode = (Model) in.readObject();
                in.close();
            } catch(Exception exc) {
                exc.printStackTrace();
            }
            
            for(int i=0;i<newNode.num_states;i++) {
                for(int j=0;j<newNode.num_states;j++) {
                    System.out.print(newNode.getA(i,j) + "\t");
                }
                System.out.println();
            }
            System.out.println();
            for(int j=0;j<newNode.num_states;j++) {
                System.out.print(newNode.getPi(j) + "\t");
            }
            System.out.println();
            System.out.println();
            /*
            for(int i=0;i<newNode.num_states;i++) {
                for(int k=0;k<newNode.num_symbols;k++) {
                    System.out.print(newNode.getB(i,k) + "\t");
                }
                System.out.println();
            }
            */
            
            newNode.saveModel("test.xml", true);

            newNode.loadModel("test.xml", true);
            
            //
            //newNode = new Model("model.xml", true);
            
            System.out.println(newNode.num_states);
            System.out.println(newNode.num_symbols);
            System.out.println(newNode.ll_bound);
            System.out.println(newNode.ur_bound);
            for(int i=0;i<newNode.num_states;i++) {
                for(int jj=0;jj<newNode.num_states;jj++) {
                    System.out.print(newNode.a[i][jj]);
                    System.out.print("\t");
                }
                System.out.println();
            }    
            for(int i=0;i<newNode.num_states;i++) {
                for(int k=0;k<newNode.num_symbols;k++) {
                    System.out.print(newNode.b[i][k]);
                    System.out.print("\t");
                }
                System.out.println();
            }    
            for(int jjj=0;jjj<newNode.num_states;jjj++) {
                System.out.print(newNode.pi[jjj]);
                System.out.print("\t");
            }
            System.out.println();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
