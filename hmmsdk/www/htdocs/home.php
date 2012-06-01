<?php include 'header.inc.php' ?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML lang="en">
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="robot" CONTENT="all">
<META name="summary" content="Hidden Markov Model Development Kit in Java">
<META name="keywords" content="Hidden Markov Model, HMM, HmmSDK, HmmLib, Speech Recognition, Handwriting Recognition, Biomolecular Sequencing, Java, Swing, Applet, Servlet, JSP, Hyoungsoo Yoon, Bluecraft Software">
<TITLE>Hidden Markov Model Development Kit | Project Info</TITLE>
<LINK rel="STYLESHEET" href="styles/hmmhome.css" type="text/css">
<LINK rel="SHORTCUT ICON" href="/images/hmmsdk.ico">
</HEAD>
<BODY>

<TABLE cellSpacing=0 cellPadding=8 width="100%" border=0>
  <TBODY>
  <TR>
    <TD vAlign=top align=left width="100%">
      <?php include 'top.inc.php' ?>
    </TD>
  </TR>
  <TR>
    <TD vAlign=top align=left width="100%">

      <TABLE width="100%" border=0>
        <TBODY>
        <TR>
          <TD vAlign=top width="73%">
            <P>
A hidden Markov model (HMM) is a stochastic model mainly used to describe random temporal processes or sequences of symbols. It is one of the primary tools for speech recognition/language processing, on-line handwriting recognition, and biomolecular sequencing. The purpose of this project is to improve on the open-source HMM library I wrote several years ago, HmmLib, and to build a complete development environment for HMM-related tools. 
            </P>
            <P>
HmmSDK consists of a core library, HmmLib, and a few additional utility libraries and tools. All tools/libraries are written in Java.
            </P>
            <P>
            <DL>
            <DT class="moduletitle">(HmmAnt)</DT>
            <DD>HmmAnt is a library of Ant custom tasks used throughout the project.</DD>
            <DT class="moduletitle">HmmLib</DT>
            <DD>HmmLib is a core HMM class library. It provides implementations of essential functions of (discrete) hidden Markov models, including Forward-backward, Viterbi, and Baum-Welch algorithms.</DD>
            <DT class="moduletitle">HmmBox</DT>
            <DD>HmmBox is a text or console-based test tool. It can also be used as a general-purpose "shell".</DD>
            <DT class="moduletitle">(HmmGUI)</DT>
            <DD>HmmGUI is a GUI library based on Swing.</DD>
            <DT class="moduletitle">(HmmIDE)</DT>
            <DD>HmmIDE is an integrated development environment for building tools which use HmmLib.</DD>
<!--
            <DT class="moduletitle">(HmmPad)</DT>
            <DD>HmmPad is a tool for...</DD>
            <DT class="moduletitle">(HmmWiz)</DT>
            <DD>HmmWiz is a tool with wizard-like interface...</DD>
-->
            <DT class="moduletitle">(HmmWeb)</DT>
            <DD>HmmWeb is a HTTP/servlet library used to expose the HMM core functionalities to the web.</DD>
            </DL>
            </P>
          </TD>
          <TD vAlign=top noWrap width="1%">
              &nbsp;
          </TD>
          <TD vAlign=top noWrap width="25%">
            <?php include 'projectlinks.inc.php' ?>
          </TD>
         </TR>
         </TBODY>
      </TABLE>

    </TD>
  </TR>
  <TR>
    <TD vAlign=top align=left width="100%">

      <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
        <TBODY>
        <TR>
          <TD vAlign=top width="49%">
            <TABLE cellSpacing=1 cellPadding=3 width="100%" border=0>
              <TBODY>

              <?php include 'projectstatus.inc.php' ?>
              <?php include 'emptyrow.inc.php' ?>
              <?php include 'todolist.inc.php' ?>

             </TBODY>
            </TABLE>
          </TD>
          <TD width=15>&nbsp;</TD>
          <TD vAlign=top width="49%">
            <TABLE cellSpacing=1 cellPadding=3 width="100%" border=0>
              <TBODY>

              <?php include 'requirements.inc.php' ?>
              <?php include 'emptyrow.inc.php' ?>
              <?php include 'sourceforgelogo.inc.php' ?>

             </TBODY>
            </TABLE>
          </TD>
       </TR>
     </TBODY>
    </TABLE>

  </TD>
</TR>
</TBODY>
</TABLE>

</BODY>
</HTML>

<!--
  Copyright (c) 2003, Bluecraft Software.  All rights reserved. 
  Requested URI: <?php echo $_SERVER['PHP_SELF']; ?> (<?php echo $_SERVER['REQUEST_URI']; ?>)
  $Id: home.php,v 1.5 2003/08/22 02:30:04 drwye Exp $
-->

