package com.grsoft.manager.view;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DivisionAgent;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.manager.AgentData;
import com.grsoft.manager.AgentRoute;
import com.grsoft.manager.R;
import com.grsoft.manager.ReportData;
import com.grsoft.manager.SyncDetail;
import com.grsoft.manager.UpdateCtrl;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ProgressDrawable;
import com.grsoft.network.LoginData;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class RowItem {
	public ManagerAgent agent;
	protected int level = 0;

	public static List<RowItem> loadAgents() {

		List<RowItem> ret = new ArrayList<RowItem>();

		Date checkDate = new Date(70, 1, 5);

		ManagerAgent a = new ManagerAgent();
		DbWriter.checkDBTable(a.getClass());
		String table = DataObjectInfo.getInstance().getTableName(a.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(a, table, null);
		while (bdo) {
			if (a.date.before(checkDate))
				a.date = null;
			RowItem ai = new RowItem();
			ai.agent = (ManagerAgent) a.clone();
			ret.add(ai);
			bdo = r.selectNext(a);
		}
		r.close();

		return ret;
	}

	public static HashMap<String, RowItem> loadAgentsMap() {
		HashMap<String, RowItem> ret = new HashMap<String, RowItem>();
		List<RowItem> agents = loadAgents();

		for (int i = 0; i < agents.size(); i++) {
			RowItem ai = agents.get(i);
			ret.put(ai.agent.id, ai);
		}

		return ret;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int new_level) {
		level = new_level;
	}

	public View getView(Context context, int pos, ReportData data) {
		View view = View.inflate(context, getViewId(), null);
		adjustView(context, data, view);
		setBackground(pos, view);

		return view;
	}

	protected void setBackground(int pos, View view) {
		view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector
				: R.drawable.even_row_selector);
	}

	protected int getViewId() {
		return R.layout.agent_row;
	}

	protected void adjustView(Context context, ReportData data, View view) {
		TextView tv;
		tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(agent.name);

		tv = (TextView) view.findViewById(R.id.tvPhone);
		tv.setText(agent.phone);

		tv = (TextView) view.findViewById(R.id.agentSync);
		if (agent.date != null) {
			tv.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
					DateFormat.MEDIUM, Locale.getDefault()).format(agent.date));
			tv.setVisibility(View.VISIBLE);
		} else
			tv.setVisibility(View.INVISIBLE);

		tv = (TextView) view.findViewById(R.id.tvDistance);

		AgentData ad = data.getAgentData(agent.id);

		String distance = "", orders = "", visits = "", sum = "", progress = "";
		if (ad != null) {

			if (ad.distance > 0){
				final int KILOMETER = Consts.DISTANCE_SCALE;
				distance = ad.distance < KILOMETER ? dstTextM(ad) : dstTextKm(ad);
			}

			orders = Integer.toString(ad.orders);
			visits = Integer.toString(ad.visits);
			sum = Util.IntToScaleStr(ad.sum, Consts.SUM_SCALE, Util.DEC_DELIM,
					false);

			progress = ad.progress + "%";
		}

		((TextView) view.findViewById(R.id.tvDistance)).setText(distance);
		((TextView) view.findViewById(R.id.tvOrders)).setText(orders);
		((TextView) view.findViewById(R.id.tvVisits)).setText(visits);
		((TextView) view.findViewById(R.id.tvSum)).setText(sum);

		tv = (TextView) view.findViewById(R.id.tvProgress);
		tv.setText(progress);
		if (ad != null)
			tv.setBackgroundDrawable(new ProgressDrawable(ad.progress));
		else
			tv.setBackgroundDrawable(null);
	}

	public String dstTextKm(AgentData ad) {
		return Integer.toString((int) Math.floor(ad.distance	/ Consts.DISTANCE_SCALE));
	}

	@SuppressLint("DefaultLocale")
	public String dstTextM(AgentData ad) {
		float dst = ad.distance / Consts.DISTANCE_SCALE;
		return String.format("%.2f", dst);
	}

	public void runNapoleon(Context context) {
		CfgMgr cfg = (CfgMgr) ConfigManager.getConfig();
		LoginData ld = new LoginData(cfg.login, cfg.passw, cfg.impersonate, context);
		StringBuilder sb = new StringBuilder();
		sb.append(cfg.login).append(";").append(cfg.passw).append(";")
				.append(cfg.address).append(";").append(cfg.address2)
				.append(";").append(cfg.port).append(";").append(agent.id).append(";").append(ld.getDuration());

		Intent intent = new Intent("com.grsoft.napoleon.StartFromManager");
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		intent.setType("text/plain");
		context.sendBroadcast(intent);
	}

	public void adjustMenu(ContextMenu menu) {

	}

	public List<DivisionAgent> getAgents() {
		List<DivisionAgent> result = new ArrayList<DivisionAgent>();
		DivisionAgent da = new DivisionAgent();
		da.id = agent.id;
		result.add(da);

		return result;
	}

	public void open(final Activity activity, final Date date) {
		SyncDetail.sync(activity, createUpdateCtrl(activity, date), agent.id, date, true);
	}

	private UpdateCtrl createUpdateCtrl(final Activity activity, final Date date) {
		return new UpdateCtrl() {
			@Override public void onFinish(boolean result) {
				if( result )
					AgentRoute.open(activity, agent.id, date);
			}
			@Override public void updateCtrl(boolean enabled) {} };
	}
	
	public String getTitle(){
		return agent.name;
	}
}

