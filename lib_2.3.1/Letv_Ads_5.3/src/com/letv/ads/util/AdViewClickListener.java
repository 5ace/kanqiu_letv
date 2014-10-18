package com.letv.ads.util;

import com.letv.adlib.model.ad.types.AdClickShowType;


public interface AdViewClickListener {
	
	public void onClick(AdClickShowType clickShowType , String pid , String vid);
}
