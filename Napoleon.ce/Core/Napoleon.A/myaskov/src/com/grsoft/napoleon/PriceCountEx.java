package com.grsoft.napoleon;

import com.grsoft.dataobjects.MinCost;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.MinCostImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount implements UpdateQtyHandler {
	private int discount;
	private int priceCost;
	private int minCost;
	private EditText edBonus;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.tvDiscount).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
					
					@Override public int getValue() { return discount; }
					
					@Override
					public void applayInput(int value, Object... params) {
						if( priceCost - value < minCost ) {
							Toast.makeText(PriceCountEx.this, "Скидка выше максимальной", Toast.LENGTH_SHORT).show();
						} else {
							discount = value;
							priceVal = priceCost - discount;
							updateCost();
							updateDiscount();
							updateSumTextView();  
						}
					}
				}, Consts.SUM_SCALE, false, "Введите скидку");
				
			}
		});
		
		if( document instanceof OrderImpl ) {
			((OrderImpl)document).setUpdateQtyHandler(this);
		}
	}
	@Override
	protected long getSum(int count) {		
		if(cbPackets.isChecked())
			count = (int)FPOperation.itemMul(count, qtyInPack, Consts.QTY_SCALE);
		
		return FPOperation.itemMul(getInputCost(price.getData()), count, Consts.QTY_SCALE);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();

		
		minCost=priceVal;
		MinCostImpl minCostImpl=new MinCostImpl();
		MinCost mc = minCostImpl.getData();
		mc.id = (null != document) ? document.getId() : "";
		mc.id_i = price.getData().id;
		
		if( minCostImpl.read() )
			minCost = mc.minCost;
		else if (mc.id.length() > 0 ) {
			mc.id = "";
			if( minCostImpl.read() )
				minCost = mc.minCost;
		}
		minCostImpl.close();
					
		priceCost = priceVal;

		
		discount = 0;
		if( document != null && document instanceof OrderImpl ) {
			OrderItemEx oe = (OrderItemEx)((OrderImpl)document).findItem(price.getData().id);
			if( oe != null ) {
				discount = oe.discount;
				priceVal = oe.cost;
				
				edBonus.setText(Util.IntToScaleStr(oe.bonus, Consts.QTY_SCALE));
			}
		}
		
		updateCost();
		updateDiscount();
	}

	private void updateDiscount() {
		String value = Util.IntToScaleStr(discount, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx ie = (OrderItemEx)item; 
		ie.discount = discount;
		ie.bonus = Util.StrToScale(edBonus.getText().toString(), Consts.QTY_SCALE);
	}
	
	@Override
	protected boolean isInputValid(Runnable r) { 
//		if (getInputCost(price.getData())==0) {
//			Toast.makeText(getApplicationContext(), "Товар с нулевой ценой отгружать нельзя", Toast.LENGTH_SHORT).show();
//			return false;
//		}
		return true; 
	}
	
	@Override
	protected void postOnCreate() {
		super.postOnCreate();
		edBonus = (EditText) findViewById(R.id.edBonus);
		
		View r = findViewById(R.id.trBonus);
		
		if(document != null && document instanceof OrderImpl ) {
			r.setVisibility(View.VISIBLE);
			edBonus.setInputType(InputType.TYPE_NULL);
			
			edBonus.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
				@Override public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus ) {
						keypadHelper.setTargetID(v.getId());
						((EditText)v).selectAll();
					}
				}
			});
		}else
			r.setVisibility(View.VISIBLE);
	}
}
