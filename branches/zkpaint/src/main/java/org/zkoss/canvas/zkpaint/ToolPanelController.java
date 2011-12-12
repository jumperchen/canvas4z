/* ToolPanelController.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 20, 2010 6:29:36 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.zkpaint;

import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

/**
 * @author simon
 *
 */
public class ToolPanelController extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	
	// control
	private Listbox fontBox;
	private Listbox fillTypeBox;
	private Listbox strokeTypeBox;
	private Intbox fontSizeBox;
	private Textbox textBox;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// shape is set in CanvasBoardController
		
		// text
		fontBox.setSelectedIndex(0);
		
		// style
		fillTypeBox.setSelectedIndex(1);
		strokeTypeBox.setSelectedIndex(1);
		
		if(ServletFns.isExplorer()){
			fontBox.setDisabled(true);
			fontSizeBox.setDisabled(true);
			textBox.setDisabled(true);
		}
		
	}
}
