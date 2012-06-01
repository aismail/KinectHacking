//////////////////////////////////////////////////////////////////////////
// The contents of this file are subject to the Mozilla Public License
// Version 1.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at
// http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS IS"
// basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
// License for the specific language governing rights and limitations under
// the License.
//
// The Original Code is Hidden Markov Model Library in Java.
//
// The Initial Developer of the Original Code is Hyoungsoo Yoon.
// Portions created by Hyoungsoo Yoon are
// Copyright (C) 1999 Hyoungsoo Yoon.  All Rights Reserved.
//
// Contributor(s):
//
//////////////////////////////////////////////////////////////////////////


/**
Hidden Markov Model Library in Java.
Please refer to Rabiner 1989.
All algorithms are directly taken from this article.
Notations and variable names also closely follow the conventions used in this paper.

@copyright  Hyoungsoo Yoon
@date  Feb 21st, 1999
*/
package com.bluecraft.hmm;


import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

         
/**
Demo program for Hidden Markov Model Library in Java

@author  Hyoungsoo Yoon
@version  0.3
*/
public class ModelBean extends Canvas implements Serializable {

    public ModelBean(){
        setSize(100,60);
        setBackground(Color.red);
    }
	
    public static void main(String[] args) {

        try {
                UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { }

        //Create the top-level container and add contents to it.
        JFrame frame = new JFrame("Hmm Model Bean");
        ModelBean mdl = new ModelBean();
        frame.getContentPane().add(mdl, BorderLayout.CENTER);

        //Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
}
