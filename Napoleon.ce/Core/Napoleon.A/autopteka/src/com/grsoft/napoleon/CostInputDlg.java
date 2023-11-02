package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.InputNumberHelper;

public class CostInputDlg {
	private static int scale = Consts.SUM_SCALE;
	private static boolean hideRest = false;
	private static int stopCost;

	public static void open(final Context context, final InputNumber inputNumber, int vMinCost, int vStopCost){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Цена");
		final View panel = View.inflate(context, R.layout.costinput, null);
		
		final EditText edCount = (EditText) panel.findViewById(R.id.edCount);

		stopCost = vStopCost;
		TextView tv;
		tv = (TextView)panel.findViewById(R.id.tvMinCost);
		tv.setText("р.цена: " + Util.IntToScaleStr(vMinCost, scale, Util.DEC_DELIM, hideRest));
		
		tv = (TextView)panel.findViewById(R.id.tvStopCost);
		tv.setText("cтоп цена: " + Util.IntToScaleStr(vStopCost, scale, Util.DEC_DELIM, hideRest));
		
		edCount.setText(Util.IntToScaleStr(inputNumber.getValue(), scale, Util.DEC_DELIM, hideRest));
		
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
					if( value < stopCost ) {
						MessageBox.show(context, "Ошибка", "Цена меньше минимальной на товар!");
					} else {
						inputNumber.applayInput(value);
						dialog.dismiss();
					}
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
