package com.letv.star.net;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;

public class HttpUtil {
	
	
	
	public static String doGet(Context context , String url, Bundle params){
		
		if(!isNetworkConnected(context)){
			return null ;
		}
		
		if(params != null && params.size() > 0){
			url += encodeUrl(params);
		}
		
		String data = "";
		
		try{
			HttpGet httpGet = new HttpGet(url);
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,5000);
			HttpConnectionParams.setSoTimeout(httpParams,5000);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
				throw new Exception();
			}
			
			HttpEntity httpEntity = httpResponse.getEntity();
			data = EntityUtils.toString(httpEntity);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static String doPost(Context context ,String url, Bundle params){
		
		if(!isNetworkConnected(context)){
			return null ;
		}
		
		String data = "";
		try{
			HttpPost httpPost = new HttpPost(url);
			
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,5000);
			HttpConnectionParams.setSoTimeout(httpParams,5000);
			
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			
			if(params != null && params.size() > 0){
				httpPost.setEntity(new UrlEncodedFormEntity(bundle2NameValuePair(params) , HTTP.UTF_8));
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
				throw new Exception();
			}
			
			HttpEntity entity = httpResponse.getEntity();
			data = EntityUtils.toString(entity);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return data ;
	}
	
	
	public static ArrayList<NameValuePair> bundle2NameValuePair(Bundle params){
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>() ;
		for (String key : params.keySet()) {
			NameValuePair pair = new BasicNameValuePair(key, params.getString(key));
			parameters.add(pair);
		}
		
		return parameters ;
	}
	
	
    /**
     * 将Key-value转换成用&号链接的URL查询参数形式。
     * 
     * @param parameters
     * @return
     */
    public static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(key + "=" + URLEncoder.encode(parameters.getString(key)));
        }
        return sb.toString();
    }
    
	/**
	 * 检查当前网络连接状态
	 * @param
	 * 		调用此方法的Context
	 * @return 
	 * 		true - 有可用的网络连接（3G/GSM、wifi等）
	 * 		false - 没有可用的网络连接，或传入的context为null
	 */
	public static boolean isNetworkConnected(Context context) {
		if(context == null){
			return false;
		}
		ConnectivityManager connManager = 
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		State mobileState = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//		State wifiState = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//		if(mobileState == State.DISCONNECTED && wifiState == State.DISCONNECTED) {
//			return false;
//		}
//		return true;
		NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(info == null || info.getState() == State.DISCONNECTED){
			return false;
		}
		return true;
	}
}
