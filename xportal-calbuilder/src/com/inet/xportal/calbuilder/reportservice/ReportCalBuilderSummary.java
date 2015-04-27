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
package com.inet.xportal.calbuilder.reportservice;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inet.xportal.calbuilder.bo.CalDeptBO;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.report.ReportService;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * ReportCalBuilderSummary.
 *
 * @author Hien Nguyen
 * @version $Id: ReportCalBuilderSummary.java Apr 27, 2015 9:35:06 AM nguyen_dv $
 *
 * @since 1.0
 */
@Named("ReportCalBuilderSummary")
public class ReportCalBuilderSummary implements ReportService {
	private static final Logger logger = LoggerFactory.getLogger(ReportCalBuilderSummary.class);
	@Inject
	private CalDeptBO deptBO;
	
	/* (non-Javadoc)
	 * @see com.inet.xportal.report.ReportService#invoke(java.util.Map)
	 */
	@Override
	public Object invoke(final Map<String, Object> params) throws WebOSBOException {
		CalDept dept = null;
		String deptID = XParamUtils.getString("dept", params);
		logger.debug("Depart ID : {}", deptID);
		if (StringUtils.hasLength(deptID))
		{
			dept = deptBO.load(deptID);
			logger.debug("Data of this department is : {}", dept);
		}
		
		return dept == null ? new CalDept() : dept;
	}
}
