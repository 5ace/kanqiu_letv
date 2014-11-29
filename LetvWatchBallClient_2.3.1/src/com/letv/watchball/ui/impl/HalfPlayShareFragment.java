package com.letv.watchball.ui.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.datastatistics.util.DataConstant;
import com.letv.watchball.R;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.ShareAlbum;
import com.letv.watchball.share.LetvRenrenShare;
import com.letv.watchball.share.LetvShareControl;
import com.letv.watchball.share.LetvSinaShareSSO;
import com.letv.watchball.share.LetvStarShare;
import com.letv.watchball.share.LetvTencentQzoneShare;
import com.letv.watchball.share.LetvTencentWeiboShare;
import com.letv.watchball.share.LetvWeixinShare;
import com.letv.watchball.share.ShareConstant;
import com.letv.watchball.ui.LetvBaseFragment;
import com.letv.watchball.ui.PlayAlbumController;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.utils.DisplayMetricsUtils;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PublicLoadLayout;
import com.weibo.sdk.android.sso.SsoHandler;

public class HalfPlayShareFragment extends LetvBaseFragment implements OnClickListener {

	/**
	 * 绑定状态
	 */
	/**
	 * 直播
	 */
	public static final int LAUNCH_MODE_LIVE = 4;
	/**
	 * 直播 全屏直播
	 */
	public static final int LAUNCH_MODE_LIVE_FULL = 5;
	private int sina_weibo_isBind;
	private int tencent_weibo_isBind;
	private int qzone_isBind;
	private boolean renren_isBind = false;
	private boolean letvstar_isBind = false;

	private PublicLoadLayout root;
	private SsoHandler mSsoHandler;
	private String channel;
	private DisplayMetricsUtils mDisplayMetricsUtils;
	private ShareAlbum album = LetvShareControl.mShareAlbum;
	private PlayAlbumController playAlbumController;
	private PlayLiveController  PlayLiveController ;
	private TextView sina_icon, qzone_icon, renren_icon, weixin_icon, lestar_icon, qq_icon;
	private TextView sina_status, qzone_status, renren_status, weixin_status, lestar_status, qq_status;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = UIs.createPage(getActivity(), R.layout.detailplay_half_share);
		findview();
		initUI();
		return root;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if(((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
			PlayLiveController = (PlayLiveController) ((BasePlayActivity) getActivity()).mPlayController;
		}else{
			playAlbumController = (PlayAlbumController) ((BasePlayActivity) getActivity()).mPlayController;
		}
		
		
		setOnClickListener();
		Log.d("lhz", "HalfPlayShareFragment.onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}

	public void findview() {
		sina_icon = (TextView) root.findViewById(R.id.sina_icon);
		qzone_icon = (TextView) root.findViewById(R.id.qzone_icon);
		renren_icon = (TextView) root.findViewById(R.id.renren_icon);
		weixin_icon = (TextView) root.findViewById(R.id.weixin_icon);
		lestar_icon = (TextView) root.findViewById(R.id.lestar_icon);
		qq_icon = (TextView) root.findViewById(R.id.qq_icon);

		sina_status = (TextView) root.findViewById(R.id.sina_status);
		qzone_status = (TextView) root.findViewById(R.id.qzone_status);
		renren_status = (TextView) root.findViewById(R.id.renren_status);
		weixin_status = (TextView) root.findViewById(R.id.weixin_status);
		lestar_status = (TextView) root.findViewById(R.id.lestar_status);
		qq_status = (TextView) root.findViewById(R.id.qq_status);

	}

	public void setOnClickListener() {
		sina_icon.setOnClickListener(this);
		qzone_icon.setOnClickListener(this);
		renren_icon.setOnClickListener(this);
		weixin_icon.setOnClickListener(this);
//		lestar_icon.setOnClickListener(this);
		qq_icon.setOnClickListener(this);
	}

	public void initUI() {

		mDisplayMetricsUtils = new DisplayMetricsUtils(getActivity());
		scalview(sina_icon);
		scalview(qzone_icon);
		scalview(renren_icon);
		scalview(weixin_icon);
//		scalview(lestar_icon);
		scalview(qq_icon);
		onFragmentResult = new onFragmentResult() {

			@Override
			public void onFragmentResult_back(int requestCode, int resultCode, Intent data) {
				// TODO Auto-generated method stub
				if (mSsoHandler != null) {
					mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
				}
			}
		};
	}

	public void scalview(View view) {
		LayoutParams params = view.getLayoutParams();
		params.width = mDisplayMetricsUtils.getW_px() / 4;
		params.height = mDisplayMetricsUtils.getW_px() / 4;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		onUpdateUI();
	}

	@Override
	public void onClick(View v) {
		channel = "0";
		
		if(((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getActivity()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
			if (PlayLiveController.getVideo() == null) {
				Toast.makeText(getActivity(), R.string.share_no_play, Toast.LENGTH_SHORT).show();
				return;
			}
			if (PlayLiveController.getVideo().needPay()) {// vip不能分享
				Toast.makeText(getActivity(), R.string.share_vip_Cantshare, Toast.LENGTH_SHORT).show();
				return;
			}
			LetvShareControl.getInstance().setAblum_att(PlayLiveController.getVideo(), PlayLiveController.getAlbum());
		}else{
			if (playAlbumController.getVideo() == null) {
				Toast.makeText(getActivity(), R.string.share_no_play, Toast.LENGTH_SHORT).show();
				return;
			}
			if (playAlbumController.getVideo().needPay()) {// vip不能分享
				Toast.makeText(getActivity(), R.string.share_vip_Cantshare, Toast.LENGTH_SHORT).show();
				return;
			}
			LetvShareControl.getInstance().setAblum_att(playAlbumController.getVideo(), playAlbumController.getAlbum());
		}
		
		

		LetvShareControl.mShareAlbum.setType(2);// 区分是否新浪微博分享，1为是 2为其他

		switch (v.getId()) {

		case R.id.sina_icon:// 新浪分享
			LetvShareControl.mShareAlbum.setType(1);
			onShareSina();
			break;
		case R.id.qzone_icon:// qq空间分享
			onShareQzone();
			break;
		case R.id.renren_icon:// 人人分享
			onShareRenren();
			break;
		case R.id.weixin_icon:// 微信分享
			onShareWeixin();
			break;
		case R.id.lestar_icon:// 大卡分享
			onShareLestar();
			break;
		case R.id.qq_icon:// qq微博分享
			onShareQQ();

			break;
		}
	}

	/**
	 * 新浪分享
	 */
	public void onShareSina() {
		if (!(LetvSinaShareSSO.isLogin(getActivity()) == ShareConstant.BindState.BIND)) {
			if (LetvUtil.isNetAvailableForPlay(getActivity())) {
				mSsoHandler = LetvSinaShareSSO.login(getActivity(), album, album.getOrder(), album.getShare_vid());
			} else {
				Toast.makeText(getActivity(), R.string.toast_net_null, Toast.LENGTH_SHORT).show();
			}
		} else {
			mSsoHandler = LetvSinaShareSSO.login(getActivity(), album, album.getOrder(), album.getShare_vid());
		}
		channel = DataConstant.ACTION.SHARE.SHARE_DIALOG_SINA;
	}

	/**
	 * qq空间分享
	 */
	public void onShareQzone() {
		if (!(LetvTencentQzoneShare.isLogin(getActivity()) == ShareConstant.BindState.BIND)) {

            if (LetvUtil.isNetAvailableForPlay(getActivity())) {
                LetvTencentQzoneShare.get_instace().login(getActivity(), album, album.getOrder(), album.getShare_vid());
            } else {
                Toast.makeText(getActivity(), R.string.toast_net_null, Toast.LENGTH_SHORT).show();
            }

		} else {
			LetvTencentQzoneShare.get_instace().login(getActivity(), album, album.getOrder(), album.getShare_vid());
		}
		channel = DataConstant.ACTION.SHARE.SHARE_DIALOG_QZONE;
	}

	/**
	 * 人人分享
	 */
	public void onShareRenren() {
		if (!LetvRenrenShare.isLogin(getActivity())) {

			if (LetvUtil.isNetAvailableForPlay(getActivity())) {
				LetvRenrenShare.login(getActivity(), album, album.getOrder(), album.getShare_vid());
			} else {
				Toast.makeText(getActivity(), R.string.toast_net_null, Toast.LENGTH_SHORT).show();
			}

		} else {
			LetvRenrenShare.login(getActivity(), album, album.getOrder(), album.getShare_vid());
		}
		channel = DataConstant.ACTION.SHARE.SHARE_DIALOG_RENREN;
	}

	/**
	 * 微信分享
	 */
	public void onShareWeixin() {
		if (LetvUtil.checkBrowser(getActivity(), "com.tencent.mm")) {
			LetvWeixinShare.share(getActivity(),
					LetvUtil.getShareHint(album.getShare_AlbumName(), album.getType(), album.getShare_id(), album.getOrder(), album.getShare_vid()),
					album.getIcon(), LetvUtil.getSharePlayUrl(album.getType(), album.getShare_id(), album.getOrder(), album.getShare_vid()));
		} else {
			UIs.callDialogMsgPositiveButton(getActivity(), R.string.SEVEN_ZERO_SEVEN_CONSTANT_TITLE,R.string.SEVEN_ZERO_SEVEN_CONSTANT_MSG, null);
		}
		channel = DataConstant.ACTION.SHARE.SHARE_DIALOG_WEIXIN;
	}

	/**
	 * 大卡分享
	 */
	public void onShareLestar() {
		if (album.getCid() == AlbumNew.Channel.TYPE_MOVIE || album.getCid() == AlbumNew.Channel.TYPE_TV || album.getCid() == AlbumNew.Channel.TYPE_CARTOON
				|| album.getCid() == AlbumNew.Channel.TYPE_JOY || album.getCid() == AlbumNew.Channel.TYPE_TVSHOW || album.getCid() == AlbumNew.Channel.TYPE_PE
				|| album.getCid() == AlbumNew.Channel.TYPE_LETV_MAKE || album.getCid() == AlbumNew.Channel.TYPE_DOCUMENT_FILM
				|| album.getCid() == AlbumNew.Channel.TYPE_OPEN_CLASS || album.getCid() == AlbumNew.Channel.TYPE_FASHION) {
			if (!LetvStarShare.isLogin(getActivity())) {

				if (LetvUtil.isNetAvailableForPlay(getActivity())) {
					LetvStarShare.login(getActivity(), album, album.getOrder(), album.getShare_vid());
				} else {
					Toast.makeText(getActivity(), R.string.toast_net_null, Toast.LENGTH_SHORT).show();
				}

			} else {
				LetvStarShare.login(getActivity(), album, album.getOrder(), album.getShare_vid());
			}
		} else {
			UIs.call(getActivity(), R.string.letv_star_share_msg, null);
		}
		channel = DataConstant.ACTION.SHARE.SHARE_DIALOG_DAKA;
	}

	/**
	 * qq聊天分享
	 */
	public void onShareQQ() {
		if (!(LetvTencentWeiboShare.isLogin(getActivity()) == ShareConstant.BindState.BIND)) {

			if (LetvUtil.isNetAvailableForPlay(getActivity())) {
				LetvTencentWeiboShare.login(getActivity(), LetvShareControl.mShareAlbum, 2, LetvShareControl.mShareAlbum.getShare_id(),
						LetvShareControl.mShareAlbum.getShare_vid());
			} else {
				Toast.makeText(getActivity(), R.string.toast_net_null, Toast.LENGTH_SHORT).show();
			}

		} else {
			LetvTencentWeiboShare.login(getActivity(), LetvShareControl.mShareAlbum, 2, LetvShareControl.mShareAlbum.getShare_id(),
					LetvShareControl.mShareAlbum.getShare_vid());
		}
		channel = DataConstant.ACTION.SHARE.SHARE_DIALOG_QQ_WEIBO;
	}

	/**
	 * onResume里調用
	 */
	public void onUpdateUI() {
		isBind();
		updateUI();
	}

	/**
	 * 是否綁定
	 */

	public void isBind() {
		sina_weibo_isBind = LetvSinaShareSSO.isLogin(getActivity());
		tencent_weibo_isBind = LetvTencentWeiboShare.isLogin(getActivity());
		qzone_isBind = LetvTencentQzoneShare.isLogin(getActivity());
		renren_isBind = LetvRenrenShare.isLogin(getActivity());
		letvstar_isBind = LetvStarShare.isLogin(getActivity());
	}

	/**
	 * 刷新ui方法
	 */

	private void updateUI() {
		if (sina_weibo_isBind == ShareConstant.BindState.UNBIND) {
			// sina_status.setText(R.string.setting_share_unbound);
			sina_status.setVisibility(View.VISIBLE);
		} else if (sina_weibo_isBind == ShareConstant.BindState.BIND) {
			// sina_status.setText(R.string.setting_share_bound);
			sina_status.setVisibility(View.GONE);
		} else if (sina_weibo_isBind == ShareConstant.BindState.BINDPASS) {
			// sina_status.setText(R.string.setting_bind_pass);
			sina_status.setVisibility(View.VISIBLE);
		}

		if (tencent_weibo_isBind == ShareConstant.BindState.UNBIND) {
			// qq_status.setText(R.string.setting_share_unbound);
			qq_status.setVisibility(View.VISIBLE);
		} else if (tencent_weibo_isBind == ShareConstant.BindState.BIND) {
			// qq_status.setText(R.string.setting_share_bound);
			qq_status.setVisibility(View.GONE);
		} else if (tencent_weibo_isBind == ShareConstant.BindState.BINDPASS) {
			// qq_status.setText(R.string.setting_bind_pass);
			qq_status.setVisibility(View.VISIBLE);
		}

		if (qzone_isBind == ShareConstant.BindState.UNBIND) {
			// qzone_status.setText(R.string.setting_share_unbound);
			qzone_status.setVisibility(View.VISIBLE);
		} else if (qzone_isBind == ShareConstant.BindState.BIND) {
			// qzone_status.setText(R.string.setting_share_bound);
			qzone_status.setVisibility(View.GONE);
		} else if (qzone_isBind == ShareConstant.BindState.BINDPASS) {
			// qzone_status.setText(R.string.setting_bind_pass);
			qzone_status.setVisibility(View.VISIBLE);
		}

		if (renren_isBind) {
			// renren_status.setText(R.string.setting_share_bound);
			renren_status.setVisibility(View.GONE);
		} else {
			// renren_status.setText(R.string.setting_share_unbound);
			renren_status.setVisibility(View.VISIBLE);
		}

		if (letvstar_isBind) {
			// lestar_status.setText(R.string.setting_share_bound);
			lestar_status.setVisibility(View.GONE);
		} else {
			// lestar_status.setText(R.string.setting_share_unbound);
			lestar_status.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static onFragmentResult onFragmentResult;

	public interface onFragmentResult {
		public void onFragmentResult_back(int requestCode, int resultCode, Intent data);
	}
}
