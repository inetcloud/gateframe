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

import com.inet.xportal.calbuilder.bo.CalElementBO;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.DataServiceMarker;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * DepartmentCalViewDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: DepartmentCalViewDataservice.java Apr 23, 2015 2:10:20 PM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderdepartmentview")
@XPortalDataService(roles={WebConstant.ROLE_USER}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/view", result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class DepartmentCalViewDataservice extends DataServiceMarker {
	@Inject
	private CalElementBO elementBO;
	
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.web.interfaces.DataServiceMarker#service(com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
    protected WebDataService service(final AbstractBaseAction action, final Map<String, Object> params) throws WebOSBOException {
		final Calendar cal = Calendar.getInstance();
		return new ObjectWebDataservice<SearchDTO<CalElement>>(
	    		elementBO.queryByPublished(XParamUtils.getString("dept", params),
	    			XParamUtils.getInteger("year",params,cal.get(Calendar.YEAR)),
		    		XParamUtils.getInteger("week",params,cal.get(Calendar.WEEK_OF_YEAR)),
		    		-1, 
		    		XParamUtils.getInteger("allday",params,0)));
    }
}
