/*****************************************************************
   Copyright 2013 by Hien Nguyen (hiennguyen@inetcloud.vn)

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

import com.inet.xportal.calbuilder.bo.CalDeptBO;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.report.dataservice.ReportDirectDownloadDataservice;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.data.ViolationDTO;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.util.SecurityUtil;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * DepartmentReportDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: DepartmentReportDataservice.java Apr 27, 2015 3:44:18 PM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderdepartmentreport")
@XPortalDataService(roles={WebConstant.ROLE_USER}, description="Report service")
@XPortalPageRequest(uri="calbuilder/department/report",
	action = WebConstant.FILE_DOWNLOAD_ACTON, 
	result = WebConstant.ACTION_XSTREAM_RESULT)
public class DepartmentReportExportDataservice extends ReportDirectDownloadDataservice {
	@Inject
	private CalDeptBO deptBO;
	
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.web.interfaces.DataServiceMarker#service(com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
	protected WebDataService service(final AbstractBaseAction action, 
						     		 final Map<String, Object> params) throws WebOSBOException {
		final SearchDTO<CalDept> result = deptBO.query(SecurityUtil.getPrincipal());
		if (result == null || result.getTotal() <= 0)
		{
			logger.error("Your have no calbuilder owner is assigned!");
			action.getViolation().add(new ViolationDTO("SECURITY", "SECURITY", 1, "SECURITY_RETRICTED"));
			throw new WebOSBOException("Bad request!");
		}
		
		if (result.getTotal() > 1)
		{
			String deptID = XParamUtils.getString("dept", params);
			if (!StringUtils.hasLength(deptID))
			{
				logger.error("Department ID is required!");
				action.getViolation().add(new ViolationDTO("DEPARTMENT", "DEPARTMENT", 1, "DEPARTMENT_MISSED"));
				throw new WebOSBOException("Bad request!");
			}
			
			for (CalDept item : result.getItems())
			{
				if (deptID.equals(item.getUuid()))
				{
					action.getRequestParams().put("templateID", item.getTemplateID());
					return super.service(action, params);
				}
			}
			
			logger.error("Your have no calbuilder is assigned with {} department!", deptID);
			action.getViolation().add(new ViolationDTO("SECURITY", "SECURITY", 1, "SECURITY_RETRICTED"));
			throw new WebOSBOException("Bad request!");
		}
		
		// update parameter
		action.getRequestParams().put("templateID", result.getItems().get(0).getTemplateID());
		action.getRequestParams().put("dept", result.getItems().get(0).getUuid());
		
		return super.service(action, params);
	}
}
