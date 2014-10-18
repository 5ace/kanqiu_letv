package com.letv.watchball.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.internal.Utils;
import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.async.RequestAlbumByIdTask;
import com.letv.watchball.bean.LiveList;
import com.letv.watchball.bean.Video;
import com.letv.watchball.bean.WorldCupEntity;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.TextUtil;

/**
 * Created by songmengyu on 14-4-9.
 */
public class WorldCupFragment extends Fragment implements WebFragmentBackListener ,View.OnClickListener{

      private ViewGroup rootView;
      private WebView mWebView;
      private boolean isShow;
      private boolean isFinish;
      private ImageView backBtn;
      private ImageView forwardBtn;
      private ImageView reloadBtn;
//      private String worldCupUrl = "http://m.letv.com/2014/?noheader=1&nofooter=1";


      @Override
      public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (rootView != null) {
                  ViewGroup parent = (ViewGroup) rootView.getParent();
                  if (parent != null)
                        parent.removeView(rootView);
            }
            try {
                  rootView = (ViewGroup) inflater.inflate(R.layout.world_cup_fragment,null, false);
            } catch (InflateException e) {
        /* map is already there, just return view as it is */
            }

            return rootView;
      }


      @Override
      public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mWebView = (WebView) rootView.findViewById(R.id.world_cup_web_view);
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(true);
//            if(Build.VERSION.SDK_INT >= 16){
//            	mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,new Paint());
//            }
            mWebView.setWebViewClient(mWebViewClient);
            requestWebViewData();
            mWebView.addJavascriptInterface(new DemoInterface(), "demo");
            backBtn = (ImageView) rootView.findViewById(R.id.webview_back);
            forwardBtn = (ImageView) rootView.findViewById(R.id.webview_forward);
            reloadBtn = (ImageView) rootView.findViewById(R.id.webview_reload);
            backBtn.setOnClickListener(this);
            forwardBtn.setOnClickListener(this);
            reloadBtn.setOnClickListener(this);
      }

      public void requestWebViewData() {
    	  if(!TextUtils.isEmpty(WorldCupEntity._getInstance().getUrl())){
    		  mWebView.loadUrl(WorldCupEntity._getInstance().getUrl());
    	  }else{
    		  mWebView.loadUrl("http://m.letv.com");
    	  }
          
      }

      private WebViewClient mWebViewClient = new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                  Log.d("smyid", url);
                  try {
                        int index = url.lastIndexOf("/");
                        if (index > 0) {
                              String u = url.substring(url.lastIndexOf("/") + 1, url.length());
                              if (u.contains("vplay_")) {
                                    String vplayId = u.substring(u.lastIndexOf("_") + 1 , u.lastIndexOf("."));
                                    String cid ,pid;
                                    if(u.contains("cid")){
                                          cid = u.substring(u.lastIndexOf("cid=") + 4 ,u.lastIndexOf("cid=") + 5);
                                    } else {
                                          cid = "4";
                                    }
                                    if (u.contains("pid")){
                                          pid = u.substring(u.lastIndexOf("pid=") + 4 ,u.length());
                                    } else {
                                          pid = "0";
                                    }
                                    Log.d("smyid", "vplayId=" + vplayId + "    cid=" + cid + "    pid=" + pid);

                                    if (!isFinish && !TextUtils.isEmpty(vplayId) && !TextUtils.isEmpty(cid)) {
                                          view.stopLoading();
                                          BasePlayActivity.launch(getActivity(), Long.parseLong(pid), Long.parseLong(vplayId), BasePlayActivity.LAUNCH_FROM_HOME);
                                          return true;
                                    } else {
                                          view.loadUrl(url + "");
                                          Log.d("smyid", url);
                                    }
                              } else {
                                    view.loadUrl(url + "");
                                    Log.d("smyid", url);
                              }

                        }
                  } catch (Exception e) {
                        e.printStackTrace();
                  }
                  Log.d("smyid", url);
                  return super.shouldOverrideUrlLoading(view, url + "");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                  //页面开始加载
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                  if (url.equals(WorldCupEntity._getInstance().getUrl()))
                        view.clearHistory();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                  //页面加载错误
            }
      };

      @Override
      public boolean keyBackPress() {
            if (isShow){
                  if (mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                  } else {
                        return false;
                  }
            } else {
                  return false;
            }
      }

      public void isWorldCupFragmentShow(boolean b) {
            isShow = b;
      }

      @Override
      public void onClick(View v) {
            switch (v.getId()){
                  case R.id.webview_back:
                        if (mWebView.canGoBack())
                              mWebView.goBack();
                        break;
                  case R.id.webview_forward:
                	  Log.i("oyys", "mWebView.canGoForward()="+mWebView.canGoForward());
                        if (mWebView.canGoForward())
                              mWebView.goForward();
                        break;
                  case R.id.webview_reload:
                        mWebView.reload();
                        break;
                  default:
                        break;
            }
      }

      public void reloadWorldCup() {
            requestWebViewData();
      }

      final private class DemoInterface {

            void clickInAndroid(String s){
                  System.out.println(s);
            }
      }

      private class WebViewPlayDataTask extends LetvHttpAsyncTask<Video>{


            public WebViewPlayDataTask(Context context) {
                  super(context);
            }

            @Override
            public LetvDataHull<Video> doInBackground() {
                  return null;
            }

            @Override
            public void onPostExecute(int updateId, Video result) {

            }
      }


      @Override
      public void onDestroy() {
            super.onDestroy();
            try {
                  getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
                  isFinish = true;
                  if (mWebView != null) {
                        mWebView.stopLoading();
                        /**
                         * 3.0以上系统编译，如果不隐藏webview会出现崩溃
                         */
                        mWebView.setVisibility(View.GONE);
                        mWebView.destroy();
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }

}
