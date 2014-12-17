package com.acme.customization.forms;

import java.util.ArrayList;

import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.lbs.data.grids.JLbsQuerySelectionGrid;
import com.lbs.data.query.QueryBusinessObject;
import com.lbs.data.query.QueryParams;
import com.lbs.data.query.QueryResult;
import com.lbs.filter.JLbsFilterBase;
import com.lbs.filter.JLbsFilterDateRange;
import com.lbs.filter.JLbsFilterList;
import com.lbs.filter.grid.JLbsFilterEditGrid;
import com.lbs.localization.LbsLocalizableException;
import com.lbs.remoteclient.IClientContext;
import com.lbs.xui.ILbsXUIPane;
import com.lbs.xui.customization.JLbsXUIControlEvent;
import com.lbs.xui.customization.JLbsXUIDataGridEvent;

public class CXESMSBatchOperationBrowser{

	public void onInitialize(JLbsXUIControlEvent event)
	{
		/** onInitialize : This is the initialization method for XUI forms. The method is called when the form and its components are created and ready to display. Event parameter object (JLbsXUIControlEvent) contains the form object (JLbsXUIPane) in 'component' and 'container' properties, and form's data in 'data' property. This method is meant to be void (no return value is expected). */
		JLbsQuerySelectionGrid grid = (JLbsQuerySelectionGrid) event.getContainer().getComponentByTag(100);
		JLbsFilterEditGrid filterGrid = (JLbsFilterEditGrid) event.getContainer().getComponentByTag(200);
		JLbsFilterList filtList = filterGrid.getFilterList();
		JLbsFilterBase filter = (JLbsFilterBase) filtList.getItemById(205);
		if (filter instanceof JLbsFilterDateRange)
		{
			((JLbsFilterDateRange) filter).setGrouped(false);
			((JLbsFilterDateRange) filter).setValue(null);
			filter.writeForcedModified(false);
			event.getContainer().applyFiltersToQueryGrid(grid, filtList);
		}
	}
	
	public boolean deleteBatch(ILbsXUIPane container, Object data, IClientContext context) throws LbsLocalizableException
	{
		//then you should delete batch exceptions..
		QueryParams params = new QueryParams();
		params.setCustomization(ProjectGlobals.getM_ProjectGUID());
		ArrayList batchRefList = new ArrayList();
		Integer batchID = Integer.valueOf(getBatchID(container));
		batchRefList.add(batchID);
		params.getVariables().put("V_BATCHREFS", batchRefList);
		try {
			context.getQueryFactory().executeServiceQuery("CQODeleteSMSBatchExceptions", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//then delete batches..
		params = new QueryParams();
		params.setCustomization(ProjectGlobals.getM_ProjectGUID());
		params.getParameters().put("P_OPERATIONID", ProjectGlobals.OPERTYPE_SMSALERT);
		params.getEnabledTerms().enable("T2");
		params.getParameters().put("P_LOGICALREF", batchID);
		try {
			context.getQueryFactory().executeServiceQuery("CQODeleteSMSBatches", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//delete alert batch table..
		params = new QueryParams();
		params.setCustomization(ProjectGlobals.getM_ProjectGUID());
		params.getEnabledTerms().disable("T1");
		params.getEnabledTerms().enable("T2");
		params.getParameters().put("P_BATCHID", batchID);
		try {
			context.getQueryFactory().executeServiceQuery("CQODeleteSMSAlertBatch", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JLbsQuerySelectionGrid grid = (JLbsQuerySelectionGrid) container.getComponentByTag(100);
		grid.doRefreshListing();
		return true;
	}
	
	protected int getBatchID(ILbsXUIPane container)
	{
		Object obj = container.getSelectedGridData(100);
		if (obj instanceof QueryBusinessObject)
		{
			QueryBusinessObject rowObj = (QueryBusinessObject) obj;
			try
			{
				return rowObj.getProperties().getInt("BatchID");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return -1;
	}


	public void onGridModifyQuery(JLbsXUIDataGridEvent event)
	{
		/** onGridModifyQuery : This method is called before query execution to let the user modify the query parameters. Event parameter object (JLbsXUIDataGridEvent) contains form data object in 'data' property, grid component in 'grid' property, and form's mode in 'index' property. This method is supposed to modify the QueryParams object stored in the query grid component. No return value is expected. */
		
	}
	
	/*public boolean viewSMSSendingForm(ILbsXUIPane container, Object data, IClientContext context) {
		
		JLbsQuerySelectionGrid grid = (JLbsQuerySelectionGrid) container.getComponentByTag(100);
		QueryBusinessObject selected = (QueryBusinessObject)grid.getSelectedObject();
		GOBOBatch batch = (GOBOBatch)UnityHelper.getBOByReference(context, GOBOBatch.class, QueryUtil.getIntProp(selected, "BatchID"));
		byte[] startPars = batch.getStartParameters();
		Object[] obj = null;
		CustomBusinessObject smsSendingObj = null;
		if (startPars != null && startPars.length > 0)
		{
			
			try {
				obj = TransportUtil.deserializeObjects(startPars);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (obj != null)
				for (int i = 0; i < obj.length; i++) {
					if (obj[i] instanceof CustomBusinessObject) {
						smsSendingObj = (CustomBusinessObject) obj[i];
					}
				}
		}
		if(smsSendingObj!=null)
		{
			boolean ok = container.openChild("Forms/BatchSMSSending.lfrm", smsSendingObj, true, JLbsXUITypes.XUIMODE_VIEWONLY);
			if (!ok)
				return true;
		}
		return true;
	}
*/

}
