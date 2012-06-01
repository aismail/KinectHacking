//
// acceptlicense.js
//

function acceptLicense() {
   var answerYes = true;
   answerYes = window.confirm("Do you accept the license agreement?");
   if(answerYes) {
       return true;
   } else {
       return false;
   }
}

