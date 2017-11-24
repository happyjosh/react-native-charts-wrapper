package com.github.wuxudong.rncharts.charts;


import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.FloatLabel;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.wuxudong.rncharts.data.CombinedDataExtract;
import com.github.wuxudong.rncharts.data.DataExtract;
import com.github.wuxudong.rncharts.listener.RNOnChartGestureListener;
import com.github.wuxudong.rncharts.listener.RNOnChartValueSelectedListener;
import com.github.wuxudong.rncharts.utils.BridgeUtils;
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

    @Override
    protected void updateLastEntry(Chart chart, ReadableMap readableMap) {
        if (!CombinedChart.class.isInstance(chart)) {
            return;
        }
        CombinedChart combinedChart = (CombinedChart) chart;
        Log.i(TAG, "updateLastEntry");

        CombinedData combinedData = combinedChart.getData();

        if (combinedData == null) {
            return;
        }

        LineData lineData = combinedData.getLineData();
        if (BridgeUtils.validate(readableMap, ReadableType.Array, "lineEntries") &&
                lineData != null) {
            ReadableArray lineEntries = readableMap.getArray("lineEntries");
            for (int i = 0; i < lineEntries.size(); i++) {
                ReadableMap lineEntry = lineEntries.getMap(i);
                float lineValue = (float) lineEntry.getDouble("y");
                IDataSet dataSet = lineData.getDataSetByIndex(i);
                if (dataSet == null) {
                    continue;
                }
                dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).setY(lineValue);
            }
            lineData.notifyDataChanged();
        }

        BarData barData = combinedData.getBarData();
        if (BridgeUtils.validate(readableMap, ReadableType.Array, "barEntries") &&
                barData != null) {
            ReadableArray barEntries = readableMap.getArray("barEntries");
            for (int i = 0; i < barEntries.size(); i++) {
                ReadableMap lineEntry = barEntries.getMap(i);
                float barValue = (float) lineEntry.getDouble("y");
                IDataSet dataSet = barData.getDataSetByIndex(i);
                if (dataSet == null) {
                    continue;
                }
                dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).setY(barValue);
            }
            barData.notifyDataChanged();
        }

        CandleData candleData = combinedData.getCandleData();
        if (BridgeUtils.validate(readableMap, ReadableType.Array, "candleEntries") &&
                candleData != null) {
            ReadableArray candleEntries = readableMap.getArray("candleEntries");
            for (int i = 0; i < candleEntries.size(); i++) {
                ReadableMap lineEntry = candleEntries.getMap(i);
                float open = (float) lineEntry.getDouble("open");
                float close = (float) lineEntry.getDouble("close");
                float high = (float) lineEntry.getDouble("shadowH");
                float low = (float) lineEntry.getDouble("shadowL");
                ICandleDataSet dataSet = candleData.getDataSetByIndex(i);
                if (dataSet == null) {
                    continue;
                }
                CandleEntry candleEntry = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1);
                candleEntry.setOpen(open);
                candleEntry.setClose(close);
                candleEntry.setHigh(high);
                candleEntry.setLow(low);
            }
            candleData.notifyDataChanged();
        }

        combinedData.notifyDataChanged();
        combinedChart.notifyDataSetChanged();
        combinedChart.postInvalidate();
    }
}
