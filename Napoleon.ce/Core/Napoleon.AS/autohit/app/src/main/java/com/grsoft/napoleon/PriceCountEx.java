package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgDogovors;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	private static final int ERROR_ORDER = 1234;
	String errMessage= "";
	Runnable runOk;
	
	int priceCost;
	int discount;
	int minCost;
	int minQty;
	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	UnitEx selected = null;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx)price.getData();
		
		minCost = pe.minCost;
		minQty = pe.minQty;
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvMinCost);
		tv.setText(Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvMinQty);
		tv.setText(Util.IntToScaleStr(minQty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cbPackets.setVisibility(View.GONE);
		priceCost = priceVal;

		TextView tv;
		tv = (TextView)findViewById(R.id.tvCost);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public int getValue() { return -discount; }
					@Override
					public void applayInput(int value, Object... params) {
						discount = -value;
						int newCost = countPriceVal(); 
						if( newCost < minCost ) {
							Toast.makeText(PriceCountEx.this, R.string.cost_below_min, Toast.LENGTH_LONG).show();
							//discount = svDiscount;
							return;
						}
						priceVal = newCost;
						updateCost();
						updateDiscount();
						updateSumTextView();
					}}, Consts.SUM_SCALE, false, "Введите скидку");
			}
		});

		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, units);
		Spinner s = (Spinner)findViewById(R.id.spUnits);
		s.setAdapter(adapter);
		if( selected != null ) {
			s.setSelection(units.indexOf(selected));
			onUnitChanged(selected);
		}

		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				onUnitChanged(units.get(pos));
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});

		if(document != null)
			s.setEnabled(document.isEditable());
		else
			s.setVisibility(View.GONE);
		
		if(document != null && document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ID ) {
			OrderImpl o = (OrderImpl)document;
			OrderItemEx oe = (OrderItemEx) o.findItem(price.getData().id);
			discount = (oe != null) ? oe.discount : 0;

			priceVal = countPriceVal();
			updateCost();
			updateSumTextView();
			
		}
		updateDiscount();
	}

	@Override
	protected void onChangeCost(int newCost) {
		if( newCost < minCost ) {
			Toast.makeText(this, R.string.cost_below_min, Toast.LENGTH_SHORT).show();
			return;
		}

		super.onChangeCost(newCost);
		discount = countDiscountVal();
		updateDiscount();
	}
	
	private int countDiscountVal() {
		return -(100 * Consts.SUM_SCALE - (int)(((float)priceCost / (float)priceVal) * Consts.SUM_SCALE * 100 ));
	}
	
	private int countPriceVal() {
		return priceCost - (int)(((long)priceCost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
	}
	
	private void updateDiscount() {
		String label = (discount >= 0 ) ? "скидка,%" : "наценка,%";
		((TextView)findViewById(R.id.tvDiscountLabel)).setText(label);
		
		String value = Util.IntToScaleStr(Math.abs(discount), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		
		if( document instanceof OrderImpl ) {
			OrderItemEx oi = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);
			if( oi != null ) {
				oi.discount = discount;
				
				if(selected != null)
					oi.unit = selected.id;
				
				document.write();
			}
		}

		return ret;
	}
	
	@Override
	protected boolean canChangeCost() {
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == ERROR_ORDER ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка в заявке");
			b.setMessage("");
			b.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { runOk.run(); }
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == ERROR_ORDER ) {
			((AlertDialog)dialog).setMessage(errMessage);
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(document instanceof OrderImpl) {
			OrgDogovors dogovor = null;
			OrderEx order = (OrderEx)document.getData();
			OrgImpl oi = new OrgImpl();
			OrgEx o = (OrgEx)oi.getData();
			o.id = order.id;
			oi.read();
			oi.close();
			
			for(OrgDogovors od : o.dogovors)
				if( od.id.equals(order.dogovor) ) {
					dogovor = od;
					break;
				}
			
			Price p = price.getData();
			int qty = qtyItems;
			qty = fixOrderQty(cbPackets.isChecked(), qty, p);
			if( qty < minQty) {
				Toast.makeText(PriceCountEx.this, R.string.qty_below_min, Toast.LENGTH_LONG).show();
				return false;
			}
			
			if( dogovor != null ) {			
				int sum = (int)((long)qty * getInputCost(p) / Consts.QTY_SCALE);
				
				for(OrderItem item : order.items) {
					if( item.id.equals(p.id) == false )
						sum += (int)(((long)item.qty * item.cost) / Consts.QTY_SCALE);
				}
				
				if( dogovor.checkPay != 0 && dogovor.maxOrder < sum ) {
					runOk = r;
					errMessage = "Заявка не может быть исполнена\n" + dogovor.limitMsg;
					showDialog(ERROR_ORDER);
					return false;
				}
			}
		}
		return super.isInputValid(r);
	}
	
	@Override
	protected int getQtyInPack(Price p) {
		
		if( selected == null ) {
			String scode = "";
			
			List<UnitItem> units = ((PriceEx)p).units;
			if( document != null && document instanceof OrderImpl ) {
				OrderItemEx oi = (OrderItemEx) ((OrderImpl)document).findItem(p.id);
				if( oi != null )
					scode = oi.unit;
			} else if( units.size() > 0 ) {
				scode = units.get(0).id;
			}
			
			for(UnitItem ui : units ) {
				UnitEx uex = new UnitEx(ui);
				
				if(ui.hide == 1 && !ui.id.equals(scode)) //Добавить в список, если в заявке
					continue;
				
				if(ui.id.compareTo(scode) == 0 )
					selected = uex;
	
				this.units.add(uex);
			}
		}
		
		if( selected != null )
			return selected.inpack;

		return super.getQtyInPack(p);
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
	
	@Override protected boolean getStartInPack() { return true; }

}
