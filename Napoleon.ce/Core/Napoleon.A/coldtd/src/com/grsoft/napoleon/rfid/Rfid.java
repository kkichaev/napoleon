package com.grsoft.napoleon.rfid;

import android.content.Context;
import android.util.Log;

import com.senter.support.openapi.StUhf;
import com.senter.support.openapi.StUhf.InterrogatorModel;
import com.senter.support.openapi.StUhf.InterrogatorModelA;
import com.senter.support.openapi.StUhf.InterrogatorModelB;
import com.senter.support.openapi.StUhf.InterrogatorModelC;
import com.senter.support.openapi.StUhf.OnNewUiiInventoried;
import com.senter.support.openapi.StUhf.UII;

public final class Rfid {
	public static final String TAG="RFID";
	static StUhf rfid;
	
	public static StUhf getRfid() {
		if (rfid == null) {
			StUhf rf = null;
			rf = StUhf.getUhfInstance();
			if (rf == null) {
				Log.e(TAG, "Rfid instance is null,exit");
				return null;
			}
			
			try {
				rf.init();
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			
			boolean b = rf.init();
			if (b == false) {
				Log.e(TAG, "cannot init rfid");
				return null;
			}
			
			InterrogatorModel model= rf.getInterrogatorModel();
			switch (model) {
				case InterrogatorModelA:
				case InterrogatorModelB:
				case InterrogatorModelC:
					rfid = rf;
					break;
			}
		}
		return rfid;		
	}
	
	public static boolean startInventory(Context context, final OnNewUiiInventoried handler) {
		if( getRfid() == null )
			return false;

		Config cfg = Config.load(context);
		
		boolean ret = false;
		InterrogatorModel model= rfid.getInterrogatorModel();
		switch (model) {
		case InterrogatorModelA:
			ret = rfid.getInterrogatorAs(InterrogatorModelA.class).startInventoryWithAntiCollision(cfg.q, handler);
			break;
		case InterrogatorModelB:
			ret = rfid.getInterrogatorAs(InterrogatorModelB.class).startInventoryWithAntiCollision(cfg.q, handler);
			break;
		case InterrogatorModelC:
			ret = rfid.getInterrogatorAs(InterrogatorModelC.class).startInventorySingleTag(new InterrogatorModelC.UmcOnNewUiiInventoried() {
				@Override public void onEnd(int arg0) {}
				@Override public void onNewTagInventoried(UII arg0) { handler.onNewUiiReceived(arg0);}
				
			});
			break;
		}
		
		return ret;
	}

	public static boolean stop() {
		if (rfid != null) {
			for (int i = 0; i < 3; i++) {
				if (rfid.stopOperation()) {
					Log.e(TAG, "stopOperation ok");
					return true;
				}
			}
			Log.e(TAG, "stopOperation fail");
			return false;
		}
		return true;
	}
	
	public static void close() {
		if( rfid != null )
			rfid.uninit();
		rfid = null;
	}
}
