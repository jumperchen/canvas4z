/* Shape.js

	Purpose:
		
	Description:
		
	History:
		May 18, 2010 3:24:34 PM , Created by simon

Copyright (C) 2010 Potix Corporation. All Rights Reserved.
*/
(function () {
	
	function _getPathCrossing(path, x, y) {
		var sg = path.obj.sg,
			sum = 0,
			crx = 0, cry = 0, 
			mvx = 0, mvy = 0, 
			edx = 0, edy = 0;
		
		for (var i = 0, len = sg.length; i < len; i++) {
			var data = sg[i].dt;
			switch (sg[i].tp) {
			case "mv":
				// close last segment group
				sum += _getLineCrossing(x, y, crx, cry, mvx, mvy);
				mvx = crx = data[0];
				mvy = cry = data[1];
				break;
			case "ln":
				edx = data[0]; edy = data[1];
				sum += _getLineCrossing(x, y, crx, cry, edx, edy);
				crx = edx; cry = edy;
				break;
			case "qd":
				edx = data[2]; edy = data[3];
				sum += _getQuadCrossing(x, y, crx, cry, edx, edy, data[0], data[1]);
				crx = edx; cry = edy;
				break;
			case "bz":
				edx = data[4]; edy = data[5];
				sum += _getBezierCrossing(x, y, crx, cry, edx, edy, data[0], data[1], data[2], data[3]);
				crx = edx; cry = edy;
				break;
			// TODO: acrTo
			case "cl":
				sum += _getLineCrossing(x, y, crx, cry, mvx, mvy);
				crx = mvx; cry = mvy;
				break;
			}
		}
		// if not closed, close at the end
		sum += _getLineCrossing(x, y, crx, cry, mvx, mvy);
		return sum;
	}
	
	function _getLineCrossing(x, y, x0, y0, x1, y1) {
		if ((y0 == y1) || 
			(y < y0 && y < y1) || 
			(y >= y0 && y >= y1) ||
			(x >= x0 && x >= x1))
			return 0;
		if (x < x0 && x < x1) 
			return y0 < y1 ? 1 : -1;
		if (x >= x0 + (y - y0) * (x1 - x0) / (y1 - y0)) 
			return 0;
		return y0 < y1 ? 1 : -1;
	}
	
	function _getQuadCrossing(x, y, x0, y0, x1, y1, cpx, cpy, depth) {
		if ((y < y0 && y < y1 && y < cpy) ||
			(y >= y0 && y >= y1 && y >= cpy) ||
			(x >= x0 && x >= x1 && x >= cpx))
			return 0;
		if (x < x0 && x < cpx && x < x1)
			return (y < y1 ? 1 : 0) - (y < y0 ? 1 : 0);
		if (depth && depth > 13) // assume < 4096 px wide screen
			return _getLineCrossing(x, y, x0, y0, x1, y1);
		var cpxa0 = (x0 + cpx) / 2,
			cpya0 = (y0 + cpy) / 2,
			cpxa1 = (cpx + x1) / 2,
			cpya1 = (cpy + y1) / 2,
			mx = (cpxa0 + cpxa1) / 2,
			my = (cpya0 + cpya1) / 2,
			d = (depth || 0) + 1;
		if (isNaN(mx) || isNaN(my))
			return 0;
		return _getQuadCrossing(x, y, x0, y0, mx, my, cpxa0, cpya0, d) +
			_getQuadCrossing(x, y, mx, my, x1, y1, cpxa1, cpya1, d);
	}
	
	function _getBezierCrossing(x, y, x0, y0, x1, y1, cpx0, cpy0, cpx1, cpy1, depth) {
		if ((y < y0 && y < y1 && y < cpy0 && y < cpy1) ||
			(y >= y0 && y >= y1 && y >= cpy0 && y >= cpy1) ||
			(x >= x0 && x >= x1 && x >= cpx0 && x >= cpx1))
			return 0;
		if (x < x0 && x < x1 && x < cpx0 && x < cpx1)
			return (y < y1 ? 1 : 0) - (y < y0 ? 1 : 0);
		if (depth && depth > 13) // assume < 4096 px wide screen
			return _getLineCrossing(x, y, x0, y0, x1, y1);
		var cpxa0 = (x0 + cpx0) / 2,
			cpya0 = (y0 + cpy0) / 2,
			cpxa1 = (x1 + cpx1) / 2,
			cpya1 = (y1 + cpy1) / 2,
			cpxa = (cpx0 + cpx1) / 2,
			cpya = (cpy0 + cpy1) / 2,
			cpxb0 = (cpxa0 + cpxa) / 2,
			cpyb0 = (cpya0 + cpya) / 2,
			cpxb1 = (cpxa1 + cpxa) / 2,
			cpyb1 = (cpya1 + cpya) / 2,
			mx = (cpxb0 + cpxb1) / 2,
			my = (cpyb0 + cpyb1) / 2,
			d = (depth || 0) + 1;
		if (isNaN(mx) || isNaN(my))
			return 0;
		return _getBezierCrossing(x, y, x0, y0, mx, my, cpxa0, cpya0, cpxb0, cpyb0, d) + 
			_getBezierCrossing(x, y, mx, my, x1, y1, cpxb1, cpyb1, cpxa1, cpya1, d);
	}
	
	function _drawPath(cvs, path) {
		var sgs = path.sg,
			ctx = cvs._ctx;
		ctx.beginPath();
		
		for (var i = 0, len = sgs.length; i < len; i++) {
			var sg = sgs[i],
				data = sg.dt;
			switch(sg.tp){
			case "mv":
				ctx.moveTo(data[0], data[1]);
				break;
			case "ln":
				ctx.lineTo(data[0], data[1]);
				break;
			case "qd":
				ctx.quadraticCurveTo(data[0], data[1], data[2], data[3]);
				break;
			case "bz":
				ctx.bezierCurveTo(data[0], data[1], data[2], data[3], data[4], data[5]);
				break;
			// TODO: acrTo
			case "cl":
				ctx.closePath();
			}
		}
	}
	
/**
 * 
 */
canvas.Shape = zk.$extends(canvas.Drawable, {
	
});

/**
 * 
 */
canvas.Rectangle = zk.$extends(canvas.Shape, {
	
	$init: function (x, y, w, h) {
		this.$super('$init');
		this.objtp = "rect";
		this.obj = new zk.Object();
		this.obj.x = x;
		this.obj.y = y;
		this.obj.w = w;
		this.obj.h = h;
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
	setSize: function (w, h) {
		this.obj.w = w;
		this.obj.h = h;
		return this;
	},
	/**
	 * Scales the rectangle. Reference point is (0,0)
	 */
	scale: function (a, b) {
		this.obj.x *= a;
		this.obj.y *= b;
		this.obj.w *= a;
		this.obj.h *= b;
	},
	/**
	 * Translate the rectangle
	 */
	translate: function (dx, dy) {
		this.obj.x += dx;
		this.obj.y += dy;
	},
	/**
	 * Return true if (x, y) is strictly inside the boundary of the rectangle.
	 */
	//@Override
	contains: function (x, y) {
		if(isNaN(x) || isNaN(y)) 
			return false;
		var sx = this.obj.x, 
			sy = this.obj.y;
		return sx < x 
			&& x < sx + this.obj.w 
			&& sy < y 
			&& y < sy + this.obj.h;
	},
	paint_: function (cvs) {
		var r = this.obj;
		switch(cvs._drwTp){
		case "none":
			break;
		case "stroke":
			cvs._ctx.strokeRect(r.x, r.y, r.w, r.h);
			break;
		case "both":
			cvs._ctx.fillRect(r.x, r.y, r.w, r.h); // fill first, so border stands out
			cvs._ctx.strokeRect(r.x, r.y, r.w, r.h);
			break;
		case "fill":
		default:
			cvs._ctx.fillRect(r.x, r.y, r.w, r.h);
		}
	}
	
});

/**
 * 
 */
canvas.Path = zk.$extends(canvas.Shape, {
	
	$init: function () {
		this.$super('$init');
		this.objtp = "path";
		this.obj = new zk.Object();
		this.obj.sg = [];
	},
	/**
	 * 
	 */
	beginPath: function () {
		this.obj.sg = [];
		return this;
	},
	/**
	 * 
	 */
	closePath: function () {
		var s = new Object();
		s.tp = "cl";
		s.dt = [];
		this.obj.sg.push(s);
		return this;
	},
	/**
	 * 
	 */
	moveTo: function (x, y) {
		var s = Object();
		s.tp = "mv";
		s.dt = [x,y];
		this.obj.sg.push(s);
		return this;
	},
	/**
	 * 
	 */
	lineTo: function (x, y) {
		var s = Object();
		s.tp = "ln";
		s.dt = [x,y];
		this.obj.sg.push(s);
		return this;
	},
	/**
	 * 
	 */
	quadTo: function (cpx, cpy, x, y) {
		var s = Object();
		s.tp = "qd";
		s.dt = [cpx, cpy, x,y];
		this.obj.sg.push(s);
		return this;
	},
	/**
	 * 
	 */
	bezierTo: function (cpx1, cpy1, cpx2, cpy2, x, y) {
		var s = Object();
		s.tp = "bz";
		s.dt = [cpx1, cpy1, cpx2, cpy2, x, y];
		this.obj.sg.push(s);
		return this;
	},
	// arcTo
	/**
	 * Transforms the path
	 */
	transform: function (m) {
		var sg = this.obj.sg;
		for(var i=sg.length;i--;){
			var dt = sg[i].dt;
			for(var j=(dt.length)/2;j--;){
				var x2 = m[0]*dt[2*j] + m[2]*dt[2*j+1] + m[4];
				dt[2*j+1] = m[1]*dt[2*j] + m[3]*dt[2*j+1] + m[5];
				dt[2*j] = x2;
			}
		}
	},
	/**
	 * Scales the path. Reference point is (0,0).
	 */
	scale: function (a,b) {
		this.transform([a,0,0,b,0,0]);
	},
	/**
	 * Translates the path
	 */
	translate: function (dx, dy) {
		this.transform([1,0,0,1,dx,dy]);
	},
	/**
	 * 
	 */
	clone: function () {
		var p2 = new canvas.Path();
		p2._copyObj(this);
		p2._copyState(this);
	},
	//@Override
	contains: function (x, y) {
		if(isNaN(x) || isNaN(y)) 
			return false;
		return _getPathCrossing(this, x, y) % 2 != 0;
	},
	paint_: function (cvs) {
		// mimic path drawing based on data
		this._drawPath(cvs);
		
		var ctx = cvs._ctx;
		
		switch(cvs._drwTp){
		case "none":
			break;
		case "stroke":
			ctx.stroke();
			break;
		case "both":
			ctx.fill();
			ctx.stroke();
			break;
		case "fill":
		default:
			ctx.fill();
		}
		ctx.beginPath();
	},
	_drawPath: function (cvs) {
		_drawPath(cvs, this.obj);
	},
	// copy object data from path
	_copyObj: function (path) {
		// TODO: may use zk.copy
		this.obj.sg = [];
		var sg1 = path.obj.sg;
		for(var i = sg1.length; i--;){
			this.obj.sg[i] = new Object();
			this.obj.sg[i].tp = sg1[i].tp;
			this.obj.sg[i].dt = [];
			for(var j = sg1[i].dt.length; j-- ;)
				this.obj.sg[i].dt[j] = sg1[i].dt[j];
		}
	}
	
});

})();