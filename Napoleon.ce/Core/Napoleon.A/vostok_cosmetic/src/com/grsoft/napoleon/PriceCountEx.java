package com.grsoft.napoleon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	int maxDiscount = 0;
	int discount = 0;
	int cost = 0;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.tvPriceName).setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				StringBuilder url = new StringBuilder();
				ConfigImpl ci = new ConfigImpl();
				if( !ci.getValue(url, "Ѕазовыйјдрес") )
					return false;

				url.append(price.getData().id);
			    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url.toString()));
			    PriceCountEx.this.startActivity(viewIntent);
			    
				return true;
			}
		});
		
//		if( document != null && document instanceof OrderImpl) {
//			findViewById(R.id.tvDiscount).setOnClickListener(new View.OnClickListener() {
//				@Override public void onClick(View v) { enterDiscount(); }
//			});
//		}
	}
	
	@Override
	protected boolean getStartInPack() {
		return super.getStartInPack();
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();

		PriceEx p = (PriceEx) price.getData();
		TextView tv = (TextView)findViewById(R.id.tvBarCode);
		tv.setText(p.barcode);
		
		tv = (TextView)findViewById(R.id.tvItemPrice);
		int sumType = document != null ? document.getSumType() : 0;
		cost = ((p.cost.size() > sumType && sumType >= 0) ? 
				p.cost.get(sumType).cost : 0);
		tv.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		if( document != null && document instanceof OrderImpl ) {
			DiscountData dd = CostStrategyEx.getDiscountData(document.getId(), p.folderID);
			
			OrderImpl o = (OrderImpl) document;
			OrderItemEx oe = (OrderItemEx) o.findItem(p.id);
			
			maxDiscount = dd.maxDiscount;
			discount = -dd.discount;

			if( oe != null )
				discount = -oe.discount;
			
			if( document.isExported() == false && discount > maxDiscount )
				discount = maxDiscount;
			refreshCost();
		}
		
		updateDiscount();
		updateSumTextView();
	}
	
	void refreshCost() {
		priceVal = cost + (int)(((long)cost * (-discount) - Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
		tvPrice.setText(Util.IntToScaleStr(priceVal, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
	
	void enterDiscount() {
		InputNumberDlg.open(this, new InputNumber() {
			@Override public int getValue() { return discount; }
			
			@Override
			public void applayInput(int value, Object... params) {
				if( value > maxDiscount ) {
					MessageBox.show(PriceCountEx.this, "ќшибка", "—кидка больше максимальной - " + 
							Util.IntToScaleStr(maxDiscount, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "%");
				} else {
					discount = value;
					refreshCost();
					updateDiscount();
					updateSumTextView();
				}
			}
		}, Consts.SUM_SCALE, false, "¬ведите скидку");
	}	
	
	void updateDiscount() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		String text = Util.IntToScaleStr(discount, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		
//		if( document != null && document instanceof OrderImpl) {
//			SpannableString ss = new SpannableString(text);
//			ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
//			tv.setText(ss);
//		} else
			tv.setText(text);
	}
	
//	@Override
//	protected boolean updateOrder() {
//		boolean ret = super.updateOrder();
//		if( document != null && document instanceof OrderImpl ) {
//			OrderItemEx oe = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);
//			if( oe != null ) {
//				oe.discount = -discount;
//				document.write();
//			}
//		}
//		return ret;
//	}
}
