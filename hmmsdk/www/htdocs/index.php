<?php
    $host = $_SERVER['HTTP_HOST'];
    $path = dirname($_SERVER['PHP_SELF']);
    $home = "home.php";
    $uri = "";
    if(strlen($path) > 0 && substr($path,strlen($path)-1) == "/") {
        $uri = $host.$path.$home;
    } else {
        $uri = $host.$path."/".$home;
    }
    //echo $uri."\n";
    $cookiename = "hmmsdkhome";
    //setcookie($cookiename, "", time()-3600); // Testing removing cookie
    $savedloc = $_COOKIE[$cookiename];
    //echo $savedloc."\n";
    if($savedloc != null && $savedloc != "") {
        $uri = $host.$savedloc;
        //echo $uri."\n";
    }
    header("Location: http://".$uri);
    exit;
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML lang="en">
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<TITLE>Hidden Markov Model Development Kit | Redirection Page</TITLE>
</HEAD>
<BODY>

<P align="center" style="color:silver;">
Hidden Markov Model Development Kit Project Home<BR>
If your browser does not direct you to the appropriate page
automatically, please start from
<A href="home.php" style="color:silver;font-weight: bold;">Project Info Page</A>.
</P>

</BODY>
</HTML>

<!--
  Copyright (c) 2003, BlueCraft Software.  All rights reserved. 
  $Id: index.php,v 1.1.1.1 2003/02/09 00:29:40 drwye Exp $
-->
