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

import com.inet.xportal.calbuilder.data.AttendeeDTO;
import com.inet.xportal.calbuilder.data.CalendarType;

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
	// reference of firm or subfirm (UUID)
	private String firmUUID;
	private String firmName;
	private String firmPrefix;
	
	// and (optional) in department
	private String departID;
	private String departName;
	
	// uuid of calendar from personal calendar
	// It means, caldav created
	private String caldavRef;
	
	// mode has 3 values {0,1,2}
	private int mode = 0;

	private int year;
	private int month;
	private int week;
	private int day;

	private String subject;
	private String location;
	private String summary;
	// in minutes. i.e from 7:30 = (7 x 60) + 30
	private int startTime;
	// in minutes. i.e to 9:00 = (9 x 60)
	private int toTime;
	
	// type of calendar (3 types supported)
	private String type = CalendarType.ORGANIZATION.name();
	
	// list of members build from request
	private List<AttendeeDTO> members = new ArrayList<AttendeeDTO>();

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the firmUUID
	 */
	public String getFirmUUID() {
		return firmUUID;
	}

	/**
	 * @param firmUUID
	 *            the firmUUID to set
	 */
	public void setFirmUUID(String firmUUID) {
		this.firmUUID = firmUUID;
	}

	/**
	 * @return the firmName
	 */
	public String getFirmName() {
		return firmName;
	}

	/**
	 * @param firmName
	 *            the firmName to set
	 */
	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	/**
	 * @return the firmPrefix
	 */
	public String getFirmPrefix() {
		return firmPrefix;
	}

	/**
	 * @param firmPrefix the firmPrefix to set
	 */
	public void setFirmPrefix(String firmPrefix) {
		this.firmPrefix = firmPrefix;
	}

	/**
	 * @return the departID
	 */
	public String getDepartID() {
		return departID;
	}

	/**
	 * @param departID the departID to set
	 */
	public void setDepartID(String departID) {
		this.departID = departID;
	}

	/**
	 * @return the departName
	 */
	public String getDepartName() {
		return departName;
	}

	/**
	 * @param departName the departName to set
	 */
	public void setDepartName(String departName) {
		this.departName = departName;
	}

	/**
	 * @return the caldavRef
	 */
	public String getCaldavRef() {
		return caldavRef;
	}

	/**
	 * @param caldavRef the caldavRef to set
	 */
	public void setCaldavRef(String caldavRef) {
		this.caldavRef = caldavRef;
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @return the week
	 */
	public int getWeek() {
		return week;
	}

	/**
	 * @param week
	 *            the week to set
	 */
	public void setWeek(int week) {
		this.week = week;
	}

	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @param day
	 *            the day to set
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the startTime
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the toTime
	 */
	public int getToTime() {
		return toTime;
	}

	/**
	 * @param toTime
	 *            the toTime to set
	 */
	public void setToTime(int toTime) {
		this.toTime = toTime;
	}

	/**
	 * @return the members
	 */
	public List<AttendeeDTO> getMembers() {
		return members;
	}

	/**
	 * @param members
	 *            the members to set
	 */
	public void setMembers(List<AttendeeDTO> members) {
		this.members = members;
	}
}
