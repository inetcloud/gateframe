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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.data.AttendeeDTO;
import com.inet.xportal.calbuilder.data.AttendeeRole;
import com.inet.xportal.calbuilder.data.CalendarType;
import com.inet.xportal.calbuilder.data.MemberDTO;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.calbuilder.util.CalElementUtil;
import com.inet.xportal.calendar.TimeZoneFactory;
import com.inet.xportal.calendar.bo.CalHeaderBO;
import com.inet.xportal.calendar.data.CalAttendee;
import com.inet.xportal.calendar.data.CalOrganizer;
import com.inet.xportal.calendar.model.CalHeader;
import com.inet.xportal.calendar.model.CalLocation;
import com.inet.xportal.nosql.web.bf.MagicContentBF;
import com.inet.xportal.nosql.web.bo.AccountBO;
import com.inet.xportal.nosql.web.bo.MagicContentBO;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.web.action.AbstractBaseAction;
import com.inet.xportal.web.context.ContentContext;
import com.inet.xportal.web.context.WebContext;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.interfaces.BeanInitiateInvoke;
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
public class CalElementBO extends MagicContentBO<CalElement> implements BeanInitiateInvoke {
	/**
	 * 
	 * @param businessFacade
	 * 
	 *            This data must be global for all access
	 * 
	 */
	@Inject
	protected CalElementBO(@ContentContext(context = "CalContext") MagicContentBF businessFacade) {
		super(businessFacade, "calbuilder-element");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inet.xportal.nosql.web.bo.MagicContentBO#add(java.lang.Object)
	 */
	@Override
	public String add(CalElement info) throws WebOSBOException {
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
		CalElementUtil.TimeAdjustWithoutSave(info);

		return super.add(info);
	}

	/**
	 * 
	 * @param uuid
	 * @param usercode
	 * @return
	 * @throws WebOSBOException
	 */
	public CalElement loadElement(String uuid, 
			String usercode) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field(BaseDBStore.ID_KEY).equal(BaseDBStore.getId(uuid));

		query.and(query.or(query.criteria("model").equal(1),
						   query.and(query.criteria("members.username").equal(usercode),
								     query.criteria("members.role").equal(AttendeeRole.CREATOR.name()))));
		
		return super.load((QueryImpl<JSONDB>) query);
	}

	/**
	 * 
	 * @param uuid
	 * @param mode
	 * @param firmUUID
	 * @param department
	 * @return
	 * @throws WebOSBOException
	 */
	public CalElement loadElement(String uuid, 
			int mode,
			String firmUUID,
			String department) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("model").equal(mode)
				.field("firmUUID").equal(firmUUID)
				.field(BaseDBStore.ID_KEY).equal(BaseDBStore.getId(uuid));

		if (StringUtils.hasLength(department))
			query.field("departID").equal(department);
		
		return super.load((QueryImpl<JSONDB>) query);
	}
	
	/**
	 * 
	 * @param firmUUID
	 * @param year
	 * @param week
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> weekCommunity(String firmUUID, 
			int year, 
			int week) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("week").equal(week)
				.field("firmUUID").equal(firmUUID)
				.field("type").equal(CalendarType.COMMUNITY.name())
				.field("mode").equal(1)
				.order("day,startTime");
		
		return super.query((QueryImpl<JSONDB>) query);
	}
	
	/**
	 * 
	 * @param firmUUID
	 * @param department
	 * @param year
	 * @param week
	 * @param mode
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> weekFirm(String firmUUID, 
			String department,
			int year, 
			int week,
			int mode) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("week").equal(week)
				.field("firmUUID").equal(firmUUID)
				.order("day,startTime");
		
		if (StringUtils.hasLength(department))
		{
			query.field("departID").equal(department)
				 .field("type").equal(CalendarType.DEPARTMENT.name());
		}
		else if (mode != 1)
		{
			query.field("type").in(CollectionUtils.asList(CalendarType.ORGANIZATION.name(),
					  									  CalendarType.COMMUNITY.name()));
		}
		else
		{
			query.field("type").equal(CalendarType.ORGANIZATION.name());
		}
		
		// status of calendar
		if (mode >= 0)
			query.field("mode").equal(mode);
		
		return super.query((QueryImpl<JSONDB>) query);
	}
	
	/**
	 * 
	 * @param firmUUID
	 * @param department
	 * @param year
	 * @param day
	 * @param mode
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> dayFirm(String firmUUID, 
			String department,
			int year, 
			int day,
			int mode) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("day").equal(day)
				.field("firmUUID").equal(firmUUID)
				.order("startTime");
		
		if (StringUtils.hasLength(department))
		{
			query.field("departID").equal(department)
				 .field("type").equal(CalendarType.DEPARTMENT.name());
		}
		else if (mode != 1)
		{
			query.field("type").in(CollectionUtils.asList(CalendarType.ORGANIZATION.name(),
					  									  CalendarType.COMMUNITY.name()));
		}
		else
		{
			query.field("type").equal(CalendarType.ORGANIZATION.name());
		}
		
		// status of calendar
		if (mode >= 0)
			query.field("mode").equal(mode);
		
		return super.query((QueryImpl<JSONDB>) query);
	}
	
	/**
	 * 
	 * @param member
	 * @param year
	 * @param week
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> weekFreeBusy(String member, 
			int year, 
			int week) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("mode").equal(1)
				.field("week").equal(week)
				.field("members.username").equal(member)
				.order("day,startTime")
				.retrievedFields(false, "members");

		return super.query((QueryImpl<JSONDB>) query);
	}

	/**
	 * 
	 * @param members
	 * @param year
	 * @param week
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> weekFreeBusy(final List<String> members, 
			int year, 
			int week) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("mode").equal(1)
				.field("week").equal(week)
				.field("members.username").in(members)
				.order("day,startTime")
				.retrievedFields(false, "members");

		return super.query((QueryImpl<JSONDB>) query);
	}
	
	/**
	 * 
	 * @param member
	 * @param year
	 * @param day
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> dayFreeBusy(String member, 
			int year, 
			int day) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("mode").equal(1)
				.field("day").equal(day)
				.field("members.username").equal(member)
				.order("startTime")
				.retrievedFields(false, "members");

		return super.query((QueryImpl<JSONDB>) query);
	}

	/**
	 * 
	 * @param members
	 * @param year
	 * @param day
	 * @return
	 * @throws WebOSBOException
	 */
	public SearchDTO<CalElement> dayFreeBusy(final List<String> members, 
			int year, 
			int day) throws WebOSBOException {
		final Query<JSONDB> query = new QueryImpl<JSONDB>()
				.field("year").equal(year)
				.field("mode").equal(1)
				.field("day").equal(day)
				.field("members.username").in(members)
				.order("startTime")
				.retrievedFields(false, "members");

		return super.query((QueryImpl<JSONDB>) query);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
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
	 * @param action
	 * @throws Throwable
	 */
	public void calendarBuilder(final CalElement element, final AbstractBaseAction action) throws Throwable 
	{
		// don't need to build this calendar
		if (CollectionUtils.isEmpty(element.getMembers()))
			return;

		final CalHeader message = new CalHeader();
		
		TimeZone timezone = TimeZoneFactory.INSTANCE.factory().getDefaultTimeZone();
		message.setTzId(timezone.getID());
		message.setUuid(element.getCaldavRef());
		// update firm context to this calendar
		message.setFirmshare(element.getFirmPrefix());
			
		final AccountBO accountBO = WebContext.INSTANCE.cache().getBean(AccountBO.class);
		final Map<String,String> members = new HashMap<String, String>();
		// add member into this meeting
		for (AttendeeDTO member : element.getMembers()) {
			if (AttendeeRole.MEMBER.name().equals(member.getRole()) ||
				AttendeeRole.OBSERVER.name().equals(member.getRole())) {
				// check user is validated in global firm
				if (!CollectionUtils.isEmpty(member.getMembers()))
				{
					for (MemberDTO item : member.getMembers())
					{
						if (members.containsKey(item.getUsername()))
						{
							if (StringUtils.hasLength(item.getUsername()) &&
								accountBO.globalExisted(item.getUsername()))
							{
								final CalAttendee attendee = new CalAttendee();
								attendee.setCn(item.getFullname());
								attendee.setEmailAddress(item.getUsername());
								attendee.setUri("mailto:" + item.getUsername());
								
								message.getAttendees().add(attendee);
								
								members.put(item.getUsername(), item.getFullname());
							}
						}
					}
				}
			} 
			else if (AttendeeRole.CHAIRMAN.name().equals(member.getRole())) {
				// check user is validated in global firm
				if (!CollectionUtils.isEmpty(member.getMembers()))
				{
					for (MemberDTO item : member.getMembers())
					{
						if (StringUtils.hasLength(item.getUsername()) &&
							 accountBO.globalExisted(item.getUsername()))
						{
							final CalOrganizer org = new CalOrganizer();
							org.setCn(item.getFullname());
							org.setEmailAddress(item.getUsername());
							org.setUri("mailto:" + item.getUsername());
							message.setOrganizer(org);
							
							break;
						}
					}
				}
			}
			else if (AttendeeRole.CREATOR.name().equals(member.getRole())) {
				// check user is validated in global firm
				if (!CollectionUtils.isEmpty(member.getMembers()))
				{
					for (MemberDTO item : member.getMembers())
					{
						message.setCreator(item.getUsername());
						message.setFullname(item.getFullname());
						break;
					}
				}
			}
		}

		// there is no member to create this calendar
		if (CollectionUtils.isEmpty(message.getAttendees()))
			return;
		
		// default, orgnizer is creator
		if (message.getOrganizer() == null ||
			!StringUtils.hasLength(message.getOrganizer().getEmailAddress()))
		{
			final CalOrganizer org = new CalOrganizer();
			org.setCn(message.getFullname());
			org.setEmailAddress(message.getCreator());
			org.setUri("mailto:" + message.getCreator());
			message.setOrganizer(org);
		}
		
		// the subject of meeting
		message.setSummary(element.getSubject());

		// description
		message.setDescription(element.getSummary());
		
		// location
		if (StringUtils.hasLength(element.getLocation()))
		{
			CalLocation loc = new CalLocation();
			loc.setAddress(element.getLocation());
			message.setLocation(loc);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, element.getYear());
		cal.set(Calendar.DAY_OF_YEAR, element.getDay());

		cal.set(Calendar.HOUR_OF_DAY, (int) (element.getStartTime() / 60));
		cal.set(Calendar.MINUTE, element.getStartTime() % 60);
		
		message.setCreated(cal.getTimeInMillis());
		
		// start time of this calendar
		message.setLstart(cal.getTimeInMillis());

		cal.set(Calendar.HOUR_OF_DAY, (int) (element.getToTime() / 60));
		cal.set(Calendar.MINUTE, element.getToTime() % 60);

		// end time fo this calendar
		message.setLend(cal.getTimeInMillis());

		try 
		{
			final CalHeader header0 = WebContext.INSTANCE.cache()
					.getBean(CalHeaderBO.class)
					.calendarMessage(message, action);
			if (header0 != null)
			{
				element.setCaldavRef(header0.getUuid());
			}
        } catch (Throwable ex) {
        	throw ex;
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inet.xportal.web.interfaces.BeanInitiateInvoke#init()
	 */
	@Override
	public void init() {
		ensureIndex("firmUUID,type,mode,year,day");
		ensureIndex("firmUUID,type,mode,year,week");

		ensureIndex("firmUUID,departID,type,mode,year,day");
		ensureIndex("firmUUID,departID,type,mode,year,week");
		
		ensureIndex("mode,year,week,members");
		ensureIndex("mode,year,day,members");
	}
}
