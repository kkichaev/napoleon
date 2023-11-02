package com.grsoft.napoleon.documents;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class DebtDocEx extends DebtDoc {

	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}

	@Override
	public void setView(View view, Document<?> doc) {
		DeliveryEx d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof DeliveryEx )
			d = (DeliveryEx)dobj;
		if( d == null ) {
			super.setView(view, doc);
			return;
		}
		
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
		Date cur = new Date();
		int color = Color.BLACK;
		int payDelay = 0;
		
		if(d.payDate.getTime() < cur.getTime()) {
			payDelay = (int)((cur.getTime() - d.payDate.getTime()) / (1000 * 3600 * 24));
			if( payDelay > 0 )
				color = Color.RED;
		}
		
		String text;
		TextView tv;			

		text = d.number;
		if( payDelay > 0 )
			text += "\n" + payDelay;
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText(text);
		tv.setTextColor(color);
		
		text = sf.format(d.date) + "\n" + sf.format(d.payDate);
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setText(text);
		tv.setTextColor(color);

		text = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "\n" +
			Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setText(text);
		tv.setTextColor(color);
	}
}
