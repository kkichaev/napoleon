package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.database.LayoutItem;
import com.grsoft.dataobjects.impl.LayoutImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.content.Context;
import android.gesture.GestureStroke;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class LayoutAdapter extends BaseAdapter {
	private List<String> pages = new ArrayList<String>();
	private Map<String, List<LayoutItem>> data = new HashMap<String, List<LayoutItem>>();
	private int cursor = 0;
	private Context context;
	
	public LayoutAdapter(Context context, LayoutImpl doc ) {
		this.context = context;
	}

	protected void loadData(LayoutImpl doc) {
		pages.clear();
		data.clear();
		
		Collections.sort(doc.getData().items, new Comparator<LayoutItem>(){
			@Override public int compare(LayoutItem lhs, LayoutItem rhs) {
				int result = lhs.grpos - rhs.grpos;
				
				if(result == 0)
					result = lhs.itname.compareTo(rhs.itname);
				
				return result;  
			}});
		
		for(LayoutItem i : doc.getData().items){
			if(!pages.contains(i.grname))
				pages.add(i.grname);
			
			if(!data.containsKey(i.grname))
				data.put(i.grname, new ArrayList<LayoutItem>());
			
			data.get(i.grname).add(i);	
		}
	}
	
	@Override
	public int getCount() {
		List<LayoutItem> list = getCurItems();
		return list == null ? 0 : list.size();
	}
	
	private List<LayoutItem> getCurItems(){
		List<LayoutItem> result = null;
		
		if(cursor < pages.size() && data.containsKey(pages.get(cursor)))
			result = data.get(pages.get(cursor));
		
		return result;
	}
	
	@Override
	public Object getItem(int position) {
		LayoutItem result = null;
		List<LayoutItem> list = getCurItems();
		
		if (list != null)
			result = list.get(position);
		
		return result;
	}

	@Override
	public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = View.inflate(context, R.layout.layoutrow, null);
		
		LayoutItem item = (LayoutItem) getItem(position);
		
		if(item != null){
			TextView tv = (TextView) convertView.findViewById(R.id.tvName);
			tv.setText(item.itname);
			
			if (item.date.length() > 0)
				tv.setText(Html.fromHtml(item.itname + "<br>" + "<font color='red'>" 
						+ convertView.getContext().getString(R.string.delivery_date) + "&nbsp;" + item.date + "</font>"));
			
			tv = (TextView) convertView.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
		}
		
		convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);		
		
		return convertView;
	}

	public String getGroupText() {
		String result = "";
		
		if(cursor < pages.size())
			result = pages.get(cursor);
		
		return result;
	}
	
	public void next(){
		if(cursor < pages.size() - 1)
			cursor++;
		
		notifyDataSetChanged();
	}
	
	public void prev(){
		if(cursor > 0)
			cursor--;
		
		notifyDataSetChanged();
	}
}
