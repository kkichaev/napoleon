package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgUnitable;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.napoleon.modules.CostManager;


public class CreateReturnEx extends CreateReturn {
//	private ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	ArrayList<CostTypeEx> costTypes = new ArrayList<CostTypeEx>();
	
//	private void initUnits(ReturnEx r, OrgEx org) {
//		if( org.units == null )
//			return;
//		
//		int selected = -1;
//		for(UnitItem ui : org.units ) {
//			if( ui.id.compareTo(r.unitCode) == 0 )
//				selected = units.size();
//
//			units.add(new UnitEx(ui));
//		}
//		
//		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, units);		
//		Spinner s = (Spinner)findViewById(R.id.spUnits);
//		s.setAdapter(adapter);
//		if( selected >= 0 )
//			s.setSelection(selected);
//
//		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
//				UnitEx ut = units.get(pos);
//				ReturnEx rex = (ReturnEx)doc.getData();
//				rex.unitCode = ut.id;
//			}
//			@Override public void onNothingSelected(AdapterView<?> arg0) {}
//		});
//	}
	
	@Override
	protected void init(Return r, Org data) {
		super.init(r, data);
		OrgEx org = (OrgEx)data;
		
		CostManager.CostType[] ctypes = Features.COST_MANAGER.getCostTypes();
		if( ctypes != null ) {
			int sumIndex = 0;
			if( org.costType != null && org.costType.length() > 0 )
				sumIndex = Features.COST_MANAGER.getCostIndex(org.costType);
			
			if( sumIndex < 0 )
				sumIndex = 0;
			r.sumType = sumIndex;
			((ReturnEx)r).costType = ctypes[sumIndex].id;
		}
		
		OrgUnitHelper.initDoc((OrgUnitable)r, (OrgEx)data);
//		if( org.units != null && org.units.size() > 0 )
//			((ReturnEx)r).unitCode = org.units.get(0).id;
	}
	
	@Override
	protected void initView() {
		ReturnEx r = (ReturnEx)doc.getData(); 
		//initUnits(r, (OrgEx)oi.getData());
		OrgUnitHelper.initUnits((Spinner)findViewById(R.id.spUnits), (OrgUnitable)r, (OrgEx)oi.getData());
	}
	
	@Override
	int getContentViewID() { return R.layout.createreturnex;}
	
	@Override
	protected void initCost(Return r) {
		ReturnEx rex = (ReturnEx)r;
		CostTypeEx selected = null;

		CostManager.CostType[] ctypes = Features.COST_MANAGER.getCostTypes();
		if( ctypes != null ) {
			int index = 0;
			for( CostManager.CostType ct : ctypes ) {
				CostTypeEx ctx = new CostTypeEx(index++, ct);
				costTypes.add(ctx);
				if( ct.id.compareTo(rex.costType) == 0 )
					selected = ctx;
			}
		}

		Collections.sort(costTypes, new Comparator<CostTypeEx>() {
			@Override public int compare(CostTypeEx object1, CostTypeEx object2) { return object1.name.compareTo(object2.name); }
		});

		ArrayAdapter<CostTypeEx> adapter = new ArrayAdapter<CostTypeEx>(this, R.layout.simple_spinner_layout, costTypes);		
		Spinner s = (Spinner)findViewById(R.id.spPrices);
		s.setAdapter(adapter);
		if( selected != null ) {
			int selIndex = costTypes.indexOf(selected);
			s.setSelection(selIndex);
		}
		
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				CostTypeEx ct = costTypes.get(pos);
				
				ReturnEx rex = (ReturnEx)doc.getData();
				rex.costType = ct.id;
				rex.sumType = ct.costIndex;
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
}
