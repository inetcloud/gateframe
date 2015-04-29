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

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.util.CollectionUtils;

import com.inet.xportal.calbuilder.BuilderConstant;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.nosql.web.bo.SiteBO;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.nosql.web.model.SiteDataModel;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.bo.PushEventBO;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.message.CalendarMessage;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * DepartmentCalPublishDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: DepartmentCalPublishDataservice.java Apr 29, 2015 8:32:54 AM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderdepartmentpublish")
@XPortalDataService(roles={BuilderConstant.ROLE_CALBUILDER}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/publish",
	transaction = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class DepartmentCalPublishDataservice extends DepartmentCalAbstraction {
	@Inject
	private PushEventBO eventBO;
	
	@Inject
	private SiteBO siteBO;
	
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.calbuilder.dataservice.DepartmentCalAbstraction#loadElement(com.inet.xportal.calbuilder.model.CalDept, com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
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
		final SearchDTO<CalElement> elements = elementBO.publishByFirm(dept.getUuid(), 
				XParamUtils.getInteger("year",params,cal.get(Calendar.YEAR)),
				XParamUtils.getInteger("week",params,cal.get(Calendar.WEEK_OF_YEAR)));
			
		// create event in calendar for all these elements
		if (elements != null && elements.getTotal() > 0)
		{
			final SiteDataModel siteInf = siteBO.load(action.getSiteID());
			for (CalElement item : elements.getItems())
			{
				if (!CollectionUtils.isEmpty(item.getMembers()))
				{
					final CalendarMessage message = elementBO.calendarBuilder(item, siteInf);
					if (message != null)
						eventBO.message(message);
				}
			}
		}
		
		return new ObjectWebDataservice<CalElement>(element);
    }
}
