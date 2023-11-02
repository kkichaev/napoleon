package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount implements ScannerHelper.DocUpdated, OrderImplBase.UpdateQtyHandler {
	ScannerHelper helper;
	
	int discount = 0;
	int dscType = 0;
	boolean canChangeDsc;

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		cbPackets.setFocusable(false);
		if( document instanceof OrderImpl )
			helper = new ScannerHelper((OrderImpl)document, this);
		//edCount.setEnabled(false);
		edCount.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
					helper.onKeyDown(event);
				return false;
			}
		});
		
		btnOK.setFocusable(false);
		
		if(document instanceof OrderImplEx && !(document instanceof ReturnImplEx))
			((OrderImplEx) document).setUpdateQtyHandler(this);
	}

	@Override
	protected int getInputCost(Price p) {
		return ((CostStrategyEx)CostStrategy.defaultInstance).getCostWODiscount(price.getData(), document);
	}
	
	@Override
	protected void refreshData() {		
		int vis = View.GONE, dscVisible = View.GONE;
		TextView dscInfo = (TextView)findViewById(R.id.tvDscInfo);

		discount = 0;
		
		if(document instanceof OrderImplEx && !(document instanceof ReturnImplEx)) {
			vis = View.VISIBLE;
			OrderEx oe = (OrderEx)document.getData();
			String idWh = oe.whId;
			String text = "";
			OrderItemEx oei = (OrderItemEx) ((OrderImplEx)document).findItem(price.getData().id);
			if( oei != null)
				idWh = oei.idWh;
			
			StringBuilder value = new StringBuilder();
			ConfigImpl ci = new ConfigImpl();
			if( ci.getValue(value, "СкладыТоваров") ) {
				ArrayList<KeyValue> sklads = new ArrayList<KeyValue>();
				int sel = DialogHelper.makeListWithKey(value.toString(), sklads, idWh);
				if( sel >= 0)
					text = sklads.get(sel).value.toString();
			}
			TextView tv = (TextView) findViewById(R.id.tvSklad);
			tv.setText(text);
			
			canChangeDsc = false;
			PriceEx pe = (PriceEx)price.getData();
			String dsc = "";
			OrgImpl oi = new OrgImpl();
			OrgEx orge = (OrgEx)oi.getData();
			orge.id = document.getId();
			oi.read();
			oi.close();
			if( orge.discount != 0 ) {
				discount = orge.discount;
				dscType = OrderEx.CLIENT_DISCOUNT;
				dsc = "Клиентская скидка " + Util.IntToScaleStr(orge.discount, Consts.SUM_SCALE) + " %";
			} else  if(pe.dscType == OrderEx.AUTO_DISCOUNT || pe.dscType == OrderEx.MANUAL_DISCOUNT) {
				dsc = pe.getDiscountText();
				if(dsc.length() > 0)
					dsc = "Скидка " + pe.getDscTypeText() + " " + pe.getDiscountText();
				
				discount = pe.minDsc;
				canChangeDsc = (pe.dscType == OrderEx.MANUAL_DISCOUNT);
				dscType = pe.dscType;
			}
			dscInfo.setText(dsc);

			OrderItemEx oie = (OrderItemEx) ((OrderImplEx)document).findItem(pe.id);
			if( oie != null ) {
				discount = oie.discount;
				dscType = oie.dscType;
			}
			
			if(canChangeDsc) {
				final EditText ed = (EditText)findViewById(R.id.edDiscount);
				ed.setText(Util.IntToScaleStr(discount, Consts.SUM_SCALE));
				ed.setInputType(InputType.TYPE_NULL);
			
				edCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
					@Override public void onFocusChange(View v, boolean hasFocus) {
						if( hasFocus ) {
							keypadHelper.setTargetID(R.id.edCount);
							edCount.selectAll();
						}
					}
				});
				
				ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
					@Override public void onFocusChange(View v, boolean hasFocus) {
						if( hasFocus ) {
							keypadHelper.setTargetID(R.id.edDiscount);
							ed.selectAll();
						}
					}
				});
				
				ed.addTextChangedListener(new TextWatcher() {
					@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
					@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
					
					@Override
					public void afterTextChanged(Editable s) {
						discount = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
						updateSumTextView();
					}
				});
				
				ed.setEnabled(document.isEditable());
				
				dscVisible = View.VISIBLE;
			}
		}
		findViewById(R.id.trSklad).setVisibility(vis);
		findViewById(R.id.trSumDsc).setVisibility(vis);
		findViewById(R.id.trDiscount).setVisibility(dscVisible);
		dscInfo.setVisibility(vis);
	
		super.refreshData();
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(canChangeDsc) {
			String text = ((EditText)findViewById(R.id.edDiscount)).getText().toString();
			discount = Util.StrToScale(text, Consts.SUM_SCALE);
			PriceEx pe = (PriceEx)price.getData();
			if(discount < pe.minDsc || discount > pe.maxDsc ) {
				Toast.makeText(this, "Скидка выходит за заданный диаппазон", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}
	
	int costWithDiscount() {
		return CostStrategy.defaultInstance.costWithDiscount(getInputCost(price.getData()), discount, Consts.SUM_SCALE);		
	}
	
	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();
		
		int count = getCountValue();
		if(cbPackets.isChecked())
			count = (int)((long)count * qtyInPack / Consts.QTY_SCALE);
		
		int cost = costWithDiscount();
		long sum = (long)count * cost / Consts.QTY_SCALE;
		TextView tv = (TextView)findViewById(R.id.tvDscSum);
		tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( event.getKeyCode() != KeyEvent.KEYCODE_BACK && helper != null )
			return helper.onKeyDown(event);
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void updated(OrderImpl doc, PriceImpl p) {
		price.read(p.getRowid(), false);
		refreshData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( helper != null )
			helper.close();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oe = (OrderItemEx)item;
		oe.discount = discount;
		oe.cost = costWithDiscount();
		oe.idWh = ((OrderEx)order).whId;
		oe.dscType = dscType;
	}
}
