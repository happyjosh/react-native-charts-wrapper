package com.github.wuxudong.rncharts.listener;

import com.github.mikephil.charting.listener.OnChartGestureListener;

/**
 * 保存类加载更多是否完成状态的
 * Created by jph on 2017/12/5.
 */
public abstract class LoadCompleteOnChartGestureListener implements OnChartGestureListener {
    private boolean mLoadComplete = true;

    public boolean isLoadComplete() {
        return mLoadComplete;
    }

    public void setLoadComplete(boolean loadComplete) {
        mLoadComplete = loadComplete;
    }
}
