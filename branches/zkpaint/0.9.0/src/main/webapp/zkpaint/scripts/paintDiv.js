zk.load("zul.wgt, canvas", function () {

	function getMousePos(e, refPos) {
		var pos = {x:0, y:0};
		if (!refPos) var refPos = {x:0, y:0};
		if (!e) var e = window.event;
		if (e.pageX || e.pageY) {
			pos.x = e.pageX - refPos.x;
			pos.y = e.pageY - refPos.y;
		} else if (e.clientX || e.clientY) {
			pos.x = e.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft - refPos.x;;
			pos.y = e.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop - refPos.y;;
		}
		return pos;
	}
	
	function getElementPos(elem) {
		var pos = {x:0, y:0};
		while(elem != null) {
			pos.x += elem.offsetLeft;
			pos.y += elem.offsetTop;
			elem = elem.offsetParent;
		}
		return pos;
	}
	
	function getScrollOffset(node){
		var scrollOfs = zk(node).scrollOffset();
		return [scrollOfs[0] - jq.innerX(), scrollOfs[1] - jq.innerY()];
	}
	
	function getTextSize(txt, font){
		return jq('<spen style="font:'+font+
			';line-height: 50%;display: inline-block;"></spen>')
			.zk.textSize(txt)
	}
	
	function transformPath(shape, path, a, b, dx, dy) {
		var sg1 = shape.obj.sg,
			sg2 = path.obj.sg;
			
		for (var i = sg2.length; i--;) {
			for (var j = sg2[i].dt.length/2; j--;) {
				sg2[i].dt[2*j]   = a*sg1[i].dt[2*j]   + dx;
				sg2[i].dt[2*j+1] = b*sg1[i].dt[2*j+1] + dy;
			}
		}
	}
	
	function sigDiff(pos1, pos2) {
		// return true if two points are at least 4 pixel away
		return (Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y + pos2.y) > 3);
	}
	
	function drawArrow (wgt, path, x, y, w, h) {
		var sg = path.obj.sg,
			attrs = wgt._arrowAttrs,
			helfArrowWidth = attrs.arrowWidth / 2,
			helfTipWidth = attrs.tipWidth / 2,
			arrowLength = Math.sqrt(w * w + h * h),
			bodyLen = arrowLength - attrs.tipLength,
			sin = h/arrowLength,
			cos = w/arrowLength,
			hArrSin = helfArrowWidth * sin,
			hArrCos = helfArrowWidth * cos,
			bdSin = bodyLen * sin,
			bdCos = bodyLen * cos,
			hTipSin = helfTipWidth * sin,
			hTipCos = helfTipWidth * cos;
		
		jq.each([
			[0, 0] ,[hArrSin, -hArrCos] ,
			[bdCos + hArrSin ,bdSin - hArrCos],
			[bdCos + hTipSin, bdSin - hTipCos],
			[arrowLength * cos, arrowLength * sin],
			[bdCos - hTipSin, bdSin + hTipCos],
			[bdCos - hArrSin, bdSin + hArrCos],
			[-hArrSin, hArrCos],[0, 0]], function (i, v) {
				
			sg[i].dt[0] = x + v[0];
			sg[i].dt[1] = y + v[1];
		});
	}
	
canvas.PaintDiv = zk.$extends(zul.wgt.Div, {
	_alpha: 0,
	_shapeIndex: 0,
	
	$init: function () {
		this.$supers('$init', arguments);
		
		this._frame = new canvas.Rectangle(0,0,0,0).setDrawingType("stroke");
		
		// global state
		this._start = {x:0, y:0};
		this._last = {x:0, y:0};
		
		var textObj = this._textObj = new canvas.Text("", 0, 0);
		textObj.setDrawingType('both');
		textObj.setAlpha(1);
		
		this._path = new canvas.Path();
		
		this._arrowTmp = new canvas.Path().moveTo(0, 0).lineTo(0, 0).lineTo(0, 0)
			.lineTo(0, 0).lineTo(0, 0).lineTo(0, 0)
			.lineTo(0, 0).lineTo(0, 0)
			.lineTo(0, 0).closePath();
		
		this._mouseHandler = this._selectShapeHandler = new canvas.SelectShapeHandler(this);
		this._drawTextHandler = new canvas.DrawTextHandler(this);
		this._drawShapeHandler = new canvas.DrawShapeHandler(this);
		this._drawArrowHandler = new canvas.DrawArrowHandler(this);
		this._selectMovingShapeHandler  = new canvas.SelectMovingShapeHandler (this);
		
		this._arrowAttrs = {
			arrowWidth: 3, tipWidth: 10, tipLength: 15};
	},
	
	$define: {
		strokeColor: function (color) {
			this.updateDrawableGhosts('setStrokeStyle', color);
		},
		fillColor: function (color) {
			this.updateDrawableGhosts('setFillStyle', color);
		},
		drawingType: function (type) {
			this.updateDrawableGhosts('setDrawingType', type);
		},
		alpha: function (alpha) {
			this.updateDrawableGhosts('setAlpha', alpha);
		},
		text: function (txt) {
			var textObj;
			if (textObj = this._textObj)
				textObj.setText(txt);
		},
		font: function (font) {
			var textObj;
			if (textObj = this._textObj)
				textObj.setFont(font);
		},
		shapeIndex: function (shapeIndex) {
			if (this._shapeCategory) {
				this._shape = this._shapeCategory[shapeIndex];
				
				var shape;
				if (shape = this._shape) {
					shape.setStrokeStyle(this._strokeColor);
					shape.setFillStyle(this._fillColor);
					shape.setDrawingType(this._drawingType);
					shape.setAlpha(this._alpha);
				}
			}
		},
		shapeCategory: function (shapeCategory) {
			if (typeof shapeCategory == "string") {
				this._shapeCategory = canvas.Drawable.createAll(shapeCategory);
				this._shape = this._shapeCategory[this._shapeIndex];
				var shape;
				if (shape = this._shape) {
					shape.setStrokeStyle(this._strokeColor);
					shape.setFillStyle(this._fillColor);
					shape.setDrawingType(this._drawingType);
					shape.setAlpha(this._alpha);
				}
			}
		},
		arrowAttrs: null
	},
	
  	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);
		
		this.listen({
			onClick: this,
		 	onMouseDown: this,
			onMouseMove: this,
			onMouseUp: this,
			onMouseOut: this
		});
		
		this.appendChild(
			this._ghostCvs = new canvas.Canvas({
				width: this.getWidth() || '100%',
				height: this.getHeight() || '100%',
				style: 'position:absolute; z-index:2'}));
    },
	
	setMode: function (mode) {
		var dwa;
		
		switch(mode) {
		case 'to-text':
		    this._mouseHandler = this._drawTextHandler;
			dwa = this._textObj;
			break;
		case 'to-draw':
		    this._mouseHandler = this._drawShapeHandler;
			dwa = this._shape;
			break;
		case 'to-draw-arrow':
		    this._mouseHandler = this._drawArrowHandler;
			dwa = this._arrowTmp;
			break;
		case 'to-select':
		    this._mouseHandler = this._selectShapeHandler;
			break;
		case 'select-moving':
		    this._mouseHandler = this._selectMovingShapeHandler;
			break;
		default: 
			this._mouseHandler = null;
		}
			
		if (dwa) {
			
			dwa.setDrawingType(this._drawingType);
			dwa.setStrokeStyle(this._strokeColor);
			dwa.setFillStyle(this._fillColor);
			dwa.setAlpha(this._alpha);
			
			if (mode == 'to-text') {
				dwa.setText(this._text);
				dwa.setFont(this._font);
			} else if (mode == 'to-draw')
				dwa.setLineWidth(2);
		}
		
		
	},
	
	//do click
	onClick: function (event) {
		this._mouseHandler.doClick(event);
	},
	
	onMouseDown: function (event) {
		this._mouseHandler.doMouseDown(event);
	},
	
	onMouseMove: function (event) {
		var pos = getMousePos(event, getElementPos(this._ghostCvs.$n()));
		
		if (sigDiff(pos, this._last))
			this._mouseHandler.doMouseMove(pos);
	},
	
	onMouseUp: function (event) {
		this._mouseHandler.doMouseUp(event);
	},
	
	onMouseOut: function (event) {
		if (!zk.ie) {
			if (this._mouseHandler.$instanceof(canvas.SelectShapeHandler)
				&& !this._mouseHandler._down) 
				return;
			this._ghostCvs.clear();
			this._cancelMoving = true;
			this._mouseHandler._down = false;
		}
	},
	
	updateDrawableGhosts: function (name, value) {
		var textObj, shape;
		if (textObj = this._textObj)
			textObj[name](value);
			
		if (shape = this._shape)
			shape[name](value);
	},
	
	updateAttrsToServer: function () {
		zAu.send(new zk.Event(this, 'onUpdateAttrs', {
			drawingType: this._drawingType,
			strokeColor: this._strokeColor,
			fillColor: this._fillColor,
			alpha: this._alpha,
			text: this._text,
			font: this._font,
			shapeIndex: this._shapeIndex
		}));
	},
	
	setSelDrawables: function (selDrawables) {
		this._selDrawables = canvas.Drawable.createAll(selDrawables);
	},
	
	unbind_: function () {
		
		this.unlisten({
		 	onClick: this,
		 	onMouseDown: this,
			onMouseMove: this,
			onMouseUp: this,
			onMouseOut: this
		});
		

		this._frame = this._start = this._last = 
		this._textObj = this._path = this._mouseHandler = 
		this._selectShapeHandler = this._drawTextHandler = 
		this._drawShapeHandler = this._drawArrowHandler = 
		this._selectMovingShapeHandler  = null;
		
		this.$supers('unbind_', arguments);
    }
});
canvas.MouseHandler = zk.$extends(zk.Object, {
	_down: false,
	$init: function (wgt) {
		this.wgt = wgt;
	},
	doClick: zk.$void,
	doMouseDown: function(event){
		this._down = true;
	},
	doMouseMove: zk.$void,
	doMouseUp: function(event){
		this._down = false;
	}
});

canvas.DrawTextHandler = zk.$extends(canvas.MouseHandler, {
	doClick: function (event) {
		var wgt = this.wgt,
			ghostNode = wgt._ghostCvs.$n(),
			pos = getMousePos(event, getElementPos(ghostNode)),
			ofs = getScrollOffset(ghostNode),
			text = wgt._text,
			textSize = getTextSize(text, wgt._font);
		
		
		wgt.updateAttrsToServer();
		
		zAu.send(new zk.Event(wgt, 'onAddText', {
			x: pos.x + ofs[0],
			y: pos.y + ofs[1],
			textSize: textSize,
			text: wgt._textObj
		}));
	},
	doMouseMove: function (pos) {
		var wgt = this.wgt,
			ghostCvs = wgt._ghostCvs,
			ofs = getScrollOffset(ghostCvs.$n());
			
		ghostCvs.clear();
		wgt._last  = {x:pos.x, y:pos.y};
		ghostCvs.add(wgt._textObj.setPos(
			pos.x + ofs[0], 
			pos.y + ofs[1]));
	},
});
canvas.DrawShapeHandler = zk.$extends(canvas.MouseHandler, {
	doMouseDown: function (event) {
		this.$supers('doMouseDown', arguments);
		
		var wgt = this.wgt,
			shape = wgt._shape,
			start = wgt._start = getMousePos(event, getElementPos(wgt._ghostCvs.$n()));
		
		wgt._cancelMoving = null;
		if (shape.$instanceof(canvas.Rectangle)) {
			var ofs = getScrollOffset(wgt._ghostCvs.$n());
			shape.obj.x = start.x + ofs[0];
			shape.obj.y = start.y + ofs[1];
		} else {
			wgt._path.import_(shape);
		}
	},
	doMouseMove: function (pos) {
		if (!this._down) return;
		
		var wgt = this.wgt,
			ghostCvs = wgt._ghostCvs,
			shape = wgt._shape,
			start = wgt._start,
			ofs = getScrollOffset(ghostCvs.$n()),
			size = {x: pos.x - start.x, y: pos.y - start.y};
		
		
		wgt._last  = {x:pos.x, y:pos.y};
		ghostCvs.clear();
		
		if (shape.$instanceof(canvas.Rectangle)) {
			shape.obj.w = size.x;
			shape.obj.h = size.y;
			ghostCvs.add(shape);
		} else {
			transformPath(shape, wgt._path, size.x/1000, size.y/1000, start.x + ofs[0], start.y + ofs[1]);
			ghostCvs.add(wgt._path);
		}
	},
	doMouseUp: function (event) {
		this.$supers('doMouseUp', arguments);
		
		if (this.wgt._cancelMoving) return;
		
		var wgt = this.wgt,
			ghostCvs = wgt._ghostCvs,
			pos = getMousePos(event, getElementPos(ghostCvs.$n())),
			start  = wgt._start,
			ofs = getScrollOffset(ghostCvs.$n()),
			dx = pos.x-start.x,
			dy = pos.y-start.y;
		
		ghostCvs.clear();
		
		// send shape param to server
		wgt.updateAttrsToServer();
		zAu.send(new zk.Event(wgt, 'onAddShape', {
			x: start.x + ofs[0], y: start.y + ofs[1], w: dx, h: dy
		}));
		
	}
});
canvas.DrawArrowHandler = zk.$extends(canvas.MouseHandler, {
	doMouseDown: function (event) {
		this.$supers('doMouseDown', arguments);
		var wgt = this.wgt;
		wgt._start = getMousePos(event, getElementPos(wgt._ghostCvs.$n()))
		wgt._cancelMoving = null;
		wgt._path.import_(wgt._arrowTmp);
	},
	doMouseMove: function(pos) {
		if (!this._down) return;
		
		var wgt = this.wgt,
			ghostCvs = wgt._ghostCvs,
			shape = wgt._shape,
			start = wgt._start,
			ofs = getScrollOffset(ghostCvs.$n());
		
		
		wgt._last  = {x:pos.x, y:pos.y};
		ghostCvs.clear();
		drawArrow(wgt, wgt._path, start.x + ofs[0], start.y + ofs[1], pos.x - start.x, pos.y - start.y);
		ghostCvs.add(wgt._path);
		
	},
	doMouseUp: function(event) {
		this.$supers('doMouseUp', arguments);
		
		var wgt = this.wgt
		if (wgt._cancelMoving) return;
		
		wgt._ghostCvs.clear();
		
		// send shape param to server
		wgt.updateAttrsToServer();
		zAu.send(new zk.Event(wgt, 'onAddArrow', {
			path: wgt._path
		}));
	}
});
canvas.SelectShapeHandler = zk.$extends(canvas.MouseHandler, {
	doMouseDown: function (event) {
		this.$supers('doMouseDown', arguments);
		
		var wgt = this.wgt,
			frame = wgt._frame,
			shape = wgt._shape,
			ghostCvs = wgt._ghostCvs,
			ofs = getScrollOffset(ghostCvs.$n()),
			start = wgt._start = getMousePos(event, getElementPos(ghostCvs.$n())),
			startX = start.x + ofs[0],
			startY = start.y + ofs[1],
			mode;
		
		wgt._cancelMoving = null;
		if (frame.contains(startX, startY)) {
			wgt.setMode('select-moving');
			
			wgt._selMoveOfs = {
				x: startX - frame.obj.x,
				y: startY - frame.obj.y
			};
			
			var selDrawables = wgt._selDrawables;
			if (selDrawables)
				for (var d, i = 0, j = selDrawables.length; i < j; i++) {
					d = selDrawables[i];
					
					if (d.$instanceof(canvas.Text)) {
						var obj = d.obj;
						d._orgX = obj.x;
						d._orgY = obj.y;
					}
					
					ghostCvs.add(d);
				}
				
			zAu.send(new zk.Event(wgt, 'onStarMoveShape'));
			wgt._last  = {x:startX, y:startY};
			
		} else {
			frame.setPos(startX, startY);
		}
	},
	doMouseMove: function (pos) {
		if (!this._down) return;
		
		var wgt = this.wgt,
			ghostCvs = wgt._ghostCvs,
			start = wgt._start,
			size = {x: pos.x - start.x, y: pos.y - start.y};
		
		wgt._last  = {x:pos.x, y:pos.y};
		ghostCvs.clear();
		ghostCvs.add(wgt._frame.setSize(size.x, size.y));
	},
	doMouseUp: function(event){
		this.$supers('doMouseUp', arguments);
		
		if (this.wgt._cancelMoving) return;
		
		var wgt = this.wgt,
			ghostCvs = wgt._ghostCvs,
			pos = getMousePos(event, getElementPos(ghostCvs.$n())),
			ofs = getScrollOffset(ghostCvs.$n()),
			start  = wgt._start,
			x = Math.min(start.x, pos.x) + ofs[0],
			y = Math.min(start.y, pos.y) + ofs[1],
			w = Math.abs(start.x - pos.x),
			h = Math.abs(start.y - pos.y);
			startX = start.x,
			startY = start.y;
		
		if (!w && !h) {
			wgt._frame.setSize(0, 0);			
			ghostCvs.clear();
		} else {
			wgt._frame.setPos(x, y).setSize(w, h);	
		}
			
		// send selected region param to server
		zAu.send(new zk.Event(wgt, 'onSelect',{
			x: x, 
			y: y, 
			w: w, 
			h: h
		}));
	}
});
canvas.SelectMovingShapeHandler = zk.$extends(canvas.MouseHandler, {
	doMouseMove: function (pos) {
		var wgt = this.wgt,
			ghostCvs = wgt._ghostCvs,
			selMoveOfs = wgt._selMoveOfs,
			selDrawables = wgt._selDrawables,
			ofs = getScrollOffset(ghostCvs.$n()),
			start = wgt._start,
			last = wgt._last,
			size = {x: pos.x - start.x, y: pos.y - start.y},
			frame, dx, dy;
			
		pos.x += ofs[0];
		pos.y += ofs[1];
		
		dx = pos.x - last.x;
		dy = pos.y - last.y;
		frame = wgt._frame.setPos(pos.x - selMoveOfs.x, pos.y - selMoveOfs.y)
			
		ghostCvs.clear();
		
		
		if (selDrawables)
			for (var d, i = 0, j = selDrawables.length; i < j; i++) {
				d = selDrawables[i];
				
				if (d.$instanceof(canvas.Rectangle)) {
					d.translate(dx, dy);
				} else if (d.$instanceof(canvas.Text)) {
					var obj = d.obj;
					d.setPos(obj.x + dx, obj.y + dy)
				} else {
					d.transform([1,0,0,1, dx, dy]);
				}
				
				ghostCvs.add(d);
			}
		ghostCvs.add(frame);
		wgt._last = {x:pos.x, y:pos.y};
	},
	doMouseUp: function(event){
		this.$supers('doMouseUp', arguments);
		
		var wgt = this.wgt;
		wgt.setMode('to-select');
		wgt._selectShapeHandler._down = false;
		if (wgt._cancelMoving) return;
		
		var ghostCvs = wgt._ghostCvs,
			pos = getMousePos(event, getElementPos(ghostCvs.$n())),
			start  = wgt._start;
		
		ghostCvs.clear();
		ghostCvs.add(wgt._frame);
		zAu.send(new zk.Event(wgt, 'onEndMoveShape', {
			dx:pos.x-start.x, dy:pos.y-start.y}));
	}
});
});