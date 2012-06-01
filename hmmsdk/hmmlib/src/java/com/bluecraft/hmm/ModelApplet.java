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


import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;
import javax.swing.*;


/**
Demo applet for Hidden Markov Model Library in Java.

@author  Hyoungsoo Yoon
@version  0.3
*/
public class ModelApplet extends JApplet {

    JButton button;

    public void init() {

	// Force SwingApplet to come up in the System L&F
	String laf = UIManager.getSystemLookAndFeelClassName();
	try {
	    UIManager.setLookAndFeel(laf);
	    // If you want the Cross Platform L&F instead, comment out the above line and
	    // uncomment the following:
	    // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	} catch (UnsupportedLookAndFeelException exc) {
	    System.err.println("Warning: UnsupportedLookAndFeel: " + laf);
	} catch (Exception exc) {
	    System.err.println("Error loading " + laf + ": " + exc);
	}

        getContentPane().setLayout(new FlowLayout());
        button = new JButton("Hello, I'm a Swing Button!");
        getContentPane().add(button);
    }

    public void stop() {
        if (button != null) {
            getContentPane().remove(button);
            button = null;
        }
    }
    
    public static void main(String s[]) {
        JFrame f = new JFrame("ShapesDemo2D");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        JApplet applet = new ModelApplet();
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(550,100));
        f.show();
    }
}
