/* Text.js

	Purpose:
		
	Description:
		
	History:
		May 19, 2010 6:12:45 PM , Created by simon

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/

/**
 * 
 */
canvas.Text = zk.$extends(canvas.Drawable, {
	
	// TODO: how to estimate width/height
	
	$init: function (txt, x, y) {
		this.$super('$init');
		this.obj = {t: txt, x: x, y: y};
	},
	/**
	 * 
	 */
	setPos: function (x, y) {
		this.obj.x = x;
		this.obj.y = y;
		return this;
	},
	/**
	 * 
	 */
	setText: function (txt) {
		this.obj.t = txt;
		return this;
	},
	paintObj_: function (cvs) {
		switch(cvs._drwTp){
		case "none":
			break;
		case "stroke":
			this._drawTxt(cvs, true);
			break;
		case "both":
			this._drawTxt(cvs);
			this._drawTxt(cvs, true);
			break;
		case "fill":
		default:
			this._drawTxt(cvs);
		}
	},
	_drawTxt: function (cvs, strk) {
		var t = this.obj;
			fn = strk ? 'strokeText' : 'fillText',
			txtMxW = cvs._txtMxW;
		if (txtMxW < 0)
			cvs._ctx[fn](t.t, t.x, t.y);
		else
			cvs._ctx[fn](t.t, t.x, t.y, txtMxW);
	},
	getBound_: function (cvs) {
		return null; // TODO: estimate
	}

});