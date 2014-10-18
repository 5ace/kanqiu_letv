package com.letv.watchball.share;

import android.content.Context;

import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Share;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.ShareLinkParser;

public class RequestShareLinkTask extends LetvHttpAsyncTask<Share>{
	

	public RequestShareLinkTask(Context context) {
		super(context );
	}

	@Override
	public LetvDataHull<Share> doInBackground() {
		return LetvHttpApi.requestShareLink(0, new ShareLinkParser());
	}

	@Override
	public void onPostExecute(int updateId, Share result) {
		if(result!=null){
			LetvShareControl.getInstance().setShare(result);
			LetvShareControl.getInstance().setIsShare(true);
		}else {
			LetvShareControl.getInstance().setShare(null);
			LetvShareControl.getInstance().setIsShare(false);
		}
	}
}
