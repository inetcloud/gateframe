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
	
	private int year;
	private int month;
	private int week;
	private int day;
	private String creatorCode;
	private String creatorName;
	// in minutes. i.e from 7:30 = (7 x 60) + 30
	private int startTime;
	// in minutes. i.e to 9:00 = (9 x 60)
	private int toTime;

	private String organCode;
	private String organName;
	//
	private String contentOwner;
	private String subject;
	private String location;
	private String summary;
	// more attributes needed
	private JSONObject attributes = new JSONObject();
	
	// members who attend to this meeting
	private String memberBrief;
	private List<AttendeeDTO> members = new ArrayList<AttendeeDTO>();

	// members who observe this meeting
	private String observerBrief;
	private List<AttendeeDTO> observers = new ArrayList<AttendeeDTO>();

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

	public String getOrganCode() {
		return organCode;
	}

	public void setOrganCode(String organCode) {
		this.organCode = organCode;
	}

	public String getOrganName() {
		return organName;
	}

	public void setOrganName(String organName) {
		this.organName = organName;
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

	public List<AttendeeDTO> getObservers() {
		return observers;
	}

	public void setObservers(List<AttendeeDTO> observers) {
		this.observers = observers;
	}
}
