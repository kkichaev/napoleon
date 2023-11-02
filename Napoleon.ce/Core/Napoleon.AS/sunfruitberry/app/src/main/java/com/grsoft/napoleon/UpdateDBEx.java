package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	
	static String MAKE_DEBT_TAG = "MAKE_DEBT_TAG";
	boolean makeDebtSync;
	
	public static void makeDebtSync(Context context) {
		Intent i = new Intent(context, UpdateDBEx.class);
		i.putExtra(MAKE_DEBT_TAG, true);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();
		if(b != null) {
			makeDebtSync = b.getBoolean(MAKE_DEBT_TAG, false);
			if(makeDebtSync) {
				((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
				((CheckBox) findViewById(R.id.cbGenData)).setChecked(false);
				((CheckBox) findViewById(R.id.cbDocs)).setChecked(false);
				((CheckBox) findViewById(R.id.cbVisit)).setChecked(false);
				findViewById(R.id.btnUpdate).performClick();
			}
		}
	}
	
	@Override
	protected void postSync(Boolean result) {
		if(result && makeDebtSync)
			finish();
	}
}
