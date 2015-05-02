/*****************************************************************
   Copyright 2013 by Hien Nguyen (hiennguyen@inetcloud.vn)

   Licensed under the iNet Solutions Corp.,;
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.inetcloud.vn/licenses

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 *****************************************************************/
package com.inet.xportal.calbuilder.wflbusiness;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Named;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.shiro.util.StringUtils;

import com.inet.workflow.web.model.GraphProcessModel;
import com.inet.workflow.web.model.TaskModel;
import com.inet.xportal.calbuilder.bo.CalDeptBO;
import com.inet.xportal.calbuilder.bo.CalElementBO;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.calbuilder.util.CalElementUtil;
import com.inet.xportal.itask.business.FirmTaskBusinessAbstraction;
import com.inet.xportal.itask.model.TaskHistory;
import com.inet.xportal.itask.model.TaskRequest;
import com.inet.xportal.itask.model.TaskUIForm;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * RequestAcceptedBusiness.
 * 
 * @author Hien Nguyen
 * @version $Id: RequestAcceptedBusiness.java Apr 27, 2015 5:19:08 PM nguyen_dv
 *          $
 * 
 * @since 1.0
 */
@Named("RequestAcceptedBusiness")
public class RequestAcceptedBusiness extends FirmTaskBusinessAbstraction {
	@Inject
	private CalElementBO elementBO;

	@Inject
	private CalDeptBO deptBO;
	/**
	 * 
	 * @param graphInfo
	 * @param taskModel
	 * @param requestInfo
	 * @param formUI
	 * @param historyInfo
	 */
	@Override
	protected void newTaskBiz(final GraphProcessModel graphInfo, 
			final TaskModel taskModel, 
			final TaskRequest requestInfo,
	        final TaskUIForm formUI, 
	        final TaskHistory historyInfo) {
		logger.debug("RequestAcceptedBusiness is comming: {}", requestInfo.getRequestData());
		// leave form data makeup
		if (requestInfo.getRequestData() != null) {
			if (!requestInfo.getRequestData().has("dept"))
			{
				logger.debug("There is no [dept]");
				return;
			}
			
			String deptID = requestInfo.getRequestData().getString("dept");
			logger.debug("Department {} sent to process.", deptID);
			if (!StringUtils.hasLength(deptID))
				return;
			
			// check department information
			final CalDept deptInf = deptBO.load(deptID);
			logger.debug("Department {} data load: {}", deptID, deptInf);
			if (deptInf == null)
				return;
			
			if (requestInfo.getRequestData().has("items"))
		    {
				Object element = requestInfo.getRequestData().get("items");
			    logger.debug("Items object: {}", element);
			    if (element != null)
			    {
			    	if (element instanceof JSONObject)
			    	{
			    		elementBuilder(deptInf, (JSONObject)element);
			    	}
			    	else if (element instanceof JSONArray)
			    	{
			    		int size = ((JSONArray)element).size();
						for (int index = 0; index <  size; index++)
						{
							elementBuilder(deptInf, ((JSONArray)element).getJSONObject(index));
						}
			    	}
			    }
		    }
			else
			{
				logger.debug("Build item: {}", requestInfo.getRequestData());
				elementBuilder(deptInf, requestInfo.getRequestData());
			}
		}
	}

	/**
	 * 
	 * @param deptInf
	 * @param json
	 */
	private void elementBuilder(final CalDept deptInf, final JSONObject json) {
		logger.debug("elementBuilder: {}", json);
		if (json.has("startTime") && 
			json.has("toTime") &&
			(json.has("day") || json.has("dateTime")) &&
			(json.has("subject") || json.has("summary"))) {
			
			final CalElement element = new CalElement();
			element.setDeptUUID(deptInf.getUuid());
			
			if (json.has("summary"))
				element.setSubject(json.getString("summary"));
			else
				element.setSubject(json.getString("subject"));
			
			// get time of request
			if (json.has("day"))
			{
				element.setDay(XParamUtils.getInteger(json.get("day")));
				if (json.has("year"))
					element.setYear(XParamUtils.getInteger(json.get("year")));
			} 
			else
			{
				long dateTime = XParamUtils.getLong(json.get("dateTime"));
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(dateTime);
				
				// update day and year f element request
				element.setDay(cal.get(Calendar.DAY_OF_YEAR));
				element.setYear(cal.get(Calendar.YEAR));
			}
			
			// get startTime and toTime
			element.setStartTime(timeParser(json.getString("startTime")));
			element.setToTime(timeParser(json.getString("toTime")));
			
			// chairman information
			if (json.has("chairmanName"))
				element.setChairmanName(json.getString("chairmanName"));
			
			if (json.has("chairmanCode"))
				element.setChairmanCode(json.getString("chairmanCode"));
			
			if (json.has("contentOwner"))
				element.setContentOwner(json.getString("contentOwner"));
			
			if (json.has("location"))
				element.setLocation(json.getString("location"));
			
			if (json.has("summary"))
				element.setSummary(json.getString("summary"));
			
			if (json.has("memberBrief"))
				element.setMemberBrief(json.getString("memberBrief"));
			
			if (json.has("observerBrief"))
				element.setObserverBrief(json.getString("observerBrief"));
			
			// attendee builder
			if (json.has("attendee"))
				CalElementUtil.attendeeUpdate(element.getMembers(), json.get("attendee"));
			
			elementBO.add(element);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private int timeParser(String value)
	{
		logger.debug("Time {} is pasring...", value);
		
		String[] vals = value.split(":");
		
		if (vals.length > 1)
		{
			return (XParamUtils.getInteger(vals[0]) * 60) + XParamUtils.getInteger(vals[1]);
		}
		else
		{
			return XParamUtils.getInteger(vals[1]);
		}
	}
}
