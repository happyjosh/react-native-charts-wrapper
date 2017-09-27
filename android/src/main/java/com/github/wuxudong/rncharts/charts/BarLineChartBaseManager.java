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
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.wuxudong.rncharts.utils.BridgeUtils;

import java.util.Map;

import javax.annotation.Nullable;

public abstract class BarLineChartBaseManager<T extends BarLineChartBase, U extends Entry> extends YAxisChartBase<T, U> {
    private static final String TAG = BarLineChartBaseManager.class.getSimpleName();

    public static final int COMMAND_CHANGE_MATRIX = 1;
    public static final int COMMAND_GET_EXTRA_OFFSET = 2;
    public static final int COMMAND_SET_EXTRA_OFFSET = 3;
    public static final int COMMAND_STOP_DECELERATION = 4;

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

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "changeMatrix",
                COMMAND_CHANGE_MATRIX,
                "getExtraOffset",
                COMMAND_GET_EXTRA_OFFSET,
                "setExtraOffset",
                COMMAND_SET_EXTRA_OFFSET,
                "stopDeceleration",
                COMMAND_STOP_DECELERATION);
    }

    @Override
    public void receiveCommand(View root, int commandId, @Nullable ReadableArray args) {
        Log.i(TAG, "receiveCommand " + args);

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
            case COMMAND_SET_EXTRA_OFFSET:
                setExtraOffset(chart, args);
                break;
            case COMMAND_STOP_DECELERATION:
                stopDeceleration(chart);
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

        event.putDouble("extraLeftOffset", chart.getExtraLeftOffset());
        event.putDouble("extraRightOffset", chart.getExtraRightOffset());

        ReactContext reactContext = (ReactContext) chart.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                chart.getId(),
                "topGetExtraOffset",
                event);
    }

    /**
     * 设置位置
     *
     * @param chart
     * @param args
     */
    private void setExtraOffset(Chart chart, @Nullable ReadableArray args) {
        Log.i(TAG, "setExtraOffset " + args);
        chart.setExtraLeftOffset((float) args.getDouble(0));
        chart.setExtraLeftOffset((float) args.getDouble(1));
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
}
