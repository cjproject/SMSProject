package com.acme.customization.forms;

import java.util.ArrayList;

import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.lbs.appobjects.GOBOUser;
import com.lbs.customization.report.customize.online.IOnlineReportingOperations;
import com.lbs.data.factory.IObjectFactory;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.lbs.data.query.IQueryFactory;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryObjectIdentifier;
import com.lbs.data.query.QueryParams;
import com.lbs.grid.interfaces.ILbsQueryGrid;
import com.lbs.grid.interfaces.IMultiSelectionList;
import com.lbs.util.IObjectCopyListener;
import com.lbs.util.QueryUtil;
import com.lbs.util.StringUtil;
import com.lbs.xui.JLbsXUILookupInfo;
import com.lbs.xui.JLbsXUITypes;
import com.lbs.xui.customization.JLbsXUIControlEvent;

public class CXEARPCardBrowser {

	public CXEARPCardBrowser() {
		// TODO Auto-generated constructor stub
	}

	public void onPopupMenuAction(JLbsXUIControlEvent event)
	{
		/** onPopupMenuAction : This method is called when user selects any item in the form's popup menu. Event parameter object (JLbsXUIControlEvent) contains form object in 'container' and 'component' properties, form data object in 'data' property, selected popup item's id value in 'index' and 'tag' properties, and selected popup item object (JLbsPopupMenuItem) in 'ctxData' property. This method is expected to execute the action corresponding to the selected popup menu item. No return value is expected. */
		 if(event.getIndex() == 60)
		 {
			 	ILbsQueryGrid grid = (ILbsQueryGrid) event.getContainer().getComponentByTag(100);
				IMultiSelectionList list = grid.getMultiSelectionList();
				int errCnt = 0;
				ArrayList arpRefList = new ArrayList();

				if (list != null && list.size() > 0)
				{
					int listSize = list.size();
					Integer arpRef;
					if (listSize == 1)
					{
						arpRef = QueryUtil.getIntegerProp((QueryBusinessObject) grid.getSelectedObject(), "Reference");
						arpRefList.add(arpRef);
					}
					else
					{
						for(int i=0; i<listSize;i++)
						{
							QueryObjectIdentifier id = (QueryObjectIdentifier) list.get(i);
							arpRef = id.getSimpleKey();
							arpRefList.add(arpRef);
						}
					}
					
				}
				CustomBusinessObject alert = ProjectUtil.createNewCBO("CBOSMSAlert");
				CustomBusinessObjects userList = ProjectUtil.getUserListWithArpInfo(event.getClientContext(), arpRefList);
				for (int i = 0; i < userList.size(); i++)
				{
					CustomBusinessObject user = (CustomBusinessObject) userList.get(i);
					ProjectUtil.setMemberValueUn(user, "UserType",  ProjectGlobals.USER_TYPE_ARP);
					ProjectUtil.setMemberValueUn(user, "CardReference",  ProjectUtil.getBOIntFieldValue(user, "ArpRef"));
					ProjectUtil.setMemberValueUn(user, "Name",  ProjectUtil.getBOStringFieldValue(user, "ArpTitle"));
					ProjectUtil.setMemberValueUn(user, "Title",  ProjectUtil.getBOStringFieldValue(user, "ArpTitle"));
					ProjectUtil.setMemberValueUn(user, "Tckno",  ProjectUtil.getBOStringFieldValue(user, "ArpIDTCNo"));
					ProjectUtil.setMemberValueUn(user, "Phonenumber",  ProjectUtil.getBOStringFieldValue(user, "ArpMobilePhone"));
					ProjectUtil.setMemberValueUn(user, "UserRef",  0);
					
				}
				ProjectUtil.setMemberValueUn(alert, "AlertUsers", userList);
				event.getContainer().openChild("Forms/CXFSendSMS.lfrm", alert, true, JLbsXUITypes.XUIMODE_DEFAULT);
							 
		 }
	}

}
