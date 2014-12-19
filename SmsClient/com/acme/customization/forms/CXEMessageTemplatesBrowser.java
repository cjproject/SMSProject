package com.acme.customization.forms;

import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXEMessageTemplatesBrowser extends JLbsCustomXUIEventListener {

	public CXEMessageTemplatesBrowser() {
		// TODO Auto-generated constructor stub
	}

	public void onPopupMenuFilter(JLbsXUIControlEvent event)
	{
		/** onPopupMenuFilter : This method is called to determine which popup menu items is filtered. Event parameter object (JLbsXUIControlEvent) contains form object in 'container' property,form data object in 'data' property, popup menu item's id in 'index' and 'tag' properties, and popup menu item object (JLbsPopupMenuItem) in 'ctxData' property. A boolean ('true' if item is visible) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'true'. */
		if (event.getIndex() == 20)
			event.setReturnObject(false);
	}

}
