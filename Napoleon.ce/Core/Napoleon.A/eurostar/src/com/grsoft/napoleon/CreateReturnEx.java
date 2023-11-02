package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CreateReturnEx extends CreateReturn {
	int selectedFirm;
	
	@Override int getContentViewID() { return R.layout.createreturnex; }

	@Override
	protected void init() {
		final ReturnEx re = (ReturnEx)doc.getData();
		final List<Firm> fv = new ArrayList<Firm>();
		selectedFirm = 0;
		DataTraveler.travel(Firm.class, new DataTraveler.Travel<Firm>(){

			@Override
			public boolean travel(DataTraveler<Firm> item) {
				if( item.data.id.equals(re.firma) )
					selectedFirm = fv.size();
				fv.add(item.data);
				item.data = new Firm();
				return true;
			}
			
		}, null);
		Spinner spFirm = (Spinner) findViewById(R.id.spFirm);
		ArrayAdapter<Firm> aa = new ArrayAdapter<Firm>(this, R.layout.simple_spinner_layout, fv);
		spFirm.setAdapter(aa);
		if( selectedFirm < aa.getCount())
			spFirm.setSelection(selectedFirm);
	
		ConfigImpl config = new ConfigImpl();
		Spinner spWh = (Spinner) findViewById(R.id.spWh);
		DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(), spWh, re.whCode);
		config.close();
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);

		ReturnEx re = (ReturnEx)r;
		
		Spinner spFirm = (Spinner)findViewById(R.id.spFirm);
		Firm sel = (Firm)spFirm.getSelectedItem();
		if( sel != null ) {
			re.firma = sel.id;
		}
	
		KeyValue whSel = (KeyValue)((Spinner) findViewById(R.id.spWh)).getSelectedItem();
		if( whSel != null )
			re.whCode = whSel.key.toString();
	}
}

