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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.wuxudong.rncharts.data.CombinedDataExtract;
import com.github.wuxudong.rncharts.data.DataExtract;
import com.github.wuxudong.rncharts.formatter.TimeIndexAxisValueFormatter;
import com.github.wuxudong.rncharts.listener.RNOnChartGestureListener;
import com.github.wuxudong.rncharts.listener.RNOnChartValueSelectedListener;
import com.github.wuxudong.rncharts.utils.BridgeUtils;
import com.github.wuxudong.rncharts.utils.ConversionUtil;
import com.github.wuxudong.rncharts.utils.FloatLabelUtil;
import com.github.wuxudong.rncharts.utils.LoadMoreUtils;

import java.util.Arrays;

public class CombinedChartManager extends BarLineChartBaseManager<CombinedChart, Entry> {

    private static final String TAG = CombinedChartManager.class.getSimpleName();

    @Override
    public String getName() {
        return "RNCombinedChart";
    }

    @Override
    protected CombinedChart createViewInstance(ThemedReactContext reactContext) {
        CombinedChart combinedChart = new XCombinedChart(reactContext);
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
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
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
                ReadableMap barEntry = barEntries.getMap(i);
                float barValue = (float) barEntry.getDouble("y");
                IDataSet dataSet = barData.getDataSetByIndex(i);
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
                    continue;
                }
                dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).setY(barValue);
                dataSet.getColors().set(dataSet.getEntryCount() - 1, barEntry.getInt("color"));
            }
            barData.notifyDataChanged();
        }

        CandleData candleData = combinedData.getCandleData();
        if (BridgeUtils.validate(readableMap, ReadableType.Array, "candleEntries") &&
                candleData != null) {
            ReadableArray candleEntries = readableMap.getArray("candleEntries");
            for (int i = 0; i < candleEntries.size(); i++) {
                ReadableMap candleEntryMap = candleEntries.getMap(i);
                float open = (float) candleEntryMap.getDouble("open");
                float close = (float) candleEntryMap.getDouble("close");
                float high = (float) candleEntryMap.getDouble("shadowH");
                float low = (float) candleEntryMap.getDouble("shadowL");
                ICandleDataSet dataSet = candleData.getDataSetByIndex(i);
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
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

    @Override
    protected void addNewEntry(Chart chart, ReadableMap readableMap) {
        if (!CombinedChart.class.isInstance(chart)) {
            return;
        }
        CombinedChart combinedChart = (CombinedChart) chart;
        Log.i(TAG, "addNewEntry");

        CombinedData combinedData = combinedChart.getData();

        if (combinedData == null) {
            return;
        }

        ReadableArray readableArray = null;
        if (BridgeUtils.validate(readableMap, ReadableType.Array, "data")) {
            readableArray = readableMap.getArray("data");
        }

        if (readableArray == null || readableArray.size() != 2) {
            //强制要求RN传来的数据是两条,第一条更新当前的最后一条，第二条添加到结尾处
            return;
        }

        ReadableMap readableMap1 = readableArray.getMap(0);
        ReadableMap readableMap2 = readableArray.getMap(1);

        LineData lineData = combinedData.getLineData();
        if (BridgeUtils.validate(readableMap1, ReadableType.Array, "lineEntries") &&
                BridgeUtils.validate(readableMap2, ReadableType.Array, "lineEntries") &&
                lineData != null) {
            ReadableArray lineEntries1 = readableMap1.getArray("lineEntries");
            for (int i = 0; i < lineEntries1.size(); i++) {
                ReadableMap lineEntry = lineEntries1.getMap(i);
                float lineValue = (float) lineEntry.getDouble("y");
                IDataSet dataSet = lineData.getDataSetByIndex(i);
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
                    continue;
                }
                dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).setY(lineValue);
            }
            ReadableArray lineEntries2 = readableMap2.getArray("lineEntries");
            for (int i = 0; i < lineEntries2.size(); i++) {
                ReadableMap lineEntry = lineEntries2.getMap(i);
                float lineValue = (float) lineEntry.getDouble("y");
                IDataSet dataSet = lineData.getDataSetByIndex(i);
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
                    continue;
                }
                dataSet.addEntry(new Entry(
                        dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).getX() + 1,
                        lineValue));
            }
            lineData.notifyDataChanged();
        }

        BarData barData = combinedData.getBarData();
        if (BridgeUtils.validate(readableMap1, ReadableType.Array, "barEntries") &&
                BridgeUtils.validate(readableMap2, ReadableType.Array, "barEntries") &&
                barData != null) {
            ReadableArray barEntries1 = readableMap1.getArray("barEntries");
            for (int i = 0; i < barEntries1.size(); i++) {
                ReadableMap lineEntry = barEntries1.getMap(i);
                float barValue = (float) lineEntry.getDouble("y");
                IDataSet dataSet = barData.getDataSetByIndex(i);
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
                    continue;
                }
                dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).setY(barValue);
                dataSet.getColors().set(dataSet.getEntryCount() - 1, lineEntry.getInt("color"));
            }
            ReadableArray barEntries2 = readableMap2.getArray("barEntries");
            for (int i = 0; i < barEntries2.size(); i++) {
                ReadableMap lineEntry = barEntries2.getMap(i);
                float barValue = (float) lineEntry.getDouble("y");
                IDataSet dataSet = barData.getDataSetByIndex(i);
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
                    continue;
                }
                dataSet.addEntry(new BarEntry(
                        dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).getX() + 1,
                        barValue));
                dataSet.getColors().add(lineEntry.getInt("color"));
            }
            barData.notifyDataChanged();
        }

        CandleData candleData = combinedData.getCandleData();
        if (BridgeUtils.validate(readableMap1, ReadableType.Array, "candleEntries") &&
                BridgeUtils.validate(readableMap2, ReadableType.Array, "candleEntries") &&
                candleData != null) {
            ReadableArray candleEntries1 = readableMap1.getArray("candleEntries");
            for (int i = 0; i < candleEntries1.size(); i++) {
                ReadableMap lineEntry = candleEntries1.getMap(i);
                float open = (float) lineEntry.getDouble("open");
                float close = (float) lineEntry.getDouble("close");
                float high = (float) lineEntry.getDouble("shadowH");
                float low = (float) lineEntry.getDouble("shadowL");
                ICandleDataSet dataSet = candleData.getDataSetByIndex(i);
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
                    continue;
                }
                CandleEntry candleEntry = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1);
                candleEntry.setOpen(open);
                candleEntry.setClose(close);
                candleEntry.setHigh(high);
                candleEntry.setLow(low);
            }
            ReadableArray candleEntries2 = readableMap2.getArray("candleEntries");
            for (int i = 0; i < candleEntries2.size(); i++) {
                ReadableMap lineEntry = candleEntries2.getMap(i);
                float open = (float) lineEntry.getDouble("open");
                float close = (float) lineEntry.getDouble("close");
                float high = (float) lineEntry.getDouble("shadowH");
                float low = (float) lineEntry.getDouble("shadowL");
                ICandleDataSet dataSet = candleData.getDataSetByIndex(i);
                if (dataSet == null || dataSet.getEntryCount() <= 0) {
                    continue;
                }
                CandleEntry candleEntry = new CandleEntry(
                        dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).getX() + 1, high, low,
                        open, close, ConversionUtil.toMap(lineEntry));
                dataSet.addEntry(candleEntry);
            }
            candleData.notifyDataChanged();
        }

        XAxis xAxis = combinedChart.getXAxis();
        if (xAxis != null) {
            IAxisValueFormatter valueFormatter = xAxis.getValueFormatter();
            if (valueFormatter instanceof TimeIndexAxisValueFormatter &&
                    BridgeUtils.validate(readableMap, ReadableType.Array, "timestamp")) {
                ReadableArray timeArray = readableMap.getArray("timestamp");

                //往坐标上插入数据
                TimeIndexAxisValueFormatter timeIndexAxisValueFormatter = (TimeIndexAxisValueFormatter) valueFormatter;
                long[] oldValues = timeIndexAxisValueFormatter.getValues();
                long[] newValues = Arrays.copyOf(oldValues, oldValues.length + 1);
                newValues[newValues.length - 1] = (long) timeArray.getDouble(timeArray.size() - 1);

                timeIndexAxisValueFormatter.setValues(newValues);
            }
        }

        combinedData.notifyDataChanged();
        combinedChart.notifyDataSetChanged();
        combinedChart.postInvalidate();
    }

    @Override
    protected void loadMoreComplete(Chart chart, ReadableMap readableMap) {
        LoadMoreUtils.loadMoreComplete(chart, readableMap);
    }
}
