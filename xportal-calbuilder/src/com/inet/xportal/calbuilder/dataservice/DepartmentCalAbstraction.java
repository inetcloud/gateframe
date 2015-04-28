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

import javax.inject.Inject;

import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inet.xportal.calbuilder.bo.CalDeptBO;
import com.inet.xportal.calbuilder.bo.CalElementBO;
import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.data.ViolationDTO;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.DataServiceMarker;
import com.inet.xportal.web.interfaces.WebDataService;
import com.inet.xportal.web.util.SecurityUtil;
import com.inet.xportal.web.util.XParamUtils;

/**
 * 
 * DepartmentCalAbstraction.
 *
 * @author Hien Nguyen
 * @version $Id: DepartmentCalAbstraction.java Apr 23, 2015 1:04:43 PM nguyen_dv $
 *
 * @since 1.0
 */
public abstract class DepartmentCalAbstraction extends DataServiceMarker {
	protected static final Logger logger = LoggerFactory.getLogger(DepartmentCalAbstraction.class);
	
	@Inject
	private CalDeptBO deptBO;
	
	@Inject
	protected CalElementBO elementBO;
	/**
	 * 
	 * @param dept
	 * @param action
	 * @param params
	 * @return
	 * @throws WebOSBOException
	 */
	protected CalElement loadElement(final CalDept dept, 
			final AbstractBaseAction action, 
			final Map<String, Object> params) throws WebOSBOException
	{
		String elementID = XParamUtils.getString("element", params);
		if (!StringUtils.hasLength(elementID))
		{
			logger.error("Element ID is required.");
			action.getViolation().add(new ViolationDTO("ELEMENT_ID", "ELEMENT_ID", 1, "ELEMENT_ID_MISSED"));
			throw new WebOSBOException("Bad request!");
		}
		
		// check the activity
		final CalElement element = elementBO.loadElement(elementID,dept.getUuid(), SecurityUtil.getPrincipal());
		if (element == null)
		{
			logger.error("Element {} is not found.", elementID);
			action.getViolation().add(new ViolationDTO("ELEMENT_ID", "ELEMENT_ID", 1, "ELEMENT_NOT_FOUND"));
			throw new WebOSBOException("Bad request!");
		}
		
		return element;
	}
	
	/**
	 * 
	 * @param dept
	 * @param element
	 * @param action
	 * @param params
	 * @return
	 * @throws WebOSBOException
	 */
	protected abstract WebDataService service(final CalDept dept,
			final CalElement element,
			final AbstractBaseAction action,
			final Map<String, Object> params) throws WebOSBOException;
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.web.interfaces.DataServiceMarker#service(com.inet.xportal.web.action.AbstractBaseAction, java.util.Map)
	 */
	@Override
    protected final WebDataService service(final AbstractBaseAction action, 
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
					return service(item, 
							loadElement(item, action, params),
							action, params);
			}
			
			logger.error("Your have no calbuilder is assigned with {} department!", deptID);
			action.getViolation().add(new ViolationDTO("SECURITY", "SECURITY", 1, "SECURITY_RETRICTED"));
			throw new WebOSBOException("Bad request!");
		}
		
		// do business with specific department
		return service(result.getItems().get(0),
				loadElement(result.getItems().get(0), action, params),
				action, params);
	}
}

