package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;

public class BonusPriceCount extends PriceCount {
	private TextView tvOrderSum;
	private TextView tvDiscount;
	private int maxSum = -1;
	private int discount;
	private int sumDoc;

	public static void open(Context context, long priceRoid,
			DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, BonusPriceCount.class);

		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);
	}

	@Override
	protected void makeSaleHistory(Price p) {
	}

	@Override
	protected boolean isComplexSalesHistory() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tvOrderSum = (TextView) findViewById(R.id.tvSumOrder);
		tvDiscount = (TextView) findViewById(R.id.tvDiscount);

		Bonus bonus = (Bonus) document.getData();
		tvOrderSum
				.setText(Util.IntToScaleStr(bonus.ordersum, Consts.SUM_SCALE));

		ConfigImpl cfgimpl = new ConfigImpl();
		StringBuilder sb = new StringBuilder();

		final int DEFAULT_DISCOUNT = 30 * Consts.SUM_SCALE;
		discount = DEFAULT_DISCOUNT;

		if (cfgimpl.getValue(sb, "discount"))
			try {
				discount = Integer.parseInt(sb.toString()) * Consts.SUM_SCALE;
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		maxSum = /*bonus.ordersum - */(int)(((long)bonus.ordersum * 
				discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));

		tvDiscount.setText(String.format("%s (%s)", Util.IntToScaleStr(discount, Consts.SUM_SCALE), 
				Util.IntToScaleStr(maxSum, Consts.SUM_SCALE)));
		
		
		for (OrderItem orderItem: ((BonusImpl)document).getData().items)
			if(!orderItem.id.equals(price.getData().id))
				sumDoc += FPOperation.itemMul(orderItem.cost, orderItem.qty, Consts.QTY_SCALE);
		
		updateSumTextView();
	}

	protected void updateSumTextView()
	{
		int sumVal = (int) getSumValue();
		
		try
		{
			tvSum.setText(String.format("%s (%s)", Util.IntToScaleWStr(sumVal, Consts.SUM_SCALE,
					Consts.PRICE_DEC_WIDTH, false),
					Util.IntToScaleWStr(sumVal + sumDoc, Consts.SUM_SCALE,
							Consts.PRICE_DEC_WIDTH, false)));
			
		}
		catch(NumberFormatException  e){}
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.bonuspricecount;
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		int sumVal = (int) getSumValue();
		
		if(sumVal + sumDoc > maxSum){
			Toast.makeText(this, R.string.max_discount_override, Toast.LENGTH_SHORT).show();
			return false;
		}
			
		return super.isInputValid(r);
	}
}
