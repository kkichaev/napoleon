package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import com.android.calculator2.Calculator;
import com.grsoft.view.KeypadHelper;

public class InputNumberDlgDecorEx implements InputNumberDlg.Decorator{

	@Override
	public int getContentView() {
		return R.layout.inputnumberdlgex;
	}

	@Override
	public void adjustView(final AlertDialog dialog, View view, KeypadHelper nh) {
		ImageButton btnCalc = (ImageButton) view.findViewById(R.id.btnCalc);
		final EditText edCount = (EditText) view.findViewById(R.id.edCount);
		
		final BroadcastReceiver calcResult = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null){
					if (edCount != null)
						edCount.setText(intent.getStringExtra(
							Calculator.CALCULATOR_RESULT_VALUE));
				}
			}
		};
		
		if (btnCalc != null){
			btnCalc.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (edCount != null){
						Intent data = new Intent(v.getContext(), Calculator.class);
						data.putExtra(Calculator.START_CALC_VAL, edCount.getText().toString());
						data.putExtra(Calculator.BROADCAST_RESULT, true);
						v.getContext().startActivity(data);
					}
				}
			});
		}
		
		dialog.getContext().registerReceiver(calcResult, 
				new IntentFilter(Calculator.CALCULATOR_RESULT_ACTION));
		
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				dialog.getContext().unregisterReceiver(calcResult);
			}
		}); 
		
		
	}

}
