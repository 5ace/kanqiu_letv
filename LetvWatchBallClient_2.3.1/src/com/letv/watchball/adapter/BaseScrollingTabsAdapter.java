package com.letv.watchball.adapter;

import android.content.Context;
import android.view.View;

public abstract class BaseScrollingTabsAdapter {

	protected final Context context;

	public BaseScrollingTabsAdapter(Context context) {
		this.context = context;
	}

	public abstract View getView(int position);

	public abstract int getCount();

	public abstract void updateView(View selectedTab, View prevTab, int curPos,
			int prevPos);
}
