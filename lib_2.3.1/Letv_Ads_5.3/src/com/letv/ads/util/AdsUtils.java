package com.letv.ads.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.letv.adlib.model.ad.types.AdClickShowType;
import com.letv.ads.view.LetvWebViewActivity;

public final class AdsUtils {

	private AdsUtils() {
	}
	
	public static long lastClickTime = 0;

	public static long currentClickTime = 0;
	
	public static boolean checkClickEvent(){
		return checkClickEvent(1000);
	}
	
	public static boolean checkClickEvent(long interval) {
		currentClickTime = System.currentTimeMillis();
		if (currentClickTime - lastClickTime > interval) {
			lastClickTime = currentClickTime;
			return true;
		} else {
			lastClickTime = currentClickTime;
			return false;
		}
	}

	public static void gotoWeb(Context context,String url, AdClickShowType skipType) {
		if(skipType == AdClickShowType.ExternalWebBrowser){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
			context.startActivity(intent);
		} else if (skipType == AdClickShowType.InternalWebView){
			LetvWebViewActivity.launch(context, url, "广告");
		}
	}
	
	public static String createExt(String cid , String pid ,String vid , String adType , String tfid){
		StringBuilder builder = new StringBuilder() ;
		
		builder.append(getEleString(cid));
		builder.append(";");
		builder.append(getEleString(pid));
		builder.append("_");
		builder.append(getEleString(vid));
		builder.append(";");
		builder.append(getEleString(adType));
		builder.append(";");
		builder.append(getEleString(tfid));
		
		return builder.toString();
	}
	
	public static String createExt(String adType , String tfid){
		StringBuilder builder = new StringBuilder() ;
		
		builder.append(getEleString(adType));
		builder.append(";");
		builder.append(getEleString(tfid));
		
		return builder.toString();
	}
	
	public static String getEleString(String ele){
		if(TextUtils.isEmpty(ele)){
			return "-";
		}
		
		return ele ;
	}
	public static String createAdDuration(String firstAdTime, String secAdTime, String thirdAdTime) {
		StringBuilder builder = new StringBuilder();
		builder.append(getEleString(thirdAdTime));
		builder.append("_");
		builder.append(getEleString(secAdTime));
		builder.append("_");
		builder.append(getEleString(firstAdTime));
		return builder.toString();
	}
	
	public static String getAdSystem(int adSystem){
		if(adSystem==1){
			return "haoye";
		}else {
			return "letv";//暂时数据
		}
	}
}
