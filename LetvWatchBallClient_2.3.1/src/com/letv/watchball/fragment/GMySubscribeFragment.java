package com.letv.watchball.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.adapter.LiveAdapter.MODE;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Game;
import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.bean.SubscribeGroupList;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.manager.RightFragmentLsn;
import com.letv.watchball.utils.LetvCacheDataHandler;
import com.letv.watchball.utils.LetvSubsribeGameUtil;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.view.EmptyAlertView;
import com.umeng.analytics.MobclickAgent;

public class GMySubscribeFragment extends GBaseFragment {

      //	private ArrayList<Game> gamesList = new ArrayList<Game>();
      private RightFragmentLsn mRightFragmentLsn;
      private ImageView emptyView;
      private boolean isLoaded = false;

      private Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                  adapter.clear();
                  notifyDateChanged();
                  Log.d("TAG","+++++++++++++++++++");
            }
      };


      public void setRightFragmentLsn(RightFragmentLsn mRightFragmentLsn) {
            this.mRightFragmentLsn = mRightFragmentLsn;
            if (null != adapter) {
                  adapter.setRightFragmentLsn(mRightFragmentLsn);
                  adapter.setCanDelete(true);
            }
      }

      @Override
      public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            //todo request server to get the subscribe data

      }

      @Override
      public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            //设置emptyview
            if (emptyView == null) {

                  emptyView = new ImageView(getActivity());
                  emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                  emptyView.setImageResource(R.drawable.empty_subscribe_icon);
                  emptyView.setVisibility(View.GONE);
                  ((ViewGroup) listView.getParent()).addView(emptyView);
                  listView.setEmptyView(emptyView);

//            emptyView = new EmptyAlertView(getActivity());
//            emptyView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//            ((ViewGroup)listView.getParent()).addView(emptyView);
//            emptyView.setVisibility(View.GONE);
//            listView.setEmptyView(emptyView);
            }

            adapter.setMode(MODE.SUBCRIBE);
//		reloadMySubsribe();
      }

      /**
       * 加载预约列表
       */
      public void loadMySubscribe() {
            // 友盟统计 我的预约

            MobclickAgent.onEvent(getActivity(), "subscribesList");
            new RequestMyGames(getActivity()).start();
      }

      public void reflashUI() {
            loadMySubscribe();
            /*
		try {
			for (int i = 0; i < adapter.listChild.size(); i++) {
				// 不显示未预约的game
				for (int j = 0; j < adapter.listChild.get(i).size(); j++) {
					Game game = adapter.listChild.get(i).get(j);
					if (!game.isGameSubscribed) {
						adapter.listChild.get(i).remove(j);
					}
				}
			}
			for (int i = 0; i < adapter.listChild.size(); i++) {
				// 不显示没有game的group
				if (adapter.listChild.get(i).size() == 0) {
					adapter.listChild.remove(i);
					adapter.listParent.remove(i);
				}
			}
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	*/
      }

      /**
       * 添加一个预约
       *
       * @param game
       */
      public void addSubscribe(Game game, String date) {
//		if(!adapter.listParent.contains(date)){
//			adapter.listParent.add(date);
//		}
            int index = 0;
//		if (adapter.listParent.size() == 0) {
//			adapter.listParent.add(date);
//		}
            //group中包含这个date
            if (adapter.listParent.contains(date)) {
                  index = adapter.listParent.indexOf(date);

                  ArrayList<Game> games = adapter.listChild.get(index);
                  for (Game game2 : games) {
                        if (game2.id.equals(game.id)) {
                              games.remove(game2);
                              break;
                        }
                  }
                  games.add(game);
                  Collections.sort(games, new Comparator<Game>() {

                        @Override
                        public int compare(Game object1, Game object2) {
                              return (int) (Integer.parseInt(object1.playTime.replace(":", "")) - Integer.parseInt(object2.playTime.replace(":", "")));
                        }
                  });

            } else {
                  boolean isAdded = false;
                  for (int i = 0; i < adapter.listParent.size(); i++) {
                        if (LetvUtil.timeFormatSubscribeGame(date, "00:00") < LetvUtil.timeFormatSubscribeGame(adapter.listParent.get(i), "00:00")) {
                              adapter.listParent.add(i, date);
                              index = i;
                              isAdded = true;
                              break;
                        }

                  }
                  if (!isAdded) {
                        adapter.listParent.add(date);
                        index = adapter.listParent.size() - 1;
                  }


                  ArrayList<Game> games = new ArrayList<Game>();
                  games.add(game);
                  adapter.listChild.add(index, games);

            }
//		if(adapter.listChild.size() < (index+1)){
//			ArrayList<Game> games = new ArrayList<Game>();
//			games.add(game);
//			adapter.listChild.add(games);
//		}else{
//			ArrayList<Game> games = adapter.listChild.get(index);
//			for (Game game2 : games) {
//				if(game2.id.equals(game.id)){
//					games.remove(game2);
//					break;
//				}
//			}
//			games.add(game);
//			Collections.sort(games, new Comparator<Game>() {
//				
//				@Override
//				public int compare(Game object1, Game object2) {
//					return (int) (LetvUtil.timeFormatSubscribeGame2(object1.playDate, object1.playTime) - LetvUtil.timeFormatSubscribeGame2(object2.playDate,
//							object2.playTime));
//				}
//			});
//		}
            notifyDateChanged();
      }

      /**
       * 根据date日期，判断group节点位置，并将game插入到该group中
       *
       * @param date
       * @param list
       * @return grouPos
       */
      private int addDate(String date, ArrayList<String> list) {
            if (list.size() == 0) {
                  list.add(date);
                  return 0;
            }
            if (list.contains(date)) {
                  return list.indexOf(date);
            }
            for (int i = 0; i < list.size(); i++) {
                  if (LetvUtil.timeFormatSubscribeGame(date, "00:00") > LetvUtil.timeFormatSubscribeGame(list.get(i), "00:00")) {
                        list.add(i, date);
                        return i;
                  }

            }
            list.add(date);
            return list.size() - 1;
      }

      /**
       * 删除一个预约
       *
       * @param id
       */
      public void removeSubscribe(String id) {
            ArrayList<ArrayList<Game>> listChild = adapter.listChild;
            for (int i = 0; i < listChild.size(); i++) {

                  for (int j = 0; j < listChild.get(i).size(); j++) {
                        Game game = listChild.get(i).get(j);
                        if (game.id.equals(id)) {
                              listChild.get(i).remove(j);
                              if (listChild.get(i).size() == 0) {
                                    listChild.remove(i);
                                    adapter.listParent.remove(i);
                              }
                              notifyDateChanged();
                              return;
                        }
                  }
            }
            Toast.makeText(getActivity(), "预约列表中未找到该预约记录", Toast.LENGTH_SHORT).show();
      }

//	private void notifyDate() {
//		adapter.clear();
//		if (gamesList.size() == 0) {
//			return;
//		}
//		ArrayList<String> listParent = new ArrayList<String>();
//		listParent.add("--");
//		ArrayList<ArrayList<Game>> listChild = new ArrayList<ArrayList<Game>>();
//		listChild.add(gamesList);
//		notifyDateChanged(listParent, listChild);
//	}

      /**
       * http 我的预约
       *
       * @author liuhanzhi
       */
      private class RequestMyGames extends LetvHttpAsyncTask<SubscribeGroupList> {

            public RequestMyGames(Context context) {
                  super(context, true);

            }

            @Override
            public boolean onPreExecute() {
//			emptyView.setVisibility(View.GONE);
                  return super.onPreExecute();
            }

            //不加载缓存
//		@Override
//		public SubscribeGroupList loadLocalData() {
//			try {
//				LocalCacheBean bean = LetvCacheDataHandler.readSubscribeListData();
//				SubscribeGroupList result = new LetvGsonParser<SubscribeGroupList>(0,SubscribeGroupList.class).initialParse(bean.getCacheData());
//				return result;
//			} catch (Exception e) {
//			}
//			return null;
//		}
//		
//		@Override
//		public boolean loadLocalDataComplete(SubscribeGroupList t) {
//			if(null != t){
//				onPostExecute(0, t);
//				return true;
//			}
//			return false;
//		}

            @Override
            public LetvDataHull<SubscribeGroupList> doInBackground() {
                  LetvDataHull<SubscribeGroupList> dataHull = LetvHttpApi.requestMatchesRemind(0, new LetvGsonParser<SubscribeGroupList>(0, SubscribeGroupList.class));
                  if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
                        LetvCacheDataHandler.saveSubscribeListData(dataHull.getSourceData());
                  }
                  return dataHull;
//			return LetvHttpApi.requestSubscribeList(new LetvGsonParser<SubscribeGroupList>(0, SubscribeGroupList.class), true);
            }

            @Override
            public void onPostExecute(int updateId, SubscribeGroupList result) {

                  try{
                        if (result.body.focus_list.length > 0) {
                              // 保存 关注球队
//                              LetvSubsribeGameUtil.comparisonSubscribeGames(getActivity(), result.body.focus_list);
                        }
                        if (result.body.subscribe_list.length == 0) {
//				emptyView.showNoSubscribe();
                              notifyDateChanged();
                              return;
                        }
                  } catch (NullPointerException e){
                        mHandler.sendEmptyMessage(0);
                        e.printStackTrace();
                        return;
                  }


                  adapter.clear();
                  ArrayList<Game> allGames = new ArrayList<Game>();
                  for (int i = 0; i < result.body.subscribe_list.length; i++) {
                        adapter.listParent.add(result.body.subscribe_list[i].date);
                        Game[] games = result.body.subscribe_list[i].live_infos;
                        ArrayList<Game> children = new ArrayList<Game>();
                        for (int j = 0; j < games.length; j++) {
                              children.add(games[j]);
                              allGames.add(games[j]);
                        }
                        adapter.listChild.add(children);
                  }
                  notifyDateChanged();
                  // 保存 预约列表
                  LetvSubsribeGameUtil.comparisonSubscribeGames(getActivity(), allGames.toArray(new Game[0]));
                  //刷新首页预约状态
                  if (null != mRightFragmentLsn) {
                        mRightFragmentLsn.updateSuscribeStatus();
                  }
            }

            @Override
            public void netNull() {
                  isLoaded = false;
            }

            @Override
            public void netErr(int updateId, String errMsg) {
                  isLoaded = false;
            }

            @Override
            public void dataNull(int updateId, String errMsg) {
                  isLoaded = false;
            }
      }

}
