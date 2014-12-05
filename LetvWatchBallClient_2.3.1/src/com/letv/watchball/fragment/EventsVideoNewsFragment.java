package com.letv.watchball.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.letv.watchball.R;
import com.letv.watchball.bean.MatchList.Body.Match;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.UIs;

/**
 * @author Liuheyuan 赛事视频列表fragmeng
 */
public class EventsVideoNewsFragment extends Fragment implements
		OnClickListener {

	private VideoListFragment videoListFragment;

	private Match match;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View root = inflater.inflate(R.layout.events_video_news_fragment,
				null, false);
		videoListFragment = (VideoListFragment) getActivity()
				.getSupportFragmentManager().findFragmentById(
						R.id.fragment_videoList);
		videoListFragment.showFocusPic = false;

		return root;
	}

	@Override
	public void onClick(View v) {
		View bottom_line_left = getView().findViewById(R.id.bottom_line_left);
		View bottom_line_right = getView().findViewById(R.id.bottom_line_right);
		LayoutParams bottom_line_leftParams = bottom_line_left
				.getLayoutParams();
		LayoutParams bottom_line_rightParams = bottom_line_right
				.getLayoutParams();
		switch (v.getId()) {
		case R.id.events_video_news_fragment_button_news:
			// 最新
			bottom_line_leftParams.height = UIs.dipToPx(3);
			bottom_line_rightParams.height = UIs.dipToPx(1);
			bottom_line_left.requestLayout();
			bottom_line_right.requestLayout();

			videoListFragment.setCurrentOrderBy_itemId_requestData(match.type,
					LetvConstant.VideoNewsOrderBy.DATE);
			break;
		case R.id.events_video_news_fragment_button_hot:
			// 最热
			bottom_line_leftParams.height = UIs.dipToPx(1);
			bottom_line_rightParams.height = UIs.dipToPx(3);
			bottom_line_left.requestLayout();
			bottom_line_right.requestLayout();

			videoListFragment.setCurrentOrderBy_itemId_requestData(match.type,
					LetvConstant.VideoNewsOrderBy.PLAYCOUNT);
		default:
			break;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getView().findViewById(R.id.events_video_news_fragment_button_news)
				.setOnClickListener(this);
		getView().findViewById(R.id.events_video_news_fragment_button_hot)
				.setOnClickListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(
				R.id.events_video_news_fragment);
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		if (null != fragment && !ft.isEmpty()) {
			ft.remove(fragment).commitAllowingStateLoss();
		}

	}

	public void setMatch(Match match) {

		if ((null == match)
				|| (null != this.match && this.match.name.equals(match.name))) {
			return;
		}
		this.match = match;
		// events_video_news_fragment_radioGroup.check(R.id.events_video_news_fragment_button_news);
		if (null == videoListFragment || match.type.equals("")) {
			return;
		}
		getView().findViewById(R.id.events_video_news_fragment_button_news)
				.performClick();
		// videoListFragment.setCurrentOrderBy_itemId_requestData(match.type,
		// LetvConstant.VideoNewsOrderBy.DATE);
		onDestroyView();
	}
}
