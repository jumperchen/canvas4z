/**
 * 
 */
package org.zkoss.canvas.drawable;

import org.zkoss.canvas.Canvas;

/**
 *
 * @author simonpai
 */
public class CanvasSnapshot extends Snapshot {
	
	private Canvas _canvas;
	
	public CanvasSnapshot(Canvas canvas, double dx, double dy) {
		super(dx, dy);
		_canvas = canvas;
	}
	
	public CanvasSnapshot(Canvas canvas, double dx, double dy, double dw, 
			double dh) {
		super(dx, dy, dw, dh);
		_canvas = canvas;
	}
	
	public CanvasSnapshot(Canvas canvas, double dx, double dy, double dw, 
			double dh, double sx, double sy, double sw, double sh) {
		super(dx, dy, dw, dh, sx, sy, sw, sh);
		_canvas = canvas;
	}
	
	public Canvas getCanvas(){
		return _canvas;
	}
	
	public CanvasSnapshot setCanvas(Canvas canvas){
		_canvas = canvas;
		return this;
	}
	
	@Override
	public String getSnapshotCntRef() {
		return _canvas.getUuid();
	}
	
	@Override
	public String getType() {
		return "canvas.CanvasSnapshot";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new CanvasSnapshot(_canvas, _dx, _dy, _dw, _dh, _sx, _sy, _sw, _sh);
	}
	
}
