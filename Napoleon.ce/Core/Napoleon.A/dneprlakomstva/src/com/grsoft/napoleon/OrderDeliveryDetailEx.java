package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import com.grsoft.database.OrderResultHitching;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail implements SendResultListener  {
	DocTypeBase curDocType;

	@Override protected void setContentView() { setContentView(R.layout.orderdeliverydetailex); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		curDocType = DocType.getCurDoc();
	super.onCreate(savedInstanceState);
		OrderHelper.updateOrderInfo((TextView)findViewById(R.id.tvDocInfo), (OrderEx)doc.getData());
	}
	
	@Override
	protected String getOrgText(Org o) {
		return OrgUtils.makeOrgInfo((OrgEx) o, (OrderImpl) doc);
	}
	
	@Override
	public void send() {
		new DocumentSender(this, btnSend, docType.getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DocType.setCurDoc(curDocType);
	}

	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		String qtyText;
		if(((PriceEx)price.getData()).boxed > 0) {
			qtyText = makePackQtyStr(item.qty, getString(R.string.box_lbl));
		} else {
			qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) + " " + getString(R.string.qty_lbl);
		}
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);			
	}

	@Override
	protected void drawDeliveryItem(int color, DeliveryItem ditem, OrderItem oitem, TextView tvQty) {
		String qtyText = "";
		if( ditem != null ) {
			if(((PriceEx)price.getData()).boxed > 0) {
				qtyText = makePackQtyStr(ditem.qty, getString(R.string.box_lbl));
			} else {
				qtyText = Util.IntToScaleStr(ditem.qty, Consts.QTY_SCALE) + " " + getString(R.string.qty_lbl);
			}
		}
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);			
	}

	@Override
	public void postSendExecute(boolean result) {
		doc.read();
		OrderEx oe = (OrderEx)doc.getData();
		OrderHelper.updateOrderInfo((TextView)findViewById(R.id.tvDocInfo), oe);

		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(Html.fromHtml(getOrgText(org.getData())));
	
		String errMsg = OrderResultHitching.getErrorMessage();
		if(errMsg.length() > 0) {
			MessageBox.show(this, getString(R.string.error), errMsg);
		}
	}
}
