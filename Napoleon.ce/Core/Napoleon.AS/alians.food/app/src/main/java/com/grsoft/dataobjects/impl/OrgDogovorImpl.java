package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.HashSet;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.napoleon.R;

public class OrgDogovorImpl extends DbObject<OrgDogovor> {
	
	static void loadDogovors(ArrayList<OrgDogovor> dog, HashSet<String> firms, String ido) {
		OrgDogovor dg = new OrgDogovor();
		String table = DataObjectInfo.getInstance().getTableName(dg.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(dg, table, "ido='" + ido + "'");
		while(bdo) {
			firms.add(dg.supplyercode);
			dog.add(dg);
			dg = new OrgDogovor();
			bdo = r.selectNext(dg);
		}
		r.close();
	}
	
	public static void loadFirms(Spinner spFirma, final Spinner spDog, String orgIdo, String firmId, String dogId) {
		ArrayList<FirmEx> firms = new ArrayList<FirmEx>();
		final ArrayList<OrgDogovor> dogovors = new ArrayList<OrgDogovor>();
		HashSet<String> orgFirms = new HashSet<String>();
		
		loadDogovors(dogovors, orgFirms, orgIdo); 
		
		FirmEx firm = new FirmEx();
		String table = DataObjectInfo.getInstance().getTableName(Firm.class); 
		DbReader r = new DbReader();
		int selected = -1;
		boolean bdo = r.select(firm, table, null, "name");
		while(bdo) {
			if( orgFirms.contains(firm.id) ) {
				if(firmId.equals(firm.id))
					selected = firms.size();
				firms.add(firm);
			}
			firm = new FirmEx();
			bdo = r.selectNext(firm);
		}
		r.close();
		
		ArrayAdapter<FirmEx> aa = new ArrayAdapter<FirmEx>(spFirma.getContext(), R.layout.simple_spinner_layout, firms);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spFirma.setAdapter(aa);
		if( selected >= 0 )
			spFirma.setSelection(selected);
		
		loadDogovors(spDog, dogovors, firmId, dogId);
		
		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onNothingSelected(AdapterView<?> arg0) { }
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FirmEx f = (FirmEx) arg0.getSelectedItem();
				loadDogovors(spDog, dogovors, f.id, null);
			}
		});
	}
	
	public static void loadDogovors(Spinner sp, ArrayList<OrgDogovor> dogovors, String firmId, String selected) {
		int sel = -1;
		ArrayList<OrgDogovor> values = new ArrayList<OrgDogovor>();
		for(OrgDogovor dg : dogovors) {
			if( dg.supplyercode.equals(firmId) != true )
				continue;
			if( selected != null && dg.idDog.equals(selected) )
				sel = values.size();
			
			values.add(dg);
		}
	
		ArrayAdapter<OrgDogovor> aa = new ArrayAdapter<OrgDogovor>(sp.getContext(), R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( sel >= 0 && sel < sp.getCount())
			sp.setSelection(sel);
		
	}
}
