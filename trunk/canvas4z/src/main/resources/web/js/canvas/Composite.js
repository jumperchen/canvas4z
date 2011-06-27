/* Composite.js

	Purpose:
		
	Description:
		
	History:
		Jun 21, 2011 15:22:23 PM , Created by simon

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
(function () {
	
/**
 * 
 */
canvas.Composite = zk.$extends(canvas.Drawable, {
	
	//@Override
	importObj_: function (obj) {
		this.obj = canvas.Drawable.createAll(obj);
		return this;
	},
	contains: function (x, y) {
		//for ()
		// TODO
		return false;
	},
	applyState_: function (cvs) {
		// do nothing
	},
	unapplyState_: function (cvs) {
		// do nothing
	},
	paint_: function (cvs) {
		for (var i = 0, drws = this.obj, len = drws.length; i < len; i++)
			cvs._paint(drws[i]);
	}
	
});

})();