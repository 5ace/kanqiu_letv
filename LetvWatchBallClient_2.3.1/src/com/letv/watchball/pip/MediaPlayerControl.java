package com.letv.watchball.pip;

public interface MediaPlayerControl {
	void start();

	void pause();

	void stopPlayback();

	boolean toggleScreen();

	String getVideoTitle();

	int getDuration();

	int getCurrentPosition();

	void seekTo(int pos);

	boolean isPlaying();

	int getBufferPercentage();

	boolean canPause();

	boolean canSeekBackward();

	boolean canSeekForward();
	
	int getScreenWidth();
	
	int getScreenHeight();
}
