package com.grsoft.manager;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ManagerPrice;
import com.grsoft.dataobjects.OrdDlv;
import com.grsoft.dataobjects.OrderPending;

class SyncOrdDlv{
	private UpdateCtrl updctrl;
	private Context context;
	
	public SyncOrdDlv(Context context, UpdateCtrl updctrl){
		this.context = context;
		this.updctrl = updctrl;
	}
	
	public void start(){
		final String MODULE_NAME = "orddlv";
		
		List<Hitching> ret = new ArrayList<Hitching>();
		List<Hitching> repResult = new ArrayList<Hitching>();
		repResult.add(new Hitching(OrdDlv.class));
		repResult.add(new Hitching(OrderPending.class));
		repResult.add(new Hitching(ManagerPrice.class));
		
		ret.add(new ReportHitching(MODULE_NAME, new P(), repResult));
		
		UpdateProcess upp = new UpdateProcess((Activity) context, updctrl, ret){
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				context.sendBroadcast(new Intent(OrderReviewEdit.REFRESH_ACTION));
			}
		};
		upp.execute((Void[]) null);
	}
	
	class P extends DataObject{
		public String dummy = "";
	}
}