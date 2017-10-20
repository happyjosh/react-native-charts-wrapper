package com.github.wuxudong.rncharts.charts;


import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.FloatLabel;
import com.github.mikephil.charting.data.Entry;
import com.github.wuxudong.rncharts.data.CombinedDataExtract;
import com.github.wuxudong.rncharts.data.DataExtract;
import com.github.wuxudong.rncharts.listener.RNOnChartGestureListener;
import com.github.wuxudong.rncharts.listener.RNOnChartValueSelectedListener;
import com.github.wuxudong.rncharts.utils.FloatLabelUtil;

public class CombinedChartManager extends BarLineChartBaseManager<CombinedChart, Entry> {

    private static final String TAG = CombinedChartManager.class.getSimpleName();

    @Override
    public String getName() {
        return "RNCombinedChart";
    }

    @Override
    protected CombinedChart createViewInstance(ThemedReactContext reactContext) {
        CombinedChart combinedChart = new CombinedChart(reactContext);
        combinedChart.setOnChartValueSelectedListener(new RNOnChartValueSelectedListener(combinedChart));
        combinedChart.setOnChartGestureListener(new RNOnChartGestureListener(combinedChart));
        return combinedChart;
    }

    @Override
    DataExtract getDataExtract() {
        return new CombinedDataExtract();
    }

    @ReactProp(name = "rightSelectLabel")
    public void setRightSelectLabel(CombinedChart chart, ReadableMap propMap) {
        Log.i(TAG, "setRightSelectLabel");
        if (!FloatLabelUtil.isEnabled(propMap)) {
            chart.setRightSelectFloatLabel(null);
            return;
        }

        FloatLabel floatLabel = FloatLabelUtil.bridgeFloatLabel(chart, propMap);
        chart.setRightSelectFloatLabel(floatLabel);
    }

    @ReactProp(name = "bottomSelectLabel")
    public void setBottomSelectLabel(CombinedChart chart, ReadableMap propMap) {
        Log.i(TAG, "setBottomSelectLabel");
        if (!FloatLabelUtil.isEnabled(propMap)) {
            chart.setBottomSelectFloatLabel(null);
            return;
        }

        FloatLabel floatLabel = FloatLabelUtil.bridgeFloatLabel(chart, propMap);
        chart.setBottomSelectFloatLabel(floatLabel);
    }
}
