package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * Created by songmengyu on 14-4-29.
 */
public class WorldCupEntity implements LetvBaseBean {

	private final static WorldCupEntity worldCupEntity = new WorldCupEntity();	
      
	private WorldCupEntity(){
    	  
      }
	  
	public static WorldCupEntity _getInstance(){
		  
		return worldCupEntity;
		  
	  }
      private boolean isShowWorldCup = false;
      private boolean isShowAD = false;
      private boolean isShowUTP = false;
      private String name="";
      private String url="";
   
      public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isShowAD() {
            return isShowAD;
      }

      public void setShowAD(boolean isShowAD) {
            this.isShowAD = isShowAD;
      }

      public boolean isShowUTP() {
            return isShowUTP;
      }

      public void setShowUTP(boolean isShowUTP) {
            this.isShowUTP = isShowUTP;
      }

      public boolean isShowWorldCup() {
            return isShowWorldCup;
      }

      public void setShowWorldCup(boolean isShowWorldCup) {
            this.isShowWorldCup = isShowWorldCup;
      }
}
