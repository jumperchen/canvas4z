package org.zkoss.canvas.zkpaint2;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.canvas.Canvas;
import org.zkoss.canvas.DrawingStyle;
import org.zkoss.canvas.drawable.Drawable;
import org.zkoss.canvas.drawable.Path;
import org.zkoss.canvas.drawable.Rectangle;
import org.zkoss.canvas.drawable.Shape;
import org.zkoss.canvas.drawable.Text;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuSetAttribute;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Div;

public class PaintDiv extends Div implements AfterCompose {
	
	private String strokeColor;
	private String fillColor;
	private String drawingType;
	private double alpha = 0;
	
	private String text;
	private String font;
	private int shapeIndex = 0;
	private List<Shape> shapeCategory;
	
	private Canvas canvas;
	
	private Number arrowWidth = 3;
	private Number tipWidth = 10;
	private Number tipLength = 15;

	private List<Drawable> _selShapes;
	private List<Integer> _selShapeIndexes;
	
	private Map<Text, Integer[]> _textSizeMap = new HashMap<Text, Integer[]>();
	
	public void afterCompose() {
		setWidgetClass("canvas.PaintDiv");
		
		String w = getWidth();
		String h = getHeight();
		
		if (Strings.isBlank(w))
			w = "100%";
		
		if (Strings.isBlank(h))
			h = "100%";
		
		canvas = new Canvas();
		canvas.setStyle("position:absolute; " +
			"z-index:1; " +
			"width:"+w+"; height:"+h+"; "+
			"background-color:white");
		
		appendChild(canvas);
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(String strokeColor) {
		if (!Objects.equals(this.strokeColor, strokeColor)) {
			this.strokeColor = strokeColor;
			smartUpdate("strokeColor", strokeColor);
		}
	}

	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		if (!Objects.equals(this.fillColor, fillColor)) {
			this.fillColor = fillColor;
			smartUpdate("fillColor", fillColor);
		}
	}

	public String getDrawingType() {
		return drawingType;
	}

	public void setDrawingType(String drawingType) {
		if (!Objects.equals(this.drawingType, drawingType)) {
			this.drawingType = drawingType;
			smartUpdate("drawingType", drawingType);
		}
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		if (this.alpha != alpha) {
			this.alpha = alpha;
			smartUpdate("alpha", alpha);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (!Objects.equals(this.text, text)) {
			this.text = text;
			smartUpdate("text", text);
		}
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		if (!Objects.equals(this.font, font)) {
			this.font = font;
			smartUpdate("font", font);
		}
	}

	public int getShapeIndex() {
		return shapeIndex;
	}

	public void setShapeIndex(int shapeIndex) {
		if (this.shapeIndex != shapeIndex) {
			this.shapeIndex = shapeIndex;
			smartUpdate("shapeIndex", shapeIndex);
		}
	}
	
	public void setShapeCategory(List<Shape> shapeCategory) {
		if (!Objects.equals(this.shapeCategory, shapeCategory)) {
			this.shapeCategory = shapeCategory;
			smartUpdate("shapeCategory",  JSONValue.toJSONString(shapeCategory));
		}
	}

	public List<Shape> getShapeCategory() {
		return shapeCategory;
	}
	
	public void setArrowAttributes(Number arrowWidth, Number tipWidth, Number tipLength) {
		
		if (!Objects.equals(this.arrowWidth, arrowWidth) ||
			!Objects.equals(this.tipWidth, tipWidth)||
			!Objects.equals(this.tipLength, tipLength)) {
			
			this.arrowWidth = arrowWidth;
			this.tipWidth = tipWidth;
			this.tipLength = tipLength;
			
			JSONObject o = new JSONObject();
			o.put("arrowWidth", arrowWidth);
			o.put("tipWidth", tipWidth);
			o.put("tipLength", tipLength);
			smartUpdate("arrowAttrs",  o);
		}
		
	}
	
	@Override
	public void service(AuRequest request, boolean everError) {
		String name = request.getCommand();
		
		Map data = (Map) request.getData();
		if ("onAddText".equals(name)) {
			Text txt = new Text(getText(), getDouble(data, "x"), getDouble(data, "y"));
			txt.getDrawingStyle().setFont(getFont());
			setDrawingState(txt.getDrawingStyle());
			
			JSONArray textSize = (JSONArray)data.get("textSize");
			_textSizeMap.put(txt, new Integer[]{
					(Integer) textSize.get(0), (Integer) textSize.get(1)});
			
			data.clear();
			data.put("text", txt);
			Events.postEvent(new Event(name, this, data));
		} else if ("onAddShape".equals(name)) {
			Shape shape = shapeCategory.get(shapeIndex);
			double x = getDouble(data, "x");
			double y = getDouble(data, "y");
			double w = getDouble(data, "w");
			double h = getDouble(data, "h");
			
			if (shape instanceof Rectangle) {
				shape = new Rectangle(x, y ,w, h);
			} else {
				Path p = new Path((Path) shape);
				p.transform(new AffineTransform(w / 1000, 0, 0,
						h / 1000, x, y));
				shape = p;
			}
			
			setDrawingState(shape.getDrawingStyle());
			
			data.clear();
			data.put("shape", shape);
			Events.postEvent(new Event(name, this, data));
		} else if ("onUpdateAttrs".equals(name)) {
			disableClientUpdate(true);
			
			setDrawingType(String.valueOf(data.get("drawingType")));
			setStrokeColor(String.valueOf(data.get("strokeColor")));
			setFillColor(String.valueOf(data.get("fillColor")));
			setAlpha(getDouble(data, "alpha"));
			setText(String.valueOf(data.get("text")));
			setFont(String.valueOf(data.get("font")));
			setShapeIndex(getInt(data, "shapeIndex"));
			
			disableClientUpdate(false);
		} else if ("onAddArrow".equals(name)) {
			
			Path p = ShapeUtils.jsonToPath((Map)data.get("path"));
			data.clear();
			data.put("arrow", p);
			Events.postEvent(new Event(name, this, data));
		} else if ("onSelect".equals(name)) {
			int x = getInt(data, "x");
			int y = getInt(data, "y");
			int w = getInt(data, "w");
			int h = getInt(data, "h");
			
			_selShapes = new ArrayList<Drawable>();
			_selShapeIndexes = new ArrayList<Integer>();
			
			
			int i = 0;
			for (Drawable d : canvas.getAllDrawables()) {
				boolean intersected = false;
				if (d instanceof Shape) {
					Shape s = (Shape) d;
					intersected = s.intersects(x, y, w, h);
				} else if (d instanceof Text) {
					Text t = (Text) d;
					Integer[] textSize = _textSizeMap.get(t);
					
					if(textSize!=null && textSize.length>0) {
						intersected = new java.awt.Rectangle((int)t.getX(), 
								(int)(t.getY()- textSize[1]), 
								textSize[0], textSize[1]).intersects(x, y, w, h);
					}
				}
				if (intersected) {
					_selShapes.add(d);
					_selShapeIndexes.add(i);
				}
				i++;
			}
			
			response(new AuSetAttribute(this, "selDrawables", 
					JSONArray.toJSONString(_selShapes)));
			data.clear();
			data.put("selShapes", _selShapes);
			data.put("selShapeIndexes", _selShapeIndexes);
			Events.postEvent(new Event(name, this, data));
		} else if ("onStarMoveShape".equals(name)) {
			List<Drawable> drawables = new ArrayList<Drawable> (canvas.getAllDrawables());
			canvas.clear();
			for (int i = 0, j = drawables.size(); i < j; i++) {
				if (_selShapeIndexes.contains(i)) continue;
				canvas.add(drawables.get(i));
			}
		} else if ("onEndMoveShape".equals(name)) {
			double dx = getDouble(data, "dx");
			double dy = getDouble(data, "dy");
			
			Iterator<Drawable> drawablesIt = 
				new ArrayList<Drawable>(canvas.getAllDrawables()).iterator();
			Iterator<Drawable> selShapeIt = _selShapes.iterator();
			canvas.clear();
			_selShapes = new ArrayList<Drawable>();
			
			int i = 0;
			while (drawablesIt.hasNext() || selShapeIt.hasNext()) {
				Drawable d;
				if (_selShapeIndexes.contains(i++))
					_selShapes.add(d = updateShape(selShapeIt.next(), dx, dy));
				else
					d = drawablesIt.next();
				
				canvas.add(d);
			}
			response(new AuSetAttribute(this, "selDrawables", 
					JSONArray.toJSONString(_selShapes)));
		} else
			super.service(request, everError);
	}
	
	private Drawable updateShape(Drawable d, double dx, double dy) {
		
		if (d instanceof Rectangle) {
			Shape shape = (Shape) d;
			java.awt.Rectangle bounds = shape.getBounds();
			double w = bounds.getWidth();
			double h = bounds.getHeight();
			((Rectangle)d).setRect(bounds.getX() + dx, bounds.getY() + dy, w, h);
		} else if (d instanceof Text) {
			Text t = (Text)d;
			t.setPosition(t.getX() + dx, t.getY() + dy);
		} else {
			d = ((Path)d).transform(new AffineTransform(1, 0, 0, 
					1, dx, dy));
		}
		return d;
	}
	
	public List<Drawable> getSelectedDrawables() {
		return _selShapes;
	}
	
	public void addDrawable(Drawable d) {
		canvas.add(d);
	}
	
	public Drawable removeDrawable(int index) {
		Drawable d = canvas.remove(index);
		
		if (d instanceof Text)
			_textSizeMap.remove(d);
		return d;
	}
	
	public void clearDrawable() {
		canvas.clear();
	}
	
	public List<Drawable> getAllDrawables() {
		return canvas.getAllDrawables();
	}
	
	@Override
	protected void renderProperties(ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);
		
		if (strokeColor != null)
			renderer.render("strokeColor", strokeColor);
		
		if (fillColor != null)
			renderer.render("fillColor", fillColor);
		
		if (drawingType != null)
			renderer.render("drawingType", drawingType);
		
		if (alpha != 0)
			renderer.render("alpha", alpha);
		
		if (text != null)
			renderer.render("text", text);
		
		if (font != null)
			renderer.render("font", font);
		
		if (shapeCategory != null)
			renderer.render("shapeCategory", JSONValue.toJSONString(shapeCategory));
		
		if (shapeIndex != 0)
			renderer.render("shapeIndex", shapeIndex);
		
		if ((arrowWidth.intValue() != 3) ||
			(tipWidth.intValue() != 10)||
			(tipLength.intValue() != 15)) {
			
			
			JSONObject o = new JSONObject();
			o.put("arrowWidth", arrowWidth);
			o.put("tipWidth", tipWidth);
			o.put("tipLength", tipLength);
			renderer.render("arrowAttrs",  o);
		}
		
	}
	
	private void setDrawingState(DrawingStyle ds) {
		ds.setDrawingType(getDrawingType());
		ds.setStrokeStyle(getStrokeColor());
		ds.setFillStyle(getFillColor());
		ds.setAlpha(getAlpha());
	}
	
	public static final double getDouble(Map data, String key) {
		Object val = data.get(key);
		return val == null ? 0: ((Number) val).doubleValue();
	}
	
	public static final int getInt(Map data, String key) {
		Object val = data.get(key);
		return val == null ? 0: ((Number) val).intValue();
	}

}
