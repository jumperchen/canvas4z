/**
 * 
 */
package org.zkoss.canvas.drawable;

import org.zkoss.json.JSONAware;
import org.zkoss.json.JSONObject;

/**
 *
 * @author simonpai
 */
public abstract class Snapshot extends Drawable {
	
	protected double _dx;
	protected double _dy;
	protected double _dw;
	protected double _dh;
	protected double _sx;
	protected double _sy;
	protected double _sw;
	protected double _sh;
	
	public Snapshot(double dx, double dy){
		this(dx, dy, -1, -1);
	}
	
	public Snapshot(double dx, double dy, double dw, double dh){
		this(dx, dy, dw, dh, -1, -1, -1, -1);
	}
	
	public Snapshot(double dx, double dy, double dw, double dh, 
			double sx, double sy, double sw, double sh){
		_dx = dx; _dy = dy; _dw = dw; _dh = dh;
		_sx = sx; _sy = sy; _sw = sw; _sh = sh;
	}
	
	public double getDestinationX(){
		return _dx;
	}
	
	public double getDestinationY(){
		return _dy;
	}
	
	public double getDestinationWidth(){
		return _dw;
	}
	
	public double getDestinationHeight(){
		return _dh;
	}
	
	public double getSourceX(){
		return _sx;
	}
	
	public double getSourceY(){
		return _sy;
	}
	
	public double getSourceWidth(){
		return _sw;
	}
	
	public double getSourceHeight(){
		return _sh;
	}
	
	public Snapshot setDestinationPosition(double x, double y){
		_dx = x;
		_dy = y;
		return this;
	}
	
	public Snapshot setDestinationSize(double width, double height){
		_dw = width;
		_dh = height;
		return this;
	}
	
	public Snapshot setSourcePosition(double x, double y){
		_sx = x;
		_sy = y;
		return this;
	}
	
	public Snapshot setSourceSize(double width, double height){
		_sw = width;
		_sh = height;
		return this;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONAware getShapeJSONObject() {
		JSONObject result = new JSONObject();
		result.put("cnt", getSnapshotCntRef());
		
		result.put("dx", _dx);
		result.put("dy", _dy);
		
		if(_dw >= 0 && _dh >= 0) { 
			result.put("dw", _dw);
			result.put("dh", _dh);
		}
		
		if(_sx >= 0 && _sy >= 0 && _sw >= 0 && _sh >= 0) {
			result.put("sx", _sx);
			result.put("sy", _sy);
			result.put("sw", _sw);
			result.put("sh", _sh);
		}
		
		return result;
	}
	
	public abstract String getSnapshotCntRef();
	
}
