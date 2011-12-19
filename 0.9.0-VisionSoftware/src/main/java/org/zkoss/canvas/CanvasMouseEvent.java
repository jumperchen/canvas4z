/* CanvasMouseEvent.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jul 1, 2011 4:35:52 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas;

import java.util.Map;

import org.zkoss.canvas.drawable.Drawable;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.MouseEvent;

/**
 *
 * @author simonpai
 */
public class CanvasMouseEvent extends MouseEvent {
	
	private static final long serialVersionUID = -7393602518805779546L;
	
	/**
	 * 
	 */
	public static final String ON_TOOLTIP = "onTooltip";
	
	/**
	 * 
	 */
	public static final String ON_SELECT = "onSelect";
	
	private final int _index;
	private final Drawable _drawable;
	private final int _prevIndex;
	private final Drawable _prevDrawable;
	
	/**
	 * Converts an AU request to a canvas mouse event.
	 */
	public static CanvasMouseEvent getEvent(AuRequest request) {
		final Map<?,?> data = request.getData();
		final String name = request.getCommand();
		final int keys = AuRequests.parseKeys(data);
		return new CanvasMouseEvent(name, request.getComponent(), 
				AuRequests.getInt(data, "i", 0, true),
				AuRequests.getInt(data, "pi", 0, true),
				AuRequests.getInt(data, "x", 0, true),
				AuRequests.getInt(data, "y", 0, true),
				AuRequests.getInt(data, "pageX", 0, true),
				AuRequests.getInt(data, "pageY", 0, true), keys);
	}
	
	/**
	 * 
	 */
	public CanvasMouseEvent(String name, Component target, int index, int prevIndex, int x, int y,
			int pageX, int pageY, int keys) {
		super(name, target, x, y, pageX, pageY, keys);
		Canvas cvs = (Canvas) target;
		_index = index;
		_drawable = index < 0 ? null : cvs.getDrawable(index);
		_prevIndex = prevIndex;
		_prevDrawable = prevIndex < 0 ? null : cvs.getDrawable(prevIndex);
	}
	
	/**
	 * 
	 */
	public int getIndex() {
		return _index;
	}
	
	/**
	 * 
	 */
	public Drawable getDrawable() {
		return _drawable;
	}
	
	/**
	 * 
	 */
	public int getPrevIndex() {
		return _prevIndex;
	}
	
	/**
	 * 
	 */
	public Drawable getPrevDrawable() {
		return _prevDrawable;
	}
	
}
