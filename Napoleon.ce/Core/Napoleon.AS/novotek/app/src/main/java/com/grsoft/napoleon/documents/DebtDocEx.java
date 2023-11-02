package com.grsoft.napoleon.documents;

import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
	static public void initialize() {
		instance = new DebtDocEx();
	}

	public void setView(Adapter adapter, View view, Document<?> doc) {
		if(doc instanceof DeliveryImpl){
			TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
			
			String address = ((DeliveryEx)doc.getData()).address;
			
			if(tvAddress != null)
				if(address.trim().length() > 0){
					tvAddress.setVisibility(View.VISIBLE);
					tvAddress.setText(address);
				}else
					tvAddress.setVisibility(View.GONE);
			
		}
		
		TextView tv = (TextView)view.findViewById(R.id.tvDate);
		
		if (doc.getDate() == null)
			tv.setText("Ошибка документа");
		else
			tv.setText(Util.simpleDateFormat.format(doc.getDate()));
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setVisibility(View.VISIBLE);
		
		String text;
		DeliveryEx de = (doc instanceof DeliveryImpl) ? ((DeliveryEx)doc.getData()) : null;
		
		if(de != null ) {
			text = Util.IntToScaleStr(de.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) +
				"\n" +
				Util.IntToScaleStr(de.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		} else
			text = Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		
		tv.setText(text);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		if(de != null){
			text = de.number + "/" + de.baseType;
			tv.setText(text);
		} else
			tv.setText(Html.fromHtml(doc.getDescription(view.getContext())));
	}
}
