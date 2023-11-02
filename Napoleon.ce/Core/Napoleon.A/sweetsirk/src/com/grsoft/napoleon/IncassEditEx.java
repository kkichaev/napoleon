package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class IncassEditEx extends IncassDebDistrEdit {
	HashSet<String> used = new HashSet<String>();

	@Override protected int getContentViewID() { return R.layout.incass_deb_distr_ex; }
	
	int selected = -1;
	boolean started = true;
	
	void refreshDovers() {
		final IncassEx ie = (IncassEx)doc.getData();

		selected = -1;
		final List<Dover> dvr = new ArrayList<Dover>();
		DataTraveler.travel(Dover.class, new DataTraveler.Travel<Dover>(true) {

			@Override
			public boolean travel(DataTraveler<Dover> item) {
				if(item.data.firm == ie.firm) {
					String nm = item.data.number + Util.simpleDateFormat.format(item.data.date);
					if(!used.contains(nm))
						dvr.add(item.data);
					else if(ie.dvrdate.equals(item.data.date) && ie.dvrnum.equals(item.data.number)) {
						selected = dvr.size();
						dvr.add(item.data);
					}
				}
				return true;
			}
			
		}, "");
		
		Dover empty = new Dover();
		dvr.add(0, empty);
		selected++;
		
		Spinner s = (Spinner)findViewById(R.id.spDVR);
		ArrayAdapter<Dover> aa = new ArrayAdapter<Dover>(s.getContext(), R.layout.simple_spinner_layout, dvr);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		s.setAdapter(aa);
		if(selected > 0 )
			s.setSelection(selected);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final IncassEx ie = (IncassEx)doc.getData();
		
		Date cd = Util.getDayStart(new Date());
		String where = "created >= " + Long.toString(cd.getTime()); 
		DataTraveler.travel(IncassEx.class, new DataTraveler.Travel<IncassEx>() {

			@Override
			public boolean travel(DataTraveler<IncassEx> item) {
				String nm = item.data.dvrnum + Util.simpleDateFormat.format(item.data.dvrdate);
				used.add(nm);
				return true;
			}
		}, where);
		
		refreshDovers();
		
		ConfigImpl ci = new ConfigImpl();
		Spinner s = (Spinner)findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(ci, "Организация", new ArrayList<CharSequence>(), s, ie.firm);
		ci.close();
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(started) {
					started = false;
					return;
				}
				
				sums.clear();
				setSum(0);
				ie.firm = arg0.getSelectedItemPosition();
				refreshDovers();
				adapter.refreshData();
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}
	
	@Override protected Item createItem(Delivery d) { return new ItemEx(d); }
	@Override protected ItemsAdapter createAdapter() { return new ItemsAdapterEx(); }
	
	@Override
	protected void setDocument() {
		super.setDocument();
		final IncassEx ie = (IncassEx)doc.getData();
		Spinner s = (Spinner)findViewById(R.id.spDVR);
		Dover sel = (Dover)s.getSelectedItem();
		if(sel != null && sel.date != null) {
			ie.dvrdate = sel.date;
			ie.dvrnum = sel.number;
		}
		
		s = (Spinner)findViewById(R.id.spFirma);
		ie.firm = s.getSelectedItemPosition();
	}
	
	class ItemEx extends Item {
		public ItemEx(Delivery d) {
			super(d);
			dlv = new DlvKeyEx(d);
		}
	}
	
	class DlvKeyEx extends DlvKey {
		public int firm = 0;
		public DlvKeyEx(Delivery d) {
			super(d);
			firm = ((DeliveryEx)d).firm;
		}
	}
	
	class ItemsAdapterEx extends ItemsAdapter {
		
		@Override
		public void refreshData() {
			items.clear();
			int firm = ((IncassEx)doc.getData()).firm;
			for(Item i : deliveries) {
				if(((DlvKeyEx)i.dlv).firm == firm)
					items.add(i);
			}
			notifyDataSetChanged();
		}
	}
}
