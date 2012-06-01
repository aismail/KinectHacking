/*****************************************************************************
 * Copyright (c) 2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: IModel.java,v 1.3 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;

import com.bluecraft.common.*;


/**
 * IModel represents a discrete Hidden Markov Model (HMM).
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
 * @version  $Revision: 1.3 $
 */
public interface IModel
{
    // TODO:
    
    /**
     * Gets the number of states in the embedded Markov model.
     * 
     * @return  Number of states in the Markov model 
     */
    int getNumStates();

    /**
     * Gets the number of observable symbols.
     * 
     * @return  Number of observable symbols 
     */
    int getNumSymbols();



}

