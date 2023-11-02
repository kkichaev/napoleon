package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ReqOrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailEx extends OrderDetail {
	private TextView tvInfo;
	private boolean isAllowToBack = true;
	private HashMap<String, BonusDef> bonuses = new HashMap<String, BonusDef>(); 
	private int debet = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvInfo = (TextView) findViewById(R.id.tvInfo);
		DeliveryInfo deliveryInfo = DeliveryInfo.collectDelivery(org.getData().id);
		debet = deliveryInfo.sum;
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
		findViewById(R.id.btnBonus).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { openBonus(); }
		});
	}

	@Override
	public void send() {
		String objName = ((OrderEx)doc.getData()).needDecision() ? ReqOrderDoc.instance().getObjectName() :
				docType.getObjectName();
		new DocumentSender(OrderDetailEx.this, btnSend,
				objName, doc,
				doc.getRowid(), this).execute((Void[])null);
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
		
		if(isAllowToBack || doc.sum() == 0)
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

		checkMinSum();
	}

	@Override
	protected void deleteItem(OrderItem orderItem) {
		super.deleteItem(orderItem);
		checkMinSum();
	}

	private void checkMinSum() {
		int ms = ((OrgEx)org.getData()).minSum;
		long s = doc.sum();

		tvInfo.setVisibility(View.GONE);
		isAllowToBack = true;
		btnSend.setEnabled(true);

		if (doc.getData() instanceof OrderEx) {
			boolean b = ((OrderEx) doc.getData()).bonus == 1;

			if (!b && ms != 0 && s > 0 && s < ms) {
				String text = "—умма за€вки " + Util.IntToScaleStr(s, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р. меньше " +
						Util.IntToScaleStr(ms, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.";
				tvInfo.setText(text);
				tvInfo.setVisibility(View.VISIBLE);
				isAllowToBack = false;
				btnSend.setEnabled(false);
			} else if (!b && ms != 0 && debet + s >= ((OrgEx) org.getData()).limitsum) {
				tvInfo.setText(getString(R.string.order_creation_reject));
				tvInfo.setVisibility(View.VISIBLE);
				isAllowToBack = false;
				btnSend.setEnabled(false);
			}
		}
	}
}
