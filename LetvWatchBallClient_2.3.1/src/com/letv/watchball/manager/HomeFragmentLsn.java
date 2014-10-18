package com.letv.watchball.manager;

import com.letv.watchball.bean.Game;

/**
 * 中间Fragment的回调时间注册
 * @author liuhanzhi
 *
 */
public interface HomeFragmentLsn {
	
	/**
	 * 预定一场比赛
	 * @param game 比赛game	
	 * @param date 比赛日期
	 */
	public void addSubscribe(Game game,String date);
	/**
	 * 取消预订一场比赛
	 * @param id
	 */
	public void removeSubscribe(String id);
	
	public void toggleRight();
	public void loadRightFragmentData();
	
	/**
	 * 重新加载所有数据(网络异常时)
	 */
	public void reloadAllDatas();
}
