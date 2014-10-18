package com.letv.watchball.fragment;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.letv.watchball.R;
import com.letv.watchball.adapter.LiveAdapter;

public class GBaseFragment extends Fragment {
	protected LiveAdapter adapter;
	protected PinnedHeaderListView listView;
	protected	View root;
	public GBaseFragment() {
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.fragment_home_live, null);
		

		if (root != null) {
	        ViewGroup parent = (ViewGroup) root.getParent();
	        if (parent != null)
	            parent.removeView(root);
	    }
	    try {
	    	root = inflater.inflate(R.layout.fragment_game_base, container, false);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
	    if(root!=null){
			listView = (PinnedHeaderListView) root.findViewById(R.id.main_listview);
			adapter = new LiveAdapter(getActivity(),false);
			listView.setAdapter(adapter);
	    }
		return root;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		 if (root != null) {
		        ViewGroup parent = (ViewGroup) root.getParent();
		        if (parent != null)
		            parent.removeView(root);
		    }
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_game_base);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		if(null != fragment&&!ft.isEmpty()){
			ft.remove(fragment).commitAllowingStateLoss();
		}
	
	}
	
	public void notifyDateChanged(){
		adapter.notifyDataSetChanged();
		//展开所有parent
//		for (int i = 0; i < adapter.listParent.size(); i++) {
//			listView.expandGroup(i);
//		}
//		//设置parent不可点击
//		listView.setOnGroupClickListener(new OnGroupClickListener() {
//			
//			@Override
//			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//				return true;
//			}
//		});
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		TextView emptyView = new TextView(getActivity()){{
//			setText("当前没有关注球队，先选择一个关注的球队吧！");
//		}};
//		((ViewGroup) getListView().getParent()).addView(emptyView);
//		getListView().setEmptyView(emptyView);
	}
	
}
