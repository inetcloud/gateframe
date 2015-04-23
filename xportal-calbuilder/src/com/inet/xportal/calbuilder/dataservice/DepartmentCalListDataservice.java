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

import com.inet.xportal.calbuilder.BuilderConstant;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * DepartmentCalListDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: DepartmentCalListDataservice.java Apr 23, 2015 2:12:20 PM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderdepartmentquery")
@XPortalDataService(roles={BuilderConstant.ROLE_CALBUILDER}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/query",
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class DepartmentCalListDataservice extends DepartmentCalAbstraction {
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
		return new CalElement();
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
		final Calendar cal = Calendar.getInstance();
		int day = XParamUtils.getInteger("day",params,-1);
		int week = XParamUtils.getInteger("week",params,cal.get(Calendar.WEEK_OF_YEAR));
		if (day > 0)
			week = -1;
		
		elementBO.queryByReviewed(dept.getUuid(), 
				XParamUtils.getInteger("year",params,cal.get(Calendar.YEAR)), 
				-1, 
				week, 
				day);
		return new ObjectWebDataservice<CalElement>(element);
    }
}
