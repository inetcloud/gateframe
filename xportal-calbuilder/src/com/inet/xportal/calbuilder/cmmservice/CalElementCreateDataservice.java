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

import javax.inject.Named;

import com.inet.xportal.calbuilder.dataservice.CalElementAbstraction;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.nosql.web.data.FirmProfileDTO;
import com.inet.xportal.web.WebConstant;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.annotation.XPortalDataService;
import com.inet.xportal.web.annotation.XPortalPageRequest;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.ObjectWebDataservice;
import com.inet.xportal.web.interfaces.WebDataService;


/**
 * 
 * CalElementCreateDataservice.
 *
 * @author Hien Nguyen
 * @version $Id: CalElementCreateDataservice.java Jul 12, 2016 9:01:17 AM nguyen_dv $
 *
 * @since 1.0
 */
@Named("calbuilderelementcreate")
@XPortalDataService(roles={WebConstant.ROLE_COMMUNITY}, description = "CalDepartment service")
@XPortalPageRequest(uri = "calbuilder/element/create",
	inherit = true,
	transaction = true,
	result = WebConstant.ACTION_XSTREAM_JSON_RESULT)
public class CalElementCreateDataservice extends CalElementAbstraction {
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.calbuilder.dataservice.CalBaseAbstraction#service(com.inet.xportal.nosql.web.data.FirmProfileDTO, com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
    protected WebDataService service(final FirmProfileDTO subfirm,
    		final AbstractBaseAction action, 
    		final Map<String, Object> params) throws WebOSBOException {
		final CalElement element = elementBuilder(subfirm, action, params);
		
		String uuid = elementBO.add(element);
		element.setUuid(uuid);
		
		return new ObjectWebDataservice<CalElement>(element);
    }
}
