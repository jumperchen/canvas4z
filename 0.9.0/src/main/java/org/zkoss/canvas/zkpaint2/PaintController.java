package org.zkoss.canvas.zkpaint2;

import java.awt.geom.Arc2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.canvas.drawable.Drawable;
import org.zkoss.canvas.drawable.Path;
import org.zkoss.canvas.drawable.Rectangle;
import org.zkoss.canvas.drawable.Shape;
import org.zkoss.canvas.drawable.Text;
import org.zkoss.canvas.util.Shapes;
import org.zkoss.json.JSONValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.Colorbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class PaintController extends GenericForwardComposer {
	
	private Listbox shapeBox;
	private Listbox strokeTypeBox;
	private Listbox fillTypeBox;
	private Colorbox strokeColorBox;
	private Colorbox fillColorBox;
	//private Slider alphaSlider;
	private double alpha;
	
	private Textbox textBox;
	private Listbox fontBox;
	private Intbox fontSizeBox;
	
	private Window shapeListWindow;
	
	private PaintDiv paintDiv;
	
	private List<Shape> _shapes;
	private List<String> _shapeNames;
	private ArrayList<List<Integer>> pathInfos;
	private List<Integer> arrowInfo;
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		constructShapes();
		
		paintDiv.setShapeCategory(_shapes);
		
		for(int i = 0; i < _shapeNames.size(); i++)
			shapeBox.appendItem(_shapeNames.get(i), ""+i);
		shapeBox.setSelectedIndex(0);
		
	}
	
	private void constructShapes() {
		_shapes = new ArrayList<Shape>();
		_shapeNames = new ArrayList<String>();

		_shapeNames.add("Rectangle");
		_shapes.add(new Rectangle(0, 0, 1000, 1000));

		_shapeNames.add("Line");
		_shapes.add(new Path().moveTo(0, 0).lineTo(1000, 1000).closePath());

		_shapeNames.add("Triangle");
		_shapes.add(new Path().moveTo(0, 0).lineTo(0, 1000).lineTo(1000, 500)
				.lineTo(0, 0).closePath());

		_shapeNames.add("Circle");
		_shapes.add(new Path().append(new Arc2D.Double(0, 0, 1000, 1000, 0,
				360, Arc2D.CHORD), false));

		_shapeNames.add("Hexagon");
		_shapes.add(Shapes.nGon(500, 6));

		_shapeNames.add("Star");
		_shapes.add(Shapes.nStar(500, 5, 43.5));

		_shapeNames.add("Heart");
		_shapes.add(Shapes.heart(1000));
		
		pathInfos = new ArrayList<List<Integer>>();
		for (Shape s : _shapes) {
			if (!(s instanceof Path)) continue;
			pathInfos.add(getSg((Path) s));
		}
		//arrow
		arrowInfo = getSg(new Path().moveTo(0, 0).lineTo(0, 0).lineTo(0, 0)
			.lineTo(0, 0).lineTo(0, 0).lineTo(0, 0)
			.lineTo(0, 0).lineTo(0, 0)
			.lineTo(0, 0).closePath());
		
	}
	
	private List<Integer> getSg(Path p) {
		PathIterator pi = p.getPathIterator(null);
		float[] coords = new float[6];
		List<Integer> sg = new ArrayList<Integer>();
		while (!pi.isDone()) {
			sg.add(pi.currentSegment(coords));
			pi.next();
		}
		return sg;
	}

	public void onAddText$paintDiv(ForwardEvent event) {
		Map data = (Map) event.getOrigin().getData();
		paintDiv.addDrawable((Text) data.get("text"));
		addToList("Text");
	}
	
	public void onAddShape$paintDiv(ForwardEvent event) {
		Map data = (Map) event.getOrigin().getData();
		Shape s = (Shape) data.get("shape");
		paintDiv.addDrawable((Drawable) s);
		addToList(_shapeNames.get(paintDiv.getShapeIndex()));
	}
	
	public void addToList(String name) {
		Events.postEvent(new Event("onAddShapes", shapeListWindow, name));
	}
	
	public void onAddArrow$paintDiv(ForwardEvent event) {
		Map data = (Map) event.getOrigin().getData();
		Shape s = (Shape) data.get("arrow");
		paintDiv.addDrawable((Drawable) s);
		addToList("arrow");
	}
	
	public void onSelect$paintDiv(ForwardEvent event) {
		Map data = (Map) event.getOrigin().getData();
		List<Integer> _selShapeIndexes = (List<Integer>) data.get("selShapeIndexes");
		
		Events.postEvent(new Event("onSelectShapes", shapeListWindow, 
				_selShapeIndexes.toArray()));
	}
	
	public void onReomveShapes(Event event) {
		Object[] selIndexes = (Object[]) event.getData();
		for (int i = 0, j = selIndexes.length; i < j; i++) {
			Integer index = (Integer) selIndexes[i];
			paintDiv.removeDrawable(index);
		}
	}
	
	public void onSave() {
		Filedownload.save(JSONValue.toJSONString(paintDiv.getAllDrawables()), null, "graph.txt");
	}
	
	public void onLoad(ForwardEvent event) {
		UploadEvent uploadEvt = (UploadEvent)event.getOrigin();
		
		String content = uploadEvt.getMedia().getStringData();
		
		for (Drawable d : ShapeUtils.stringToDrawables(content)) {
			
			paintDiv.addDrawable(d);
			if (d instanceof Text) {
				addToList("Text");
			} else if (d instanceof Rectangle) {
				addToList("Rectangle");
			} else if (d instanceof Path) {
				addToList(getPathName((Path) d));
			}
		}
	}

	private String getPathName(Path targetPath) {
		boolean found = false;
		
		List<Integer> tSg = getSg(targetPath);
		if (arrowInfo.equals(tSg))
			return "arrow";
		
		int i = 1;
		for (Iterator it = pathInfos.iterator(); it.hasNext(); i++) {
			List<Integer> info = (List<Integer>) it.next();
			if (info.equals(tSg))
				break;
		}
		//Circle or Heart
		if (i == 3) {
			PathIterator pi = targetPath.getPathIterator(null);
			float[] coords = new float[6];
			
			float c0 = pi.currentSegment(coords);
			pi.next();
			float c1 = pi.currentSegment(coords);
			
			i = c0 == c1 ? 3: 6;
		}
		

		return _shapeNames.get(i);
	}
	
}
