package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.InputNumberHelper;

public class DiscountInputDlg {
	public static class Helper{
		public int getLayoutId(){
			return R.layout.discount_input;
		}
		
		public void adjustView(View view){}
	}
	
	public enum Type { OnlyDiscount, OnlyNac, Both }
	
	public static void open(final Context context, final InputNumber inputNumber){
		open(context, inputNumber, Consts.SUM_SCALE, false, context.getString(R.string.cost_changing));
	}
	
	public static void open(final Context context, final InputNumber inputNumber, final int scale, boolean hideRest, String title){
		open(context, inputNumber, scale, hideRest, title, Type.Both);
	}
	
	public static void open(final Context context, final InputNumber inputNumber, final int scale, boolean hideRest, String title, Type type){
		open(context, inputNumber, scale, hideRest, title, type, new Helper());
	}
	
	private static String getCostText(int priceCost, long value, int scale) {
		long newCost = CostStrategy.costWithDiscount(priceCost, value, scale);
		return "цена " + Util.IntToScaleStr(newCost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
	}
	
	public static void open(final Context context, final InputNumber inputNumber, final int scale, 
			boolean hideRest, String title, Type type, Helper helper){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		final View panel = View.inflate(context, helper.getLayoutId(), null);
		
		final EditText edCount = (EditText) panel.findViewById(R.id.edCount);
		long value = inputNumber.getValue();
		
		final Spinner sp = (Spinner)panel.findViewById(R.id.spDscType);
		if( type != Type.Both ) {
			sp.setVisibility(View.GONE);
			sp.setSelection((type == Type.OnlyDiscount) ? 0 : 1);
		} else
			sp.setSelection((value<=0) ? 0 : 1);
		value = Math.abs(value);
		edCount.setText(Util.IntToScaleStr(value, scale, Util.DEC_DELIM, hideRest));
		edCount.setInputType(InputType.TYPE_NULL);
		
		if(inputNumber.priceCost != 0) {
			final TextView dscView = (TextView) panel.findViewById(R.id.tvDiscountInfo);
			dscView.setVisibility(View.VISIBLE);
			dscView.setText(getCostText(inputNumber.priceCost, inputNumber.getValue(), scale));
			edCount.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					int newdsc = (int) Util.StrToScale(arg0.toString(), scale);
					if(sp.getSelectedItemPosition() == 1)
						newdsc= -newdsc;
					String dscText = getCostText(inputNumber.priceCost, newdsc, scale);
					dscView.setText(dscText);
				}
				
				@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
				@Override public void afterTextChanged(Editable arg0) {}
			});
			
			sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					int newdsc = (int)Util.StrToScale(edCount.getText().toString(), scale);
					if(arg2 == 1)
						newdsc= -newdsc;
					String dscText = getCostText(inputNumber.priceCost, newdsc, scale);
					dscView.setText(dscText);
				}

				@Override public void onNothingSelected(AdapterView<?> arg0) {}
			});
		}
				
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
					int value = (int)Util.StrToScale(edCount.getText().toString(), scale);
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
						message = context.getString(R.string.check_for_valid_number);
					MessageBox.show(context, context.getString(R.string.error), message);
				}
				
			}
		});
		
		helper.adjustView(panel);
		dialog.show();
	}
}
