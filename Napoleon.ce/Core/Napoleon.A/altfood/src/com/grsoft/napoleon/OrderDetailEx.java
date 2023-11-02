package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	private TextView tvInfo;
	private boolean isAllowToBack = true;
	
	@Override protected void setContentView() { setContentView(R.layout.orderdetailex); }
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		int inPack = 0;
		String qtyText, packName = "";
		PriceEx p = (PriceEx) price.getData();
		for(PriceUnit ui : p.units) {
			if( ui.id.equals(((OrderItemEx)item).unitId) ) {
				inPack = ui.inPack;
				packName = ui.name;
				break;
			}				
		}
		if( inPack == 0 )
			inPack = Consts.QTY_SCALE;
		int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);

		qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + packName;
		
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvInfo = (TextView) findViewById(R.id.tvInfo);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		int ms = ((OrgEx)org.getData()).minSum;
		long s = doc.sum(); 
		
		if(ms != 0 && s	> 0 && s < ms){
			String text = "Сумма заявки " + Util.IntToScaleStr(s, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р. меньше " +
					Util.IntToScaleStr(ms, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.";
			tvInfo.setText(text);
			tvInfo.setVisibility(View.VISIBLE);
			isAllowToBack = false;
			btnSend.setEnabled(false);
		}else{
			tvInfo.setVisibility(View.GONE);
			isAllowToBack = true;
			btnSend.setEnabled(true);
		}
	}
	
	@Override
	public void onBackPressed() {
		if(isAllowToBack)
			super.onBackPressed();
	}
}
