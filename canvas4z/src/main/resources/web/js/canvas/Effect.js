/* Effect.js

	Purpose:
		
	Description:
		
	History:
		Jun 29, 2011 15:23:33 PM , Created by simon

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
(function () {
	
canvas.Effect = zk.$extends(zk.Object, {
	
	/**
	 * 
	 */
	hover: function (cvs, drw, index, hover) {},
	
	/**
	 * 
	 */
	select: function (cvs, drw, index, select) {}
	
});

canvas.SimpleEffect = zk.$extends(canvas.Effect, {
	
	hover: function (cvs, drw, index) {
		// TODO
	},
	
	select: function (cvs, drw, index) {
		// TODO
	}
	
});

})();