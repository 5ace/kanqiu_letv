package com.letv.watchball.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.letv.cache.LetvCacheTools;
import com.letv.watchball.activity.SharePageActivity;
import com.letv.watchball.bean.ShareAlbum;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.renren.api.connect.android.AsyncRenren;
import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.photos.PhotoHelper;
import com.renren.api.connect.android.photos.PhotoUploadRequestParam;
import com.renren.api.connect.android.photos.PhotoUploadResponseBean;
import com.renren.api.connect.android.status.StatusSetRequestParam;
import com.renren.api.connect.android.status.StatusSetResponseBean;
import com.renren.api.connect.android.view.RenrenAuthListener;

public class LetvRenrenShare {
	
	/**
	 * 判断是否登录
	 * */
	public static boolean isLogin(final Context context){
		final Renren renren = new Renren(ShareConstant.Renren.API_KEY, ShareConstant.Renren.SECRET_KEY, ShareConstant.Renren.APP_ID, context);
		if(renren.isAccessTokenValid()){
			return true ;
		}
		
		return false ;
	}
	
	/**
	 * 登录
	 * */
	public static void login(final Activity context , final ShareAlbum album , final int order, final int vid){
		final Renren renren = new Renren(ShareConstant.Renren.API_KEY, ShareConstant.Renren.SECRET_KEY, ShareConstant.Renren.APP_ID, context);
		final RenrenAuthListener listener = new RenrenAuthListener() {

			@Override
			public void onComplete(Bundle values) {
				if(context instanceof BasePlayActivity){
					SharePageActivity.launch(context,4, album.getShare_AlbumName(), album.getIcon(), album.getShare_id(), album.getType(), album.getCid(), album.getYear(), album.getDirector(), album.getActor(), album.getTimeLength() , order, vid);
				}
			}

			@Override
			public void onRenrenAuthError(RenrenAuthError renrenAuthError) {

			}

			@Override
			public void onCancelLogin() {
			}

			@Override
			public void onCancelAuth(Bundle values) {
			}
			
		};
		
		renren.authorizeFull(context , listener);
	}

	/**
	 * 分享图片
	 * */
	public static void share(Activity context ,String caption, final String imaUrl ,final AbstractRequestListener<PhotoUploadResponseBean> listener) {
		try{
			Renren renren = new Renren(ShareConstant.Renren.API_KEY, ShareConstant.Renren.SECRET_KEY, ShareConstant.Renren.APP_ID, context);
			final PhotoHelper photoHelper = new PhotoHelper(renren);
			PhotoUploadRequestParam photoParam = new PhotoUploadRequestParam();
			if (!"".equals(caption)) {
				photoParam.setCaption(caption);
			}
			
			String path = LetvCacheTools.StringTool.createFilePath(imaUrl);
			File file = new File(path);
			String newpath = LetvCacheTools.StringTool.createFilePath2(imaUrl);
			File newfile = new File(newpath);
			
			if(file.exists()){
				
				FileInputStream inputStream = null;
				FileOutputStream fos = null;
				
				try{
					inputStream = new FileInputStream(file);
					fos = new FileOutputStream(newfile);
					byte[] buffer = new byte[1024];
					int len = -1;
					while ((len = inputStream.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
				}catch(Exception exception){
					exception.printStackTrace();
				}finally{
					try{
						inputStream.close();
						fos.close();
					}catch (Exception e) {
					}
				}
			}

			if(newfile.exists()){
				photoParam.setFile(newfile);
				photoHelper.asyncUploadPhoto(photoParam, listener);
			}else{
				AbstractRequestListener<StatusSetResponseBean> l = new AbstractRequestListener<StatusSetResponseBean>(){

					@Override
					public void onComplete(StatusSetResponseBean arg0) {
						listener.onComplete(null);
					}

					@Override
					public void onFault(Throwable arg0) {
						listener.onFault(arg0);
					}

					@Override
					public void onRenrenError(RenrenError arg0) {
						listener.onRenrenError(arg0);
					}
				};
				share(context ,caption ,l);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 分享文字
	 * */
	public static void share(Activity context ,String status , AbstractRequestListener<StatusSetResponseBean> listener){
		Renren renren = new Renren(ShareConstant.Renren.API_KEY, ShareConstant.Renren.SECRET_KEY, ShareConstant.Renren.APP_ID, context);
		AsyncRenren aRenren = new AsyncRenren(renren);
		StatusSetRequestParam param = new StatusSetRequestParam(status);
		aRenren.publishStatus(param, listener , true);
	}
	
	/**
	 * 登出
	 * */
	public static void logout(Activity context){
		Renren renren = new Renren(ShareConstant.Renren.API_KEY, ShareConstant.Renren.SECRET_KEY, ShareConstant.Renren.APP_ID, context);
		renren.logout(context);
	}
}
