package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	int dsc;
	int minCost;
	int priceCost;
	
	EditText edQty1;
	EditText edQty2;
	CheckBox cbPack1;
	CheckBox cbPack2;
	TextView tvQty1;
	TextView tvQty2;
	View actionLayout;
	View baseLayout;
	
	Boolean canChangeCost = null;
	String agentmincost = "";
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean canChangeCost() {
		if(!(document != null && document instanceof OrderImplBase<?>) )
			return false;
		
		if(canChangeCost == null) {
			canChangeCost = false;
	        ConfigImpl config = new ConfigImpl();
			config.getData().key = "МожноИзменятьЦену";
			try {
				if (config.read() && Integer.parseInt(config.getData().value) == 1)
					canChangeCost = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			config.close();
		}
		return canChangeCost;
	}
	
	View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			if( arg1 ) {
				keypadHelper.setTargetID(arg0.getId());
				((EditText)arg0).selectAll();
			}
		}
	};
	
	protected long getSumValue() {
		String text = edQty1.getText().toString();
		int count =  text.length() == 0 ? 0 : (int)Util.StrToScale(text, Consts.QTY_SCALE);
		qtyItems = count;
		
		if(cbPack1.isChecked())
			count = (int)FPOperation.itemMul(count, qtyInPack, Consts.QTY_SCALE);
		
		long sumItems = ((long)getInputCost(price.getData()) * count + Consts.QTY_SCALE/2) / Consts.QTY_SCALE;
		
		text = edQty2.getText().toString();
		count =  text.length() == 0 ? 0 : (int) Util.StrToScale(text, Consts.QTY_SCALE);
		qtyItems += count;
		
		if(cbPack2.isChecked())
			count = (int)FPOperation.itemMul(count, qtyInPack, Consts.QTY_SCALE);
		
		sumItems += ((long)getInputCost(price.getData()) * count + Consts.QTY_SCALE/2) / Consts.QTY_SCALE;

		
		return sumItems;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		edQty1 = (EditText) findViewById(R.id.edQty1);
		edQty2 = (EditText) findViewById(R.id.edQty2);
		cbPack1 = (CheckBox) findViewById(R.id.cbPack1);
		cbPack2 = (CheckBox) findViewById(R.id.cbPack2);
		tvQty1 = (TextView) findViewById(R.id.tvQty1);
		tvQty2 = (TextView) findViewById(R.id.tvQty2);
		actionLayout = findViewById(R.id.actionLayout);
		baseLayout = findViewById(R.id.baseLayout);
		
		edQty1.setInputType(InputType.TYPE_NULL);
		edQty2.setInputType(InputType.TYPE_NULL);
		
		edQty1.setOnFocusChangeListener(focusListener);
		edQty2.setOnFocusChangeListener(focusListener);
		
		TextWatcher tw = new CountTextWatcher();
		edQty1.addTextChangedListener(tw);
		edQty2.addTextChangedListener(tw);
		
		OnCheckedChangeListener cc = createPacketChangeListener();
		cbPack1.setOnCheckedChangeListener(cc);
		cbPack2.setOnCheckedChangeListener(cc);
		
		if( canChangeCost() ) {
			findViewById(R.id.trDiscount).setVisibility(View.VISIBLE);
			TextView tv = (TextView)findViewById(R.id.tvPrice);
			updateCost();
	
			PriceEx p = (PriceEx) price.getData();
			minCost = p.minCost;
	
			int sumType = document != null ? document.getSumType() : 0;
			priceCost = (p.cost.size() > sumType && sumType >= 0) ? 
					p.cost.get(sumType).cost : 0;			
	
			if(priceCost != 0)
				dsc = (int)(1000 - (long)priceVal * 1000 / priceCost);
			else
				dsc = 0;
			
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					CostInputDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
						@Override public long getValue() { return priceVal; }
					}, minCost); 
				}
			});
			
			
			tv = (TextView)findViewById(R.id.tvDiscount);
			updateNac();
			
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public void applayInput(int value, Object... params) { onNacChange(value); }
						@Override public long getValue() { return dsc; }
					}, 10, false, "Наценка"); 
				}
			});
		}
		
		tvQty1.setText(Util.IntToScaleStr(price.getData().qty, Consts.QTY_SCALE));
		tvQty2.setText("0");
		
		if (((PriceEx)price.getData()).whQty.size() > 0)
			tvQty2.setText(Util.IntToScaleStr(((PriceEx)price.getData()).whQty.get(0).qty, Consts.QTY_SCALE));
		
		OrderImplEx ord = (OrderImplEx) document;
		
		if (document != null) {
			edQty1.setText(Util.IntToScaleStr(ord.getItemQty(price.getData(), 0), Consts.QTY_SCALE));
			edQty2.setText(Util.IntToScaleStr(ord.getItemQty(price.getData(), 1), Consts.QTY_SCALE));
			
			OrgImpl org = new OrgImpl();
			org.read("id", document.getData().id);
	 		actionLayout.setVisibility(((OrgEx)org.getData()).whCount > 0 ? View.VISIBLE : View.GONE);
		}else {
			actionLayout.setVisibility(View.GONE);
			baseLayout.setVisibility(View.GONE);
		}
	}

	void updateNac() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(dsc, 10, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	@Override
	protected void onChangeCost( int newCost ) {
		priceVal = newCost;
		Price p = price.getData();

		if( p.cost.get(0).cost != 0 )
			dsc = (int)(1000 - (long)priceVal * 1000 / priceCost);

		updateCost();
		updateSumTextView();
		updateNac();
	}
	
	void onNacChange( int newNac ) {
		dsc = newNac;
		priceVal = (int)(((long)priceCost * (1000 - dsc)) / 1000);
		
		updateCost();
		updateSumTextView();
		updateNac();
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		
		ConfigImpl cf = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		if (cf.getValue(sb, "MinCost")){
			String v = sb.toString().trim();
			
			if (v.length() > 0) {
				agentmincost = v;
				int i = (int)Util.StrToScale(v, Consts.SUM_SCALE);

				if (getSumValue() < i)
					result = false;
			}
		}
		
		return result;
	}
	
	@Override
	protected void invalidInputValueHandler() {
		Toast.makeText(this, 
				getString(R.string.min_cost_invalid, agentmincost), Toast.LENGTH_SHORT).show();
	}
	
	protected boolean updateOrder() {
		boolean showAlert = false;
		if( document != null ) {
			int qty1 = (int)Util.StrToScale(edQty1.getText().toString().trim(), Consts.QTY_SCALE);
			
			if (cbPack1.isChecked())
				qty1 = (int)FPOperation.itemMul(qty1, qtyInPack, Consts.QTY_SCALE);
			
			int qty2 = (int)Util.StrToScale(edQty2.getText().toString().trim(), Consts.QTY_SCALE);
			
			if (cbPack2.isChecked())
				qty2 = (int)FPOperation.itemMul(qty2, qtyInPack, Consts.QTY_SCALE);
			
			showAlert = updateQty(cbPack1.isChecked(), qty1, cbPack2.isChecked(), qty2); 
		}
		
		return showAlert;
	}
	
	protected boolean updateQty(boolean inPack1, int qty1, boolean inPack2, int qty2) {
		return !((OrderImplEx)document).updateQty(price, qty1, qty2, (int)getInputCost(price.getData()), inPack1, inPack2);
	}
}
