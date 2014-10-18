package com.letv.watchball.fragment;

import java.util.ArrayList;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.adapter.LiveAdapter;
import com.letv.watchball.adapter.LiveAdapter.MODE;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.MatchList.Body.Match;
import com.letv.watchball.bean.MatchScheduleListBean;
import com.letv.watchball.bean.MatchScheduleListBean.MatchSchedule.Round;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.view.PullToRefreshListViewHeader;

/**
 * 赛程列表Fragment
 * 
 * @author Liuheyuan
 * 
 */
public class GScheduleListFragment extends Fragment {

	/**
	 * 轮次gallery
	 */
//	private Gallery schedule_list_fragment_galleryFlow;

//	private GalleryFlowAdapter galleryFlowAdapter;

	/**
	 * 赛事列表
	 */
//	private MatchExpandableLisView schedule_list_fragment_listView;
//	private LiveAdapter listViewAdapter;
	protected LiveAdapter adapter;
	protected PullToRefreshListViewHeader listView;
	/**
	 * 轮次数据
	 */
	private Round[] rounds = new Round[] {};

	private String match_type = "";
	private String round_key = "";

	private RequestData requestData;
	
	private Match match;
	private int type=0;  //1下一页，-1上一页
	private int Prepage=0;
	private int Nextpage=0;
	private int selection=0;
	private boolean IsSingle=true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.schedule_list_fragment,container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

//		schedule_list_fragment_galleryFlow = (Gallery) getView().findViewById(R.id.schedule_list_fragment_galleryFlow);
//		galleryFlowAdapter = new GalleryFlowAdapter();
//		schedule_list_fragment_galleryFlow.setAdapter(galleryFlowAdapter);
		listView = (PullToRefreshListViewHeader) getView().findViewById(R.id.schedule_list_fragment_listView);
		adapter = new LiveAdapter(getActivity(),false);
		listView.getRefreshableView().setFadingEdgeLength(0);
		listView.getRefreshableView().setDivider(null);
		adapter.setMode(MODE.SCHEDULE);
		listView.getRefreshableView().setAdapter(adapter);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2<PinnedHeaderListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<PinnedHeaderListView> refreshView) {
//				//刷新第一页数据
				loadPrePage();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<PinnedHeaderListView> refreshView) {
				loadNextPage();
//				mAutoRefreshHandler.progress=0;//刷新时候进度条归零
//				mHomeFragmentLsn.loadRightFragmentData();
			}

		});
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				  if (null != listView){
					  listView.getRefreshableView().onScrollStateChanged(view, scrollState);
	                }
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				  if (null != listView){
					  listView.getRefreshableView().onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	                }
			}
		});
		
		
//		schedule_list_fragment_galleryFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Round currentRound = (Round) parent.getItemAtPosition(position);
//				if(currentRound.key == round_key){
//					return;
//				}
//				round_key = currentRound.key;
//				requestData();
//			}
//		});
	}

	protected void loadPrePage() {
		// TODO Auto-generated method stub
	
		if(Prepage>0 && Prepage < rounds.length -1){
		type=-1;
		Prepage--;
		selection = 0;//上拉时候坐标初始化
		System.out.println("loadPrePage Prepage =="+Prepage);
		Round currentRound = rounds[Prepage];
		round_key = currentRound.key;
		requestData();
		}else{
			Toast.makeText(getActivity(),"没有数据了", Toast.LENGTH_SHORT).show();
			listView.onRefreshComplete();
		
		}
	}

	protected void loadNextPage() {
		// TODO Auto-generated method stub
	
		if(Nextpage>0&&Nextpage<rounds.length-1){
			type=1;
			selection=0;
			Nextpage++;
			System.out.println("loadNextPage Nextpage =="+Nextpage);
			Round currentRound = rounds[Nextpage];
			round_key = currentRound.key;
			requestData();
		}else{
			Toast.makeText(getActivity(),"没有数据了", Toast.LENGTH_SHORT).show();
			listView.onRefreshComplete();
			
		}
	}

	/**
	 * 设置比赛type
	 * 
	 * @param match_type
	 */
	public void setMatch_type(String match_type) {
		this.match_type = match_type;
		this.round_key = "";
	}
	
	/**
	 * 设置比赛信息
	 * @param match
	 */
	public void setMatch(Match match){
		if(null != this.match && this.match.type.equals(match.type)){
			return;
		}
		if(match==null || match.type.equals("")){
			return;
		}
		this.match = match;
		//match变化时，先clear之前的数据
		adapter.clear();
		IsSingle=true;
		adapter.notifyDataSetChanged();
		setMatch_type(match.type);
		requestData();
	}

	public void requestData() {
		if (null != requestData) {
			requestData.cancel(true);
		}
		requestData = new RequestData(getActivity());
		requestData.start();
	}

	/**
	 * 请求赛事数据
	 * 
	 * @author Liuheyuan
	 */
	private class RequestData extends LetvHttpAsyncTask<MatchScheduleListBean> {

		public RequestData(Context context) {
			super(context, true);
		}

		@Override
		public LetvDataHull<MatchScheduleListBean> doInBackground() {
			LetvDataHull<MatchScheduleListBean> dataHull = null;

			dataHull = LetvHttpApi.requestMatchSchedule(match_type, round_key, new LetvGsonParser<MatchScheduleListBean>(0, MatchScheduleListBean.class));
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, MatchScheduleListBean result) {

			if (null != result && null != result.body) {
				if (null != result.body.rounds) {
					rounds = result.body.rounds;
				}
		
				for(int i=0; i<rounds.length; i++){
					
					if(IsSingle&&"1".equals(rounds[i].cur)){
//						schedule_list_fragment_galleryFlow.setSelection(rounds.length * 10 + i);
						Prepage = i;
						Nextpage = i;
						IsSingle=false;
						break;
					}
					
				}
//				if (null != galleryFlowAdapter) {
//					galleryFlowAdapter.notifyDataSetChanged();
//				}
				
				if (null != result.body.match_list && null != adapter) {
//					adapter.clear();
					for (int i = 0; i < result.body.match_list.length; i++) {
				
						if(type==-1){
							String dates=result.body.match_list[i].date+" "+rounds[Prepage].name;
							adapter.listParent.add(0, dates);
							if(i==0){//最后一条数据时候，上面的for循环汉之写的。
								selection=selection-1;
							}
						}else{
							String dates=result.body.match_list[i].date+" "+rounds[Nextpage].name;
							adapter.listParent.add(dates);
						}
						ArrayList<Game> children = new ArrayList<Game>();
						for(int j=0; j<result.body.match_list[i].matches.length; j++ ){
							Game game = result.body.match_list[i].matches[j];
							//手动设置比赛轮次 用于在adapter中显示 
							if(type==-1){
								game.matchName = rounds[Prepage].name;
								}else{
									game.matchName = rounds[Nextpage].name;
								}
							children.add(game);
						}
						if(type==-1){
						adapter.listChild.add(0, children);
						selection+=result.body.match_list[i].matches.length+1;
						}else{
						adapter.listChild.add(children);
						}
					}
		
					adapter.notifyDataSetChanged();
				
					if (type==-1&&selection>=0) {
						listView.getRefreshableView().setSelection(selection);
					}
					type=0;//初始化
					listView.onRefreshComplete();
				}
			}
		

		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			super.dataNull(updateId, errMsg);
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			super.netErr(updateId, errMsg);
		}

		@Override
		public void netNull() {
			super.netNull();
		}

	}

	/**
	 * 轮次adapter
	 * 
	 * @author Liuheyuan
	 */
	class GalleryFlowAdapter extends BaseAdapter {

		private ViewHodler mViewHodler;
		@Override
		public int getCount() {
			if (null != rounds && rounds.length > 0) {
				// return rounds.length;
				return Integer.MAX_VALUE;
			}
			return 0;
		}

		@Override
		public Round getItem(int position) {
			if (null != rounds && rounds.length > 0) {
				// return rounds[position];
				return rounds[position % rounds.length];

			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			if (null != rounds && rounds.length > 0) {
				return position % rounds.length;
			}
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			mViewHodler = new ViewHodler();
			if(null == convertView){
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.schedule_gallery_item, null);
				convertView.setTag(mViewHodler);
			}else{
				mViewHodler = (ViewHodler) convertView.getTag();
			}
			mViewHodler.title = (TextView) convertView.findViewById(R.id.schedule_gallery_item_tv);
			mViewHodler.title.setText(getItem(position).name);
			return convertView;
		}
		
		private class ViewHodler {
			TextView title;
		}

	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		/**
		 * frament 销毁时，移除此fragment
		 */
		Fragment fragment = getFragmentManager().findFragmentById(R.id.schedule_list_fragment);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		if(null != fragment&&!ft.isEmpty()){
			ft.remove(fragment).commitAllowingStateLoss();
		}
	}
}
