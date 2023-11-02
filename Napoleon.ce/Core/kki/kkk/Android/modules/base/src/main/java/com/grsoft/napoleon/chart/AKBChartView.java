package com.grsoft.napoleon.chart;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.grsoft.napoleon.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class AKBChartView extends ChartView {
	public static class Data implements ChartViewData{
		public int alldoc = 0;
		public int inroute = 0;
	}
	
	public AKBChartView(Context context, Data data) {
		super(context, data);
	}

	@Override
	int getViewType() {
		return 0;
	}

	@Override
	View getView(View view) {
		if(view == null) {
			view = View.inflate(context, R.layout.akb_chart_row, null);
		}
		
		HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.chart);
		
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxisPosition.BOTTOM);
        //xl.setTypeface(tfLight);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);

        YAxis yl = chart.getAxisLeft();
        //yl.setTypeface(tfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        YAxis yr = chart.getAxisRight();
        //yr.setTypeface(tfLight);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);

        chart.setFitBars(true);
        chart.animateY(2500);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f); 

        chart.setData((BarData) chartData);
		return view;
	}

	@Override
	ChartData<?> buildChart(ChartViewData data) {
		Data d = ((AKBChartView.Data)data);
		
		ArrayList<IBarDataSet> ds = new ArrayList<IBarDataSet>();
		ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
		
		vals = new ArrayList<BarEntry>();
		vals.add(new BarEntry(1, d.inroute));
		BarDataSet pset = new BarDataSet(vals, "По маршруту");
		pset.setValueFormatter(new IValueFormatter() {
			@Override
			public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
				return String.format("%d", (int) value);
			}
		});
		pset.setColor(Color.rgb(180, 0, 0));
		ds.add(pset);
		
		vals = new ArrayList<BarEntry>();
		vals.add(new BarEntry(2, d.alldoc));
		pset = new BarDataSet(vals, "Всех точек");
		pset.setColor(Color.rgb(0, 180, 0));
		pset.setValueFormatter(new IValueFormatter() {
			@Override
			public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
				return String.format("%d", (int) value);
			}
		});
		ds.add(pset);
		
		BarData bd = new BarData(ds);
		bd.setValueTextSize(10f);
		bd.setBarWidth(0.9f);
		
		return bd;
	}

	public void setData(Data data) {
		chartData = buildChart(data);
	}
}
