package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Sklad;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


public class SkladAdapter implements SpinnerAdapter {
	private Context context;
	private List<Sklad> data = new ArrayList<Sklad>();
	
	public SkladAdapter(Context context) {
		this.context = context;
		data.clear();
		
		DataTraveler.travel(Sklad.class, new DataTraveler.Travel<Sklad>(true) {

			@Override
			public boolean travel(DataTraveler<Sklad> item) {
				data.add(item.data);
				return true;
			}}, null
		);
	}
	
	@Override public int getCount() { return data.size(); }
	@Override public Object getItem(int position) { return data.get(position); }
	@Override public long getItemId(int position) {	return 0; }
	@Override public int getItemViewType(int position) { return 0; }
	@Override public View getView(int position, View convertView, ViewGroup parent) { return getDropDownView(position, convertView, parent);}
	@Override public int getViewTypeCount() { return 0; }
	@Override public boolean hasStableIds() { return false; }
	@Override public boolean isEmpty() { return getCount() > 0; }
	@Override public void registerDataSetObserver(DataSetObserver observer) { }
	@Override public void unregisterDataSetObserver(DataSetObserver observer) { }

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = View.inflate(context, R.layout.simple_spinner_layout, null);
		}
		
		Sklad item = (Sklad) getItem(position);
		if(item != null){
			TextView tv = (TextView) convertView.findViewById(R.id.tvFirmaName);
			tv.setText(item.name);
		}
			
		return convertView;

	}

}
