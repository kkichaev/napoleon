package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class DocumentsEx extends Documents {
	private View btnPrice;
	private DeliveryInfo deliveryInfo;
	
	@Override protected int getContentViewID() { return R.layout.documentsex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnPrice = findViewById(R.id.btnPrice);
		btnPrice.setOnClickListener(btnPriceClick);
	}
	
	OnClickListener btnPriceClick = new OnClickListener() { @Override public void onClick(View v) { WarehouseEx.open(v.getContext(), org.getData().costype);} };
	
	
	protected String orgInfo(com.grsoft.dataobjects.Org o) {
		deliveryInfo = DeliveryInfo.collectDelivery(o.id);
		OrgEx oe = (OrgEx) o;
		StringBuilder sb = new StringBuilder(super.orgInfo(o));
		sb.append("<br>");
		
		sb.append(getString(R.string.debt_info, Util.IntToScaleStr(oe.limitsum, Consts.SUM_SCALE),
				Util.IntToScaleStr(deliveryInfo.sum, Consts.SUM_SCALE), oe.limitcnt, deliveryInfo.count));
		
		return sb.toString();
	};
	
	@Override
	protected void doCreate() {
		if(DocType.getCurDoc() == OrderDoc.instance() && (deliveryInfo.hasExceed || deliveryInfo.sum >= ((OrgEx)org.getData()).limitsum)) 
			showDialog(R.id.has_exceed_delivery_dlg);
		else
			super.doCreate();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.has_exceed_delivery_dlg)
			return new ExceedDeliveryDialogFactory().createDialog(this, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					docCreating();
				}
			});
		else
			return super.onCreateDialog(id);
	}
}
