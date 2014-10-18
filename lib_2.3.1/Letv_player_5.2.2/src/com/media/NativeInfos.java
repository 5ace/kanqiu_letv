package com.media;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import android.text.TextUtils;

public class NativeInfos {
	
//	private static final String TAG = "NativeInfos";
	
	public static final int SUPPORT_MP4_LEVEL = 0;
	public static final int SUPPORT_TS350K_LEVEL = 1;
	public static final int SUPPORT_TS800K_LEVEL = 2;
	public static final int SUPPORT_TS1000K_LEVEL = 3;
	public static boolean mOffLinePlay = false;
	public static boolean mIfNative3gpOrMp4 = false;
	public static boolean mIsLive = false;
	
	public static String CPUFeatures = null;
	
	public static String CPUClock = null;
	
	public static String getCPUFeatures() {
		
		if(!TextUtils.isEmpty(CPUFeatures)) {
			return CPUFeatures;
		}
		
        String cmd = "cat /proc/cpuinfo";
        Map<String, Object> map = NativeCMD.runCmd(cmd);
        if (map == null) {
        	CPUFeatures = "Sorry, Run Cmd Failure !!!";
            return CPUFeatures;
        } else {
            InputStream in = (InputStream) map.get("input");
            InputStreamReader is = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(is);
            StringBuilder sb = new StringBuilder();
            String line = "";
            try {
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("Features") != -1)
                    	sb.append(line);
                }
            } catch (Exception e) {
            	CPUFeatures = "Read InputStream Failure !!!";
                return CPUFeatures;
            } finally {
                try {
                    if (br != null)
                        br.close();
                } catch (IOException e) {
                }
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                }
            }
            
            CPUFeatures = sb.toString();
            
            return CPUFeatures;
        }
	}
	 
	 public static String getCPUClock() throws Exception{
		 
		 if(!TextUtils.isEmpty(CPUClock)) {
			 return CPUClock;
		 }
		 
		 String cmd = "cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
	        Map<String, Object> map = NativeCMD.runCmd(cmd);
	        if (map == null) {
	        	CPUClock = "Sorry, Run Cmd Failure !!!";
	            return CPUClock;
	        } else {
	            InputStream in = (InputStream) map.get("input");
	            InputStreamReader is = new InputStreamReader(in);
	            BufferedReader br = new BufferedReader(is);
	            StringBuilder sb = new StringBuilder();
	            String line = "";
	            try {
	                while ((line = br.readLine()) != null) {
	                    	sb.append(line);
	                }
	            } catch (Exception e) {
	            	CPUClock = "Read InputStream Failure !!!";
	                return CPUClock;
	            } finally {
	                try {
	                    if (br != null)
	                        br.close();
	                } catch (IOException e) {
	                }
	                try {
	                    if (is != null)
	                        is.close();
	                } catch (IOException e) {
	                }
	                try {
	                    if (in != null)
	                        in.close();
	                } catch (IOException e) {
	                }
	            }
	            
	            CPUClock = sb.toString();
	            
	            return CPUClock;
	     }
	 }
	 
	 public static boolean ifSupportVfpOrNeon(){
		 String ret = getCPUFeatures();		 
		 if (ret.indexOf("neon") != -1 || ret.indexOf("vfp") != -1)
			 return true;
		 else
			 return false;
	 }

	 public static boolean ifSupportNeon(){
		 if (getCPUFeatures().indexOf("neon") != -1)
			 return true;
		 else
			 return false;
	 }
	 
	 /**
	  * cpu频率分级
	  * */
	 public static int getSupportLevel(){
		 int cpuClock = 0;
		 int ret = SUPPORT_MP4_LEVEL;
		 try {
			 cpuClock = Integer.parseInt(TextUtils.isEmpty(getCPUClock()) ? "900000" : getCPUClock());
		 } catch (NumberFormatException e) {
			 e.printStackTrace();
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 
		 if (cpuClock < 900000) { //<900Mhz, only support mp4
			 ret = SUPPORT_MP4_LEVEL;	
		 } else if (cpuClock >= 900000 && cpuClock < 1200000) {  // 900Mhz-1.2Ghz, support 350k TS、极速 180K TS
			 ret = SUPPORT_TS350K_LEVEL;
		 } else if (cpuClock >= 1200000 && cpuClock < 1600000) {  // 1.2Ghz-1.6Ghz, support 800k TS
			 ret = SUPPORT_TS800K_LEVEL;
		 } else {  // > 1.6Ghz, support 1000k TS
			 ret = SUPPORT_TS1000K_LEVEL;		
		 }   
		 System.out.println("vtype cpuClock ="+cpuClock);
		 return ret;
	 }
	 public static boolean ifNativePlayer(){
		 boolean ret;
		 if(mIsLive) {
			 ret = true;
		 } else if (getSupportLevel() == SUPPORT_MP4_LEVEL || (mOffLinePlay && mIfNative3gpOrMp4) || !ifSupportVfpOrNeon()) {
			 ret = false;
		 } else{
			 ret = true;
		 }
		 
		 return ret;
	 }
	 
	 public static void doWithNativePlayUrl(String url){
		url = url.toLowerCase();
		if (url.indexOf(".letv") != -1 || url.indexOf(".3gp") != -1 ||
			url.indexOf(".mp4") != -1){
			mIfNative3gpOrMp4 = true;    		
		}else if(url.startsWith("content")){
			mIfNative3gpOrMp4 = true;
		}else{
			mIfNative3gpOrMp4 = false;
		}
	 }
}
