package com.grsoft.napoleon;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgContractItem;
import com.grsoft.dataobjects.impl.OrgContractImpl;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CreateOrderEx extends CreateOrder {
	Spinner spContract;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		spContract = (Spinner) findViewById(R.id.spContract);
		
		OrgContractImpl c = new OrgContractImpl();
		
		if (c.read("id", order.getData().id)) {
			ArrayAdapter<OrgContractItem> aa = new ArrayAdapter<OrgContractItem>(this, R.layout.simple_spinner_layout, c.getData().items);
			spContract.setAdapter(aa);

			String cid = ((OrderEx)order.getData()).contract.trim();
			
			if (cid.length() > 0)
				for(int i = 0 ; i < aa.getCount(); i++) {
					OrgContractItem ci = aa.getItem(i);
					
					if (ci.id.equals(cid)) {
						spContract.setSelection(i, true);
						break;
					}
				}
		}
	}

	@Override
	protected void initOrder(Order o, Org org) {
		super.initOrder(o, org);
		
		OrderEx oe = (OrderEx)o;
		oe.answers = DocumentsEx.answers;
	}
	
	public void warehauseOpen(){
		Cursor c = null;
		boolean av = false;
		try{
			c = DataBaseManager.getDataBase().rawQuery("select count(*) from action", null);
			
			if(c.moveToFirst() && c.getInt(0) > 0)
				av = true;
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (c != null)
				c.close();
		}
		
		if (av)
			ActionView.open(this, order.getRowid());
		else
			Warehouse.open(this, order, false);
	}
	
	@Override
	public void postOKDone() {
		super.postOKDone();
		
		OrgContractItem kv = (OrgContractItem) spContract.getSelectedItem();
		
		if (kv != null) {
			OrderEx oe = (OrderEx) order.getData();
			oe.contract = kv.id;
		}
	}
}
