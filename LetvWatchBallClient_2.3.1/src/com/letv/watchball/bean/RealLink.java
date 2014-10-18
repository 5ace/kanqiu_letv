package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;
/**
 * 真实的播放地址
 * @author 
 *
 */
public class RealLink  implements LetvBaseBean {
	private String location;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return location;
	}
}
