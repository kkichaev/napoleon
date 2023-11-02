package com.grsoft.napoleon.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.grsoft.napoleon.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class TopSalesChartView extends ChartView {
	public static class Data implements ChartViewData{
		public Map<String, Integer> values = new HashMap<String, Integer>(); 
	}

	public TopSalesChartView(Context context, Data data) {
		super(context, data);
	}

	@Override
	int getViewType() {
		return 2;
	}

	@Override
	View getView(View view) {
		if(view == null) {
			view = View.inflate(context, R.layout.top_sales_chart_row, null);
		}

		PieChart chart = (PieChart) view.findViewById(R.id.chart);
		
	    chart.getDescription().setEnabled(false);
	    chart.setHoleRadius(52f);
        chart.setTransparentCircleRadius(57f);
        //chart.setCenterText(mCenterText);
        //chart.setCenterTextTypeface(mTf);
        chart.setCenterTextSize(9f);
        chart.setUsePercentValues(true);
        chart.setExtraOffsets(5, 10, 50, 10);
        chart.setDrawEntryLabels(false);

        chartData.setValueFormatter(new PercentFormatter());
        //chartData.setValueTypeface(mTf);
        chartData.setValueTextSize(11f);
        chartData.setValueTextColor(Color.WHITE);
        chartData.setDrawValues(false);
        // set data
        chart.setData((PieData) chartData);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        chart.animateY(900);

        return view;
	}

	@Override
	ChartData<?> buildChart(ChartViewData data) {
		ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

		for (Entry<String, Integer> e : ((TopSalesChartView.Data)data).values.entrySet()) {
			entries.add(new PieEntry((float) e.getValue(), e.getKey()));
		}

		PieDataSet d = new PieDataSet(entries, "");

		d.setSliceSpace(2f);
		d.setColors(ColorTemplate.COLORFUL_COLORS);

		return new PieData(d);
	}

	public void setData(Data data) {
		chartData = buildChart(data);
	}

}
