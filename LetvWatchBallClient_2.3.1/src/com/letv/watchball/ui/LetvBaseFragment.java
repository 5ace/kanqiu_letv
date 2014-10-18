package com.letv.watchball.ui;

import android.support.v4.app.Fragment;

public class LetvBaseFragment extends Fragment{

	@Override
	public void onDestroy() {
//		LetvCacheMannager.getInstance().clearCacheBitmap();
		super.onDestroy();
	}
}
