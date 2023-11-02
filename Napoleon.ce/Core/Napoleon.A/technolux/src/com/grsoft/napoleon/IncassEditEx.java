package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class IncassEditEx extends IncassEdit {
		
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		IncassEx ie  = (IncassEx)doc.getData();
		
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx)oi.getData();
		org.id = doc.getId();
		oi.read();
		oi.close();

		int selected = -1;
		ArrayList<OrgDog> dogs = new ArrayList<OrgDog>();
		for(OrgDog dog : org.dogovors ) {
			if( dog.id.equals( ie.dogovor ))
				selected = dogs.size();
			dogs.add(dog);
		}

		ArrayAdapter<OrgDog> da = new ArrayAdapter<OrgDog>(this, R.layout.simple_spinner_layout, dogs);
		Spinner spDog = (Spinner)findViewById(R.id.spDogovor);
		spDog.setAdapter(da);
		if(selected >= 0)
			spDog.setSelection(selected);
	
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		Spinner spDog = (Spinner)findViewById(R.id.spDogovor);
		
		IncassEx ie  = (IncassEx)doc.getData();
		OrgDog dog = (OrgDog) spDog.getSelectedItem();
		if( dog != null)
			ie.dogovor = dog.id;
	}
}
