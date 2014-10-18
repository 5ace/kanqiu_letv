package com.letv.watchball.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.PlayRecord;
import com.letv.watchball.bean.PlayRecordList;
import com.letv.watchball.bean.PlayTraceBoolean;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.PlayRecordParser;
import com.letv.watchball.parser.PlayTraceBooleanParser;
import com.letv.watchball.parser.PlayTracesSearchParser;

public class LetvPlayRecordFunction {

	private static final String TAG = "LetvPlayRecordFunction";
	
	private static final int PAGE_SIZE = 20;
	
	/**
	 * 最后一条数据
	 */
	public static PlayRecord getLastPlayRecord() {
		return DBManager.getInstance().getPlayTrace().getLastPlayTrace();
	}
	
	/**
	 * 获取专辑的视频Id
	 * @param albumId 专辑Id
	 * @return
	 */
	public static int getPlayRecordByAlbumId(int albumId) {
		int vid = 0;
		PlayRecord localRecord = DBManager.getInstance().getPlayTrace().getPlayTrace(albumId);

		if (localRecord != null) {
			vid = localRecord.getVideoId();
		}
		LogInfo.log(TAG, "getPlayRecordByPid----->vid=" + vid);

		return vid;
	}
	
	/**
	 * 获取播放记录当前集数
	 * @param albumId
	 * @return
	 */
	public static int getPlayRecordOrderByAlbumId(int albumId) {
		int order = -1;
		PlayRecord localRecord = DBManager.getInstance().getPlayTrace().getPlayTrace(albumId);
		
		if (localRecord != null) {
			order = localRecord.getCurEpsoid();
		}
		LogInfo.log(TAG, "getPlayRecordByPid------->getPlayTraceOrderByPid ="+order);
		
		return order;
	}

	/**
	 * 得到播放视频额记录点（同步）
	 * @param vid 视频Id
	 * @param isDownload 是否已经下载
	 * @return
	 */
	public static PlayRecord getPoint(int pid , int vid , boolean isDownload) {

		PlayRecord record = null;

		if (PreferencesManager.getInstance().isLogin() && !isDownload) {
			
			LetvDataHull<PlayRecord> datahull =  LetvHttpApi.searchPlayTraces(0, String.valueOf(pid), String.valueOf(vid), PreferencesManager
					.getInstance().getSso_tk(), new PlayTracesSearchParser(String.valueOf(vid), String.valueOf(pid)));

			if (datahull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				record = datahull.getDataEntity();
			}
		}
		PlayRecord localRecord = null ;
		if(pid > 0){
			localRecord = DBManager.getInstance().getPlayTrace().getPlayTraceByAlbumId(pid);
		} else {
			localRecord = DBManager.getInstance().getPlayTrace().getPlayTraceByEpsodeId(vid);
		}
		if(record != null){
			if(localRecord != null){
				if(record.getUpdateTime() < localRecord.getUpdateTime()){
					record = localRecord ;
				}else {
					record.setTitle(localRecord.getTitle());
					record.setImg(localRecord.getImg());
				}
			}
		} else {
			record = localRecord ;
		}
		
		return record ;
	}

	/**
	 * 从云端服务器，拉取所有播放记录 更新本地表，并且上传本地记录（同步耗时，上传不耗时）
	 * */
	public static int updatePlayRecordFromCloud(Context context) {
		//0失败，1成功，2token过期
		int isSucceed = 0 ; 
		LetvDataHull<PlayRecordList> dataHull = null ;
		
		if(PreferencesManager.getInstance().isLogin()){
			dataHull = LetvHttpApi.getPlayTraces(0, LetvUtil.getUID(), 
					"1", String.valueOf(PAGE_SIZE), PreferencesManager.getInstance().getSso_tk(), new PlayRecordParser());
			
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				savePlayRecord(dataHull.getDataEntity());
				isSucceed = 1;
			} else{
				int code = dataHull.getErrMsg();
				
				isSucceed = code ;
			}

			SubmitPlayTraces submitPlayTrace = new SubmitPlayTraces(context , false);
			submitPlayTrace.start();
		}

		return isSucceed;
	}

	/**
	 * 删除一条或者多条播放记录（异步提交到云端）
	 * */
	public static boolean deletePlayRecord(Context context, PlayRecordList list) {
		if (list == null || list.size() == 0) {
			return false;
		}
		boolean netAvailable = NetWorkTypeUtils.isNetAvailable();
		if(!PreferencesManager.getInstance().isLogin()){//未登录,删除本地
			deletePlayRecord(list);
			return true;
		}else if(PreferencesManager.getInstance().isLogin() && netAvailable){//登录，没网，不进行操作
			UIs.showToast(R.string.load_data_no_net);
			return false;
		}else if(PreferencesManager.getInstance().isLogin() && !netAvailable){//已登录，有网,正常进行删除
			for (PlayRecord record : list) {
				int albumId = record.getAlbumId();
				int videoId = record.getVideoId();
				if (albumId > 0) {
					DBManager.getInstance().getPlayTrace().tagDeleteByPid(albumId);
				} else {
					DBManager.getInstance().getPlayTrace().tagDeleteByVid(videoId);
				}
			}
			new DeleteOneItemPlayTrace(context).start();
			return true;
		}
		return false;
	}

	/**
	 * 清除所有播放记录（异步记录到云端）
	 * */
	public static boolean clearPlayTraces(Context context) {
		boolean netAvailable = NetWorkTypeUtils.isNetAvailable();
		if(!PreferencesManager.getInstance().isLogin()){//未登录,删除本地
			PlayRecordList list = DBManager.getInstance().getPlayTrace().getAllPlayTrace();
			deletePlayRecord(list);
			return true;
		}else if(PreferencesManager.getInstance().isLogin() && netAvailable){//登录，没网，不进行操作
			UIs.showToast(R.string.load_data_no_net);
			return false;
		}else if(PreferencesManager.getInstance().isLogin() && !netAvailable){//已登录，有网,正常进行删除
			new DeletePlayTrace(context, 1, true).start();
			return true;
		}
		return true;
	}

	/**
	 * 提交单条数据（异步提交）
	 * */
	public static void submitPlayTraces(Context context, int cid, int pid, int vid, int nvid, int vtype,
			long vtime, long htime, String title, String img, int nc) {
		DBManager.getInstance().getPlayTrace().savePlayTrace(cid, pid, vid, nvid, LetvUtil.getUID(), 2, vtype, vtime, htime,
						System.currentTimeMillis() / 1000, title, img, 1, nc , null);

		if (PreferencesManager.getInstance().isLogin()) {
			SubmitPlayTrace submitPlayTrace = new SubmitPlayTrace(context);
			submitPlayTrace.init(cid, pid, vid, nvid, vtype, htime);
			submitPlayTrace.start();
		}
	}

	/**
	 * 批量提交滞留的播放记录（异步提交）
	 * */
	public static void submitPlayTracesAndUpdate(Context context) {
		SubmitPlayTraces submitPlayTrace = new SubmitPlayTraces(context , true);
		submitPlayTrace.start();
	}

	/**
	 * 清空播放记录
	 * @author haitian
	 *
	 */
	private static class DeletePlayTrace extends LetvHttpAsyncTask<PlayTraceBoolean> {
		int flush = 0;
		public DeletePlayTrace(Context context,int flush, boolean isDialog) {
			super(context);
			this.flush = flush;
		}
		@Override
		public LetvDataHull<PlayTraceBoolean> doInBackground() {
			PlayRecordList list = DBManager.getInstance().getPlayTrace().getAllPlayTrace();
			if (list != null && list.size() > 0) {
				LetvDataHull<PlayTraceBoolean> dataHull = LetvHttpApi.deletePlayTraces(0, null, null,
						LetvUtil.getUID(), null, flush+"", "0", PreferencesManager.getInstance()
								.getSso_tk(), new PlayTraceBooleanParser());

				if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
					deletePlayRecord(list);
				}
			}
			return null;
		}
		@Override
		public void onPostExecute(int updateId, PlayTraceBoolean result) {
		}
		
	}
	/**
	 * 批量提交清除播放记录，由于考虑到清除播放记录的操作的延时性，所以处理为批量删除
	 * @author haitian
	 *
	 */
	private static class DeleteOneItemPlayTrace extends LetvHttpAsyncTask<PlayTraceBoolean> {
		public DeleteOneItemPlayTrace(Context context) {
			super(context);
		}
		
		@Override
		public LetvDataHull<PlayTraceBoolean> doInBackground() {
			
			PlayRecordList list = DBManager.getInstance().getPlayTrace().getTagDeletes();
			if (list != null && list.size() > 0) {
				LetvDataHull<PlayTraceBoolean> dataHull = LetvHttpApi.deletePlayTraces(0, null, null,
						LetvUtil.getUID(), getDeleteString(list), "0", "0", PreferencesManager.getInstance()
						.getSso_tk(), new PlayTraceBooleanParser());
				
				if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
					deletePlayRecord(list);
				}
			}
			return null;
		}
		@Override
		public void onPostExecute(int updateId, PlayTraceBoolean result) {
		}
		
	}

	/**
	 * 提交单条播放记录的任务线程（及时提交）
	 * */
	private static class SubmitPlayTrace extends LetvHttpAsyncTask<PlayTraceBoolean> {

		private int cid;

		private int pid;

		private int vid;

		private int nvid;

		private int vtype;

		private long htime;

		public void init(int cid, int pid, int vid, int nvid, int vtype, long htime) {
			this.cid = cid;
			this.pid = pid;
			this.vid = vid;
			this.nvid = nvid;
			this.vtype = vtype;
			this.htime = htime;
		}

		public SubmitPlayTrace(Context context) {
			super(context);
		}

		@Override
		public LetvDataHull<PlayTraceBoolean> doInBackground() {
			LetvDataHull<PlayTraceBoolean> datahull = LetvHttpApi.submitPlayTrace(0, cid + "", pid + "", vid
					+ "", nvid + "", LetvUtil.getUID(), vtype + "", "2", htime + "", PreferencesManager
					.getInstance().getSso_tk(), new PlayTraceBooleanParser());
			if (datahull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				DBManager.getInstance().getPlayTrace().save2Normal(pid, vid);
			}

			return null;
		}

		@Override
		public void onPostExecute(int updateId, PlayTraceBoolean result) {
		}
	}

	/**
	 * 批量提交播放记录的任务线程（延时提交）
	 * */
	private static class SubmitPlayTraces extends LetvHttpAsyncTask<PlayTraceBoolean> {

		private boolean update = true ;
		
		public SubmitPlayTraces(Context context , boolean update) {
			super(context);
			this.update = update ;
		}

		@Override
		public LetvDataHull<PlayTraceBoolean> doInBackground() {
			if(!PreferencesManager.getInstance().isLogin()){
				return null ;
			}
			
			boolean isSucceed = true ;
			
			// 提交滞留更新的播放记录
			PlayRecordList list = DBManager.getInstance().getPlayTrace().getTagSubmits();

			if (list != null && list.size() > 0) {
				int count = list.size() % 10 == 0 ? list.size() / 10 : list.size() / 10 + 1;
				for (int i = 0; i < count; i++) {
					List<PlayRecord> cl = list.subList(i * 10, (i * 10 + 10) < list.size() ? i + 10 : list.size());
					
					if (cl != null && cl.size() > 0) {
						LetvDataHull<PlayTraceBoolean> datahull = LetvHttpApi.submitPlayTraces(0, LetvUtil.getUID(), getSubmitString(list), PreferencesManager.getInstance().getSso_tk(), new PlayTraceBooleanParser());
						if (datahull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
							for (PlayRecord record : cl) {
								DBManager.getInstance().getPlayTrace().save2Normal(record.getAlbumId(), record.getVideoId());
							}
						} else {
							isSucceed = false ;
						}
					}
				}
			}

			// 提交滞留删除的播放记录
			list = DBManager.getInstance().getPlayTrace().getTagDeletes();
			if (list != null && list.size() > 0) {
				int count = list.size() % 10 == 0 ? list.size() / 10 : list.size() / 10 + 1;
				for (int i = 0; i < count; i++) {
					List<PlayRecord> cl = list.subList(i * 10, (i * 10 + 10) < list.size() ? i + 10 : list.size());
					if (cl != null && cl.size() > 0) {
						LetvDataHull<PlayTraceBoolean> dataHull = LetvHttpApi.deletePlayTraces(0, null, null,
								LetvUtil.getUID(), getDeleteString(cl), "0", "0", PreferencesManager.getInstance().getSso_tk(), new PlayTraceBooleanParser());

						if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
							deletePlayRecord(list);
						} else {
							isSucceed = false ;
						}
					}
				}
				
			}
			
			if(update){
				LetvDataHull<PlayRecordList> dataHull = LetvHttpApi.getPlayTraces(0, LetvUtil.getUID(), "1", String.valueOf(PAGE_SIZE),
						PreferencesManager.getInstance().getSso_tk(), new PlayRecordParser());

				if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
					list = dataHull.getDataEntity();
					savePlayRecord(list);
				} else {
					isSucceed = false ;
				}
			}
			
			if(isSucceed){
//				PreferencesManager.getInstance().setisPlayCloud(true);
			}

			return null;
		}

		@Override
		public void onPostExecute(int updateId, PlayTraceBoolean result) {
		}
	}

	/**
	 * 拼接批量删除的参数串
	 * */
	private static String getDeleteString(List<PlayRecord> list) {

		StringBuilder builder = new StringBuilder();
		builder.append("(");
		int i = 0;
		for (PlayRecord record : list) {
			if (i != 0) {
				builder.append(",");
			}
			builder.append("vid:");
			builder.append(record.getVideoId());
			builder.append(",");
			builder.append("pid:");
			builder.append(record.getAlbumId());

			i++;
		}
		builder.append(")");

		return builder.toString();
	}

	/**
	 * 提交播放记录的参数串
	 * */
	private static String getSubmitString(List<PlayRecord> list) {

		JSONArray array = new JSONArray();
		try {
			for (PlayRecord record : list) {
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("uid", record.getUserId());
				jsonObject.put("vid", record.getVideoId());
				jsonObject.put("cid", record.getChannelId());
				jsonObject.put("pid", record.getAlbumId());
				jsonObject.put("nvid", record.getVideoNextId());
				jsonObject.put("vtype", record.getVideoType());
				jsonObject.put("from", record.getFrom());
				jsonObject.put("htime", record.getPlayedDuration());
				jsonObject.put("utime", record.getUpdateTime());

				array.put(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (array.length() > 0) {
			return array.toString();
		}

		return "";
	}
	
	private static void deletePlayRecord(PlayRecordList list) {
		for (PlayRecord record : list) {
			
			int albumId = record.getAlbumId();
			int videoId = record.getVideoId();
			
			if (albumId > 0) {
				DBManager.getInstance().getPlayTrace().deleteByPid(albumId);
			} else {
				DBManager.getInstance().getPlayTrace().deleteByVid(videoId);
			}
		}
	}
	
	private static void savePlayRecord(PlayRecordList list) {
		for (PlayRecord record : list) {
			DBManager.getInstance().getPlayTrace().savePlayTrace(
					record.getChannelId(), 
					record.getAlbumId(), 
					record.getVideoId(), 
					record.getVideoNextId(),
					record.getUserId(), 
					record.getFrom().getInt(), 
					record.getVideoType(), 
					record.getTotalDuration(),
					record.getPlayedDuration(), 
					record.getUpdateTime(), 
					record.getTitle(),
					record.getImg(), 
					0, 
					record.getCurEpsoid(),
					record.getImg300());
		}
	}
}
