package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.widget.FrameLayout;

import com.handmark.pulltorefresh.library.ILoadingLayout;

/**
 * Created by mariotaku on 14-8-8.
 */
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {
    public LoadingLayout(Context context) {
        super(context);
    }

    public abstract void reset();

    public abstract void setWidth(int width);

    public abstract void setHeight(int height);

    public abstract void releaseToRefresh();

    public abstract void refreshing();

    public abstract void pullToRefresh();

    public abstract int getContentSize();

    public abstract void onPull(float scale);

    public abstract void hideAllViews();

    public abstract void showInvisibleViews();
}
