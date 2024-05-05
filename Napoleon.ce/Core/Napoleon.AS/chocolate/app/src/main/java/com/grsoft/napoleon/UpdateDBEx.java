package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentRcv;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgTypeCost;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.network.exception.RuntimeException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB{
	private static final String OPEN_BLOCKED = "open_blocked";
	boolean blocked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		blocked = (b==null) ? false : b.getBoolean(OPEN_BLOCKED, false);
		
		super.onCreate(savedInstanceState);
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && blocked){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(OrgDogovor.class, "OrgDogovors"));
		ret.add(new RcvNewHitching(OrgTypeCost.class, "OrgTypeCost"));
		ret.add(new RcvNewHitching(PriceCost.class, "PriceCost"));
		ret.add(new RcvNewHitching(PriceQty.class, "PriceQty"));
		ret.add(new RcvNewHitching(AgentRcv.class, "AgentPda"));
		
		CostStrategyEx.clearCache();
		return ret;
	}
	
	public static void openBlocked(Context c) {
		Intent i = new Intent(c, UpdateDBEx.class);
		i.putExtra(OPEN_BLOCKED, true);
		c.startActivity(i);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(OPEN_BLOCKED, blocked);
	}
}
