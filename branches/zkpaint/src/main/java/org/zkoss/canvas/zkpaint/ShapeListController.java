/* ZKPaintController.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 20, 2010 11:44:33 AM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.zkpaint;

import org.zkoss.canvas.Canvas;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

/**
 * @author simon
 *
 */
public class ShapeListController extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	
	private Window zkpaintWindow;
	
	private Listbox shapeListBox;
	
	public void onClick$deleteBtn(Event event){
		Window canvasBoardWindow = (Window) zkpaintWindow
			.getFellow("canvasInc").getFellow("canvasBoardWindow");
		Canvas cvs1 = (Canvas) canvasBoardWindow.getFellow("cvs1");
		int size = shapeListBox.getItemCount();
		System.out.println(size);
		for(int i=size-1; i>-1; i--){
			if(!shapeListBox.getItemAtIndex(i).isSelected()) continue;
			System.out.println(i);
			cvs1.remove(i);
			((ListModelList) shapeListBox.getModel()).remove(i);
		}
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		zkpaintWindow = (Window) comp.getParent().getFellow("zkpaintWindow");
		
		
	}
	
}
