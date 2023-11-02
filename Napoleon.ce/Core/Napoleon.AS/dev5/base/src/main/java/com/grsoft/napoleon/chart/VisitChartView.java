package com.grsoft.napoleon.chart;
import com.grsoft.aceteam.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.grsoft.aceteam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitChartView extends ChartView {
    private IAxisValueFormatter formatter;

    public static class Data implements ChartViewData {
        public List<VisitChartView.DataItem> values = new ArrayList<>();
    }

    public static class DataItem {
        public Date date;
        public long count = 0;
    }

    public VisitChartView(Context context, ChartViewData data) {
        super(context, data);
    }

    @Override
    int getViewType() {
        return 2;
    }

    @Override
    View getView(View view) {
        if (view == null) {
            view = View.inflate(context, R.layout.visit_chart_row, null);
        }

        BarChart chart = view.findViewById(R.id.chart);

        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        if (formatter != null)
            xAxis.setValueFormatter(formatter);

        chart.setData((BarData) chartData);
        chart.animateX(750);

        chart.getAxisLeft().setValueFormatter(valueFormatter);
        chart.getAxisRight().setValueFormatter(valueFormatter);

        return view;
    }

    IAxisValueFormatter valueFormatter = new IAxisValueFormatter() {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.format("%d", (int) value);
        }
    };

    @Override
    ChartData<?> buildChart(ChartViewData data) {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < ((VisitChartView.Data) data).values.size(); i++) {
            values.add(new BarEntry(i, ((VisitChartView.Data) data).values.get(i).count));
        }

        formatter = createLabelFormatter(data);

        BarDataSet set1 = new BarDataSet(values, "Посещения");
        set1.setColor(context.getResources().getColor(R.color.bar_visit_color));

        set1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (value == 0)
                    return "";
                else
                    return String.format("%d", (int) value);
            }
        });

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return new BarData(dataSets);
    }

    protected IAxisValueFormatter createLabelFormatter(final ChartViewData data) {
        return new IAxisValueFormatter() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String result = "";
                VisitChartView.Data d = ((VisitChartView.Data) data);


                if (value > 0 && value < d.values.size())
                    result = sdf.format(d.values.get((int) value).date);

                return result;
            }
        };
    }

    public void setData(ChartViewData buildVisitData) {
        chartData = buildChart(buildVisitData);
    }
}
