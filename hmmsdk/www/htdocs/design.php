<?php include 'header.inc.php' ?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML lang="en">
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="robot" CONTENT="all">
<META name="summary" content="Hidden Markov Model Development Kit in Java">
<META name="keywords" content="Hidden Markov Model, HMM, HmmSDK, HmmLib, Speech Recognition, Handwriting Recognition, Biomolecular Sequencing, Java, Swing, Applet, Servlet, JSP, Hyoungsoo Yoon, Bluecraft Software">
<TITLE>Hidden Markov Model Development Kit | Design Details</TITLE>
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
HmmSDK is a hidden Markov model (HMM) software development kit written in Java. It consists of core library of HMM functions (Forward-backward, Viterbi, and Baum-Welch algorithms) and toolkits for application development.
            </P>
            <P>
            <I>(This is work in progress.)</I>
            </P>
          </TD>
          <TD vAlign=top noWrap width="1%">
              &nbsp;
          </TD>
          <TD vAlign=top noWrap width="25%">
            <?php include 'designlinks.inc.php' ?>
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

              <?php include 'hmmsdkdesign.inc.php' ?>
              <?php include 'emptyrow.inc.php' ?>

             </TBODY>
            </TABLE>
          </TD>
          <TD width=15>&nbsp;</TD>
          <TD vAlign=top width="49%">
            <TABLE cellSpacing=1 cellPadding=3 width="100%" border=0>
              <TBODY>

              <?php include 'moduledesign.inc.php' ?>
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
  Copyright (c) 2003, BlueCraft Software.  All rights reserved. 
  Requested URI: <?php echo $_SERVER['PHP_SELF']; ?> (<?php echo $_SERVER['REQUEST_URI']; ?>)
  $Id: design.php,v 1.2 2003/05/27 05:46:48 drwye Exp $
-->

