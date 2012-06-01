/*****************************************************************************
 * Copyright (c) 2002-2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: ProbabilityUnnormalizableException.java,v 1.4 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;


/**
 * ProbabilityUnnormalizableException extends HmmRuntimeException.
 * This exception indicates that the probability in question is not normalizable.
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.4 $
*/
public class ProbabilityUnnormalizableException
    extends HmmRuntimeException
{
    /**
     * Constructs a new ProbabilityUnnormalizableException.
     */
    public ProbabilityUnnormalizableException()
    {
        super();
    }
    
    /**
     * Constructs a new ProbabilityUnnormalizableException with the given message.
     *
     * @param message the detail message
     */
    public ProbabilityUnnormalizableException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new ProbabilityUnnormalizableException with the specified detail message and cause. 
     *
     * @param message the detail message
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public ProbabilityUnnormalizableException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new ProbabilityUnnormalizableException with the specified cause and a detail message of cause
     *
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public ProbabilityUnnormalizableException(Throwable cause)
    {
        super(cause);
    }

}
