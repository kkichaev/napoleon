package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.AFProjects;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document instanceof OrderImpl )
			((OrderImpl)document).setUpdateQtyHandler(this);
		
		((Spinner)findViewById(R.id.spUnits)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				PriceUnit unit = ((PriceUnit)arg0.getAdapter().getItem(arg2)); 
				qtyInPack = unit.inPack;
				if( document != null && document instanceof Itemsable ) {
					PriceEx p = (PriceEx) price.getData();
					Itemsable id = (Itemsable)document;
					int whQty = id.getItemValue(p);
					int remnantsInPack = (int)(((long)whQty * Consts.QTY_SCALE) / qtyInPack);
					TextView tvRemnantInPack = (TextView) findViewById(R.id.tvQty);
					tvRemnantInPack.setText(Util.IntToScaleStr(remnantsInPack, Consts.QTY_SCALE));
					
//					TextView tv = (TextView)findViewById(R.id.tvPriceEx);
//					tv.setText(Util.IntToScaleStr((int)((long)priceVal * qtyInPack / Consts.QTY_SCALE), Consts.SUM_SCALE, Util.DEC_DELIM, false));
				}
				
				TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
				tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));
				updateSumTextView();
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx p = (PriceEx) price.getData();
		Spinner sp = (Spinner)findViewById(R.id.spUnits);
		ArrayAdapter<PriceUnit> adapter = new ArrayAdapter<PriceUnit>(this, R.layout.simple_spinner_layout, p.units);
		sp.setAdapter(adapter);


		String project = "";
		if(document instanceof OrderImpl) {
			OrderItemEx oe = (OrderItemEx)((OrderImpl)document).findItem(p.id);
			if( oe != null ) {
				project = oe.project;
				int sel = 0;
				for(PriceUnit pu : p.units) {
					if(pu.id.equals(oe.unitId)) {
						qtyItems = (int)((long)oe.qty * Consts.QTY_SCALE/ pu.inPack);
						
						edCount.setText(Util.IntToScaleStr(qtyItems, Consts.QTY_SCALE));
						edCount.selectAll();						
						sp.setSelection(sel);
						break;
					}
					sel++;
				}
			}
		}

		final String selSp = project;
		sp = findViewById(R.id.spProject);
		DialogHelper.loadSpinnerFromDataObject(sp, AFProjects.class, new DialogHelper.Selected<AFProjects>() {
			@Override public boolean isSelected(AFProjects object) { return selSp.equals(object.id); }
		}, true);
	}
	
	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx) item;
		PriceUnit pu = (PriceUnit)((Spinner)findViewById(R.id.spUnits)).getSelectedItem();
		if( pu != null )
			oie.unitId = pu.id;
		
		AFProjects selP = (AFProjects) ((Spinner)findViewById(R.id.spProject)).getSelectedItem();
		if(selP != null)
			oie.project = selP.id;
	}
}
