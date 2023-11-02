package com.grsoft.prch_order;

import com.grsoft.prch_order.dataobjects.ConfigHelper;
import com.grsoft.prch_order.dataobjects.Gate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class GateBuildEdit extends EditPage {

	@Override public String getTitle() { return "Ворота"; }

	@Override
	public void write(Gate g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.gate_build, container, false);
				
		int[] ids = new int[] {
			R.id.spGateType, R.id.spGateColor, R.id.spDriveUnit, R.id.spDriveType,
		};
		
		String[] keys = new String[] {
			ConfigHelper.GATE_TYPE,	ConfigHelper.GATE_COLOR, ConfigHelper.DRIVE_UNIT, ConfigHelper.DRIVE_TYPE 
		};
		
		String[] fields = new String[] {
			"type", "color", "drive", "climbType",
		};
		setSpinners(v, ids, fields, keys);
		
		
		ids = new int[] {
			R.id.edWindows, R.id.edWidth, R.id.edUp, R.id.edHeight, R.id.edUnderGate, R.id.edMaterial,
		};
		fields = new String[] {
			"window", "width", "up", "height", "nearWall", "climbMaterial",
		};
		setEditText(v, ids, fields);
		
		CheckBox cb = (CheckBox)v.findViewById(R.id.cbGate);
		cb.setChecked(gate.gate > 0);
		
		return v;
	}
	
}
