package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	View dlgView;
	OrderInfoAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				
				if(!Features.CAN_SEND_EMPTY_DOCS && doc.getData().items.size() == 0 )
					showDialog(CANT_SEND_EMPTY_DOC_DLG);
				else
					trysend();
			}
		});
		
		findViewById(R.id.tvTotalSum).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(R.id.order_info_by_top_folders); }
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if(id == R.id.order_info_by_top_folders) {
			return createInfoDialog();
		}
		return super.onCreateDialog(id, args);
	}
	
	private Dialog createInfoDialog() {
		return OrderInfoAdapter.createInfoDialog(this);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.order_info_by_top_folders){
			List<Order> docs = new ArrayList<Order>();
			docs.add(doc.getData());
			OrderInfoAdapter.prepareDialog(this, docs);
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		Price p = price.getData();
		int inPack = p.qtyInPack;
		if( inPack == 0 )
			inPack = Consts.QTY_SCALE;
		int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
		String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
	
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
	
	protected void trysend() {
		if(doc.isExported())
			showDialog(R.id.ask_to_resend_order);
		else	
			send();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.ask_to_resend_order:
			return createdAskToResendDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createdAskToResendDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.question);
		builder.setMessage(R.string.ask_to_resend_order);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { send(); }	});
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}
	
}

