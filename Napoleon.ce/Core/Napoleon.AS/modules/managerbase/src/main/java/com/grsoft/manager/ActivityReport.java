package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.manager.SelectAgentHelper.AgentSelectedListener;
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

public class ActivityReport extends Activity implements OnClickListener, AgentSelectedListener, SelectPeriodListener {
	private BarChart chart;
	private String[] labels;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
	private SelectAgentHelper slAgentHelper;
	private SelectPeriodHelper slPeriodHelper;
	
	public static void open(Context context) {
		Intent i = new Intent(context, ActivityReport.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_report);
		chart = (BarChart) findViewById(R.id.chart);
		
		View v = getLayoutInflater().inflate(R.layout.activity_report_action_bar, null);
		ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowTitleEnabled(false);
        a.setDisplayShowCustomEnabled(true);
        
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(this);
        tvTitle.setText(R.string.all);
        
        TextView tvPeriod = (TextView) v.findViewById(R.id.tvPeriod);
        tvPeriod.setOnClickListener(this);
		
		chart.getDescription().setEnabled(false);
		
		IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter(){

			@Override
			public String getFormattedValue(float value, AxisBase axis) {
				return labels[(int) value];
			}
			
		};

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); 
        xAxis.setValueFormatter(xAxisFormatter);
 
		slAgentHelper = new SelectAgentHelper();
		slAgentHelper.init();
		slAgentHelper.setControl(tvTitle);
		slAgentHelper.insertAllAgentsItem("", getString(R.string.all));
		slAgentHelper.setSelection("");
		slAgentHelper.setAgentSelectedListner(this);
		
		slPeriodHelper = new SelectPeriodHelper();
		List<SelectPeriodHelper.Period> ranges = new ArrayList<SelectPeriodHelper.Period>();
		
		SelectPeriodHelper.Period p = new SelectPeriodHelper.Period();
		p.name = getString(R.string.week);
		p.range = 7;
		
		ranges.add(p);
		
		p = new SelectPeriodHelper.Period();
		p.name = getString(R.string.month);
		p.range = 7 * 4;
		
		ranges.add(p);
		
		slPeriodHelper.init(ranges, 0);
		slPeriodHelper.setSelectPeriodListener(this);
		slPeriodHelper.setControl(tvPeriod);
		
		updateChart(slPeriodHelper.getSelected(), "");
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.agent_dlg)
			return slAgentHelper.createDialog(this);
		else if (id == R.id.period_dlg)
			return slPeriodHelper.createDialog(this);
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.agent_dlg)
			slAgentHelper.prepareDialog(dialog);
		else if (id == R.id.period_dlg)
			slPeriodHelper.prepareDialog(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private static class Data{
		public int progress = 0;
		public int order_progress = 0;
	}
	
	private void updateChart(int range, String id) {
		Map<Long, List<Data>> data = createChartData(range, id);
		makeChart(data);
	}

	protected void makeChart(Map<Long, List<Data>> data) {
		List<Long> items = new ArrayList<Long>(data.keySet()); 
		Collections.sort(items);
		labels = new String[items.size()];
		
		ArrayList<BarEntry> pVals = new ArrayList<BarEntry>();
		ArrayList<BarEntry> oVals = new ArrayList<BarEntry>();
		
		for(int i = 0; i < items.size(); i++){
			List<Data> v = ((List<Data>)data.get(items.get(i)));
			int cnt  = v.size();
			int psum = 0;
			int osum = 0;
			
			for(Data d : v){
				psum += d.progress;
				osum += d.order_progress;
			}
			
			int pavg = cnt > 0 ? psum / cnt : 0;
			int oavg = cnt > 0 ? osum /cnt : 0;
			
			/***
			 * for demo
			pavg = (int) (Math.random() * 100);
			oavg = (int) (Math.random() * 80);
			*/
			
 			pVals.add(new BarEntry(i, pavg));
			oVals.add(new BarEntry(i, oavg));
			
			labels[i] = sdf.format(new Date(items.get(i)));
		}
		
		chart.clear();
		
		if(items.size() > 0){
			BarDataSet pset = new BarDataSet(pVals, "Посещения");
			pset.setColor(Color.GREEN);
			BarDataSet oset = new BarDataSet(oVals, "Заявки");
			
			ArrayList<IBarDataSet> ds = new ArrayList<IBarDataSet>();
            ds.add(pset);
            ds.add(oset);
            
            BarData bd = new BarData(ds);
            bd.setValueTextSize(10f);
            bd.setBarWidth(0.9f);
            
            chart.setData(bd);
		}
	}

	protected Map<Long, List<Data>> createChartData(int range, String id) {
		final Map<Long, List<Data>> result = new HashMap<Long, List<Data>>();
		
		Date date = Util.resetTime(new Date());
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		date = c.getTime();
		c.add(Calendar.DATE, -(range+1));
		Date start = c.getTime();
		
		String where = String.format("start_date >= %d and start_date< %d", start.getTime(), date.getTime());
		
		if(id.trim().length() > 0)
			where += String.format(" and id='%s'", id);
		
		DataTraveler.travel(AgentReportData.class, new DataTraveler.Travel<AgentReportData>() {
			@Override
			public boolean travel(DataTraveler<AgentReportData> item) {
				long k = item.data.start_date.getTime();
				
				if(!result.containsKey(k)){
					result.put(k, new ArrayList<Data>());
				}
				
				List<Data> di = result.get(k);
				Data d = new Data();
				d.order_progress += item.data.order_progress;
				d.progress += item.data.progress;
				di.add(d);
				
				return true;
			}
		}, where);
		
		return result;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.tvTitle)
			showDialog(R.id.agent_dlg);
		else if (id == R.id.tvPeriod)
			showDialog(R.id.period_dlg);
	}

	@Override
	public void onAgentSelected(ManagerAgent agent) {
		updateChart(slPeriodHelper.getSelected(), agent.id) ;
	}

	@Override
	public void onPeriodSelect(int range) {
		updateChart(range, slAgentHelper.getSelected().id);
	}
}
