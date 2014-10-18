package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class Video implements LetvBaseBean {

	private static final long serialVersionUID = 1L;

	private long id;

	private String nameCn;

	private String subTitle;

	private String pic;

	private long btime;

	private long etime;

	private int cid;

	private long pid;

	private int type;

	private int at;

	private String releaseDate;

	private long duration;

	private int style;

	private int play;

	private int jump;

	private int pay;

	private int download;

	private String description;

	private String controlAreas;

	private int disableType;

	private String mid;

	private String brList;

	private int episode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNameCn() {
		return nameCn;
	}

	public void setNameCn(String nameCn) {
		this.nameCn = nameCn;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public long getBtime() {
		return btime;
	}

	public void setBtime(long btime) {
		this.btime = btime;
	}

	public long getEtime() {
		return etime;
	}

	public void setEtime(long etime) {
		this.etime = etime;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAt() {
		return at;
	}

	public void setAt(int at) {
		this.at = at;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getPlay() {
		return play;
	}

	public void setPlay(int play) {
		this.play = play;
	}
	
	public boolean canPlay(){
		return this.play == 1 ;
	}

	public int getJump() {
		return jump;
	}

	public void setJump(int jump) {
		this.jump = jump;
	}

	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}

	public int getDownload() {
		return download;
	}
	
	public boolean canDownload() {
		return download == 1;
	}

	public void setDownload(int download) {
		this.download = download;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getControlAreas() {
		return controlAreas;
	}

	public void setControlAreas(String controlAreas) {
		this.controlAreas = controlAreas;
	}

	public int getDisableType() {
		return disableType;
	}

	public void setDisableType(int disableType) {
		this.disableType = disableType;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getBrList() {
		return brList;
	}

	public void setBrList(String brlist) {
		this.brList = brlist;
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}
	
	public boolean needJump() {
		return jump == 1;
	}

	public boolean needPay() {
		return pay == 1;
	}
}
