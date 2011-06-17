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
	}
	
});