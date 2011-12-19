/* Colors.js

	Purpose:
		
	Description:
		
	History:
		Jun 29, 2011 15:23:33 PM , Created by simon

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
(function () {
	
/**
 * 
 */
canvas.Color = zk.$extends(zk.Object, {
	
	// 1. some group has to be clean, so starts with RGB
	// 2. when get a value, its group has to be clean
	// 3. when set a value, its group has to be clean, and marks others dirty
	
	_r: 0,
	_g: 0,
	_b: 0,
	// dirty flags
	_rgbCln: true,
	/*
	_hsvCln: false,
	_hslCln: false,
	*/
	
	$init: function (v) {
		if (typeof(v) == 'string') {
			// #: RGB
			// TODO
		}
	},
	_syncToHSL: function () {
		if (this._hslCln)
			return;
		
		if (this._hsvCln) {
			// TODO
			
		} else if (this._rgbCln) {
			var r = this._r,
				g = this._g,
				b = this._b,
				v = this._v = Math.max(r, g, b);
			// TODO
		}
		
		this._hslCln = true;
	},
	_syncToHSV: function () {
		if (this._hsvCln)
			return;
		
		if (this._hslCln) {
			// TODO
			
		} else if (this._rgbCln) {
			var r = this._r,
				g = this._g,
				b = this._b,
				v = this._v = Math.max(r, g, b),
				m = Math.min(r, g, b);
			// TODO
		}
		
		this._hsvCln = true;
	},
	_syncToRGB: function () {
		if (this._rgbCln)
			return;
		
		if (this._hsvCln) {
			// TODO
		} else if (this._hslCln) {
			// TODO
		}
		
		// TODO
		this._rgbCln = true;
	}
	
});
	
})();