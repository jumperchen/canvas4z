/* SimpleEffect.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jun 28, 2011 6:48:13 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.json.JSONObject;

/**
 *
 * @author simonpai
 */
public class SimpleEffect implements DrawableEffect {
	
	protected DrawingStyle _hoverStyle;
	protected DrawingStyle _selectedStyle;
	protected String _hoverText;
	// TODO: hover text tooltip
	
	public SimpleEffect() {
		this(new DrawingStyle(), new DrawingStyle());
	}
	
	public SimpleEffect(DrawingStyle hoverStyle, DrawingStyle selectedStyle) {
		_hoverStyle = hoverStyle;
		_selectedStyle = selectedStyle;
	}
	
	/**
	 * 
	 */
	public DrawingStyle getHoverStyle() {
		return _hoverStyle;
	}
	
	/**
	 * 
	 */
	public void setHoverStyle(DrawingStyle style) {
		_hoverStyle = style;
	}
	
	/**
	 * 
	 */
	public DrawingStyle getSelectedStyle() {
		return _selectedStyle;
	}
	
	/**
	 * 
	 */
	public void setSelectedStyle(DrawingStyle style) {
		_selectedStyle = style;
	}
	
	@Override
	public String getHandler() {
		return "canvas.SimpleEffect";
	}
	
	@Override
	public String toJSONString() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("hvs", _hoverStyle);
		map.put("sls", _selectedStyle);
		return JSONObject.toJSONString(map);
	}
	
}
