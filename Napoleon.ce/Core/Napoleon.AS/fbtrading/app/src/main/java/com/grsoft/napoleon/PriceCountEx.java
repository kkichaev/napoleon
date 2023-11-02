package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.MaxDiscounts;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.OwnSklad;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.PriceWhData;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.MaxDiscountImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgDiscountImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	protected static final int SHOW_SKLAD_INFO = 0x10;
	
	long priceCost = 0;
	long priceCostWithoutTax = 0;

	long dsc = 0;
	long maxDiscount = 0, minCost = 0;
	long lastCost = 0;

	List<Sklad> sklads = new ArrayList<>();
	List<SkladData> skladData = new ArrayList<>();

	boolean inited = false;

	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		ConfigImpl ci = new ConfigImpl();
//		StringBuilder sb = new StringBuilder();
//		if(ci.getValue(sb, "Склады")) {
//			DialogHelper.makeListWithKey(sb.toString(), sklads, "");
//		}
//		ownSklad = OwnSklad.getOwnSklad();

		DbReader r = new DbReader();
		sklads = r.fetch(Sklad.class);

		super.onCreate(savedInstanceState);

		findViewById(R.id.ivSkladInfo).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(SHOW_SKLAD_INFO); }
		});
		if( document instanceof OrderImpl){
			((OrderImpl)document).setUpdateQtyHandler(this);
		}
		findViewById(R.id.tvDiscount).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public void applayInput(int value, Object... params) { onDiscountChange(-value); }
					@Override public long getValue() { return -dsc; }
				}, Consts.SUM_SCALE, false, "Скидка/Наценка", DiscountInputDlg.Type.Both); 
			}
		});
	}

	private void updatePriceWhithoutTaxField() {
		TextView tv = findViewById(R.id.tvPrice2);
		tv.setText(Util.IntToScaleStr(priceCostWithoutTax, Consts.SUM_SCALE));
	}


	@Override
	protected void setCenterImage(String fileName) {
		super.setCenterImage(fileName);
		
		if(ivPresent2 != null) {
			Display display = getWindowManager().getDefaultDisplay(); 
			int height = display.getHeight();
			ivPresent2.getLayoutParams().height = height / 6;
			ivPresent2.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
	}
	
	void updateChangedFields() {
		updateCost();
		updateSumTextView();
		updateDsc();
	}
	
	void onDiscountChange( int newDsc ) {
		if(newDsc > maxDiscount) {
			Toast.makeText(this, "Цена выходит за пределы изменения", Toast.LENGTH_SHORT).show();
			return;
		}
		dsc = newDsc;
		priceVal = CostStrategy.costWithDiscount(priceCost, dsc, Consts.SUM_SCALE);
		calcCostWhithoutPrice();
		updateChangedFields();
		updatePriceWhithoutTaxField();
	}	
	
	@Override
	protected void onChangeCost( int newCost ) {
		if( newCost < minCost){
			Toast.makeText(this, "Цена выходит за пределы изменения", Toast.LENGTH_SHORT).show();
			return;
		}
		
		priceVal = newCost;
		calcCostWhithoutPrice();
		dsc = (int)(10000 - (long)priceVal * 10000 / priceCost);

		updateChangedFields();
		updatePriceWhithoutTaxField();
	}

	private void calcCostWhithoutPrice() {
		priceCostWithoutTax = (int)(Math.round((double)priceVal / 1.2));
	}


	void updateDsc() {		
		String text = "<u><font color='blue'>" + Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</font></u>";
		TextView tv;
		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setText(Html.fromHtml(text));
	}
	
	@Override protected boolean canChangeCost() { return (document instanceof OrderImpl); }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == SHOW_SKLAD_INFO) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Наличие товара");
			b.setMessage("");
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == SHOW_SKLAD_INFO) {
			String message = "";
			
			PriceEx pe = (PriceEx)price.getData();
			
			for(int i=0; i<sklads.size() - 1 && i < pe.whQty.size(); i++ ) {
				PriceWhData whd = (PriceWhData) pe.whQty.get(i);
				if( whd.qty > 0 ) {
					String data = sklads.get(i+1).name;
					data += ": " + Util.IntToScaleStr(whd.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true) + "<br/>";
					message += data;
				}
			}
			
			((AlertDialog)dialog).setMessage(Html.fromHtml(message));
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();

		if(!inited) {
			skladData = new ArrayList<>();
			for(int i=0; i<sklads.size(); i++) {
				Sklad skl = sklads.get(i);
				boolean add = skl.userid.isEmpty() || skl.userid.equals(skl.agentid);
				if(add) {
					SkladData sd = new SkladData();
					sd.id = skl.id;
					sd.name = skl.name;
					sd.index = i;
					skladData.add(sd);
				}
			}
			Spinner spWh = (Spinner) findViewById(R.id.spWh);
			ArrayAdapter<SkladData> aa = new ArrayAdapter<SkladData>(this, R.layout.simple_spinner_layout, skladData);
			aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			spWh.setAdapter(aa);
			if(document instanceof OrderImplEx) {
				spWh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						SkladData sd = (SkladData)arg0.getSelectedItem();
						int qty = ((OrderImplEx)document).getItemValue(price.getData(), sd.index);

						String text = Util.IntToScaleStr(qty, Consts.QTY_SCALE);

						int res = ((OrderImplEx)document).getItemRes(price.getData(), sd.index);

						if (res > 0)
							text = String.format("%s (%s)", text, Util.IntToScaleStr(res, Consts.QTY_SCALE));

						((TextView)findViewById(R.id.tvQty)).setText(text);
					}
		
					@Override public void onNothingSelected(AdapterView<?> arg0) {}
				});
			}
			inited = true;
		}
		
		PriceEx pe = (PriceEx)price.getData();
		int vsbl = View.GONE;
		for(PriceQtyItem whd : pe.whQty)
			if(whd.qty > 0 ) {
				vsbl = View.VISIBLE;
				break;
			}
		
		findViewById(R.id.ivSkladInfo).setVisibility(vsbl);

		priceCost = priceVal;
		MaxDiscountImpl mi = new MaxDiscountImpl();
		MaxDiscounts mdsc = mi.getData();
		mdsc.id = pe.id;
		if(mi.read()) {
			maxDiscount = mdsc.discount;
		} else {
			mdsc.id = "";
			mi.read();
			maxDiscount = mdsc.discount;
		}
		minCost = CostStrategy.costWithDiscount(priceCost, maxDiscount, Consts.SUM_SCALE);
		
		if(document instanceof OrderImpl) {
			OrderItemEx oi = (OrderItemEx) ((OrderImpl) document).findItem(pe.id);
			if(oi == null) {
				OrgDiscountItem dscI = OrgDiscountImpl.getDiscount(document.getId(), pe.id);
				if(dscI != null) {
					dsc = dscI.discount;
					lastCost = dscI.cost;
				} else {
					dsc = 0;
					lastCost = 0;
				}
				if(dsc > maxDiscount)
					dsc = maxDiscount;
				priceVal = CostStrategy.costWithDiscount(priceCost, dsc, Consts.SUM_SCALE);
			} else {
				priceVal = Math.round(oi.cost * 1.2);
				dsc = oi.discount;

				int idx = 0;
				for(SkladData sd : skladData) {
					if(sd.index == oi.skladIndex) {
						break;
					}
					idx++;
				}
				Spinner spWh = (Spinner) findViewById(R.id.spWh);
				if(spWh.getAdapter().getCount() > idx)
					spWh.setSelection(idx);
				
			}
		}
		calcCostWhithoutPrice();
		updatePriceWhithoutTaxField();
		updateChangedFields();
		TextView tv;
		tv = (TextView)findViewById(R.id.tvPriceCost);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		tv = (TextView)findViewById(R.id.tvMaxDsc);
		tv.setText(Util.IntToScaleStr(maxDiscount, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		tv = (TextView)findViewById(R.id.tvLastCost);
		tv.setText(Util.IntToScaleStr(lastCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}

	@Override
	protected boolean updateQty(boolean inPack, int qty) {
		if(document instanceof OrderImplEx) {
			Spinner spWh = (Spinner) findViewById(R.id.spWh);
			SkladData sd = (SkladData)spWh.getSelectedItem();
			if(sd == null)
				sd = (SkladData) spWh.getAdapter().getItem(0);
			((OrderImplEx)document).setCurSklad(sd.index);
		}

		return !((Itemsable)document).updateQty(price,
				qty, priceCostWithoutTax, inPack);
	}
	
	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx)item;
		oie.discount = (int)dsc;
		oie.priceCost = (int)priceCost;
		Spinner spWh = (Spinner) findViewById(R.id.spWh);
		SkladData sd = (SkladData)spWh.getSelectedItem();
		if(sd == null)
			sd = (SkladData) spWh.getAdapter().getItem(0);
		
		oie.skladIndex = sd.index;
		oie.skladId = sd.id;
	}

	protected long getSum(int count) {
		if(cbPackets != null && cbPackets.isChecked())
			count = (int) FPOperation.itemMul(count, qtyInPack, Consts.QTY_SCALE);

		long val = (long)priceCostWithoutTax * count / Consts.QTY_SCALE;
		return val;
//		return (int)FPOperation.itemMul(getInputCost(price.getData()), count, Consts.QTY_SCALE);
	}
}

class SkladData {
	public String id = "";
	public int index = 0;
	public String name = "";
	
	@Override
	public String toString() {
		return name;
	}
}