package com.letv.watchball.share;

import android.app.Activity;
import android.content.Context;

import com.letv.star.LetvStar;
import com.letv.star.LetvStarListener;
import com.letv.watchball.activity.SharePageActivity;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.ShareAlbum;
import com.letv.watchball.ui.impl.BasePlayActivity;

public class LetvStarShare {
	
	/**
	 * 判断是否已经登录
	 * */
	public static boolean isLogin(final Context context) {
		
		return LetvStar.getInstance().isLogin(context);
	}
	
	
	public static void login(final Activity context , final ShareAlbum album , final int order, final int vid){
		
		LetvStar.getInstance().login(context , new LetvStarListener() {
			
			@Override
			public void onFail(final String failLog) {
//				context.runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						Toast.makeText(context, failLog, Toast.LENGTH_SHORT).show();
//					}
//				});
			}
			
			@Override
			public void onErr(final String errLog) {
//				context.runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						Toast.makeText(context, errLog, Toast.LENGTH_SHORT).show();
//					}
//				});
			}
			
			@Override
			public void onComplete() {
//				if((context instanceof ShareActivity) || (context instanceof ShareConfigActivity)){
//					
//				}else{
				if(context instanceof BasePlayActivity){
				SharePageActivity.launch(context,5, album.getShare_AlbumName(), album.getIcon(), album.getShare_vid(), album.getType(), album.getCid(), album.getYear(), album.getDirector(), album.getActor(), album.getTimeLength() , order, vid);
				}
				//				}
			}
		});
	}
	
	public static void share(Context context ,String desc, int id , int type , int cid , String icon , String title , String year , String director , String actor , long timeLength , LetvStarListener listener){
		try{
			LetvStar letvStar = LetvStar.getInstance();
			
			String isAlbum = "0";
			if(type == AlbumNew.Type.VRS_MANG){
				isAlbum = "1" ;
			}
			System.out.println("icon======"+icon);
			letvStar.share(context, desc, id+"", isAlbum, cid+"", icon, title, year, director, actor , timeLength == 0 ? "" : timeLength+"", listener);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void logout(final Activity context) {
		
		LetvStar.getInstance().logout(context);
	}
}
