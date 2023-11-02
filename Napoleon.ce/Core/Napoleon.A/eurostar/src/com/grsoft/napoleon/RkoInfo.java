package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.dataobjects.Cash;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Pko;
import com.grsoft.dataobjects.Rko;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.PkoImplBase;
import com.grsoft.dataobjects.impl.RkoImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RkoDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;


public class RkoInfo extends PkoInfo {
	private Spinner spCause;
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, RkoInfo.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected PkoImplBase<? extends Pko> createDocument() {	return new RkoImpl(); }
	
	@Override
	protected int getLayoutId() { return R.layout.rkoinfo; }
	
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
		inflateView();
		initView();
	}
	
	private void inflateView(){
		spCause = (Spinner) findViewById(R.id.spCause);
	};
	
	private void initView(){
		List<CharSequence> list = new ArrayList<CharSequence>();
		final String KEY = "ПричинаРКО";
		ConfigImpl cfg = new ConfigImpl();
		list.add(0, "");
		
		Rko rko = (Rko)pkoImpl.getData();
		
		DialogHelper.loadSpinnerFromConfig(cfg, KEY, list, spCause, rko.cause);
	
		((Spinner) findViewById(R.id.spCash)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Cash c = (Cash)arg0.getAdapter().getItem(arg2);
				if( c != null )
					((Rko)pkoImpl.getData()).cash = c.id;
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});

		loadCash(rko.supplyercode, rko.cash);
	
		((Spinner) findViewById(R.id.spFirm)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Firm f = (Firm)arg0.getAdapter().getItem(arg2);
				if( f != null )
					loadCash(f.id, "");
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
	};
	
	@Override
	protected void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), RkoDoc.instance().getObjectName(), 
				pkoImpl, pkoImpl.getRowid(), this).execute((Void[])null);
	}
	
	@Override
	protected void adjustPko() {
		super.adjustPko();
		((Rko)pkoImpl.getData()).cause = spCause.getSelectedItem().toString();
	}
}
