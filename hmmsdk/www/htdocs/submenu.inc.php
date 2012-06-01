<?php
    $submenus = array (array ("home.php", "Project&nbsp;Info", "HmmSDK Project Home"),
                       array ("guide.php", "Getting&nbsp;Started", "Quick Guide to How to Get Started"),
                       array ("design.php", "Design&nbsp;Details", "Links to Design Documents"),
                      // array ("sources.php", "Source&nbsp;Codes", "HmmSDK Source Codes"),
                      // array ("javadocs.php", "Javadocs", "Java API Documentations"),
                       array ("faqs.php", "FAQs", "Frequently Asked Questions"),
                       array ("about.php", "About", "About HmmSDK and BlueCraft Software")
                      );
    //
    $reqURI = $_SERVER['PHP_SELF'];
    //$reqURI = $_SERVER['REQUEST_URI'];
    $elems = split("/", $reqURI);
    $self = $elems{count($elems)-1};
?>

<?php
    for($i=0;$i<count($submenus);$i++) {
       $uri = $submenus[$i][0];
       $text = $submenus[$i][1];
       $desc = $submenus[$i][2];
?>
<?php
       if($uri == $self) {
?>
          <SPAN class="submenuselected"><?php echo $text;?></SPAN>&nbsp;|&nbsp; 
<?php
       } else {
?>
          <A href="<?php echo $uri;?>" title="<?php echo $desc;?>"><SPAN class="submenu"><?php echo $text;?></SPAN></A>&nbsp;|&nbsp; 
<?php
       }
?>
<?php
    }
?>

            
<?php
  // Copyright (c) 2003, BlueCraft Software.  All rights reserved. 
  // $Id: submenu.inc.php,v 1.1.1.1 2003/02/09 00:29:40 drwye Exp $
?>
