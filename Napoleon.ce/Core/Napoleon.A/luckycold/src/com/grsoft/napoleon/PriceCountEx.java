package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PriceEx p = (PriceEx) price.getData();
		int whQty = p.qty;
		String s = getString(R.string.oneitem);
		
		if(p.usePack > 0){
			int inPack = p.qtyInPack;
			
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			
			whQty = (int)((long)whQty * Consts.QTY_SCALE / inPack);
			s = getString(R.string.packitem);
		}
		
		TextView tvQty = (TextView) findViewById(R.id.tvQty);
		String text = String.format("%s %s", Util.IntToScaleStr(whQty, Consts.QTY_SCALE, Util.DEC_DELIM,true), s);
		tvQty.setText(text);
	}
	
	@Override
	protected boolean getStartInPack() {
		PriceEx pe = (PriceEx) price.getData();
		return (pe.usePack > 0);
	}
}
