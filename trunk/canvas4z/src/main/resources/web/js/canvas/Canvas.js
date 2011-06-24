/* Canvas.js

	Purpose:
		
	Description:
		
	History:
		May 12, 2010 3:17:24 PM , Created by simon

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
(function () {
	
	// TODO: move to static methods, or make it extensible
	function _createDrawables(drws) { // TODO: take JSON directly
		//var arr = [];
		for (var arr = [], i = 0, len = drws.length; i < len; i++)
			arr.push(_createDrawable(drws[i]));
		return arr;
	}
	
	function _createDrawable(drw) { // TODO: take JSON directly
		switch(drw.objtp){
		case "rect":
			return zk.copy(new canvas.Rectangle(), drw);
		case "path":
			return zk.copy(new canvas.Path(), drw);
		case "text":
			return zk.copy(new canvas.Text(), drw);
		case "comp":
			// TODO: introduce composite drawable
			//this._paintComposite(drw.obj);
			break;
		case "img":
			return zk.copy(new canvas.ImageSnapshot(), drw);
		case "cvs":
			return zk.copy(new canvas.CanvasSnapshot(), drw);
		case "vid":
		default:
			// unsupported types
		}
		// TODO: how to cover custom type
		return zk.copy(new canvas.Drawable(), drw); // unhandled cases
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
	
	// extended drawing states
	_drwTp: "fill",
	_drwTpBak: "fill",
	_txtMxW: -1,
	_txtMxWBak: -1,
	
	// TODO: rerender upon resize
	
	setDrwngs: function (v) {
		this._drwbls = _createDrawables(jq.evalJSON(v));
	},
	setAdd: function (drwJSON) {
		this.add(_createDrawable(jq.evalJSON(drwJSON)));
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
		var idrw = _createDrawable(jq.evalJSON(idrwJSON));
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
		var idrw = _createDrawable(jq.evalJSON(idrwJSON));
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
	_paint: function(drw){
		// TODO: preload image issue
		this._applyLocalState(drw.state);
		drw.paint_(this);
		this._unapplyLocalState();
	},
	/*
	_paintRect: function(rect) {
		switch(this._drwTp){
		case "none":
			break;
		case "stroke":
			this._ctx.strokeRect(rect.x, rect.y, rect.w, rect.h);
			break;
		case "both":
			this._ctx.fillRect(rect.x, rect.y, rect.w, rect.h);
			this._ctx.strokeRect(rect.x, rect.y, rect.w, rect.h);
			break;
		case "fill":
		default:
			this._ctx.fillRect(rect.x, rect.y, rect.w, rect.h);
		}
	},
	_paintPath: function(path) {
		// mimic path drawing based on data
		this._drawPath(path);
		
		switch(this._drwTp){
		case "none":
			break;
		case "stroke":
			this._ctx.stroke();
			break;
		case "both":
			this._ctx.fill();
			this._ctx.stroke();
			break;
		case "fill":
		default:
			this._ctx.fill();
		}
		this._ctx.beginPath();
	},
	*/
	_drawPath: function(path) {
		var segments = path.sg;
		this._ctx.beginPath();
		
		for (var i = 0, len = segments.length; i < len; i++){
			var data = segments[i].dt;
			switch(segments[i].tp){
			case "mv":
				this._ctx.moveTo(data[0], data[1]);
				break;
			case "ln":
				this._ctx.lineTo(data[0], data[1]);
				break;
			case "qd":
				this._ctx.quadraticCurveTo(data[0], data[1], data[2], data[3]);
				break;
			case "bz":
				this._ctx.bezierCurveTo(
						data[0], data[1], data[2], data[3], data[4], data[5]);
				break;
			// TODO: acrTo
			case "cl":
				this._ctx.closePath();
			}
		}
	},
	/*
	_paintText: function(text) {
		switch(this._drwTp){
		case "none":
			break;
		case "stroke":
			this._strkTxt(text);
			break;
		case "both":
			this._filTxt(text);
			this._strkTxt(text);
			break;
		case "fill":
		default:
			this._filTxt(text);
		}
	},
	_strkTxt: function(text) {
		// TODO: simplify
		if (this._txtMxW < 0)
			this._ctx.strokeText(text.t, text.x, text.y);
		else
			this._ctx.strokeText(text.t, text.x, text.y, this._txtMxW);
	},
	_filTxt: function(text) {
		// TODO: simplify
		if (this._txtMxW < 0)
			this._ctx.fillText(text.t, text.x, text.y);
		else
			this._ctx.fillText(text.t, text.x, text.y, this._txtMxW);
	},
	*/
	_paintComposite: function (comp) {
		if (comp)
			for (var i = 0, len = comp.length; i < len; i++)
				this._paint(comp[i]);
	},
	// TODO: also add image snapshot class
	_paintImage: function(img){
		this._paintSnapshot(img, jq('#' + img.cnt));
	},
	// TODO: also add canvas snapshot class
	_paintCanvas: function(cvs){
		this._paintSnapshot(cvs, jq('#' + cvs.cnt));
	},
	_paintSnapshot: function(obj, img){
		// TODO: check argument
		if(obj.sx)
			this._ctx.drawImage(img[0], obj.sx, obj.sy, obj.sw, obj.sh, 
					obj.dx, obj.dy, obj.dw, obj.dh);
		else if(obj.dw)
			this._ctx.drawImage(img[0], obj.dx, obj.dy, obj.dw, obj.dh);
		else
			this._ctx.drawImage(img[0], obj.dx, obj.dy);
		// TODO: should we preload the image?
		/*
		var c = this._ctx;
		img.load(function() {
			if(obj.sx)
				c.drawImage(img[0], obj.sx, obj.sy, obj.sw, obj.sh, 
						obj.dx, obj.dy, obj.dw, obj.dh);
			else if(obj.dw)
				c.drawImage(img[0], obj.dx, obj.dy, obj.dw, obj.dh);
			else
				c.drawImage(img[0], obj.dx, obj.dy);
		});
		*/
	},
	// state management helper //
	_applyLocalState: function (st) {
		// save current global state on DOM canvas context
		this._txtMxWBak = this._txtMxW;
		this._drwTpBak = this._drwTp;
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
			this._drawPath(st.clp.obj); // TODO: call on path function
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
		this._txtMxW = this._txtMxWBak;
		this._drwTp = this._drwTpBak;
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