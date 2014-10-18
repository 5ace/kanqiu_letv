package com.letv.watchball.parser;

import android.util.Log;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.WorldCupEntity;
import com.nostra13.universalimageloader.utils.L;
import org.json.JSONObject;

/**
 * Created by songmengyu on 14-4-29.
 */
public class WorldCupParser extends LetvMainParser<WorldCupEntity,JSONObject> {

      @Override
      public WorldCupEntity parse(JSONObject data) throws Exception {

    	  WorldCupEntity  worldCupEntity=WorldCupEntity._getInstance();
           
            if(data.has("world_cpu")) {
                  String world_cpu = data.getString("world_cpu");
                  worldCupEntity.setShowWorldCup(world_cpu.equals("1"));
            }
            
            if(data.has("operate")) {
             
            	   JSONObject operate = data.getJSONObject("operate");
            	    if (operate.has("name")) {
            	    	Log.i("oyys", "name="+operate.getString("name"));
            	    	worldCupEntity.setName(operate.getString("name"));
                    }else{
                    	worldCupEntity.setName("");
                    }
            	    if(operate.has("url")){
            	    	worldCupEntity.setUrl(operate.getString("url"));
            	    }else{
            	    	worldCupEntity.setUrl("");
            	    }
            	    
          }
            
            if (data.has("openStatus")) {
                  JSONObject openStatus = data.getJSONObject("openStatus");
                  String ad;
                  if (openStatus.has("ad")) {
                        ad = openStatus.getString("ad");
                        worldCupEntity.setShowAD(ad.equals("1"));
                  }
                  String androidUtp;
                  if (openStatus.has("androidUtp")) {
                  androidUtp = openStatus.getString("androidUtp");
                  worldCupEntity.setShowUTP(androidUtp.equals("1"));
                  }
                  Log.d("ads",worldCupEntity.isShowAD() + "    " + worldCupEntity.isShowUTP() +"  "+ worldCupEntity.isShowWorldCup());
            }


            return worldCupEntity;
      }

      @Override
      protected boolean canParse(String data) {
            if(null != data && !data.equals(""))
                  return true;
            return false;
      }

      @Override
      protected JSONObject getData(String data) throws Exception {
            JSONObject result = new JSONObject(data);
            result = result.getJSONObject("body");
            return result;
      }

}
