package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.grsoft.dataobjects.AKBData;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.manager.MultiSelectAgentHelper.MultiAgentSelectedListener;
import com.grsoft.manager.SelectAkbTypeHelper.AkbSelectListener;
import com.grsoft.util.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AKBReport extends Activity  implements OnClickListener, MultiAgentSelectedListener, AkbSelectListener{
	private HorizontalBarChart chart;
	private String[] labels;
	private MultiSelectAgentHelper slAgentHelper;
	private SelectAkbTypeHelper slAkbTypeHelper;
	
	public static void open(Context context) {
		Intent i = new Intent(context, AKBReport.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.akb_report);
		chart = (HorizontalBarChart) findViewById(R.id.chart);
		
		View v = getLayoutInflater().inflate(R.layout.akb_report_action_bar, null);
		ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowTitleEnabled(false);
        a.setDisplayShowCustomEnabled(true);
        
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(this);
        tvTitle.setText(R.string.agents);
        
        TextView tvType = (TextView) v.findViewById(R.id.tvType);
        tvType.setOnClickListener(this);
		
		chart.getDescription().setEnabled(false);
		chart.setDrawValueAboveBar(true);
		chart.setFitBars(true);
		
		IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter(){

			@Override
			public String getFormattedValue(float value, AxisBase axis) {
				String result = "";
				
				if ( value >= 0 && value < labels.length)
					result = labels[(int) value];
				
				return result;
			}
		};

		XAxis xl = chart.getXAxis();
        xl.setPosition(XAxisPosition.TOP);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(xAxisFormatter);
 
        YAxis yl = chart.getAxisLeft();
        yl.setAxisMaximum(110);
        yl.setAxisMinimum(-10);
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setDrawLabels(false);
        
        yl = chart.getAxisRight();
        yl.setEnabled(false);
        
		slAgentHelper = new MultiSelectAgentHelper(this);
		slAgentHelper.init();
		slAgentHelper.setMultiAgentSelectedListener(this);
		
		slAkbTypeHelper = new SelectAkbTypeHelper(this);
		slAkbTypeHelper.setControl(tvType);
		slAkbTypeHelper.setAkbSelectListener(this);
		
		Legend l = chart.getLegend();
        l.setEnabled(false);
        
		updateChart(slAgentHelper.collectSelected(), slAkbTypeHelper.getSelType());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.agent_dlg)
			return slAgentHelper.createDialog(this);
		else if (id == R.id.akb_type_dlg)
			return slAkbTypeHelper.createDialog(this);
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.agent_dlg)
			slAgentHelper.prepareDialog(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private void updateChart(List<ManagerAgent> agents, int type) {
		List<AKBData> data = createChartData(agents);
		makeChart(data, type);
	}

	protected void makeChart(List<AKBData> data, int type) {
		Collections.sort(data, new Comparator<AKBData>() {

			@Override
			public int compare(AKBData lhs, AKBData rhs) {
				String x = lhs.userid;
				String y = rhs.userid;
				
				ManagerAgent ln = slAgentHelper.findAgent(x);
				
				if (ln != null)
					x = ln.name;
				
				ln = slAgentHelper.findAgent(y);
				
				if (ln != null)
					y = ln.name;
				
				return x.compareTo(y) * -1;
			}
		});
		
		labels = new String[data.size()];
		
		ArrayList<IBarDataSet> ds = new ArrayList<IBarDataSet>();
		
		for(int i = 0; i < data.size(); i++){
			AKBData d = data.get(i);
			ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
			
			vals.add(new BarEntry(i, type == 0 ? d.alldoc : d.inroute));
			
			ManagerAgent a = slAgentHelper.findAgent(d.userid); 
			labels[i] = a != null ? a.name : d.userid;
			
			BarDataSet pset = new BarDataSet(vals, labels[i]);
			pset.setColor(ChartHelper.COMMON_COLORS[i % ChartHelper.COMMON_COLORS.length]);
			ds.add(pset);
		}
		
		chart.clear();
		
		BarData bd = new BarData(ds);
		bd.setValueTextSize(10f);
		bd.setBarWidth(0.9f);
     
		XAxis x = chart.getXAxis();
		x.setLabelCount(data.size());
		chart.setData(bd);
	}

	protected List<AKBData> createChartData(List<ManagerAgent> agents) {
		final int range = 30; 
		final List<AKBData> result = new ArrayList<AKBData>();
		
		Date date = Util.resetTime(new Date());
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		date = c.getTime();
		c.add(Calendar.DATE, -(range+1));
		
		StringBuilder sb = new StringBuilder();
		
		for(ManagerAgent a : agents){
			if(sb.length() > 0)
				sb.append(",");
			sb.append("'");
			sb.append(a.id);
			sb.append("'");
		}
		
		String where = String.format("userid in(%s)", sb.toString());
		
		DataTraveler.travel(AKBData.class, new DataTraveler.Travel<AKBData>(true) {
			@Override
			public boolean travel(DataTraveler<AKBData> item) {
				result.add(item.data);
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
		else if (id == R.id.tvType)
			showDialog(R.id.akb_type_dlg);
	}

	@Override public void onMultiAgentSelect(List<ManagerAgent> sel) { updateChart(sel, slAkbTypeHelper.getSelType()) ;	}
	@Override public void onAkbSelect(int type) { updateChart(slAgentHelper.collectSelected(), type) ; }
}
