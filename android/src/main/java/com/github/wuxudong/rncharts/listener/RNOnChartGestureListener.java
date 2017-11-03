package com.github.wuxudong.rncharts.listener;

import android.util.Log;
import android.view.MotionEvent;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.github.mikephil.charting.charts.Chart;
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

    public RNOnChartGestureListener(Chart chart) {
        mWeakChart = new WeakReference<>(chart);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

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
    }

}
