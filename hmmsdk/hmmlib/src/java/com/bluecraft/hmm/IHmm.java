/*****************************************************************************
 * Copyright (c) 2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: IHmm.java,v 1.4 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;

import com.bluecraft.common.*;


/**
 * IHmm encapsulates a discrete Hidden Markov Model (HMM)
 * and some related algorithms.
 * This interface is built on top of IModel:
 * (1) it adds a sequence of observed symbols to IModel.
 * (2) it exposes some of the core algorthms of HMM.
 * (Please refer to Rabiner 1989 for more information.)
 * 
 * NumStates: Number of distinct states in the embedded Markov model.
 * NumSymbols: Number of different observable symbols.
 * LenObSeq: Length of the observation sequence.
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.4 $
 */
public interface IHmm
    extends ITrainer, IRecognizer, IAnalyzer
{
    // TODO:
}

