package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.NoDiscount;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	int priceCost;
	int discount;
	
	Boolean noDiscount = null;

	@Override
	protected boolean canChangeCost() {
		if( noDiscount == null ) {
			String table = DataObjectInfo.getInstance().getTableName(NoDiscount.class);
			String where = "id='" + price.getData().id + "'";
			DbReader r = new DbReader();
			NoDiscount nd = new NoDiscount();
			noDiscount = r.select(nd, table, where);
			r.close();			
		}
		return !noDiscount;
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		cbPackets.setVisibility(View.GONE);
		if( getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID) == ExtrasConst.INVALID_ROWID){
			@SuppressWarnings("unchecked")
			CostStrategy costStrategy = CostStrategy.getInstance((Class<? extends Document<?>>) ((document == null)	? null : document.getClass())); 
			priceCost = costStrategy.getCostInt(price.getData(), document, WarehouseEx.GetSumTypeForNonDoc());
		}else
			priceCost = priceVal;

		TextView tv;
		tv = (TextView)findViewById(R.id.tvCost);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvDiscount);
		if( canChangeCost() ) {
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public int getValue() { return -discount; }
						@Override
						public void applayInput(int value, Object... params) {
							int newDiscount = -value;//discount - value;
							int newPreciVal = priceCost - (int)(((long)priceCost * newDiscount
									+ Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
							
							if(newPreciVal > ((PriceEx)price.getData()).minCost)
							{
								discount = newDiscount;
								priceVal = newPreciVal;
								updateCost();
								updateDicsount();
								updateSumTextView();
							} else
								minPriceExceedToast();
						}});
				}
			});
		}
		
		if(document != null && document instanceof OrderImplBase<?> &&  document.getRowid() != ExtrasConst.INVALID_ID ) {
			OrgImpl org = new OrgImpl(); 
			OrderImplBase<?> o = (OrderImplBase<?>) document;
			DataObject dobj = o.findItem(price.getData().id);
			if( dobj != null && dobj instanceof OrderItemEx) {
				OrderItemEx item = (OrderItemEx)dobj;
				discount = item.discount;
				int cost = item.cost;
				if( priceVal != cost ) {
					priceVal = cost;
					updateCost();
					updateSumTextView();
				}
			}
			
			org.close();
		}
		updateDicsount();
		
		tv = (TextView) findViewById(R.id.tvMinCost);
		tv.setText(Util.IntToScaleStr(((PriceEx)price.getData()).minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		if(((PriceEx)price.getData()).minCost < newCost){
			int checkDiscount = (int)((long)(priceCost - newCost) * 10000 / priceCost);
			discount = checkDiscount;
			updateDicsount();
			super.onChangeCost(newCost);
		}else
			minPriceExceedToast();
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
		if( canChangeCost() ) {
			SpannableString ss = new SpannableString(value);
			ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
			tv.setTextColor(Color.BLUE);
			tv.setText(ss);
		} else {
			tv.setText(value);
			tv.setTextColor(Color.BLACK);
		}
	}

	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		
		DataObject item = ((OrderImplBase<?>)document).findItem(price.getData().id);	
		
		if( item != null && item instanceof OrderItemEx) {
			((OrderItemEx)item).discount = discount;
			document.write();
		}

		return ret;
	}

	protected void minPriceExceedToast() {
		Toast.makeText(this, 
				R.string.min_price_exceed, Toast.LENGTH_SHORT).show();
	}
}
