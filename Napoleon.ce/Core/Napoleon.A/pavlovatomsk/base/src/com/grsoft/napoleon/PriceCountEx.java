package com.grsoft.napoleon;

import java.util.HashMap;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	
	private static final int LIMIT_QTY = 10;
	int limit;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void refreshData() {
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
		
		limit = ((PriceEx)price.getData()).limit;
		((TextView)findViewById(R.id.tvLimit)).setText(Util.IntToScaleStr(limit, Consts.QTY_SCALE));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == LIMIT_QTY) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка");
			b.setMessage("Вы не можете отгрузить меньше лимита");
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(document instanceof OrderImpl && ((OrderEx)document.getData()).bonus == 0) {
			int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
			if(qty < limit) {
				showDialog(LIMIT_QTY);
				return false;
			}
		}
		
		return super.isInputValid(r);
	}
	
	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).boxed != 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(document instanceof OrderImpl && ((OrderEx)document.getData()).bonus == 0) {
			cbPackets.setEnabled(((PriceEx)price.getData()).boxed == 0);
		}
	}

	
}
