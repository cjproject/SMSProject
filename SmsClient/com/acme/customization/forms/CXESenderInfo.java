package com.acme.customization.forms;

import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.query.IQueryFactory;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryParams;
import com.lbs.util.QueryUtil;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.events.swing.JLbsCustomXUIEventListener;
import com.lbs.xui.customization.JLbsXUIControlEvent;


public class CXESenderInfo extends JLbsCustomXUIEventListener {
	

	public CXESenderInfo() {
		// TODO Auto-generated constructor stub
	}

	public void onSaveData(JLbsXUIControlEvent event)
	{
		/** onSaveData : This method is called before form data is saved to determine whether the form data can be saved or not. Event parameter object (JLbsXUIControlEvent) contains form object in 'container' and 'component' properties, and form data object in 'data' property. A boolean ('true' means form data can be saved) return value is expected. If no return value is specified or the return value is not of type boolean, default value is 'true'. */
		 
		ProjectUtil.setMemberValueUn((CustomBusinessObject)event.getData(), "UserNr", ProjectUtil.getUserNr(event.getClientContext()));
	}

	public void onInitialize(JLbsXUIControlEvent event)
	{
		boolean ok = false;
		QueryBusinessObjects results = new QueryBusinessObjects();
		QueryParams params = new QueryParams();
		params.setCustomization(ProjectGlobals.getM_ProjectGUID());
		IQueryFactory factory = (IQueryFactory) event.getClientContext().getQueryFactory();
		try
		{
			params.getEnabledTerms().enable("T_DEFAULT");
			params.getParameters().put("P_USERNR", ProjectUtil.getUserNr(event.getClientContext()));
			ok = factory.select("CQOGetSenderInfo", params, results, -1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ok = false;
		}
		
		if (ok)
		{
			boolean active = false;
			if (results.size() == 0)
				active = true;
			else 
			{
				QueryBusinessObject qbo = results.get(0);
				if (ProjectUtil.getBOIntFieldValue(
						((CustomBusinessObject) event.getData()),
						"LogicalReference") == QueryUtil.getIntProp(qbo,
						"LogicalRef"))
					active = true;
					
			}
			if (active)
				event.getContainer().setPermanentStateByTag(2000013,
						JLbsXUITypes.XUISTATE_ACTIVE); 
		}
	}


}
