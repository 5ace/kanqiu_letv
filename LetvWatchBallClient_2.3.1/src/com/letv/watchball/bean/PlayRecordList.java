package com.letv.watchball.bean;

import java.util.ArrayList;

import com.letv.http.bean.LetvBaseBean;


/**
 * 保存播放记录实体,用来保存播放记录
 *
 */
public class PlayRecordList extends ArrayList<PlayRecord> implements LetvBaseBean{

	private static final long serialVersionUID = 1L;
	
	private int page ;
	
	private int pagesize ;
	
	private int total ;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
