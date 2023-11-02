package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {
	
	private OnFocusChangeListener onFocusSet = new OnFocusChangeListener() { @Override public void onFocusChange(View v, boolean hasFocus) { keyHelper.setTargetID(v.getId());	} };
	
	@Override protected int getContentViewID() { return R.layout.incass_deb_distr_ex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		IncassDebDistrEx incass = (IncassDebDistrEx) doc.getData();
		
		AutoCompleteTextView atv = (AutoCompleteTextView)findViewById(R.id.tvDover);
		atv.setAdapter(new DoverAdapter());
		if(incass.dvrnum.length() > 0)
			atv.setText(String.format("%s (%s)", incass.dvrnum, Util.simpleDateFormat.format(incass.dvrdate)));
		atv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if( doc.isEditable() ) {
					Dover d = (Dover) arg0.getAdapter().getItem(arg2);
					IncassDebDistrEx ie = (IncassDebDistrEx)doc.getData();; 
					ie.dvrnum = d.number;
					ie.dvrdate = d.date;
					doc.write();
				}
			}
		});

		atv.setOnFocusChangeListener(onFocusSet);
		atv.setInputType(InputType.TYPE_NULL);
	}

	class DoverAdapter extends BaseAdapter implements Filterable {

		List<Dover> allData = new ArrayList<Dover>();
		List<Dover> data;
		
		public DoverAdapter() {
			DataTraveler.travel(Dover.class, new DataTraveler.Travel<Dover>() {

				@Override
				public boolean travel(DataTraveler<Dover> item) {
					allData.add(item.data);
					item.data = new Dover();
					return true;
				}
			}, "");
			
			data = allData;
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null)
				convertView = View.inflate(IncassDebDistrEditEx.this, R.layout.simple_spinner_layout, null);
			
			Dover dvr = (Dover) getItem(position);
			TextView tv = (TextView) convertView.findViewById(R.id.tvFirmaName);
			tv.setText(dvr.toString());
			
			return convertView;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence str) {
					int len = str.length();
					FilterResults fr = new FilterResults();
					List<Dover> res = new ArrayList<Dover>();
					for(Dover d : allData) {
						if( d.number.length() >= len && d.number.substring(d.number.length() - len).equals(str) )
							res.add(d);
					}
					fr.count = res.size();
					fr.values = res;
					return fr;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence arg0, FilterResults arg1) {
					if(arg1 != null && arg1.count > 0 )
						data = (List<Dover>)arg1.values;
					else
						data = allData;
					notifyDataSetChanged();
				}				
			};
		}
		
	}
}
