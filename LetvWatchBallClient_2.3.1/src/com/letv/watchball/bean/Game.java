package com.letv.watchball.bean;



import java.io.Serializable;

public class Game implements Serializable{

      public String platform;
      /**
       * 比赛id
       */
      public String id = "0";

      /**
       * 赛事名称
       */
      public String level;
      /**
       * 赛事
       */
      public String level0;
      
      /**
       * 主队
       */
      public String home;
      /**
       * 客队
       */
      public String guest;
      /**
       * 主队比分
       */
      public String homeScore;
      /**
       * 客队比分
       */
      public String guestScore;
      /**
       * 辅助显示地段，vs=1时，主客队正常显示，vs=0时，主客队显示level字段
       */
      public String vs;
      /**
       * 主队icon
       */
      public String homeImg;
      /**
       * 客队icon
       */
      public String guestImg;
      /**
       * 开赛日期
       */
      public String playDate;
      /**
       * 开赛日(星期几)
       */
      public String playDay;
      /**
       * 开赛时间
       */
      public String playTime;
      /**
       * 比赛状态    0：未开始；1：直播中；2：已结束
       */
      public int status;
      /**
       * 专辑id
       */
      public String pid;
      /**
       * 视频id
       */
      public String vid;
      /**
       * recording_id
       */
      public String recording_id;

      public String ch;
      /**
       * 350码流直播地址
       */
      public String live_url_350;
      /**
       * 800码流直播地址
       */
      public String live_url_800;
      /**
       * 1300码流直播地址
       */
      public String live_url_1300;

      public LiveTs live_350;
      public LiveTs live_800;
      public LiveTs live_1300;
      /*
       * 是否需要付费
       */
      public String pay;
      
      public class LiveTs implements Serializable{
				public String code;
				public long tm;
				public String liveUrl;
				public String streamId;
      }
			
//    public boolean isFirstGameToday;//是否是今天的第一场比赛
      public boolean isGameSubscribed;//当前比赛是否被预定了
      /**
       * 比赛轮次
       */
      public String matchName;

      public long getPid() {
            try {
                  return Long.parseLong(pid);
            } catch (Exception e) {
            }
            return 0;
      }
      public void setPid(String pid) {
            this.pid = pid;
      }
      public long getVid() {
            try {
                  return Long.parseLong(vid);
            } catch (Exception e) {
            }
            return 0;
      }
      public void setVid(String vid) {
            this.vid = vid;
      }
      public String getVs() {

            if(vs!=null){
                  return vs;
            }

            return "2";
      }
      public void setVs(String vs) {
				this.vs = vs;
			}

      public long getRecording_id() {
            try {
                  return Long.parseLong(recording_id);
            } catch (Exception e) {
            }
            return 0;
      }
}