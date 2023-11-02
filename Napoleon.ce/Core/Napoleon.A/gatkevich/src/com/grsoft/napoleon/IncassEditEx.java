package com.grsoft.napoleon;

import java.util.ArrayList;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class IncassEditEx extends IncassDebDistrEdit {
	@Override protected int getContentViewID() { return R.layout.incass_deb_distr_ex; }
	
	int selected = -1;
	boolean started = true;
	
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final IncassEx ie = (IncassEx)doc.getData();
		
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
		Spinner s = (Spinner)findViewById(R.id.spFirma);
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
