package com.letv.watchball.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letv.android.slidingmenu.lib.app.SlidingFragmentActivity;
import com.letv.watchball.R;
import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.bean.MatchList.Body.OriginalColumn;

/**
 * 原创节目Fragment
 * 
 * @author Liuheyuan
 * 
 */
public class OriginalFragment extends Fragment {

	private TextView original_fragment_title_tag;

	private OriginalVideoListFragment original_fragment_videoList;
	
	private OriginalColumn originalColumn;

	 private View view;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {
	        view = inflater.inflate(R.layout.original_fragment, container, false);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
		
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getView()!=null){
		original_fragment_title_tag = (TextView) getView().findViewById(R.id.original_fragment_title_tag);
		original_fragment_videoList = (OriginalVideoListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.original_fragment_videoList);
		getView().findViewById(R.id.toggle_left).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MainActivity)getActivity()).toggle();
			}
		});
		}
	}
	
	/**
	 * 设置赛事内容
	 * @param originalColumn
	 */
	public void setOriginalColumn(OriginalColumn originalColumn) {
		if (null != this.originalColumn && originalColumn.id == this.originalColumn.id) {
			return;
		}
		this.originalColumn = originalColumn;
		
		original_fragment_title_tag.setText(originalColumn.name);
		
		original_fragment_videoList.setOriginalColumn(originalColumn);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(R.id.original_fragment);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		if(null != fragment&&!ft.isEmpty()){
			ft.remove(fragment).commitAllowingStateLoss();
		}
	}
	
}
