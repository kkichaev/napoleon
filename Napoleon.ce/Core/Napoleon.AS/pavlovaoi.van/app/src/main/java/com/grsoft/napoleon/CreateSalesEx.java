package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.content.Context;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;


public class CreateSalesEx extends CreateSales {
	private Spinner spDogovor;
	protected int getSalesLayoutId() { return R.layout.createsalesex;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		spDogovor = (Spinner) findViewById(R.id.spDogovor);
		
		OrgEx o = (OrgEx) oi.getData();
		List<ODV> d = new ArrayList<ODV>();
		for(OrgDogovor od: o.dogovors)
			d.add(new ODV(od));
		Collections.sort(d, new Comparator<ODV>() {	@Override public int compare(ODV lhs, ODV rhs) { return lhs.name.compareTo(rhs.name); }});
		ArrayAdapter<ODV> aa = new ArrayAdapter<ODV>(this, R.layout.simple_spinner_layout, d);
		spDogovor.setAdapter(aa);
	}
	
	static class ODV extends OrgDogovor{
		public ODV(OrgDogovor i){
			this.name = i.name;
			this.firm = i.firm;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	@Override
	protected void postOkDone(Sales sales) {
		OrgDogovor d = (OrgDogovor) spDogovor.getSelectedItem();
		
		if( d != null){
			((SalesEx)sales).dogovor = d.id;
			sales.supplyercode = d.firm;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Sales s = salesImpl.getData();
		
		if(s.supplyercode.length() > 0){
			Adapter a = spDogovor.getAdapter();
			for(int i = 0; i < a.getCount(); i++){
				OrgDogovor d = (OrgDogovor) a.getItem(i);
				
				if(d.firm.equals(s.supplyercode)){
					spDogovor.setSelection(i, true);
					break;
				}
			}
		}
	}
	
	private final static int MAX_LEN_NUMBER = 10;
	
	@Override
	protected void okDone(Context context, boolean updateSumType) {
		String num = edNumber.getText().toString().trim();
		
		if (num.length() > MAX_LEN_NUMBER)
			Toast.makeText(context, R.string.number_len_sales_exceeded, Toast.LENGTH_SHORT).show();
		else
			super.okDone(context, updateSumType);
	}
}
