package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Concurent;
import com.grsoft.dataobjects.ConcurentItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.RemnantsEx;
import com.grsoft.napoleon.CreateRemnants;
import android.content.Context;


public class RemnantsImplEx extends RemnantsImpl {
	@Override
	protected void openPrice(Context context) {
		CreateRemnants.open(context, this);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		DataTraveler.travel(Concurent.class, new DataTraveler.Travel<Concurent>() {

			@Override
			public boolean travel(DataTraveler<Concurent> item) {
				ConcurentItem i = new ConcurentItem();
				i.id = item.data.id;
				i.name = item.data.name;
				((RemnantsEx)data).cncs.add(i);
				return true;
			}}, null);
	}
}
