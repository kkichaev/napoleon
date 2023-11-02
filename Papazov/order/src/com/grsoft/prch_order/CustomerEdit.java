package com.grsoft.prch_order;

import com.grsoft.prch_order.dataobjects.Gate;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CustomerEdit extends EditPage {

	@Override public String getTitle() { return "Заказчик"; }

	@Override
	public void write(Gate g) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.customer, container, false);
		int[] ids = new int[] {
			R.id.edCustomer, R.id.edPhone,	R.id.edEmail, R.id.edAddress, R.id.edWhDist,
		};
		
		String[] fields = new String[] {
			"customer", "phone", "email", "address", "distance",	
		};
		
		setEditText(v, ids, fields);
		
		return v;
	}

}
