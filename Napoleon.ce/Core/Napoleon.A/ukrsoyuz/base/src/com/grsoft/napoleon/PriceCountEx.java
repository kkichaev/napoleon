package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSetQty;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

@SuppressLint("SimpleDateFormat")
public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {

	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	UnitEx selected = null;
	protected Spinner spUnits;

	@Override protected int getContentViewId() {return R.layout.pricecountex; }
	
	@Override
	protected boolean isComplexSalesHistory() {
		if(document != null) {
			if(PriceSetQty.needSetQty(price.getData().id))
				return true;
		}
		return super.isComplexSalesHistory();
	}
	
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
		
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
					@Override public int getValue() { return priceVal; }		
				}, Consts.SUM_SCALE, false, "Цена"); 
			}
		});
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbPackets);
		cb.setVisibility(View.GONE);
		cb.setChecked(true);
		
		if(document instanceof OrderImpl)
			((OrderImpl)document).setUpdateQtyHandler(this);
	}
	
	void updatePackPrice() {
		TextView tv = (TextView)findViewById(R.id.tvPricePack);
		int pc = (int)((long)priceVal * qtyInPack / Consts.QTY_SCALE);
		tv.setText(Util.IntToScaleStr(pc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();

		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, units);
		spUnits = (Spinner)findViewById(R.id.spUnits);
		spUnits.setAdapter(adapter);

		updatePackPrice();
		
		if( document != null ) {
			OrderItemEx oie = (OrderItemEx) getDocItem(price.getData());
			
			if (oie != null) {
				for(UnitEx unit : units)
					if (unit.id.equals(oie.unit)) {
						spUnits.setSelection(units.indexOf(unit));
						break;
					}
			} else if( spUnits.getCount() > 0)
				spUnits.setSelection(0);				
		}

		if( edRest != null ) {
			refreshQty(edRest.getText(), false);
				edRest.addTextChangedListener(new RestUpdateEx());
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
	
	boolean starting = true;
	void onUnitChanged(UnitEx newUnit) {
		selected = newUnit;		
		qtyInPack = newUnit.inpack;
		if( qtyInPack == 0 )
			qtyInPack = Consts.QTY_SCALE;

		TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
		tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));

		updateSumTextView();
		updatePackPrice();
		
		if( !starting && edRest != null ) {
			makeSaleHistory(price.getData());
			refreshQty(edRest.getText(), true);
		} else
			starting = false;
	}
	
	@SuppressWarnings("static-access")
	@Override
	protected OffTakeHistory getHistory(String docId, boolean fromOrders) {
		OffTakeHistory result = new OffTakeHistory(docId, fromOrders);
		result.inflator = new OffTakeHistory.OffTakeInflator(){
			private int coeff = 0;
			
			{
				FolderImpl folder = new FolderImpl();
				folder.getData().id = price.getData().folderID;
				
				if(folder.read())
					coeff = ((FolderEx)folder.getData()).coeff;
				
				folder.close();
			}
			
			public int getOffTake() {
				return coeff == 0 ? super.getOffTake() : coeff;
			};
		};
		
		return result;
	}
	
	protected void makeSaleHistory(Price p) {
		if( document == null )
			return;
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.llSilesHistory);
		ll.removeAllViews();
		
		if (isComplexSalesHistory()) {
			history = getHistory(document.getId(), Features.SALES_FROM_ORDERS);

			SimpleDateFormat sf = new SimpleDateFormat("dd.MM");

			ArrayList<Date> labels = history.getLabels();
			ArrayList<OffTakeHistory.Item> items = history.getHistory(p.id);
			
			for( int i=0; i<labels.size(); i++ ) {
				Date cd = labels.get(i);
				String text = sf.format(cd);
				OffTakeHistory.Item item = items.get(i);
				TextView tv = new TextView(this);
				
				tv.setGravity(Gravity.RIGHT);
				tv.setTextColor(Color.BLACK);
				tv.setPadding(5, 3, 5, 3);
				
				OffTakeHistory.Item labelItem = history.new Item(item.date);
				labelItem.qty = item.qty / qtyInPack * Consts.QTY_SCALE;
				labelItem.rest = item.rest / qtyInPack * Consts.QTY_SCALE;
				labelItem.offTake = item.offTake / qtyInPack * Consts.QTY_SCALE;
				
				text += "<br>" + labelItem.makeText((i==0));			
				
				tv.setLines(getHistoryLines());
				ll.addView(tv);
				
				if( i == 0 ) {
					// для первого View будем писать в refreshQty
					firstView = tv;
					lastItem = item;
				} else {
					tv.setText(Html.fromHtml(text));
				}
			}
		}else{
			String history = ((OrderDoc)DocType.getCurDoc()).getHistory(document.getData().id, p.id);
			String historyItems[] = history.split(" ");
			
			for (int i = 0; i < historyItems.length -1; i += 2) {
				int val = 0;
				
				try{
					val = Integer.parseInt(historyItems[i+1]) * Consts.QTY_SCALE / qtyInPack;
				}catch(Exception e){
					e.printStackTrace();
				}
				
				TextView tvSaleItem = new TextView(this);
				tvSaleItem.setText(Html.fromHtml(
						String.format("%s<br>%s", historyItems[i], Integer.toString(val))));
				tvSaleItem.setLines(2);
				tvSaleItem.setTextColor(getResources().getColor(R.color.black));
				tvSaleItem.setPadding(5, 3, 5, 3);
				ll.addView(tvSaleItem);
				
				Log.d("makeSaleHistory", tvSaleItem.getText().toString());
			}
		}
	}
	
	class RestUpdateEx extends RestUpdate {
		@Override public void afterTextChanged(Editable txt) { refreshQty(txt, true); }
	}
	
	@Override protected RestUpdate getRestUpdateHandler() { return null; }

	protected void refreshQty(Editable txt, boolean updateQty) {
		int rest = 0;
		if( txt != null && txt.length() != 0 ) {
			rest = Util.StrToScale(txt.toString(), Consts.QTY_SCALE);
		}
		
		boolean inPack = cbPackets.isChecked();
		rest = fixOrderQty(inPack, rest, price.getData());
		
		if( Features.PUT_REST_BEFORE_QTY )
			edCount.setEnabled((txt != null && txt.length() != 0));
		
		if( firstView != null ) {
			OffTakeHistory.PrevItemParam prv = history.new PrevItemParam();
			
			OffTakeHistory.Item item = history.updateRest(price.getData().id, rest, prv);

			String qtxt = edCount.getText().toString();
			int count = qtxt.length() == 0  ? 0 : Util.StrToScale(qtxt, Consts.QTY_SCALE);
			
			int qty = (!prv.prevItem.empty()) ? item.qty / qtyInPack * Consts.QTY_SCALE : count; 
			
			OffTakeHistory.Item labelItem = history.new Item(item.date);
			labelItem.qty = qty;
			labelItem.rest = item.rest / qtyInPack * Consts.QTY_SCALE;
			labelItem.offTake = item.offTake / qtyInPack * Consts.QTY_SCALE;
			
			SimpleDateFormat sf = new SimpleDateFormat("dd.MM");
			String text = sf.format(item.date);
			text += "<br>" + labelItem.makeText(true);			
			firstView.setText(Html.fromHtml(text));
			
			if( updateQty )
				edCount.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
		}
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		UnitEx sel = ((UnitEx)spUnits.getSelectedItem());
		if(sel != null)
			((OrderItemEx)item).unit = sel.id;
		
	}
}
