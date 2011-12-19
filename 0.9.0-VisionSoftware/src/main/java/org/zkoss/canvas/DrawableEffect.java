/* DrawableEffect.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jun 28, 2011 6:45:59 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas;

import org.zkoss.json.JSONAware;

/**
 *
 * @author simonpai
 */
public interface DrawableEffect extends JSONAware {
	
	/**
	 * Returns the client side handler function of this effect.
	 */
	public String getHandler();
	
}
