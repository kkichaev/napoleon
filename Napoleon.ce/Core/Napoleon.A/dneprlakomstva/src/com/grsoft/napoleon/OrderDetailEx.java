package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.OrderResultHitching;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceTop;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailEx extends OrderDetail implements SendResultListener {
	@Override protected void setContentView() { setContentView(R.layout.orderdetailex); }
	
	public static final int REMOVE_ORDER_ALERT = 0x123;
	
	DocTypeBase curDocType;
	List<MatrixItem> topPrice = null;
	
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
	protected int focusButtonColor() {
		return Color.GREEN;
	}
	
	@Override
	protected boolean haveFocusedGroup() {
		if(topPrice == null)
			topPrice = PriceTop.get(doc.getId());
		return topPrice.size() > 0;
	}
	
	@Override
	protected boolean haveUnsettedFocusedGroups() {
		for(OrderItem oi : doc.getData().items) {
			for(MatrixItem mi : topPrice)
				if(oi.id.equals(mi.id))
					return false;
		}
//		
//		if(cpy.size() == 0)
//			return false;
//		
//		PriceImpl pi = new PriceImpl();
//		Price p = pi.getData();
//		
//		// check wh anount
//		boolean ret = false;
//		for(MatrixItem it : cpy) {
//			p.id = it.id;
//			if(pi.read() && doc.getItemValue(p)> 0) {
//				ret = true;
//				break;
//			}
//		}
//		pi.close();
		return true;
	}
	
	@Override
	protected void openFocusItemEditor() {
		WarehouseEx.openTopMatrix(this, doc);
	}
	
	@Override
	protected String getOrgText(Org o) {
		return OrgUtils.makeOrgInfo((OrgEx) o, (OrderImpl) doc);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		curDocType = DocType.getCurDoc();
		super.onCreate(savedInstanceState);
		OrderHelper.updateOrderInfo((TextView)findViewById(R.id.tvDocInfo), (OrderEx)doc.getData());
		
		Button b = (Button)findViewById(R.id.btnFocus);
		b.setText("Топ-ассортимент не заказан");
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(Html.fromHtml(getOrgText(org.getData())));
	}
	
	boolean checkIsOrderGood() {
		if(doc.isExported())
			return  true;
		long outSum = OrgUtils.getOutDebt(doc.getId());
		return ((OrderImplEx)doc).isGood(outSum);
	}
	
	@Override
	protected void updateTotalSum() {
		updateTotalSum(doc.sum(), doc.weight(), 0);
	}
	
	@Override
	public void send() {
		if(checkIsOrderGood()) {
			new DocumentSender(this, btnSend, docType.getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
		} else {
			Toast.makeText(this, R.string.outDebtErr, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == REMOVE_ORDER_ALERT) {
			AlertDialog.Builder bld = new AlertDialog.Builder(this);
			bld.setTitle(R.string.warning);
			bld.setMessage(R.string.outDebtErrDel);
			bld.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doc.delete();
					finish();
				}
			});
			
			bld.setNegativeButton(R.string.no, null);
			return bld.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void onBackPressed() {
		if( checkIsOrderGood() ) {
			super.onBackPressed();
			DocType.setCurDoc(curDocType);
		} else
			showDialog(REMOVE_ORDER_ALERT);
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result ) {
			doc.read();
			OrderEx oe = (OrderEx)doc.getData();
			if( oe.number.length() > 0) {
				OrderDeliveryDetail.open(this, (OrderImpl)doc);
				finish();
			} else {
				OrderHelper.updateOrderInfo((TextView)findViewById(R.id.tvDocInfo), oe);

				TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
				tvOrg.setText(Html.fromHtml(getOrgText(org.getData())));
			}
			
			String errMsg = OrderResultHitching.getErrorMessage();
			if(errMsg.length() > 0) {
				MessageBox.show(this, getString(R.string.error), errMsg);
			}
		}
	}
}
