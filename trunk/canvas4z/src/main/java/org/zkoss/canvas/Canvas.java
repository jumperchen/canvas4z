/* Canvas.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 12, 2010 3:16:36 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.zkoss.canvas.drawable.Drawable;
import org.zkoss.canvas.util.AbstractProxyList;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.XulElement;

/**
 * The prototype component corresponding to HTML 5 Canvas.
 * While HTML 5 Canvas is a command-based DOM object that allows user to draw
 * items on a surface, This Canvas maintains a list of drawable items and allow
 * user to operate the list by adding, removing, updating, replacing the 
 * elements. The changes will be reflected on the client side upon these
 * operations.
 * 
 * @author simon
 *
 */
@SuppressWarnings("serial")
public class Canvas extends XulElement {
	
	protected CanvasProxyList _drawables = 
		new CanvasProxyList(new ArrayList<Drawable>());
	
	static {
		addClientEvent(Canvas.class, CanvasMouseEvent.ON_TOOLTIP, CE_REPEAT_IGNORE);
		addClientEvent(Canvas.class, CanvasMouseEvent.ON_SELECT, 0);
	}
	
	/**
	 * Return a list of all drawings in Canvas.
	 */
	public List<Drawable> getDrawables() {
		return _drawables;
	}
	
	/**
	 * Returns the drawing at position index. 
	 * @param index: drawings at 0 is the earliest drawing.
	 */
	public Drawable getDrawable(int index) {
		return _drawables.get(index);
	}
	
	/**
	 * Return true if the list is empty.
	 */
	public boolean isEmpty() {
		return _drawables.isEmpty();
	}
	
	/**
	 * Return the size of Drawable lists
	 */
	public int size() {
		return _drawables.size();
	}
	
	/**
	 * Clears the Drawable list. The Canvas is also cleared as a result.
	 */
	public void clear(){
		_drawables.inner().clear();
		smartUpdate("clear", null);
	}
	
	/**
	 * Add the Drawable objects to the end of the list.
	 */
	public boolean add(Drawable ... drawables) {
		_drawables.inner().addAll(Arrays.asList(drawables));
		smartUpdate("addAll", JSONArray.toJSONString(drawables), true);
		return drawables.length > 0;
	}
	
	/**
	 * Removes the Drawable at specific index.
	 * @return The removed Drawable
	 */
	public Drawable remove(int index) {
		Drawable removed = _drawables.inner().remove(index);
		smartUpdate("remove", index, true);
		return removed;
	}
	
	/**
	 * Remove the given object from Drawable list
	 * @return true if the list is changed
	 */
	public boolean remove(Object obj) {
		int index = _drawables.indexOf(obj);
		if (index < 0)
			return false;
		remove(index);
		return true;
	}
	
	/**
	 * Insert Drawables at specific index
	 */
	@SuppressWarnings("unchecked")
	public boolean add(int index, Drawable ... drawables){
		if (drawables.length == 0)
			return false;
		_drawables.inner().addAll(index, Arrays.asList(drawables));
		JSONObject args = new JSONObject();
		args.put("i", index);
		args.put("drws", drawables);
		smartUpdate("insert", args, true);
		return true;
	}
	
	/**
	 * Replace a Drawable at specific index.
	 * @return The replaced Drawable
	 */
	@SuppressWarnings("unchecked")
	public Drawable set(int index, Drawable drawable){
		Drawable removed = _drawables.inner().set(index, drawable);
		JSONObject args = new JSONObject();
		args.put("i", index);
		args.put("drw", drawable);
		smartUpdate("replace", args, true);
		return removed;
	}
	
	// TODO: replaceAll
	
	/**
	 * Update the drawable at given index.
	 */
	public void update(int index) { // TODO: range
		set(index, _drawables.get(index));
	}
	
	/**
	 * Update the given drawable.
	 */
	public void update(Drawable drawable) {
		int i = _drawables.indexOf(drawable);
		if (i < 0)
			throw new IllegalArgumentException("Drawable " + drawable + 
					"is not a member of canvas " + this);
		set(i, drawable);
	}
	
	
	
	// super //
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(CanvasMouseEvent.ON_SELECT) || 
				cmd.equals(CanvasMouseEvent.ON_TOOLTIP)) {
			CanvasMouseEvent evt = CanvasMouseEvent.getEvent(request);
			Events.postEvent(evt);
		} else 
			super.service(request, everError);
	}
	
	protected void renderProperties(ContentRenderer renderer) 
		throws IOException {
		
		super.renderProperties(renderer);
		render(renderer, "drwngs", JSONValue.toJSONString(_drawables));
		
	}
	
	public String getZclass() {
		return _zclass == null ? "z-canvas" : _zclass;
	}
	
	
	
	// helper //
	
	// proxy list //
	private class CanvasProxyList extends AbstractProxyList<Drawable> {
		
		private CanvasProxyList(List<Drawable> list) { super(list); }
		private List<Drawable> inner() { return _list; }
		
		@Override
		public void clear() {
			Canvas.this.clear();
		}
		@Override
		public boolean add(Drawable e) {
			return Canvas.this.add(e);
		}
		@Override
		public boolean addAll(Collection<? extends Drawable> c) {
			return Canvas.this.add(c.toArray(new Drawable[0]));
		}
		@Override
		public void add(int index, Drawable element) {
			Canvas.this.add(index, element);
		}
		@Override
		public boolean addAll(int index, Collection<? extends Drawable> c) {
			return Canvas.this.add(index, c.toArray(new Drawable[0]));
		}
		@Override
		public boolean remove(Object o) {
			return Canvas.this.remove(o);
		}
		@Override
		public Drawable remove(int index) {
			return Canvas.this.remove(index);
		}
		@Override
		public Drawable set(int index, Drawable element) {
			return Canvas.this.set(index, element);
		}
		@Override
		public List<Drawable> subList(int fromIndex, int toIndex) {
			throw new UnsupportedOperationException(); // TODO
		}
		
	}
	
}

