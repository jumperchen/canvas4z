/**
 * 
 */
package org.zkoss.canvas.drawable;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.canvas.DrawingStyle;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

/**
 * @author Ashish
 *
 */
public class Drawables {

	public static Drawable getDrawable(JSONObject jo) {
		if(jo instanceof JSONObject) {
			// get object type
			String objType  = (String) jo.get("objtp");
			JSONObject jState = (JSONObject) jo.get("state");
			JSONObject jObj = (JSONObject) jo.get("obj");
			if(objType.equals(Rectangle.TYPE)) {
				// get rectanle state
				Rectangle rect = new Rectangle();
				rect.setRect((Double)jObj.get("x"), (Double)jObj.get("y"), (Double)jObj.get("w"), (Double)jObj.get("h"));
				Drawables.setDrawingStyle(rect.getDrawingStyle(), jState);
				return rect;
			} else if(objType.equals(Text.TYPE)) {
				// get Text state
				Text txt = new Text((String)jObj.get("t"), (Double)jObj.get("x"), (Double)jObj.get("y"));
				Drawables.setDrawingStyle(txt.getDrawingStyle(), jState);
				if(jState.get("fnt") != null) {
					txt.getDrawingStyle().setFont((String) jState.get("fnt"));
				}
				return txt;
			} else if(objType.equals(Path.TYPE)) {
				// get Path state
				// get the sg JSON array out first
				JSONArray jSg = (JSONArray) jObj.get("sg");
				//then perform all mv/ln/bz and cl ops as they encounter
				Path p = new Path();
				for(Object sgo: jSg) {
					JSONObject jSgo = (JSONObject) sgo;
					String sgType = (String) jSgo.get(Path.PathSegment.TYPE_KEY);
					JSONArray jDt = (JSONArray) jSgo.get(Path.PathSegment.DATA_KEY);
					if(Path.PathSegment.MOVE_TO.equals(sgType)) {
						p.moveTo((Double)jDt.get(0), (Double)jDt.get(1));
					} else if(Path.PathSegment.LINE_TO.equals(sgType)) {
						p.lineTo((Double)jDt.get(0), (Double)jDt.get(1));
					} else if(Path.PathSegment.CLOSE.equals(sgType)) {
						p.closePath();
					} else if(Path.PathSegment.BEZIER_TO.equals(sgType)) {
						p.curveTo((Double)jDt.get(0), (Double)jDt.get(1),
								  (Double)jDt.get(2), (Double)jDt.get(3),
								  (Double)jDt.get(4), (Double)jDt.get(5));
					}
				}
				Drawables.setDrawingStyle(p.getDrawingStyle(), jState);
				return p;
			}
		} 
		return null;
	}
	public static List<Drawable> getDrawables(JSONArray ja) {
		List<Drawable> drawables = new ArrayList<Drawable>(ja.size());
		for (Object o : ja) {
			if(o instanceof JSONObject) {
				drawables.add(Drawables.getDrawable((JSONObject)o));
			}
		}
		return drawables;
	}
	
	private static void setDrawingStyle(DrawingStyle ds,
			JSONObject jState) {
		String drawingType = (String) jState.get("dwtp");
		String storkeColor = (String) jState.get("strk");
		String fillColor = (String) jState.get("fil");
		Double alpha = (Double) jState.get("alfa");
		//double alpha = alphaSlider.getCurpos() / 100.0;
		//bug #3006313: getCurpos() does not work
		if(storkeColor != null) ds.setStrokeStyle(storkeColor);
		ds.setDrawingType(drawingType);
		if(fillColor != null) ds.setFillStyle(fillColor);
		if(alpha != null) ds.setAlpha(alpha);
	}
}
