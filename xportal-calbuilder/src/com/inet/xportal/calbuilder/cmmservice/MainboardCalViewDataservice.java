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
package com.inet.xportal.calbuilder.cmmservice;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.bo.CalElementBO;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.calbuilder.util.CalElementUtil;
import com.inet.xportal.nosql.web.bo.SiteBO;
import com.inet.xportal.nosql.web.bo.SubFirmProfileBO;
import com.inet.xportal.nosql.web.data.FirmProfileDTO;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.context.WebContext;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.DataServiceMarker;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * MainboardCalViewDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: MainboardCalViewDataservice.java Jul 12, 2016 5:45:22 PM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuildermainboardview")
@XPortalDataService(roles={WebConstant.ROLE_COMMUNITY}, description = "CalDepartment service")
@XPortalPageRequest(uri = "calbuilder/mainboard/view",
	inherit = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class MainboardCalViewDataservice extends DataServiceMarker {
	@Inject
	private CalElementBO elementBO;
	
	@Inject
	private SubFirmProfileBO subfirmBO;
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.web.interfaces.DataServiceMarker#service(com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
    protected WebDataService service(final AbstractBaseAction action, final Map<String, Object> params) throws WebOSBOException {
		FirmProfileDTO subfirm = subfirmBO.loadByPrefix(action.getFirmPrefix());
		if (subfirm == null)
		{
			subfirm = WebContext.INSTANCE.cache()
			.getBean(SiteBO.class)
			.load(action.getSiteID());
			
			subfirm.setPrefix(WebContext.INSTANCE.cache().getWebContext());
		}
		
		SearchDTO<CalElement> result = null;
		int[] ymwd = CalElementUtil.ymwd();
		// get current year (if missed)
		int year = XParamUtils.getInteger("year", params, ymwd[0]);
		// get day report
		int day = XParamUtils.getInteger("day", params, -1);
		if (day == -1)
		{
			// get current week (if missed)
			result = elementBO.weekFirm(subfirm.getUuid(),
					StringUtils.EMPTY_STRING,
					year, 
					XParamUtils.getInteger("week", params, ymwd[2]), 
					1);
		}
		else {
			result = elementBO.dayFirm(subfirm.getUuid(),
					StringUtils.EMPTY_STRING,
					year, 
					day, 
					1);
		}
		
		if (result == null)
			result = new SearchDTO<CalElement>();
		
		return new ObjectWebDataservice<SearchDTO<CalElement>>(result);
    }
}
