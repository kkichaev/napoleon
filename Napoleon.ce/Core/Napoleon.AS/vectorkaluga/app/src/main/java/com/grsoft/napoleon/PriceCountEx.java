package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceUnitImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

@SuppressLint("SimpleDateFormat")
public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {

	List<UnitItem> units = new ArrayList<>();
	protected Spinner spUnits;

	@Override protected int getContentViewId() {return R.layout.pricecountex; }

	void loadUnits(Price p) {
		PriceUnitImpl pi = new PriceUnitImpl();
		PriceUnit pu = pi.getData();
		pu.id = p.id;
		pi.read();

		units.clear();
		units.addAll(pu.items);

		pi.close();
	}

	@Override
	protected int getQtyInPack(Price p) {
		UnitItem sel = null;
		if(p != null && spUnits != null ) {
			sel = (UnitItem) spUnits.getSelectedItem();
			if(sel != null) {
				return sel.inpack;
			}
		}

		sel = findUnit();
		if(sel != null)
			return sel.inpack;
		if(units.size() > 0) {
			return units.get(0).inpack;
		}

		return super.getQtyInPack(p);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbPackets);
		cb.setChecked(true);
		findViewById(R.id.trPack).setVisibility(View.GONE);
		
		if(document instanceof OrderImpl)
			((OrderImpl)document).setUpdateQtyHandler(this);
	}

	UnitItem findUnit() {
		UnitItem ret = null;
		if( document != null ) {
			OrderItemEx oie = (OrderItemEx) getDocItem(price.getData());
			if (oie != null) {
				for (UnitItem unit : units)
					if (unit.id.equals(oie.idunit)) {
						ret = unit;
						break;
					}
			}
		}
		return ret;
	}

	@Override
	protected boolean getStartInPack() { return true; }

	@Override
	protected void refreshData() {
		loadUnits(price.getData());
		super.refreshData();

		ArrayAdapter<UnitItem> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_layout, units);
		spUnits = (Spinner)findViewById(R.id.spUnits);
		spUnits.setAdapter(adapter);

		if( document != null ) {
			UnitItem sel = findUnit();
			if(sel != null) {
				spUnits.setSelection(units.indexOf(sel));
			} else if( spUnits.getCount() > 0)
				spUnits.setSelection(0);				
		}

		spUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			int lastPos = -1;
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if(lastPos != -1){
					edCount.setText("0");
					edCount.selectAll();
					if(edRest != null) {
						edRest.setText("0");
						edRest.selectAll();
					}
				}else
					lastPos = pos;
				onUnitChanged(units.get(pos));
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
	}
	
	void onUnitChanged(UnitItem newUnit) {
		qtyInPack = newUnit.inpack;
		if( qtyInPack == 0 )
			qtyInPack = Consts.QTY_SCALE;

		TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
		tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));

		updateSumTextView();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		UnitItem sel = ((UnitItem)spUnits.getSelectedItem());
		if(sel != null)
			((OrderItemEx)item).idunit = sel.id;
		item.flags |= OrderItem.IN_PACK;
	}
}
