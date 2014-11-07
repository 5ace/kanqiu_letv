package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class PushMsgBean implements LetvBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1212154587L;
	public class Pushmsg {

		private String id;
		private String time;
		private String title;
		private String msg;
		private String type;
		private String at;
		private String resid;
		private String needJump;
		private String liveEndDate;
		private String cid;
		private String picUrl;
		private String isActivate;
		private String isOnDeskTop;
		private String assist;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getAt() {
			return at;
		}
		public void setAt(String at) {
			this.at = at;
		}
		public String getResid() {
			return resid;
		}
		public void setResid(String resid) {
			this.resid = resid;
		}
		public String getNeedJump() {
			return needJump;
		}
		public void setNeedJump(String needJump) {
			this.needJump = needJump;
		}
		public String getLiveEndDate() {
			return liveEndDate;
		}
		public void setLiveEndDate(String liveEndDate) {
			this.liveEndDate = liveEndDate;
		}
		public String getCid() {
			return cid;
		}
		public void setCid(String cid) {
			this.cid = cid;
		}
		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		public String getIsActivate() {
			return isActivate;
		}
		public void setIsActivate(String isActivate) {
			this.isActivate = isActivate;
		}
		public String getIsOnDeskTop() {
			return isOnDeskTop;
		}
		public void setIsOnDeskTop(String isOnDeskTop) {
			this.isOnDeskTop = isOnDeskTop;
		}
		public String getAssist() {
			return assist;
		}
		public void setAssist(String assist) {
			this.assist = assist;
		}
	}
	public class Body {
		private Pushmsg pushmsg;

		public Pushmsg getPushmsg() {
			return pushmsg;
		}

		public void setPushmsg(Pushmsg pushmsg) {
			this.pushmsg = pushmsg;
		}
	};
	public class Header {
		private String markid;
		private String status;
		public String getMarkid() {
			return markid;
		}
		public void setMarkid(String markid) {
			this.markid = markid;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	};
	public Body body;
	public Header header;
	public Body getBody() {
		return body;
	}
	public void setBody(Body body) {
		this.body = body;
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	
}
