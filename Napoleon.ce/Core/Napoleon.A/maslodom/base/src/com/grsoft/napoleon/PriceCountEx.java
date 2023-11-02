package com.grsoft.napoleon;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
//	int maxPrcDD = 5;
	int priceCost;
	int discount;
	int limit;
		
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override protected boolean canChangeCost() { return true; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Features.CAN_CHANGE_COST = false;
		priceCost = CostStrategy.getInstance(null).getItemCost(price.getData(), document);
		//Features.CAN_CHANGE_COST = true;

		if (null !=document) {
		    OrgImpl oi = new OrgImpl();
			OrgEx org = (OrgEx)oi.getData();
			
			org.id = document.getId();
			oi.read();
			oi.close();
			
			FolderImpl fi = new FolderImpl();
			Folder f = fi.getData();
			f.id = price.getData().folderID;
			fi.read();
			fi.close();
			
			 for(OrgDiscount od : org.discounts)
					if(od.id.equals(f.fid)) {
						limit = od.discount;
						break;
					}
			
		}
		
		if(document != null && document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ID ) {
			OrgImpl oi = new OrgImpl();
			OrgEx org = (OrgEx)oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();
			
			FolderImpl fi = new FolderImpl();
			Folder f = fi.getData();
			f.id = price.getData().folderID;
			fi.read();
			fi.close();
			
			limit = 0;
			for(OrgDiscount od : org.discounts)
				if(od.id.equals(f.fid)) {
					limit = od.discount;
					break;
				}


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
							if( -value > limit ) {
								MessageBox.show(PriceCountEx.this, "Ошибка", "Скидка выше максимальной");
								return;
							}
							discount = -value;
							priceVal = priceCost - (int)(((long)priceCost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
							updateCost();
							updateDicsount();
							updateSumTextView();
						}}, Consts.SUM_SCALE, false, getString(R.string.cost_changing), DiscountInputDlg.Type.OnlyDiscount);
				}
			});
			
			OrderImpl o = (OrderImpl)document;
			o.setUpdateQtyHandler(this);
			OrderItemEx oe = (OrderItemEx) o.findItem(price.getData().id);
			if( oe != null ) {
				discount = oe.discount;
				if( priceVal != oe.cost ) {
					priceVal = oe.cost;
					updateSumTextView();
				}
			}
		}
		
		updateCost();
		updateDicsount();
		
		cbPackets.setVisibility(View.GONE);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
	}
	
//	@Override
//	protected void onChangeCost(int newCost) {
//		int checkDiscount = (int)((long)(priceCost - newCost) * 10000 / priceCost);
//		discount = checkDiscount;
//		updateDicsount();
//		super.onChangeCost(newCost);
//	}
//	
	@Override
	protected void onChangeCost(int newCost) {
		int checkDiscount = (int)((long)(priceCost - newCost) * 10000 / priceCost);
		
		if(checkDiscount <= limit){
			discount = checkDiscount;
			updateDicsount();
			super.onChangeCost(newCost);
		}else
			MessageBox.show(PriceCountEx.this, "Ошибка", "Скидка выше максимальной");
	}
	
	private void updateDicsount() {
		int val = discount;
		String label = "скидка,%";
		if( val < 0 ) {
			label = "наценка,%";
			val = -val;
		}
		((TextView)findViewById(R.id.tvDiscountLabel)).setText(label);
		
		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	void makePrcAlert(String message, final Runnable run ) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Вопрос");
		b.setMessage(message);
		b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { run.run(); }
		});
		b.setNegativeButton("Нет", null);
		b.create().show();		
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((OrderItemEx)item).discount = discount;
	}
	
}
