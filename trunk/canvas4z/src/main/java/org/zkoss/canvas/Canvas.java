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
import java.util.Iterator;
import java.util.List;

import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
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
	
	public Canvas() {
		_drawables = new ArrayList<Drawable>();
	}
	
	/* getter */
	/**
	 * Return a list of all drawings in Canvas.
	 */
	public List<Drawable> getAllDrawables(){
		List<Drawable> result = new ArrayList<Drawable>();
		Iterator<Drawable> itr = _drawables.iterator();
		while(itr.hasNext())
			result.add(itr.next());
		return result; 
	}
	
	/**
	 * Returns an iterator of Drawable list.
	 */
	public Iterator<Drawable> getDrawableIterator(){
		return _drawables.iterator();
	}
	
	/**
	 * Returns the drawing at position index. 
	 * @param index: drawings at 0 is the earliest drawing.
	 */
	public Drawable getDrawable(int index){
		return _drawables.get(index);
	}
	
	/* query */
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
	
	
	
	/* operation */
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
	 * Adds a Image snapshot
	 */
	public void add(Image image, double dx, double dy){
		add(new ImageSnapshot(image, dx, dy));
	}
	
	/**
	 * Adds a Image snapshot
	 */
	public void add(Image image, double dx, double dy, double dw, double dh){
		add(new ImageSnapshot(image, dx, dy, dw, dh));
	}
	
	/**
	 * Adds a Image snapshot
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
	 * 
	 */
	public void insert(int index, Path2D.Double path){
		insert(index, new Path(path));
	}
	
	/**
	 * 
	 */
	public void insert(int index, Rectangle2D.Double rectangle){
		insert(index, new Rectangle(rectangle));
	}
	
	/**
	 * 
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
	 * 
	 */
	public Drawable replace(int index, Path2D.Double path){
		return replace(index, new Path(path));
	}
	
	/**
	 * 
	 */
	public Drawable replace(int index, Rectangle2D.Double rectangle){
		return replace(index, new Rectangle(rectangle));
	}
	
	/**
	 * 
	 */
	public Drawable replace(int index, String text, double x, double y){
		return replace(index, new Text(text, x, y));
	}
	
	
	
	/* helper */
	private static Drawable cloneIfPossible(Drawable drawable){
		/*
		try {
			return (Drawable) drawable.clone();
		} catch (CloneNotSupportedException e) {}
		*/
		return drawable;
	}
	
	/* super */
	protected void renderProperties(ContentRenderer renderer) 
		throws IOException {
		
		super.renderProperties(renderer);
		render(renderer, "drwngs", JSONValue.toJSONString(_drawables));
		
	}
	
	public String getZclass() {
		return _zclass == null ? "z-canvas" : _zclass;
	}
	
}

