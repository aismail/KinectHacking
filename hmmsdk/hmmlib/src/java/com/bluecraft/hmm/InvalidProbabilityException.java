/*****************************************************************************
 * Copyright (c) 2002-2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: InvalidProbabilityException.java,v 1.2 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;


/**
 * InvalidProbabilityException extends HmmRuntimeException.
 * This exception indicates that a value smaller than 0.0 or bigger than 1.0 is used in the context of probabilty.
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.2 $
*/
public class InvalidProbabilityException
    extends HmmRuntimeException
{
    /**
     * Constructs a new InvalidProbabilityException.
     */
    public InvalidProbabilityException()
    {
        super();
    }
    
    /**
     * Constructs a new InvalidProbabilityException with the given message.
     *
     * @param message the detail message
     */
    public InvalidProbabilityException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new InvalidProbabilityException with the specified detail message and cause. 
     *
     * @param message the detail message
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public InvalidProbabilityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidProbabilityException with the specified cause and a detail message of cause
     *
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public InvalidProbabilityException(Throwable cause)
    {
        super(cause);
    }

}
