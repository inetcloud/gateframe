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
 * CalDepartmentUpdateDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: CalDepartmentUpdateDataservice.java Apr 27, 2015 10:02:51 AM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderdepartmentupdate")
@XPortalDataService(roles={NoSQLConstant.ROLE_SUBADMIN}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/update",
	inherit = true,
	transaction = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class CalDepartmentUpdateDataservice extends  CalBaseAbstraction {
	@Inject
	private CalDepartmentBO deptBO;
	
	/*
	 * 
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
		if (model == null)
		{
			logger.error("Depart {} is not found.", depart);
			action.getViolation().add(new ViolationDTO("DEPARTMENT", "DEPARTMENT", 1, "DEPARTMENT_NOT_FOUND"));
			throw new WebOSBOException("Bad request!");
		}
		
		String name = XParamUtils.getString("name", params);
		if (StringUtils.hasLength(name))
		{
			if (!model.getDepartment().equalsIgnoreCase(name) &&
				deptBO.loadByName(subfirm.getUuid(), name) != null)
			{
				logger.error("Depart {} is existed.", name);
				action.getViolation().add(new ViolationDTO("DEPARTMENT", "DEPARTMENT", 1, "DEPARTMENT_NAME_DUPLICATED"));
				throw new WebOSBOException("Bad request!");
			}
			
			model.setDepartment(name);
		}
		
		model.setReviewer(XParamUtils.getString("reviewer", params, model.getReviewer()));
		deptBO.update(model.getUuid(), model);
		
		return new ObjectWebDataservice<CalDepartment>(model);
    }
}
