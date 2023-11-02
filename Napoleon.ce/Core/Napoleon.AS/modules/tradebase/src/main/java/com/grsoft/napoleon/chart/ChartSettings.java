package com.grsoft.napoleon.chart;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Spinner;

public class ChartSettings extends DialogFragment implements OnClickListener {
	private CfgNpl config;
	private Spinner spPeriod;
	private Spinner spAKB;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		config = (CfgNpl) ConfigManager.getConfig();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.report_setting);
		View result = inflater.inflate(R.layout.chart_setiings, container, false);
		
		spPeriod = (Spinner) result.findViewById(R.id.spPeriod);
		spAKB = (Spinner) result.findViewById(R.id.spAKB);
		
		result.findViewById(R.id.btnOK).setOnClickListener(this);
		result.findViewById(R.id.btnCancel).setOnClickListener(this);
		
		spPeriod.setSelection(config.chartPeriod, true);
		spAKB.setSelection(config.chartAKB, true);
		
		return result;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK)
			saveSetting();
		
		dismiss();
		
	}

	private void saveSetting() {
		config.chartPeriod = spPeriod.getSelectedItemPosition();
		config.chartAKB = spAKB.getSelectedItemPosition();
	}
}
