package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.letv.watchball.bean.VideoFile.VideoSchedulingAddress;

public class VideoSchedulingAddressParser extends LetvMobileParser<VideoSchedulingAddress>{
	
	private final String MAINURL = "mainUrl" ;
	
	private final String BACKURL0 = "backUrl0" ;
	
	private final String BACKURL1 = "backUrl1" ;
	
	private final String BACKURL2 = "backUrl2" ;
	
	private final String FILESIZE = "filesize" ;
	
	private String GSLB;

	public VideoSchedulingAddressParser(boolean needPay){
		GSLB = "&format=1&expect=1&termid=2&pay=" +(needPay ? 1 : 0)+ "&ostype=android&hwtype=iphone" ;
	}
	
	@Override
	public VideoSchedulingAddress parse(JSONObject data) throws JSONException {
		
		VideoSchedulingAddress address = new VideoSchedulingAddress();
		
		if(has(data, MAINURL)){
			String url = getString(data, MAINURL) ;
			if(!TextUtils.isEmpty(url)){
				url += GSLB ;
			}
			
			address.setMainUrl(url);
		}
		
		if(has(data, BACKURL0)){
			String url = getString(data, BACKURL0) ;
			if(!TextUtils.isEmpty(url)){
				url += GSLB ;
			}
			
			address.setBackUrl0(url);
		}
		
		if(has(data, BACKURL1)){
			String url = getString(data, BACKURL1) ;
			if(!TextUtils.isEmpty(url)){
				url += GSLB ;
			}
			
			address.setBackUrl1(url);
		}
		
		if(has(data, BACKURL2)){
			String url = getString(data, BACKURL2) ;
			if(!TextUtils.isEmpty(url)){
				url += GSLB ;
			}
			
			address.setBackUrl2(url);
		}
		
		address.setFilesize(getLong(data, FILESIZE));
		
		return address;
	}
}
