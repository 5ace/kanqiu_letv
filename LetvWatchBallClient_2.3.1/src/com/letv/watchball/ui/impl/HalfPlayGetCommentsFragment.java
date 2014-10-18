package com.letv.watchball.ui.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Text;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.adapter.DetailCommentsListViewAdapter;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Comments;
import com.letv.watchball.bean.Comments.Data;
import com.letv.watchball.bean.Video;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.ui.LetvBaseFragment;
import com.letv.watchball.ui.PlayAlbumController.PlayAlbumControllerCallBack;
import com.letv.watchball.ui.PlayController;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PublicLoadLayout;

public class HalfPlayGetCommentsFragment extends LetvBaseFragment implements OnItemClickListener, PlayAlbumControllerCallBack {
	private PlayController playController;
	private PublicLoadLayout root;
	private ListView gridview;
	private DetailCommentsListViewAdapter adapter;
	/**
	 * 直播
	 */
	public static final int LAUNCH_MODE_LIVE = 4;
	/**
	 * 直播 全屏直播
	 */
	public static final int LAUNCH_MODE_LIVE_FULL = 5;
	/**
	 * 数据是否请求失败
	 */
	private boolean isError = true;
	/**
	 * 推荐数据列表
	 */
	private ArrayList<Comments.Data> mComments = new ArrayList<Comments.Data>();
	/**
	 * pageNumber
	 */
	private int pn = 1;
	/**
	 * pageSize
	 */
	private int ps = Integer.MAX_VALUE;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("lhz", "HalfPlayGetCommentsFragment.onCreateView()");
		root = UIs.createPage(getActivity(), R.layout.detailplay_half_comments);
		root.setPadding(1, 0, 1, 0);
		root.setBackgroundResource(R.color.letv_color_ffdfdfdf);
		findView();
		return root;
	}

	private void findView() {
		gridview = (ListView) root.findViewById(R.id.detailplay_half_related_gridview);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		playController = ((BasePlayActivity) getActivity()).mPlayController;
		playController.getCommentsCallBack = this;
	
		adapter = new DetailCommentsListViewAdapter(getActivity());
		adapter.setList(mComments);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(this);
		Log.d("lhz", "HalfPlayGetCommentsFragment.onActivityCreated()");
		
		handlerData(playController.getCommentsCallBackState);
	}

	/**
	 * 更新UI
	 */
	private void updateUI() {
		if (adapter != null) {
			adapter.setList(mComments);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * 请求数据
	 * 
	 */
	List<Data> asList = new ArrayList<Data>();
	private class RequestRecommendTask extends LetvHttpAsyncTask<Comments> {

            public RequestRecommendTask(Context context) {
                  super(context);
                  if (root != null) {
                        root.loading(false);
                  }
            }

		@Override
		public LetvDataHull<Comments> doInBackground() {
			LetvDataHull<Comments> requestDetailRecommendInfo = null;
			AlbumNew mAlbumNew = playController.getAlbum();
			int aid = (int) playController.aid;
			int vid = (int) playController.vid;
			int id = (int) playController.id;
			Video video = playController.getVideo();
			if(((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
				requestDetailRecommendInfo = LetvHttpApi.requestGetComment(0, new LetvGsonParser<Comments>(0, Comments.class), id+"", pn+"", ps+"");
			}else{
				requestDetailRecommendInfo = LetvHttpApi.requestGetComment(0, new LetvGsonParser<Comments>(0, Comments.class), vid+"", pn+"", ps+"");
			}
			return requestDetailRecommendInfo;
		}

		@Override
		public void onPostExecute(int updateId, Comments result) {
			System.out.println("result ="+result);
			mComments.clear();
			asList.clear();
			// 这个判断条件有错
			if (result != null && null != result.body&&null != result.body.comments&&null !=result.body.comments.data&&result.body.comments.data.length > 0) {
				
				for(int i=0;i<result.body.comments.data.length;i++){
					asList.add(result.body.comments.data[i]);
				}
				if(!TextUtils.isEmpty(playController.content)){
				
					asList.add(0, new Comments().newData());
					asList.get(0).user=new Comments().newUser();
					asList.get(0).user.username=PreferencesManager.getInstance().getNickUserName();
					asList.get(0).content=playController.content;
//					mComments.addAll(asList);
				}

				mComments.addAll(asList);
				updateUI();
				if (root != null) {
					root.finish();
				}
			}else{
					if(playController!=null&&playController.content!=null){
						for(int i=0;i<result.body.comments.data.length;i++){
							asList.add(result.body.comments.data[i]);
						}
						asList.add(0, new Comments().newData());
						asList.get(0).user=new Comments().newUser();
						asList.get(0).user.username=PreferencesManager.getInstance().getNickUserName();
						asList.get(0).content=playController.content;
						asList.get(0).vtime="刚刚";
						mComments.addAll(asList);
						updateUI();
						if (root != null) {
							root.finish();
						}
					}else{
						if(null != root){
							root.error("暂无评论!");
						}
					}
				}
			if(asList.size()==0){
				if(null != root){
					root.error("暂无评论!");
				}
			}
			isError = false;
		}

		@Override
		public void netNull() {
			if (root != null) {
				root.error(R.string.get_data_error);
			}
			isError = true;
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			if (root != null) {
				root.error(R.string.get_data_error);
			}
			isError = true;
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			if (root != null) {
				root.error(R.string.get_data_error);
			}
			isError = true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.playController.getCommentsCallBack = null;
		playController = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		if (playAlbumController != null) {
//			Recommend tmp = mComments.get(position);
//			if (tmp.type == 1) {
//				BasePlayActivity.launch(getActivity(), tmp.id, 0, BasePlayActivity.LAUNCH_FROM_RELATED);
//			} else {
//				BasePlayActivity.launch(getActivity(), 0, tmp.id, BasePlayActivity.LAUNCH_FROM_RELATED);
//			}
//		}
//		new RequestRecommendTask(getActivity()).start();
	}

	/**
	 * 根据状态完成数据载入
	 * */
	private void handlerData(int state) {
		if (playController != null) {
			switch (state) {
		
			case PlayAlbumControllerCallBack.STATE_RUNNING:
				if (root != null) {
					root.loading(true);
				}
				break;
			case PlayAlbumControllerCallBack.STATE_FINISH:
				if (root != null) {
//					if (mComments == null || mComments.size() <= 0 || isError) {
					if (mComments != null&&mComments.size() > 0) {
						mComments.clear();
					}	
					if(mComments != null&&mComments.size()==0){
						new RequestRecommendTask(getActivity()).start();
					}
				
//					}
				}
				break;
			case PlayAlbumControllerCallBack.STATE_RETRY:
				if (root != null) {
				
//					if (mComments == null || mComments.size() <= 0 || isError) {
					if (mComments != null&&mComments.size() > 0) {
						mComments.clear();
						asList.clear();
					}
					if(mComments != null&&mComments.size()==0){
						new RequestRecommendTask(getActivity()).start();
					}
					
				}
				break;
			case PlayAlbumControllerCallBack.STATE_NET_NULL:
				if (root != null) {
					root.error(false, false);
				}
				break;
			case PlayAlbumControllerCallBack.STATE_NET_ERR:
				if (root != null) {
					root.error(false, false);
				}
				break;
			case PlayAlbumControllerCallBack.STATE_DATA_NULL:
				if (root != null) {
					root.error(false, false);
				}
				break;
			}
		}
	}

	@Override
	public void notify(int state) {
		handlerData(playController.getCommentsCallBackState);
		updateUI();
	}

	@Override
	public void requestDetails(long cid, String vid) {
		
	}

	@Override
	public void setCurPage(int curPage) {
		// TODO Auto-generated method stub
		
	}
}
