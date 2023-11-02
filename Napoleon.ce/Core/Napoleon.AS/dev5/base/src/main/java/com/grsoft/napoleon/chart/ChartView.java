package com.grsoft.napoleon.chart;
import com.grsoft.aceteam.R;

import com.github.mikephil.charting.data.ChartData;

import android.content.Context;
import android.view.View;

public abstract class ChartView {
	public interface ChartViewData{};
	protected Context context;
	protected ChartData<?> chartData;
	
	public ChartView(Context context, ChartViewData data) {
		this.context = context;
		this.chartData = buildChart(data);
	}
	
	abstract int getViewType();
	abstract View getView(View view);
	abstract ChartData<?> buildChart(ChartViewData data);
}
