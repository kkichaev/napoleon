package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.printsources.SalesSource;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail implements ScannerHelper.DocUpdated {
	public static final String NAKL_TITLE = "Накладная";	
	public static final String CHECK_TITLE = "Товарный чек";	
	private static final int DO_PRINTING = 0;
	protected static final int SELECT_PRINT_FORM = 100;

	ScannerHelper helper;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( doc instanceof OrderImpl )
			helper = new ScannerHelper((OrderImpl)doc, this);

		findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(SELECT_PRINT_FORM); }
		});
		
		lvItems.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
					helper.onKeyDown(event);
				return false;
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( event.getKeyCode() != KeyEvent.KEYCODE_BACK && helper != null )
			return helper.onKeyDown(event);
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( helper != null )
			helper.close();
	}
	
	protected Sales makeSales(OrderImplBase<? extends Order> doc) {
		Sales ret = new Sales();
		OrderEx src = (OrderEx) doc.getData();
		
		ret.created = src.created;
		ret.date = Util.getDate();//src.date;
		ret.sumType = src.sumType;
		ret.supplyer = src.supplyer;
		ret.supplyercode = src.firmCode;
		ret.id = src.id;
		ret.number = src.ordNumber;
		
		ret.items = new ArrayList<OrderItem>();
		for(OrderItem oi : src.items) {
			SalesItem si = new SalesItem();
			si.id = oi.id;
			si.cost = oi.cost;
			si.qty = oi.qty;
			si.sum = (int)((long)oi.cost * oi.qty / Consts.QTY_SCALE);
			si.costWOtax = si.cost;
			si.ntd = "";

			ret.items.add(si);
		}
		return ret;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == DO_PRINTING )
			return SelectPrinFormDlg.createWaitDlg(this);
		if( id == SELECT_PRINT_FORM ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Выберите документ");
			final String[] items = new String[] { NAKL_TITLE, CHECK_TITLE };
			b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SalesSource ss = new SalesSource(makeSales(doc));
					SelectPrinFormDlg.createPrintForm(OrderDetailEx.this, ss, DO_PRINTING, items[which], null);
					dialog.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void updated(OrderImpl doc, PriceImpl p) {
		PriceCount.open(this, p.getRowid(), doc);
	}
}
