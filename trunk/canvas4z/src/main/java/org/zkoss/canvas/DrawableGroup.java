/* DrawableGroup.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jun 9, 2011 10:05:28 AM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author simonpai
 */
public class DrawableGroup extends CompositeDrawable {
	
	protected List<Drawable> _drawables;
	
	/**
	 * 
	 * @param drawables
	 */
	public DrawableGroup(Drawable ... drawables) {
		_drawables = Arrays.asList(drawables);
	}
	
	/**
	 * 
	 * @param drawables
	 */
	public DrawableGroup(List<Drawable> drawables) {
		_drawables = drawables;
	}
	
	@Override
	public List<Drawable> getDrawables() {
		return _drawables;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		List<Drawable> list = new ArrayList<Drawable>();
		for(Drawable d : _drawables)
			list.add((Drawable) d.clone());
		return new DrawableGroup(list);
	}
	
}
