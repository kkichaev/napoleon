/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   12/04/2011   creating
 */
package com.grsoft.napoleon;

import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.util.Consts;
import com.grsoft.util.MessageBox;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.InputNumberHelper;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Дилог для ввода числа
 * @author kki
 *
 */
public class InputNumberDlgEx {

	private static int scale = Consts.QTY_SCALE;
	private static boolean hideRest = true;
	
	public static Decorator decorator = new InputNumberDlgDecor() {
		public int getContentView() {
			return R.layout.inputnumberdlgex;
		};
	};

	public static void open(final Context context, final InputNumberEx inputNumber) { 
		open(context, inputNumber, Consts.QTY_SCALE, true, context.getString(R.string.input_value), false);
	}
	
	public static void open(final Context context, final InputNumberEx inputNumber, int iscale, boolean ihideRest, String title){
		open(context, inputNumber, iscale, ihideRest, title, false);
	}
	
	public static void open(final Context context, final InputNumberEx inputNumber, int iscale, boolean ihideRest, String title, boolean useInPack){
		open(context, inputNumber, iscale, ihideRest, title, useInPack, decorator);
	}

	public static void open(final Context context, final InputNumberEx inputNumber, int iscale, boolean ihideRest, 
			String title, boolean useInPack, Decorator dec){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		final View panel = View.inflate(context, dec.getContentView(), null);
		
		final EditText edCount = (EditText) panel.findViewById(R.id.edCount);
		final EditText edCost = (EditText) panel.findViewById(R.id.edCost);

		
		final CheckBox cbInPack = (CheckBox) panel.findViewById(R.id.cbPack);
		if( useInPack ) {
			cbInPack.setVisibility(View.VISIBLE);
			cbInPack.setChecked(inputNumber.isInpack());
			cbInPack.setEnabled(inputNumber.isPackCanChange());
		} else
			cbInPack.setVisibility(View.GONE);
		
		scale = iscale;
		hideRest = ihideRest;
		int[] values = inputNumber.getValues();
		
		edCount.setText(Util.IntToScaleStr(values[0], scale, Util.DEC_DELIM, hideRest));
		edCount.setInputType(InputType.TYPE_NULL);
		edCount.requestFocus();
		edCount.selectAll();
		
		edCost.setText(Util.IntToScaleStr(values[1], Consts.SUM_SCALE, Util.DEC_DELIM, hideRest));
		edCost.setInputType(InputType.TYPE_NULL);
		
		KeypadHelper kh = new KeypadHelper(panel, R.id.edCount);
//		InputNumberHelper nh = new InputNumberHelper((EditText)panel.findViewById(R.id.edCount));
//		nh.makeNumericKeypad(panel);
		
		ImageButton btnDel = (ImageButton) panel.findViewById(R.id.btnDel);
		btnDel.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				edCount.setText("");
				return false;
			}
		});
		
		View btnComma = panel.findViewById(R.id.btnComma); 
		if( inputNumber.replaceCommaToPlus() ) {
			((ImageButton)btnComma).setImageResource(R.drawable.kp1);
			btnComma.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EditText edCount = (EditText) panel.findViewById(R.id.edCount);
					try{
						int value = Util.StrToScale(edCount.getText().toString(), scale);
						value += scale;
						edCount.setText(Util.IntToScaleStr(value, scale, Util.DEC_DELIM, hideRest));
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else
			btnComma.setEnabled(inputNumber.useComma());
		
		builder.setView(panel);
		final AlertDialog dialog = builder.create();
				
		ImageButton btnOK = (ImageButton) panel.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListenerToNotify() {
			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				EditText edCount = (EditText) panel.findViewById(R.id.edCount);
				try{
					String sval = edCount.getText().toString();
					inputNumber.setEditValue(sval);
					int value = Util.StrToScale(sval, scale);
					if( inputNumber.isValid(value, cbInPack.isChecked(), dialog)) {
						
						int[] arr = new int[]{
								Util.StrToScale(edCount.getText().toString(), scale),
								Util.StrToScale(edCost.getText().toString(), Consts.SUM_SCALE)
						};

						
						inputNumber.applayInput(arr, cbInPack.isChecked(), dialog);
						dialog.dismiss();
					}
				}
				catch(Exception e){
					edCount.selectAll();
					String message = e.getMessage();
					if( message == null )
						message = context.getString(R.string.check_input);
					MessageBox.show(context, context.getString(R.string.error), message);
				}
			}
		});
		
		edCost.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					InputNumberHelper nh = new InputNumberHelper(
							(EditText) v);
					nh.makeNumericKeypad(panel);
				}
			}
		});

		edCount.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							InputNumberHelper nh = new InputNumberHelper(
									(EditText) v);
							nh.makeNumericKeypad(panel);
						}
					}
				});
		
		dec.adjustView(dialog, panel, kh);

		dialog.show();
	}
	
	class DeleteAll extends OnClickListenerToNotify{ }
}
