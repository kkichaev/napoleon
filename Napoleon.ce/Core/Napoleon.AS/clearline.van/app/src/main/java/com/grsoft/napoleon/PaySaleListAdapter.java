package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PaySale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class PaySaleListAdapter extends BaseAdapter{
	private Context context;
	private List<PaySale> data = new ArrayList<PaySale>();

	public PaySaleListAdapter(Context context){
		this.context = context;
		load();
	}
	
	public void load(){
		Date s = Util.getDate();
		Calendar c = Calendar.getInstance();
		c.setTime(s);
		c.add(Calendar.DATE, 1);
		Date f = c.getTime();
		
		data.clear();
		StringBuilder sb = new StringBuilder();
		
		sb.append("[created] >= ").append(s.getTime()).append(" and [created] < ").append(f.getTime());
		
		String where = sb.toString();
		
		DataTraveler.travel(PaySale.class, new DataTraveler.Travel<PaySale>(true){

			@Override
			public boolean travel(DataTraveler<PaySale> item) {
				data.add(item.data);
				return true;
			}}, where);
	}
		
	@Override public int getCount() { return data.size();	}
	@Override public Object getItem(int position) { return data.get(position); }
	@Override public long getItemId(int position) {	return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.paysale_row, null);
		
		PaySale i = (PaySale) getItem(position);
		TextView tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(i.name);
		
		tv = (TextView) convertView.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(i.sum, Consts.SUM_SCALE));
		
		convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
				
		return convertView;
	}
	
}