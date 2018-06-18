package com.qin.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.qin.R;
import com.qin.activity.consume.Sphygmomanometer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.BubbleChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class PhysicalReportActivity extends AppCompatActivity {

    @BindView(R.id.physical_report_toolbar)
    Toolbar physicalReportToolbar;
    @BindView(R.id.tmpo_chart)
    LineChartView tmpoChart;
    @BindView(R.id.xueya)
    Sphygmomanometer xueya;
    @BindView(R.id.bubble_chart_view)
    BubbleChartView bubbleChartView;


    private LineChartData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_report);
        ButterKnife.bind(this);
        physicalReportToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhysicalReportActivity.this.finish();
            }
        });

        //加载心跳图表
        tmpoChart = (LineChartView) findViewById(R.id.tmpo_chart);
        generateTempoData();

        //加载血糖图表
        bubbleChartView=(BubbleChartView) findViewById(R.id.bubble_chart_view);
        generateData();
    }


    private void generateTempoData() {
        // I got speed in range (0-50) and height in meters in range(200 - 300). I want this chart to display both
        // information. Differences between speed and height values are large and chart doesn't look good so I need
        // to modify height values to be in range of speed values.

        // The same for displaying Tempo/Height chart.

        float minHeight = 200;
        float maxHeight = 300;
        float tempoRange = 15; // from 0min/km to 15min/km

        float scale = tempoRange / maxHeight;
        float sub = (minHeight * scale) / 2;

        int numValues = 52;

        Line line;
        List<PointValue> values;
        List<Line> lines = new ArrayList<Line>();

        // Height line, add it as first line to be drawn in the background.
        values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            // Some random height values, add +200 to make line a little more natural
            float rawHeight = (float) (Math.random() * 100 + 200);
            float normalizedHeight = rawHeight * scale - sub;
            values.add(new PointValue(i, normalizedHeight));
        }

        line = new Line(values);
        line.setColor(Color.GRAY);
        line.setHasPoints(false);
        line.setFilled(true);
        line.setStrokeWidth(1);
        lines.add(line);

        values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            // Some random raw tempo values.
            float realTempo = (float) Math.random() * 6 + 2;
            float revertedTempo = tempoRange - realTempo;
            values.add(new PointValue(i, revertedTempo));
        }

        line = new Line(values);
        line.setColor(ChartUtils.COLOR_RED);
        line.setHasPoints(false);
        line.setStrokeWidth(3);
        lines.add(line);

        data = new LineChartData(lines);
        Axis distanceAxis = new Axis();
        distanceAxis.setName("Distance");
        distanceAxis.setTextColor(ChartUtils.COLOR_ORANGE);
        distanceAxis.setMaxLabelChars(4);
        distanceAxis.setFormatter(new SimpleAxisValueFormatter().setAppendedText("km".toCharArray()));
        distanceAxis.setHasLines(true);
        distanceAxis.setHasTiltedLabels(true);
        data.setAxisXBottom(distanceAxis);
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (float i = 0; i < tempoRange; i += 0.25f) {
            // I'am translating float to minutes because I don't have data in minutes, if You store some time data
            // you may skip translation.
            axisValues.add(new AxisValue(i).setLabel(formatMinutes(tempoRange - i)));
        }

        Axis tempoAxis = new Axis(axisValues).setName("Tempo [min/km]").setHasLines(true).setMaxLabelChars(4)
                .setTextColor(ChartUtils.COLOR_RED);
        data.setAxisYLeft(tempoAxis);

        // *** Same as in Speed/Height chart.
        // Height axis, this axis need custom formatter that will translate values back to real height values.
        data.setAxisYRight(new Axis().setName("Height [m]").setMaxLabelChars(3)
                .setFormatter(new PhysicalReportActivity.HeightValueFormatter(scale, sub, 0)));

        // Set data
        tmpoChart.setLineChartData(data);

        // Important: adjust viewport, you could skip this step but in this case it will looks better with custom
        // viewport. Set
        // viewport with Y range 0-12;
        Viewport v = tmpoChart.getMaximumViewport();
        v.set(v.left, tempoRange, v.right, 0);
        tmpoChart.setMaximumViewport(v);
        tmpoChart.setCurrentViewport(v);

    }

    private String formatMinutes(float value) {
        StringBuilder sb = new StringBuilder();

        // translate value to seconds, for example
        int valueInSeconds = (int) (value * 60);
        int minutes = (int) Math.floor(valueInSeconds / 60);
        int seconds = (int) valueInSeconds % 60;

        sb.append(String.valueOf(minutes)).append(':');
        if (seconds < 10) {
            sb.append('0');
        }
        sb.append(String.valueOf(seconds));
        return sb.toString();
    }

    /**
     * Recalculated height values to display on axis. For this example I use auto-generated height axis so I
     * override only formatAutoValue method.
     */
    private static class HeightValueFormatter extends SimpleAxisValueFormatter {
        private float scale;
        private float sub;
        private int decimalDigits;
        public HeightValueFormatter(float scale, float sub, int decimalDigits) {
            this.scale = scale;
            this.sub = sub;
            this.decimalDigits = decimalDigits;
        }
        @Override
        public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value, int autoDecimalDigits) {
            float scaledValue = (value + sub) / scale;
            return super.formatValueForAutoGeneratedAxis(formattedValue, scaledValue, this.decimalDigits);
        }
    }


    /*
    加载血糖图表
     */
    private void generateData() {
        BubbleChartData bubbledata;
        ValueShape shape = ValueShape.CIRCLE;
        int BUBBLES_NUM = 8;
        List<BubbleValue> values = new ArrayList<BubbleValue>();
        for (int i = 0; i < BUBBLES_NUM; ++i) {
            BubbleValue value = new BubbleValue(i, (float) Math.random() * 100, (float) Math.random() * 1000);
            value.setColor(ChartUtils.pickColor());
            value.setShape(shape);
            values.add(value);
        }
        bubbledata = new BubbleChartData(values);
        bubbledata.setHasLabels(true);
        bubbledata.setHasLabelsOnlyForSelected(true);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Axis X");
        axisY.setName("Axis Y");
        bubbledata.setAxisXBottom(axisX);
        bubbledata.setAxisYLeft(axisY);
        bubbleChartView.setBubbleChartData(bubbledata);

    }
}
