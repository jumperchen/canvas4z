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
		this.objtp = "text";
		this.obj = new zk.Object();
		this.obj.t = txt;
		this.obj.x = x;
		this.obj.y = y;
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
			this._strkTxt(cvs);
			break;
		case "both":
			this._filTxt(cvs);
			this._strkTxt(cvs);
			break;
		case "fill":
		default:
			this._filTxt(cvs);
		}
	},
	_strkTxt: function(cvs) {
		var t = this.obj;
		// TODO: merge
		if (cvs._txtMxW < 0)
			cvs._ctx.strokeText(t.t, t.x, t.y);
		else
			cvs._ctx.strokeText(t.t, t.x, t.y, cvs._txtMxW);
	},
	_filTxt: function(cvs) {
		var t = this.obj;
		// TODO: merge
		if (cvs._txtMxW < 0)
			cvs._ctx.fillText(t.t, t.x, t.y);
		else
			cvs._ctx.fillText(t.t, t.x, t.y, cvs._txtMxW);
	}
	
});