package com.github.wuxudong.rncharts.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.wuxudong.rncharts.formatter.TimeIndexAxisValueFormatter;
import com.github.wuxudong.rncharts.listener.LoadCompleteOnChartGestureListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理从RN过来的加载更多的数据
 * Created by jph on 2017/12/4.
 */
public class LoadMoreUtils {

    private static final String TAG = LoadMoreUtils.class.getSimpleName();

    public static void loadMoreComplete(Chart chart, final ReadableMap readableMap) {
        if (!CombinedChart.class.isInstance(chart)) {
            return;
        }
        final CombinedChart combinedChart = (CombinedChart) chart;
        Log.i(TAG, "loadMoreComplete");

        final CombinedData combinedData = combinedChart.getData();

        if (combinedData == null) {
            return;
        }
        if (!BridgeUtils.validate(readableMap, ReadableType.Map, "data")) {
            return;
        }
        final ReadableMap dataMap = readableMap.getMap("data");


        //必须停止惯性滑动，不然刷新数据后会继续之前未完的任务
        combinedChart.stopDeceleration();
        combinedChart.clearAllViewportJobs();
        combinedChart.setTouchEnabled(false);

        combinedChart.postDelayed(new Runnable() {
            @Override
            public void run() {
                processData(combinedChart, combinedData, readableMap, dataMap);
                combinedChart.setTouchEnabled(true);
            }
        }, 300);


    }

    private static void processData(CombinedChart combinedChart, CombinedData combinedData,
                                    ReadableMap readableMap, ReadableMap dataMap) {
        float oldCount = getCombinedDataCount(combinedData);

        float oldScaleMinX = combinedChart.getViewPortHandler().getMinScaleX();
        float oldScaleMaxX = combinedChart.getViewPortHandler().getMaxScaleX();
        float oldScaleX = combinedChart.getScaleX();

        if (BridgeUtils.validate(dataMap, ReadableType.Array, "lineEntries")) {
            ReadableArray lineEntries = dataMap.getArray("lineEntries");
            loadedLineData(combinedChart.getLineData(), lineEntries);
        }

        if (BridgeUtils.validate(dataMap, ReadableType.Array, "barEntries")) {
            ReadableArray barEntries = dataMap.getArray("barEntries");
            loadedBarData(combinedChart.getBarData(), barEntries);
        }

        if (BridgeUtils.validate(dataMap, ReadableType.Array, "candleEntries")) {
            ReadableArray candleEntries = dataMap.getArray("candleEntries");
            loadedCandleData(combinedChart.getCandleData(), candleEntries);
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

                int timeSize = timeArray.size();
                long[] newValues = new long[timeSize + oldValues.length];

                for (int i = 0, c = timeSize; i < c; i++) {
                    newValues[i] = (long) timeArray.getDouble(i);
                }

                for (int i = timeSize; i < newValues.length; i++) {
                    newValues[i] = oldValues[i - timeSize];
                }

                timeIndexAxisValueFormatter.setValues(newValues);
            }
        }

        int newCount = getCombinedDataCount(combinedData);
        //通过前后数据的比例，计算出新的缩放参数
        float ratio = (float) newCount / oldCount;

        float newScaleMinX = ratio * oldScaleMinX;
        float newScaleMaxX = ratio * oldScaleMaxX;
        float newScaleX = ratio * oldScaleX;

        combinedData.notifyDataChanged();
        combinedChart.notifyDataSetChanged();

        //定位到之前的位置和缩放表现
        combinedChart.setScaleMinima(newScaleX, 1);//避免数据修改后改变缩放表现
        combinedChart.moveViewTo(newCount - oldCount - combinedChart.getXAxis().getSpaceMin(), 0, YAxis.AxisDependency.RIGHT);

        combinedChart.setScaleMinima(newScaleMinX, 1);
        combinedChart.setScaleMaxima(newScaleMaxX, 1);

        //允许继续加载
        if (combinedChart.getOnChartGestureListener() instanceof LoadCompleteOnChartGestureListener) {
            ((LoadCompleteOnChartGestureListener) combinedChart.getOnChartGestureListener())
                    .setLoadComplete(true);
        }
    }

    /**
     * 处理折线图数据
     *
     * @param lineData    所有折线图对应的
     * @param lineEntries 包裹了多个折线图数据的ReadableArray
     */
    private static void loadedLineData(@Nullable LineData lineData, ReadableArray lineEntries) {
        if (lineData == null) {
            return;
        }

        //遍历多个折线数据
        for (int i = 0; i < lineEntries.size(); i++) {
            IDataSet dataSet = lineData.getDataSetByIndex(i);
            if (dataSet == null || dataSet.getEntryCount() <= 0) {
                continue;
            }

            LineDataSet lineDataSet = (LineDataSet) dataSet;

            List<Entry> oldDataList = new ArrayList<>(lineDataSet.getValues());

            ReadableArray aEntries = lineEntries.getArray(i);//得到某一个折线图对应的新数据

            if (aEntries == null) {
                continue;
            }
            int newDataCount = aEntries.size();
            List<Entry> newDataList = new ArrayList<>();
            //遍历某个折线对应的新数据
            for (int j = 0; j < newDataCount; j++) {
                float x = j;
                float lineValue = (float) aEntries.getMap(j).getDouble("y");
                newDataList.add(new Entry(x, lineValue));
            }

            lineDataSet.getValues().clear();//清空之前的数据

            lineDataSet.getValues().addAll(newDataList);//插入新数据

            //为老数据设置新的x值
            for (int j = 0, c = oldDataList.size(); j < c; j++) {
                oldDataList.get(j).setX(newDataCount + j);
            }
            lineDataSet.getValues().addAll(oldDataList);//追加之前的老数据

            lineDataSet.notifyDataSetChanged();//通知数据改变，计算最大最小值
        }
        lineData.notifyDataChanged();
    }

    /**
     * 处理柱形图数据
     *
     * @param barData    所有柱形图对应的
     * @param barEntries 包裹了多个柱形图数据的ReadableArray
     */
    private static void loadedBarData(@Nullable BarData barData, ReadableArray barEntries) {
        if (barData == null) {
            return;
        }

        //遍历多个柱形数据
        for (int i = 0; i < barEntries.size(); i++) {
            IDataSet dataSet = barData.getDataSetByIndex(i);
            if (dataSet == null || dataSet.getEntryCount() <= 0) {
                continue;
            }

            BarDataSet barDataSet = (BarDataSet) dataSet;

            List<BarEntry> oldDataList = new ArrayList<>(barDataSet.getValues());

            ReadableArray aEntries = barEntries.getArray(i);//得到某一个柱形图对应的新数据

            if (aEntries == null) {
                continue;
            }
            int newDataCount = aEntries.size();
            List<BarEntry> newDataList = new ArrayList<>();
            //遍历某个柱形图对应的新数据
            for (int j = 0; j < newDataCount; j++) {
                float x = j;
                float barValue = (float) aEntries.getMap(j).getDouble("y");
                newDataList.add(new BarEntry(x, barValue));
            }

            barDataSet.getValues().clear();//清空之前的数据

            barDataSet.getValues().addAll(newDataList);//插入新数据

            //为老数据设置新的x值
            for (int j = 0, c = oldDataList.size(); j < c; j++) {
                oldDataList.get(j).setX(newDataCount + j);
            }
            barDataSet.getValues().addAll(oldDataList);//追加之前的老数据

            barDataSet.notifyDataSetChanged();//通知数据改变，计算最大最小值
        }
        barData.notifyDataChanged();
    }

    /**
     * 处理蜡烛图数据
     *
     * @param candleData    所有蜡烛图对应的
     * @param candleEntries 包裹了多个蜡烛图数据的ReadableArray
     */
    private static void loadedCandleData(@Nullable CandleData candleData, ReadableArray candleEntries) {
        if (candleData == null) {
            return;
        }

        //遍历多个蜡烛数据
        for (int i = 0; i < candleEntries.size(); i++) {
            IDataSet dataSet = candleData.getDataSetByIndex(i);
            if (dataSet == null || dataSet.getEntryCount() <= 0) {
                continue;
            }

            CandleDataSet candleDataSet = (CandleDataSet) dataSet;

            List<CandleEntry> oldDataList = new ArrayList<>(candleDataSet.getValues());

            ReadableArray aEntries = candleEntries.getArray(i);//得到某一个蜡烛图对应的新数据

            if (aEntries == null) {
                continue;
            }
            int newDataCount = aEntries.size();
            List<CandleEntry> newDataList = new ArrayList<>();
            //遍历某个蜡烛图对应的新数据
            for (int j = 0; j < newDataCount; j++) {
                float x = j;

                ReadableMap candleEntry = aEntries.getMap(j);
                float open = (float) candleEntry.getDouble("open");
                float close = (float) candleEntry.getDouble("close");
                float high = (float) candleEntry.getDouble("shadowH");
                float low = (float) candleEntry.getDouble("shadowL");

                newDataList.add(new CandleEntry(
                        x, high, low,
                        open, close, ConversionUtil.toMap(candleEntry)));
            }

            candleDataSet.getValues().clear();//清空之前的数据

            candleDataSet.getValues().addAll(newDataList);//插入新数据

            //为老数据设置新的x值
            for (int j = 0, c = oldDataList.size(); j < c; j++) {
                oldDataList.get(j).setX(newDataCount + j);
            }
            candleDataSet.getValues().addAll(oldDataList);//追加之前的老数据

            candleDataSet.notifyDataSetChanged();//通知数据改变，计算最大最小值
        }
        candleData.notifyDataChanged();
    }

    /**
     * 获得CombinedData中所有类型数据的总数最大值
     *
     * @param combinedData
     * @return
     */
    private static int getCombinedDataCount(CombinedData combinedData) {
        int count = 0;

        //TODO 暂时只判断了这几种数据
        CandleData candleData = combinedData.getCandleData();
        LineData lineData = combinedData.getLineData();
        BarData barData = combinedData.getBarData();

        if (candleData != null) {
            int candleCount = getDataCount(candleData);
            count = Math.max(count, candleCount);
            Log.i(TAG, "candleCount:" + candleCount);
        }
        if (lineData != null) {
            int lineCount = getDataCount(lineData);
            count = Math.max(count, lineCount);
            Log.i(TAG, "lineCount:" + lineCount);
        }
        if (barData != null) {
            int barCount = getDataCount(barData);
            count = Math.max(count, barCount);
            Log.i(TAG, "barCount:" + barCount);
        }

        Log.i(TAG, "count:" + count);
        return count;
    }

    /**
     * 获得某种类型数据的总数最大值
     *
     * @param chartData
     * @return
     */
    private static int getDataCount(ChartData chartData) {
        int count = 0;

        if (chartData != null) {
            for (int i = 0; i < chartData.getDataSetCount(); i++) {
                count = Math.max(chartData.getDataSetByIndex(i).getEntryCount(), count);
            }
        }

        return count;
    }
}
