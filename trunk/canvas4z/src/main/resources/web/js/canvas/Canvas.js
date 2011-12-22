/* Canvas.js

	Purpose:
		
	Description:
		
	History:
		May 12, 2010 3:17:24 PM , Created by simon

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
(function () {
	
	function _getRectFromBound(wgt, bnd) {
		var cvs = wgt._cvs;
		if (!bnd)
			return [0, 0, cvs.width, cvs.height];
		var x0 = bnd.x0 || 0,
			y0 = bnd.y0 || 0,
			w = (bnd.x1 || cvs.width) - x0,
			h = (bnd.y1 || cvs.height) - y0;
		return [x0, y0, w, h];
	}
	function _getTargetIndex(wgt, evt) {
		var n = wgt.$n(),
			x = evt.data.pageX - n.offsetLeft,
			y = evt.data.pageY - n.offsetTop;
		for (var drws = wgt._drwbls, i = drws.length, d; i--;) 
			if ((d = drws[i]) && d.contains(x, y))
				return i;
		return -1;
	}
	
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
	_hvi: -1,
	_sldi: -1,
	
	// extended drawing states
	_drwTp: "fill",
	_drwTpBak: [],
	_txtMxW: -1,
	_txtMxWBak: [],
	
	// TODO: rerender upon resize
	
	setDrwngs: function (drwsJSON) {
		this._drwbls = canvas.Drawable.createAll(drwsJSON);
		if (this.desktop)
			this._repaint();
	},
	setAddAll: function (drwsJSON) { // called by server
		this.addAll(canvas.Drawable.createAll(drwsJSON));
	},
	/**
	 * Adds a Drawable to canvas.
	 */
	add: function (drw) {
		this._paint(drw);
		this._drwbls.push(drw);
	},
	addAll: function (drws) {
		for (var i = 0, len = drws.length; i < len; i++)
			this.add(drws[i]);
	},
	setRemove: function (index) { // called by server
		this.remove(index);
	},
	/**
	 * Removes the Drawable at specific index.
	 */
	remove: function (index) { // TODO: remove batch
		var drw = this._drwbls.splice(index, 1);
		this._repaint(drw.getBound_());
		return drw;
	},
	setInsert: function (idrwsJSON) { // called by server
		var idrws = canvas.Drawable.createAll(idrwsJSON);
		this.insert(idrws.i, idrws.drws);
	},
	/**
	 * Inserts a Drawable at specific index.
	 */
	insert: function (index, drws) {
		var drwbls = this._drwbls; 
		drwbls.splice.apply(drwbls, [index, 0].concat(drws));
		this._repaint(/*drw.getBound_()*/); // TODO: get all bounds
	},
	setReplace: function (idrwJSON) { // called by server
		var idrw = canvas.Drawable.create(idrwJSON);
		this.replace(idrw.i, idrw.drw);
	},
	/**
	 * Replace the Drawable at specific index.
	 */
	replace: function (index, drw) { // TODO: replace batch
		var removed = this._drwbls.splice(index, 1, drw),
			bnd0 = removed[0].getBound_(),
			bnd1 = drw.getBound_();
		this._repaint(canvas.Drawable._joinBounds(bnd0, bnd1));
		return removed[0];
	},
	setClear: function () { // called by server
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
	_clearCanvas: function (bnd) {
		var r = _getRectFromBound(this, bnd);
		this._ctx.clearRect(r[0], r[1], r[2], r[3]);
	},
	_repaint: function (bnd) { // TODO: setTimeout
		this._clearCanvas(bnd);
		this._applyBound(bnd);
		for (var i = 0, drws = this._drwbls, len = drws.length; i < len; i++)
			this._paint(drws[i]);
		this._unapplyBound();
	},
	_paint: function (drw) {
		drw.paint_(this); // TODO: rethink design
		/*
		drw.applyState_(this);
		drw.paintObj_(this, bnd);
		drw.unapplyState_(this);
		*/
	},
	_applyBound: function (bnd) {
		var ctx = this._ctx;
		ctx.save();
		if (bnd) {
			var cvs = this._cvs,
				r = _getRectFromBound(this, bnd);
			ctx.beginPath();
			ctx.rect(r[0], r[1], r[2], r[3]);
			ctx.clip();
		}
	},
	_unapplyBound: function () {
		this._ctx.restore();
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
		if (zk.ie < 9) 
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
	doMouseMove_: function (evt) {
		var i = _getTargetIndex(this, evt),
			pi = this._hvi;
		if (pi != i) {
			this.fire('onTooltip', zk.copy(evt.data, {i: i, pi: pi}));
			this._hvi = i;
		}
		this.$supers('doMouseMove_', arguments);
	},
	doClick_: function (evt) {
		var i = _getTargetIndex(this, evt),
			pi = this._sldi; // TODO
		if (pi != i) {
			this.fire('onSelect', zk.copy(evt.data, {i: i, pi: pi}));
			this._sldi = i;
		}
		this.$supers('doClick_', arguments);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-canvas";
	}
	
});

})();