package cn.com.iresearch.mvideotracker;

import cn.com.iresearch.mvideotracker.db.FinalDb;

import android.content.Context;

import java.util.List;

public class IRVideo {

    private static final String TAG = "IRVideo";
    
    private Context context;
    
    private static IRVideo irVideo;
    
    private VideoPlayInfo videoPlayInfo;
    
    private FinalDb vvtracker_db;
    
    private static final String DBNAME = "vvtracker.db";
    
    private String uaid = "";
    
    private static final String INIT = "init";
    
    private static final String PLAY = "play";
    
    private static final String PAUSE = "pause";
    
    private static final String END = "end";
    
    private static final String UAID = "vv_uaid";

    //发送数据条数限制
    private static final int SEND_LIMIT = 10;
    
    private int pauseCount = 0;
    
    private long playTime = 0;
    
    private int heartTimes = 0;
    
    private long lastActionTime = 0;
    
    private IRVideo(Context context){
        this.context = context;
        vvtracker_db = FinalDb.create(context, DBNAME, false);
    }
    
    public static IRVideo getInstance(Context context){
        if (irVideo == null) {
            synchronized (IRVideo.class) {
                    irVideo = new IRVideo(context);
            }
        }
        return irVideo;
    }
    
    
    public void init(String uaid){
        VVUtil.vv_Logd(TAG, "初始化开始");
        this.uaid = uaid;
        VVUtil.saveSharedPreferences(context, UAID, uaid);
        VVUtil.vv_Logd(TAG, "初始化结束");
    }
    
    /**
     * 创建一条视频信息
     * @param videoID
     * @param videoLength
     * @param isPlay
     */
    public void newVideoPlay(String videoID, long videoLength, Boolean isPlay){
        try {
            if("".equals(getUaid())){
               VVUtil.vv_Loge(TAG, "未初始化!");
               return; 
            }
            VVUtil.vv_Logd(TAG, "创建视频数据start");
            pauseCount = 0;
            playTime = 0;
            lastActionTime = 0;
            videoPlayInfo = new VideoPlayInfo();
            videoPlayInfo.setVideoID(videoID);
            videoPlayInfo.setPauseCount(pauseCount);
            videoPlayInfo.setPlayTime(playTime);
            videoPlayInfo.setLastActionTime(lastActionTime);
            videoPlayInfo.setHeartTime(heartTimes);
            videoPlayInfo.setVideoLength(videoLength);
            videoPlayInfo.setAction(INIT);
            sendHistoryInfo();
            saveVideoPlayInfo(videoPlayInfo);
            if(isPlay){
              videoPlay();  
            }
            VVUtil.vv_Logd(TAG, "创建视频数据end");
        } catch (Exception e) {
            VVUtil.vv_Loge(TAG, "创建视频数据异常" + e.toString());
            e.printStackTrace();
        }
    }
    
    /**
     * 视频播放，A点发送数据
     */
    public void videoPlay(){
        try {
            if(videoPlayInfo == null){
                return;
            }
            String action = videoPlayInfo.getAction();
            lastActionTime = VVUtil.getUnixTimeStamp();
            videoPlayInfo.setAction(PLAY);
            videoPlayInfo.setLastActionTime(lastActionTime);
            updateVideoPlayInfo(videoPlayInfo);
            if(INIT.equals(action)){
                sendAVideoInfo(videoPlayInfo);
            }
        } catch (Exception e) {
            VVUtil.vv_Loge(TAG, "存放视频A点数据到数据库异常" + e.toString());
            e.printStackTrace();
        }
    }
    
    /**
     * 视频暂停
     */
    public void videoPause(){
        try {
            if (videoPlayInfo == null) {
                return;
            }
            if(!"play".equals(videoPlayInfo.getAction())){
                return;
            }
            if(lastActionTime == 0){
                deleteVideoPlayInfoByWhere(videoPlayInfo);;
                return;
            }
            playTime += VVUtil.getUnixTimeStamp() - lastActionTime;
            lastActionTime = VVUtil.getUnixTimeStamp();
            pauseCount ++;
            videoPlayInfo.setAction(PAUSE);
            videoPlayInfo.setLastActionTime(lastActionTime);
            videoPlayInfo.setPlayTime(playTime);
            videoPlayInfo.setPauseCount(pauseCount);
            updateVideoPlayInfo(videoPlayInfo);
        } catch (Exception e) {
            VVUtil.vv_Loge(TAG, "更新视频暂停数据异常" + e.toString());
            e.printStackTrace();
        }
    }
    /**
     * 视频结束，B点发送数据
     */
    public void videoEnd(){
        try {
            if (videoPlayInfo == null) {
                return;
            }
            if(lastActionTime == 0){
                deleteVideoPlayInfoByWhere(videoPlayInfo);;
                return;
            }
            playTime += VVUtil.getUnixTimeStamp() - lastActionTime;
            lastActionTime = VVUtil.getUnixTimeStamp();
            videoPlayInfo.setPlayTime(playTime);
            videoPlayInfo.setLastActionTime(lastActionTime);
            videoPlayInfo.setAction(END);
            updateVideoPlayInfo(videoPlayInfo);
            sendBVideoInfo(videoPlayInfo);
        } catch (Exception e) {
            VVUtil.vv_Loge(TAG, "更新视频B点数据异常" + e.toString());
            e.printStackTrace();
        }
    }
    
    //将当前视频对象置为空
    private void setVideoInfoNull(){
        videoPlayInfo = null;
    }
    
    /**
     * 第一次进入发送历史遗留数据（C点数据）
     */
    private void sendHistoryInfo() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!VVUtil.isNetworkAvailable(context)) {
                        VVUtil.vv_Loge(TAG, "网络不畅通！");
                        return;
                    }
                    List<VideoPlayInfo> videoPlayInfos = vvtracker_db.findAll(VideoPlayInfo.class);
                    VVUtil.vv_Logd(TAG, "遗留数据数目：" + videoPlayInfos.size());
                    for (VideoPlayInfo info : videoPlayInfos) {
                        if ("end".equals(info.getAction())) {
                            int status = VVUtil.urlGet(context, VVUtil.getUrl(context, info,VVUtil.IWT_P1_C));
                            if (status == 1) {
                                deleteVideoPlayInfo(info);
                            }
                        }
                    }
                } catch (Exception e) {
                    VVUtil.vv_Loge(TAG, "发送遗留数据发送异常！");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    /**
     * A点发送数据
     */
    private void sendAVideoInfo(final VideoPlayInfo videoPlayInfo) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String urlString = VVUtil.getUrl(context, videoPlayInfo,VVUtil.IWT_P1_A);
                    // TODO 发送URL
                    int status = VVUtil.urlGet(context,urlString);
                    if (status == 0) {
                        VVUtil.vv_Loge(TAG, "A点数据发送失败！");
                    } else {
                        VVUtil.vv_Loge(TAG, "A点数据发送成功！");
                    }
                } catch (Exception e) {
                    VVUtil.vv_Loge(TAG, "A点数据发送异常！");
                    e.printStackTrace();
                }
            }
        }).start();

    }
    
    /**
     * B点发送数据
     * @param videoPlayInfo
     */
    private void sendBVideoInfo(final VideoPlayInfo videoPlayInfo) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String urlString = VVUtil.getUrl(context, videoPlayInfo,VVUtil.IWT_P1_B);
                    // TODO 发送URL
                    int status = VVUtil.urlGet(context,urlString);
                    if (status == 0) {
                        VVUtil.vv_Loge(TAG, "B点数据发送失败！");
                    } else {
                        deleteVideoPlayInfoByWhere(videoPlayInfo);
                        VVUtil.vv_Loge(TAG, "B点数据发送成功！");
                    }
                    setVideoInfoNull();
                } catch (Exception e) {
                    VVUtil.vv_Loge(TAG, "B点数据发送异常！");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 保存一条数据
     * @param videoPlayInfo
     */
    private void saveVideoPlayInfo(final VideoPlayInfo videoPlayInfo){
       
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    //若数据库中数据条目多于限制条数则将数据库中多余数据删除
                    List<VideoPlayInfo> videoPlayInfos = vvtracker_db.findAll(VideoPlayInfo.class);
                    boolean isHave = false;
                    for(VideoPlayInfo info : videoPlayInfos){
                        if(videoPlayInfo.getVideoID().equals(info.getVideoID())){
                            isHave = true;
                        }
                    }
                    if(!isHave){
                        if(videoPlayInfos.size() > SEND_LIMIT){
                            videoPlayInfos = videoPlayInfos.subList(SEND_LIMIT, videoPlayInfos.size());
                            VVUtil.vv_Logd(TAG, "数据库中数据太多移除多余数据数目:" + videoPlayInfos.size());
                            for(VideoPlayInfo info : videoPlayInfos){
                                deleteVideoPlayInfo(info);
                            }
                        }
                        VVUtil.vv_Loge(TAG, videoPlayInfo.toString());
                        videoPlayInfo.setUid(DataProvider.getDesU(context));
                        vvtracker_db.save(videoPlayInfo);
                    }else{
                       updateVideoPlayInfo(videoPlayInfo); 
                    }
                } catch (Exception e) {
                    VVUtil.vv_Loge(TAG, "保存视频数据到数据库异常" + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
        
    }
    
    /**
     * 更新一条数据
     * @param videoPlayInfo
     */
    private void updateVideoPlayInfo(final VideoPlayInfo videoPlayInfo){
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    if(videoPlayInfo == null){
                       return; 
                    }
                    List<VideoPlayInfo> videoPlayInfos = vvtracker_db.findAll(VideoPlayInfo.class);
                    if(videoPlayInfos.size() >0){
                        vvtracker_db.update(videoPlayInfo, "videoID=" + "'" + videoPlayInfo.getVideoID() + "'");
                        VVUtil.vv_Loge(TAG, videoPlayInfo.toString());
                    }
                } catch (Exception e) {
                    VVUtil.vv_Loge(TAG, "更新视频数据异常" + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
        
    }
    /**
     * 删除一条数据
     * @param videoPlayInfo
     */
    private void deleteVideoPlayInfo(final VideoPlayInfo videoPlayInfo){
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    vvtracker_db.delete(videoPlayInfo);
                } catch (Exception e) {
                    VVUtil.vv_Loge(TAG, "保存视频数据到数据库异常" + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
        
    }
    
    /**
     * 删除当前数据
     * @param videoPlayInfo
     */
    private void deleteVideoPlayInfoByWhere(final VideoPlayInfo videoPlayInfo){
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    List<VideoPlayInfo> videoPlayInfos = vvtracker_db.findAll(VideoPlayInfo.class);
                    for(VideoPlayInfo info : videoPlayInfos){
                       if(videoPlayInfo.getVideoID().equals(info.getVideoID())){
                           vvtracker_db.delete(info);
                       } 
                    }
                } catch (Exception e) {
                    VVUtil.vv_Loge(TAG, "保存视频数据到数据库异常" + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
        
    }
    
    /**
     * 清空数据表
     */
    public void clearVideoPlayInfo(){
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    vvtracker_db.deleteByWhere(VideoPlayInfo.class, null);
                } catch (Exception e) {
                    VVUtil.vv_Loge(TAG, "清理数据库中视频数据异常" + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    public String getUaid(){
        if(!"".equals(uaid)){
            return uaid;
        }else{
            return VVUtil.getSharedPreferences(context, UAID);
        }
    }
    
    public String getUid() {
        return DataProvider.getDesU(context);
    }
    
}
