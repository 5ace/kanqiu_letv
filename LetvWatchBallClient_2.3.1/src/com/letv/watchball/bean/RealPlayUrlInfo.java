package com.letv.watchball.bean;

import android.text.TextUtils;
import com.letv.http.bean.LetvBaseBean;

public class RealPlayUrlInfo implements LetvBaseBean {

	private String ddUrl = null;

	private String realUrl = null;

	private String geo = null;

	private boolean isIpValid = false;

	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDdUrl() {
		return ddUrl;
	}

	public void setDdUrl(String ddUrl) {
		this.ddUrl = ddUrl;
	}

	public String getRealUrl() {
		return realUrl;
	}

	public void setRealUrl(String realUrl) {
		this.realUrl = realUrl;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;

		if (!TextUtils.isEmpty(geo)) {
			setIpValid(geo.trim().toUpperCase().startsWith("CN"));
		}
	}

	public boolean isIpValid() {
		return isIpValid;
	}

	public void setIpValid(boolean isIpValid) {
		this.isIpValid = isIpValid;
	}
}
