package com.grsoft.napoleon.modules;

import java.util.ArrayList;
import java.util.Collections;

import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CostHelper {
	public interface CostSelector {
		public void selectedCost(CostManager.CostType costType, int index);
	}
	
	public static void loadCostTypes(Spinner sp, String selectedCost, final CostSelector selector) {
		CostTypeEx selected = null;
		final ArrayList<CostTypeEx> costTypes = new ArrayList<CostTypeEx>();
		CostManager.CostType[] ctypes = Features.COST_MANAGER.getCostTypes();
		if( ctypes != null ) {
			int index = 0;
			for( CostManager.CostType ct : ctypes ) {
				CostTypeEx ctx = new CostTypeEx(index++, ct);
				costTypes.add(ctx);
				if( selectedCost != null && ct.id.compareTo(selectedCost) == 0 )
					selected = ctx;
			}
		}

		Collections.sort(costTypes);

		ArrayAdapter<CostTypeEx> adapter = new ArrayAdapter<CostTypeEx>(sp.getContext(), R.layout.simple_spinner_layout, costTypes);
		sp.setAdapter(adapter);
		if( selected != null ) {
			int selIndex = costTypes.indexOf(selected);
			sp.setSelection(selIndex);
		}
		
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if( selector != null ) {
					CostTypeEx ct = costTypes.get(position);
					selector.selectedCost(ct, ct.costIndex);
				}
			}
		});
	}
}

class CostTypeEx extends CostManager.CostType implements Comparable<CostTypeEx> {
	int costIndex;
	
	public CostTypeEx(int index, CostManager.CostType c) { 
		super(c.id, c.name);
		costIndex = index;
	}
	
	@Override public String toString() { return name ; }

	@Override
	public int compareTo(CostTypeEx another) {
		return name.compareTo(another.name);
	}
}
