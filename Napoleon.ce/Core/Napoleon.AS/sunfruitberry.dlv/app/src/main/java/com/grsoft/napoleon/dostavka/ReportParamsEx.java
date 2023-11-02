package com.grsoft.napoleon.dostavka;

import com.grsoft.napoleon.ReportParams;

import android.os.Bundle;
import android.view.View;

public class ReportParamsEx extends ReportParams {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.trOrg).setVisibility(View.GONE);
	}
}
