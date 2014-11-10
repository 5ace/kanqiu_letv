package com.letv.watchball.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.letv.watchball.ui.DetailLivePlayFragmentMannager;
import com.letv.watchball.ui.DetailPlayFragmentMannager;

public class DetailLivePlayPagerAdapter extends FragmentStatePagerAdapter{

	private DetailLivePlayFragmentMannager fragmentMannager ;
	
	private FragmentManager fm ;
	
	public DetailLivePlayPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm ;
		fragmentMannager = new DetailLivePlayFragmentMannager() ;
	}

	@Override
	public Fragment getItem(int pos) {
		return fragmentMannager.newInstance(pos);
	}

	@Override
	public int getCount() {
		return 4;
	}
	
	public void format(){
		fragmentMannager.destroy(fm);
		fragmentMannager = null ;
	}
}
