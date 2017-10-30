package com.github.wuxudong.rncharts.charts;

import android.graphics.Matrix;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.FloatLabel;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.wuxudong.rncharts.utils.BridgeUtils;
import com.github.wuxudong.rncharts.utils.FloatLabelUtil;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public abstract class BarLineChartBaseManager<T extends BarLineChartBase, U extends Entry> extends YAxisChartBase<T, U> {
    private static final String TAG = BarLineChartBaseManager.class.getSimpleName();

    public static final int COMMAND_CHANGE_MATRIX = 1;
    public static final int COMMAND_GET_EXTRA_OFFSET = 2;
    public static final int COMMAND_SET_ONE_EXTRA_OFFSET = 3;
    public static final int COMMAND_STOP_DECELERATION = 4;
    public static final int COMMAND_HIGHLIGHT_BY_OTHERS = 5;
    public static final int COMMAND_HIDE_HIGHLIGHT = 6;

    @Override
    public void setYAxis(Chart chart, ReadableMap propMap) {
        BarLineChartBase barLineChart = (BarLineChartBase) chart;

        if (BridgeUtils.validate(propMap, ReadableType.Map, "left")) {
            YAxis leftYAxis = barLineChart.getAxisLeft();
            setCommonAxisConfig(chart, leftYAxis, propMap.getMap("left"));
            setYAxisConfig(leftYAxis, propMap.getMap("left"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.Map, "right")) {
            YAxis rightYAxis = barLineChart.getAxisRight();
            setCommonAxisConfig(chart, rightYAxis, propMap.getMap("right"));
            setYAxisConfig(rightYAxis, propMap.getMap("right"));
        }
    }

    @ReactProp(name = "drawGridBackground")
    public void setDrawGridBackground(BarLineChartBase chart, boolean enabled) {
        chart.setDrawGridBackground(enabled);
    }

    @ReactProp(name = "gridBackgroundColor")
    public void setGridBackgroundColor(BarLineChartBase chart, Integer color) {
        chart.setGridBackgroundColor(color);
    }

    @ReactProp(name = "drawBorders")
    public void setDrawBorders(BarLineChartBase chart, boolean enabled) {
        chart.setDrawBorders(enabled);
    }

    @ReactProp(name = "borderColor")
    public void setBorderColor(BarLineChartBase chart, Integer color) {
        chart.setBorderColor(color);
    }

    @ReactProp(name = "borderWidth")
    public void setBorderWidth(BarLineChartBase chart, float width) {
        chart.setBorderWidth(width);
    }

    @ReactProp(name = "maxVisibleValueCount")
    public void setMaxVisibleValueCount(BarLineChartBase chart, int count) {
        chart.setMaxVisibleValueCount(count);
    }

    @ReactProp(name = "autoScaleMinMaxEnabled")
    public void setAutoScaleMinMaxEnabled(BarLineChartBase chart, boolean enabled) {
        chart.setAutoScaleMinMaxEnabled(enabled);
    }

    @ReactProp(name = "keepPositionOnRotation")
    public void setKeepPositionOnRotation(BarLineChartBase chart, boolean enabled) {
        chart.setKeepPositionOnRotation(enabled);
    }

    @ReactProp(name = "scaleEnabled")
    public void setScaleEnabled(BarLineChartBase chart, boolean enabled) {
        chart.setScaleEnabled(enabled);
    }

    @ReactProp(name = "dragEnabled")
    public void setDragEnabled(BarLineChartBase chart, boolean enabled) {
        chart.setDragEnabled(enabled);
    }

    @ReactProp(name = "scaleXEnabled")
    public void setScaleXEnabled(BarLineChartBase chart, boolean enabled) {
        chart.setScaleXEnabled(enabled);
    }

    @ReactProp(name = "scaleYEnabled")
    public void setScaleYEnabled(BarLineChartBase chart, boolean enabled) {
        chart.setScaleYEnabled(enabled);
    }

    @ReactProp(name = "pinchZoom")
    public void setPinchZoom(BarLineChartBase chart, boolean enabled) {
        chart.setPinchZoom(enabled);
    }

    @ReactProp(name = "doubleTapToZoomEnabled")
    public void setDoubleTapToZoomEnabled(BarLineChartBase chart, boolean enabled) {
        chart.setDoubleTapToZoomEnabled(enabled);
    }

    @ReactProp(name = "zoom")
    public void setZoom(BarLineChartBase chart, ReadableMap propMap) {
        if (BridgeUtils.validate(propMap, ReadableType.Number, "scaleX") &&
                BridgeUtils.validate(propMap, ReadableType.Number, "scaleY") &&
                BridgeUtils.validate(propMap, ReadableType.Number, "xValue") &&
                BridgeUtils.validate(propMap, ReadableType.Number, "yValue")) {

            YAxis.AxisDependency axisDependency = YAxis.AxisDependency.LEFT;
            if (propMap.hasKey("axisDependency") &&
                    propMap.getString("axisDependency").equalsIgnoreCase("RIGHT")) {
                axisDependency = YAxis.AxisDependency.RIGHT;
            }

            chart.zoom(
                    (float) propMap.getDouble("scaleX"),
                    (float) propMap.getDouble("scaleY"),
                    (float) propMap.getDouble("xValue"),
                    (float) propMap.getDouble("yValue"),
                    axisDependency
            );
        }
    }

    @ReactProp(name = "scaleLimit")
    public void setScaleLimit(BarLineChartBase chart, ReadableMap propMap) {
        if (BridgeUtils.validate(propMap, ReadableType.Number, "scaleMinX") &&
                BridgeUtils.validate(propMap, ReadableType.Number, "scaleMinY")) {
            chart.setScaleMinima((float) propMap.getDouble("scaleMinX"),
                    (float) propMap.getDouble("scaleMinY"));
        }

        if (BridgeUtils.validate(propMap, ReadableType.Number, "scaleMaxX") &&
                BridgeUtils.validate(propMap, ReadableType.Number, "scaleMaxY")) {
            chart.setScaleMaxima((float) propMap.getDouble("scaleMaxX"),
                    (float) propMap.getDouble("scaleMaxY"));
        }
    }

    @ReactProp(name = "floatYLabel")
    public void setFloatYLabel(BarLineChartBase chart, ReadableMap propMap) {
        Log.i(TAG, "setFloatYLabel");
        if (!FloatLabelUtil.isEnabled(propMap)) {
            chart.setRightFloatYLabel(null);
            return;
        }

        FloatLabel floatLabel = FloatLabelUtil.bridgeFloatLabel(chart, propMap);

        if (BridgeUtils.validate(propMap, ReadableType.Number, "value")) {
            chart.setFloatYValue((float) propMap.getDouble("value"));
        }
        chart.setRightFloatYLabel(floatLabel);
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        Map<String, Integer> map = MapBuilder.of(
                "changeMatrix",
                COMMAND_CHANGE_MATRIX,
                "getExtraOffset",
                COMMAND_GET_EXTRA_OFFSET,
                "setOneExtraOffset",
                COMMAND_SET_ONE_EXTRA_OFFSET,
                "stopDeceleration",
                COMMAND_STOP_DECELERATION,
                "highlightByOthers",
                COMMAND_HIGHLIGHT_BY_OTHERS,
                "hideHighlight",
                COMMAND_HIDE_HIGHLIGHT
        );
        if (super.getCommandsMap() != null) {
            map.putAll(super.getCommandsMap());
        }
        return map;
    }

    @Override
    public void receiveCommand(View root, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        Log.i(TAG, "receiveCommand " + commandId + " " + args);

        if (!Chart.class.isInstance(root)) {
            return;
        }

        Chart chart = (Chart) root;
        switch (commandId) {
            case COMMAND_CHANGE_MATRIX:
                changeMatrix(chart, args);
                break;
            case COMMAND_GET_EXTRA_OFFSET:
                responseExtraOffset(chart);
                break;
            case COMMAND_SET_ONE_EXTRA_OFFSET:
                setOneExtraOffset(chart, args);
                break;
            case COMMAND_STOP_DECELERATION:
                stopDeceleration(chart);
                break;
            case COMMAND_HIGHLIGHT_BY_OTHERS:
                highlightByOthers(chart, args);
                break;
            case COMMAND_HIDE_HIGHLIGHT:
                //取消高亮
                chart.highlightValue(null, false);
                break;
        }
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                //拖拽和缩放时回调
                .put("topMatrixChange", MapBuilder.of("registrationName", "onMatrixChange"))
                .put("topGetExtraOffset", MapBuilder.of("registrationName", "onGetExtraOffset"))
                .put("topSingleTapped", MapBuilder.of("registrationName", "onSingleTapped"))
                .build();
    }

    /**
     * 改变图表的缩放和移动位置
     *
     * @param chart
     * @param args
     */
    private void changeMatrix(Chart chart, @Nullable ReadableArray args) {
        Log.i(TAG, "changeMatrix");

        float[] dstVals = new float[9];

        Matrix dstMatrix = chart.getViewPortHandler().getMatrixTouch();
        dstMatrix.getValues(dstVals);

        dstVals[Matrix.MSCALE_X] = (float) args.getDouble(Matrix.MSCALE_X);
        dstVals[Matrix.MSKEW_X] = (float) args.getDouble(Matrix.MSKEW_X);
        dstVals[Matrix.MTRANS_X] = (float) args.getDouble(Matrix.MTRANS_X);
        dstVals[Matrix.MSKEW_Y] = (float) args.getDouble(Matrix.MSKEW_Y);
        dstVals[Matrix.MSCALE_Y] = (float) args.getDouble(Matrix.MSCALE_Y);
        dstVals[Matrix.MTRANS_Y] = (float) args.getDouble(Matrix.MTRANS_Y);
        dstVals[Matrix.MPERSP_0] = (float) args.getDouble(Matrix.MPERSP_0);
        dstVals[Matrix.MPERSP_1] = (float) args.getDouble(Matrix.MPERSP_1);
        dstVals[Matrix.MPERSP_2] = (float) args.getDouble(Matrix.MPERSP_2);

        dstMatrix.setValues(dstVals);

        chart.getViewPortHandler().refresh(dstMatrix, chart, true);
    }

    /**
     * 返回extraOffset到js
     */
    private void responseExtraOffset(Chart chart) {
        Log.i(TAG, "responseExtraOffset");
        WritableMap event = Arguments.createMap();

        event.putDouble("extraLeftOffset", chart.getViewPortHandler().offsetLeft());
        event.putDouble("extraRightOffset", chart.getViewPortHandler().offsetRight());

        ReactContext reactContext = (ReactContext) chart.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                chart.getId(),
                "topGetExtraOffset",
                event);
    }

    /**
     * 设置某个方向的偏移
     *
     * @param chart
     * @param args
     */
    private void setOneExtraOffset(Chart chart, @Nullable ReadableArray args) {
        Log.i(TAG, "setOneExtraOffset " + args);

        String type = args.getString(0);
        float offset = (float) args.getDouble(1);

        if (type == null) {
            return;
        }

        switch (type) {
            case "left":
                chart.setExtraLeftOffset(PixelUtil.toDIPFromPixel(offset));
                break;
            case "right":
                chart.setExtraRightOffset(PixelUtil.toDIPFromPixel(offset));
                break;
        }

        chart.postInvalidate();
    }

    /**
     * 停止惯性滑动
     *
     * @param chart
     */
    private void stopDeceleration(Chart chart) {
        if (!BarLineChartBase.class.isInstance(chart)) {
            return;
        }

        ((BarLineChartBase) chart).stopDeceleration();
    }

    /**
     * 根据其他关联图表的选中高亮信息来手动高亮选中
     *
     * @param chart 需要操作的图表
     * @param args  其他图表传过来的高亮参数
     */
    private void highlightByOthers(Chart chart, @Nullable ReadableArray args) {
        float x = (float) args.getDouble(0);
        float y = (float) args.getDouble(1);
        float touchY = (float) args.getDouble(2);
//        int dataIndex = args.getInt(3);
//        int dataSetIndex = args.getInt(4);
        float manualYOffset = PixelUtil.toPixelFromDIP((float) args.getDouble(3));//表2和表1Y方向的layout偏差


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
}
