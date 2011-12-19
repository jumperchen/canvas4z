/* TestCaseController.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 27, 2010 3:55:26 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.test;

import org.zkoss.zul.*;
import org.zkoss.canvas.*;
import org.zkoss.canvas.drawable.CompositeDrawable;
import org.zkoss.canvas.drawable.Drawable;
import org.zkoss.canvas.drawable.DrawableGroup;
import org.zkoss.canvas.drawable.ImageSnapshot;
import org.zkoss.canvas.drawable.Path;
import org.zkoss.canvas.drawable.Rectangle;
import org.zkoss.canvas.drawable.Text;
import org.zkoss.canvas.util.Shapes;
import org.zkoss.canvas.DrawingStyle.*;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;

/**
 * 
 * @author simon
 */
@SuppressWarnings("serial")
public class TestCaseController extends GenericForwardComposer {
	
	private Grid grid;
	private Image img;
	
	/*
	private Drawable[] drawables = { 
		new Rectangle(25, 25, 50, 50), 
		new Path().moveTo(50, 25).lineTo(75, 75).lineTo(25, 75).closePath(),
		Shapes.heart(100),
		new Text("ZK", 25, 65)
		//new ImageSnapshot(img, 0, 0)
	};
	*/
	
	private DrawingStyleApplier[] _appliers = {
		new DrawingStyleApplier(){
			public String getName() {
				return "No Style";
			}
			public void doStyles(DrawingStyle s) {
				// do nothing
			}
		},
		new DrawingStyleApplier(){
			public String getName() {
				return "Color, Alpha";
			}
			public void doStyles(DrawingStyle s) {
				s.clear();
				s.setDrawingType(DrawingType.BOTH);
				s.setStrokeStyle("#00FFFF");
				s.setFillStyle("#FF00FF");
				s.setAlpha(0.3);
			}
		},
		// TODO gradient, pattern
		new DrawingStyleApplier() {
			public String getName() {
				return "Line Style";
			}
			public void doStyles(DrawingStyle s) {
				s.clear();
				s.setDrawingType(DrawingType.STROKE);
				s.setLineWidth(5);
				s.setLineJoin(LineJoin.ROUND);
			}
		},
		new DrawingStyleApplier() {
			public String getName() {
				return "Shadow";
			}
			public void doStyles(DrawingStyle s) {
				s.clear();
				s.setDrawingType(DrawingType.FILL);
				s.setShadowColor("#FFFF00");
				s.setShadowOffset(20, 20);
				s.setShadowBlur(10);
			}
		},
		new DrawingStyleApplier() {
			public String getName() {
				return "Transformation";
			}
			public void doStyles(DrawingStyle s) {
				double cos60 = Math.cos(Math.PI/3);
				double sin60 = Math.sin(Math.PI/3);
				s.clear();
				s.setDrawingType(DrawingType.BOTH);
				s.setTransformation(cos60, sin60, -sin60, cos60, 60, 0);
			}
		},
		new DrawingStyleApplier() {
			public String getName() {
				return "Clipping";
			}
			public void doStyles(DrawingStyle s) {
				s.clear();
				s.setDrawingType(DrawingType.BOTH);
				s.setClipping(new Path().moveTo(0, 0).lineTo(100, 0)
							.lineTo(0, 100).closePath());
			}
		}
	};
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// columns
		for(DrawingStyleApplier app : _appliers)
			grid.getColumns().appendChild(createUnitColumn(app.getName()));
		
		Drawable[] drawables = { 
				new Rectangle(25, 25, 50, 50), 
				new Path().moveTo(50, 25).lineTo(75, 75).lineTo(25, 75).closePath(),
				Shapes.heart(100),
				new Text("ZK", 25, 65),
				new DrawableGroup(Shapes.heart(100), new Text("ZK", 45, 85)),
				new ImageSnapshot(img, 0, 0)
		};
		
		// rows
		for(Drawable d : drawables){
			Row r = new Row();
			
			for(DrawingStyleApplier app : _appliers)
				r.appendChild(createUnitCanvas(app.applyStyle((Drawable)d.clone())));
			
			grid.getRows().appendChild(r);
		}
	}
	
	private abstract class DrawingStyleApplier {
		public abstract void doStyles(DrawingStyle s);
		public abstract String getName();
		
		public Drawable applyStyle(Drawable drawable){
			doStyles(drawable.getDrawingStyle());
			doDefault(drawable);
			return drawable;
		}
		
		protected void doDefault(Drawable d) {
			if(d instanceof Text)
				d.getDrawingStyle().setFont("40px serif");
			else if(d instanceof CompositeDrawable)
				for(Drawable dc : ((CompositeDrawable) d).getDrawables())
					doDefault(dc);
		}
	}
	
	private Column createUnitColumn(String text){
		Column result = new Column(text);
		result.setWidth("106px");
		return result;
	}
	
	private Canvas createUnitCanvas(Drawable drawable){
		Canvas result = new Canvas();
		result.setWidth("100px");
		result.setHeight("100px");
		if(drawable != null) result.add(drawable);
		return result;
	}
	
}
