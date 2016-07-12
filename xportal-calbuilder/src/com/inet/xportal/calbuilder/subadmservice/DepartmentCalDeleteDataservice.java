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
import com.inet.xportal.web.data.ViolationDTO;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * DepartmentCalDeleteDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: DepartmentCalDeleteDataservice.java Apr 23, 2015 2:48:38 PM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderdepartmentdelete")
@XPortalDataService(roles={NoSQLConstant.ROLE_SUBADMIN}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/delete",
	inherit = true,
	transaction = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class DepartmentCalDeleteDataservice extends CalBaseAbstraction {
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
		String depart = XParamUtils.getString("department", params);
		if (!StringUtils.hasLength(depart))
		{
			logger.error("Depart ID is required");
			action.getViolation().add(new ViolationDTO("DEPARTMENT", "DEPARTMENT", 1, "DEPARTMENT_MISSED"));
			throw new WebOSBOException("Bad request!");
		}
		
		final CalDepartment model = deptBO.loadByID(subfirm.getUuid(), depart);
		if (model != null)
			deptBO.remove(depart);
		
		return new ObjectWebDataservice<CalDepartment>(model);
    }
}
