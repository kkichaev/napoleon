/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   12/04/2011   creating
 */
package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

/**
 * Дилог для ввода числа
 * @author kki
 *
 */
public class InputNumberDlg {

	private static int scale = Consts.QTY_SCALE;
	private static boolean hideRest = true;
	
	public static Decorator decorator = new InputNumberDlgDecor();
	
	public interface Decorator{
		int getContentView();
		public void adjustView(AlertDialog dialog, View view, KeypadHelper nh);
	}

	public static void open(final Context context, final InputNumber inputNumber) { 
		open(context, inputNumber, Consts.QTY_SCALE, true, context.getString(R.string.input_value), false);
	}
	
	public static void open(final Context context, final InputNumber inputNumber, int iscale, boolean ihideRest, String title){
		open(context, inputNumber, iscale, ihideRest, title, false);
	}
	
	public static void open(final Context context, final InputNumber inputNumber, int iscale, boolean ihideRest, String title, boolean useInPack){
		open(context, inputNumber, iscale, ihideRest, title, useInPack, decorator);
	}

	static String getDiscountText(int priceCost, int newCost) {
		String dscText = "";
		
		int dsc = 100 * Consts.SUM_SCALE - (int)(((float)newCost/(float)priceCost) * Consts.SUM_SCALE * 100 );
		
		if(dsc >= 0) {
			dscText = "скидка ";
		} else {
			dscText = "наценка ";
			dsc = -dsc;
		}
		dscText += Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " %";
		return dscText;
	}
	
	public static void open(final Context context, final InputNumber inputNumber, int iscale, boolean ihideRest, 
			String title, boolean useInPack, Decorator dec){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		final View panel = View.inflate(context, dec.getContentView(), null);
				
		final EditText edCount = (EditText) panel.findViewById(R.id.edCount);
		
		final CheckBox cbInPack = (CheckBox) panel.findViewById(R.id.cbPack);
		if( useInPack ) {
			cbInPack.setVisibility(View.VISIBLE);
			cbInPack.setChecked(inputNumber.isInpack());
			cbInPack.setEnabled(inputNumber.isPackCanChange());
		} else
			cbInPack.setVisibility(View.GONE);
		
		scale = iscale;
		hideRest = ihideRest;
		edCount.setText(Util.IntToScaleStr(inputNumber.getValue(), scale, Util.DEC_DELIM, hideRest));
		edCount.setInputType(InputType.TYPE_NULL);
		edCount.requestFocus();
		edCount.selectAll();

		if(inputNumber.priceCost != 0) {
			final TextView dscView = (TextView) panel.findViewById(R.id.tvDiscountInfo);
			dscView.setVisibility(View.VISIBLE);
			dscView.setText(getDiscountText(inputNumber.priceCost, inputNumber.getValue()));
			edCount.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					int newCost = Util.StrToScale(arg0.toString(), scale);
					String dscText = getDiscountText(inputNumber.priceCost, newCost);
					dscView.setText(dscText);
				}
				
				@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
				@Override public void afterTextChanged(Editable arg0) {}
			});
		}
		
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
				applyInput(inputNumber, panel, dialog);
			}
		});
		
		dec.adjustView(dialog, panel, kh);

		edCount.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent event) {
				int kc = event.getKeyCode();
				if(event.getAction() == KeyEvent.ACTION_DOWN && 
						(kc == KeyEvent.KEYCODE_ENTER || kc == KeyEvent.KEYCODE_DPAD_CENTER || kc == 160)) {
					applyInput(inputNumber, panel, dialog);
					return true;
				}
				return false;
			}
		});
		
		dialog.show();
	}
	
	protected static void applyInput(final InputNumber inputNumber, final View panel, final AlertDialog dialog) {
		EditText edCount = (EditText) panel.findViewById(R.id.edCount);
		CheckBox cbPack = (CheckBox) panel.findViewById(R.id.cbPack);
		try{
			String sval = edCount.getText().toString();
			inputNumber.setEditValue(sval);
			int value = Util.StrToScale(sval, scale);
			if( inputNumber.isValid(value, cbPack.isChecked(), dialog)) {
				inputNumber.applayInput(value, cbPack.isChecked(), dialog);
				dialog.dismiss();
			}
		}
		catch(Exception e){
			edCount.selectAll();
			String message = e.getMessage();
			if( message == null )
				message = panel.getContext().getString(R.string.check_input);
			MessageBox.show(panel.getContext(), panel.getContext().getString(R.string.error), message);
		}
	}	
	
	class DeleteAll extends OnClickListenerToNotify{ }
}

class InputNumberDlgDecor implements Decorator{

	@Override
	public int getContentView() {
		return R.layout.inputnumberdlg;
	}

	@Override
	public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {}
}
