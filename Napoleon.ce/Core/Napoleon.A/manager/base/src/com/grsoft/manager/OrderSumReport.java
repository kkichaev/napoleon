package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.OrderSum;
import com.grsoft.manager.MultiSelectAgentHelper.MultiAgentSelectedListener;
import com.grsoft.manager.SelectPeriodHelper.SelectPeriodListener;
import com.grsoft.util.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class OrderSumReport extends Activity
		implements MultiAgentSelectedListener, SelectPeriodListener, OnClickListener, OnChartValueSelectedListener {
	private MultiSelectAgentHelper slAgentHelper;
	private SelectPeriodHelper slPeriodHelper;
	private LineChart chart;
	private List<String> labels;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");

	public static void open(Context context) {
		Intent i = new Intent(context, OrderSumReport.class);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ordersum_report);

		View v = getLayoutInflater().inflate(R.layout.ordersum_report_action_bar, null);
		ActionBar a = getActionBar();
		a.setCustomView(v);
		a.setDisplayShowTitleEnabled(false);
		a.setDisplayShowCustomEnabled(true);

		TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
		tvTitle.setOnClickListener(this);
		tvTitle.setText(R.string.all);

		TextView tvPeriod = (TextView) v.findViewById(R.id.tvPeriod);
		tvPeriod.setOnClickListener(this);

		slAgentHelper = new MultiSelectAgentHelper(this);
		slAgentHelper.init();
		slAgentHelper.setMultiAgentSelectedListener(this);

		slPeriodHelper = new SelectPeriodHelper();
		List<SelectPeriodHelper.Period> ranges = new ArrayList<SelectPeriodHelper.Period>();

		SelectPeriodHelper.Period p = new SelectPeriodHelper.Period();
		p.name = getString(R.string.week);
		p.range = 1;

		ranges.add(p);

		p = new SelectPeriodHelper.Period();
		p.name = getString(R.string.month);
		p.range = 2;

		ranges.add(p);

		slPeriodHelper.init(ranges, 0);
		slPeriodHelper.setSelectPeriodListener(this);
		slPeriodHelper.setControl(tvPeriod);

		chart = (LineChart) findViewById(R.id.chart);
		chart.setOnChartValueSelectedListener(this);

		chart.getDescription().setEnabled(false);
		chart.setTouchEnabled(true);
		chart.setDragDecelerationFrictionCoef(0.9f);
		chart.setDragEnabled(true);
		chart.setScaleEnabled(true);
		chart.setDrawGridBackground(false);
		chart.setHighlightPerDragEnabled(true);
		chart.setPinchZoom(true);
		chart.animateX(2500);
		Legend l = chart.getLegend();

		l.setForm(LegendForm.LINE);
		l.setTextSize(11f);
		l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
		l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
		l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
		l.setDrawInside(false);

		IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter(){

			@Override
			public String getFormattedValue(float value, AxisBase axis) {
				String result = "";
				
				if (value < labels.size())
					result = labels.get((int)value);
				
				return result;
			}
		};
		
		XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); 
        xAxis.setValueFormatter(xAxisFormatter);
		
		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.setDrawGridLines(true);
		leftAxis.setGranularityEnabled(true);
		
		YAxis rightAxis = chart.getAxisRight();
		rightAxis.setDrawGridLines(false);
		rightAxis.setGranularityEnabled(true);

		updateChart(slAgentHelper.collectSelected(), slPeriodHelper.getSelected());
	}

	@Override
	public void onMultiAgentSelect(List<ManagerAgent> sel) {
		updateChart(sel, slPeriodHelper.getSelected());
	}

	@Override
	public void onPeriodSelect(int range) {
		updateChart(slAgentHelper.collectSelected(), range);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.tvTitle)
			showDialog(R.id.agent_dlg);
		else if (id == R.id.tvPeriod)
			showDialog(R.id.period_dlg);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.agent_dlg)
			return slAgentHelper.createDialog(this);
		else if (id == R.id.period_dlg)
			return slPeriodHelper.createDialog(this);
		else
			return super.onCreateDialog(id);
	}

	private static class Data {
		public Date date;
		public int sum = 0;
	}

	private void updateChart(List<ManagerAgent> agents, int range) {
		ChartData data = createChartData(agents, range);
		makeChart(agents, data);
	}

	private static class ChartData {
		Date start;
		Date finish;
		Map<String, List<Data>> data = new HashMap<String, List<Data>>();
	}

	protected ChartData createChartData(List<ManagerAgent> agents, int range) {
		final ChartData result = new ChartData();

		Date date = Util.resetTime(new Date());
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		date = c.getTime();

		if (range == 1)
			c.add(Calendar.DAY_OF_MONTH, -7);
		else if (range == 2)
			c.add(Calendar.DAY_OF_MONTH, -30);

		Date start = c.getTime();

		StringBuilder sb = new StringBuilder();

		for (ManagerAgent a : agents) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append("'");
			sb.append(a.id);
			sb.append("'");
		}

		String where = String.format("date >= %d and date< %d and userid in(%s)", start.getTime(), date.getTime(),
				sb.toString());

		DataTraveler.travel(OrderSum.class, new DataTraveler.Travel<OrderSum>() {
			@Override
			public boolean travel(DataTraveler<OrderSum> item) {
				String k = item.data.userid;

				if (!result.data.containsKey(k)) {
					result.data.put(k, new ArrayList<Data>());
				}

				List<Data> di = result.data.get(k);
				Data d = new Data();
				d.date = item.data.date;
				d.sum = item.data.sum;
				di.add(d);

				return true;
			}
		}, where);

		result.start = start;
		result.finish = date;
		return result;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.period_dlg)
			slPeriodHelper.prepareDialog(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private void makeChart(List<ManagerAgent> agents, ChartData data) {

		chart.clear();

		List<String> items = new ArrayList<String>(data.data.keySet());
		List<ILineDataSet> sets = new ArrayList<ILineDataSet>();
		labels = new ArrayList<String>();
		Map<String, ManagerAgent> agm = new HashMap<String, ManagerAgent>();
		
		for(ManagerAgent a : agents)
			if (!agm.containsKey(a.id))
				agm.put(a.id, a);
		
		boolean genlabels = true;
		
		for (int i = 0; i < items.size(); i++) {
			List<Data> v = ((List<Data>) data.data.get(items.get(i)));
			Map<Long, Integer> map = new HashMap<Long, Integer>();

			for (Data d : v)
				if (!map.containsKey(d.date.getTime()))
					map.put(d.date.getTime(), d.sum);

			long s = data.start.getTime();

			ArrayList<Entry> vals = new ArrayList<Entry>();
			int x = 0;
			while (s < data.finish.getTime()) {
				long y = 0;
				if (map.containsKey(s))
					y = map.get(s) / 100;

				vals.add(new Entry(x, y));
				x++;
				
				if(genlabels)
					labels.add(sdf.format(new Date(s)));
				
				s += 24 * 60 * 60 * 1000;
			}
			
			genlabels = false;
			String k = items.get(i);
			
			if (agm.containsKey(k))
				k = agm.get(k).name;
			
			LineDataSet set = new LineDataSet(vals, k);
			set.setAxisDependency(AxisDependency.LEFT);
			set.setColor(ChartHelper.COMMON_COLORS[i % ChartHelper.COMMON_COLORS.length]);
			set.setCircleColor(Color.BLACK);
			set.setLineWidth(2f);
			set.setCircleRadius(3f);
			set.setFillAlpha(65);
			set.setFillColor(ColorTemplate.getHoloBlue());
			set.setHighLightColor(Color.rgb(244, 117, 117));
			set.setDrawCircleHole(false);

			sets.add(set);
		}

		LineData ld = new LineData(sets);
		ld.setValueTextColor(Color.BLACK);
		ld.setValueTextSize(9f);
		chart.setData(ld);
	}

	@Override
	public void onValueSelected(Entry e, Highlight h) {
		LineDataSet ds = (LineDataSet) chart.getDataSetByTouchPoint(e.getX(), e.getY());
		if(ds != null)
			Toast.makeText(this, ds.getLabel(), Toast.LENGTH_SHORT).show();
	}

	@Override public void onNothingSelected() {	}
}
