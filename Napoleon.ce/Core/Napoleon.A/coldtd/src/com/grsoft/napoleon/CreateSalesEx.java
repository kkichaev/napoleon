package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.util.view.dialog_helper.DateHandler;


public class CreateSalesEx extends CreateSales {
	private DateHandler dateHandler;
	private static final int DIALOG_DATE_PICKER_ID = 0;
	
	int selected = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), salesImpl.getData().date, DIALOG_DATE_PICKER_ID);
	
		findViewById(R.id.edNumber).setEnabled(false);
		
		final String dogId = ((SalesEx)salesImpl.getData()).dogId; 
		
		final List<OrgDogovor> dogs = new ArrayList<OrgDogovor>();
		DataTraveler.travel(OrgDogovor.class, new DataTraveler.Travel<OrgDogovor>() {

			@Override
			public boolean travel(DataTraveler<OrgDogovor> item) {
				if(dogId .equals(item.data.id))
					selected = dogs.size();
				
				dogs.add(item.data);
				item.data = new OrgDogovor();
				return true;
			}
			
		}, "ido='" + ((OrgEx)oi.getData()).ido + "'");
		
		if(dogs.size() > 0) {
			Spinner sp = (Spinner)findViewById(R.id.spDog);
			ArrayAdapter<OrgDogovor> aa = new ArrayAdapter<OrgDogovor>(this, R.layout.simple_spinner_layout, dogs);
			aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			sp.setAdapter(aa);
			if( selected >= 0 && selected < sp.getCount())
				sp.setSelection(selected);
		} else
			findViewById(R.id.trDog).setVisibility(View.GONE);
	}
	
	@Override
	protected int getSalesLayoutId() { return R.layout.createsalesex; }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
			default:
				return super.onCreateDialog(id);
		}
	}
	
	@Override
	protected void postOkDone(Sales sales) {
		sales.date = dateHandler.getDate();
		Spinner sp = (Spinner)findViewById(R.id.spDog);
		OrgDogovor dog = (OrgDogovor) sp.getSelectedItem();
		((SalesEx)sales).dogId = (dog == null) ? "" : dog.id;
	}
}
