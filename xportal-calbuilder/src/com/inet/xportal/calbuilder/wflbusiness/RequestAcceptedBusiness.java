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
		String deptID = (String) getVariable("dept");
		if (!StringUtils.hasLength(deptID))
			return;

		// check department information
		final CalDept deptInf = deptBO.load(deptID);
		if (deptInf == null)
			return;
		
		// leave form data makeup
		if (requestInfo.getRequestData() != null) {
			if (requestInfo.getRequestData().has("elements"))
		    {
				Object element = requestInfo.getRequestData().get("elements");
			    logger.debug("Element object: {}", element);
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
			json.has("day") &&
			json.has("subject")) {
			
			final CalElement element = new CalElement();
			element.setDeptUUID(deptInf.getUuid());
			element.setSubject(json.getString("subject"));
			element.setDay(json.getInt("day"));
			if (json.has("year"))
				element.setYear(json.getInt("year"));
			
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
}
