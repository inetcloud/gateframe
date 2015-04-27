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
package com.inet.xportal.calbuilder.reportservice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inet.xportal.calbuilder.BuilderConstant;
import com.inet.xportal.calbuilder.bo.CalElementBO;
import com.inet.xportal.calbuilder.data.WeekReportData;
import com.inet.xportal.calbuilder.model.CalElement;
import com.inet.xportal.nosql.web.data.SearchDTO;
import com.inet.xportal.report.ReportService;
import com.inet.xportal.web.exception.WebOSBOException;
import com.inet.xportal.web.util.XParamUtils;

/**
 * ReportCalBuilderWeek.
 *
 * @author Hien Nguyen
 * @version $Id: ReportCalBuilderWeek.java Apr 23, 2015 4:27:48 PM nguyen_dv $
 *
 * @since 1.0
 */
@Named("ReportCalBuilderDOWeek")
public class ReportCalBuilderDOWeek implements ReportService {
	private static final Logger logger = LoggerFactory.getLogger(ReportCalBuilderDOWeek.class);
	@Inject
	private CalElementBO elementBO;
	
	/* (non-Javadoc)
	 * @see com.inet.xportal.report.ReportService#invoke(java.util.Map)
	 */
	@Override
	public Object invoke(final Map<String, Object> params) throws WebOSBOException {
		// report parameter conducting
		final Calendar cal = Calendar.getInstance();
		String deptID = XParamUtils.getString("dept", params);
		logger.debug("Department ID : {}", deptID);
		
		int year = XParamUtils.getInteger("year", params, cal.get(Calendar.YEAR));
		int week = XParamUtils.getInteger("week", params, cal.get(Calendar.WEEK_OF_YEAR));
		
		if (!StringUtils.hasLength(deptID))
			deptID = BuilderConstant.PUBLISHED_SHOW;
		
		SearchDTO<CalElement> result = null;
		if (!StringUtils.hasLength(deptID))
			result = elementBO.queryByMainboard(year, week, -1, 0);
		else if (XParamUtils.getBoolean("published", params, true))
			result = elementBO.queryByPublished(deptID,year, week, -1, 0);
		else
			result = elementBO.queryByReviewed(deptID,year, week, -1,0);
		
		// result builder
		final List<WeekReportData> report = new ArrayList<WeekReportData>();
		if (result != null && result.getTotal() > 0)
		{
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.WEEK_OF_YEAR,week);
			
			for (CalElement item : result.getItems())
			{
				cal.set(Calendar.DAY_OF_YEAR, item.getDay());
				WeekReportData element = new WeekReportData();
				
				// day of week
				int doweek = cal.get(Calendar.DAY_OF_WEEK);
				if (doweek == Calendar.MONDAY)
					element.setDayofweek("MONDAY");
				else if (doweek == Calendar.TUESDAY)
					element.setDayofweek("TUESDAY");
				else if (doweek == Calendar.WEDNESDAY)
					element.setDayofweek("WEDNESDAY");
				else if (doweek == Calendar.THURSDAY)
					element.setDayofweek("THURSDAY");
				else if (doweek == Calendar.FRIDAY)
					element.setDayofweek("FRIDAY");
				else if (doweek == Calendar.SATURDAY)
					element.setDayofweek("SATURDAY");
				else 
					element.setDayofweek("SUNDAY");
				
				int index = report.indexOf(element);
				if (index >= 0)
					element = report.get(index);
				
				if (item.getStartTime() >  720)
					element.getPmdata().add(item);
				else
					element.getAmdata().add(item);
				
				if (index < 0)
					report.add(element);
				else
					report.set(index,element);
			}
		}
		
		return report;
	}
}
