package com.grsoft.napoleon;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	DeliveryImpl di = new DeliveryImpl();

	@Override
	protected void onResume() {
		super.onResume();		
		tvOrgInfo.setText(Html.fromHtml(orgInfo(org.getData())));
	}
	
	@Override
	protected String orgInfo(Org o) {
		return OrgUtils.makeOrgInfo((OrgEx) o, null);
	}
	
	@Override
	protected void onDestroy() {
		di.close();
		super.onDestroy();
	}
	
	@Override
	public void updateTotalSum(long sum, int weight) {
		updateTotalSum(sum, weight, 0);
	}
	
	@Override
	public void updateTotalSum(long sum, int weight, int count) {
		View v = findViewById(R.id.tvTotalSum);
		if( v != null )
			v.setVisibility(View.GONE);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if(docType == DebtDoc.instance()) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();			
		} else {
			adapter = null;
			super.adjustViewForDocType(docType);
		}
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		if( docType == OrderDoc.instance()) {
			String order = getOrder(docType); 
			return new OrderAdapter(this, docType, id, order);
		}
		return super.createAdapter(docType, id);
	}
	
	class OrderAdapter extends DocumentsAdapter {
		public OrderAdapter(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			OrderEx oe = (OrderEx)doc.getData();
			if( oe.number.length() > 0 ) {
				Delivery d = di.getData();
				d.id = oe.id;
				d.number = oe.number;
				boolean readed = di.read();
				if( readed ) {
					int color = OrgUtils.isDocsDiff(oe, d) ? Color.RED : Color.BLACK;
					TextView tv;
					String text;
					
					tv = (TextView)view.findViewById(R.id.tvDate);
					text = Util.simpleDateFormat.format(doc.getDate());
					tv.setText(text);
					tv.setTextColor(color);
					
					tv = (TextView)view.findViewById(R.id.tvOther);
					tv.setText(Html.fromHtml(doc.getDescription(view.getContext())));
					tv.setTextColor(color);
					
					tv = (TextView)view.findViewById(R.id.tvSum);
					text = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
					tv.setText(Html.fromHtml("<b>" + text + "</b>"));
					tv.setTextColor(color);
					
					return;
				} else {
					oe.number = "";
					doc.write();
				}
			}
			super.setData(view, doc, position);
		}
	}
}
