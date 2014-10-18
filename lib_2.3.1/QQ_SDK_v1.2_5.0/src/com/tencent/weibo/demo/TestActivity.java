package com.tencent.weibo.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tencent.weibo.R;
import com.tencent.weibo.TWeibo;
import com.tencent.weibo.TWeibo.TWeiboListener;

/**
 * 授权方式选择界面
 */
public class TestActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
    }
    
    public void login(View v){
    	TWeibo.getInstance().login(this, new TWeiboListener() {
			
			@Override
			public void onFail(String message) {
				// TODO Auto-generated method stub
				Toast.makeText(TestActivity.this, message, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				Toast.makeText(TestActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
			}
		});
    }
    
	public void share(View v){
    	TWeibo.getInstance().share(this, new TWeiboListener() {
			
			@Override
			public void onFail(String message) {
				Toast.makeText(TestActivity.this, message, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				Toast.makeText(TestActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
			}
		}, "test", "http://www.baidu.com/img/baidu_jgylogo3.gif",false);
    }
	
	public void share2(View v){
    	TWeibo.getInstance().share(this, new TWeiboListener() {
			
			@Override
			public void onFail(String message) {
				Toast.makeText(TestActivity.this, message, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				Toast.makeText(TestActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
			}
		}, "test同步到空间", "http://www.baidu.com/img/baidu_jgylogo3.gif",true);
    }
	
	public void logout(View v){
		TWeibo.getInstance().logout(this);
	}
}