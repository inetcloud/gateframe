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
package com.inet.xportal.calbuilder.subadmservice;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.bo.CalDepartmentBO;
import com.inet.xportal.calbuilder.dataservice.CalBaseAbstraction;
import com.inet.xportal.calbuilder.model.CalDepartment;
import com.inet.xportal.nosql.web.NoSQLConstant;
import com.inet.xportal.nosql.web.data.FirmProfileDTO;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
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
@XPortalDataService(roles={NoSQLConstant.ROLE_SUBADMIN}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/create",
	inherit = true,
	transaction = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class DepartmentCalCreateDataservice extends CalBaseAbstraction {
	@Inject
	private CalDepartmentBO deptBO;
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.calbuilder.dataservice.DepartmentCalAbstraction#service(com.inet.xportal.calbuilder.model.CalDept, com.inet.xportal.calbuilder.model.CalElement, com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
    protected WebDataService service(final FirmProfileDTO subfirm,
			final AbstractBaseAction action,
			final Map<String, Object> params) throws WebOSBOException {
		final CalDepartment model = action.getModel(CalDepartment.class);
		model.setFirmUUID(subfirm.getUuid());
		
		if (!StringUtils.hasLength(model.getReviewer()))
			model.setReviewer(SecurityUtil.getPrincipal());
		
		String uuid = deptBO.add(model);
		model.setUuid(uuid);
		
		return new ObjectWebDataservice<CalDepartment>(model);
    }
}
