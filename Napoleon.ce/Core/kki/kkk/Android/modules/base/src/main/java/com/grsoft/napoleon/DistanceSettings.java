package com.grsoft.napoleon;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

public class DistanceSettings extends DialogFragment implements OnClickListener {
	private CfgNpl config;
	private Spinner spStart;
	private Spinner spEnd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		config = (CfgNpl) ConfigManager.getConfig();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.report_setting);
		View result = inflater.inflate(R.layout.distance_seting, container, false);

		spStart = (Spinner) result.findViewById(R.id.spStart);
		spEnd = (Spinner) result.findViewById(R.id.spEnd);
		
		result.findViewById(R.id.btnOK).setOnClickListener(this);
		result.findViewById(R.id.btnCancel).setOnClickListener(this);
		
		spStart.setSelection(config.distance_start, true);
		spEnd.setSelection(config.distance_end, true);
		
		return result;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK)
			saveSetting();
		
		dismiss();
		
	}

	private void saveSetting() {
		config.chartPeriod = spStart.getSelectedItemPosition();
		config.chartAKB = spEnd.getSelectedItemPosition();
	}
}
