package cn.com.iresearch.mvideotracker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class VVUtil {
    //日志输出
    public static boolean isDebug = false;
    public static final String VV_URL = "http://irs01.com/irt";
    public static final String IWT_ID = "_imt_id";
    public static final String IWT_UA = "_iwt_UA";
    public static final String IWT_T = "_t";
    public static final String IWT_T_VALUE = "i";
    public static final String IWT_P1 = "_iwt_p1";
    public static final String IWT_P1_A = "A-0-0";
    public static final String IWT_P1_B = "B-0-0";
    public static final String IWT_P1_C = "C-0-0";
    public static final String IWT_P2 = "_iwt_p2";
    public static final String IWT_P3 = "_iwt_p3";
    public static final String IWT_P4 = "_iwt_p4";
    //操作系统ios/android
    public static final String IWT_P5 = "_iwt_p5";
    public static final String IWT_P5_VALUE = "android";
    //操作系统版本
    public static final String IWT_P6 = "_iwt_p6";
    //机型
    public static final String IWT_P7 = "_iwt_p7";
    
    public static String getUrl(Context context,VideoPlayInfo videoPlayInfo,String action){
        
        if(videoPlayInfo == null){
            return null;
        }
       
       StringBuffer urlBuffer = new StringBuffer();
       urlBuffer.append(VV_URL);
       urlBuffer.append("?");
       urlBuffer.append(IWT_ID + "=" + IRVideo.getInstance(context).getUaid() + "&");
       urlBuffer.append(IWT_UA + "=" + videoPlayInfo.getUid() + "&");
       urlBuffer.append(IWT_T + "=" + IWT_T_VALUE + "&");
       urlBuffer.append(IWT_P1 + "=" + action + "&");

       urlBuffer.append(IWT_P2 + "=" + videoPlayInfo.getVideoID() + "&");
       
       String videoInfoString = videoPlayInfo.getVideoLength() + "-" + videoPlayInfo.getPlayTime() + "-" + videoPlayInfo.getPauseCount() + "-" + videoPlayInfo.getHeartTime() ;
       
       urlBuffer.append(IWT_P3 + "=" + videoInfoString + "&");
       
       urlBuffer.append(IWT_P4 + "=" + videoPlayInfo.getCustomVal() + "&");
       
       urlBuffer.append(IWT_P5 + "=" + IWT_P5_VALUE + "&");
       
       urlBuffer.append(IWT_P6 + "=" + getOsVersion(context) + "&");
       
       urlBuffer.append(IWT_P7 + "=" + getPhoneMsg());

       return urlBuffer.toString();
    }
    

    //保存数据到SharedPreferences
    public static void saveSharedPreferences(Context context,String key,String value){
        SharedPreferences sharedPreferences =context.getSharedPreferences("VV_Tracker", 1);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    //从SharedPreferences取数据
    public static String getSharedPreferences(Context context,String key){
        SharedPreferences sharedPreferences =context.getSharedPreferences("VV_Tracker", 1);
        String value = sharedPreferences.getString(key, "");
        return value;
    }
    
    //日志输出
    public static void vv_Logd(String tag,String msg){
        if(isDebug){
            Log.d(tag, msg);
        }
    }
    //错误日志输出
    public static void vv_Loge(String tag,String msg){
        if(isDebug){
            Log.e(tag, msg);
        }
    }
    /**
     * 获取机型信息
     * @return
     */
    public static String getPhoneMsg(){
        return  android.os.Build.MODEL;
    }
    /**
     * 获取操作系统版本
     * @param context
     * @return
     */
    public static String getOsVersion(Context context) {
        String osVersion = "";
        if (checkPhoneState(context)) {
            osVersion = android.os.Build.VERSION.RELEASE;

            return osVersion;
        } else {
            
            return null;
        }
    }
    /**
     * check phone _state is readied ;
     * 
     * @param context
     * @return
     */
    public static boolean checkPhoneState(Context context) {
        PackageManager packageManager = context.getPackageManager();

        if (packageManager.checkPermission("android.permission.READ_PHONE_STATE", context.getPackageName()) != 0) {
            return false;
        }
        return true;
    }
    
    /**
     * 检查是否联网
     * 
     * @param context
     * @return true or false
     */
    public static boolean isNetworkAvailable(Context context) {
        if (checkPermissions(context, "android.permission.INTERNET")) {
            ConnectivityManager cManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            } else {
                vv_Loge("error", "Network error");
                return false;
            }
        } else {
            vv_Loge(" lost  permission", "lost----> android.permission.INTERNET");
            return false;
        }
    }
    
    /**
     * 检查是否有网络权限
     * 
     * @param context
     * @param permission
     * @return true or false
     */
    public static boolean checkPermissions(Context context, String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        return localPackageManager.checkPermission(permission,
                context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * 获取当前时间，单位为秒
     * @return
     */
    public static long getUnixTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }
    
    /**
     * 发送get请求
     * @param url
     * @return
     */
    public static int urlGet(Context context,String url){
        if (!isNetworkAvailable(context)) {
            vv_Loge("VVUtil --- urlGet", "网络不畅通！");
            return 0;
        }
        HttpGet get=new HttpGet(url);  
        HttpClient client=new DefaultHttpClient(); 
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        // 读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        HttpResponse response;
        try {  
            response=client.execute(get);
            int status = response.getStatusLine().getStatusCode();
            vv_Loge("fx--------------urlstatus", ""+status);
            switch (status) {
                case 200:
                    return 1;
                default:
                    return 0;
            }
        } catch (Exception e) {  
            e.printStackTrace();
            return 0;
        }  
    }
    
}
