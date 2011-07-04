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

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zkoss.canvas.drawable.CanvasSnapshot;
import org.zkoss.canvas.drawable.Drawable;
import org.zkoss.canvas.drawable.ImageSnapshot;
import org.zkoss.canvas.drawable.Path;
import org.zkoss.canvas.drawable.Rectangle;
import org.zkoss.canvas.drawable.Text;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Image;
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
	
	private List<Drawable> _drawables;
	
	static {
		addClientEvent(Canvas.class, CanvasMouseEvent.ON_TOOLTIP, CE_REPEAT_IGNORE);
		addClientEvent(Canvas.class, CanvasMouseEvent.ON_SELECT, 0);
	}
	
	public Canvas() {
		_drawables = new ArrayList<Drawable>();
	}
	
	/**
	 * Return a list of all drawings in Canvas.
	 */
	public List<Drawable> getAllDrawables(){
		return Collections.unmodifiableList(_drawables);
	}
	
	/**
	 * Returns the drawing at position index. 
	 * @param index: drawings at 0 is the earliest drawing.
	 */
	public Drawable getDrawable(int index){
		return _drawables.get(index);
	}
	
	/**
	 * Return true if the list is empty.
	 */
	public boolean isEmpty(){
		return _drawables.isEmpty();
	}
	
	/**
	 * Returns the size of Drawable lists
	 */
	public int size(){
		return _drawables.size();
	}
	
	/**
	 * Adds the Drawable object to the end of the list.
	 */
	public void add(Drawable drawable){
		_drawables.add(cloneIfPossible(drawable));
		smartUpdate("add", drawable.toJSONString(), true);
	}
	
	/**
	 * Adds a Java 2D Path object.
	 */
	public void add(Path2D.Double path){
		add(new Path(path));
	}
	
	/**
	 * Adds a Java 2D Rectangle object.
	 */
	public void add(Rectangle2D.Double rectangle){
		add(new Rectangle(rectangle));
	}
	
	/**
	 * Adds a Text object.
	 */
	public void add(String text, double x, double y){
		add(new Text(text, x, y));
	}
	
	// TODO: delegate other image type
	/**
	 * Adds an Image snapshot
	 */
	public void add(Image image, double dx, double dy){
		add(new ImageSnapshot(image, dx, dy));
	}
	
	/**
	 * Adds an Image snapshot
	 */
	public void add(Image image, double dx, double dy, double dw, double dh){
		add(new ImageSnapshot(image, dx, dy, dw, dh));
	}
	
	/**
	 * Adds an Image snapshot
	 */
	public void add(Image image, double dx, double dy, double dw, double dh,
			double sx, double sy, double sw, double sh){
		add(new ImageSnapshot(image, dx, dy, dw, dh, sx, sy, sw, sh));
	}
	
	/**
	 * Adds a Canvas snapshot
	 */
	public void add(Canvas canvas, double dx, double dy){
		add(new CanvasSnapshot(canvas, dx, dy));
	}
	
	/**
	 * Adds a Canvas snapshot
	 */
	public void add(Canvas canvas, double dx, double dy, double dw, double dh){
		add(new CanvasSnapshot(canvas, dx, dy, dw, dh));
	}
	
	/**
	 * Adds a Canvas snapshot
	 */
	public void add(Canvas canvas, double dx, double dy, double dw, double dh,
			double sx, double sy, double sw, double sh){
		add(new CanvasSnapshot(canvas, dx, dy, dw, dh, sx, sy, sw, sh));
	}
	
	/**
	 * Removes the Drawable at specific index.
	 * @return The removed Drawable
	 */
	public Drawable remove(int index){
		Drawable removed = _drawables.remove(index);
		smartUpdate("remove", index, true);
		return removed;
	}
	
	/**
	 * Clears the Drawable list. The Canvas is also cleared as a result.
	 */
	public void clear(){
		_drawables.clear();
		smartUpdate("clear", null);
	}
	
	/**
	 * Inserts the Drawable at specific index
	 */
	@SuppressWarnings("unchecked")
	public void insert(int index, Drawable drawable){
		_drawables.add(index, cloneIfPossible(drawable));
		JSONObject args = new JSONObject();
		args.put("i", index);
		args.put("drw", drawable);
		smartUpdate("insert", args, true);
	}
	
	/**
	 * Inserts a Path.
	 */
	public void insert(int index, Path2D.Double path){
		insert(index, new Path(path));
	}
	
	/**
	 * Inserts a Rectangle.
	 */
	public void insert(int index, Rectangle2D.Double rectangle){
		insert(index, new Rectangle(rectangle));
	}
	
	/**
	 * Inserts a piece of Text.
	 */
	public void insert(int index, String text, double x, double y){
		insert(index, new Text(text, x, y));
	}
	
	/**
	 * Replace a Drawable at specific index.
	 * @return The replaced Drawable
	 */
	@SuppressWarnings("unchecked")
	public Drawable replace(int index, Drawable drawable){
		Drawable removed = _drawables.remove(index);
		_drawables.add(index, cloneIfPossible(drawable));
		JSONObject args = new JSONObject();
		args.put("i", index);
		args.put("drw", drawable);
		smartUpdate("replace", args, true);
		return removed;
	}
	
	/**
	 * Replaces a Path.
	 */
	public Drawable replace(int index, Path2D.Double path){
		return replace(index, new Path(path));
	}
	
	/**
	 * Replaces a Rectangle.
	 */
	public Drawable replace(int index, Rectangle2D.Double rectangle){
		return replace(index, new Rectangle(rectangle));
	}
	
	/**
	 * Replaces a piece of Text.
	 */
	public Drawable replace(int index, String text, double x, double y){
		return replace(index, new Text(text, x, y));
	}
	
	
	
	// helper //
	private static Drawable cloneIfPossible(Drawable drawable){
		/*
		try {
			return (Drawable) drawable.clone();
		} catch (CloneNotSupportedException e) {}
		*/
		return drawable;
	}
	
	// service //
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(CanvasMouseEvent.ON_SELECT) || 
				cmd.equals(CanvasMouseEvent.ON_TOOLTIP)) {
			CanvasMouseEvent evt = CanvasMouseEvent.getEvent(request);
			Events.postEvent(evt);
		} else 
			super.service(request, everError);
	}
	
	// super //
	protected void renderProperties(ContentRenderer renderer) 
		throws IOException {
		
		super.renderProperties(renderer);
		render(renderer, "drwngs", JSONValue.toJSONString(_drawables));
		
	}
	
	public String getZclass() {
		return _zclass == null ? "z-canvas" : _zclass;
	}
	
}

