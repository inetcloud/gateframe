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
package com.inet.xportal.calbuilder.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.bo.AliasIndexBO;
import com.inet.xportal.calbuilder.data.AttendeeDTO;
import com.inet.xportal.calbuilder.data.AttendeeRole;
import com.inet.xportal.calbuilder.data.MemberDTO;
import com.inet.xportal.calbuilder.model.AliasIndex;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.calendar.data.PartStat;
import com.inet.xportal.web.util.XParamUtils;

/**
 * CalElementUtil.
 *
 * @author Hien Nguyen
 * @version $Id: CalElementUtil.java Apr 27, 2015 5:41:37 PM nguyen_dv $
 *
 * @since 1.0
 */
public class CalElementUtil {
	/**
	 * 
	 * @param info
	 */
	public static void TimeAdjustWithoutSave(final CalElement info)
	{
		final Calendar cal = Calendar.getInstance();
		
		if (info.getYear() > 0)
			cal.set(Calendar.YEAR, info.getYear());
		
		if (info.getDay() > 0)
			cal.set(Calendar.DAY_OF_YEAR, info.getDay());
		
		if (info.getWeek() > 0)
			cal.set(Calendar.WEEK_OF_YEAR, info.getWeek());
		
		info.setYear(cal.get(Calendar.YEAR));
		info.setMonth(cal.get(Calendar.MONTH) +  1);
		info.setWeek(cal.get(Calendar.WEEK_OF_YEAR));
		info.setDay(cal.get(Calendar.DAY_OF_YEAR));
		
		// adjust time of this element
		if (info.getStartTime() <= 0)
			info.setStartTime(cal.get(Calendar.HOUR_OF_DAY) * 60);
		
		// get time of this field (as required)
		// defaul is 1 hour meeting
		if (info.getToTime() <= 0 || 
			info.getToTime() <= info.getStartTime())
			info.setToTime(info.getStartTime() + 60);
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	public static int[] ymwd()
	{
		return ymwd(new Date());
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	public static int[] ymwd(Date time)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		
		return new int[]{cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.WEEK_OF_YEAR),
				cal.get(Calendar.DAY_OF_YEAR)};
	}
	
	/**
	 * 
	 * @param aliasBO
	 * @param element
	 * @param params
	 */
	public static void attendeeUpdate(final AliasIndexBO aliasBO,
			final CalElement element, 
			final Map<String, Object> params)
	{
		if (params.containsKey("attendee"))
		{
			element.getMembers().clear();
			
			// get members of this project
			String members = XParamUtils.getString("attendee", params);
			if (StringUtils.hasLength(members))
			{
				// get json object from request
				final JSONObject json = JSONObject.fromObject("{items:" + members +"}");
				
				// attendee builder
				final Object val = json.get("items");
				if (val instanceof JSONArray)
				{
					int size = ((JSONArray)val).size();
					for (int index = 0; index <  size; index++)
					{
						attendeeBuilder(aliasBO, element.getFirmUUID(), element.getMembers(), ((JSONArray)val).getJSONObject(index));
					}
				}
				else if (val instanceof JSONObject) {
					attendeeBuilder(aliasBO, element.getFirmUUID(), element.getMembers(), (JSONObject)val);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param aliasBO
	 * @param firmUUID
	 * @param arrlist
	 * @param json
	 */
	public static void attendeeBuilder(final AliasIndexBO aliasBO,
			String firmUUID,
			final List<AttendeeDTO> arrlist, 
			final JSONObject json)
	{
		if (json != null && 
			(json.has("alias") || json.has("members")))
		{
			final AttendeeDTO resource = new AttendeeDTO();
			
			if (json.has("alias"))
			{
				resource.setAlias(json.getString("alias"));
				
				AliasIndex alias = aliasBO.loadByAlias(firmUUID, resource.getAlias());
				if (alias == null)
				{
					alias = new AliasIndex();
					// build this alias to database
					alias.setAlias(resource.getAlias());
					alias.setFirmUUID(firmUUID);
					
					// gte member of this alias
					if (json.has("members"))
					{
						memberBuilder(alias.getMembers(), json);
						
						// save this alias
						aliasBO.add(alias);
						
						// add resource member
						resource.setMembers(alias.getMembers());
					}
				}
				else
				{
					resource.setMembers(alias.getMembers());
				}
			}
			else
			{
				memberBuilder(resource.getMembers(), json);
				
				// get the fist user in this member 
				resource.setAlias(resource.getMembers().get(0).getFullname());
			}
			
			// role data for this resource
			if (json.has("role"))
			{
				String role = json.getString("role");
				if (AttendeeRole.OBSERVER.name().equalsIgnoreCase(role))
					resource.setRole(AttendeeRole.OBSERVER.name());
				else if (AttendeeRole.CHAIRMAN.name().equalsIgnoreCase(role))
					resource.setRole(AttendeeRole.CHAIRMAN.name());
				else
					resource.setRole(AttendeeRole.MEMBER.name());
			}
			else
			{
				resource.setRole(AttendeeRole.MEMBER.name());
			}
			
			resource.setState(PartStat.ACCEPTED.name());
			
			arrlist.add(resource);
		}
	}
	
	/**
	 * 
	 * @param member
	 * @param json
	 */
	public static void memberBuilder(final List<MemberDTO> list, final JSONObject json)
	{
		if (json.has("members"))
		{
			String members = json.getString("members");
			for (String member : members.split(","))
			{
				final MemberDTO resource = new MemberDTO();
				
				final String[] values = member.split(":");
				
				// username
				resource.setUsername(values[0]);
				
				// fullname
				if (values.length > 1 && StringUtils.hasLength(values[1]))
					resource.setFullname(values[1]);
				else 
					resource.setFullname(values[0]);
				
				if (!list.contains(resource))
					list.add(resource);
			}
		}
	}
	
	/**
	 * 
	 * @param aliasBO
	 * @param element
	 * @param params
	 */
	public static void aliasBuilder(String members,
			final List<MemberDTO> list)
	{
		if (StringUtils.hasLength(members))
		{
			// get json object from request
			final JSONObject json = JSONObject.fromObject("{items:" + members +"}");
			
			// attendee builder
			final Object val = json.get("items");
			if (val instanceof JSONArray)
			{
				int size = ((JSONArray)val).size();
				for (int index = 0; index <  size; index++)
				{
					aliasBuilder(list, ((JSONArray)val).getJSONObject(index));
				}
			}
			else if (val instanceof JSONObject) {
				aliasBuilder(list, (JSONObject)val);
			}
		}
	}
	
	/**
	 * 
	 * @param list
	 * @param json
	 */
	public static void aliasBuilder(final List<MemberDTO> list, 
			final JSONObject json)
	{
		if (json != null && 
			json.has("username") &&
			json.has("fullname"))
		{
			final MemberDTO member = new MemberDTO();
			member.setFullname(json.getString("fullname"));
			member.setUsername(json.getString("username"));
			
			if (!list.contains(member))
				list.add(member);
		}
	}
}
