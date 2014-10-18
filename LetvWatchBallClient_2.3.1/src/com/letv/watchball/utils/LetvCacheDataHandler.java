package com.letv.watchball.utils;

import java.util.List;

import com.letv.watchball.bean.LocalCacheBean;

public class LetvCacheDataHandler {

	/**
	 * 读取首页数据
	 * */
	public static LocalCacheBean readHomePageData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("HomeData");
		return bean ;
	}
	
	/**
	 * 保存首页数据
	 * */
	public static LocalCacheBean saveHomePageData(String markId , String data){
		LocalCacheBean bean = new LocalCacheBean("HomeData", markId, data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取频道列表数据
	 * */
	public static LocalCacheBean readChannelsData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("Channels");
		return bean ;
	}
	
	/**
	 * 保存频道列表数据
	 * */
	public static LocalCacheBean saveChannelsData(String markId , String data){
		LocalCacheBean bean = new LocalCacheBean("Channels", markId, data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取频道筛选数据
	 * */
	public static LocalCacheBean readChannelsSiftData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("ChannelsSift");
		LogInfo.log("-------readChannelsSiftData----------");
		return bean ;
	}
	
	/**
	 * 保存频道筛选数据
	 * */
	public static LocalCacheBean saveChannelsSiftData(String markId , String data){
		LocalCacheBean bean = new LocalCacheBean("ChannelsSift", markId, data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取VIP频道筛选数据
	 * */
	public static LocalCacheBean readVipChannelSiftData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("VipChannelSift");
		return bean ;
	}
	
	/**
	 * 保存VIP频道筛选数据
	 * */
	public static LocalCacheBean saveVipChannelSiftData(String markId , String data){
		LocalCacheBean bean = new LocalCacheBean("VipChannelSift", markId, data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取专题数据
	 * */
	public static LocalCacheBean readSpecialsData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("Specials");
		return bean ;
	}
	
	/**
	 * 保存专题数据
	 * */
	public static LocalCacheBean saveSpecialsData(String markId , String data){
		LocalCacheBean bean = new LocalCacheBean("Specials", markId, data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取排行数据
	 * */
	public static LocalCacheBean readTopsData(String id){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("tops" + id);
		return bean ;
	}
	
	/**
	 * 保存排行数据
	 * */
	public static LocalCacheBean saveTopsData(String markId , String data , String id){
		LocalCacheBean bean = new LocalCacheBean("tops" + id, markId, data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取频道列表数据
	 * */
	public static LocalCacheBean readChannelListData(String im , String cid , String itemid , String date , String areaid , String typeid , String orderby , String sort , String start , String num){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("channellist");
		buffer.append("&");
		buffer.append(im);
		buffer.append("&");
		buffer.append(cid);
		buffer.append("&");
		buffer.append(itemid);
		buffer.append("&");
		buffer.append(date);
		buffer.append("&");
		buffer.append(areaid);
		buffer.append("&");
		buffer.append(typeid);
		buffer.append("&");
		buffer.append(orderby);
		buffer.append("&");
		buffer.append(sort);
		buffer.append("&");
		buffer.append(start);
		buffer.append("&");
		buffer.append(num);
		LogInfo.log("readChannelListData="+buffer.toString());
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData(buffer.toString());
		return bean ;
	}
	
	/**
	 * 保存频道列表数据
	 * */
	public static LocalCacheBean saveChannelListData(String markId , String data , String im , String cid , String itemid , String date , String areaid , String typeid , String orderby , String sort , String start , String num){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("channellist");
		buffer.append("&");
		buffer.append(im);
		buffer.append("&");
		buffer.append(cid);
		buffer.append("&");
		buffer.append(itemid);
		buffer.append("&");
		buffer.append(date);
		buffer.append("&");
		buffer.append(areaid);
		buffer.append("&");
		buffer.append(typeid);
		buffer.append("&");
		buffer.append(orderby);
		buffer.append("&");
		buffer.append(sort);
		buffer.append("&");
		buffer.append(start);
		buffer.append("&");
		buffer.append(num);
		
		LocalCacheBean bean = new LocalCacheBean(buffer.toString(), markId==null?"":markId, data, System.currentTimeMillis());
		LogInfo.log("beanToString = "+bean.toString());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取详情数据
	 * */
	public static LocalCacheBean readDetailData(String id){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("detail" + id);
		return bean ;
	}
	
	/**
	 * 保存详情数据
	 * */
	public static LocalCacheBean saveDetailData(String markId , String data , String id){
		LocalCacheBean bean = new LocalCacheBean("detail" + id, markId, data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取详情剧集数据
	 * */
	public static LocalCacheBean readDetailVLData(String id , String b , String s , String o , String m){
		StringBuffer buffer = new StringBuffer();
		buffer.append("detailvl");
		buffer.append("&");
		buffer.append(id);
		buffer.append("&");
		buffer.append(b);
		buffer.append("&");
		buffer.append(s);
		buffer.append("&");
		buffer.append(o);
		buffer.append("&");
		buffer.append(m);
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData(buffer.toString());
		LogInfo.log("readDetailVLData--->"+buffer.toString());
		return bean ;
	}
	
	/**
	 * 保存详情剧集数据
	 * */
	public static LocalCacheBean saveDetailVLData(String markId , String data , String id , String b , String s , String o , String m){
		StringBuffer buffer = new StringBuffer();
		buffer.append("detailvl");
		buffer.append("&");
		buffer.append(id);
		buffer.append("&");
		buffer.append(b);
		buffer.append("&");
		buffer.append(s);
		buffer.append("&");
		buffer.append(o);
		buffer.append("&");
		buffer.append(m);
		LocalCacheBean bean = new LocalCacheBean(buffer.toString(), markId, data, System.currentTimeMillis());
		bean.setAssistKey(id);
		LocalCacheTool.getInstance().writeCacheData(bean);
		LogInfo.log("saveDetailVLData--->"+buffer.toString()+"--bean");
		return bean ;
	}
	
	/**
	 * 读取详情剧集数据(根据专辑ID得到所有视频别表)
	 * */
	public static List<LocalCacheBean> readDetailVLData(String id){
		List<LocalCacheBean> bean = LocalCacheTool.getInstance().readCacheDataByAssistKey(id);
		return bean ;
	}
	
	/**
	 * 读取VIP频道列表数据
	 * */
	public static LocalCacheBean readVIPChannelListData(String cid, String itemid, String date, String areaid, String orderby, String sort, String start, String num, String pf, String allowmonth){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("vipchannellist");
		buffer.append("&");
		buffer.append(cid);
		buffer.append("&");
		buffer.append(itemid);
		buffer.append("&");
		buffer.append(date);
		buffer.append("&");
		buffer.append(areaid);
		buffer.append("&");
		buffer.append(orderby);
		buffer.append("&");
		buffer.append(sort);
		buffer.append("&");
		buffer.append(start);
		buffer.append("&");
		buffer.append(num);
		buffer.append("&");
		buffer.append(pf);
		buffer.append("&");
		buffer.append(allowmonth);
		
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData(buffer.toString());
		return bean ;
	}
	
	/**
	 * 保存VIP频道列表数据
	 * */
	public static LocalCacheBean saveVIPChannelListData(String markId , String data , String cid, String itemid, String date, String areaid, String orderby, String sort, String start, String num, String pf, String allowmonth){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("vipchannellist");
		buffer.append("&");
		buffer.append(cid);
		buffer.append("&");
		buffer.append(itemid);
		buffer.append("&");
		buffer.append(date);
		buffer.append("&");
		buffer.append(areaid);
		buffer.append("&");
		buffer.append(orderby);
		buffer.append("&");
		buffer.append(sort);
		buffer.append("&");
		buffer.append(start);
		buffer.append("&");
		buffer.append(num);
		buffer.append("&");
		buffer.append(pf);
		buffer.append("&");
		buffer.append(allowmonth);
		
		LocalCacheBean bean = new LocalCacheBean(buffer.toString(), markId, data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	
	
	

	/**
	 * 读取首页直播数据
	 * */
	public static LocalCacheBean readHomeLiveData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("HomeLive");
		return bean ;
	}
	
	/**
	 * 保存首页直播数据
	 * */
	public static LocalCacheBean saveHomeLiveData(String data){
		LocalCacheBean bean = new LocalCacheBean("HomeLive", 0+"", data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取首页新闻数据
	 * */
	public static LocalCacheBean readHomeNewsData(String currentOrderBy,String itemId){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("HomeNews_"+currentOrderBy+"_"+itemId);
		return bean ;
	}
	
	/**
	 * 保存首页新闻数据
	 * */
	public static LocalCacheBean saveHomeNewsData(String data,String currentOrderBy,String itemId){
		LocalCacheBean bean = new LocalCacheBean("HomeNews_"+currentOrderBy+"_"+itemId, 0+"", data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	/**
	 * 读取 焦点图
	 * */
	public static LocalCacheBean readFocusPicInfo(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("FocusPicInfo");
		return bean ;
	}
	
	/**
	 * 保存 焦点图
	 * */
	public static LocalCacheBean saveFocusPicInfo(String data){
		LocalCacheBean bean = new LocalCacheBean("FocusPicInfo", 0+"", data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取新闻筛选数据
	 * */
	public static LocalCacheBean readVideoTypesData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("VideoTypes");
		return bean ;
	}
	
	/**
	 * 保存新闻筛选数据
	 * */
	public static LocalCacheBean saveVideoTypesData(String data){
		LocalCacheBean bean = new LocalCacheBean("VideoTypes", 0+"", data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取赛事列表数据
	 * */
	public static LocalCacheBean readMatchListData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("MatchList");
		return bean ;
	}
	
	/**
	 * 保存赛事列表数据
	 * */
	public static LocalCacheBean saveMatchListData(String data){
		LocalCacheBean bean = new LocalCacheBean("MatchList", 0+"", data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取直播订阅数据
	 * */
	public static LocalCacheBean readSubscribeListData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("SubscribeList");
		return bean ;
	}
	
	/**
	 * 保存直播订阅数据
	 * */
	public static LocalCacheBean saveSubscribeListData(String data){
		LocalCacheBean bean = new LocalCacheBean("SubscribeList", 0+"", data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取我的球队列表数据
	 * */
	public static LocalCacheBean readMyTeamsData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("MyTeams");
		return bean ;
	}
	
	/**
	 * 保存我的球队列表数据
	 * */
	public static LocalCacheBean saveMyTeamsData(String data){
		LocalCacheBean bean = new LocalCacheBean("MyTeams", 0+"", data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
	/**
	 * 读取所有可关注球队列表数据
	 * */
	public static LocalCacheBean readGetFocusTeamData(){
		LocalCacheBean bean = LocalCacheTool.getInstance().readCacheData("GetFocusTeam");
		return bean ;
	}
	
	/**
	 * 保存所有可关注球队列表数据
	 * */
	public static LocalCacheBean saveGetFocusTeamData(String data){
		LocalCacheBean bean = new LocalCacheBean("GetFocusTeam", 0+"", data, System.currentTimeMillis());
		LocalCacheTool.getInstance().writeCacheData(bean);
		return bean ;
	}
	
}
