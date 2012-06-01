/*****************************************************************************
 * Copyright (c) 2002-2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: HmmRuntimeException.java,v 1.3 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;


/**
 * HmmRuntimeException is a super class of all runtime exceptions in HmmSDK.
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.3 $
*/
public class HmmRuntimeException
    extends RuntimeException
{
    /**
     * Constructs a new HmmRuntimeException.
     */
    public HmmRuntimeException()
    {
        super();
    }
    
    /**
     * Constructs a new HmmRuntimeException with the given message.
     *
     * @param message the detail message
     */
    public HmmRuntimeException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new HmmRuntimeException with the specified detail message and cause. 
     *
     * @param message the detail message
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public HmmRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new HmmRuntimeException with the specified cause and a detail message of cause
     *
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public HmmRuntimeException(Throwable cause)
    {
        super(cause);
    }

}

