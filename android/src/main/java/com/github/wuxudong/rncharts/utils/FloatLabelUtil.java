package com.github.wuxudong.rncharts.utils;

import android.util.TypedValue;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.uimanager.PixelUtil;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.FloatLabel;

/**
 * Created by jph on 2017/10/19.
 */
public class FloatLabelUtil {

    public static FloatLabel bridgeFloatLabel(Chart chart, ReadableMap propMap) {
        FloatLabel floatLabel = new FloatLabel(chart.getContext());
        floatLabel.setChartView(chart);

        if (BridgeUtils.validate(propMap, ReadableType.Number, "textColor")) {
            floatLabel.getLabelText().setTextColor(propMap.getInt("textColor"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.Number, "textSize")) {
            floatLabel.getLabelText().setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                    (float) propMap.getDouble("textSize"));
        }

        if (BridgeUtils.validate(propMap, ReadableType.Number, "backgroundColor")) {
            floatLabel.getLabelText().setBackgroundColor(propMap.getInt("backgroundColor"));
        }

        int paddingLeft = 0, paddingTop = 0, paddingRight = 0, paddingBottom = 0;
        if (BridgeUtils.validate(propMap, ReadableType.Number, "paddingLeft")) {
            paddingLeft = (int) PixelUtil.toPixelFromDIP(propMap.getDouble("paddingLeft"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.Number, "paddingTop")) {
            paddingTop = (int) PixelUtil.toPixelFromDIP(propMap.getDouble("paddingTop"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.Number, "paddingRight")) {
            paddingRight = (int) PixelUtil.toPixelFromDIP(propMap.getDouble("paddingRight"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.Number, "paddingBottom")) {
            paddingBottom = (int) PixelUtil.toPixelFromDIP(propMap.getDouble("paddingBottom"));
        }
        floatLabel.getLabelText().setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;
        if (BridgeUtils.validate(propMap, ReadableType.Number, "marginLeft")) {
            marginLeft = (int) PixelUtil.toPixelFromDIP(propMap.getDouble("marginLeft"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.Number, "marginTop")) {
            marginTop = (int) PixelUtil.toPixelFromDIP(propMap.getDouble("marginTop"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.Number, "marginRight")) {
            marginRight = (int) PixelUtil.toPixelFromDIP(propMap.getDouble("marginRight"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.Number, "marginBottom")) {
            marginBottom = (int) PixelUtil.toPixelFromDIP(propMap.getDouble("marginBottom"));
        }
        floatLabel.setPadding(marginLeft, marginTop, marginRight, marginBottom);

        return floatLabel;
    }

    public static boolean isEnabled(ReadableMap propMap) {
        return BridgeUtils.validate(propMap, ReadableType.Boolean, "enabled") &&
                propMap.getBoolean("enabled");
    }
}
