package com.letv.watchball.async;

import java.util.ArrayList;

import android.content.Context;

import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.dao.StatisCacheBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.bean.MessageBeanListMap;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.DialogMsgTraceHandler;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.MessageBeanListParser;

public class RequestInfoTask extends LetvHttpAsyncTask<MessageBeanListMap> {

	public RequestInfoTask(Context context) {
		super(context);
	}

	@Override
	public LetvDataHull<MessageBeanListMap> doInBackground() {
		PreferencesManager.getInstance().saveDialogMsgIsSuc(false);
		DialogMsgTraceHandler dialogMsgTrace = DBManager.getInstance()
				.getDialogMsgTrace();
		if (!dialogMsgTrace.getDialogMsgSize()) {
			String[] dialogMsgArrays = LetvApplication.getInstance()
					.getResources().getStringArray(R.array.dialog_msg_arrays);
			if (dialogMsgArrays != null) {
				dialogMsgTrace.clearAll();// 清除数据再插入，效率更高
				int len = dialogMsgArrays.length;
				for (int i = 0; i < len; i += 3) {
					dialogMsgTrace.saveDialogMsg(dialogMsgArrays[i],
							dialogMsgArrays[i + 1], dialogMsgArrays[i + 2]);
					// LetvHttpLog.Err("dialogMsgArrays["+i+"]="+dialogMsgArrays[i]);
					// LetvHttpLog.Err("dialogMsgArrays["+(i+1)+"]="+dialogMsgArrays[i+1]);
					// LetvHttpLog.Err("dialogMsgArrays["+(i+2)+"]="+dialogMsgArrays[i+2]);
				}
				PreferencesManager.getInstance().saveDialogMsgIsSuc(true);
			}
		}
		PreferencesManager.getInstance().saveDialogMsgIsSuc(true);
		// 提交上报错误数据
		ArrayList<StatisCacheBean> mStatisCacheBeanList = DataStatistics
				.getInstance().getAllErrorCache(context);
		if (mStatisCacheBeanList != null && mStatisCacheBeanList.size() > 0) {
			for (StatisCacheBean statisCacheBean : mStatisCacheBeanList) {
				DataStatistics.getInstance().submitErrorInfo(context,
						statisCacheBean);
			}
		}
		return LetvHttpApi.requestDialogMsgInfo(0, PreferencesManager
				.getInstance().getDialogMsgMarkid(),
				new MessageBeanListParser());
	}

	@Override
	public void onPostExecute(int updateId, MessageBeanListMap result) {
	}

	@Override
	public void netNull() {
	}

	@Override
	public void netErr(int updateId, String errMsg) {
	}

}
