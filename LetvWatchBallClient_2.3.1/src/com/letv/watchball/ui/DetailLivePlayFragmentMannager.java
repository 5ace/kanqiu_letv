package com.letv.watchball.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.letv.watchball.ui.impl.HalfPlayGetCommentsFragment;
import com.letv.watchball.ui.impl.HalfPlayIntroductionFragment;
import com.letv.watchball.ui.impl.HalfPlayShareFragment;
import com.letv.watchball.ui.impl.HalfPlayVideosFragment;

public class DetailLivePlayFragmentMannager {

	private Fragment[] fragments;

	public DetailLivePlayFragmentMannager() {
		fragments = new Fragment[4];
	}

	public Fragment newInstance(int pageId) {
		switch (pageId) {
		case 0:
			if (fragments[pageId] == null) {
				fragments[pageId] = new HalfPlayIntroductionFragment();
			}
		case 1:
			if (fragments[pageId] == null) {
				fragments[pageId] = new HalfPlayGetCommentsFragment();
			}

			return fragments[pageId];
		case 2:
			if (fragments[pageId] == null) {
				fragments[pageId] = new HalfPlayVideosFragment();
			}

			return fragments[pageId];
		case 3:
			if (fragments[pageId] == null) {
				fragments[pageId] = new HalfPlayShareFragment();
			}

			return fragments[pageId];
		}

		return null;
	}

	public void destroy(FragmentManager fm) {
		for (int i = 0; i < fragments.length; i++) {
			if (fragments[i] != null) {
				fm.beginTransaction().remove(fragments[i]);
				// fm.beginTransaction().commit();
				fm.beginTransaction().commitAllowingStateLoss();// 如果在Activity保存玩状态后再给它添加Fragment就会出错。解决办法就是把commit（）方法替换成
																// commitAllowingStateLoss()就行

				fragments[i] = null;
			}
		}
		fragments = null;
	}
}
