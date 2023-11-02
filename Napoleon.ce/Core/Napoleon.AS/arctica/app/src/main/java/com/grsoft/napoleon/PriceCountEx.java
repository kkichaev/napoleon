package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	boolean inited = false;
	int cosType = 0;
	EditText edBonus;

	@Override
	protected void onChangeCost(int newCost) {
//		if( newCost < ((PriceEx)price.getData()).minCost ) {
//			Toast.makeText(this, R.string.costBelowMinALert, Toast.LENGTH_SHORT).show();
//			return;
//		}
		super.onChangeCost(newCost);
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override protected boolean getStartInPack() { return true; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((Spinner)findViewById(R.id.spUnits)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				qtyInPack = ((PriceUnit)arg0.getAdapter().getItem(arg2)).inpack;
				
				TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
				tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));
				updateSumTextView();
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}
	
	@Override
	protected void refreshData() {
		OrderImplEx oi = null;
		if(document instanceof OrderImplEx) {
			 oi = (OrderImplEx)document;
			 oi.setNoFindBonus(true);
		}

		super.refreshData();

		PriceEx p = (PriceEx) price.getData();
		Spinner spu = (Spinner)findViewById(R.id.spUnits);
		List<PriceUnit> units = new ArrayList<PriceUnit>(p.units);
		PriceUnit unit = new PriceUnit();
		unit.inpack = Consts.QTY_SCALE;
		unit.name = p.unit;
		units.add(0, unit);

		ArrayAdapter<PriceUnit> adapter = new ArrayAdapter<PriceUnit>(this, R.layout.simple_spinner_layout, units);
		spu.setAdapter(adapter);

		Spinner sp = (Spinner)findViewById(R.id.spCost);
		if(oi != null) {
			inited = true;

			if(edBonus == null) {
				edBonus = findViewById(R.id.edBonus);
				edBonus.setInputType(InputType.TYPE_NULL);

				edBonus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(hasFocus) {
							edBonus.selectAll();
							keypadHelper.setTargetView(edBonus);
						}
					}
				});
			}
			
			oi.setUpdateQtyHandler(this);

			Spinner spbu = findViewById(R.id.spBonusPack);
			ArrayAdapter<PriceUnit> bad = new ArrayAdapter<PriceUnit>(this, R.layout.simple_spinner_layout, units);
			spbu.setAdapter(bad);

			int bq = 0;
			OrderItemEx bns = oi.getBonus(price.getData().id);
			if(bns != null) {
				bq = bns.qty;
				if(bns.unit.length() > 0) {
					for(PriceUnit pu : units) {
						if(pu.id.equals( bns.unit)) {
							spbu.setSelection(units.indexOf(pu));
							bq = (int)((long)bq * Consts.QTY_SCALE / pu.inpack);
							break;
						}
					}
				}
			}
			edBonus.setText(bq == 0 ? "" :Util.IntToScaleStr(bq, Consts.QTY_SCALE));
			
			sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if(!inited) {
						inited = true;
						return;
					}
					cosType = arg2;
					int newCost = CostStrategy.defaultInstance.getPriceCost(price.getData(), cosType, document);
					onChangeCost(newCost);
				}

				@Override public void onNothingSelected(AdapterView<?> arg0) {}
			});
			
			OrderItemEx oie = (OrderItemEx) oi.findItem(price.getData().id);
			cosType = oie == null ? document.getSumType() : oie.sumType;
			
			if( oie != null ) {
				int sel = 0;
				for(PriceUnit pu : units) {
					if(pu.id.equals(oie.unit)) {
						qtyItems = (int)((long)oie.qty * Consts.QTY_SCALE/ pu.inpack);
						
						edCount.setText(Util.IntToScaleStr(qtyItems, Consts.QTY_SCALE));
						edCount.selectAll();						
						spu.setSelection(sel);
						break;
					}
					sel++;
				}
			}
			
			ConfigImpl ci = new ConfigImpl();
			DialogHelper.loadSpinnerFromConfig(ci, "¬ид÷ены", new ArrayList<CharSequence>(), sp, cosType);
		} else {
			sp.setVisibility(View.GONE);
			findViewById(R.id.llBonus).setVisibility(View.GONE);
		}
	}


	@Override
	protected boolean canChangeCost() {
		return ((PriceEx)price.getData()).canDiscount > 0;
	}

	@Override
	protected boolean updateOrder() {
		OrderImplEx oie = (OrderImplEx) document;
		int bq = Util.StrToScale(edBonus.getText().toString(), Consts.QTY_SCALE);
		if(bq != 0) {
			Spinner spu = findViewById(R.id.spBonusPack);
			oie.addBonus(price.getData().id, bq, (PriceUnit) spu.getSelectedItem());
		} else {
			oie.removeBonus(price.getData().id);
		}
		oie.write();

		return super.updateOrder();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((OrderItemEx)item).sumType = cosType;		
		PriceUnit pu = (PriceUnit)((Spinner)findViewById(R.id.spUnits)).getSelectedItem();
		if( pu != null )
			((OrderItemEx)item).unit = pu.id;
	}
}
