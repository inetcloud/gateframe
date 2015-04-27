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
package com.inet.xportal.calbuilder.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.shiro.util.StringUtils;

import com.inet.xportal.calbuilder.data.AttendeeDTO;

/**
 * CalElement.
 * 
 * @author Hien Nguyen
 * @version $Id: CalElement.java Apr 23, 2015 8:49:06 AM nguyen_dv $
 * 
 * @since 1.0
 */
public class CalElement {
	private String uuid;
	
	// name of department (ref to CalDept)
	private String deptUUID;
	// limit of show this calendar
	// if value is BuilderConstant.PUBLISHED_SHOW, this element
	// will be displayed from main board
	private String scopeShow = StringUtils.EMPTY_STRING;
	// status of this element (to be in reviewed list or published)
	private boolean published = false;
	private int year;
	private int month;
	private int week;
	private int day;
	// in minutes. i.e from 7:30 = (7 x 60) + 30
	private int startTime;
	// in minutes. i.e to 9:00 = (9 x 60)
	private int toTime;
	private String creatorCode;
	private String creatorName;
	
	private String chairmanCode;
	private String chairmanName;
	
	// content prepare owner (who/ firm will be played as content master)
	private String contentOwner;
	private String subject;
	private String location;
	private String summary;
	// more attributes needed
	private JSONObject attributes = new JSONObject();
	
	// members who attend to this meeting
	private String memberBrief;
	// members who observe this meeting
	private String observerBrief;
	
	// list of members build from request
	private List<AttendeeDTO> members = new ArrayList<AttendeeDTO>();

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDeptUUID() {
		return deptUUID;
	}

	public void setDeptUUID(String deptUUID) {
		this.deptUUID = deptUUID;
	}

	public String getScopeShow() {
		return scopeShow;
	}

	public void setScopeShow(String scopeShow) {
		this.scopeShow = scopeShow;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getCreatorCode() {
		return creatorCode;
	}

	public void setCreatorCode(String creatorCode) {
		this.creatorCode = creatorCode;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getToTime() {
		return toTime;
	}

	public void setToTime(int toTime) {
		this.toTime = toTime;
	}

	public String getChairmanCode() {
		return chairmanCode;
	}

	public void setChairmanCode(String chairmanCode) {
		this.chairmanCode = chairmanCode;
	}

	public String getChairmanName() {
		return chairmanName;
	}

	public void setChairmanName(String chairmanName) {
		this.chairmanName = chairmanName;
	}

	public String getContentOwner() {
		return contentOwner;
	}

	public void setContentOwner(String contentOwner) {
		this.contentOwner = contentOwner;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public JSONObject getAttributes() {
		return attributes;
	}

	public void setAttributes(JSONObject attributes) {
		this.attributes = attributes;
	}

	public String getMemberBrief() {
		return memberBrief;
	}

	public void setMemberBrief(String memberBrief) {
		this.memberBrief = memberBrief;
	}

	public List<AttendeeDTO> getMembers() {
		return members;
	}

	public void setMembers(List<AttendeeDTO> members) {
		this.members = members;
	}

	public String getObserverBrief() {
		return observerBrief;
	}

	public void setObserverBrief(String observerBrief) {
		this.observerBrief = observerBrief;
	}
}
