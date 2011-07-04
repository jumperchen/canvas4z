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
	hover: function (cvs, hover) {},
	
	/**
	 * 
	 */
	select: function (cvs, select) {}
	
});
		
canvas.SimpleEffect = zk.$extends(canvas.Effect, {
	
	hover: function (cvs, hover) {
		// TODO
	},
	
	select: function (cvs, select) {
		// TODO
	}
	
});
	
})();