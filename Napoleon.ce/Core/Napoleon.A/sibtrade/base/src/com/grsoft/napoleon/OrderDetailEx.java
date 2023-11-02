package com.grsoft.napoleon;

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

	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int minSum;

		OrgImpl o = new OrgImpl();
		o.getData().id = doc.getId();
		o.read();
		o.close();
		
		OrgEx org = (OrgEx)o.getData();
		minSum = org.minOrder;
		
		TextView tv2 = (TextView)findViewById(R.id.tvInfo1);
		tv2.setVisibility(View.VISIBLE);
		
		if( doc.getData().items != null ) {
			int sum = 0;
			
			PriceImpl p = new PriceImpl();
			Price price = p.getData();
			for( OrderItem item : doc.getData().items ) {
				price.id = item.id;
				p.read();
				
				sum += (int)((long)item.cost * item.qty / Consts.QTY_SCALE);
			}
			p.close();
			
			if( sum < minSum ) {
				String text = "Сумма заявки " + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р. меньше " +
					Util.IntToScaleStr(minSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.";
				tv2.setText(text);
				tv2.setTextColor(getResources().getColor(R.color.red));
			} else
				tv2.setVisibility(View.GONE);
				
		} else {
			tv2.setVisibility(View.GONE);
		}
	}
}
