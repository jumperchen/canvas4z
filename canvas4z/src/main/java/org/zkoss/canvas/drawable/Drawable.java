/* Drawable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 13, 2010 11:55:17 AM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.drawable;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.canvas.DrawingStyle;
import org.zkoss.json.JSONAware;
import org.zkoss.json.JSONObject;

/**
 * 
 * @author simon
 */
public abstract class Drawable implements JSONAware, Cloneable {
	
	protected DrawingStyle _style;
	
	// TODO: draggable
	
	/**
	 * 
	 */
	public Drawable() {
		_style = new DrawingStyle();
	}
	
	/**
	 * Returns the type of Drawable to specify the corresponding Drawable js 
	 * class on the client side.
	 */
	public abstract String getType();
	
	/**
	 * Returns a JSON Object representing ONLY the shape.
	 * The drawing state is covered in toJSONString()
	 */
	public abstract JSONAware getShapeJSONObject();
	
	@Override
	public String toJSONString() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objtp", getType());
		map.put("obj", getShapeJSONObject());
		map.put("state", _style);
		// TODO: draggable
		return JSONObject.toJSONString(map);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
	
	// state management //
	/**
	 * Returns the drawing style.
	 */
	public DrawingStyle getDrawingStyle() {
		return _style;
	}
	
	/**
	 * Handle drawing style by a callback.
	 */
	public Drawable setStyle(StyleSetter setter) {
		setter.set(_style);
		return this;
	}
	
	public static interface StyleSetter {
		public void set(DrawingStyle style);
	}
	
	/**
	 * Uses the state from another Drawable. 
	 */
	public Drawable copyStyleFrom(Drawable drawable) {
		_style = new DrawingStyle(drawable._style);
		return this;
	}
	
}
