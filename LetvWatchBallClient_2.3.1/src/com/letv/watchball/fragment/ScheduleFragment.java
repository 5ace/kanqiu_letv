package com.letv.watchball.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.activity.MainActivity;
import com.letv.watchball.bean.MatchList.Body.Match;

/**
 * 赛程列表fragment
 * 
 * @author Liuheyuan
 * 
 */
public class ScheduleFragment extends Fragment {
	private TextView schedule_fragment_title_tag;
	private RadioGroup schedule_fragment_title_radioGroup;
	private RadioButton schedule_fragment_radio_video;
	private RadioButton schedule_fragment_radio_schedule;
	private RadioButton schedule_fragment_radio_ranker;

	private EventsVideoNewsFragment schedule_fragment_group_video;
	private GScheduleListFragment schedule_fragment_group_schedule;
	private RankerFragment schedule_fragment_group_rank;
	private View view;
	private Match match;
	
	public ScheduleFragment() {
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		container.removeAllViews();
		
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {
	        view = inflater.inflate(R.layout.schedule_fragment, container, false);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
//		View view=inflater.inflate(R.layout.schedule_fragment,container, false);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getView()!=null){
		schedule_fragment_title_tag = (TextView) getView().findViewById(R.id.schedule_fragment_title_tag);
		schedule_fragment_title_radioGroup = (RadioGroup) getView().findViewById(R.id.schedule_fragment_title_radioGroup);
		schedule_fragment_radio_video = (RadioButton) getView().findViewById(R.id.schedule_fragment_radio_video);
		schedule_fragment_radio_schedule = (RadioButton) getView().findViewById(R.id.schedule_fragment_radio_schedule);
		schedule_fragment_radio_ranker = (RadioButton) getView().findViewById(R.id.schedule_fragment_radio_ranker);

		schedule_fragment_group_video = (EventsVideoNewsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.schedule_fragment_group_video);
		schedule_fragment_group_schedule = (GScheduleListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.schedule_fragment_group_schedule);
		schedule_fragment_group_rank = (RankerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.schedule_fragment_group_rank);

		schedule_fragment_title_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (checkedId == R.id.schedule_fragment_radio_video) {
					// 点击新闻
//					schedule_fragment_radio_video.setTextColor(getResources().getColor(R.color.letv_header_tv_sel));
//					schedule_fragment_radio_schedule.setTextColor(getResources().getColor(R.color.letv_header_tv_nor));
//					schedule_fragment_radio_ranker.setTextColor(getResources().getColor(R.color.letv_header_tv_nor));
					
					schedule_fragment_group_video.getView().setVisibility(View.VISIBLE);
					schedule_fragment_group_schedule.getView().setVisibility(View.GONE);
					schedule_fragment_group_rank.getView().setVisibility(View.GONE);
					
					schedule_fragment_group_video.setMatch(match);

				} else if (checkedId == R.id.schedule_fragment_radio_schedule) {
					// 点击直播
//					schedule_fragment_radio_video.setTextColor(getResources().getColor(R.color.letv_header_tv_nor));
//					schedule_fragment_radio_schedule.setTextColor(getResources().getColor(R.color.letv_header_tv_sel));
//					schedule_fragment_radio_ranker.setTextColor(getResources().getColor(R.color.letv_header_tv_nor));
					
					schedule_fragment_group_video.getView().setVisibility(View.GONE);
					schedule_fragment_group_schedule.getView().setVisibility(View.VISIBLE);
					schedule_fragment_group_rank.getView().setVisibility(View.GONE);
					
					schedule_fragment_group_schedule.setMatch(match);

				} else if (checkedId == R.id.schedule_fragment_radio_ranker) {
					// 点击排名
//					schedule_fragment_radio_video.setTextColor(getResources().getColor(R.color.letv_header_tv_nor));
//					schedule_fragment_radio_schedule.setTextColor(getResources().getColor(R.color.letv_header_tv_nor));
//					schedule_fragment_radio_ranker.setTextColor(getResources().getColor(R.color.letv_header_tv_sel));
					
					schedule_fragment_group_video.getView().setVisibility(View.GONE);
					schedule_fragment_group_schedule.getView().setVisibility(View.GONE);
					schedule_fragment_group_rank.getView().setVisibility(View.VISIBLE);

					schedule_fragment_group_rank.setMatch(match);
				}

			}
		});
		getView().findViewById(R.id.evnet_toggle_left).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MainActivity)getActivity()).toggle();
			}
		});
		}
	}
	
	/**
	 * 设置赛事数据
	 * 
	 * @param match
	 */
	public void setMatch(Match match) {
		if(null != this.match && this.match.name.equals(match.name)){
			return;
		}
		
		this.match = match;
		schedule_fragment_title_tag.setText(match.name);
		
//		if(match.schedule.equals("1")){
//			//支持赛程
//			schedule_fragment_radio_schedule.setEnabled(true);
//		}else{
//			//不支持赛程
//			schedule_fragment_radio_schedule.setEnabled(false);
//		}
		
//		if(match.rank.equals("1")){
//			//支持排名
//			schedule_fragment_radio_ranker.setEnabled(true);
//		}else{
//			//不支持排名
//			schedule_fragment_radio_ranker.setEnabled(false);
//		}
		//对欧冠篮球做特俗处理
		if(match.type.equals("296")||match.name.equals("欧冠篮球")){
			schedule_fragment_title_radioGroup.setVisibility(View.GONE);
		}else{
			schedule_fragment_title_radioGroup.setVisibility(View.VISIBLE);
		}
		if(match.type.equals("296")||match.name.equals("NBA")){
			schedule_fragment_title_radioGroup.setVisibility(View.GONE);
		}else{
			schedule_fragment_title_radioGroup.setVisibility(View.VISIBLE);
		}

		schedule_fragment_radio_schedule.setChecked(true);
		schedule_fragment_group_schedule.setMatch(match);
            schedule_fragment_title_radioGroup.check(R.id.schedule_fragment_radio_video);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(R.id.schedule_fragment);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		if(null != fragment&&!ft.isEmpty()){
			ft.remove(schedule_fragment_group_video).commitAllowingStateLoss();
			ft.remove(schedule_fragment_group_schedule).commitAllowingStateLoss();
			ft.remove(fragment).commitAllowingStateLoss();
		
		}
		
	
	}

}
