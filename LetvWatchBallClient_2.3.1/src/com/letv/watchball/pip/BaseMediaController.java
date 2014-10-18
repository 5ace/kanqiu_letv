package com.letv.watchball.pip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.letv.watchball.bean.AlbumNew;

public interface BaseMediaController {

	abstract void hide();
	
	abstract void show();
	
	abstract void show(int timeout);
	
	abstract boolean isShowing();
	
	abstract void setMediaPlayer(MediaPlayerControl player);
	
	abstract void setEnabled(boolean enabled);
	
	abstract void setAnchorView(View view);

	abstract void onActivityResult(int requestCode, int resultCode, Intent data);

	abstract void unregisterReceiver();

	abstract void updateSkipState();

	abstract void setPlayController(PipPlayController playController);

	abstract void adjustVolumeSeekBar();

	abstract void adjustSoundDrawable();

	abstract void setMbundle(Bundle mBundle);

	abstract void setAlbum(AlbumNew result);

	abstract void updateLiveChannelProgram();

//	abstract void setLiveChannelListener(LiveChannelListener mLiveChannelListener);

	abstract void setLive(boolean b);
	
	abstract void setOnlyLive(boolean b);
	
	abstract void setHd(boolean b);
	
	abstract void setHasLow(boolean b);
	
	abstract void setHasHigh(boolean b);
	abstract void updateEpisodeLayout(AlbumNew mAlbum);
}
