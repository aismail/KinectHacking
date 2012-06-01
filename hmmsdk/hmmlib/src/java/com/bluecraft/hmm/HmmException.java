/*****************************************************************************
 * Copyright (c) 2002-2003, Bluecraft Software. All Rights Reserved.
 * This software is the proprietary information of Bluecraft Software
 * and it is supplied subject to license terms.
 * See the HmmSDK Home Page (http://hmmsdk.sourceforge.net/) for more details.
 *****************************************************************************/
// $Id: HmmException.java,v 1.3 2003/05/31 06:00:58 drwye Exp $

package com.bluecraft.hmm;


/**
 * HmmException is a super class of all <i>checked</i> exceptions in HmmSDK.
 * 
 * @author   <A href="mailto:hyoon@bluecraft.com">Hyoungsoo Yoon</A>
 * @version  $Revision: 1.3 $
 */
public class HmmException
    extends Exception
{
    /**
     * Constructs a new HmmException.
     */
    public HmmException()
    {
        super();
    }
    
    /**
     * Constructs a new HmmException with the given message.
     *
     * @param message the detail message
     */
    public HmmException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new HmmException with the specified detail message and cause. 
     *
     * @param message the detail message
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public HmmException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new HmmException with the specified cause and a detail message of cause
     *
     * @param cause the cause. null value means that the cause is nonexistent or unknown.
     */
    public HmmException(Throwable cause)
    {
        super(cause);
    }

}

