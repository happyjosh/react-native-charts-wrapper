package com.github.wuxudong.rncharts.markers;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.FloatYLabel;
import com.github.wuxudong.rncharts.R;

/**
 * Created by jph on 2017/10/9.
 */
public class MFloatYLabel extends FloatYLabel {

    TextView mLabelTxt;

    public MFloatYLabel(Context context) {
        super(context, R.layout.layout_float_y_label);
        mLabelTxt = (TextView) findViewById(R.id.y_label_txt);
    }

    @Override
    public TextView getLabelText() {
        return mLabelTxt;
    }
}
