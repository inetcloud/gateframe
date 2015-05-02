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

import com.inet.xportal.calbuilder.data.AttendeeDTO;
import com.inet.xportal.calbuilder.data.AttendeeRole;
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
import com.inet.xportal.xdb.query.Update;
import com.inet.xportal.xdb.query.impl.QueryImpl;
import com.inet.xportal.xdb.query.impl.UpdateImpl;

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
			int published,
			int year, 
			int week, 
			int day,
			int allday) throws WebOSBOException
	{
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.order("day,startTime")
				.retrievedFields(false, "attributes");
		
		if (published > 0)
			query.field("published").equal(published == 1 ? true : false);
		
		if (StringUtils.hasLength(deptID))
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
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> publishByFirm(String deptID,
			int year, 
			int week) throws WebOSBOException
	{
		// query all items in week
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("week").equal(week)
				.field("published").equal(false)
				.field("deptUUID").equal(deptID);
		
		// update with published status
		final Update<JSONDB> opts = new UpdateImpl<JSONDB>()
				.set("published", true);
		
		final SearchDTO<CalElement> result = super.query((QueryImpl<JSONDB>)query);
		if (result != null && result.getTotal() > 0)
		{
			// publish all elements
			super.update((UpdateImpl<JSONDB>)opts, (QueryImpl<JSONDB>)query);
		}
		
		return result;
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
		return super.query((QueryImpl<JSONDB>)queryBuilder(deptID, 0, year, week, day, allday));
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
		return super.query((QueryImpl<JSONDB>)queryBuilder(deptID, 1, year, week, day,allday));
	}
	
	/**
	 * 
	 * @param scopeShow
	 * @param year
	 * @param week
	 * @param day
	 * @param allday
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> queryByMainboard(String scopeShow, int year, int week, int day, int allday) throws WebOSBOException
	{
		final Query<JSONDB> query = queryBuilder(StringUtils.EMPTY_STRING, 
				1, 
				year, 
				week, 
				day,allday);
		
		if (StringUtils.hasLength(scopeShow))
			query.field("scopeShow").equal(scopeShow);
		else
			query.field("scopeShow").notEqual(StringUtils.EMPTY_STRING);
		
		return super.query((QueryImpl<JSONDB>)query);
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
	public CalendarMessage calendarBuilder(final CalElement element, final SiteDataModel siteInf)
	{
		// don't need to build this calendar
		if (CollectionUtils.isEmpty(element.getMembers()))
			return null;
		
		final CalendarMessage message = new CalendarMessage();
		message.setAction(TodoActionType.CREATE.name());
		
		// add member into this meeting
		for (AttendeeDTO member : element.getMembers())
		{
			if (AttendeeRole.MEMBER.name().equals(member.getRole()))
			{
				message.getMembers().add(member.getCode());
			}
		}
		
		if (CollectionUtils.isEmpty(message.getMembers()))
			return null;
		
		// chairman
		if (StringUtils.hasLength(element.getChairmanCode()))
			message.setChairman(element.getChairmanCode());
		else
			message.setChairman(element.getChairmanName());
		
		// the subject of meeting
		message.setSubject(element.getSubject());
		
		// description
		message.setSummary(element.getSummary());
		// location
		message.setLocation(element.getLocation());
		// who create this meeting
		message.setCreator(element.getCreatorCode());
		
		message.setFirmContext(siteInf.getFirmContext());
		message.setFirmName(siteInf.getName());
		message.setFirmSharedDomain(siteInf.getShareDomain());
		message.setRefTodoID(element.getUuid());
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,element.getYear());
		cal.set(Calendar.DAY_OF_YEAR,element.getDay());
		
		cal.set(Calendar.HOUR_OF_DAY,(int)(element.getStartTime() / 60));
		cal.set(Calendar.MINUTE,element.getStartTime() % 60);
		// start time of this calendar
		message.setStartdate(cal.getTimeInMillis());
		
		cal.set(Calendar.HOUR_OF_DAY,(int)(element.getToTime() / 60));
		cal.set(Calendar.MINUTE,element.getToTime() % 60);
		
		// end time fo this calendar
		message.setEnddate(cal.getTimeInMillis());
		
		return message;
	}
}
