package com.grsoft.dataobjects;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.manager.AgentData;
import com.grsoft.napoleon.manager.R;
import com.grsoft.napoleon.manager.ReportData;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ProgressDrawable;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

@TableInfo(name="AgentInfo",keyFields="id")
public class AgentInfo extends DataObject  {
	
	public String id;
	public String name;
	public String phone;
	public String login;
	public String password;
	public Date date = null;
	
	protected int level = 0;
	
	public static List<AgentInfo> loadAgents() {
		
		List<AgentInfo> ret = new ArrayList<AgentInfo>();
		
		Date checkDate = new Date(70, 1, 5);
		
		AgentInfo a = new AgentInfo();
		DbWriter.checkDBTable(a.getClass());
		String table = DataObjectInfo.getInstance().getTableName(a.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(a, table, null);
		while(bdo) {
			if( a.date.before(checkDate) )
				a.date = null;
			ret.add(a);
			a = new AgentInfo();
			bdo = r.selectNext(a);
		}
		r.close();

		return ret;
	}
	
	public static HashMap<String,AgentInfo> loadAgentsMap() {
		HashMap<String,AgentInfo> ret = new HashMap<String,AgentInfo>();
		List<AgentInfo> agents = loadAgents();
		
		for(int i = 0; i < agents.size(); i++ ) {
			AgentInfo ai = agents.get(i); 
			ret.put(ai.id,ai); 
		}
		
		return ret;		
	}
	
	public int getLevel() { return level; }
	public void setLevel(int new_level) { level = new_level; }
	
	public View getView(Context context, int pos, ReportData data){
		View view = View.inflate(context, getViewId(), null);
//		int width = 20;
//		view.setPadding(width*getLevel(),
//				view.getPaddingTop(), 
//				view.getPaddingRight(),
//				view.getPaddingBottom());			
		
		adjustView(context, data, view);
		
		view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector : R.drawable.list_grey_selector );
		
		return view;
	}

	protected int getViewId() {
		return R.layout.agent_row;
	}

	protected void adjustView(Context context, ReportData data, View view) {
		TextView tv;
		tv = (TextView)view.findViewById(R.id.tvName);
		tv.setText(name);			
		
		tv = (TextView)view.findViewById(R.id.tvPhone);
		tv.setText(phone);
		
		tv = (TextView)view.findViewById(R.id.agentSync);
		if(date != null ){ 
			tv.setText(DateFormat.getDateTimeInstance(
					DateFormat.MEDIUM,DateFormat.MEDIUM,Locale.getDefault()).format(date));
			tv.setVisibility(View.VISIBLE);
		}else
			tv.setVisibility(View.INVISIBLE);
		
		tv = (TextView)view.findViewById(R.id.tvDistance);
		
		AgentData ad = data.getAgentData(id);
		
		String distance = "", orders = "", visits = "", sum = "", progress = "";
		if( ad != null ) {
			distance = Integer.toString(ad.distance);
			
			orders = Integer.toString(ad.orders);
			visits = Integer.toString(ad.visits);
			sum = Util.IntToScaleStr(ad.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			
			progress = Util.IntToScaleStr(ad.progress * 100, Consts.QTY_SCALE, Util.DEC_DELIM, true) + "%";
		}
		
		((TextView)view.findViewById(R.id.tvDistance)).setText(distance);
		((TextView)view.findViewById(R.id.tvOrders)).setText(orders);
		((TextView)view.findViewById(R.id.tvVisits)).setText(visits);
		((TextView)view.findViewById(R.id.tvSum)).setText(sum);
		
		tv = (TextView)view.findViewById(R.id.tvProgress); 
		tv.setText(progress);
		if( ad != null )
			tv.setBackgroundDrawable(new ProgressDrawable(ad.progress));
		else
			tv.setBackgroundDrawable(null);
	}
	
	public void runNapoleon(Context context){
		CfgMgr cfg = (CfgMgr) ConfigManager.getConfig();
		StringBuilder sb = new StringBuilder();
		sb.append(cfg.login).append(";")
			.append(cfg.passw).append(";")
			.append(cfg.address).append(";")
			.append(cfg.address2).append(";")
			.append(cfg.port).append(";")
			.append(id);
		

		Intent intent = new Intent("com.grsoft.napoleon.StartFromManager");
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		intent.setType("text/plain");
		context.sendBroadcast(intent);
	}

	public void adjustMenu(ContextMenu menu) {
		
	}
	
	public List<DivisionAgent> getAgents(){
		List<DivisionAgent> result = new ArrayList<DivisionAgent>();
		DivisionAgent da = new DivisionAgent(); 
		da.id = id;
		result.add(da);
		
		return result;
	}
}
