package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class AdJoiningBean implements LetvBaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5308650296994173109L;

	/**
	 * 前贴广告拼接状态，列表中每个元素为 每个广告拼接状态，数字大于0 为成功， 等于0为失败
	 */
	private String ahs;
	/**
	 * 视频拼装状态  列表中每个元素为 每个广告拼接状态，数字大于0 为成功， 等于0为失败
	 */
	private String vs;
	/**
	 * 后贴广告拼接状态   列表中每个元素为 每个广告拼接状态，数字大于0 为成功， 等于0为失败
	 */
	private String ats;
	/**
	 * 播放地址
	 */
	private String muri;
	public String getAhs() {
		return ahs;
	}
	public void setAhs(String ahs) {
		this.ahs = ahs;
	}
	public String getVs() {
		return vs;
	}
	public void setVs(String vs) {
		this.vs = vs;
	}
	public String getAts() {
		return ats;
	}
	public void setAts(String ats) {
		this.ats = ats;
	}
	public String getMuri() {
		return muri;
	}
	public void setMuri(String muri) {
		this.muri = muri;
	}
	/**
	 * 前贴片是否拼接成功
	 * @return
	 */
	public boolean isAhsSuccess(){
		try {
			ahs.replace(" ", "");
			String ahStr[] = ahs.split(",");
			for (String item : ahStr) {
				if(Integer.parseInt(item) == 0){
					return false;
				}
			}
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	/**
	 * 视频是否拼接成功
	 * @return
	 */
	public boolean isVsSuccess(){
		try {
			vs.replace(" ", "");
			String vsStr[] = vs.split(",");
			for (String item : vsStr) {
				if(Integer.parseInt(item) == 0){
					return false;
				}
			}
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	/**
	 * 视频是否拼接成功
	 * @return
	 */
	public boolean isAtsSuccess(){
		try {
			ats.replace(" ", "");
			String atsStr[] = ats.split(",");
			for (String item : atsStr) {
				if(Integer.parseInt(item) == 0){
					return false;
				}
			}
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
}
