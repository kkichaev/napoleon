package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.TopSel;
import com.grsoft.dataobjects.TopSelItem;
import com.grsoft.dataobjects.impl.TopSelImpl;
import com.grsoft.manager.SelectAgentHelper.AgentSelectedListener;
import com.grsoft.manager.SelectPeriodHelper.SelectPeriodListener;
import com.grsoft.util.Consts;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class LeaderReport extends Activity implements OnClickListener, AgentSelectedListener, OnChartValueSelectedListener, OnSeekBarChangeListener, SelectPeriodListener {
	private PieChart chart;
	private SelectAgentHelper slAgentHelper;
	private SelectPeriodHelper slPeriodHelper;
	private SeekBar seekBar;
	private TextView tvSeekLabel;
	
	public static void open(Context context) {
		Intent i = new Intent(context, LeaderReport.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leader_report);
		
		chart = (PieChart) findViewById(R.id.chart);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		tvSeekLabel = (TextView) findViewById(R.id.tvSeekLabel);
		
		View v = getLayoutInflater().inflate(R.layout.activity_report_action_bar, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
		TextView tvPeriod = (TextView) v.findViewById(R.id.tvPeriod);
		
		slAgentHelper = new SelectAgentHelper();
		slAgentHelper.init();
		slAgentHelper.setControl(tvTitle);
		slAgentHelper.insertAllAgentsItem("", getString(R.string.all));
		slAgentHelper.setSelection("");
		
		
		ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowTitleEnabled(false);
        a.setDisplayShowCustomEnabled(true);
        
        tvTitle.setOnClickListener(this);
        tvTitle.setText(R.string.all);
        tvPeriod.setOnClickListener(this);
        
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(0, 10, 0, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.setOnChartValueSelectedListener(this);
        chart.setDrawEntryLabels(false);
        chart.setUsePercentValues(false);
        
        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setXOffset(10);

        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
        
        seekBar.setOnSeekBarChangeListener(this);
        
        slPeriodHelper = new SelectPeriodHelper();
		List<SelectPeriodHelper.Period> ranges = new ArrayList<SelectPeriodHelper.Period>();
		
		SelectPeriodHelper.Period p = new SelectPeriodHelper.Period();
		p.name = getString(R.string.day);
		p.range = 0;
		
		ranges.add(p);
		
		p = new SelectPeriodHelper.Period();
		p.name = getString(R.string.week);
		p.range = 1;
		
		ranges.add(p);
		
		p = new SelectPeriodHelper.Period();
		p.name = getString(R.string.month);
		p.range = 4;
		
		ranges.add(p);
		
		slPeriodHelper.init(ranges, 1);
		slPeriodHelper.setControl(tvPeriod);
		
		updateChart(slPeriodHelper.getSelected(), slAgentHelper.getSelected().id);
		slPeriodHelper.setSelectPeriodListener(this);
		slAgentHelper.setAgentSelectedListner(this);
	}

	private CharSequence generateCenterSpannableText() {return null; }

	private static class Data{
		public String name = "";
		public int qty;
	}
	
	private void updateChart(int range, String id) {
		Map<String, Data> data = createChartData(range, id);
		makeChart(data);
	}
	
	private Map<String, Data> createChartData(int range, String id){
		final Map<String, Data> result = new HashMap<String, Data>();
		
		TopSelImpl topSel = new TopSelImpl();
		topSel.getData().userid = id;
		topSel.getData().period = range;
		
		if (topSel.read()) {
			for(TopSelItem i : topSel.getData().items) {
				Data d = new Data();
				d.name = i.name;
				d.qty = i.qty;
				result.put(i.id, d);
			}
		}
		
		topSel.close();
		
		return result;
	}
	
	private void makeChart(Map<String, Data> data) {
		List<Data> list = new ArrayList<Data>();
		list.addAll(data.values());
		Collections.sort(list, new Comparator<Data>(){

			@Override
			public int compare(Data lhs, Data rhs) {
				return rhs.qty - lhs.qty;
			}});
		
		int ic = seekBar.getProgress() + 1;
		
		if(ic > 0 && ic < list.size())
			list = list.subList(0, ic);
		
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
       
        
        for (Data d : list) {
            entries.add(new PieEntry((float)d.qty,
                    d.name,
                    getResources().getDrawable(R.drawable.star)));
        }

        PieDataSet dataSet = new PieDataSet(entries, null);

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData pd = new PieData(dataSet);
        pd.setValueFormatter(new IValueFormatter() {
			
			@Override
			public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
				return Util.IntToScaleStr((int)value, Consts.QTY_SCALE);
			}
		});
        
        pd.setValueTextSize(11f);
        pd.setValueTextColor(Color.BLACK);
        chart.setData(pd);
        chart.highlightValues(null);

        chart.invalidate();
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
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.agent_dlg)
			return slAgentHelper.createDialog(this);
		else if (id == R.id.period_dlg)
			return slPeriodHelper.createDialog(this);
		
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.agent_dlg)
			slAgentHelper.prepareDialog(dialog);
		else if (id == R.id.period_dlg)
			slPeriodHelper.prepareDialog(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	@Override public void onAgentSelected(ManagerAgent agent) { updateChart(slPeriodHelper.getSelected(), agent.id); }
	@Override public void onValueSelected(Entry e, Highlight h) {}
	@Override public void onNothingSelected() {}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		updateChart(slPeriodHelper.getSelected(), slAgentHelper.getSelected().id);
		tvSeekLabel.setText(Integer.toString(progress + 1));
	}

	@Override public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override public void onStopTrackingTouch(SeekBar seekBar) {}

	@Override public void onPeriodSelect(int range) { updateChart(range, slAgentHelper.getSelected().id); }
}
