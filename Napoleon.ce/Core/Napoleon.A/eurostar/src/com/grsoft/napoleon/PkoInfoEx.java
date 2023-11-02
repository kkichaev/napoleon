package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.grsoft.dataobjects.Cash;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.PkoEx;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.napoleon.modules.print.util.DocHelper;

public class PkoInfoEx extends PkoInfo {
	@Override protected int getLayoutId() { return R.layout.pkoinfoex; }

	int selectedCash;
	
	void loadCash(String firm, final String seleceted) {
		
		selectedCash = 0;
		
		final List<Cash> vals = new ArrayList<Cash>();
		DataTraveler.travel(Cash.class, new DataTraveler.Travel<Cash>() {

			@Override
			public boolean travel(DataTraveler<Cash> item) {
				if(item.data.id.equals(seleceted))
					selectedCash = vals.size();
				vals.add(item.data);
				item.data = new Cash();
				
				return true;
			}
		}, "firm='"+firm+"'");
	
		Spinner spCash = (Spinner) findViewById(R.id.spCash);
		ArrayAdapter<Cash> aa = new ArrayAdapter<Cash>(this, R.layout.simple_spinner_layout, vals);
		spCash.setAdapter(aa);
		if( selectedFirm < aa.getCount())
			spCash.setSelection(selectedCash);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PkoEx pe = (PkoEx)pkoImpl.getData();
		((Spinner) findViewById(R.id.spCash)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Cash c = (Cash)arg0.getAdapter().getItem(arg2);
				if( c != null )
					((PkoEx)pkoImpl.getData()).cash = c.id;
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});

		loadCash(pe.supplyercode, pe.cash);
		((Spinner) findViewById(R.id.spFirm)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Firm f = (Firm)arg0.getAdapter().getItem(arg2);
				if( f != null ){
					loadCash(f.id, "");
					initDocNumber(f.id);
					edNumber.setText(pkoImpl.getData().number);
				}
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
	
	}
	
	protected void initDocNumber(String id) {
		pkoImpl.getData().supplyercode = id; 
		pkoImpl.getData().number = DocHelper.makeDocNumber(pkoImpl);
	}
}
