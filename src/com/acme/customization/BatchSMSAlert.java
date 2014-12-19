package com.acme.customization;

import com.lbs.batch.BatchOperationBase;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


















import com.lbs.batch.BatchSuspensionResult;
import com.lbs.batch.IBatchSuspendable;
import com.lbs.batch.IBatchTerminatable;
import com.lbs.batch.classes.BatchSuspensionDataBase;
import com.lbs.batch.classes.ServerBatchUtil;
import com.lbs.data.database.cache.DBPreparedStatementCache;
import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.objects.CustomBusinessObjects;
import com.acme.customization.cbo.CEOAlertInfo;
import com.acme.customization.cbo.CEOSMSObject;
import com.acme.customization.client.MessageSplitControl;
import com.acme.customization.shared.ProjectGlobals;
import com.acme.customization.shared.ProjectUtil;
import com.acme.customization.ws.maradit.Maradit;
import com.acme.customization.ws.maradit.SubmitResponse;

public class BatchSMSAlert extends BatchOperationBase implements IBatchTerminatable, IBatchSuspendable, Serializable{
	
	private ArrayList m_SMSObjectList = new ArrayList();
	private ArrayList m_UsersRefList = new ArrayList();
	private CustomBusinessObject m_SMSAlertObj = new CustomBusinessObject();
	private CEOAlertInfo m_AlertInfo = new CEOAlertInfo();
	private Vector m_LogList = new Vector();
	private int operationID = ProjectGlobals.OPERTYPE_SMSALERT;
	private int recStartCount = 0;
	private int totalCount = 0;
	private static final long serialVersionUID = 1L;
	private ServerBatchUtil batchUtil = new ServerBatchUtil();
	boolean okay = true;
	
	public int setParams(CustomBusinessObject smsAlertObj)
	{
		try {
			m_SMSAlertObj = smsAlertObj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProjectUtil.setAlertInfoPropFromCBO(m_AlertInfo, m_SMSAlertObj);
		return STATUS_COMPLETED;
	}
	
	public int run()
	{
		try
		{
			DBPreparedStatementCache.STATEMENT_CACHE = false;
			//insert into batch tables
			setBatchOperationID(operationID);
			m_UsersRefList = m_AlertInfo.getUsersRefList();
			okay =  m_UsersRefList.size() > 0;

			if (okay)
				sendSMSRoutine(batchUtil);
		}
		catch (Exception e)
		{
			m_ServerContext.getLogger().error("Exception: ", e);
			updateStatus(STATUS_ERROR, "Exception :" + e);
			return STATUS_ERROR;
		}

		finalizeBatch();

		return STATUS_COMPLETED;
	} //END of run()
	
	private void sendSMSRoutine(ServerBatchUtil util) throws Exception
	{
			Maradit maradit = new Maradit(m_AlertInfo.getUserName(), m_AlertInfo.getPassword());
        	maradit.validityPeriod = 120;
        	//maradit.from =  ProjectUtil.getCompanyName(m_ServerContext);
			for (int i = recStartCount; i < m_UsersRefList.size(); i++)
			{
				totalCount++;
				recStartCount++;
				Integer userRef = (Integer) m_UsersRefList.get(i);
				CustomBusinessObject user = null;
				if (userRef.intValue() > 0)
				{
					user = ProjectUtil.readObject(m_ServerContext, "CBOMblInfoUser", userRef);
					ProjectUtil.setUserInfo(m_ServerContext, user);
				}
				else
				{
					CustomBusinessObjects users = (CustomBusinessObjects) ProjectUtil.getMemberValue(m_SMSAlertObj, "AlertUsers");
					user = (CustomBusinessObject) users.get(i);
				}
				
				if (user != null)
				{
					String phoneNumber = ProjectUtil.getBOStringFieldValue(user, "Phonenumber");
					String title = ProjectUtil.getBOStringFieldValue(user, "Title");
					String message = prepareMessage(user);
					
					ArrayList ToList = new ArrayList<String>();
					ToList.add(phoneNumber);
					SubmitResponse response = maradit.submit(ToList, message);
					if (response.statusCode != 200 || !response.status) {
						CEOSMSObject logRec = new CEOSMSObject();
						logRec.setPhoneNumber(phoneNumber);
						logRec.setTitle(title);
						logRec.setError(response.error);
						logRec.setStatusDesc(response.statusDescription);
						m_LogList.add(logRec);
					}
				}
			}


	} //END of sendSMSRoutine()
	
	private String prepareMessage(CustomBusinessObject user)
	{
		String messageMain = m_AlertInfo.getMainMessage();
		String message=null;
		if (!messageMain.isEmpty()) {
			
				if (ProjectUtil.getBOStringFieldValue(user, "Phonenumber")
						.length() == 0)
					return message;
				message = messageMain;
				  
				  if(message.contains("P10"))
					{
						if(ProjectUtil.getMemberValue(user,
								"PersonName")!=null)
						message=message.replace("P10",(String) ProjectUtil.getMemberValue(user,
								"PersonName"));
						else 
							message= message.replace("P10","");
					}
					
					if(message.contains("P11"))
					{
						if(ProjectUtil.getMemberValue(user,
								"PersonSurName")!=null)
						message=message.replace("P11",(String) ProjectUtil.getMemberValue(user,
								"PersonSurName"));
						else 
							message= message.replace("P11","");
					}
				
				 if(message.contains("P1"))
					{
						if(ProjectUtil.getMemberValue(user,
								"Name")!=null)
		              message= message.replace("P1",(String) ProjectUtil.getMemberValue(user,
									"Name"));	
						else 
							message= message.replace("P1","");
					}
					
					if(message.contains("P2"))
					{
						if(ProjectUtil.getMemberValue(user,
								"SurName")!=null)
						message=message.replace("P2",(String) ProjectUtil.getMemberValue(user,
									"SurName"));
						else 
							message= message.replace("P2","");
					}
					
					if(message.contains("P3"))
					{
						if(ProjectUtil.getMemberValue(user,
								"Phonenumber")!=null)
						message=message.replace("P3",(String) ProjectUtil.getMemberValue(user,
									"Phonenumber"));
						else 
							message= message.replace("P3","");
					}
					
					if(message.contains("P4"))
					{
						message=message.replace("P4",MessageSplitControl.returnDate());
					}
					
					if(message.contains("P5"))
					{
						message=message.replace("P5",MessageSplitControl.returnTime());
					}
					
					if(message.contains("P6"))
					{
						if(ProjectUtil.getMemberValue(user,
								"ArpCode")!=null)
						message=message.replace("P6",(String) ProjectUtil.getMemberValue(user,
								"ArpCode"));
						else 
							message= message.replace("P6","");
					}
					
					if(message.contains("P7"))
					{
						if(ProjectUtil.getMemberValue(user,
								"ArpTitle")!=null)
						message=message.replace("P7",(String) ProjectUtil.getMemberValue(user,
								"ArpTitle"));
						else 
							message= message.replace("P7","");
					}
			
					if(message.contains("P8"))
					{
						if(ProjectUtil.getMemberValue(user,
								"ArpBalance")!=null)
						message=message.replace("P8",((BigDecimal) ProjectUtil.getMemberValue(user,
								"ArpBalance")).toString());
						else 
							message= message.replace("P8","");
					}
					
					if(message.contains("P9"))
					{
						if(ProjectUtil.getMemberValue(user,
								"PersonCode")!=null)
						message=message.replace("P9",((String) ProjectUtil.getMemberValue(user,
								"PersonCode")).toString());
						else 
							message= message.replace("P9","");
					}
					
		     	}
			

		return message;
	}
	
	
	public Vector getBatchLog(Vector logList, Object[] paramList)
	{
		return getBatchLog(logList, paramList, 0, 0, 0);
	}

	
	@Override
	public int resume(Object suspensionData) {
		// TODO Auto-generated method stub
		if (suspensionData instanceof BatchSMSAlertSuspData)
		{
			try
			{
				copyDataToBatch(suspensionData);
				batchUtil = new ServerBatchUtil();
				okay = true;
				sendSMSRoutine(batchUtil);
			}
			catch (Exception e)
			{
				m_ServerContext.getLogger().error("Resume to the batch failed: ", e);
			}
		}
		return finalizeBatch();
	}
	
	public int finalizeBatch()
	{

		Vector logList = getBatchLog(m_LogList, new Object[] { "PhoneNumber", "Title", "Error", "StatusDesc" });
		insertBatchExceptions(logList, 0, 1);
		updateBatchRecCounts(totalCount, recStartCount);

		return STATUS_COMPLETED;
	}
	
	private HashMap copyDataToSuspension()
	{
		HashMap values = new HashMap();
		/*Main Vars*/
		values.put("operationID", Integer.valueOf(operationID));
		values.put("m_SMSAlertObj", m_SMSAlertObj);
		values.put("m_UsersRefList", m_UsersRefList);
		values.put("m_AlertInfo", m_AlertInfo);
		values.put("m_LogList", m_LogList);
		values.put("totalCount", Integer.valueOf(totalCount));
		values.put("recStartCount", Integer.valueOf(recStartCount));
		values.put("okay", new Boolean(okay));
		return values;
	}

	private void copyDataToBatch(Object suspensionData)
	{
		BatchSMSAlertSuspData batchData = ((BatchSMSAlertSuspData) suspensionData);
		HashMap values = batchData.suspHashMap;
		this.operationID = ((Integer) values.get("operationID")).intValue();
		this.totalCount = ((Integer) values.get("totalCount")).intValue();
		this.recStartCount = ((Integer) values.get("recStartCount")).intValue();
		this.m_SMSAlertObj = (CustomBusinessObject) values.get("m_SMSAlertObj");
		this.m_AlertInfo = (CEOAlertInfo) values.get("m_AlertInfo");
		this.m_LogList = (Vector) values.get("m_LogList");
		this.m_UsersRefList = (ArrayList)values.get("m_UsersRefList");
		this.okay = ((Boolean) values.get("okay")).booleanValue();
	}


	@Override
	public BatchSuspensionResult suspend() {
		return new BatchSuspensionResult(true, new BatchSMSAlertSuspData(copyDataToSuspension()));
	}

	@Override
	public boolean terminate() {
		return true;
	}

}

class BatchSMSAlertSuspData extends BatchSuspensionDataBase
{
	private static final long serialVersionUID = 1L;
	protected HashMap suspHashMap = new HashMap();

	public BatchSMSAlertSuspData(HashMap values)
	{
		super();
		suspHashMap = values;
	}
}
