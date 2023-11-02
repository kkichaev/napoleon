package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.IncassDebDistrEdit.Item;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {

	@Override protected int getContentViewID() { return R.layout.incass_deb_distr_ex; }
	@Override protected ItemsAdapter createAdapter() { return new ItemsAdapterEx(); }
	@Override protected Item createItem(Delivery d) { return new ItemEx(d); }
	
	void loadSklads(List<String> availSklads, String selected, Spinner spWh, ConfigImpl config) {
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		Config c = config.getData();
		c.key = "Склады";
		config.read();
		
		if(selected.length() > 0 && availSklads.contains(selected) == false) {
			availSklads.add(selected);
		}

		int sel = DialogHelper.makeListWithKey(c.value, values, selected);
		int selectedIndex = -1;
		ArrayList<KeyValueIndex> indexs = new ArrayList<KeyValueIndex>();
		int index  = 0;
		for(KeyValue kv:values) {
			if(availSklads.contains(kv.key.toString())) {
				if(sel == index)
					selectedIndex = indexs.size();
				indexs.add(new KeyValueIndex(kv, index));
			}
			index++;
		}
	
		ArrayAdapter<KeyValueIndex> aa = new ArrayAdapter<KeyValueIndex>(spWh.getContext(), R.layout.simple_spinner_layout, indexs);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spWh.setAdapter(aa);
		if( selectedIndex >= 0 && selectedIndex < spWh.getCount())
			spWh.setSelection(selectedIndex);
	}

	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		IncassDebDistrEx inc = (IncassDebDistrEx) doc.getData();
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = inc.id;
		oi.read();
		oi.close();
		String[] availSklads = oe.sklads.split(",");

		Spinner spWh = (Spinner) findViewById(R.id.spWh);
		ConfigImpl config = new ConfigImpl();
		loadSklads(Arrays.asList(availSklads), inc.whCode, spWh, config);
		config.close();

		spWh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				KeyValueIndex sel = (KeyValueIndex)arg0.getSelectedItem();

				if(((IncassDebDistrEx) doc.getData()).whCode.equals(sel.key.toString()) == false) {
					((IncassDebDistrEx) doc.getData()).whCode = sel.key.toString();
					setSum(0);
					sums.clear();
					adapter.refreshData();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				sendDoc();
			}
		});
	}
	
	class ItemsAdapterEx extends ItemsAdapter {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
		private DeliveryImpl delivery = new DeliveryImpl();
		
		@Override
		public void refreshData() {
			String whCode = ((IncassDebDistrEx)doc.getData()).whCode;
			
			items.clear();
			for(Item i : deliveries) {
				if(whCode.compareTo(((ItemEx)i).whCode) == 0)
					items.add(i);
			}
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			View result = super.getView(position, view, parent);
			
			Item item = (Item)getItem(position);
			delivery.getData().number = item.dlv.number;
			delivery.getData().id = doc.getId();
			delivery.read();
			delivery.close();

			TextView tv = (TextView)result.findViewById(R.id.tvDlvDate);
			String str = sd.format(item.dlv.date)  + "<br>" + sd.format(delivery.getData().payDate);
			tv.setText(Html.fromHtml(str));
			tv.setTextColor(item.dlv.color);
			
			return result;
		}
	}
	
	class ItemEx extends Item {
		public String whCode = "";
		
		public ItemEx(Delivery d) {
			super(d);
			whCode = ((DeliveryEx)d).whCode;
		}
	}
	
	@Override
	protected void btnOkPressed() {
		checkForDelivery(new Runnable() {
			@Override
			public void run() {
				save();
				finish();
			}
		});
	}
	
	protected void sendDoc() {
		checkForDelivery(new Runnable() {
			@Override
			public void run() {
				send();
			}
		});
	}
	
	private void checkForDelivery(Runnable process) {
		if(sums.size() > 0) {
			process.run();
		}else 
			Toast.makeText(this, R.string.delivery_have_to_selected, Toast.LENGTH_SHORT).show();
	}
	
}
