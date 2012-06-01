//
// stylesheet.js
//

// [1] Browser sniffer
var isNav4Up = false;
var isIE4Up = false;
if (parseInt(navigator.appVersion.charAt(0)) >= 4) {
   if (navigator.appName == "Netscape") {
      isNav4Up = true;
   } else if (navigator.appVersion.indexOf("MSIE") != -1) {
      isIE4Up = true;
   }
}

// [2] Choose different style sheets
if(isIE4Up) {
  document.write('<LINK rel="STYLESHEET" href="hmmlib.css" type="text/css">');
} else if(isNav4Up) {
  document.write('<LINK rel="STYLESHEET" href="hmmlibns.css" type="text/css">');
} else {
  // No style sheet
}

