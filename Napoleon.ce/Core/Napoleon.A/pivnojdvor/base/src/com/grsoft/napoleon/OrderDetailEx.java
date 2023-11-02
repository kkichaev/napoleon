package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.BonusImpl;

import android.view.View;
import android.widget.Toast;

public class OrderDetailEx extends OrderDetail {
	
	HashMap<String, BonusDef> bonuses = new HashMap<String, BonusDef>(); 
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
		findViewById(R.id.btnBonus).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { openBonus(); }
		});
	}

	protected void openBonus() {
		BonusImpl bi = BonusImpl.fromOrder(doc, bonuses);
		if(bi == null) {
			Toast.makeText(OrderDetailEx.this, "”слови€ бонуса не выполн€ютс€", Toast.LENGTH_SHORT).show();
		} else
			bi.open(this);
	}

	boolean canMakeBonus() {
		for(OrderItem oi : doc.getData().items){
			BonusDef bi = bonuses.get(oi.id);
			if( bi != null && oi.qty >= bi.qty )
				return true;
			}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		BonusImpl.fromOrder(doc, bonuses);
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();

		bonuses = BonusDefImpl.getActiveBonuses(doc.getDate());
		
		View actionView = findViewById(R.id.btnBonus);
		if( doc.isEditable() && canMakeBonus() ) {
			View v = findViewById(R.id.llFocus);
			v.setVisibility(View.VISIBLE);
			if( haveFocusedGroup() == false ) {
				findViewById(R.id.btnFocus).setVisibility(View.GONE);
			}
			actionView.setVisibility(View.VISIBLE);
		} else {
			actionView.setVisibility(View.GONE);
		}
	}
}
