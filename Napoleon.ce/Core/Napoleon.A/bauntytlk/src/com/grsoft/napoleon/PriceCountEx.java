package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	ArrayAdapter<UnitEx> adapter;
	UnitEx selected = null;
	protected Spinner spUnits;

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	void loadUnits() {
		String scode = "";

		PriceEx p = (PriceEx) price.getData();
		List<UnitItem> punits = p.units;
		if (document != null) {
			OrderItemEx oi = (OrderItemEx) getDocItem(p);
			if (oi != null)
				scode = oi.unit;
		} else if (punits.size() > 0) {
			scode = punits.get(0).id;
		}

		selected = null;
		this.units.clear();
		for (UnitItem ui : punits) {
			UnitEx uex = new UnitEx(ui);
			if (ui.id.compareTo(scode) == 0)
				selected = uex;

			this.units.add(uex);
		}
		if (spUnits == null) {
			spUnits = (Spinner) findViewById(R.id.spUnits);
			adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, this.units);
			spUnits.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected int getQtyInPack(Price p) {
		if (selected != null)
			return selected.inpack;

		return super.getQtyInPack(p);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (document instanceof OrderImpl) {
			((OrderImpl) document).setUpdateQtyHandler(this);
		}

		if (spUnits == null) {
			spUnits = (Spinner) findViewById(R.id.spUnits);
		}

		spUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				onUnitChanged(units.get(pos));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		CheckBox cb = (CheckBox) findViewById(R.id.cbPackets);
		cb.setVisibility(View.GONE);
		cb.setChecked(true);
	}

	@Override
	protected boolean getStartInPack() {
		return true;
	}

	void onUnitChanged(UnitEx newUnit) {
		selected = newUnit;
		qtyInPack = newUnit.inpack;
		if (qtyInPack == 0)
			qtyInPack = Consts.QTY_SCALE;

		TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
		tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));

		updateSumTextView();
	}

	void updateChangedFields() {
		updateCost();
		updateSumTextView();
	}

	@Override
	protected void refreshData() {
		loadUnits();
		super.refreshData();

		if (document instanceof OrderImpl) {
			if (selected != null) {
				spUnits.setSelection(units.indexOf(selected));
				onUnitChanged(selected);
			} else {
				if (spUnits.getCount() > 0)
					spUnits.setSelection(0);
			}
		}
		updateChangedFields();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx) item;

		if (selected != null)
			oie.unit = selected.id;
	}
}
