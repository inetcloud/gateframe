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
package com.inet.xportal.calbuilder.admservice;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.inet.xportal.calbuilder.bo.CalDeptBO;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.DataServiceMarker;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;


/**
 * 
 * CalDepartmentDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: CalDepartmentDataservice.java Apr 27, 2015 10:01:46 AM nguyen_dv $
 *
 * @since 1.0
 */
@Named("caldepartmentquery")
@XPortalDataService(roles={WebConstant.ROLE_ADMIN}, description = "CalDepartment service")
@XPortalPageRequest(uri = "caldepartment/query",
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class CalDepartmentDataservice extends  DataServiceMarker {
	@Inject
	private CalDeptBO deptBO;
	
	/*
	 * 
	 */
	@Override
    protected WebDataService service(final AbstractBaseAction action, 
    		final Map<String, Object> params) throws WebOSBOException {
		return new ObjectWebDataservice<SearchDTO<CalDept>>(deptBO.query());
    }
}
