package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.CostData;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements UpdateQtyHandler {
//	int discount;
	int priceCost;
//	int maxDiscount;
	int minCost;
	boolean costChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
//			TextView tv;
//			tv = (TextView) findViewById(R.id.tvDiscount);
//			tv.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
//						@Override
//						public int getValue() { return -discount; }
//	
//						@Override
//						public void applayInput(int value, Object... params) {
//							if(-value > maxDiscount) {
//								Toast.makeText(PriceCountEx.this, "Скидка больше максимальной", Toast.LENGTH_SHORT).show();
//								return;
//							}
//							discount = -value;
//	
//							updateCost();
//							updateDicsount();
//							updateSumTextView();
//						}
//					}, Consts.SUM_SCALE, false, getString(R.string.cost_changing), DiscountInputDlg.Type.OnlyDiscount);
//				}
//			});
//			findViewById(R.id.trDsc).setVisibility(View.VISIBLE);
//			findViewById(R.id.trMaxDsc).setVisibility(View.VISIBLE);
			
//		}
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		if(newCost < minCost) {
			Toast.makeText(this, "Цена меньше минимальной", Toast.LENGTH_SHORT).show();
			return;			
		}
//		int dsc = (int)((1.0 - (double)newCost / priceCost) * 100.0 * Consts.SUM_SCALE + 0.5);
//		if(dsc > maxDiscount) {
//			Toast.makeText(this, "Скидка больше максимальной", Toast.LENGTH_SHORT).show();
//			return;			
//		}
//		discount = dsc;
//		updateDicsount();
		costChanged = true;
		super.onChangeCost(newCost);
	}
	
//	@Override
//	protected int getInputCost(Price p) {
//		if(priceCost == 0)
//			return super.getInputCost(p);
//		
//		return CostStrategy.costWithDiscount(priceCost, discount, Consts.SUM_SCALE); 
//	}
	
	@Override protected boolean canChangeCost() { return minCost != 0; }
	
	@Override
	protected void refreshData() {
		costChanged = false;
		
		if (document instanceof OrderImpl) {
			((OrderImpl) document).setUpdateQtyHandler(null);
		}
		
		PriceEx p = (PriceEx) price.getData();

		OrderImpl o = null;

		if(document instanceof OrderImpl) {
			o = (OrderImpl) document;
			
			@SuppressWarnings("unused")
			int cost = ((CostManagerImplEx)Features.COST_MANAGER).getCost(p.id, o.getData().sumType);
			CostData cd = ((CostManagerImplEx)Features.COST_MANAGER).getCurCost();
			minCost = cd.minCost;
		}
		CostStrategyEx cse = (CostStrategyEx)CostStrategy.defaultInstance; 
		int ac = cse.getActionCost(p, document);
		if(ac != 0) {
			minCost = 0; // can't change ac
		}
		
		super.refreshData();
				
		int fq = p.freeQty;
		priceCost = cse.getPriceCost(p, document);
		
		int actVsbl = View.GONE;
		if(ac != 0) {
			actVsbl = View.VISIBLE;
			super.onChangeCost(ac);

			((TextView)findViewById(R.id.tvPrice)).setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			((TextView)findViewById(R.id.tvActPrice)).setText(Util.IntToScaleStr(ac, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
			int dsc = (int)(priceCost == 0 ? 0 : 1000 - priceVal * 1000 / priceCost);
			((TextView)findViewById(R.id.tvActPrc)).setText(Util.IntToScaleStr(dsc, 10, Util.DEC_DELIM, false));
			
			findViewById(R.id.tvPrice).setOnClickListener(null);
		}
		findViewById(R.id.trActCost).setVisibility(actVsbl);
		findViewById(R.id.trActPrc).setVisibility(actVsbl);
		
		if (o != null) {			
			OrderItemEx oe = (OrderItemEx) o.findItem(p.id);
			int whi = ((OrderEx)o.getData()).whIndex;
			if(whi != 0 && whi <= p.whQty.size()) {
				fq = ((PriceWhData)p.whQty.get(whi-1)).freeQty;
			}

			if (oe != null) {
//				discount = oe.discount;
				if (priceVal != oe.cost) {
					priceVal = oe.cost;
					//updateCost();
					updateSumTextView();
				}
			} else {
//				priceCost = priceVal;
//				discount = 0;
			}
//			maxDiscount = noDiscount ? 0 : findMaxDiscount();
//			updateDicsount();
//			((TextView)findViewById(R.id.tvMaxDiscount)).setText(Util.IntToScaleStr(maxDiscount, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		}
		((TextView)findViewById(R.id.tvFreeQty)).setText(Util.IntToScaleStr(fq, Consts.QTY_SCALE, Util.DEC_DELIM, true));
		
		if( canChangeCost() ) {
			findViewById(R.id.tvPrice).setOnClickListener(new View.OnClickListener() {				
				@Override public void onClick(View v) { doCostChange(); }
			});
		}
		
		updateCost();
		
		if (document instanceof OrderImpl) {
			((OrderImpl) document).setUpdateQtyHandler(this);
		}
	}

//
//	private int findMaxDiscount() {
//		int dsc = 0;
//		FolderTree ft = new FolderTree();
//		
//		OrgImpl oi = new OrgImpl();
//		OrgEx oe = (OrgEx) oi.getData();
//		oe.id = document.getId();
//		
//		boolean readed = oi.read();
//		oi.close();
//		
//		if( readed ) {
//			ft.load();
//			int fidx = ft.findFolder(price.getData().folderID);
//			Folder cf = fidx < 0 ? null : ft.get(fidx);
//			boolean finded = false;
//			while(cf != null && !finded) {
//				for(OrgDiscount od : oe.discounts) {
//					if(od.fid.equals(cf.fid)) {
//						finded = true;
//						dsc = od.discount;
//						break;
//					}
//				}
//				cf = ft.getParent(cf);
//			}
//		}
//		return dsc;
//	}
//
//	private void updateDicsount() {
//		int val = discount;
//		String label = "скидка,%";
//		if (val < 0) {
//			label = "наценка,%";
//			val = -val;
//		}
//		((TextView) findViewById(R.id.tvDiscountLabel)).setText(label);
//
//		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE, Util.DEC_DELIM, false);
//		TextView tv = (TextView) findViewById(R.id.tvDiscount);
//		SpannableString ss = new SpannableString(value);
//		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
//		tv.setTextColor(Color.BLUE);
//		tv.setText(ss);
//	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx)item;
		oie.manualCost = costChanged ? 1 : 0;
//		oie.discount = discount;
//		oie.costWODsc = priceCost;
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(cbPackets.isChecked())
			return true;
		
//		int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
		int quant = ((PriceEx)price.getData()).quant;
		return quant == 0 ? true : qtyItems % quant == 0;
	}
	
	@Override
	protected void invalidInputValueHandler() {
		Toast.makeText(this, getString(R.string.quant_alert, 
				Util.IntToScaleStr(((PriceEx)price.getData()).quant, Consts.QTY_SCALE)),
				Toast.LENGTH_SHORT).show();
	}
}
