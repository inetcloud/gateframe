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

import java.util.Map;

import javax.inject.Named;

import com.inet.xportal.calbuilder.BuilderConstant;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.data.ViolationDTO;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;

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
@XPortalDataService(roles={BuilderConstant.ROLE_CALBUILDER}, description = "CalBuilder service")
@XPortalPageRequest(uri = "calbuilder/department/delete",
	transaction = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class DepartmentCalDeleteDataservice extends DepartmentCalAbstraction {
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.calbuilder.dataservice.DepartmentCalAbstraction#service(com.inet.xportal.calbuilder.model.CalDept, com.inet.xportal.calbuilder.model.CalElement, com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
    protected WebDataService service(final CalDept dept,
    		final CalElement element,
    		final AbstractBaseAction action, 
    		final Map<String, Object> params) throws WebOSBOException {
		if (element.isPublished())
		{
			logger.error("This element cannot be deleted due to published status.");
			action.getViolation().add(new ViolationDTO("INVALIDATE", "INVALIDATE", 1, "INVALIDATE"));
			throw new WebOSBOException("Bad request!");
		}
		
		elementBO.remove(element.getUuid());
		
		return new ObjectWebDataservice<CalElement>(element);
    }
}
