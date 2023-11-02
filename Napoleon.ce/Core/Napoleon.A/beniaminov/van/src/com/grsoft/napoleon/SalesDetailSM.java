package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.SalesFake;
import com.grsoft.dataobjects.impl.SalesImplSM;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDocEx;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.gps.GPSUtilNew;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;

public class SalesDetailSM extends SalesDetailEx implements OnClickListener {
	private View btnCloseSale;
	private View btnDelivery;
	private boolean oldPackValue = false;
	
	public static void open(Context context, long rowid){
		Intent i = new Intent(context, SalesDetailSM.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DocType.setCurDoc(SalesDocEx.instance());
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		oldPackValue= cfg.isPackView;
		cfg.isPackView = true;
		ConfigManager.save();
		
		super.onCreate(savedInstanceState);
		btnCloseSale = findViewById(R.id.btnCloseSale);
		btnDelivery = findViewById(R.id.btnDelivery);
		
		btnCloseSale.setOnClickListener(this);
		btnDelivery.setOnClickListener(this);
		
		btnEditOrder.setVisibility(View.GONE);
		btnAddItems.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onResume() {
		DocType.setCurDoc(SalesDocEx.instance());
		super.onResume();
	}
	
	protected void setContentView(){ setContentView(R.layout.salesdetailsimplemode); }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.btnCloseSale)
			closeCurrentSale();
		else if (id == R.id.btnDelivery)
			openDeliveryList();
	}

	private void openDeliveryList() {
		DocType.setCurDoc(DebtDocEx.instance());
		DocumentsW.open(this, org.getData()); 
	}

	private void closeCurrentSale() {
		SalesFake.getInstance(this, true);
		
		GpsCoord c = GPSUtilNew.getLastKnownLocation();
		Sales s = (Sales)doc.getData();
		s.latitude = c.latitude;
		s.longitude = c.longitude;
		doc.write();
		doc.close();
		
		finish();
	}
	
	@Override protected OrderImplBase<? extends Order> createDocInstance() { return new SalesImplSM();}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		cfg.isPackView = oldPackValue;
		ConfigManager.save();
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) { return true; }
	@Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {	super.onCreateContextMenu(menu, v, menuInfo); }
}
