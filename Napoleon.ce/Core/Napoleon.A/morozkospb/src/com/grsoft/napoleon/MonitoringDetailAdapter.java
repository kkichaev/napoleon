package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.impl.MonitoringImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MonitoringDetailAdapter extends BaseAdapter {
	private List<MonitoringItem> data = new ArrayList<MonitoringItem>();
	private PriceImpl price = new PriceImpl();
	
	private Context context;
	private LinesCountController linesController;
	
	public MonitoringDetailAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	protected String getName(String id) {
		price.read("id", id);
		return price.getData().name;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.monitoringrow, null);
		
		MonitoringItem i = (MonitoringItem) getItem(position);
		String name = getName(i.id);
		
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(name);
		
		if (linesController != null)
			linesController.prepareTextView(tv);
		
		tv = (TextView) view.findViewById(R.id.tvCost);
		tv.setText(Util.IntToScaleStr(i.cost, Consts.SUM_SCALE));
		
		tv = (TextView) view.findViewById(R.id.tvCost1);
		tv.setText(Util.IntToScaleStr(i.cost1, Consts.SUM_SCALE));
		
		tv = (TextView) view.findViewById(R.id.tvCost2);
		tv.setText(Util.IntToScaleStr(i.cost2, Consts.SUM_SCALE));
		
		return view;
	}

	public void setLinesController(LinesCountController linesController) {
		this.linesController = linesController;
	}

	public void refresh(MonitoringImplBase<?> doc) {
		data.clear();
		
		for(MonitoringItem i : doc.getData().items)
			data.add(i);
		
	}
}
