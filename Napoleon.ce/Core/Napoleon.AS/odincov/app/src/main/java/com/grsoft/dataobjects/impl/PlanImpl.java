package com.grsoft.dataobjects.impl;

import static com.grsoft.util.Util.GrServerColorToSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.grsoft.dataobjects.Plan;
import com.grsoft.dataobjects.PlanItem;

public class PlanImpl extends DbObject<Plan>{
	private static final String DELIM = ";";
	
	public void open(Context context) {
//		Plan plan = getData();
//
//		if (plan != null){
//			SalesStackedBarChart chart = new SalesStackedBarChart(plan.name,
//					getTitles(), getValues(), getLabels(),
//					getBackground(), getColors());
//			Intent intent = chart.execute(context);
//
//			if (intent != null)
//				context.startActivity(intent);
//		}
	}

	private List<Integer> getColors() {
		List<Integer> result = new ArrayList<Integer>();
		List<PlanItem> items = getData().items;
		
		if (items != null && items.size() > 0)
			for(PlanItem item : items)
				result.add(GrServerColorToSystem(item.color));
		
		return result;
	}

	private int getBackground() {
		return GrServerColorToSystem(data.background);
	}

	private List<String> getLabels() {
		String lbls = new String(getData().labels);
		return Arrays.asList(lbls.split(DELIM));
	}

	private List<double[]> getValues() {
		List<double[]> result = new ArrayList<double[]>();
		
		for(PlanItem item : getData().items)
			if (item != null){
				String[] strVal = new String(item.values).split(DELIM);
				double[] dblVal = new double[strVal.length];
				
				for(int i = 0; i < strVal.length; i++){
					try{
						dblVal[i] = Double.parseDouble(strVal[i]);
					}catch(Exception e){
						dblVal[i] = 0;
					}
				}
				
				result.add(dblVal);
			}
		
		return result;
	}

	private List<String> getTitles() {
		List<String> result = new ArrayList<String>();
		List<PlanItem> items = getData().items;
		
		if (items != null && items.size() > 0)
			for(PlanItem item : items)
				result.add(item.caption);
		
		return result;
	}
}
