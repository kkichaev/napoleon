package com.grsoft.napoleon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DiscountImpl;
import com.grsoft.util.MessageBox;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class DocHelper {
	public static OrgDogovor getDogovor(OrgEx org, String dogId) {
		for(OrgDogovor dg : org.dogovors)
			if(dg.id.equals(dogId))
				return dg;
		
		return null;
	}
	
	public static void prepareSpinners(Spinner spDog, final Spinner spDiscount, List<OrgDogovor> dgvList, String idDog, final String idDisc) {
		int selected = -1;
		ArrayList<OrgDogovor> dogovors = new ArrayList<OrgDogovor>();
		for(OrgDogovor dg : dgvList) {
			if( dg.id.equals(idDog) )
				selected = dogovors.size();
			dogovors.add(dg);
		}
		ArrayAdapter<OrgDogovor> adog = new ArrayAdapter<OrgDogovor>(spDog.getContext(), R.layout.simple_spinner_layout, dogovors);
		spDog.setAdapter(adog);
		if( selected >= 0)
			spDog.setSelection(selected);
	
		if( spDiscount != null ) {
			spDog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				boolean inited = false;
				
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					String selected = null;
					
					if( !inited ) {
						inited = true;
						selected = idDisc;
					}
					
					OrgDogovor dg = (OrgDogovor) arg0.getAdapter().getItem(arg2);
					if( dg != null ) {
						if( dg.stop != 0 ) {
							MessageBox.show(arg0.getContext(), R.string.error, R.string.client_in_stop_list);
						}
						refreshDiscounts(spDiscount, dg.id, selected);
					}
				}
	
				@Override public void onNothingSelected(AdapterView<?> arg0) { }
			});
			refreshDiscounts(spDiscount, idDog, idDisc);
		}
	}
	
	public static void refreshDiscounts(Spinner spDiscount, String idDog, String selected) {
		HashMap<String, DiscountItem> discMap = DiscountImpl.loadFromDogovor(idDog);
		DiscountItem di = new DiscountItem();
		di.id = "";
		di.name = spDiscount.getContext().getString(R.string.no_discount);
		di.val = 0;
		discMap.put("", di);
		
		int sel = -1;
		ArrayList<DiscountItem> items = new ArrayList<DiscountItem>();
		for(DiscountItem i : discMap.values()) {
			if( selected != null && i.id.equals(selected) && i.id.length() == selected.length() )
				sel = items.size();
			items.add(i);
		}

		ArrayAdapter<DiscountItem> aa = new ArrayAdapter<DiscountItem>(spDiscount.getContext(), R.layout.simple_spinner_layout, items);
		spDiscount.setAdapter(aa);
		if( sel >= 0)
			spDiscount.setSelection(sel, true);
	}

	public static void loadSpinner(String key, Spinner sp, Class<? extends DataObject> dataType) {
		try {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			DbReader reader = new DbReader();
			DataObject data = dataType.newInstance();

			boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(dataType), null);

			Field fid = dataType.getField("id");
			Field fname = dataType.getField("name");

			int selected = -1;
			while (bdo) {
				KeyValue kv = new KeyValue(fid.get(data).toString(), fname.get(data).toString());
				if( kv.key.equals(key) )
					selected = values.size();
				values.add(kv);
				bdo = reader.selectNext(data);
			}
			reader.close();

			ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(sp.getContext(), R.layout.simple_spinner_layout, values);

			sp.setAdapter(aa);

			if (selected >= 0)
				sp.setSelection(selected);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setFieldVal(DataObject data, String name, Object value){
		try{
			Field f = data.getClass().getField(name);
			f.set(data, value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Object getFieldVal(DataObject data, String name){
		Object result = "";
		try{
			Field f = data.getClass().getField(name);
			result = f.get(data);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
