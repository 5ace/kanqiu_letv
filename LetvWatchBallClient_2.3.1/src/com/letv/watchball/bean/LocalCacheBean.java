package com.letv.watchball.bean;

public class LocalCacheBean {
	private String cacheId;// 缓存id
	private String markId;
	private String cacheData;// 缓存数据
	private long cacheTime;// 缓存时间
	private String assistKey; // 辅助KEY

	public String getCacheId() {
		return cacheId;
	}

	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getCacheData() {
		return cacheData;
	}

	public void setCacheData(String cacheData) {
		this.cacheData = cacheData;
	}

	public long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	public String getAssistKey() {
		return assistKey;
	}

	public void setAssistKey(String assistKey) {
		this.assistKey = assistKey;
	}

	public LocalCacheBean(String cacheId, String markId, String cacheData,
			long cacheTime) {
		super();
		this.cacheId = cacheId;
		this.markId = markId;
		this.cacheData = cacheData;
		this.cacheTime = cacheTime;
	}

	public LocalCacheBean() {
	}

	@Override
	public String toString() {
		return "LocalCacheBean [cacheId=" + cacheId + ", markId=" + markId
				+ ", cacheData=" + cacheData.length() + ", cacheTime="
				+ cacheTime + "]";
	}

}
