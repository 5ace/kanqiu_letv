package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class DDUrlsResult implements LetvBaseBean{
	private static final long serialVersionUID = 1L;

	private String[] ddurls ;
	
	private boolean hasHigh ;
	
	private boolean hasLow ;
	
	private int isHd ;
	
	private boolean isDolby ;
	
	private String streamLevel = "13";
	/**
	 * @return the streamLevel
	 */
	public String getStreamLevel() {
		return streamLevel;
	}

	/**
	 * @param streamLevel the streamLevel to set
	 */
	public void setStreamLevel(String streamLevel) {
		this.streamLevel = streamLevel;
	}

	public String[] getDdurls() {
		return ddurls;
	}

	public void setDdurls(String[] ddurls) {
		this.ddurls = ddurls;
	}

	public boolean isHasHigh() {
		return hasHigh;
	}

	public void setHasHigh(boolean hasHigh) {
		this.hasHigh = hasHigh;
	}

	public boolean isHasLow() {
		return hasLow;
	}

	public void setHasLow(boolean hasLow) {
		this.hasLow = hasLow;
	}

	public int isHd() {
		return isHd;
	}

	public void setHd(int isHd) {
		this.isHd = isHd;
	}

	public boolean isDolby() {
		return isDolby;
	}

	public void setDolby(boolean isDolby) {
		this.isDolby = isDolby;
	}
}
