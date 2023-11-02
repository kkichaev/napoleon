package com.grsoft.napoleon;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.DiscountInputDlg.Type;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;


public class PriceCountEx extends PriceCount {
	int discount = 0;
	int maxdisc = 0;
	int priceCost;
	GiftHelper giftHelper;
	TextView tvDiscount;
	TextView tvMaxDisc;
//	boolean isAction = false;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		priceCost = ((CostStrategyEx)CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass())).getBaseItemCost(price.getData(),document);
		tvDiscount = (TextView)findViewById(R.id.tvDiscount);
		tvMaxDisc = (TextView) findViewById(R.id.tvMaxDiscount);
		
		if(DocType.getCurDoc() == OrderDoc.instance()){
			tvDiscount.setOnClickListener(discountClick);
			OrderItemEx i = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);
			
			if(i != null) {
				discount = i.disc;
				if(i.IsActionItem()) {
					btnOK.setEnabled(false);
					llKeyboard.setVisibility(View.GONE);
				}
			}
			
			updateCosts();
			DiscountHelper.init(document.getId());
			maxdisc = DiscountHelper.getMaxDiscount(document.getId(), (PriceEx) price.getData());
			tvMaxDisc.setText(Util.IntToScaleStr(maxdisc, Consts.SUM_SCALE,	Util.DEC_DELIM, false));
		}
		
		
	}
	
	private void updateDicsount() {
		int val = discount;
		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE,
				Util.DEC_DELIM, false);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tvDiscount.setTextColor(Color.BLUE);
		tvDiscount.setText(ss);
	}
	
	private void updateCosts(){
		updateDicsount();
		updateCost();
		updateSumTextView();
	}
	@Override
	protected int getInputCost(com.grsoft.dataobjects.Price p) {
		if(DocType.getCurDoc() == OrderDoc.instance())
			return DiscountHelper.calcDisc(priceCost, discount);
		else
			return super.getInputCost(p);
	};
	
	OnClickListener discountClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DiscountInputDlg.open(v.getContext(), new InputNumber() {
				@Override
				public int getValue() {
					return ((OrderImplEx)document).getDisc(price.getData());
				}

				@Override
				public void applayInput(int value, Object... params) {
					value = Math.abs(value);
					
					if(value <= maxdisc){
						discount = value;
						updateCosts();
					}else
						Toast.makeText(PriceCountEx.this, getString(R.string.min_price_exceed, Util.IntToScaleStr(maxdisc, Consts.SUM_SCALE, Util.DEC_DELIM, true)), Toast.LENGTH_SHORT).show();
				}
			}, Consts.SUM_SCALE, false, getString(R.string.cost_changing), Type.OnlyDiscount);
		}
	};
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		TextView tvGift;
		int vsbl = View.GONE;
		tvGift = (TextView) findViewById(R.id.tvGift);
		if(document instanceof OrderImplEx) {
			giftHelper = new GiftHelper((OrderImplEx) document);
			
			StringBuilder sb = new StringBuilder();
			if(giftHelper.loadGift(price.getData().id, sb)) {
				tvGift.setText(getString(R.string.gift, Util.IntToScaleStr(giftHelper.giftQty(), Consts.QTY_SCALE), sb.toString()));
				vsbl = View.VISIBLE;
			} else
				giftHelper = null;
		} else {
			giftHelper = null;
		}
		tvGift.setVisibility(vsbl);
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if( giftHelper != null) {
			int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
			if(giftHelper.needShowGiftDialog(qty)) {
				showDialog(giftHelper.giftDialogId());
				return false;
			}
		}
		return super.isInputValid(r);
	}
	
	@Override
	protected boolean updateQty(boolean inPack, int qty) {
		if(giftHelper != null)
			giftHelper.updateGift(qty);
		
		boolean result = super.updateQty(inPack, qty);
		
		if(DocType.getCurDoc() == OrderDoc.instance())
			((OrderImplEx)document).setDisc(price.getData(), discount, maxdisc);
		
		return result;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog d = (giftHelper == null) ? null : giftHelper.createSelectGiftDialog(this, id, 
				new Runnable() { @Override public void run() { new BtnOKClickListenet().onClick(btnOK); }});
		return d == null ? super.onCreateDialog(id) : d;
	}
}

