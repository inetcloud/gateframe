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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.BuilderConstant;
import com.inet.xportal.calbuilder.data.AttendeeDTO;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.nosql.web.bf.MagicContentBF;
import com.inet.xportal.nosql.web.bo.MagicContentBO;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.nosql.web.model.SiteDataModel;
import com.inet.xportal.unifiedpush.data.TodoActionType;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.message.CalendarMessage;
import com.inet.xportal.xdb.business.BaseDBStore;
import com.inet.xportal.xdb.persistence.JSONDB;
import com.inet.xportal.xdb.query.Query;
import com.inet.xportal.xdb.query.impl.QueryImpl;

/**
 * CalElementBO.
 *
 * @author Hien Nguyen
 * @version $Id: CalElementBO.java Apr 23, 2015 11:25:46 AM nguyen_dv $
 *
 * @since 1.0
 */
@Named("CalBuilderElementBO")
public class CalElementBO extends MagicContentBO<CalElement> {
	/**
	 * 
	 * @param businessFacade
	 * 
	 *            This data must be global for all access
	 * 
	 */
	@Inject
	protected CalElementBO(MagicContentBF businessFacade) {
		super(businessFacade, "calbuilder-element");
	}

	/**
	 * 
	 * @param info
	 */
	public void TimeAdjustWithoutSave(final CalElement info)
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, info.getYear());
		cal.set(Calendar.DAY_OF_YEAR, info.getDay());
		
		// week and month is auto update
		info.setMonth(cal.get(Calendar.MONTH) +  1);
		info.setWeek(cal.get(Calendar.WEEK_OF_YEAR));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.nosql.web.bo.MagicContentBO#add(java.lang.Object)
	 */
	@Override
	public String add(CalElement info) throws WebOSBOException
	{
		final Calendar cal = Calendar.getInstance();
		
		// year update if missed
		if (info.getYear() <= 0)
			info.setYear(cal.get(Calendar.YEAR));
		else
			cal.set(Calendar.YEAR, info.getYear());
		
		// adjust day of this calendar
		if (info.getDay() <= 0)
			info.setYear(cal.get(Calendar.DAY_OF_YEAR));
		else
			cal.set(Calendar.DAY_OF_YEAR, info.getDay());
		
		// week and month is auto update
		TimeAdjustWithoutSave(info);
		
		return super.add(info);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param deptID
	 * @param usercode
	 * @return
	 * @throws WebOSBOException
	 */
    public CalElement loadElement(String uuid, 
    		String deptID, 
    		String usercode) throws WebOSBOException
    {
    	final Query<JSONDB> query = new QueryImpl<JSONDB>()
    			.field("creatorCode").equal(usercode)
    			.field("deptUUID").equal(deptID)
    			.field(BaseDBStore.ID_KEY).equal(BaseDBStore.getId(uuid));
    	
    	return super.load((QueryImpl<JSONDB>) query);
    }
    
	/**
	 * 
	 * @param deptID
	 * @param published
	 * @param year
	 * @param week
	 * @param day
	 * @param allday
	 * @return
	 * @throws WebOSBOException
	 */
	protected Query<JSONDB> queryBuilder(String deptID,
			boolean published,
			int year, 
			int week, 
			int day,
			int allday) throws WebOSBOException
	{
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("published").equal(published)
				.order("day,startTime")
				.retrievedFields(false, "attributes");
		
		if (BuilderConstant.PUBLISHED_SHOW.equalsIgnoreCase(deptID))
			query.field("scopeShow").equal(BuilderConstant.PUBLISHED_SHOW);
		else
			query.field("deptUUID").equal(deptID);
			
		if (week > 0)
			query.field("week").equal(week);
			
		if (day > 0)
			query.field("day").equal(day);
		
		// morning only
		if (allday == 1)
		{
			query.field("toTime").lessThanOrEq(720);
		}
		// afternoon
		else if (allday == 2)
		{
			query.field("startTime").greaterThanOrEq(720);
		}
		
		return query;
	}
	
	/**
	 * 
	 * @param deptID
	 * @param year
	 * @param week
	 * @param day
	 * @param allday
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> queryByReviewed(String deptID,
			int year, 
			int week, 
			int day,
			int allday) throws WebOSBOException
	{
		return super.query((QueryImpl<JSONDB>)queryBuilder(deptID, false, year, week, day, allday));
	}
	
	/**
	 * 
	 * @param deptID
	 * @param year
	 * @param week
	 * @param day
	 * @param allday
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> queryByPublished(String deptID,
			int year, 
			int week, 
			int day,
			int allday) throws WebOSBOException
	{
		return super.query((QueryImpl<JSONDB>)queryBuilder(deptID, true, year, week, day,allday));
	}
	
	/**
	 * 
	 * @param year
	 * @param week
	 * @param day
	 * @param allday
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> queryByMainboard(int year, int week, int day, int allday) throws WebOSBOException
	{
		return super.query((QueryImpl<JSONDB>)queryBuilder(BuilderConstant.PUBLISHED_SHOW, 
				true, 
				year, 
				week, 
				day,allday));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.inet.xportal.nosql.web.bo.SQLMagicBase#getClassConvetor()
	 */
	@Override
    protected Class<CalElement> getClassConvetor() {
	    return CalElement.class;
    }
	
	// this map will help object convert all children data
	static Map<String, Class<?>> childrenConvert;
	static {
		childrenConvert = new HashMap<String, Class<?>>();
		childrenConvert.put("members", AttendeeDTO.class);
		childrenConvert.put("observers", AttendeeDTO.class);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inet.xportal.nosql.web.bo.MagicContentBO#childrenConvertMap()
	 */
	@Override
	protected Map<String, Class<?>> childrenConvertMap() {
		return childrenConvert;
	}
	
	/**
	 * 
	 * @param element
	 */
	public void calendarBuilder(final CalElement element, final SiteDataModel siteInf)
	{
		// don't need to build this calendar
		if (CollectionUtils.isEmpty(element.getMembers()))
			return;
		
		for (AttendeeDTO member : element.getMembers())
		{
			final CalendarMessage message = new CalendarMessage();
			message.setAction(TodoActionType.CREATE.name());
			
			// chairman
			if (StringUtils.hasLength(element.getChairmanCode()))
				message.setChairman(element.getChairmanCode());
			else
				message.setChairman(element.getChairmanName());
			
			// the subject of meeting
			message.setSubject(element.getSubject());
			
			// description
			message.setSummary(element.getSummary());
			
			// who create this meeting
			message.setCreator(member.getCode());
			
			message.setFirmContext(siteInf.getFirmContext());
			message.setFirmName(siteInf.getName());
			message.setFirmSharedDomain(siteInf.getShareDomain());
			message.setRefTodoID(element.getUuid());
			message.setEnddate(element.getToTime());
		}
	}
}
