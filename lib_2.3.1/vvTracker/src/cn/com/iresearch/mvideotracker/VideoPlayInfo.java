package cn.com.iresearch.mvideotracker;

import cn.com.iresearch.mvideotracker.db.annotation.sqlite.Table;


@Table(name = "VideoPlayInfo")
public class VideoPlayInfo {

    private int _id;
    private String uid = "";
    private String videoID = "";
    private long videoLength = 0;
    private String customVal = "";
    private long playTime = 0;
    private int heartTime = 0;
    private int pauseCount = 0;
    private String action = "";
    private long lastActionTime = 0;
    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getVideoID() {
        return videoID;
    }
    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }
    public long getVideoLength() {
        return videoLength;
    }
    public void setVideoLength(long videoLength) {
        this.videoLength = videoLength;
    }
    public String getCustomVal() {
        return customVal;
    }
    public void setCustomVal(String customVal) {
        this.customVal = customVal;
    }
    public long getPlayTime() {
        return playTime;
    }
    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }
    public int getPauseCount() {
        return pauseCount;
    }
    public void setPauseCount(int pauseCount) {
        this.pauseCount = pauseCount;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public long getLastActionTime() {
        return lastActionTime;
    }
    public void setLastActionTime(long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }
    public int getHeartTime() {
        return heartTime;
    }
    public void setHeartTime(int heartTime) {
        this.heartTime = heartTime;
    }
    @Override
    public String toString() {
        return "VideoPlayInfo [_id=" + _id + ", videoID=" + videoID + ", videoLength="
                + videoLength + ", customVal=" + customVal + ", playTime=" + playTime
                + ", heartTime=" + heartTime + ", pauseCount=" + pauseCount + ", action=" + action
                + ", lastActionTime=" + lastActionTime + "]";
    }
    
}
