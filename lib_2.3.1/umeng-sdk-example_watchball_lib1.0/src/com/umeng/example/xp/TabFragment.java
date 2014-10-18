package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.view.ExchangeViewManager;

public class TabFragment extends BaseSinglePaneActivity {
	@Override
	protected Fragment onCreatePane() {
		return new TabsFragment();
	}

	public static class TabsFragment extends Fragment implements OnTabChangeListener {
		public static final String TAB_APP = "装机必备";
		public static final String TAB_RECOMMENT = "乐视推荐";
		
		public static final String TAB_APP_ID = "40825";
//		public static final String TAB_RECOMMENT_ID = "40829";

		private View mRoot;
		private TabHost mTabHost;
		private int mCurrentTab;
		private TabWidget mTabWidget;
		private Context mContext;
		
		private TextView txtView1;
		private TextView txtView2;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mContext = activity;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRoot = inflater.inflate(R.layout.umeng_example_xp_tabfragment, container, false);
			mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
			mTabWidget = (TabWidget) mRoot.findViewById(android.R.id.tabs);
			setupTabs();
			return mRoot;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setRetainInstance(true);

			mTabHost.setOnTabChangedListener(this);
			mTabHost.setCurrentTab(mCurrentTab);

			ListView l1 = (ListView) mRoot.findViewById(R.id.list_1);
			ListView l2 = (ListView) mRoot.findViewById(R.id.list_2);

			ViewGroup vg1 = (ViewGroup) mRoot.findViewById(R.id.father1);
			ViewGroup vg2 = (ViewGroup) mRoot.findViewById(R.id.father2);

			ExchangeDataService exchangeDataService1 = new ExchangeDataService();
			exchangeDataService1.slot_id = TAB_APP_ID;
			exchangeDataService1.setKeywords(TAB_APP);
			new ExchangeViewManager(mContext, exchangeDataService1).addView(vg1, l1);
			ExchangeDataService exchangeDataService2 = new ExchangeDataService();
			exchangeDataService2.setKeywords(TAB_RECOMMENT);
			new ExchangeViewManager(mContext, exchangeDataService2).addView(vg2, l2);
		}

		private void setupTabs() {
			mTabHost.setup(); // must call this before adding  tabs!

			View view1 = LayoutInflater.from(mContext).inflate(R.layout.umeng_example_tab_indicator, null);
			txtView1 = (TextView) view1.findViewById(R.id.umeng_example_tab_text);
			txtView1.setBackgroundResource(R.drawable.tab_left);
			txtView1.setText(TAB_APP);
			View view2 = LayoutInflater.from(mContext).inflate(R.layout.umeng_example_tab_indicator, null);
			txtView2 = (TextView) view2.findViewById(R.id.umeng_example_tab_text);
			txtView2.setBackgroundResource(R.drawable.tab_right);
			txtView2.setText(TAB_RECOMMENT);

			mTabHost.addTab(mTabHost.newTabSpec(TAB_APP).setIndicator(view1)
					.setContent(R.id.list_1));
			mTabHost.addTab(mTabHost.newTabSpec(TAB_RECOMMENT).setIndicator(view2)
					.setContent(R.id.list_2));
		}

		@Override
		public void onTabChanged(String tabId) {
			if (TAB_APP.equals(tabId)) {
				txtView1.setSelected(true);
				txtView2.setSelected(false);
				return;
			}
			if (TAB_RECOMMENT.equals(tabId)) {
				txtView1.setSelected(false);
				txtView2.setSelected(true);
				return;
			}
		}
	}

}
