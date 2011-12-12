package org.zkoss.canvas.zkpaint2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.canvas.DrawingStyle;
import org.zkoss.canvas.drawable.Drawable;
import org.zkoss.canvas.drawable.Path;
import org.zkoss.canvas.drawable.Rectangle;
import org.zkoss.canvas.drawable.Text;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;

public class ShapeUtils {

	private static final String MOVE_TO = "mv";
	private static final String LINE_TO = "ln";
	private static final String QUAD_TO = "qd";
	private static final String BEZIER_TO = "bz";
	private static final String CLOSE = "cl";
	
	private static final String TYPE_KEY = "tp";
	private static final String DATA_KEY = "dt";
	public static final String TEXT_TYPE = "canvas.Text";
	public static final String PATH_TYPE = "canvas.Path";
	public static final String RECTANGLE_TYPE = "canvas.Rectangle";
	
	public static final List<Drawable> stringToDrawables (String content) {
		Object o = JSONValue.parse(content);
		
		if (o instanceof JSONArray)
			return jsonAryToDrawables((JSONArray) o);
		return null;
	}
	
	public static final List<Drawable> jsonAryToDrawables (JSONArray jary) {
		List<Drawable> results = new ArrayList<Drawable>(jary.size());
		
		for (Object o : jary) {
			if (o instanceof JSONObject) {
				JSONObject json = (JSONObject) o;
				String type = String.valueOf(json.get("objtp"));
				
				if (TEXT_TYPE.equals(type)) {
					results.add(jsonToTextObject(json));
				} else if (PATH_TYPE.equals(type)) {
					results.add(jsonToPath(json));
				} else if (RECTANGLE_TYPE.equals(type)) {
					results.add(jsonToRectangle(json));
				}
				
			}
			
		}
		return results;
	}
	
	public static final Text stringToTextObject(String jsonString) {
		return jsonToTextObject((Map) JSONValue.parse(jsonString));
	}
	
	public static final Text jsonToTextObject(Map jsonData) {
		
		String text = "";
		double x = 0;
		double y = 0;
		
		Map obj = (Map) jsonData.get("obj");
		
		if (obj != null) {
			text = getString(obj, "t");
			x = getDouble(obj, "x");
			y = getDouble(obj, "y");
		}
		
		Text txt = new Text(text, x ,y);
		
		Map state = (Map) jsonData.get("state");
		if (state != null)  {
			Object fnt = state.get("fnt");
			if (fnt != null)
				txt.getDrawingStyle().setFont(fnt.toString());
			setDrawingState(txt.getDrawingStyle(), state);
		}

		return txt;
	}

	public static final Path stringToPath(String jsonString) {
		return jsonToPath((Map) JSONValue.parse(jsonString));
	}
	
	public static final Path jsonToPath(Map jsonData) {
		Path p = new Path();
		
		List sg = getSegment(jsonData);
		if (sg != null) {
			for (int i = 0, j = sg.size(); i < j; i++) {
				Map json = (Map) sg.get(i);
				
				String type = json.get(TYPE_KEY).toString();
				List dt = (List) json.get(DATA_KEY);
				
				if (MOVE_TO.equals(type)) {
					p.moveTo(getDouble(dt, 0), getDouble(dt, 1));
				} else if (LINE_TO.equals(type)) {
					p.lineTo(getDouble(dt, 0), getDouble(dt, 1));
				} else if (QUAD_TO.equals(type)) {
					p.quadTo(getDouble(dt, 0), getDouble(dt, 1), 
						getDouble(dt, 2), getDouble(dt, 3));
				} else if (BEZIER_TO.equals(type)) {
					p.curveTo(getDouble(dt, 0), getDouble(dt, 1), getDouble(dt, 2), 
						getDouble(dt, 3), getDouble(dt, 4), getDouble(dt, 5));
				} else if (CLOSE.equals(type)) {
					p.closePath();
				} 
			}
		}
		
		Map state = (Map) jsonData.get("state");
		if (state != null) 
			setDrawingState(p.getDrawingStyle(), state);
		return p;
	}

	public static final Rectangle stringToRectangle(String jsonString) {
		return jsonToRectangle((Map) JSONValue.parse(jsonString));
	}
	
	public static final Rectangle jsonToRectangle(Map jsonData) {
		double x = 0;
		double y = 0;
		double w = 0;
		double h = 0;
		Map obj = (Map) jsonData.get("obj");
		
		if (obj != null) {
			x = getDouble(obj, "x");
			y = getDouble(obj, "y");
			w = getDouble(obj, "w");
			h = getDouble(obj, "h");
		}
		Rectangle r = new Rectangle(x, y, w, h);
		
		Map state = (Map) jsonData.get("state");
		if (state != null) 
			setDrawingState(r.getDrawingStyle(), state);
		
		return r;
	}
	
	private static List getSegment(Map data) {
		Map obj = (Map) data.get("obj");
		if (obj != null) 
			return (List) obj.get("sg");
		return null;
	}
	
	private static final void setDrawingState(DrawingStyle ds, Map state) {
		ds.setDrawingType(getString(state, "dwtp"));
		ds.setStrokeStyle(getString(state, "strk"));
		ds.setFillStyle(getString(state, "fil"));
		ds.setAlpha(getInt(state, "alfa"));
	}
	
	public static final double getDouble(Map data, String key) {
		Object val = data.get(key);
		return val == null ? 0: ((Number) val).doubleValue();
	}
	
	public static final int getInt(Map data, String key) {
		Object val = data.get(key);
		return val == null ? 0: ((Number) val).intValue();
	}
	
	public static final String getString(Map data, String key) {
		Object val = data.get(key);
		return val == null ? "": val.toString();
	}
	
	public static final double getDouble(List data, int index) {
		Object val = data.get(index);
		return val == null ? 0: ((Number) val).doubleValue();
	}

}
