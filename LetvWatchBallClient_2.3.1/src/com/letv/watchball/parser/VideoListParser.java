package com.letv.watchball.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import com.letv.watchball.bean.VideoList;

public class VideoListParser extends LetvMobileParser<VideoList>{

	@Override
	public VideoList parse(JSONObject data) throws Exception {
		
		if(data != null){
			JSONArray array = getJSONArray(data, "videoInfo");
			
			if(array != null && array.length() > 0){
				VideoParser parser = new VideoParser() ;
				VideoList list = new VideoList() ;
				for(int i = 0 ; i < array.length() ; i++){
					JSONObject object = getJSONObject(array, i);
					list.add(parser.parse(object));
				}
				
				if(has(data, "pagenum")){
					list.setPagenum(getInt(data, "pagenum"));
				}
				
				if(has(data, "videoPosition")){
					list.setVideoPosition(getInt(data, "videoPosition"));
				}
				
				return list ;
			}
		}
		
		return null;
	}
}
