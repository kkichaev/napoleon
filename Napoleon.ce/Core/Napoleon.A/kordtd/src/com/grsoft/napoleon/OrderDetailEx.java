package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;

import android.text.Html;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void updateTotalSum() {
		long prcSum = 0;
		for(OrderItem oi : doc.getData().items) {
			prcSum += ((long)((OrderItemEx)oi).costWOD) * oi.qty / Consts.QTY_SCALE;
		}
		long sum = doc.sum();
		int weight = doc.weight();
		int count = ((CfgNplW)ConfigManager.getConfig()).isPackView ? doc.countPack() : doc.count();
		
		
		TextView tv = (TextView) findViewById(R.id.tvTotalSum);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(DocType.SumConverter.toString(prcSum));
		
		DocType dt = OrderDoc.instance();
		if( weight != 0 || count != 0 ) {
			sb.append("&nbsp;&nbsp;<i>");
			
			if( count != 0 )
				sb.append(Integer.toString(count));
				sb.append(" ");
				sb.append(dt.getCountText(this));
			if( weight != 0 ) {
				if( sb.length() > 0 ) 
					sb.append(", ");
				
				sb.append(dt.weightToString(weight, getString(R.string.kg)));
			}
			
			sb.append("</i><br>");				
		}
		
		sb.append("<b>");
		sb.append(DocType.SumConverter.toString(sum));
		sb.append("</b>");
		
		tv.setText(Html.fromHtml(sb.toString()));
	}
}
