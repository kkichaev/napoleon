package com.grsoft.napoleon.chart;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.AgentAKBData;
import com.grsoft.dataobjects.AgentOrderSum;
import com.grsoft.dataobjects.AgentTopSale;
import com.grsoft.dataobjects.AgentVisit;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.util.ProgressManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class ChartSync extends NetworkAsyncTask {
	public static final String START_SYNC = "start_sync";
	public static final String FINISH_SYNC = "start_sync";
	public static final String LAST_SYNC_TIME = "last_sync_time";
	
	Context context;
	
	public ChartSync(Context context) {
		super(new ProgressManager(context));
		((ProgressManager)this.progressHelper).setUpdateProcess(this);
		
		this.context = context;
	}

	static class TopSelParam extends DataObject{
		public Date start;
	}
	
	static class OrderSumParam extends DataObject{
		public Date start;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		onUpdate(UpdateStatus.BEGIN_UPDATE, 0);

		TopSelParam arg1 = new TopSelParam();
		arg1.start = getTopSelDate();
		
		List<Hitching> rcv = new ArrayList<Hitching>();
		//rcv.add(new RcvNewHitching(AgentTopSale.class));
		rcv.add(new RcvNewHitching(AgentVisit.class));
		rcv.add(new RcvNewHitching(AgentOrderSum.class));
		rcv.add(new RcvNewHitching(AgentAKBData.class));
		ReportHitching tsr = new ReportHitching("chartrequest", arg1, rcv);
		
		List<Hitching> rcvHitch = new ArrayList<Hitching>();
		rcvHitch.add(tsr);
		
		ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(rcvHitch);
		dataBaseUpdater.setUpdateProcessListenet(this);

		Config config = ConfigManager.getConfig();
		LoginData ld = new LoginData(config.login, config.passw, "", context, config.uuid, config.serverCode);
		
		boolean res = false;
		String errMessage = context.getString(R.string.recieved_error);
		
		try {
			res = dataBaseUpdater.update(context, ld, false); 
			if(!res)
				errMessage = dataBaseUpdater.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			errMessage = e.getMessage();
		}

		if (!res)
			showErrorMsg(errMessage, context);
		else
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
		
		return res;

	}

	private Date getTopSelDate() {
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		if (cfg.chartPeriod == 0)
			c.set(Calendar.DATE, 1);
		else if (cfg.chartPeriod == 1)
			c.add(Calendar.DATE, -7);
		else if (cfg.chartPeriod == 2)
			c.add(Calendar.DATE, -30);
		
		return c.getTime();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		context.sendBroadcast(new Intent(START_SYNC));
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = pref.edit();
		ed.putLong(LAST_SYNC_TIME, Calendar.getInstance().getTimeInMillis());
		ed.commit();

		context.sendBroadcast(new Intent(FINISH_SYNC));
	}
}
