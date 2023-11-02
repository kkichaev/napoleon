package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;


public class PriceCountEx extends PriceCount {
	private int avgQty = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cbPackets.setVisibility(View.GONE);
		edCount.requestFocus();
	}
	
	protected void calcAvg() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(Util.getDate());
		cal.add(Calendar.DATE, -1);
		Date end = cal.getTime();
		cal.add(Calendar.MONTH, -1);
		Date begin = cal.getTime();
		DatePeriod dp = new DatePeriod(begin, end);
		
		com.grsoft.napoleon.documents.DocList dl = OrderDoc.instance().docList(document.getId(), null, dp);
		
		if(dl != null && dl.getCount() > 0){
			int idx = 0;
			int sum = 0;
			
			Iterator<Document<?>> iter =  dl.iterator();
			String id = price.getData().id;
			
			while(iter.hasNext()){
				OrderImpl o = (OrderImpl) iter.next();
				OrderItem oi = (OrderItem) o.findItem(id);
				
				if(oi != null){
					idx++;
					sum += oi.qty;
				}
			}
			
			if (idx != 0)
				avgQty = sum / idx;
		}
	}
	@Override protected String getRestText(long rest, RemnantItem ri) {
		String result = "";
		
		if(ri != null)
			result = Util.IntToScaleStr(rest, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			
		return result;
	}
	
	@Override
	protected void updateRest(boolean inPack, int rest, Editable txt) {
		if (txt != null && txt.length() > 0)
			super.updateRest(inPack, rest, txt);
		else{
			if(((RemnantsImplEx)rdoc).deleteItem(price.getData().id))
				rdoc.write();
		}
	}
	
	protected RestUpdate getRestUpdateHandler() {
		calcAvg();
		
		return new RestUpdate(){
		@Override
		public void afterTextChanged(Editable txt) {
			if(txt == null || txt.toString().trim().length() == 0){
				if( firstView != null ) {
					StringBuilder text = new StringBuilder();
					SimpleDateFormat sf = new SimpleDateFormat("dd.MM", Locale.getDefault());
					text.append(sf.format(Util.getDate()));
					text.append("<br>");
					text.append("&nbsp;");
					text.append("<br>");
					text.append("&nbsp;");
					text.append("<br>");
					
					text.append("<b>");
					text.append(Util.IntToScaleStr(avgQty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
					text.append("</b>");			
					firstView.setText(Html.fromHtml(text.toString()));
				}
			}else
				super.afterTextChanged(txt);
		}
	}; }
}
