/* EventTestController.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jul 3, 2011 8:56:47 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.test;

import org.zkoss.canvas.Canvas;
import org.zkoss.canvas.CanvasMouseEvent;
import org.zkoss.canvas.DrawingStyle;
import org.zkoss.canvas.drawable.Drawable;
import org.zkoss.canvas.drawable.Drawable.StyleSetter;
import org.zkoss.canvas.util.Shapes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

/**
 *
 * @author simonpai
 */
public class EventTestController extends GenericForwardComposer {
	
	private static final long serialVersionUID = -5879784409387272202L;
	
	private Div div;
	private Canvas cvs;
	private Popup popup;
	private Label lbi, lbo;
	
	public void onSelect$cvs(CanvasMouseEvent event) {
		Div d = new Div();
		d.appendChild(new Label(event.getPrevIndex() + " -> " + event.getIndex()));
		div.appendChild(d);
	}
	
	public void onTooltip$cvs(CanvasMouseEvent event) {
		Drawable d = event.getDrawable();
		if (d != null) {
			lbi.setValue("" + event.getIndex());
			lbo.setValue("" + event.getPrevIndex());
			popup.open(event.getPageX(), event.getPageY());
		} else 
			popup.close();
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		StyleSetter ss = new StyleSetter() {
			public void set(DrawingStyle s) {
				s.setDrawingType("both");
				s.setFillStyle("#888888");
			}
		};
		cvs.add(Shapes.heart(200).setStyle(ss));
		cvs.add(Shapes.nGon(50, 6).setStyle(ss));
	}
	
}
