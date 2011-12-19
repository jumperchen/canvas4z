/* Drawable.js

	Purpose:
		
	Description:
		
	History:
		May 18, 2010 5:16:04 PM , Created by simon

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
(function () {
	
	
	
/**
 * 
 */
canvas.Drawable = zk.$extends(zk.Object, {
	objtp: "",
	obj: null,
	state: null,
	
	// TODO: extract drawing style info, reduce API
	
	$init: function () {
		this.state = new Object();
	},
	/**
	 * Returns drawing type. 
	 */
	getDrawingType: function () {
		return this.state.dwtp;
	},
	/**
	 * Sets drawing type.
	 */
	setDrawingType: function (v) {
		this.state.dwtp = v;
		return this;
	},
	/**
	 * Returns transformation.
	 */
	getTransformation: function () {
		return this.state.trns;
	},
	/**
	 * Sets transformation
	 */
	setTransformation: function (v) {
		this.state.trns = v;
		return this;
	},
	/**
	 * Returns transformation.
	 */
	getClipping: function () {
		return this.state.clp; // TODO
	},
	/**
	 * Sets transformation
	 */
	setClipping: function (v) {
		this.state.clp = v;
		return this;
	},
	/**
	 * Returns transformation.
	 */
	getStrokeStyle: function () {
		return this.state.strk;
	},
	/**
	 * Sets transformation
	 */
	setStrokeStyle: function (v) {
		this.state.strk = v;
		return this;
	},
	/**
	 * Returns fill style.
	 */
	getFillStyle: function () {
		return this.state.fil;
	},
	/**
	 * Sets fill style.
	 */
	setFillStyle: function (v) {
		this.state.fil = v;
		return this;
	},
	/**
	 * Returns alpha.
	 */
	getAlpha: function () {
		return this.state.alfa;
	},
	/**
	 * Sets alpha.
	 */
	setAlpha: function (v) {
		this.state.alfa = v;
		return this;
	},
	/**
	 * Returns line width.
	 */
	getLineWidth: function () {
		return this.state.lnw;
	},
	/**
	 * Sets line width.
	 */
	setLineWidth: function (v) {
		this.state.lnw = v;
		return this;
	},
	/**
	 * Returns line cap.
	 */
	getLineCap: function () {
		return this.state.lncp;
	},
	/**
	 * Sets line cap.
	 */
	setLineCap: function (v) {
		this.state.lncp = v;
		return this;
	},
	/**
	 * Returns line join.
	 */
	getLineJoin: function () {
		return this.state.lnj;
	},
	/**
	 * Sets line join.
	 */
	setLineJoin: function (v) {
		this.state.lnj = v;
		return this;
	},
	/**
	 * Returns miter limit.
	 */
	getMiterLimit: function () {
		return this.state.mtr;
	},
	/**
	 * Sets miter limit.
	 */
	setMiterLimit: function (v) {
		this.state.mtr = v;
		return this;
	},
	/**
	 * Returns shadow offset X.
	 */
	getShadowOffsetX: function () {
		return this.state.shx;
	},
	/**
	 * Sets shadow offset X;
	 */
	setShadowOffsetX: function (v) {
		this.state.shx = v;
		return this;
	},
	/**
	 * Returns shadow offset Y.
	 */
	getShadowOffsetY: function () {
		return this.state.shy;
	},
	/**
	 * Sets shadow offset Y.
	 */
	setShadowOffsetY: function (v) {
		this.state.shy = v;
		return this;
	},
	/**
	 * Returns shadow blur.
	 */
	getShadowBlur: function () {
		return this.state.shb;
	},
	/**
	 * Sets shadow blur.
	 */
	setShadowBlur: function (v) {
		this.state.shb = v;
		return this;
	},
	/**
	 * Returns shadow color.
	 */
	getShadowColor: function () {
		return this.state.shc;
	},
	/**
	 * Sets shadow color.
	 */
	setShadowColor: function (v) {
		this.state.shc = v;
		return this;
	},
	/**
	 * Returns composite.
	 */
	getComposite: function () {
		return this.state.cmp;
	},
	/**
	 * Sets composite.
	 */
	setComposite: function (v) {
		this.state.cmp = v;
		return this;
	},
	/**
	 * Returns font.
	 */
	getFont: function () {
		return this.state.fnt;
	},
	/**
	 * Sets font.
	 */
	setFont: function (v) {
		this.state.fnt = v;
		return this;
	},
	/**
	 * Returns text align.
	 */
	getTextAlign: function () {
		return this.state.txal;
	},
	/**
	 * Sets text align.
	 */
	setTextAlign: function (v) {
		this.state.txal = v;
		return this;
	},
	/**
	 * Returns text baseline.
	 */
	getTextBaseline: function () {
		return this.state.txbl;
	},
	/**
	 * Sets text baseline.
	 */
	setTextBaseline: function (v) {
		this.state.txbl = v;
		return this;
	},
	/**
	 * Returns text max width.
	 */
	getTextMaxWidth: function () {
		return this.state.txmw;
	},
	/**
	 * Sets text max width.
	 */
	setTextMaxWidth: function (v) {
		this.state.txmw = v;
		return this;
	},
	
	
	
	/**
	 * Returns true if the point is contains in this Drawable
	 */
	contains: function (x, y) {
		return false;
	},
	/**
	 * Apply state to canvas. Override to skip.
	 */
	applyState_: function (cvs) {
		cvs._applyLocalState(this.state);
	},
	/**
	 * Unapply state to canvas. Override to skip.
	 */
	unapplyState_: function (cvs) {
		cvs._unapplyLocalState();
	},
	/**
	 * Paints the drawable item, including drawing state handling.
	 */
	paint_: function (cvs, efts) {
		this.applyState_(cvs);
		this.paintObj_(cvs);
		this.unapplyState_(cvs);
	},
	/**
	 * Paints the drawable item. Shall be implemented by subclass.
	 */
	paintObj_: function (cvs) {},
	/**
	 * Imports the data from JSON
	 */
	import_: function (drw) {
		this.importObj_(drw.obj);
		this.importState_(drw.state);
		this.slbl = drw.slbl;
		return this;
	},
	/**
	 * Imports the drawable object data
	 */
	importObj_: function (obj) {
		this.obj = zk.copy({}, obj);
		return this;
	},
	/**
	 * Imports the drawable state
	 */
	importState_: function (state) {
		this.state = zk.copy({}, state); // TODO: check deep values
		return this;
	},
	/**
	 * Returns the (nearly) minimal Rectangle which contains the drawable.
	 */
	getBoundingRect_: function (cvs) {
		var cn = this.$n('cnt');
		return {x: 0, y: 0, w: cn.width, h: cn.height}; // unknown item, return all view port
	}
	
},{
	
	/**
	 * 
	 */
	create: function (drw) {
		if (typeof(drw) == 'string')
			drw = jq.evalJSON(drw);
		if (!drw.objtp)
			return null;
		eval('var d = new ' + drw.objtp + '()');
		if (!d)
			d = new canvas.Drawable();
		return d.import_(drw);
	},
	/**
	 * 
	 */
	createAll: function (drws) {
		if (typeof(drws) == 'string')
			drws = jq.evalJSON(drws);
		for (var arr = [], i = 0, len = drws.length; i < len; i++)
			arr.push(canvas.Drawable.create(drws[i]));
		return arr;
	},
	/**
	 * Joins bnd1 range into bnd0.
	 */
	_joinBound: function (bnd0, bnd1) {
		// TODO
	}
	
});

})();