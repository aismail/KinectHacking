<?php
    $uri = $_SERVER['PHP_SELF'];
    //$uri = $_SERVER['REQUEST_URI'];
    $cookiename = "hmmsdkhome";
    setcookie($cookiename, $uri, time()+18000);  // 5 hours
    //echo $_COOKIE[$cookiename];
?>

<?php
  // Copyright (c) 2003, BlueCraft Software.  All rights reserved. 
  // $Id: header.inc.php,v 1.1.1.1 2003/02/09 00:29:40 drwye Exp $
?>
