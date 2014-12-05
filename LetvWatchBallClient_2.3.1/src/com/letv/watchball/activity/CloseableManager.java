package com.letv.watchball.activity;

import java.util.ArrayList;

/**
 * Created by SongMengyu on 14-2-17.
 */
public class CloseableManager {

	private static CloseableManager manager = null;
	private final ArrayList<Icloseable> mList;

	private CloseableManager() {
		mList = new ArrayList<Icloseable>();
	}

	public static CloseableManager getInstance() {
		if (null == manager) {
			manager = new CloseableManager();
		}
		return manager;
	}

	public void closeAll() {
		for (int i = 0; i < mList.size(); i++) {
			mList.get(i).close();
		}
		mList.clear();
	}

	public void close(Icloseable icloseable) {
		mList.remove(icloseable);
	}

	public void add(Icloseable icloseable) {
		mList.add(icloseable);
	}

}
