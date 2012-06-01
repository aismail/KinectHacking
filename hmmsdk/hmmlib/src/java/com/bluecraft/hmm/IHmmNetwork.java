/*****************************************************************************
 * Copyright (c) 2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: IHmmNetwork.java,v 1.3 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;


/**
 * IHmmNetwork encapsulates a discrete Hidden Markov Model (HMM).
 * (Please refer to Rabiner 1989 for more information.)
 * 
 * NumStates: Number of distinct states in the embedded Markov model.
 * NumSymbols: Number of different observable symbols.
 * LenObSeq: Length of the observation sequence.
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.3 $
 */
public interface IHmmNetwork
    extends IModel
{

    /**
     * Retrieves the length of the observation sequence.
     * 
     * @return  Length of the observation sequence.
     */
    int getLenObSeq();


}

