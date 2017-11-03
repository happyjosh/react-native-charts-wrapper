package com.github.wuxudong.rncharts;

import android.app.Activity;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.PixelUtil;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.List;

/**
 * Created by jph on 2017/11/3.
 */
public class BindChartModule extends ReactContextBaseJavaModule {
    private static final String TAG = BindChartModule.class.getSimpleName();

    public BindChartModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "ChartBinder";
    }

    @ReactMethod
    public void bindChart(int chart1Tag, int chart2Tag, float manualYOffset) {
        Activity activity = getCurrentActivity();

        Chart chart1 = (Chart) activity.findViewById(chart1Tag);
        Chart chart2 = (Chart) activity.findViewById(chart2Tag);

        if (chart1 == null || chart2 == null) {
            return;
        }

        bindMatrixChange(chart1, chart2);
        bindSelectChange(chart1, chart2, PixelUtil.toPixelFromDIP(manualYOffset));
    }

    /**
     * 绑定缩放和移动事件
     *
     * @param chart1
     * @param chart2
     */
    private void bindMatrixChange(Chart chart1, Chart chart2) {
        chart1.setOnChartGestureListener(new LinkOnChartGestureListener(chart1, chart2));
        chart2.setOnChartGestureListener(new LinkOnChartGestureListener(chart2, chart1));
    }

    /**
     * 绑定选中事件
     *
     * @param chart1
     * @param chart2
     */
    private void bindSelectChange(final Chart chart1, final Chart chart2, final float manualYOffset) {
        final OnChartValueSelectedListener oldChart1SelectedListener =
                chart1.getOnChartValueSelectedListener();
        final OnChartValueSelectedListener oldChart2SelectedListener =
                chart2.getOnChartValueSelectedListener();

        chart1.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (oldChart1SelectedListener != null) {
                    oldChart1SelectedListener.onValueSelected(e, h);
                }
                highlightByOthers(chart2, h.getX(), h.getY(), h.getTouchY(), manualYOffset);
            }

            @Override
            public void onNothingSelected() {
                if (oldChart1SelectedListener != null) {
                    oldChart1SelectedListener.onNothingSelected();
                }
                chart2.highlightValue(null);
            }
        });
        chart2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (oldChart2SelectedListener != null) {
                    oldChart2SelectedListener.onValueSelected(e, h);
                }
                highlightByOthers(chart1, h.getX(), h.getY(), h.getTouchY(), -manualYOffset);
            }

            @Override
            public void onNothingSelected() {
                if (oldChart2SelectedListener != null) {
                    oldChart2SelectedListener.onNothingSelected();
                }
                chart1.highlightValue(null);
            }
        });
    }

    /**
     * 根据其他关联图表的选中高亮信息来手动高亮选中
     *
     * @param chart         需要操作的图表
     * @param x
     * @param y
     * @param touchY
     * @param manualYOffset
     */
    private void highlightByOthers(Chart chart, float x, float y, float touchY, float manualYOffset) {
        int[] indexes = getEnableHighlightIndexes(chart);
        int dataIndex = indexes[0];
        int dataSetIndex = indexes[1];

        Log.i(TAG, "highlightByOthers " + x + "|" + y + "|" + touchY + "|" + dataIndex +
                "|" + dataSetIndex + "|" + manualYOffset);

        if (dataSetIndex < 0) {
            return;
        }
        if (CombinedChart.class.isInstance(chart) && dataIndex < 0) {
            return;
        }

        float newTouchY = touchY - manualYOffset;
        Highlight highlight = new Highlight(x, y, dataSetIndex);
        highlight.setDataIndex(dataIndex);
        Highlight h1 = chart.getHighlightByTouchPoint(x, newTouchY);
        highlight.setTouchY(newTouchY);
        if (null == h1) {
            highlight.setTouchYValue(0);
        } else {
            highlight.setTouchYValue(h1.getTouchYValue());
        }
        chart.highlightValues(new Highlight[]{highlight});
    }

    private int getEnableHighlightDataSetIndex(ChartData chartData) {
        //TODO 暂时只处理了一个图表一个高亮的情况
        for (int i = 0, c = chartData.getDataSetCount(); i < c; i++) {
            IDataSet dataSet = chartData.getDataSetByIndex(i);
            if (dataSet.isHighlightEnabled()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 得到高亮的dataIndex和dataSetIndex
     *
     * @param chart
     * @return [dataIndex, dataSetIndex]
     */
    private int[] getEnableHighlightIndexes(Chart chart) {
        if (!CombinedChart.class.isInstance(chart)) {
            //不是CombinedChart的情况
            return new int[]{-1, getEnableHighlightDataSetIndex(chart.getData())};
        }

        //CombinedChart的情况
        CombinedChart combinedChart = (CombinedChart) chart;
        //只处理了一个图标一个高亮的情况
        CombinedData combinedData = combinedChart.getData();
        List<? extends ChartData> dataList = combinedData.getAllData();
        for (int i = 0, c = dataList.size(); i < c; i++) {
            ChartData chartData = dataList.get(i);
            int enableHighlightDataSetIndex = getEnableHighlightDataSetIndex(chartData);
            Log.i(TAG, chartData.getClass().getSimpleName() + "---" + i + "|" +
                    combinedData.getDataIndex(chartData) + "|" + enableHighlightDataSetIndex);
            if (enableHighlightDataSetIndex >= 0) {
                return new int[]{combinedData.getDataIndex(chartData), enableHighlightDataSetIndex};
            }
        }

        return new int[]{-1, -1};
    }

    static class LinkOnChartGestureListener implements OnChartGestureListener {

        private Chart chart1, chart2;
        private OnChartGestureListener oldOnChartGestureListener;

        public LinkOnChartGestureListener(Chart chart1, Chart chart2) {
            this.chart1 = chart1;
            this.chart2 = chart2;

            this.oldOnChartGestureListener = chart1.getOnChartGestureListener();
        }

        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            if (this.oldOnChartGestureListener != null) {
                this.oldOnChartGestureListener.onChartGestureStart(me, lastPerformedGesture);
            }
        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            if (this.oldOnChartGestureListener != null) {
                this.oldOnChartGestureListener.onChartGestureEnd(me, lastPerformedGesture);
            }
        }

        @Override
        public void onChartLongPressed(MotionEvent me) {
            if (this.oldOnChartGestureListener != null) {
                this.oldOnChartGestureListener.onChartLongPressed(me);
            }
        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {
            if (this.oldOnChartGestureListener != null) {
                this.oldOnChartGestureListener.onChartDoubleTapped(me);
            }
        }

        @Override
        public void onChartSingleTapped(MotionEvent me) {
            if (this.oldOnChartGestureListener != null) {
                this.oldOnChartGestureListener.onChartSingleTapped(me);
            }
        }

        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            if (this.oldOnChartGestureListener != null) {
                this.oldOnChartGestureListener.onChartFling(me1, me2, velocityX, velocityY);
            }
        }

        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            if (this.oldOnChartGestureListener != null) {
                this.oldOnChartGestureListener.onChartScale(me, scaleX, scaleY);
            }
            onMatrixChange();
        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
            if (this.oldOnChartGestureListener != null) {
                this.oldOnChartGestureListener.onChartTranslate(me, dX, dY);
            }
            onMatrixChange();
        }

        public void onMatrixChange() {
            Matrix srcMatrix;
            float[] srcVals = new float[9];

            srcMatrix = chart1.getViewPortHandler().getMatrixTouch();
            srcMatrix.getValues(srcVals);
            srcMatrix.getValues(srcVals);

            // apply X axis scaling and position to dst charts:
            Matrix dstMatrix;
            float[] dstVals = new float[9];
            dstMatrix = chart2.getViewPortHandler().getMatrixTouch();
            dstMatrix.getValues(dstVals);

            dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X];
            dstVals[Matrix.MSKEW_X] = srcVals[Matrix.MSKEW_X];
            dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X];
            dstVals[Matrix.MSKEW_Y] = srcVals[Matrix.MSKEW_Y];
            dstVals[Matrix.MSCALE_Y] = srcVals[Matrix.MSCALE_Y];
            dstVals[Matrix.MTRANS_Y] = srcVals[Matrix.MTRANS_Y];
            dstVals[Matrix.MPERSP_0] = srcVals[Matrix.MPERSP_0];
            dstVals[Matrix.MPERSP_1] = srcVals[Matrix.MPERSP_1];
            dstVals[Matrix.MPERSP_2] = srcVals[Matrix.MPERSP_2];

            dstMatrix.setValues(dstVals);
            chart2.getViewPortHandler().refresh(dstMatrix, chart2, true);
        }
    }
}
