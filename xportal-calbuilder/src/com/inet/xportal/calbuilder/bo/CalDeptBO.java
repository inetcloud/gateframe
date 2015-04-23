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
package com.inet.xportal.calbuilder.bo;

import javax.inject.Inject;
import javax.inject.Named;

import com.inet.xportal.calbuilder.model.CalDept;
import com.inet.xportal.nosql.web.bf.MagicContentBF;
import com.inet.xportal.nosql.web.bo.MagicContentBO;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.xdb.persistence.JSONDB;
import com.inet.xportal.xdb.query.Query;
import com.inet.xportal.xdb.query.impl.QueryImpl;

/**
 * CalDeptBO.
 *
 * @author Hien Nguyen
 * @version $Id: CalDeptBO.java Apr 23, 2015 11:25:36 AM nguyen_dv $
 *
 * @since 1.0
 */
@Named("CalBuilderDeptBO")
public class CalDeptBO extends MagicContentBO<CalDept> {
	/**
	 * 
	 * @param businessFacade
	 * 
	 *            This data must be global for all access
	 * 
	 */
	@Inject
	protected CalDeptBO(MagicContentBF businessFacade) {
		super(businessFacade, "calbuilder-department");
	}

	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.nosql.web.bo.MagicContentBO#add(java.lang.Object)
	 */
	@Override
    public String add(CalDept info) throws WebOSBOException {
	    return super.add(info,"name");
    }

	/**
	 * 
	 * @param ownerCode
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalDept> query(String ownerCode) throws WebOSBOException
	{
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("ownerCode").equal(ownerCode);
		
		return super.query((QueryImpl<JSONDB>)query);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.nosql.web.bo.SQLMagicBase#getClassConvetor()
	 */
	@Override
    protected Class<CalDept> getClassConvetor() {
	    return CalDept.class;
    }
}
