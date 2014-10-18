package com.tencent.weibo;

import android.os.Bundle;


public interface TencentAuthListener {

	 /**
     * 未处理
     */
    public final static int ACTION_UNPROCCESS = 0;

    /**
     * 已处理
     */
    public final static int ACTION_PROCCESSED = 1;

    /**
     * 由Dialog处理
     */
    public final static int ACTION_DIALOG_PROCCESS = 2;

    /**
     * 成功后调用
     */
    public void onComplete();
    
    public interface CallbackListener{
		
		public void callBack(String msg);
	}    
    /**
     * 页面加载之前调用。
     * 
     * @param url
     * @return 0:未处理，1:已经处理，2:由Dialog处理
     */
    public int onPageBegin(String url);

    /**
     * 页面开始加载时调用。
     * 
     * @param url
     * @return
     */
    public boolean onPageStart(String url);

    /**
     * 页面加载结束调用。
     * 
     * @param url
     */
    public void onPageFinished(String url);

    /**
     * 出现错误调用。
     * 
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    public void onReceivedError(int errorCode, String description, String failingUrl);
	
    /**
     * 登录和授权完成后调用。
     * 
     * @param values key:授权服务器返回的参数名，value:是参数值。
     */
    public void onComplete(Bundle values);
    
    /**
     * 用户取消登录。
     */
    public void onCancelLogin();

    /**
     * 用户取消授权。
     * 
     * @param values key:授权服务器返回的参数名，value:是参数值。
     */
    public void onCancelAuth(Bundle values);

}
