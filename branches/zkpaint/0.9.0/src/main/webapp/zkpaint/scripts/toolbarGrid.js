zk.load("zul.grid", function () {
	
	function switchHighlight(wgt, index) {
		jq.each(['catLbSelect', 'catLbText', 'catLbShape'], 
			function(i, value) { 
				wgt[value].setSclass(index == i ? 
					'paint-tool-panel-text-highlight': '');
			}
		);
	}

	function getDrawingType(f,s){
		switch((f>0)*2 + (s>0)){
		case 0:
			return "none";
		case 1:
			return "stroke";
		case 2:
			return "fill";
		case 3:
			return "both";
		}
	}
	
	function getFont(i){
		switch(i){
			case 1:
				return "sans-serif";
			case 2:
				return "monospace";
			case 0:
			default:
				return "serif";
		}
	}
	
canvas.ToolbarGrid = zk.$extends(zul.grid.Grid, {
	
  	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);
		
		this.wireVariables();
		this.addEventListeners();
		
		this.updateText();
		this.updateFont();
		this.updatePaintDiv();
    },
	
	wireVariables: function () {
		this.catLbSelect = this.$f('catLbSelect');
		this.catLbText = this.$f('catLbText');
		this.catLbShape = this.$f('catLbShape');
		
		this.fillTypeBox = this.$f('fillTypeBox');
		this.strokeTypeBox = this.$f('strokeTypeBox');
		this.strokeColorBox = this.$f('strokeColorBox');
		this.fillColorBox = this.$f('fillColorBox');
		this.alphaSlider = this.$f('alphaSlider');
		
		this.fontBox = this.$f('fontBox');
		this.fontSizeBox = this.$f('fontSizeBox');
		this.textBox = this.$f('textBox');
		
		this.shapeBox = this.$f('shapeBox');
		
		this.paintDiv = this.$f('paintDiv');
	},
	
	addEventListeners: function () {
		this.$f('selectBtn').listen({onClick: [this, this.changeSelectMode]});
		this.$f('textBtn').listen({onClick: [this, this.changeTextMode]});
		this.$f('shapeBtn').listen({onClick: [this, this.changeShapeMode]});
		this.$f('arrowBtn').listen({onClick: [this, this.changeArrowMode]});
		this.shapeBox.listen({onSelect: [this, this.changeShapeIndex]});
		
		
		this.fontBox.listen({onBlur: [this, this.updateFont]});
		this.fontSizeBox.listen({onBlur: [this, this.updateFont]});
		this.textBox.listen({onBlur: [this, this.updateText]});
		
		this.strokeColorBox.listen({onChange: [this, this.updatePaintDiv]});
		this.fillColorBox.listen({onChange: [this, this.updatePaintDiv]});
		this.alphaSlider.listen({onScroll: [this, this.updatePaintDiv]});
		
		this.strokeTypeBox.listen({onBlur: [this, this.updatePaintDiv]});
		this.fillTypeBox.listen({onBlur: [this, this.updatePaintDiv]});
	},
	
	changeSelectMode: function () {
		this.paintDiv.setMode('to-select');
		switchHighlight(this, 0);
	},
	
	changeTextMode: function () {
		if (zk.ie) {
			alert("Sorry, this feature is not supported in IE.");
			return;			
		}
		
		this.paintDiv.setMode('to-text');
		switchHighlight(this, 1);
	},
	
	changeShapeMode: function () {
		this.paintDiv.setMode('to-draw');
		switchHighlight(this, 2);
	},
	changeArrowMode: function () {
		this.paintDiv.setMode('to-draw-arrow');
		switchHighlight(this, 2);
	},
	
	
	changeShapeIndex: function () {
		this.paintDiv.setShapeIndex(this.shapeBox.getSelectedIndex());
	},
	
	updateFont: function () {
		var fntId = this.fontBox.getSelectedIndex();
		if (fntId == -1) fntId = 0;
		this.paintDiv.setFont(
			this.fontSizeBox.getValue() + 'px ' + getFont(fntId));
	},
	
	updateText: function () {
		this.paintDiv.setText(this.textBox.getValue());
	},
	
	updatePaintDiv: function () {
		var paintDiv = this.paintDiv;	
		paintDiv.setDrawingType(getDrawingType(
			this.fillTypeBox.getSelectedIndex(), 
			this.strokeTypeBox.getSelectedIndex()));
		paintDiv.setStrokeColor(this.strokeColorBox._currColor.getHex());
		paintDiv.setFillColor(this.fillColorBox._currColor.getHex());
		paintDiv.setAlpha(this.alphaSlider.getCurpos()/100);
	},
	
	unbind_: function () {
		
		this.$f('selectBtn').unlisten({onClick: [this, this.changeSelectMode]});
		this.$f('textBtn').unlisten({onClick: [this, this.changeTextMode]});
		this.$f('shapeBtn').unlisten({onClick: [this, this.changeShapeMode]});
		this.$f('arrowBtn').unlisten({onClick: [this, this.changeArrowMode]});
		this.shapeBox.unlisten({onSelect: this.changeShapeIndex});
		
		this.fontBox.unlisten({onBlur: [this, this.updateFont]});
		this.fontSizeBox.unlisten({onBlur: [this, this.updateFont]});
		this.textBox.unlisten({onBlur: [this, this.updateText]});
		
		this.strokeColorBox.listen({onChange: [this, this.updatePaintDiv]});
		this.fillColorBox.listen({onChange: [this, this.updatePaintDiv]});
		this.alphaSlider.listen({onScroll: [this, this.updatePaintDiv]});
		
		this.strokeTypeBox.unlisten({onBlur: [this, this.updatePaintDiv]});
		this.fillTypeBox.unlisten({onBlur: [this, this.updatePaintDiv]});
		
		
		this.catLbSelect = this.catLbText = 
		this.catLbShape = this.fillTypeBox = 
		this.strokeTypeBox = this.strokeColorBox = 
		this.strokeColorBox = this.fillColorBox = 
		this.alphaSlider = this.fontBox = 
		this.fontSizeBox = this.textBox = 
		this.paintDiv = this.catLbShape = 
		this.shapeBox = null;
		
		
		this.$supers('unbind_', arguments);
    }
});
});