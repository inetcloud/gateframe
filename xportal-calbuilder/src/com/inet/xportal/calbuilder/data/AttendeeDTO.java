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
package com.inet.xportal.calbuilder.data;

import com.inet.xportal.calendar.data.PartStat;

/**
 * 
 * AttendeeDTO.
 * 
 * @author Hien Nguyen
 * @version $Id: AttendeeDTO.java Apr 23, 2015 9:00:15 AM nguyen_dv $
 * 
 * @since 1.0
 */
public class AttendeeDTO extends MemberAlias {
	private String state = PartStat.ACCEPTED.name();
	// role of each member in this calendar
	private String role = AttendeeRole.MEMBER.name();
	
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}
}
