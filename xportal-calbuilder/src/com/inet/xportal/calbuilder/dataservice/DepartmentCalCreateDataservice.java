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

import java.util.Calendar;
import java.util.Map;

import javax.inject.Named;

import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.BuilderConstant;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.calbuilder.util.CalElementUtil;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.data.ViolationDTO;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.util.SecurityUtil;

/**
 * 
 * DepartmentCalCreateDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: DepartmentCalCreateDataservice.java Apr 23, 2015 1:02:31 PM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderdepartmentcreate")
@XPortalDataService(roles={BuilderConstant.ROLE_CALBUILDER}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/create",
	transaction = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class DepartmentCalCreateDataservice extends DepartmentCalAbstraction {
	/**
	 * 
	 * @param dept
	 * @param action
	 * @param params
	 * @return
	 * @throws WebOSBOException
	 */
	@Override
	protected CalElement loadElement(final CalDept dept, 
			final AbstractBaseAction action, 
			final Map<String, Object> params) throws WebOSBOException
	{
		// get model content
		final CalElement model = action.getModel(CalElement.class);
		model.setDeptUUID(dept.getUuid());
		model.setCreatorCode(SecurityUtil.getPrincipal());
		model.setCreatorName(SecurityUtil.getAlias());
		model.setScopeShow(StringUtils.EMPTY_STRING);
		
		if (!StringUtils.hasLength(model.getSubject()))
		{
			logger.error("Subject of this meeting is required.");
			action.getViolation().add(new ViolationDTO("SUBJECT", "SUBJECT", 1, "SUBJECT_MISSED"));
			throw new WebOSBOException("Bad request!");
		}
		
		if (model.getStartTime() <= 0 || 
			model.getStartTime() >= 1440)
		{
			logger.error("Start time must be in range of (1 to 1440).");
			action.getViolation().add(new ViolationDTO("STARTTIME", "STARTTIME", 1, "STARTTIME_MISSED"));
			throw new WebOSBOException("Bad request!");
		}
		

		if (model.getToTime() <= 0 || 
			model.getToTime() >= 1440)
		{
			logger.error("End time must be in range of (1 to 1440).");
			action.getViolation().add(new ViolationDTO("ENDTIME", "ENDTIME", 1, "ENDTIME_MISSED"));
			throw new WebOSBOException("Bad request!");
		}
		
		if (model.getStartTime() >= model.getToTime())
		{
			logger.error("Start time must be less than end time.");
			action.getViolation().add(new ViolationDTO("TIMEINVALIDATE", "TIMEINVALIDATE", 1, "TIMEINVALIDATE"));
			throw new WebOSBOException("Bad request!");
		}
		
		// time of this element in calendar
		final Calendar cal = Calendar.getInstance();
		if (model.getYear() <= 0)
			model.setYear(cal.get(Calendar.YEAR));
		else
			cal.set(Calendar.YEAR, model.getYear());
		
		if (model.getDay() <= 0)
			model.setDay(cal.get(Calendar.DAY_OF_YEAR));
		else
			cal.set(Calendar.DAY_OF_YEAR, model.getDay());
		
		elementBO.TimeAdjustWithoutSave(model);
		
		// attribute update
		CalElementUtil.attributeUpdate(model.getAttributes(), params);
		
		// attendee builder
		CalElementUtil.attendeeUpdate(model.getMembers(), params);
				
		return model;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.calbuilder.dataservice.DepartmentCalAbstraction#service(com.inet.xportal.calbuilder.model.CalDept, com.inet.xportal.calbuilder.model.CalElement, com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
    protected WebDataService service(final CalDept dept,
    		final CalElement element,
    		final AbstractBaseAction action, 
    		final Map<String, Object> params) throws WebOSBOException {
		
		String uuid = elementBO.add(element);
		element.setUuid(uuid);
		
		return new ObjectWebDataservice<CalElement>(element);
    }
}
