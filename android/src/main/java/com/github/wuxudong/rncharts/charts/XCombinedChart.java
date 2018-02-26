package com.github.wuxudong.rncharts.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * 扩展的合并图表
 * Created by jph on 2018/2/3.
 */
public class XCombinedChart extends CombinedChart {
    public XCombinedChart(Context context) {
        super(context);
    }

    public XCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void highlightValue(final Highlight high, final boolean callListener) {
        if (high == null) {
            //延时结束选中状态
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    XCombinedChart.super.highlightValue(high, callListener);
                }
            }, 1000);
        } else {
            super.highlightValue(high, callListener);
        }
    }

    /**
     * 立即执行高亮状态改变
     *
     * @param high
     * @param callListener
     */
    public void immediateHighlightValue(final Highlight high, final boolean callListener) {
        super.highlightValue(high, callListener);
    }

    /**
     * 立即执行高亮状态改变,默认不回调
     *
     * @param highlight
     */
    public void immediateHighlightValue(Highlight highlight) {
        immediateHighlightValue(highlight, false);
    }

}
