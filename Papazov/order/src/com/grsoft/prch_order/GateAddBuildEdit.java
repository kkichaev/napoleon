package com.grsoft.prch_order;

import com.grsoft.prch_order.dataobjects.ConfigHelper;
import com.grsoft.prch_order.dataobjects.Gate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class GateAddBuildEdit extends EditPage {

	@Override public String getTitle() { return "Монтаж"; }

	@Override
	public void write(Gate g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.add_build, container, false);
		
		int[] ids = new int[] {
			R.id.spTubeSize, R.id.spTubeColor,
		};
			
		String[] keys = new String[] {
			ConfigHelper.TUBE_CUT_SIZE,	ConfigHelper.TUBE_COLOR, 
		};
		
		String[] fields = new String[] {
			"tubeCut", "tubeColor",
		};
		
		setSpinners(v, ids, fields, keys);

		ids = new int[] {
			R.id.edAddComplect, R.id.edTubeLength, R.id.edAddWork, R.id.edBuilderComment, R.id.edBuildDuration, R.id.edCustRemark,
		};
		fields = new String[] {
			"addComplect", "tubeLength", "addWork", "comment", "installTime", "commentCustomer",
		};
		setEditText(v, ids, fields);
		
		CheckBox cb = (CheckBox)v.findViewById(R.id.cbBuildGate);
		cb.setChecked(gate.buildClimb > 0);
		
		cb = (CheckBox)v.findViewById(R.id.cbNotReady);
		cb.setChecked(gate.notReady > 0);
		
		return v;
	}
	
}
