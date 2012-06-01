/*****************************************************************************
 * Copyright (c) 1998-2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: Hmm.java,v 1.8 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;

import com.bluecraft.hmm.util.*;

import java.util.*;
import java.io.*;


/**
 * Hmm implements IHmm which encapsulates a discrete Hidden Markov Model (HMM)
 * and some related algorithms.
 * This class adds a sequence of observed symbols to the class Model 
 * (so that HMM can be used as a trainer or recognizer),
 * and it implements some of the most important algorithms of HMM.
 * (Please refer to Rabiner 1989 for more information.)
 * 
 * NumStates: Number of distinct states in the embedded Markov model.
 * NumSymbols: Number of different observable symbols.
 * LenObSeq: Length of the observation sequence.
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.8 $
 */
public class Hmm 
    implements IHmm, Serializable 
{
    // Maximum number of iteration.
    private static final int MAX_ITERATION = 100;
    // Error below which the value is assumed to have converged to a solution.
    private static final double MIN_ERROR = 0.001;
    // Maximum length of observation sequence.
    private static final int MAX_LEN_OBSEQ = 10;

    // Random number generator.
    private static Random rand = new Random();


    /**
     * @serial  Underlying Markov model and its parameters (A, B, and pi)
     */
    private Model lambda;

    /**
     * @serial  Forward variables  (Rabiner eq. 18)
     */
    private double[][] alpha;

    /**
     * @serial  Backward variables  (Rabiner eq. 23)
     */
    private double[][] beta;

    /**
     * @serial  Internal state probabilities (Rabiner eq. 26)
     */
    private double[][] gamma;

    /**
     * @serial  Internal pair-state probabilities (Rabiner eq. 36)
     */
    private double[][][] ksi;

    /**
     * @serial  Best score variables (Rabiner eq. 30)
     */
    private double[][] delta;

    /**
     * @serial  Backtracking variables (Rabiner eq. 33b)
     */
    private int[][] psi;

    /**
     * @serial  Most likely path (Rabiner eq. 35)
     */
    private int[] qstar;

    /**
     * @serial  Scale factor (Rabiner eq. 91)
     */
    private double[] scaleFactor;

    // Length of observation sequence.
    private transient int  len_obseq = 0;
    // Observation sequence.
    private transient int[] obSeq;
    // File name from and to which the HMM will be read and saved.
    private transient String file_name = null;



    /**
     * Construct HMM with a minimum size (length of observation sequence: 1).
     * The elements of the matrices are initialized by random values.
     */
    public Hmm() 
        throws HmmConstructionException
    {
        this(true);
    }

    /**
     * Construct HMM with a minimum size (length of observation sequence: 1).
     * 
     * @param bRandomize If true, the elements of the matrices are initialized by random values
     */
    public Hmm(boolean bRandomize) 
        throws HmmConstructionException
    {
        this(1, bRandomize);
    }

    /**
     * Construct HMM by specifying its length of observation sequence. 
     * (number of states: 1, number of symbols: 1).
     * The elements of the matrices are initialized by random values.
     * 
     * @param lobseq Length of symbols observation sequence
     */
    public Hmm(int lobseq) 
        throws HmmConstructionException
    {
        this(lobseq, true);
    }

    /**
     * Construct HMM by specifying its length of observation sequence. 
     * (number of states: 1, number of symbols: 1).
     * 
     * @param lobseq Length of symbols observation sequence
     * @param bRandomize If true, the elements of the matrices are initialized by random values
     */
    public Hmm(int lobseq, boolean bRandomize) 
        throws HmmConstructionException
    {
        this(lobseq, 1, 1, 0, 0, bRandomize);
    }

    /**
     * Construct HMM by specifying its length, number of states, and number of symbols.
     * The elements of the matrices are initialized by random values.
     * 
     * @param lobseq Length of symbols observation sequence
     * @param nstates Number of states
     * @param nsymbols Number of symbols
     */
    public Hmm(int lobseq, int nstates, int nsymbols) 
        throws HmmConstructionException
    {
        this(lobseq, nstates, nsymbols, true);
    }

    /**
     * Construct HMM by specifying its length, number of states, and number of symbols.
     * 
     * @param lobseq Length of symbols observation sequence
     * @param nstates Number of states
     * @param nsymbols Number of symbols
     * @param bRandomize If true, the elements of the matrices are initialized by random values
     */
    public Hmm(int lobseq, int nstates, int nsymbols, boolean bRandomize) 
        throws HmmConstructionException
    {
        this(lobseq, nstates, nsymbols, nstates-1, nstates-1, bRandomize);
    }

    /**
     * Construct HMM by specifying its length, number of states, and number of symbols.
     * Lower bound and upper bound  along the diagonal direction are also specified, outside which all probability elements are identically zero.
     * The elements of the matrices are initialized by random values.
     * 
     * @param lobseq Length of symbols observation sequence
     * @param nstates Number of states
     * @param nsymbols Number of symbols
     * @param llb Lower bound below which all probability elements are identically zero.
     * @param urb Upper bound beyond which all probability elements are identically zero.
     */
    public Hmm(int lobseq, int nstates, int nsymbols, int llb, int urb) 
        throws HmmConstructionException
    {
        this(lobseq, nstates, nsymbols, llb, urb, true);
    }    

    /**
     * Construct HMM by specifying its length, number of states, and number of symbols.
     * Lower bound and upper bound along the diagonal direction are also specified, outside which all probability elements are identically zero.
     * 
     * @param lobseq Length of symbols observation sequence
     * @param nstates Number of states
     * @param nsymbols Number of symbols
     * @param llb Lower bound below which all probability elements are identically zero.
     * @param urb Upper bound beyond which all probability elements are identically zero.
     * @param bRandomize If true, the elements of the matrices are initialized by random values
     */
    public Hmm(int lobseq, int nstates, int nsymbols, int llb, int urb, boolean bRandomize) 
        throws HmmConstructionException
    {
        lambda = new Model(nstates, nsymbols, llb, urb, bRandomize);
    
        len_obseq = lobseq;
        
        alpha = new double[len_obseq][getNumStates()];
        beta = new double[len_obseq][getNumStates()];
        gamma = new double[len_obseq][getNumStates()];
        ksi = new double[len_obseq-1][getNumStates()][getNumStates()];
        delta = new double[len_obseq][getNumStates()];
        psi = new int[len_obseq][getNumStates()];
        qstar = new int[len_obseq];
        scaleFactor = new double[len_obseq];
        obSeq = new int[len_obseq];
        
        initializeModel(bRandomize);
    }

    /**
     * Construct HMM by loading model parameters from an XML file.
     * 
     * @param file Name of input file
     */
    public Hmm(String file) 
        throws HmmConstructionException
    {
        this(file, 1, true);
    }    

    /**
     * Construct HMM by loading model parameters from an XML file.
     * 
     * @param file Name of input file
     * @param lobseq Length of observation sequence
     */
    public Hmm(String file, int lobseq) 
        throws HmmConstructionException
    {
        this(file, lobseq, true);
    }    

    /**
     * Construct HMM by loading model parameters from a file.
     * 
     * @param file Name of input file
     * @param bXml If true, read input file in XML format
     */
    public Hmm(String file, boolean bXml) 
        throws HmmConstructionException
    {
        this(file, 1, bXml);
    }    

    /**
     * Construct HMM by loading model parameters from a file.
     *
     * @param file Name of input file
     * @param lobseq Length of observation sequence
     * @param bXml If true, read input file in XML format
     */
    public Hmm(String file, int lobseq, boolean bXml) 
        throws HmmConstructionException
    {
        lambda = new Model(file, bXml);
        len_obseq = lobseq;
        
        alpha = new double[len_obseq][getNumStates()];
        beta = new double[len_obseq][getNumStates()];
        gamma = new double[len_obseq][getNumStates()];
        ksi = new double[len_obseq-1][getNumStates()][getNumStates()];
        delta = new double[len_obseq][getNumStates()];
        psi = new int[len_obseq][getNumStates()];
        qstar = new int[len_obseq];
        scaleFactor = new double[len_obseq];
        obSeq = new int[len_obseq];
    }    



    /**
     * Computes scale factors.
     */
    private void computeScaleFactor() 
    {
        for(int t=0;t<len_obseq;t++) {
            computeScaleFactor(t);
        }    
    }

    /**
     * Computes the scale factor at time t.
     * 
     * @param t Observation time
     */
    private void computeScaleFactor(int t) 
    {
        scaleFactor[t] = 0.0;
        for(int i=0;i<getNumStates();i++) {
            scaleFactor[t] += alpha[t][i];
        }    
    }

    /**
     * Rescales Alpha with a new scale.
     */
    private void rescaleAlpha() 
    {
        rescaleAlpha(true);
    }

    /**
     * Rescales Alpha.
     * 
     * @param bNewScale If true, recompute the scale factors
     */
    private void rescaleAlpha(boolean bNewScale) 
    {
        if(bNewScale) {
            computeScaleFactor();
        }
        for(int t=0;t<len_obseq;t++) {
            rescaleAlpha(t);
        }    
    }

    /**
     * Rescales Alpha at time t with a new scale factor.
     * 
     * @param t Observation time
     */
    private void rescaleAlpha(int t) 
    {
        rescaleAlpha(t,true);
    }
    
    /**
     * Rescales Alpha at time t.
     * 
     * @param t Observation time
     * @param bNewScale If true, recompute the scale factors
     */
    private void rescaleAlpha(int t, boolean bNewScale) 
    {
        if(bNewScale) {
            computeScaleFactor(t);
        }
        for(int i=0;i<getNumStates();i++) {
            alpha[t][i] /= scaleFactor[t];
        }    
    }
    
    /**
     * Rescales Beta.
     * (It reuses the current scale factors.)
     */
    private void rescaleBeta() 
    {
        rescaleBeta(false);
    }

    /**
     * Rescales Beta.
     * 
     * @param bNewScale If true, recompute the scale factors
     */
    private void rescaleBeta(boolean bNewScale) 
    {
        if(bNewScale) {
            computeScaleFactor();
        }
        for(int t=0;t<len_obseq;t++) {
            rescaleBeta(t);
        }    
    }

    /**
     * Rescales Beta at time t.
     * (It reuses the current scale factor.)
     * 
     * @param t Observation time
     */
    private void rescaleBeta(int t) 
    {
        rescaleBeta(t,false);
    }

    /**
     * Rescales Beta at time t.
     * 
     * @param t Observation time
     * @param bNewScale If true, recompute the scale factors
     */
    private void rescaleBeta(int t, boolean bNewScale) 
    {
        if(bNewScale) {
            computeScaleFactor(t);
        }
        for(int i=0;i<getNumStates();i++) {
            beta[t][i] /= scaleFactor[t];
        }    
    }

    /**
     * Rescales Alphas and Betas.
     * (New scale factors will be used for Alphas, whereas the current scale factors will be reused for Betas.)
     */
    private void rescaleTrellis() 
    {
        for(int t=0;t<len_obseq;t++) {
            rescaleTrellis(t);
        }    
    }

    /**
     * Rescalse Alpha and Beta at time t.
     * (A new scale factor will be used for Alpha, whereas the current scale factor will be reused for Beta.)
     * 
     * @param t Observation time
     */
    private void rescaleTrellis(int t) 
    {
        rescaleAlpha(t,true);
        rescaleBeta(t,false);
    }

    /**
     * Resets all Alphas to zero.
     */
    private void clearAlpha() 
    {
        for(int t=0;t<len_obseq;t++) {
            for(int i=0;i<getNumStates();i++) {
                alpha[t][i] = 0.0;
            }
        }    
    }

    /**
     * Resets all Betas to zero.
     */
    private void clearBeta() 
    {
        for(int t=0;t<len_obseq;t++) {
            for(int i=0;i<getNumStates();i++) {
                beta[t][i] = 0.0;
            }
        }    
    }

    /**
     * Resets all Gammas to zero.
     */
    private void clearGamma() 
    {
        for(int t=0;t<len_obseq;t++) {
            for(int i=0;i<getNumStates();i++) {
                gamma[t][i] = 0.0;
            }
        }    
    }

    /**
     * Resets all Alphas, Betas, and Gammas to zero.
     */
    private void clearTrellis() 
    {
        clearAlpha();
        clearBeta();
        clearGamma();
    }


    /**
     * "Forward" part of the forward-backward algorithm.
     * 
     * @return Probability of the given sequence given lambda
     */
    private double forwardAlpha() 
    {
        clearAlpha();
        for(int j=0;j<getNumStates();j++) {
            alpha[0][j] = lambda.getPi(j) * lambda.getB(j,obSeq[0]);
        }    
        rescaleAlpha(0);

        for(int t=1;t<len_obseq;t++) {
            for(int i=0;i<getNumStates();i++) {
                double sum = 0.0;
                for(int j=lambda.getLLimit(i);j<=lambda.getRLimit(i);j++) {
                    sum += alpha[t-1][i] * lambda.getA(i,j);
                }
                alpha[t][i] = sum*lambda.getB(i,obSeq[t]);
            }
            rescaleAlpha(t);
        }    
        double sum = 0.0;
        for(int i=0;i<getNumStates();i++) {
            sum += alpha[len_obseq-1][i];
        }    
        return sum;
    }    

    /**
     * "Backward" part of the forward-backward algorithm.
     */
    private void backwardBeta() 
    {
        clearBeta();
        for(int j=0;j<getNumStates();j++) {
            beta[len_obseq-1][j] = 1.0;
        }    
        rescaleBeta(len_obseq-1);

        for(int t=len_obseq-2;t>=0;t--) {
            for(int i=0;i<getNumStates();i++) {
                double sum = 0.0;
                for(int j=lambda.getLLimit(i);j<=lambda.getRLimit(i);j++) {
                    sum += beta[t+1][i] * lambda.getA(i,j) * lambda.getB(j,obSeq[t+1]);
                }
                beta[t][i] = sum;
            }
            rescaleBeta(t);
        }    
    }    

    /**
     * Computes Gammas in the forward-backward algorithm.
     */
    private void computeGamma() 
    {
        for(int t=0;t<len_obseq;t++) {
            double sum = 0.0;
            for(int i=0;i<getNumStates();i++) {
                gamma[t][i] = alpha[t][i] * beta[t][i];
                sum += gamma[t][i];
            }
            for(int i=0;i<getNumStates();i++) {
                gamma[t][i] /= sum;
            }
        }    
    }

    /**
     * Computes Deltas.
     *
     * @param t Observation time
     * @param j State index
     * @return Delta value at time t for the state j
     */
    private double computeDelta(int t, int j) 
    {
        if(t==0) {
            psi[t][j] = 0;
            return delta[t][j] = lambda.getPi(j) * lambda.getB(j,obSeq[t]);
        }
        else if(t==len_obseq) {
            double max = 0.0;
            double tmp;
            int indx = 0;
            for(int i=0;i<getNumStates();i++) {
                tmp = computeDelta(t-1,i);
                if(tmp >= max) {
                    max = tmp;
                    indx = i;
                }
            }
            qstar[t-1] = indx;
            for(int s=t-2;s>=0;s--) {
                qstar[s] = psi[s+1][qstar[s+1]];
            }    
            return max;
        }
        else {
            double max = 0.0;
            for(int i=0;i<getNumStates();i++) {
                double tmp = computeDelta(t-1,i) * lambda.getA(i,j) * lambda.getB(j,obSeq[t]);
                if(tmp >= max) {
                    max = tmp;
                }
            }
            double amax = 0.0;
            int indx = 0;
            for(int i=0;i<getNumStates();i++) {
                double atmp = computeDelta(t-1,i) * lambda.getA(i,j);
                if(atmp >= amax) {
                    amax = atmp;
                    indx = i;
                }
            }
            psi[t][j] = indx;
            return delta[t][j] = max;
        }    
    }

    /**
     * Viterbi Algorithm.
     * 
     * @return Delta value at the end of observation
     */
    public double viterbiAlgorithm() 
    {
        return computeDelta(len_obseq,0); // 0 arbitrary
    }

    /**
     * Computes Ksis in the forward-backward algorithm.
     */
    private void computeKsi() 
    {
        for(int t=0;t<len_obseq-1;t++) {
            double sum = 0.0;
            for(int i=0;i<getNumStates();i++) {
                for(int j=lambda.getLLimit(i);j<=lambda.getRLimit(i);j++) {
                    ksi[t][i][j] = alpha[t][i] * beta[t+1][i] * lambda.getA(i,j) * lambda.getB(j,obSeq[t+1]);
                    sum += ksi[t][i][j];
                }    
            }
            for(int i=0;i<getNumStates();i++) {
                for(int j=lambda.getLLimit(i);j<=lambda.getRLimit(i);j++) {
                    ksi[t][i][j] /= sum;
                }    
            }
        }    
    }

    /**
     * Forward-backward algorithm.
     * 
     * @return Probability of the given sequence given lambda
     */
    private double forwardBackwardTrellis() 
    {
        double ret = forwardAlpha();
        backwardBeta();
        computeGamma();
        computeKsi();
        return ret;
    }

    /**
     * Returns the sum of Gammas at time t for the given state i.
     * 
     * @param t Observation time
     * @param i State index
     * @return Sum of Gammas
     */
    private double sumGamma(int t, int i) 
    {
        double sum = 0.0;
        for(int s=0;s<t;s++) {
            sum += gamma[s][i];
        }
        return sum;
    }  
    
    /**
     * Returns the sum of Gammas at time t for the given state i and for the observation symbol k.
     * 
     * @param t Observation time
     * @param i State index
     * @param k Observation symbol index
     * @return Sum of Gammas
     */  
    private double sumGamma(int t, int i, int k) 
    {
        double sum = 0.0;
        for(int s=0;s<t;s++) {
            if(obSeq[s] == k) {
                sum += gamma[s][i];
            }    
        }
        return sum;
    }

    /**
     * Returns the sum of Ksis at time t for the given states i and j.
     * 
     * @param t Observation time
     * @param i State index
     * @param j State index
     * @return Sum of Ksis
     */         
    private double sumKsi(int t, int i, int j) 
    {
        double sum = 0.0;
        for(int s=0;s<t;s++) {
            sum += ksi[s][i][j];
        }
        return sum;
    }

    /**
     * Re-estimates Lambda.
     */
    private void reestimateLambda() 
    {
        // (1) pi
        double[] rowPi = new double[getNumStates()];
        for(int i=0;i<getNumStates();i++) {
            rowPi[i] = gamma[0][i];
        }
        lambda.setPi(rowPi);

        // (2) a
        for(int i=0;i<getNumStates();i++) {
            double[] rowA = new double[getNumStates()];
            double a_denom = sumGamma(len_obseq-1,i);
            for(int j=lambda.getLLimit(i);j<=lambda.getRLimit(i);j++) {
                rowA[j] = sumKsi(len_obseq-1,i,j)/a_denom;
            }
            lambda.setA(i,rowA);
        }

        // (3) b
        for(int i=0;i<getNumStates();i++) {
            double[] rowB = new double[getNumSymbols()];
            double b_denom = sumGamma(len_obseq,i);
            for(int k=0;k<getNumSymbols();k++) {
                rowB[k] = sumGamma(len_obseq,i,k)/b_denom;
            }
            lambda.setB(i,rowB);
        }
    }


    /**
     * Retrieves the number of internal states.
     * 
     * @return  Number of internal states of the underlying model.
     */
    public int getNumStates() 
    {
        return lambda.getNumStates();
    }

    /**
     * Retrieves the number of observation symbols.
     * 
     * @return  Number of observation symbols.
     */
    public int getNumSymbols() 
    {
        return lambda.getNumSymbols();
    }

    /**
     * Retrieves the length of the observation sequence.
     * 
     * @return  Length of the observation sequence.
     */
    public int getLenObSeq() 
    {
        return len_obseq;
    }


    /**
     * Load model parameters from an XML file.
     * 
     * @param file Name of input file
     */
    public void loadHmm(String file) 
        throws HmmConstructionException
    {
        loadHmm(file, 1, true);
    }

    /**
     * Load model parameters from an XML file.
     * 
     * @param file Name of input file
     * @param bXml If true, read file in XML format
     */
    public void loadHmm(String file, boolean bXml) 
        throws HmmConstructionException
    {
        loadHmm(file, 1, bXml);
    }

    /**
     * Load model parameters from a file.
     * 
     * @param file Name of input file
     * @param lobseq Length of observation sequence
     */
    public void loadHmm(String file, int lobseq) 
        throws HmmConstructionException
    {
        loadHmm(file, lobseq, true);
    }

    /**
     * Load model parameters from a file.
     * 
     * @param file Name of input file
     * @param lobseq Length of observation sequence
     * @param bXml If true, read file in XML format
     */
    public void loadHmm(String file, int lobseq, boolean bXml) 
        throws HmmConstructionException
    {
        if(bXml) {
            loadHmmXml(file, lobseq);
        } else {
            loadHmmAscii(file, lobseq);
        }
    }

    /**
     * Load model parameters from an Ascii file.
     * Length of observation sequence is set to 1.
     * 
     * @param file Name of input file
     */
    public void loadHmmAscii(String file) 
        throws HmmConstructionException
    {
        loadHmmAscii(file, 1);
    }

    /**
     * Load model parameters from an Ascii file.
     * 
     * @param file Name of input file
     * @param lobseq Length of observation sequence
     */
    public void loadHmmAscii(String file, int lobseq) 
        throws HmmConstructionException
    {
        lambda.loadModelAscii(file);
        len_obseq = lobseq;
        
        alpha = new double[len_obseq][getNumStates()];
        beta = new double[len_obseq][getNumStates()];
        gamma = new double[len_obseq][getNumStates()];
        ksi = new double[len_obseq-1][getNumStates()][getNumStates()];
        delta = new double[len_obseq][getNumStates()];
        psi = new int[len_obseq][getNumStates()];
        qstar = new int[len_obseq];
        scaleFactor = new double[len_obseq];
        obSeq = new int[len_obseq];
    }

    /**
     * Load model parameters from an Xml file.
     * Length of observation sequence is set to 1.
     * 
     * @param file Name of input file
     */
    public void loadHmmXml(String file) 
        throws HmmConstructionException
    {
        loadHmmXml(file, 1);
    }

    /**
     * Load model parameters from an Xml file.
     * 
     * @param file Name of input file
     * @param lobseq Length of the observation sequence
     */
    public void loadHmmXml(String file, int lobseq) 
        throws HmmConstructionException
    {
        lambda.loadModelXml(file);
        len_obseq = lobseq;
        
        alpha = new double[len_obseq][getNumStates()];
        beta = new double[len_obseq][getNumStates()];
        gamma = new double[len_obseq][getNumStates()];
        ksi = new double[len_obseq-1][getNumStates()][getNumStates()];
        delta = new double[len_obseq][getNumStates()];
        psi = new int[len_obseq][getNumStates()];
        qstar = new int[len_obseq];
        scaleFactor = new double[len_obseq];
        obSeq = new int[len_obseq];
    }


    /**
     * Initializes the model with random parameters.
     */
    public void initializeModel() 
    {
        initializeModel(true);
    }

    /**
     * Initializes the model.
     * 
     * @param bRandomize  If true, initialize the parameters with random values
     */
    public void initializeModel(boolean bRandomize) 
    {
        lambda.setProbability(bRandomize);
    }


    /**
     * Sets the observation sequence with random vlaues.
     */
    public void setObSeq() 
    {
        setObSeq(true);
    }

    /**
     * Sets the observation sequence.
     * 
     * @param bRandomize  If true, set the sequence with random values
     */
    public void setObSeq(boolean bRandomize) 
    {
        if(bRandomize == true) {
            for(int t=0;t<len_obseq;t++) {
                obSeq[t] = (int) (getNumSymbols() * rand.nextDouble());
            }    
        }
        else {
            for(int t=0;t<len_obseq;t++) {
                obSeq[t] = 0;
            }    
        }
    }

    /**
     * Sets the observation sequence.
     * 
     * @param seq  Observation sequence to be assigned
     */
    public void setObSeq(int[] seq) 
    {
        for(int t=0;t<len_obseq;t++) {
            obSeq[t] = seq[t];
        }    
    }

    /**
     * Retrieves the observation symbol at time t.
     * 
     * @param t  Time at which the observation symbol is to be retrieved
     */
    public int getObSeq(int t) 
    {
        return obSeq[t];
    }    

    /**
     * Retrieves the observation sequence.
     */
    public int[] getObSeq() 
    {
        return obSeq;
    }    


    /**
     * Baum-Welch (EM) algorithm.
     * Train the HMM until MIN_ERROR is reached.
     * It also stops if the iteration reaches MAX_ITERATION.
     * 
     * @return  Likelihood of the model given the observation sequence after training
     */
    public double baumWelchAlgorithm() 
    {
        return baumWelchAlgorithm(MIN_ERROR);
    }

    /**
     * Baum-Welch (EM) algorithm.
     * Train the HMM until likelihood difference becomes smaller than the given min_error.
     * It also stops if the iteration reaches MAX_ITERATION.
     * 
     * @param  min_error  Minimum desired error
     * @return  Likelihood of the model given the observation sequence after training
     */
    public double baumWelchAlgorithm(double min_error) 
    {
        double diff=1.0;
        double p1 = forwardBackwardTrellis();
        double p2 = 1.0;
        for(int n=0;n<MAX_ITERATION;n++) {
            reestimateLambda();
            p2 = forwardBackwardTrellis();
            diff = p2 - p1;
            p1 = p2;
            if(diff <= min_error) {
                break;
            }
        }
        return p1;
    }

    /**
     * Generates random observation sequence.
     * 
     * @return  Randomly generated observation sequence according to the model
     */
    public int[] generateSeq() 
    {
        int[] seq = new int[len_obseq];
        for(int t=0;t<len_obseq;t++) {
            double r = rand.nextDouble();
            double p = 0.0;
            for(int k=0;k<getNumSymbols();k++) {
                double s = 0.0;
                for(int i=0;i<getNumStates();i++) {
                    s += lambda.getB(i,k) * lambda.getPi(t,i);
                }
                p += s;
                if(p > r) {
                    seq[t] = k;
                    break;
                }
            }
        }
        return seq;
    }


    /**
     * One of the three canonical questions of HMM a la Ferguson-Rabiner.
     * [1] Given the observation sequence obSeq and a model lambda,
     *      what is the probability of the given sequence given lambda?
     * 
     * @return  Probability of the given sequence given lambda
     */
    public double getObSeqProbability() 
    {
        /*
        double logP = Math.log(forwardAlpha());
        for(int t=0;t<len_obseq;t++) {
            logP += Math.log(scaleFactor[t]);
        }
        return logP;
        */
        return forwardAlpha();
    }

    /**
     * One of the three canonical questions of HMM a la Ferguson-Rabiner.
     * [2] Given the observation sequence obSeq and a model lambda,
     *      what is the "optimal" sequence of hidden states?
     * 
     * @return  Most likely sequence of hidden states
     */
    public int[] getMaxLikelyState() 
    {
        viterbiAlgorithm();
        return qstar;
    }
    
    /**
     * One of the three canonical questions of HMM a la Ferguson-Rabiner.
     * [3] How do we adjust the model parameters lambda
     *      to maximize the likelihood of the given sequence obSeq?
     * 
     * @return Locally optimal likelihood value of the given sequence
     */
    public double optimizeLambda() 
    {
        return baumWelchAlgorithm();
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
            Hmm h = new Hmm(3, 4, 5, 0, 1, false);
            
            for(int i=0;i<h.getNumStates();i++) {
                for(int j=0;j<h.getNumStates();j++) {
                    System.out.print(h.lambda.getA(i,j) + "\t");
                }
                System.out.println();
            }
            System.out.println();
            for(int j=0;j<h.getNumStates();j++) {
                System.out.print(h.lambda.getPi(j) + "\t");
            }
            System.out.println();
            System.out.println();
            for(int i=0;i<h.getNumStates();i++) {
                for(int k=0;k<h.getNumSymbols();k++) {
                    System.out.print(h.lambda.getB(i,k) + "\t");
                }
                System.out.println();
            }
            System.out.println();
            
            for(int z=0;z<10;z++) {
                int[] seq = h.generateSeq();
                for(int t=0;t<seq.length;t++) {
                    System.out.print(seq[t] + "\t");
                }
                System.out.println();
            }    
            System.out.println();
            
            /*
            h.setObSeq();
            for(int t=0;t<h.getLenObSeq();t++) {
                System.out.print(h.getObSeq(t) + "\t");
            }
            System.out.println();
            System.out.println();
            */
            
            //double p = h.optimizeLambda();
            //double p = h.getObSeqProbability();
            //System.out.println(p);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}

