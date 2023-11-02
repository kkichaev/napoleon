package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.AgentActivityHitching;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentRcv;
import com.grsoft.dataobjects.UserPinData;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.exception.RuntimeException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	private static final String OPEN_BLOCKED = "open_blocked";
	boolean blocked = false;
	
	public static void openBlocked(Context c) {
		Intent i = new Intent(c, UpdateDBEx.class);
		i.putExtra(OPEN_BLOCKED, true);
		c.startActivity(i);
	}

	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> ret = super.getExported();
		ret.add(new AgentActivityHitching());
		return ret;
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(AgentRcv.class, "AgentsRcv"));
		return ret;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		blocked = (b==null) ? false : b.getBoolean(OPEN_BLOCKED, false);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(OPEN_BLOCKED, blocked);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && blocked){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cb = (CheckBox) findViewById(R.id.cbRemains);
		cb.setChecked(false);
		
		cb = (CheckBox) findViewById(R.id.cbDebt);
		cb.setChecked(true);
	}
	
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = super.getRestoreHitching();
		result.add(new DocumentRestore(IncassDoc.instance()));
		return result;
	}
	
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitchingEx();
	}

	@Override
	protected void postSync(Boolean result) {
		if(result) {
			UserPinData upd = UserPinData.get();
			if(upd.resetPin > 0) {
				Registration.open(this);
				finish();
			}
		}
	}
}
