package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class ShareAlbum implements LetvBaseBean{

	private int Share_id;
	
	private int Share_vid;
	
	private String Share_AlbumName;
	
	private int order=1;
	
	private String icon;
	
	private int type=1;
	
	private int cid;
	
	private String year;

	private int timeLength;

	private String director;

	private String actor;
	
	private int albumid;
	
	public int getShare_id() {
		return Share_id;
	}
	public void setShare_id(int share_id) {
		Share_id = share_id;
	}
	public int getShare_vid() {
		return Share_vid;
	}
	public void setShare_vid(int share_vid) {
		Share_vid = share_vid;
	}
	public String getShare_AlbumName() {
		return Share_AlbumName;
	}
	public void setShare_AlbumName(String share_AlbumName) {
		Share_AlbumName = share_AlbumName;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getYear() {
		return this.year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public long getTimeLength() {
		return this.timeLength;
	}
	public void setTimeLength(int timeLength) {
		this.timeLength = timeLength;
	}
	public String getDirector() {
		return this.director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getActor() {
		return this.actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public int getAlbumid() {
		return this.albumid;
	}
	public void setAlbumid(int albumid) {
		this.albumid = albumid;
	}
	
	
}
