package com.acme.customization.forms;

import com.acme.customization.shared.ProjectUtil;
import com.lbs.data.query.QueryParams;
import com.lbs.xui.customization.JLbsXUIDataGridEvent;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXESenderInfoBrowser {

	public CXESenderInfoBrowser() {
		// TODO Auto-generated constructor stub
	}

	public void onGridModifyQuery(JLbsXUIDataGridEvent event)
	{
		/** onGridModifyQuery : This method is called before query execution to let the user modify the query parameters. Event parameter object (JLbsXUIDataGridEvent) contains form data object in 'data' property, grid component in 'grid' property, and form's mode in 'index' property. This method is supposed to modify the QueryParams object stored in the query grid component. No return value is expected. */
		
		QueryParams params = event.getQueryGrid().getQueryParams();
		params.getParameters().put("P_USERNR", ProjectUtil.getUserNr(event.getClientContext()));
	}

	public void onPopupMenuFilter(JLbsXUIControlEvent event)
	{
		/** onPopupMenuFilter : This method is called to determine which popup menu items is filtered. Event parameter object (JLbsXUIControlEvent) contains form object in 'container' property,form data object in 'data' property, popup menu item's id in 'index' and 'tag' properties, and popup menu item object (JLbsPopupMenuItem) in 'ctxData' property. A boolean ('true' if item is visible) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'true'. */
		if (event.getIndex() == 20)
			event.setReturnObject(false);
	}

}
