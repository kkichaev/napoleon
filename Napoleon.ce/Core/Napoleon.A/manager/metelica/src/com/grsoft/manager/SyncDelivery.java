package com.grsoft.manager;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.ManagerPrice;
import com.grsoft.dataobjects.PaymentEx;


public class SyncDelivery {
	private UpdateCtrl updctrl;
	private Context context;
	
	public SyncDelivery(Context context, UpdateCtrl updctrl){
		this.context = context;
		this.updctrl = updctrl;
	}
	
	public void start(String id, String userid){
		final String MODULE_NAME = "debtlist";
		
		List<Hitching> ret = new ArrayList<Hitching>();
		List<Hitching> repResult = new ArrayList<Hitching>();
		repResult.add(new RcvNewHitching(DeliveryEx.class, "Delivery"));
		repResult.add(new RcvNewHitching(PaymentEx.class, "Payment"));
		repResult.add(new Hitching(ManagerPrice.class));
		ret.add(new ReportHitching(MODULE_NAME, createParam(id, userid), repResult));
		UpdateProcess upp = new UpdateProcess((Activity) context, updctrl, ret);
		upp.execute((Void[]) null);
	}
	
	private DataObject createParam(String id, String userid) {
		RequestParam result = new RequestParam();
		result.id = id;
		result.userid = userid;
		return result;
	}
	
	class RequestParam extends DataObject{
		public String id = "";
		public String userid = "";
	}
}
