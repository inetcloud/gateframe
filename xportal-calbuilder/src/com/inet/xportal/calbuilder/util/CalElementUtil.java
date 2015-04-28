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

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.data.AttendeeDTO;
import com.inet.xportal.calbuilder.data.AttendeeRole;
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
	 * @param attribute
	 * @param params
	 */
	public static void attributeUpdate(final JSONObject attribute, 
			final Map<String, Object> params)
	{
		for (String param : params.keySet())
		{
			final Object value = params.get(param);
			if (param.startsWith("_"))
			{
				if (value != null)
				{
					attribute.put(param.substring(1), value);
				}
				else if (attribute.has(param.substring(1)))
				{
					attribute.remove(param.substring(1));
				}
			}
		}
	}
	
	/**
	 * 
	 * @param arrlist
	 * @param params
	 */
	public static void attendeeUpdate(final List<AttendeeDTO> arrlist, final Map<String, Object> params)
	{
		if (params.containsKey("attendee"))
		{
			arrlist.clear();
			// get members of this project
			String members = XParamUtils.getString("attendee", params);
			if (StringUtils.hasLength(members))
			{
				// get json object from request
				final JSONObject json = JSONObject.fromObject("{items:" + members +"}");
				
				// attendee builder
				attendeeUpdate(arrlist, json.get("items"));
			}
		}
	}
	
	/**
	 * 
	 * @param arrlist
	 * @param val
	 */
	public static void attendeeUpdate(final List<AttendeeDTO> arrlist, final Object val)
	{
		if (val instanceof JSONArray)
		{
			int size = ((JSONArray)val).size();
			for (int index = 0; index <  size; index++)
			{
				attendeeBuilder(arrlist, ((JSONArray)val).getJSONObject(index));
			}
		}
		else if (val instanceof JSONObject) {
			attendeeBuilder(arrlist, (JSONObject)val);
		}
	}
	
	/**
	 * 
	 * @param item
	 * @param json
	 * @return
	 */
	public static void attendeeBuilder(final List<AttendeeDTO> arrlist, final JSONObject json)
	{
		final AttendeeDTO resource = new AttendeeDTO();
		resource.setCode(json.getString("usercode"));
		
		// remove current resource in this list
		arrlist.remove(resource);
		
		// name of resource
		if (json.has("name"))
			resource.setName(json.getString("name"));
		
		// role data for this resource
		if (json.has("role"))
		{
			String role = json.getString("role");
			if (AttendeeRole.MEMBER.name().equalsIgnoreCase(role))
				resource.setRole(AttendeeRole.MEMBER.name());
			else if (AttendeeRole.CHAIRMAN.name().equalsIgnoreCase(role))
				resource.setRole(AttendeeRole.CHAIRMAN.name());
			else
				resource.setRole(AttendeeRole.OBSERVER.name());
		}
		
		arrlist.add(resource);
	}
}
