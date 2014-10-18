package com.letv.watchball.parser;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.PushAdImage;

public class PushAdImageParse extends LetvMainParser<PushAdImage, JSONObject>{

	@Override
	public PushAdImage parse(JSONObject data) throws Exception {
            PushAdImage pushAdImage = new PushAdImage();
            if(null != data && data.has("playPlatform") && data.has("pic1")){
			pushAdImage.setId(data.getInt("id"));
			pushAdImage.setPic1(data.getString("pic1"));
			pushAdImage.setCreateTime(data.getLong("ctime"));
			pushAdImage.setmTime(data.getLong("mtime"));
                  JSONObject playPlatform = data.getJSONObject("playPlatform");
                  pushAdImage.setPlayPlatform((playPlatform.has("420003_1") && playPlatform.getString("420003_1").equals("Android")));

                  Log.d("smydebug","PlayPlatform" + pushAdImage.getPlayPlatform());

			return pushAdImage;
		}else{

                  pushAdImage.setPlayPlatform(false);
			return pushAdImage;
		}
	}

	@Override
	protected boolean canParse(String data) {
		
		if(null != data && !data.equals(""))
			return true;
		return false;
	}

	@Override
	protected JSONObject getData(String data) throws Exception {
            JSONObject object = new JSONObject(data);
            JSONObject result = null;
            if (object.has("blockContent")){
                  JSONArray array = getJSONArray(object, "blockContent");
                  try {
                	  if(null != array && array.length()>0){
                          result = array.getJSONObject(0);
                    }else{
                  	  result = object;
                    }
				} catch (Exception e) {
					// TODO: handle exception
					return  object;
				}
                 
            } else {
                  result = object;
            }
		return result;
	}

}
