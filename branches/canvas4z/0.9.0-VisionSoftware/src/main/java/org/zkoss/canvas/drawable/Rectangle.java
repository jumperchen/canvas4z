/* Rectangle.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 13, 2010 12:35:23 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.drawable;

import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.zkoss.json.JSONAware;
import org.zkoss.json.JSONObject;

/**
 * @author simon
 *
 */
public class Rectangle extends Shape {
	
	public static final String TYPE = "canvas.Rectangle";
	
	/**
	 * Constructs a Rectangle at (0,0) with 0 width and height
	 */
	public Rectangle() {
		super();
		_internalShape = new Rectangle2D.Double();
	}
	
	/**
	 * Creates a new Rectangle.
	 * @param x: x-position
	 * @param y: y-position
	 * @param w: width
	 * @param h: height
	 */
	public Rectangle(double x, double y, double w, double h) {
		super();
		_internalShape = new Rectangle2D.Double(x, y, w, h);
	}
	
	/**
	 * Creates a new Rectangle based on the given Rectangle2D.Float
	 */
	public Rectangle(Rectangle2D.Double rect){
		super();
		_internalShape = (java.awt.Shape) rect.clone();
	}
	
	/**
	 * Creates a new Rectangle by cloning another
	 */
	public Rectangle(Rectangle rect){
		this((Rectangle2D.Double) rect._internalShape); 
	}
	
	
	
	/* interface implementation */
	@Override
	public String getType() {
		// This value must match the setting in Canvas.js #_paint
		return TYPE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONAware getShapeJSONObject() {
		Rectangle2D.Double rect = (Rectangle2D.Double) _internalShape;
		JSONObject m = new JSONObject();
		m.put("x", rect.x);
		m.put("y", rect.y);
		m.put("w", rect.width);
		m.put("h", rect.height);
		return m;
	}
	
	@Override
	public Object clone() {
		return new Rectangle(this);
	}
	
	

	/* delegate methods to Rectangle2D.Double */
	public void add(double newx, double newy) {
		((Rectangle2D.Double) _internalShape).add(newx, newy);
	}
	
	public void add(Point2D pt) {
		((Rectangle2D.Double) _internalShape).add(pt);
	}
	
	public void add(Rectangle2D r) {
		((Rectangle2D.Double) _internalShape).add(r);
	}
	
	// method that returns Java 2D object
	public Rectangle2D createIntersection(Rectangle2D r) {
		return ((Rectangle2D.Double) _internalShape).createIntersection(r);
	}
	
	// method that returns Java 2D object
	public Rectangle2D createUnion(Rectangle2D r) {
		return ((Rectangle2D.Double) _internalShape).createUnion(r);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Rectangle)
			return ((Rectangle2D.Double) _internalShape).equals(((Rectangle)obj)._internalShape);
		return false;
	}
	
	public double getCenterX() {
		return ((Rectangle2D.Double) _internalShape).getCenterX();
	}
	
	public double getCenterY() {
		return ((Rectangle2D.Double) _internalShape).getCenterY();
	}
	
	// method that returns Java 2D object
	public Rectangle2D getFrame() {
		return ((Rectangle2D.Double) _internalShape).getFrame();
	}
	
	public double getHeight() {
		return ((Rectangle2D.Double) _internalShape).getHeight();
	}
	
	public double getMaxX() {
		return ((Rectangle2D.Double) _internalShape).getMaxX();
	}
	
	public double getMaxY() {
		return ((Rectangle2D.Double) _internalShape).getMaxY();
	}
	
	public double getMinX() {
		return ((Rectangle2D.Double) _internalShape).getMinX();
	}
	
	public double getMinY() {
		return ((Rectangle2D.Double) _internalShape).getMinY();
	}
	
	public double getWidth() {
		return ((Rectangle2D.Double) _internalShape).getWidth();
	}
	
	public double getX() {
		return ((Rectangle2D.Double) _internalShape).getX();
	}
	
	public double getY() {
		return ((Rectangle2D.Double) _internalShape).getY();
	}
	
	public int hashCode() {
		return ((Rectangle2D.Double) _internalShape).hashCode();
	}
	
	public boolean intersectsLine(double x1, double y1, double x2, double y2) {
		return ((Rectangle2D.Double) _internalShape).intersectsLine(x1, y1, x2, y2);
	}
	
	public boolean intersectsLine(Line2D l) {
		return ((Rectangle2D.Double) _internalShape).intersectsLine(l);
	}
	
	public boolean isEmpty() {
		return ((Rectangle2D.Double) _internalShape).isEmpty();
	}
	
	public int outcode(double x, double y) {
		return ((Rectangle2D.Double) _internalShape).outcode(x, y);
	}
	
	public int outcode(Point2D p) {
		return ((Rectangle2D.Double) _internalShape).outcode(p);
	}
	
	public void setFrame(double x, double y, double w, double h) {
		((Rectangle2D.Double) _internalShape).setFrame(x, y, w, h);
	}
	
	public void setFrame(Point2D loc, Dimension2D size) {
		((Rectangle2D.Double) _internalShape).setFrame(loc, size);
	}
	
	public void setFrame(Rectangle2D r) {
		((Rectangle2D.Double) _internalShape).setFrame(r);
	}
	
	public void setFrameFromCenter(double centerX, double centerY,
			double cornerX, double cornerY) {
		((Rectangle2D.Double) _internalShape)
			.setFrameFromCenter(centerX, centerY, cornerX, cornerY);
	}
	
	public void setFrameFromCenter(Point2D center, Point2D corner) {
		((Rectangle2D.Double) _internalShape)
			.setFrameFromCenter(center, corner);
	}
	
	public void setFrameFromDiagonal(double x1, double y1, double x2, double y2) {
		((Rectangle2D.Double) _internalShape)
			.setFrameFromDiagonal(x1, y1, x2, y2);
	}
	
	public void setFrameFromDiagonal(Point2D p1, Point2D p2) {
		((Rectangle2D.Double) _internalShape).setFrameFromDiagonal(p1, p2);
	}
	
	public void setRect(double x, double y, double w, double h) {
		((Rectangle2D.Double) _internalShape).setRect(x, y, w, h);
	}
	
	public void setRect(float x, float y, float w, float h) {
		((Rectangle2D.Double) _internalShape).setRect(x, y, w, h);
	}
	
	public void setRect(Rectangle2D r) {
		((Rectangle2D.Double) _internalShape).setRect(r);
	}
	
	public String toString() {
		return ((Rectangle2D.Double) _internalShape).toString();
	}
	/* end of delegation */
	
}
