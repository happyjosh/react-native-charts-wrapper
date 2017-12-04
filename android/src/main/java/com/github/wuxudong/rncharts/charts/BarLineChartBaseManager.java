package com.github.wuxudong.rncharts.charts;

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
import com.github.mikephil.charting.charts.FloatLabel;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.wuxudong.rncharts.utils.BridgeUtils;
import com.github.wuxudong.rncharts.utils.FloatLabelUtil;

import java.util.Map;

import javax.annotation.Nullable;

public abstract class BarLineChartBaseManager<T extends BarLineChartBase, U extends Entry> extends YAxisChartBase<T, U> {
    private static final String TAG = BarLineChartBaseManager.class.getSimpleName();

    public static final int COMMAND_GET_EXTRA_OFFSET = 2;
    public static final int COMMAND_SET_ONE_EXTRA_OFFSET = 3;
    public static final int COMMAND_STOP_DECELERATION = 4;
    public static final int COMMAND_RESET_CHART = 5;
    public static final int COMMAND_UPDATE_LAST_ENTRY = 6;
    public static final int COMMAND_ADD_NEW_ENTRY = 7;

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
                "getExtraOffset",
                COMMAND_GET_EXTRA_OFFSET,
                "setOneExtraOffset",
                COMMAND_SET_ONE_EXTRA_OFFSET,
                "stopDeceleration",
                COMMAND_STOP_DECELERATION,
                "resetChart",
                COMMAND_RESET_CHART,
                "updateLastEntry",
                COMMAND_UPDATE_LAST_ENTRY,
                "addNewEntry",
                COMMAND_ADD_NEW_ENTRY
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
            case COMMAND_GET_EXTRA_OFFSET:
                responseExtraOffset(chart);
                break;
            case COMMAND_SET_ONE_EXTRA_OFFSET:
                setOneExtraOffset(chart, args);
                break;
            case COMMAND_STOP_DECELERATION:
                stopDeceleration(chart);
                break;
            case COMMAND_RESET_CHART:
                resetChart(chart);
                break;
            case COMMAND_UPDATE_LAST_ENTRY:
                updateLastEntry(chart, args.getMap(0));
                break;
            case COMMAND_ADD_NEW_ENTRY:
                addNewEntry(chart, args.getMap(0));
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
                .put("topLoadMore", MapBuilder.of("registrationName", "onLoadMore"))
                .build();
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
     * 使图表恢复初始化
     *
     * @param chart
     */
    private void resetChart(Chart chart) {
        Log.i(TAG, "resetChart");
        if (!BarLineChartBase.class.isInstance(chart)) {
            return;
        }

        BarLineChartBase barLineChartBase = (BarLineChartBase) chart;

        barLineChartBase.clear();
        barLineChartBase.fitScreen();
        barLineChartBase.highlightValue(null);
        barLineChartBase.stopDeceleration();
        barLineChartBase.clearAllViewportJobs();

        barLineChartBase.getXAxis().removeAllLimitLines();
        barLineChartBase.getAxisLeft().removeAllLimitLines();
        barLineChartBase.getAxisRight().removeAllLimitLines();
    }

    /**
     * 更新最后一个元素(需要对应的子类实现)
     *
     * @param chart
     * @param readableMap
     */
    protected void updateLastEntry(Chart chart, ReadableMap readableMap) {

    }

    /**
     * 添加一个新元素
     *
     * @param chart
     * @param readableMap
     */
    protected void addNewEntry(Chart chart, ReadableMap readableMap) {

    }
}
