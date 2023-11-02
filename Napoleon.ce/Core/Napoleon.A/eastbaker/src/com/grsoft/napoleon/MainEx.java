package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class MainEx extends Main {
	
	Map<String, Integer> orderCounts = new HashMap<String, Integer>();
	
	@Override
	protected void refreshDocSum(DocType docType) {
		if(docType == OrderDoc.instance()) {
			orderCounts.clear();
			OrgSumImpl.periodSum = new HashMap<String, Long>();

			SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
			int period = pref.getInt(PERIOD_TYPE, 0);
			String where = makePeriodWhere(docType, period);
			
			int count = 0;
			int qty = 0;
			com.grsoft.napoleon.documents.DocList list = docType.docList(null, null, where);
			for( int i=0; i<list.getCount(); i++ ) {
				OrderImpl d = (OrderImpl) list.get(i);
				
				int s = d.qty();
				qty += s;
				
				long si = 0;
				if(OrgSumImpl.periodSum.containsKey(d.getId()))
					si = OrgSumImpl.periodSum.get(d.getId());
				OrgSumImpl.periodSum.put(d.getId(), s + si);
				
				Integer ctr = orderCounts.get(d.getId());
				if(ctr == null)
					ctr = 0;
				orderCounts.put(d.getId(), ++ctr);
				count++;
			}
			
			list.close();

			updateTotalSum((long)count * Consts.SUM_SCALE, 0, qty);
			return;
		}
		super.refreshDocSum(docType);
	}
	
	@Override
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);
		if(DocType.getCurDoc() == OrderDoc.instance()) {
			Integer count = orderCounts.get(org.id);
			Long qty = OrgSumImpl.periodSum == null ? null : OrgSumImpl.periodSum.get(org.id);
			String text = "";
			if(qty != null && count != null) {
				text += "<i>" + Util.IntToScaleStr(qty, 1);
				text += "רע.</i><br/>";
				text += Integer.toString(count) + ",00";
			}
			TextView tvOrgSum = (TextView)view.findViewById(R.id.tvOrgSum);
			tvOrgSum.setText(Html.fromHtml(text));
		} 
	}
}
