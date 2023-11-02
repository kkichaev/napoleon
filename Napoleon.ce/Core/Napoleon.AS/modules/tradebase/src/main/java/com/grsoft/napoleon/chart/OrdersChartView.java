package com.grsoft.napoleon.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.grsoft.napoleon.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class OrdersChartView extends ChartView {
	private IAxisValueFormatter formatter;
	
	public static class Data implements ChartViewData{
		public List<DataItem> values = new ArrayList<DataItem>();
	}
	
	public static class DataItem{
		public Date date;
		public long sum = 0;
	}
	
	public OrdersChartView(Context context, Data data) {
		super(context, data);
	}
	
	@Override
	int getViewType() {
		return 1;
	}
	
	public View getView(View view) {
		if(view == null) {
			view = View.inflate(context, R.layout.orders_chart_row, null);
		}
		
		LineChart chart = (LineChart) view.findViewById(R.id.chart);
		
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        
        if (formatter != null)
        	xAxis.setValueFormatter(formatter);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); 

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); 

        chart.setData((LineData) chartData);
        chart.animateX(750);
		
		return view;
	}

	@Override
	ChartData<?> buildChart(final ChartViewData data) {
		ArrayList<Entry> values1 = new ArrayList<Entry>();

		for (int i = 0; i < ((OrdersChartView.Data)data).values.size(); i++) {
			values1.add(new Entry(i, ((OrdersChartView.Data)data).values.get(i).sum));
		}

		LineDataSet d1 = new LineDataSet(values1, "Ñóììà");
		d1.setLineWidth(2.5f);
		d1.setCircleRadius(4.5f);
		d1.setHighLightColor(Color.rgb(244, 117, 117));
		d1.setDrawValues(false);
		
		formatter = createLabelFormatter(data);

		ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
		
		if(values1.size() > 0)
			sets.add(d1);

		return new LineData(sets);
	}

	protected IAxisValueFormatter createLabelFormatter(final ChartViewData data) {
		return new IAxisValueFormatter(){
			SimpleDateFormat sdf =  new SimpleDateFormat("dd");
			@Override
			public String getFormattedValue(float value, AxisBase axis) {
				String result = "";
				Data d = ((OrdersChartView.Data)data);
				
				
				if (value > 0 && value < d.values.size())
					result = sdf.format(d.values.get((int)value).date);
				
				return result;
			}
		};
	}

	public void setData(Data data) {
		chartData = buildChart(data);
	}
	
}
