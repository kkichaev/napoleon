package com.grsoft.napoleon;


import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.RmntSalesPlaceQty;
import com.grsoft.dataobjects.SalesTypes;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RemnantsPriceCount extends PriceCount {
	static final String SALES_TYPE_ID = "SalesTypes";
	
	String salesType; 
	
	public static void open(Context context, RemnantsImplEx doc, String priceId, SalesTypes st) {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		p.id = priceId;
		pi.read();
		pi.close();
		
		Intent i = new Intent(context, RemnantsPriceCount.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, pi.getRowid());
		i.putExtra(SALES_TYPE_ID, st.id);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DocType.setCurDoc(RemnantsDoc.instance());
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		salesType = b.getString(SALES_TYPE_ID);
		
		super.onCreate(savedInstanceState);
		
		View v;
		v = findViewById(R.id.tableLayout2);
		if( v != null)
			v.setVisibility(View.INVISIBLE);
		v = findViewById(R.id.cbPackets);
		v.setVisibility(View.INVISIBLE);

		TextView tv = (TextView)findViewById(R.id.textView6);
		if(tv != null)
			tv.setText("Остаток");
	}

	@Override
	protected boolean updateOrder() {
		int qty = qtyItems;
		((RemnantsImplEx)document).setQty(qty, price.getData(), salesType);
		return false;
	}
	
	@Override
	protected KeypadHelper createKeypadHelper() {
		return new KeypadHelper(this, R.id.edCount, false);
	}
	
	@Override protected boolean isComplexSalesHistory() {return false; }
	@Override protected boolean getStartInPack() { return false; }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(SALES_TYPE_ID, salesType);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		int qty = 0;
		RemnantItemEx ri = (RemnantItemEx)((RemnantsImplEx)document).findItem(price.getData().id);
		if(ri != null) {
			for(RmntSalesPlaceQty pi : ri.items) {
				if(pi.id.equals(salesType)) {
					qty = pi.qty;
					break;
				}
			}
		}
		
		edCount.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
		edCount.selectAll();
	}
}
