package com.grsoft.napoleon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.Descr;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected String getItemName(Price p) {
		String descr = Descr.read(this, p.id);
		
		if(descr.length() > 0)
			return p.name + "<br>" + descr;
		else 
			return super.getItemName(p);
			
//		if(((PriceEx)p).descr != null && ((PriceEx)p).descr.length() > 0 )
//			return p.name + "\n" + ((PriceEx)p).descr*;
//		return super.getItemName(p);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (((PriceEx)price.getData()).pack > 0)
			cbPackets.setEnabled(false);
		
		boolean editable = document != null && 
				(!document.isExported() || document.isProceeded() && 
				!((document.getData().params & OrderProceededEx.APPROVED) ==  OrderProceededEx.APPROVED));
		
		btnOK.setEnabled(editable);
		llKeyboard.setVisibility(editable ? View.VISIBLE : View.GONE);
		findViewById(R.id.btnComma).setEnabled(false);//.setVisibility(View.GONE);
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected boolean getStartInPack() {
		return ((((PriceEx)price.getData()).pack > 0) || ((CfgNpl)ConfigManager.getConfig()).isPackView);
	}
	
	@Override
	protected boolean updateOrder() {
		boolean result = super.updateOrder();
		document.setExported(false);
		document.unsetProceeded();
		document.write();
		document.close();
		return result;
	}
	
	@Override
	protected void hideItemImage() {
		ivPresent.setVisibility(View.VISIBLE);
		ivPresent.setImageResource(android.R.color.transparent);
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
