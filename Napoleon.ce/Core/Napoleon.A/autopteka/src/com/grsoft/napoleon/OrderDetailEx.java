package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {

	@Override protected void setContentView() { setContentView(R.layout.orderdetailex); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		int minPremium;
		int minSum;
		
		OrgImpl o = new OrgImpl();
		o.getData().id = doc.getId();
		o.read();
		o.close();
		
		OrgEx org = (OrgEx)o.getData();
		minPremium = org.minPremium/10;
		minSum = org.minOrder;
		
		TextView tv1 = (TextView)findViewById(R.id.tvInfo);
		TextView tv2 = (TextView)findViewById(R.id.tvInfo1);
		tv1.setVisibility(View.VISIBLE);
		tv2.setVisibility(View.VISIBLE);
		if( doc.getData().items != null ) {
			int sum0 = 0;
			int sum = 0;
			
			PriceImpl p = new PriceImpl();
			Price price = p.getData();
			for( OrderItem item : doc.getData().items ) {
				price.id = item.id;
				p.read();
				
				sum += (int)((long)item.cost * item.qty / Consts.QTY_SCALE);
				sum0 += (int)((long)price.cost.get(0).cost * item.qty / Consts.QTY_SCALE);
			}
			p.close();
			
			int color;
			String text = "";
			int premium = (sum == 0) ? 0 : (int)((long)sum * 1000 / sum0 - 1000);
			if( premium < minPremium ) {
				color = Color.RED;
				text += "Наценка " + Util.IntToScaleStr(premium, 10, Util.DEC_DELIM, false) + 
					"% меньше " + Util.IntToScaleStr(minPremium, 10, Util.DEC_DELIM, false) + "%";
			} else {
				color = Color.BLACK;
				text += "Наценка по заявке " + Util.IntToScaleStr(premium, 10, Util.DEC_DELIM, false) + "%";
			}
			tv1.setText(text);
			tv1.setTextColor(color);
			
			if( sum < minSum ) {
				text = "Сумма заявки " + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р. меньше " +
					Util.IntToScaleStr(minSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.";
				tv2.setText(text);
				tv2.setTextColor(Color.RED);
			} else
				tv2.setVisibility(View.GONE);
				
		} else {
			tv1.setVisibility(View.GONE);
			tv2.setVisibility(View.GONE);
		}
	}
}
