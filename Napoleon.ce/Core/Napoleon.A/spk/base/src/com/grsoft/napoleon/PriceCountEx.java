package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.calculator2.Calculator;
import com.grsoft.dataobjects.OrderImplEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.Unitable;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	public static int CALCULATOR = 1;

	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	protected UnitEx selectedUnit = null;
	protected Spinner spUnits;

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	@Override
	protected int getQtyInPack(Price p) {

		if (selectedUnit == null) {
			String scode = "";

			List<UnitItem> units = ((PriceEx) p).units;
			if (document != null) {
				Unitable oi = (Unitable) getDocItem(p);
				;
				if (oi != null)
					scode = oi.getUnit();
			} else if (units.size() > 0) {
				scode = units.get(0).id;
			}

			for (UnitItem ui : units) {
				UnitEx uex = new UnitEx(ui);
				if (ui.id.compareTo(scode) == 0)
					selectedUnit = uex;

				this.units.add(uex);
			}
		}

		if (selectedUnit != null)
			return selectedUnit.inpack;

		return super.getQtyInPack(p);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this,
				R.layout.simple_spinner_layout, units);
		spUnits = (Spinner) findViewById(R.id.spUnits);
		spUnits.setAdapter(adapter);

		spUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				onUnitChanged(units.get(pos));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		TextView tv = (TextView) findViewById(R.id.tvPrice);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
					@Override
					public void applayInput(int value, Object... params) {
						onChangeCost(value);
					}

					@Override
					public int getValue() {
						return priceVal;
					}
				}, Consts.SUM_SCALE, false, "÷ена");
			}
		});

		CheckBox cb = (CheckBox) findViewById(R.id.cbPackets);
		cb.setVisibility(View.GONE);
		cb.setChecked(true);

		ImageButton btnCalc = (ImageButton) findViewById(R.id.btnCalc);

		if (btnCalc != null)
			btnCalc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(v.getContext(), Calculator.class);
					i.putExtra(Calculator.START_CALC_VAL, edCount.getText()
							.toString().replace(',', '.'));
					startActivityForResult(i, CALCULATOR);
				}
			});

		DocType curdt = DocType.getCurDoc();

		if (curdt.equals(OrderDoc.instance())
				|| curdt.equals(ReturnDoc.instance())) {
			
			StringBuilder val = new StringBuilder();
			ConfigImpl config = new ConfigImpl();
			
			if(config.getValue(val, "ћожноћен€ть≈д»зм"))
				spUnits.setEnabled(val.toString().equals("1"));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CALCULATOR && resultCode == Activity.RESULT_OK
				&& data != null)
			edCount.setText(data.getStringExtra(
					Calculator.CALCULATOR_RESULT_VALUE).replace('.', ','));
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (document != null) {
			Unitable oi = (Unitable) getDocItem(price.getData());

			if (oi != null) {
				for (UnitEx unit : units)
					if (unit.id.equals(oi.getUnit())) {
						spUnits.setSelection(units.indexOf(unit));
						onUnitChanged(unit);
						break;
					}
			} else {
				if (spUnits.getCount() > 0)
					spUnits.setSelection(0);
			}
		}
	}

	void onUnitChanged(UnitEx newUnit) {
		selectedUnit = newUnit;
		qtyInPack = newUnit.inpack;
		if (qtyInPack == 0)
			qtyInPack = Consts.QTY_SCALE;

		TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
		tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));

		updateSumTextView();
	}

	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();

		Unitable oi = (Unitable) getDocItem(price.getData());
		if (oi != null && selectedUnit != null) {
			oi.setUnit(selectedUnit.id);
			document.write();
		}

		return ret;
	}

	@Override
	protected int fixOrderQty(boolean inPack, int qty, Price price) {
		int result = super.fixOrderQty(inPack, qty, price);

		if (document instanceof OrderImplEx) {
			OrgImpl orgImpl = new OrgImpl();
			orgImpl.getData().id = document.getId();
			orgImpl.read();
			orgImpl.close();

			PriceEx pe = (PriceEx) price;
			int avgWeight = pe.avgWeight;

			if (avgWeight > 0) {
				int rest = qty % avgWeight;

				OrgEx orgEx = (OrgEx) orgImpl.getData();

				if (orgEx.useAvgWeight > 0 && rest != 0)
					result = (qty / avgWeight) * avgWeight
							+ (rest >= avgWeight / 2 ? avgWeight : 0);
			}
		}

		return result;
	}

	@Override
	protected OffTakeHistory getHistory(String docId, boolean fromOrders) {
		OffTakeHistory result = new OffTakeHistory(docId, fromOrders);
		OffTakeHistory.inflator = new OffTakeHistory.OffTakeInflator() {
			public int getOffTake() {
				int coeff = ((PriceEx) price.getData()).coeff;
				return coeff == 0 ? super.getOffTake() : coeff;
			};
		};

		return result;
	}
}
