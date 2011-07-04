/* CompositeDrawable.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jun 8, 2011 11:56:44 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.drawable;

import java.util.List;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONAware;

/**
 *
 * @author simonpai
 */
public abstract class CompositeDrawable extends Drawable {
	
	public abstract List<Drawable> getDrawables();
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONAware getShapeJSONObject() {
		JSONArray result = new JSONArray();
		for(Drawable d : getDrawables())
			result.add(d);
		return result;
	}
	
	@Override
	public String getType() {
		return "canvas.Composite";
	}
	
}
