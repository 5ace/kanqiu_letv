package com.letv.watchball.db;

import android.content.Context;

import com.letv.watchball.LetvApplication;

public class DBManager {

	private Context context = LetvApplication.getInstance();

      private DBManager() {

      }

      private static DBManager instance = new DBManager();

      public static DBManager getInstance() {
            return instance;
      }

      private PlayRecordHandler playTrace = new PlayRecordHandler(context);
      private LiveSubscribeTraceHandler subscribeGameTrace = new LiveSubscribeTraceHandler(context);
      private LocalCacheTraceHandler localCacheTrace = new LocalCacheTraceHandler(context);
      private DialogMsgTraceHandler dialogMsgTrace = new DialogMsgTraceHandler(context);
	private PushAdImageTraceHandler pushAdImageTrace = new PushAdImageTraceHandler(context);

      public PlayRecordHandler getPlayTrace() {
            return playTrace;
      }

      public LiveSubscribeTraceHandler getSubscribeGameTrace() {
            return subscribeGameTrace;
      }

      public LocalCacheTraceHandler getLocalCacheTrace() {
            return localCacheTrace;
      }

      public DialogMsgTraceHandler getDialogMsgTrace(){
            return dialogMsgTrace;
      }
	
	public PushAdImageTraceHandler getPushAdImageTrace(){
		return pushAdImageTrace;
	}
}
