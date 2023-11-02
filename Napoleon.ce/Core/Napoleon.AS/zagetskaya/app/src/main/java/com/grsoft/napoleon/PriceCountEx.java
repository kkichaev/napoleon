package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.CmpHistory;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesHistory;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.SimpleMessageBox;

public class PriceCountEx extends PriceCount {

	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	UnitEx selected = null;
	protected Spinner spUnits;
	LinearLayout llReturnHistory;

	@Override protected int getContentViewId() {return R.layout.pricecountex; }
	
	@Override
	protected int getQtyInPack(Price p) {
		if(p != null && selected == null ) {
			String scode = "";
			
			List<UnitItem> units = ((PriceEx)p).units;
			if( document != null ) {
				OrderItemEx oi = (OrderItemEx) getDocItem(p);;
				if( oi != null )
					scode = oi.unit;
			} else if( units.size() > 0 ) {
				scode = units.get(0).id;
			}

			for(UnitItem ui : units ) {
				UnitEx uex = new UnitEx(ui);
				if(ui.id.compareTo(scode) == 0 )
					selected = uex;
				
				this.units.add(uex);
			}
		}
		
		if( selected != null )
			return selected.inpack;

		return super.getQtyInPack(p);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, units);
		spUnits = (Spinner)findViewById(R.id.spUnits);
		spUnits.setAdapter(adapter);

		spUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				onUnitChanged(units.get(pos));
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
					@Override public long getValue() { return priceVal; }
				}, Consts.SUM_SCALE, false, "÷ÂÌ‡"); 
			}
		});
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbPackets);
		cb.setVisibility(View.GONE);
		cb.setChecked(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if( document != null ) {
			OrderItemEx oie = (OrderItemEx) getDocItem(price.getData());
			
			if (oie != null) {
				for(UnitEx unit : units)
					if (unit.id.equals(oie.unit)){
						spUnits.setSelection(units.indexOf(unit));
						onUnitChanged(unit);
						break;
					}
			} else {
				ConfigImpl cfg = new ConfigImpl();
				StringBuilder sb = new StringBuilder();
				final String KEY = "≈‰»ÁÏ";
				
				if(cfg.getValue(sb, KEY)){
					for(int i = 0; i < spUnits.getCount(); i++){
						UnitItem u = (UnitItem)spUnits.getItemAtPosition(i);
						
						if(u != null && u.id.equals(sb.toString())){
							spUnits.setSelection(i);
							onUnitChanged((UnitEx) u);
						}
					}
				}else if( spUnits.getCount() > 0){
					spUnits.setSelection(0);
					onUnitChanged((UnitEx) spUnits.getItemAtPosition(0));
				}
			}
		}
	}
	
	void onUnitChanged(UnitEx newUnit) {
		selected = newUnit;		
		qtyInPack = newUnit.inpack;
		if( qtyInPack == 0 )
			qtyInPack = Consts.QTY_SCALE;

		TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
		tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));

		updateSumTextView();
	}
	
	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		
		OrderItemEx oie = (OrderItemEx) getDocItem(price.getData());
		if( oie != null && selected != null ) {
			oie.unit = selected.id;
			document.write();
		}

		return ret;
	}

	@Override
	protected boolean isInputValid(Runnable r) {
		boolean res = true;
		PriceEx p = (PriceEx) price.getData();
		int qty = qtyItems;
		qty = fixOrderQty(true, qty, price.getData());

		if (p.grav == 0)
			res = qty >= p.min;
		else
			res =  (int) FPOperation.itemMul(qty, p.weight, Consts.QTY_SCALE) >= p.min;

		return res;
	}

	@Override
	protected void invalidInputValueHandler() {
		SimpleMessageBox smb = new SimpleMessageBox(getString(R.string.warning),
				getString(R.string.limit_error,  Util.IntToScaleStr(((PriceEx)price.getData()).min, Consts.QTY_SCALE)), PriceCountEx.this);
		smb.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		smb.show();
	}

	@Override
	protected void makeSaleHistory(Price p) {
		super.makeSaleHistory(p);

		llReturnHistory = findViewById(R.id.llReturnHistory);

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
		DocList list = OrderDoc.instance().docList(document.getId());
		list.sort((x,y)->(int)(x-y));

		if (list.getCount() > 0){
			OrderImpl ord = (OrderImpl) list.get(0);
			String where = String.format("created > %d", Util.resetTime(ord.getData().created).getTime());
			DocList rlist = ReturnDoc.instance().docList(document.getId(), "created", where);

			Map<Long, Integer> dic = new HashMap<>();

			if (rlist.getCount() > 0){
				for(Document d : rlist){
					ReturnImpl r = (ReturnImpl)d;
					for(OrderItem i : r.getData().items){
						if (i.id.equals(p.id)){
							long key = r.getData().created.getTime();
							if (dic.containsKey(key))
								dic.put(key, dic.get(key + i.qty));
							else
								dic.put(key, i.qty);
						}
					}
				}
			}

			ArrayList<Map.Entry<Long, Integer>> saleHistory = new ArrayList<Map.Entry<Long,Integer>>();
			saleHistory.addAll(dic.entrySet());
			Collections.sort(saleHistory, new CmpHistory());

			for (Map.Entry<Long, Integer> entry: saleHistory) {
				TextView tvSaleItem = new TextView(this);
				tvSaleItem.setText(Html.fromHtml(
						String.format("%s<br>%s", sdf.format(new Date(entry.getKey())),
								Util.IntToScaleStr(entry.getValue(), Consts.QTY_SCALE))));
				tvSaleItem.setLines(2);
				tvSaleItem.setTextColor(getResources().getColor(R.color.black));
				tvSaleItem.setPadding(5, 3, 5, 3);
				llReturnHistory.addView(tvSaleItem);
			}
		}
	}
}
