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

/**
 * 
 * AttendeeDTO.
 *
 * @author Hien Nguyen
 * @version $Id: AttendeeDTO.java Apr 23, 2015 9:00:15 AM nguyen_dv $
 *
 * @since 1.0
 */
public class AttendeeDTO {
	private String code;
	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj) {
	    if (obj == null || !(obj instanceof AttendeeDTO))
	    	return false;
	    // usercode is unique
	    return code.equals(((AttendeeDTO)obj).code);
    }
}
