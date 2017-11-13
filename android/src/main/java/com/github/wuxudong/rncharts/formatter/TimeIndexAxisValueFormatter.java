
package com.github.wuxudong.rncharts.formatter;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IPreviousAxisFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * This formatter is used for passing an array of x-axis labels, on whole x steps.
 */
public class TimeIndexAxisValueFormatter implements IAxisValueFormatter, IPreviousAxisFormatter {
    private static final String TAG = TimeIndexAxisValueFormatter.class.getSimpleName();

    private long[] mValues = new long[]{};
    private int mValueCount = 0;

    private String mFormatType;
    private SimpleDateFormat mSimpleDateFormat;

    static TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+0");

    /**
     * An empty constructor.
     * Use `setValues` to set the axis labels.
     */
    public TimeIndexAxisValueFormatter() {
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param formatType 格式化时间的类型
     * @param values     The values string array
     */
    public TimeIndexAxisValueFormatter(String formatType, long[] values) {
        this.mFormatType = formatType;

        if (values != null)
            setValues(values);

        if (formatType != null) {
            switch (formatType) {
                case "simpleDate":
                    mSimpleDateFormat = new SimpleDateFormat("MM-dd");
                    break;
                case "simpleTime":
                    mSimpleDateFormat = new SimpleDateFormat("HH:mm");
                    break;
                default:
                    mSimpleDateFormat = new SimpleDateFormat("MM-dd");
                    break;
            }

            mSimpleDateFormat.setTimeZone(TIME_ZONE);
        }
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int index = Math.round(value);

        if (index < 0 || index >= mValueCount || index != (int) value)
            return "";

        long timestamp = mValues[index];

        return mSimpleDateFormat.format(timestamp);
    }

    @Override
    public String getFormattedValue(float previousValue, float value, AxisBase axis) {
        int previousIndex = Math.round(previousValue);
        if (previousIndex < 0 || previousIndex >= mValueCount || previousIndex != (int) previousValue) {
            return getFormattedValue(value, axis);
        }

        int index = Math.round(value);
        if (index < 0 || index >= mValueCount || index != (int) value) {
            return "";
        }

        long previousTimestamp = mValues[previousIndex];
        long timestamp = mValues[index];
        if ("simpleTime".equals(mFormatType)) {
            Calendar previousCalender = Calendar.getInstance(TIME_ZONE);
            previousCalender.setTimeInMillis(previousTimestamp);

            Calendar calender = Calendar.getInstance(TIME_ZONE);
            calender.setTimeInMillis(timestamp);

            Log.i(TAG, previousCalender.get(Calendar.YEAR) + "-" + calender.get(Calendar.YEAR) + "-" +
                    previousCalender.get(Calendar.MONTH) + "-" + calender.get(Calendar.MONTH) + "-" +
                    previousCalender.get(Calendar.DAY_OF_MONTH) + "-" + calender.get(Calendar.DAY_OF_MONTH));

            if (previousCalender.get(Calendar.YEAR) != calender.get(Calendar.YEAR) ||
                    previousCalender.get(Calendar.MONTH) != calender.get(Calendar.MONTH) ||
                    previousCalender.get(Calendar.DAY_OF_MONTH) != calender.get(Calendar.DAY_OF_MONTH)) {
                //和上一个坐标轴不是同一天
                SimpleDateFormat newSimpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                newSimpleDateFormat.setTimeZone(TIME_ZONE);
                return newSimpleDateFormat.format(timestamp);
            }
        }


        return mSimpleDateFormat.format(timestamp);
    }

    public long[] getValues() {
        return mValues;
    }

    public void setValues(long[] values) {
        if (values == null)
            values = new long[]{};

        this.mValues = values;
        this.mValueCount = values.length;
    }
}
