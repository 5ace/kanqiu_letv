package com.letv.watchball.async;

import android.content.Context;
import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.AlbumNewParse;
import com.letv.watchball.ui.impl.BasePlayActivity;


public class RequestAlbumByIdTask extends LetvHttpAsyncTask<AlbumNew> {
	private String vplayId;
//	private LoadingDialog loadingDialog;

	public RequestAlbumByIdTask(Context context, String vplayId) {
		super(context);
		this.vplayId = vplayId;
//		showDialog();
	}

	@Override
	public LetvDataHull<AlbumNew> doInBackground() {
		return LetvHttpApi.requestGetAlbumById(0, vplayId, new AlbumNewParse());
	}

	@Override
	public void onPostExecute(int updateId, AlbumNew result) {
		cancelDialog();
		if (result != null) {
			if (result.getType() == AlbumNew.Type.VRS_MANG) {
				BasePlayActivity.launch(context, result.getId(), Long.parseLong(vplayId),
                                BasePlayActivity.LAUNCH_FROM_HOME);
			} else {
				BasePlayActivity.launch(context, 0, Long.parseLong(vplayId), BasePlayActivity.LAUNCH_FROM_HOME);
			}
		}
	}
	
	@Override
	public void netNull() {
		super.netNull();
		cancelDialog();
	}
	@Override
	public void netErr(int updateId, String errMsg) {
		super.netErr(updateId, errMsg);
		cancelDialog();
	}
	@Override
	public void dataNull(int updateId, String errMsg) {
		super.dataNull(updateId, errMsg);
		cancelDialog();
	}

//	private void showDialog() {
//		if (loadingDialog == null || !loadingDialog.isShowing()) {
//			if (context instanceof Activity) {
//				if (!((Activity) context).isFinishing() && !context.isRestricted()) {
//					try {
//						loadingDialog = new LoadingDialog(context, R.string.dialog_loading);
//						loadingDialog.setCancelable(true);
//						loadingDialog.show();
//					} catch (Throwable e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}

//	private void cancelDialog() {
//		if (loadingDialog != null && loadingDialog.isShowing()) {
//			try {
//				loadingDialog.dismiss();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
