package com.media;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;

public class PlayActivity extends Activity {
    /** Called when the activity is first created. */
//	String url = "http://119.167.223.131/gate_way.m3u8?ID=id9,id10&id9.proxy_url=aHR0cDovL2czLmxldHYuY24vdm9kL3YxL01qSXZNak12TkRrdmJHVjBkaTEzWldJdk1qQXhNekEwTURndk1qYzFOVGczTlRjNEwzQmhaR0o1Ym5Cd0xtMXdOQT09P2I9MTIzNCZsZXR2RXh0aWQ9OSZ0YWc9Z3VnJm5wPTEmZ3VndHlwZT01Jm0zdTg9aW9z&id10.proxy_url=aHR0cDovLzExOS4xODguMTIyLjM1LzQwLzgvODQvbGV0di11dHMvNDY5MTc4My1BVkMtMjQ5OTA1LUFBQy0zMTk5NS0yNzI2ODAwLTk2ODQ0NDcxLTcxYWQ4ZDk3YzBkZGJlNWNkYzMzOWQwMGVlM2U4ODAxLTEzNzE2Mjc0OTkyNzAubTN1OD9jcnlwdD01OTY0ZWUzNGFhN2YyZTEwNiZiPTI4NCZnbj03NTEmbmM9MSZiZj0zMCZwMnA9MSZ2aWRlb190eXBlPW1wNCZjaGVjaz0wJnRtPTEzNzM3OTA2MDAma2V5PTk1NTUyODg1N2EzODU3NGY0ZDZhYzViNTY2ZWJhOTZiJm9wY2s9MSZsZ249bGV0diZwcm94eT0yMDA3NDcwOTA2JmNpcHM9MTAuNTguMTA1LjIyMSZnZW89Q04tMS0wLTImdHNucD0xJm1tc2lkPTI3Mjg1NzImcGxhdGlkPTMmc3BsYXRpZD0zMDImcGxheWlkPTAmdHNzPWlvcyZzaWduPW1iJmRuYW1lPW1vYmlsZSZ0YWc9bW9iaWxlJnZ0eXBlPXBsYXkmdGVybWlkPTImcGF5PTAmb3N0eXBlPWFuZHJvaWQmaHd0eXBlPWlwaG9uZQ==";
	String url = "http://60.217.237.160/39/41/67/letv-uts/4406909-AVC-249457-AAC-31984-2755000-97688537-167fcf07e17253d82d17cac5b6cb391c-1370937277116.m3u8?crypt=6b3ae308aa7f2e68&b=283&gn=750&nc=1&bf=19&p2p=1&video_type=mp4&check=0&tm=1373716800&key=448021d5dbfd515a049e2704bc2f5471&opck=1&lgn=letv&proxy=2007470898&cips=10.58.104.100&geo=CN-1-0-2&tsnp=1&mmsid=2654947&platid=3&splatid=302&playid=0&tss=ios&sign=mb&dname=mobile&tag=mobile&vtype=play&termid=2&pay=0&ostype=android&hwtype=iphone";
    private VideoView mViedeoView;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        NativeInfos.mOffLinePlay = false;
        RelativeLayout mVideoRelativeLayout = (RelativeLayout)findViewById(R.id.video_view);
        mVideoRelativeLayout.removeAllViews();
        mVideoRelativeLayout.setVisibility(View.GONE);
        mViedeoView = new VideoView(this);
        
        mVideoRelativeLayout.addView(mViedeoView);
        mVideoRelativeLayout.setVisibility(View.VISIBLE);
        mViedeoView.setVideoPath(url);
        
        mViedeoView.setMediaController(new MediaController(this));
        mViedeoView.requestFocus();
        mViedeoView.start();
    }
}