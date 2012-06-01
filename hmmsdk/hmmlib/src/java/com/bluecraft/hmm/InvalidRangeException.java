/*****************************************************************************
 * Copyright (c) 2002-2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: InvalidRangeException.java,v 1.2 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;


/**
 * InvalidRangeException extends HmmRuntimeException.
 * This exception indicates that an element outside the valid range is being accessed.
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.2 $
*/
public class InvalidRangeException
    extends HmmRuntimeException
{
    /**
     * Constructs a new InvalidRangeException.
     */
    public InvalidRangeException()
    {
        super();
    }
    
    /**
     * Constructs a new InvalidRangeException with the given message.
     *
     * @param message the detail message
     */
    public InvalidRangeException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new InvalidRangeException with the specified detail message and cause. 
     *
     * @param message the detail message
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public InvalidRangeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidRangeException with the specified cause and a detail message of cause
     *
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public InvalidRangeException(Throwable cause)
    {
        super(cause);
    }

}
