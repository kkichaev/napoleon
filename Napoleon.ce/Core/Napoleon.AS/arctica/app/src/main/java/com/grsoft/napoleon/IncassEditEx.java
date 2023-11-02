package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class IncassEditEx extends IncassDebDistrEdit {
	ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	
	@Override
	protected int getContentViewID() {
		return R.layout.incassex;
	}
	
	@Override protected ItemsAdapter createAdapter() { 
		ConfigImpl config = new ConfigImpl();
		
		final IncassEx ie = (IncassEx)doc.getData();
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, ie.supplyer);
		config.close();
	
		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(doc.isEditable()) {
					if(arg2 != ie.supplyer)
						sums.clear();
					adapter.refreshData();
				}
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});

		return new ItemsAdapterEx(); 
	}

	@Override
	protected void childInit(Incass incass, Org org) {
		super.childInit(incass, org);
		EditText ed = findViewById(R.id.edPANumber);
		ed.setText(((IncassEx)incass).panumber);
	}

	@Override
	public void onBackPressed() {
		if( !Features.OK_BTN_INCASS && !save() )
		{
			if(doc.getData().sum != 0) {
				Toast.makeText(this, "Не заполнена доверенность", Toast.LENGTH_LONG).show();
				return;
			} else {
				doc.delete();
			}
		}
		finish();
	}

	@Override
	protected void btnOkPressed() {
		if(!save()) {
			Toast.makeText(this, "Введите номер доверенности", Toast.LENGTH_LONG).show();
		} else {
			finish();
		}
	}

	@Override
	protected boolean save() {
		if(!doc.isEditable())
			return true;

		boolean ret = super.save();
		if(ret && ((IncassEx)doc.getData()).panumber.length() == 0) {
			ret = false;
		}

		return ret;
	}

	@Override
	protected void setDocument() {
		super.setDocument();
		
		IncassEx ie = (IncassEx)doc.getData();

		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int suppl = spFirma.getSelectedItemPosition();
		if( suppl >= 0 )
			ie.supplyer = suppl;

		EditText ed = findViewById(R.id.edPANumber);
		ie.panumber = ed.getText().toString();
	}
	
	public class ItemsAdapterEx extends ItemsAdapter {
		@Override
		public void refreshData() {
			Collections.sort(deliveries, new Comparator<Item>() {

				@Override
				public int compare(Item arg0, Item arg1) {
					return arg0.dlv.date.compareTo(arg1.dlv.date);
				}
			});
			String f = firms.size() == 0 ? "" :
					firms.get(((Spinner) findViewById(R.id.spFirma)).getSelectedItemPosition()).toString();
			
			items.clear();
			for(Item i : deliveries) {
				if(((DeliveryEx)i.dlv.delivery).firma.equals(f))
					items.add(i);
			}
			notifyDataSetChanged();
		}
	}
}
