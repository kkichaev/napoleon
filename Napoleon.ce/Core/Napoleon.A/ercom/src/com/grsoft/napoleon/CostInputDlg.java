package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.InputNumberHelper;

public class CostInputDlg {
	private static int scale = Consts.SUM_SCALE;
	private static boolean hideRest = false;

	public static void open(final Context context, final InputNumber inputNumber){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Изменение цены");
		final View panel = View.inflate(context, R.layout.costinput, null);
		
		final EditText edCount = (EditText) panel.findViewById(R.id.edCount);
		int value = inputNumber.getValue();
		
		Spinner sp = (Spinner)panel.findViewById(R.id.spDscType);
		sp.setSelection((value<=0) ? 0 : 1);
		value = Math.abs(value);
		edCount.setText(Util.IntToScaleStr(value, scale, Util.DEC_DELIM, hideRest));
		
		InputNumberHelper nh = new InputNumberHelper((EditText)panel.findViewById(R.id.edCount));
		nh.makeNumericKeypad(panel);
		
		ImageButton btnDel = (ImageButton) panel.findViewById(R.id.btnDel);
		btnDel.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				edCount.setText("");
				return false;
			}
		});
		
		builder.setView(panel);
		final AlertDialog dialog = builder.create();
				
		ImageButton btnOK = (ImageButton) panel.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListenerToNotify() {
			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				EditText edCount = (EditText) panel.findViewById(R.id.edCount);
				try{
					int value = Util.StrToScale(edCount.getText().toString(), scale);
					Spinner sp = (Spinner)panel.findViewById(R.id.spDscType);
					if( sp.getSelectedItemPosition() == 0 )
						value = -value;
					
					inputNumber.applayInput(value);
					dialog.dismiss();
				}
				catch(Exception e){
					edCount.selectAll();
					String message = e.getMessage();
					if( message == null )
						message = "Проверьте правильность ввода числа";
					MessageBox.show(context, "Ошибка", message);
				}
				
			}
		});
		
		dialog.show();
	}

}
