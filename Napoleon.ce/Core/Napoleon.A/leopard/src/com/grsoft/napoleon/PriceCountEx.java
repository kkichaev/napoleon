package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PackItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {

	ArrayList<PackItemListObj> packs = new ArrayList<PackItemListObj>();
	PackItemListObj selected = null;
	private Spinner spUnits;

	@Override protected int getContentViewId() {return R.layout.pricecountex; }
	
	@Override
	protected int getQtyInPack(Price p) {
		String whCode = "";
		if( selected == null ) {
			String scode = "";
			
			List<PackItem> packs = ((PriceEx)p).packs;
			if( document != null && document instanceof OrderImpl ) {
				OrderItemEx oi = (OrderItemEx) ((OrderImpl)document).findItem(p.id);
				whCode = ((OrderEx)document.getData()).whCode;
				
				if( oi != null )
					scode = oi.pack;
				else{
					if(whCode.trim().length() == 0)
						whCode = OrderImplEx.getDafaultSkladId();
					
					scode = getPackItemMain(packs, whCode);
				}
				
			} else if( packs.size() > 0 ) 
				scode = getPackItemMain(packs, OrderImplEx.getDafaultSkladId());
			
			
			for(PackItem pi : packs ) {
				if(whCode.equals(pi.warehouse)){
					PackItemListObj listObj = new PackItemListObj(pi);
					this.packs.add(listObj);
					
					if(pi.pack.compareTo(scode) == 0 )
						selected = listObj;
				}
			}
		}
		
		if( selected != null )
			return selected.packItem.inPack;

		return super.getQtyInPack(p);
	}

	private String getPackItemMain(List<PackItem> packs,
			String whCode) {
		String result = "";
		
		for(int i = 0; i < packs.size(); i++){
			PackItem packItem = packs.get(i);
			if(packItem.warehouse.equals(whCode) && 
					((packItem.flags & PackItem.MAIN) == PackItem.MAIN)){
				result = packItem.pack;
				break;
			}
		}
		return result;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ArrayAdapter<PackItemListObj> adapter = new ArrayAdapter<PackItemListObj>(
				this, R.layout.simple_spinner_layout, packs);
		spUnits = (Spinner)findViewById(R.id.spUnits);
		spUnits.setAdapter(adapter);
		if( selected != null ) {
			spUnits.setSelection(packs.indexOf(selected));
			onUnitChanged(selected);
		}

		spUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				onUnitChanged(packs.get(pos));
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
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
		
		OrderImplEx doc = (OrderImplEx) document;
		if (doc == null)
			doc = new OrderImplEx();
		
		PackItemListObj listObj = (PackItemListObj) spUnits.getSelectedItem();
		
		if(listObj != null){
			PackItem pack = listObj.packItem;
			int whQty = 0;
			if(pack != null)
				whQty = doc.getItemValue(price.getData(), pack.pack);
			else
				whQty = doc.getItemValue(price.getData());
			
			TextView tvQty = (TextView) findViewById(R.id.tvQty);
			tvQty.setText(Util.IntToScaleStr(whQty, Consts.QTY_SCALE));
		}
		
		if (document == null)
			spUnits.setVisibility(View.GONE);
	}
	
	@Override
	protected void updateCost() {
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(priceVal, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}

	void onUnitChanged(PackItemListObj obj) {
		if(obj != null){
			selected = obj;		
			
			if (obj.packItem != null)
				qtyInPack = obj.packItem.inPack;
			
			if( qtyInPack == 0 )
				qtyInPack = Consts.QTY_SCALE;
	
			TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
			tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));
			
			int whQty = 0;
			
			if(document != null)
				whQty = ((OrderImplEx)document).getItemValue(
						price.getData(), selected.packItem.pack);
				
			TextView tvQty = (TextView) findViewById(R.id.tvQty);
			tvQty.setText(Util.IntToScaleStr(whQty, Consts.QTY_SCALE));
			
			updateSumTextView();
		}
	}
	
	@Override
	protected void onChangeCost( int newCost ) {
		priceVal = newCost;
		updateCost();
		updateSumTextView();
	}

	@Override
	protected boolean updateQty(boolean inPack, int qty) {
		if(document instanceof OrderImplEx){
			String pack = "";
			if(selected != null)
				pack = selected.packItem.pack;
			
			return !((OrderImplEx)document).updateQtyEx(price, 
					qty, getInputCost(price.getData()), inPack, pack);
		}else
			return super.updateQty(inPack, qty);
	}
	
	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		if( document instanceof OrderImpl ) {
			OrderItemEx oi = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);
			if( oi != null && selected != null ) {
				oi.pack = selected.packItem.pack;
				document.write();
			}
		}
		return ret;
	}
}

class PackItemListObj{
	PackItem packItem;
	
	PackItemListObj(PackItem packItem){
		this.packItem = packItem;
	}
	
	@Override
	public String toString() {
		return packItem.pack;
	}
}
