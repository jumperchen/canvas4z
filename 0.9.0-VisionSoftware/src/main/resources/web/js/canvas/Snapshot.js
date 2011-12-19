/* Snapshot.js

	Purpose:
		
	Description:
		
	History:
		Jun 24, 2011 15:32:11 PM , Created by simon

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
(function () {
	
/**
 * 
 */
canvas.Snapshot = zk.$extends(canvas.Drawable, {
	
	//@Override
	contains: function (x, y) {
		// TODO: just range check
		return false;
	},
	paintObj_: function (cvs) {
		var obj = this.obj,
			img = jq('#' + obj.cnt)[0];
		if(obj.sx)
			cvs._ctx.drawImage(img, obj.sx, obj.sy, obj.sw, obj.sh, 
					obj.dx, obj.dy, obj.dw, obj.dh);
		else if(obj.dw)
			cvs._ctx.drawImage(img, obj.dx, obj.dy, obj.dw, obj.dh);
		else
			cvs._ctx.drawImage(img, obj.dx, obj.dy);
	}
	
});

/**
 * 
 */
canvas.ImageSnapshot = zk.$extends(canvas.Snapshot, {
	
});

/**
 * 
 */
canvas.CanvasSnapshot = zk.$extends(canvas.Snapshot, {
	
});
	
})();