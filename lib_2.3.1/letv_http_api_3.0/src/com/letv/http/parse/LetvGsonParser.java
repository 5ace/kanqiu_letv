package com.letv.http.parse;

import java.lang.reflect.Modifier;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.letv.http.bean.LetvBaseBean;

public class LetvGsonParser<T extends LetvBaseBean> extends LetvBaseParser<T, String>{

	/**
	 * 接口信息节点
	 * */
	protected final String HEADER = "header" ;
	/**
	 * 接口返回状态：1-正常，2-无数据，3-服务异常
	 * */
	protected final String STATUS = "status" ;
	/**
	 * 接口时间轴
	 * */
	protected final String MARKID = "markid" ;
	/**
	 * 接口返回数据节点
	 * */
	protected final String BODY = "body" ;
	
	public interface STATE{
		public int NORMAL = 1;
		public int NODATA = 2;
		public int EXCEPTION = 3;
		public int NOUPDATE = 4;
	}
	
	/**
	 * 借口状态
	 * */
	private int status ;
	
	/**
	 * 接口时间轴
	 * */
	private String markid ;
	
	static GsonBuilder sBuilder;
	static Gson sGson;

	static {
		sBuilder = new GsonBuilder();
		sBuilder.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.VOLATILE, Modifier.PRIVATE);
		sGson = sBuilder.create();
	}
	private Class<T> cls;
	public LetvGsonParser(int from,Class<T> cls) {
		super(from);
		this.cls = cls;
	}

	@Override
	public T parse(String data) throws Exception {
		T t;
		try {
			t = sGson.fromJson(data, cls);
		} catch (Exception e) {
			throw e;
		}
		return t;
	}

	@Override
	protected boolean canParse(String data) {
//		try {
//			JSONObject object = new JSONObject(data);
//			if(!object.has(HEADER)) {
//				return false ;
//			}
//			JSONObject headJson =  object.getJSONObject(HEADER);
//			status = headJson.getInt(STATUS);
//			
//			if(status == STATE.NORMAL){
//				if(headJson.has(MARKID)){
//					markid = headJson.getString(MARKID);
//				}
//				return true ;
//			}
//			
//			if(status == STATE.NOUPDATE){
//				return true ;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return false;
		//不对Header信息做判断，直接解析出来 by lhz 
		return true;
	}

	@Override
	protected String getData(String data) throws Exception {
		return data;
	}

	
}