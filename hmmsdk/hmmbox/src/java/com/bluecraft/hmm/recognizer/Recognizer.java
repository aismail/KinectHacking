/*****************************************************************************
 * Copyright (c) 2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: Recognizer.java,v 1.7 2003/05/31 06:01:11 drwye Exp $

package com.bluecraft.hmm.recognizer;

import com.bluecraft.common.*;
import com.bluecraft.util.*;
import com.bluecraft.hmm.*;
//import com.bluecraft.hmm.util.*;
import com.bluecraft.hmm.shell.util.*;


/**
 * Recognizer
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.7 $
 */
public class Recognizer
    implements IRecognizer
{
    // TODO:

    // Aggregated Hmm.
    private IHmm mHmm = null;

    /**
     * Retrieves the number of internal states.
     * 
     * @return  Number of internal states of the underlying model.
     */
    public int getNumStates() 
    {
        return mHmm.getNumStates();
    }

    /**
     * Retrieves the number of observation symbols.
     * 
     * @return  Number of observation symbols.
     */
    public int getNumSymbols() 
    {
        return mHmm.getNumSymbols();
    }

    /**
     * Retrieves the length of the observation sequence.
     * 
     * @return  Length of the observation sequence.
     */
    public int getLenObSeq() 
    {
        return mHmm.getLenObSeq();
    }

    
    /**
     *
     */
    public static void main(String[] args)
    {
        IRecognizer recognizer = new Recognizer();
        
        System.out.println("Recognizer = " + recognizer);

    }

}

