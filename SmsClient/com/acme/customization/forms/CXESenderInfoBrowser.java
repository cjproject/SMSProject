package com.acme.customization.forms;

import com.acme.customization.shared.ProjectUtil;
import com.lbs.data.query.QueryParams;
import com.lbs.xui.customization.JLbsXUIDataGridEvent;

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

}
