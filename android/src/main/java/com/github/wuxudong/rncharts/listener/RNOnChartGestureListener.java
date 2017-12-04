package com.github.wuxudong.rncharts.listener;

import android.util.Log;
import android.view.MotionEvent;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.lang.ref.WeakReference;

/**
 * 实现了手势监听，调用JS（暂时未全部实现）
 * Created by jph on 2017/9/27.
 */
public class RNOnChartGestureListener implements OnChartGestureListener {

    private static final String TAG = RNOnChartGestureListener.class.getSimpleName();

    private WeakReference<Chart> mWeakChart;

    private boolean mIsCanLoad;

    public RNOnChartGestureListener(Chart chart) {
        mWeakChart = new WeakReference<>(chart);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //判断惯性到最后的滑动更多
        if (mWeakChart == null) {
            return;
        }
        Chart chart = mWeakChart.get();
        if (!BarLineChartBase.class.isInstance(chart)) {
            return;
        }

        CombinedChart loadChart = (CombinedChart) chart;

        float leftX = loadChart.getLowestVisibleX();    //获取可视区域中，显示在x轴最右边的index

        if (lastPerformedGesture == ChartTouchListener.ChartGesture.DRAG) {
            mIsCanLoad = true;
            if (leftX <= loadChart.getXAxis().getAxisMinimum()) {
                mIsCanLoad = false;
                //加载更多数据的操作
                callOnLoadMore();
            }
        }
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i(TAG, "onChartSingleTapped");
        if (mWeakChart == null) {
            return;
        }
        Chart chart = mWeakChart.get();

        ReactContext reactContext = (ReactContext) chart.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                chart.getId(),
                "topSingleTapped",
                null);
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        //判断直接滑动到最后的加载更多
        if (mWeakChart == null) {
            return;
        }
        Chart chart = mWeakChart.get();
        if (!CombinedChart.class.isInstance(chart)) {
            return;
        }

        CombinedChart loadChart = (CombinedChart) chart;
        if (mIsCanLoad) {
            float leftX = loadChart.getLowestVisibleX();     //获取可视区域中，显示在x轴最右边的index
            if (leftX <= loadChart.getXAxis().getAxisMinimum()) {
                mIsCanLoad = false;
                //加载更多数据的操作
                callOnLoadMore();
            }
        }
    }

    private void callOnLoadMore() {
        Log.i(TAG, "callOnLoadMore");
        if (mWeakChart == null) {
            return;
        }
        Chart chart = mWeakChart.get();

        ReactContext reactContext = (ReactContext) chart.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                chart.getId(),
                "topLoadMore",
                null);
    }
}
