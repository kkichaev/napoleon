package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.OrderReport.AdapterData;
import com.grsoft.napoleon.OrderReport.DataItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderReportAdapter extends BaseAdapter {
	private OrderReport orderReport;
	private List<OrderReport.DataItem> data;
	
	public OrderReportAdapter(OrderReport orderReport) {
		this.orderReport = orderReport;
		data = new ArrayList<OrderReport.DataItem>();
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

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(orderReport, R.layout.orderreportrow, null);
		
		DataItem i = (DataItem) getItem(position);
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(i.folder.name);
		
		tv = (TextView) view.findViewById(R.id.tvWeight);
		tv.setText(Util.IntToScaleStr(i.weight, Consts.WEIGHT_SCALE));
		
		tv = (TextView) view.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(i.sum, Consts.SUM_SCALE));
		
		return view;
	}

	public void setData(AdapterData result) {
		data.clear();
		data = result.getData();
	}

}
