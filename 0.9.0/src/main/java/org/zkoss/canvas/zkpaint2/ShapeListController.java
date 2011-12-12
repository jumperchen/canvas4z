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
package org.zkoss.canvas.zkpaint2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.canvas.Canvas;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

/**
 * @author simon
 * 
 */
public class ShapeListController extends GenericForwardComposer {

	private Window zkpaintWindow;

	private Listbox shapeListBox;
	private ListModelList shapeListModel;

	public void onClick$deleteBtn(Event event) {
		int size = shapeListBox.getItemCount();

		List<Integer> selectedIndexes = new ArrayList<Integer>();
		for (int i = size - 1; i > -1; i--) {
			if (!shapeListBox.getItemAtIndex(i).isSelected())
				continue;
			shapeListModel.remove(i);
			selectedIndexes.add(i);
		}
		Events.postEvent(new Event("onReomveShapes", zkpaintWindow,
				selectedIndexes.toArray()));
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		zkpaintWindow = (Window) comp.getParent().getFellow("zkpaintWindow");
		shapeListModel = new ListModelList();
		shapeListBox.setModel(shapeListModel);
	}

	public void onAddShapes(Event event) {
		shapeListModel.add(event.getData());
	}

	public void onSelectShapes(Event event) {
		Object[] selIndexes = (Object[]) event.getData();
		shapeListBox.clearSelection();
		for (int i = 0, j = selIndexes.length; i < j; i++) {
			Integer index = (Integer) selIndexes[i];
			shapeListBox.getItemAtIndex(index).setSelected(true);
		}
	}

	public void onClearShapes(Event event) {
		shapeListModel.clear();
	}

}
