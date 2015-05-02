/*****************************************************************
   Copyright 2015 by Hien Nguyen (hiennguyen@inetcloud.vn)

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
package com.inet.xportal.calbuilder.dataservice;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.BuilderConstant;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.calbuilder.util.CalElementUtil;
import com.inet.xportal.nosql.web.bo.SiteBO;
import com.inet.xportal.unifiedpush.data.TodoActionType;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.bo.PushEventBO;
import com.inet.xportal.web.data.ViolationDTO;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.message.CalendarMessage;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * DepartmentCalUpdateDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: DepartmentCalUpdateDataservice.java Apr 23, 2015 2:17:27 PM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderdepartmentupdate")
@XPortalDataService(roles={BuilderConstant.ROLE_CALBUILDER}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/update",
	transaction = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class DepartmentCalUpdateDataservice extends DepartmentCalAbstraction {
	@Inject
	private PushEventBO eventBO;
	
	@Inject
	private SiteBO siteBO;
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.calbuilder.dataservice.DepartmentCalAbstraction#service(com.inet.xportal.calbuilder.model.CalDept, com.inet.xportal.calbuilder.model.CalElement, com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
    protected WebDataService service(final CalDept dept,
    		final CalElement element,
    		final AbstractBaseAction action, 
    		final Map<String, Object> params) throws WebOSBOException {
		
		// subject update
		element.setSubject(XParamUtils.getString("subject", params));
		if (!StringUtils.hasLength(element.getSubject()))
		{
			logger.error("Subject of this meeting is required.");
			action.getViolation().add(new ViolationDTO("SUBJECT", "SUBJECT", 1, "SUBJECT_MISSED"));
			throw new WebOSBOException("Bad request!");
		}
		
		// day can be updated 
		int day = XParamUtils.getInteger("day", params,-1);
		if (day > 0)
		{
			element.setDay(day);
			elementBO.TimeAdjustWithoutSave(element);
		}
		
		// start time update
		element.setStartTime(XParamUtils.getInteger("startTime", params, element.getStartTime()));
		if (element.getStartTime() <= 0 || 
			element.getStartTime() >= 1440)
		{
			logger.error("Start time must be in range of (1 to 1440).");
			action.getViolation().add(new ViolationDTO("STARTTIME", "STARTTIME", 1, "STARTTIME_MISSED"));
			throw new WebOSBOException("Bad request!");
		}
		
		element.setToTime(XParamUtils.getInteger("toTime", params, element.getToTime()));
		if (element.getToTime() <= 0 || 
			element.getToTime() >= 1440)
		{
			logger.error("End time must be in range of (1 to 1440).");
			action.getViolation().add(new ViolationDTO("ENDTIME", "ENDTIME", 1, "ENDTIME_MISSED"));
			throw new WebOSBOException("Bad request!");
		}
		
		if (element.getStartTime() >= element.getToTime())
		{
			logger.error("Start time must be less than end time.");
			action.getViolation().add(new ViolationDTO("TIMEINVALIDATE", "TIMEINVALIDATE", 1, "TIMEINVALIDATE"));
			throw new WebOSBOException("Bad request!");
		}
		
		// 1. remove old calendar
		if (element.isPublished())
		{
			// change this record to un-published
			element.setPublished(false);
			
			CalendarMessage message = new CalendarMessage();
			message.setAction(TodoActionType.REMOVE.name());
			message.setRefTodoID(element.getUuid());
			
			// remove calendar of this event
			eventBO.message(message);
		}
		
		element.setScopeShow(XParamUtils.getString("scopeShow", params));
		element.setChairmanCode(XParamUtils.getString("chairmanCode", params));
		element.setChairmanName(XParamUtils.getString("chairmanName", params));
		element.setContentOwner(XParamUtils.getString("contentOwner", params));
		element.setLocation(XParamUtils.getString("location", params));
		element.setSummary(XParamUtils.getString("summary", params));
		element.setMemberBrief(XParamUtils.getString("memberBrief", params));
		element.setObserverBrief(XParamUtils.getString("observerBrief", params));
		
		// attribute update
		CalElementUtil.attributeUpdate(element.getAttributes(), params);
		
		// update member list
		CalElementUtil.attendeeUpdate(element.getMembers(), params);
	
		// update element 
		elementBO.update(element.getUuid(), element);
		
		return new ObjectWebDataservice<CalElement>(element);
    }
}
