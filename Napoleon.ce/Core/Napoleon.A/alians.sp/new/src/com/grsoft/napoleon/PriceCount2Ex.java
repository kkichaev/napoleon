package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class PriceCount2Ex extends PriceCountEx {
	private static final String GOTO_WAREHOUSE = "goto_warehouse";
	private TextView tvDisc;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc, boolean gw) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(GOTO_WAREHOUSE, gw);

		context.startActivity(i);		
	}

	private boolean goto_warehouse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		goto_warehouse = getIntent().getExtras().getBoolean(GOTO_WAREHOUSE);
		tvDisc = (TextView) findViewById(R.id.tvDisc);
		int disc = ((CostStrategy2Ex) CostStrategy2Ex.defaultInstance).getDiscount(price.getData(), document);
		tvDisc.setText(Util.IntToScaleStr(disc, Consts.SUM_SCALE) + " %");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing() && goto_warehouse)
			WarehouseEx.open(this, document, price.getData().folderID, false);
	}
	
	@Override
	protected void hideItemImage() {
		ivPresent.setVisibility(View.VISIBLE);
		ivPresent.setImageResource(android.R.color.transparent);
	}
	
	@Override
	protected void showItemImage() {
		hideItemImage();
		super.showItemImage();
	}
	
	@Override
	protected void setItemImage(String fileName) {
		try{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			Bitmap src = BitmapFactory.decodeFile(fileName, opt);
			ivPresent.setImageDrawable(new BitmapDrawable(src));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
