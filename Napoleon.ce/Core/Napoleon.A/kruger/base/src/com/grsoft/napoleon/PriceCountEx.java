package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void refreshData() {
		cbPackets.setEnabled(false);
		super.refreshData();

		int bonusVisible = View.GONE;
		if( document instanceof OrderImpl ) {
			HashMap<String, BonusDef> bonuses = BonusDefImpl.getActiveBonuses(document.getDate());
			BonusDef bd = bonuses.get(price.getData().id);
			if( bd != null ) {
				TextView tvBonus = (TextView)findViewById(R.id.tvBonus);
				String text = Util.IntToScaleStr(bd.qty, Consts.QTY_SCALE);
				tvBonus.setText(text);
				bonusVisible = View.VISIBLE;				
			}
		}
		findViewById(R.id.trBonus).setVisibility(bonusVisible);
	}
	
}
