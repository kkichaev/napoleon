package com.grsoft.napoleon;

import java.util.ArrayList;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgUnitable;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;


public class OrgUnitHelper {
	public static ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	
	public static void initUnits(Spinner s, final OrgUnitable u, OrgEx org) {
		if( org.units == null )
			return;
		
		units.clear();
		
		int selected = -1;
		for(UnitItem ui : org.units ) {
			if( ui.id.compareTo(u.getCode()) == 0 )
				selected = units.size();

			units.add(new UnitEx(ui));
		}
		
		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(s.getContext(), R.layout.simple_spinner_layout, units);		
		s.setAdapter(adapter);
		if( selected >= 0 )
			s.setSelection(selected);

		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				UnitEx ut = units.get(pos);
				u.setCode(ut.id);
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	
	public static void initDoc(OrgUnitable u, OrgEx o){
		if( o.units != null && o.units.size() > 0 )
			u.setCode(o.units.get(0).id); 
	}
}
