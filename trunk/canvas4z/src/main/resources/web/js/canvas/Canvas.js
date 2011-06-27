/* Canvas.js

	Purpose:
		
	Description:
		
	History:
		May 12, 2010 3:17:24 PM , Created by simon

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
(function () {
	
/**
 * The ZK component corresponding to HTML 5 Canvas.
 * While HTML 5 Canvas is a command-based DOM object that allows user to draw
 * items on a surface, ZK Canvas maintains a list of drawable items and allow
 * user to operate the list by adding, removing, updating, replacing the 
 * elements. The changes will be reflected on the client side upon these
 * operations.
 * 
 * <p>Default {@link #getZclass}: z-canvas.
 */
canvas.Canvas = zk.$extends(zul.Widget, {
	_cvs: null,
	_ctx: null,
	_drwbls: [],
	_states: [],
	
	// extended drawing states
	_drwTp: "fill",
	_drwTpBak: [],
	_txtMxW: -1,
	_txtMxWBak: [],
	
	// TODO: rerender upon resize
	
	setDrwngs: function (drwsJSON) {
		this._drwbls = canvas.Drawable.createAll(drwsJSON);
	},
	setAdd: function (drwJSON) {
		this.add(canvas.Drawable.create(drwJSON));
	},
	/**
	 * Adds a Drawable to canvas.
	 */
	add: function (drw) {
		this._paint(drw);
		this._drwbls.push(drw);
	},
	setRemove: function (index) {
		this.remove(index);
	},
	/**
	 * Removes the Drawable at specific index.
	 */
	remove: function (index) {
		var drw = this._drwbls.splice(index,1);
		this._repaint();
		return drw;
	},
	setInsert: function (idrwJSON) {
		var idrw = canvas.Drawable.create(idrwJSON);
		this.insert(idrw.i, idrw.drw);
	},
	/**
	 * Inserts a Drawable at specific index.
	 */
	insert: function (index, drw) {
		this._drwbls.splice(index, 0, drw);
		this._repaint();
	},
	setReplace: function (idrwJSON) {
		var idrw = canvas.Drawable.create(idrwJSON);
		this.replace(idrw.i, idrw.drw);
	},
	/**
	 * Replace the Drawable at specific index.
	 */
	replace: function (index, drw) {
		var removed = this._drwbls.splice(index, 1, drw);
		this._repaint();
		return removed[0];
	},
	setClear: function () {
		this.clear();
	},
	/**
	 * Remove all Drawables.
	 */
	clear: function () {
		this._drwbls = [];
		this._clearCanvas();
	},
	/*
	// experimental, does not work on Chrome and IE
	saveAsPNG: function(width, height){
		if(!width) width = this._cvs.width;
		if(!height) height = this._cvs.height;
		Canvas2Image.saveAsPNG(this._cvs, false, width, height);
	},
	*/
	
	
	
	// private //
	_clearCanvas: function () {
		this._ctx.clearRect(0, 0, this._cvs.width, this._cvs.height);
	},
	_repaint: function () {
		this._clearCanvas();
		for (var i = 0, drws = this._drwbls, len = drws.length; i < len; i++)
			this._paint(drws[i]);
	},
	_paint: function (drw) {
		// TODO: preload image issue
		drw.applyState_(this);
		drw.paint_(this);
		drw.unapplyState_(this);
	},
	// state management helper //
	_applyLocalState: function (st) {
		// save current global state on DOM canvas context
		this._txtMxWBak.push(this._txtMxW);
		this._drwTpBak.push(this._drwTp);
		this._ctx.save();
		// apply local state to context
		this._setDOMContextState(st);
	},
	_setDOMContextState: function (st) {
		var ctx = this._ctx;
		// drawing type is NOT a part of DOM Canvas state
		if(st.dwtp) 
			this._drwTp = st.dwtp;
		if(st.trns) { // transformation
			var trns = st.trns;
			ctx.setTransform(trns[0], trns[1], trns[2], trns[3], trns[4], trns[5]);
		}
		if(st.clp) { // clipping
			canvas.Path.doPath(this, st.clp.obj);
			ctx.clip();
			ctx.beginPath();
		}
		if(st.strk) ctx.strokeStyle   = st.strk;
		if(st.fil)  ctx.fillStyle     = st.fil;
		if(st.alfa) ctx.globalAlpha   = st.alfa;
		if(st.lnw)  ctx.lineWidth     = st.lnw;
		if(st.lncp) ctx.lineCap       = st.lncp;
		if(st.lnj)  ctx.lineJoin      = st.lnj;
		if(st.mtr)  ctx.miterLimit    = st.mtr;
		if(st.shx)  ctx.shadowOffsetX = st.shx;
		if(st.shy)  ctx.shadowOffsetY = st.shy;
		if(st.shb)  ctx.shadowBlur    = st.shb;
		if(st.shc)  ctx.shadowColor   = st.shc;
		if(st.cmp)  ctx.globalCompositeOperation = st.cmp;
		if(st.fnt)  ctx.font          = st.fnt;
		if(st.txal) ctx.textAlign     = st.txal;
		if(st.txbl) ctx.textBaseline  = st.txbl;
		// maxWidth is not a part of DOM Canvas state
		if(st.txmw) this._txtMxW = st.txmw;
	},
	_unapplyLocalState: function () {
		// restore global state
		this._txtMxW = this._txtMxWBak.pop();
		this._drwTp = this._drwTpBak.pop();
		this._ctx.restore();
	},
	//@Override
	bind_: function () {
		this.$supers("bind_", arguments);
		zWatch.listen({onSize: this, onShow: this});
		
		this._cvs = document.createElement("canvas");
		this._init();
		
		this._cvs.id = this.uuid + '-cnt';
		// TODO: <canvas> zclass
		
		this._ctx = this._cvs.getContext("2d");
		jq(this.$n()).append(this._cvs);
		
		this._repaint();
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers("unbind_", arguments);
	},
	_init: function () {
		var n = this.$n(),
			w = n.offsetWidth,
			h = n.offsetHeight,
			cvs = this._cvs;
		if (w) 
			cvs.width = zk.parseInt(w);
		if (h) 
			cvs.height = zk.parseInt(h);
		if (zk.ie) 
			G_vmlCanvasManager.initElement(cvs);
	},
	onSize: _zkf = function () {
		this._init();
		this._repaint();
	},
	onShow: _zkf,
	setWidth: function () {
		this.$supers('setWidth', arguments);
		if (this.$n())
			this.onSize();
	},
	setHeight: function () {
		this.$supers('setHeight', arguments);
		if (this.$n())
			this.onSize(); // TODO: refine
	},
	_getSelected: function (evt) {
		var n = this.$n(),
			x = evt.data.pageX - n.offsetLeft,
			y = evt.data.pageY - n.offsetTop;
		for (var drws = this._drwbls, i = drws.length, d; i--;) 
			if ((d = drws[i]) && d.slbl && d.contains(x,y))
				return i;
	},
	doMouseMove_: function (evt) {
		var i = this._getSelected(evt);
		if (this._sldi != i) {
			// TODO: effect
			this._sldi = i;
		}
		this.$supers('doMouseMove_', arguments);
	},
	doClick_: function (evt) {
		// TODO: select
		this.$supers('doClick_', arguments);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-canvas";
	}
	
});

})();