package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.Itemsable;

public class PriceCountEx extends PriceCount {
	ImageView ivInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ivInfo = (ImageView) findViewById(R.id.ivInfo);
		ivInfo.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { showDialog(R.id.price_descr_dlg); } });
	}
	
	@Override
	protected boolean updateQty(boolean inPack, int qty) {
		OrderItem item = (OrderItem) ((Itemsable)document).findItem(price.getData().id);
		if(item != null && qty != 0)
			qty += item.qty;
		return super.updateQty(inPack, qty);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.price_descr_dlg)
			return priceDescrDlg();
		else
			return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.price_descr_dlg)
			preparePriceDescrDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}
	private void preparePriceDescrDlg(Dialog dialog) {
		AlertDialog ad = (AlertDialog) dialog;
		ad.setMessage(((PriceEx)price.getData()).descr);
	}

	private Dialog priceDescrDlg() {
		AlertDialog.Builder result = new AlertDialog.Builder(this);
		result.setTitle(R.string.price_descr);
		result.setMessage("");
		result.setPositiveButton(R.string.ok, null);
		return result.create();
	}
	
	protected int getContentViewId() { return R.layout.pricecountex; }
}
