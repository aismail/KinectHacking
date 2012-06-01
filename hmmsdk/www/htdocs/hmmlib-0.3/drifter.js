//
// drifter.js
//

var _dQueue = null;
var _interval = null;
var _wPrev = 0;
var _wH = 0;
var isNav4, isIE4;
if (parseInt(navigator.appVersion.charAt(0)) >= 4) {
  isNav4 = (navigator.appName == "Netscape") ? true : false;
  isIE4 = (navigator.appName.indexOf("Microsoft") != -1) ? true : false;
}
function getTop(layer) { if(isNav4) return layer.top; return layer.style.pixelTop; }
function Drift() {
  this.min = false; this.max = false;
  this.tOffset = -.069; this.bOffset = 0;
  this.lOffset = 0; this.interval = 200;
  this.startDrift = startDrift;
  return this;
}
var prefs;
function startDrift(layer) {
  if(_wH == 0) { if(isNav4) _wH = window.innerHeight; else _wH = document.body.clientHeight; }
  layer.tOffset = getTop(layer);
  if(!this.interval) {
    prefs = new Drift();
    prefs.temp = true;
  } else prefs = this;
  layer.min = prefs.min;
  layer.max = prefs.max;
  if(prefs.tOffset != -.069) layer.tOffset = prefs.tOffset; // offset from top of window
  layer.bOffset = prefs.bOffset; // offset from bottom of window
  layer.lOffset = prefs.lOffset; // for nested layers
  if(_dQueue == null) _dQueue = new Array();
  _dQueue[_dQueue.length] = layer;
  if(_interval == null) _interval = setInterval("checkDrift()", prefs.interval);
  if(prefs.temp) prefs = null;
  return (_dQueue.length-1);
}
var str, i, top, bottom;
function getClipHeight(layer) {
  if(isNav4) return layer.clip.height;
  str = layer.style.clip;
  if(str) {
    i = str.indexOf("(");
    top = parseInt(str.substring(i + 1, str.length));
    i = str.indexOf(" ", i + 1); i = str.indexOf(" ", i + 1);
    bottom = parseInt(str.substring(i + 1, str.length));
    if(top < 0) top = 0;
    return (bottom-top);
  }
  return layer.style.pixelHeight;
}
var layer, dst, wOff;
function checkDrift() {
  if(isNav4) wOff = window.pageYOffset;
  else wOff = document.body.scrollTop;
  for(i = 0; i < _dQueue.length; i++) {
    layer = _dQueue[i];
    if(!layer) continue;
    if(wOff > _wPrev) {
      if(getClipHeight(layer)+layer.bOffset+layer.tOffset > _wH) {
	if(wOff+_wH-layer.bOffset > getTop(layer)+layer.lOffset+getClipHeight(layer))
	  dst = wOff+_wH-layer.lOffset-getClipHeight(layer)-layer.bOffset;
      } else dst = wOff-layer.lOffset+layer.tOffset;
    } else if(wOff < _wPrev) {
      if(getClipHeight(layer)+layer.bOffset+layer.tOffset <= _wH)
	dst = wOff-layer.lOffset+layer.tOffset;
      else if(wOff < getTop(layer)+layer.lOffset-layer.tOffset)
	dst = wOff-layer.lOffset+layer.tOffset;
    } 
    if(layer.max) if(dst > layer.max) dst = layer.max;
    if(layer.min || layer.min == 0)
      if(dst < layer.min) dst = layer.min; 
    if(dst || dst == 0) {
      if(isNav4) layer.top = dst;
      else layer.style.pixelTop = dst;
    }
  }
  _wPrev = wOff;
}
function stopDrift(id) { _dQueue[id] = null; }

