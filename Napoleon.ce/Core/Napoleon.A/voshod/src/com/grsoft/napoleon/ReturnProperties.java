package com.grsoft.napoleon;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class ReturnProperties extends CreateReturn {

	private OrgImpl org = new OrgImpl();

	private ArrayList<KeyValue> firms = new ArrayList<KeyValue>();
	private ArrayList<DogData> dogovors = new ArrayList<DogData>();

	class DogData {
		public String number;
		public String name;
		public String cost;
		
		public DogData(OrgDogovor dog) {
			name = dog.name;
			number = dog.number;
			cost = dog.ctype;
		}
		
		@Override public String toString() { return name; }
	}
	

	public static void open(Context context, OrderImplBase<? extends Order> order, boolean editOldOrder) {
		Intent i = new Intent(context, ReturnProperties.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	@Override int getContentViewID() { return R.layout.createreturn; }
	
	@Override
	protected void init() {
		super.init();
				
		org.getData().id = doc.getId();
		org.read();
		org.close();
		
		ReturnEx re = (ReturnEx)doc.getData();
		EditText ed = (EditText)findViewById(R.id.edNumber);
		ed.setText(re.retNum);

		if( (re.params & ParamState.ofCash) != 0 )
			((CheckBox)findViewById(R.id.cbCash)).setChecked(true);

		ConfigImpl config = new ConfigImpl();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onFirmChanged(firms.get(arg2).key.toString());
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});

		DialogHelper.loadSpinnerWithKey(config, "Организация", firms, spFirma, re.suplCode);
		config.close();
	}
	
	protected void onFirmChanged(String firmId) {
		OrgEx oe = (OrgEx)org.getData();		
		
		int sel = -1;
		String id = ((ReturnEx)doc.getData()).dogovor;
		dogovors.clear();
		for(OrgDogovor d : oe.dogovors) {
			if( d.firm.equals(firmId)) {
				DogData dd = new DogData(d);
				if( dd.number.equals(id))
					sel = dogovors.size();
				dogovors.add(dd);
			}
		}
		Spinner s = (Spinner)findViewById(R.id.spDogovor);
		ArrayAdapter<DogData> aa = new ArrayAdapter<DogData>(s.getContext(), R.layout.simple_spinner_layout, dogovors);
		s.setAdapter(aa);
		if( sel >= 0 && sel < s.getCount())
			s.setSelection(sel);		
	}

	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		
		ReturnEx re = (ReturnEx)r;
		EditText ed = (EditText)findViewById(R.id.edNumber);
		re.retNum = ed.getText().toString();
		
		int suppl = ((Spinner) findViewById(R.id.spFirma)).getSelectedItemPosition();
		re.supplyer = suppl;
		
		if(firms.size() > suppl && suppl >= 0)
			re.suplCode = firms.get(suppl).key.toString();
		else
			re.suplCode = "";

		int dog = ((Spinner) findViewById(R.id.spDogovor)).getSelectedItemPosition();
		if( dog >= 0 ) {
			DogData dd = dogovors.get(dog);
			re.sumType = Features.COST_MANAGER.getCostIndex(dd.cost);
			re.dogovor = dd.number;
		}

		if( ((CheckBox)findViewById(R.id.cbCash)).isChecked() ) re.params |= ParamState.ofCash;
		else re.params &= (~ParamState.ofCash);
	}
}
