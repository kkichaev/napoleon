package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.napoleon.manager.AgentData;
import com.grsoft.napoleon.manager.R;
import com.grsoft.napoleon.manager.ReportData;
import com.grsoft.napoleon.util.ProgressDrawable;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DivisionInfo extends AgentInfo {
	
	public String parent;	
	public String description;
	
	public List<DivisionAgent> agents;
	public List<DivisionAgent> allDivisionAgents;
	
	public static List<DivisionInfo> loadDivisions() {
		List<DivisionInfo> ret = new ArrayList<DivisionInfo>();	
		
		Division div = new Division();
		String table = DataObjectInfo.getInstance().getTableName(div.getClass());
		DbWriter.checkDBTable(div.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(div, table, null);
		while(bdo) {
			DivisionInfo div_info = new DivisionInfo();
			div_info.id = Integer.toString(div.id);
			div_info.parent = Integer.toString(div.parent);
			div_info.name = div.name;
			ret.add(div_info);
			div_info.agents = div.agents;
			div_info.allDivisionAgents = new ArrayList<DivisionAgent>();
			div_info.allDivisionAgents.addAll(div_info.agents);
			div = new Division();
			bdo = r.selectNext(div);
		}
		r.close();
		
		return ret;
	}	
	
	public static List<DivisionInfo> getHierarchicalDivisionList() {
		List<DivisionInfo> div_list = loadDivisions();
		
		DivisionInfo root = new DivisionInfo();
		root.id = "-1";
		
		for(int i = 0; i <div_list.size(); i++) {
			if(div_list.get(i).parent.equals("-1")) {
				root = div_list.get(i);
				div_list.remove(root);
				break;
			}
		}
		
		List<DivisionInfo> ret = hierList(div_list, root.id, 1);
		
		for(int i = 0; i < ret.size(); i++ ) {
			root.allDivisionAgents.addAll(ret.get(i).allDivisionAgents);
		}
		
		root.setLevel(0);
		
		if( div_list.size() > 0 )
			ret.add(0, root);
		
		return ret;		
	}
	
	protected static List<DivisionInfo> hierList(List<DivisionInfo> lst, String start_with, int level) {
		
		List<DivisionInfo> subdivs = new ArrayList<DivisionInfo>();		
		
		for(int i = 0; i <lst.size(); i++) {
			DivisionInfo div = lst.get(i); 
			if(div.parent.equals(start_with)) {
				div.setLevel(level);
				subdivs.add(div);
			}
		}
		
		List<DivisionInfo> ret = new ArrayList<DivisionInfo>();
		
		for(int i = 0; i < subdivs.size(); i++) {
			DivisionInfo div = subdivs.get(i);
			ret.add(div);
			lst.remove(div);
			List<DivisionInfo> hiersubdivs = hierList(lst,div.id, level+1);
			for(int j = 0; j < hiersubdivs.size(); j++) {
				div.allDivisionAgents.addAll(hiersubdivs.get(j).allDivisionAgents);
			}
			ret.addAll(hiersubdivs);
		}
		
		return ret;	
	}
	
	@Override
	protected int getViewId() {
		return R.layout.division_row;
	}
	
	@Override
	protected void adjustView(Context context, ReportData data, View view) {
		TextView tv;
		tv = (TextView)view.findViewById(R.id.tvName);
		tv.setText(name);			
		
		AgentData ad = data.getDivisionData(id);
		
		String distance = "", orders = "", visits = "", sum = "", progress = "";
		if( ad != null ) {
			String km = context.getResources().getString(R.string.km);
			distance = Util.IntToScaleStr(ad.distance, 1) + " " + km;
			
			orders = Util.IntToScaleStr(ad.orders, 1);
			visits = Util.IntToScaleStr(ad.visits, 1);
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
	
	@Override
	public void runNapoleon(Context context) {}
	
	@Override
	public void adjustMenu(ContextMenu menu) {
		menu.findItem(R.id.itNapoleon).setVisible(false);
	}
	
	@Override
	public List<DivisionAgent> getAgents() {
		return agents;
	}
}
