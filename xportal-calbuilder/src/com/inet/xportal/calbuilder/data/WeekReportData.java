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

import java.util.ArrayList;
import java.util.List;

import com.inet.xportal.calbuilder.model.CalElement;

/**
 * WeekReportData.
 * 
 * @author Hien Nguyen
 * @version $Id: WeekReportData.java Apr 27, 2015 1:57:21 PM nguyen_dv $
 * 
 * @since 1.0
 */
public class WeekReportData {
	private String dayofweek;
	private List<CalElement> amdata = new ArrayList<CalElement>();
	private List<CalElement> pmdata = new ArrayList<CalElement>();

	public String getDayofweek() {
		return dayofweek;
	}

	public void setDayofweek(String dayofweek) {
		this.dayofweek = dayofweek;
	}

	public List<CalElement> getAmdata() {
		return amdata;
	}

	public void setAmdata(List<CalElement> amdata) {
		this.amdata = amdata;
	}

	public List<CalElement> getPmdata() {
		return pmdata;
	}

	public void setPmdata(List<CalElement> pmdata) {
		this.pmdata = pmdata;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof WeekReportData))
			return false;
		
		final WeekReportData other = (WeekReportData)obj;
	    return other.dayofweek.equals(dayofweek);
    }
	
	
}
