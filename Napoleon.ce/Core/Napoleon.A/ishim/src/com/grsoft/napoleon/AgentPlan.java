package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class AgentPlan extends BaseActivity {
	public static void open(Context context) {
		Intent i = new Intent(context, AgentPlan.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.agent_plan);
		ListView lv = (ListView)findViewById(R.id.lvPlans);
		Adapter a = new Adapter();
		lv.setAdapter(a);
	}
	
	class Adapter extends BaseAdapter {
		
		ArrayList<PlanItem> items = new ArrayList<PlanItem>();
		
		HashMap<String, Integer> fact = new HashMap<String, Integer>();
		Date factdate;
		
		SimpleDateFormat fmt = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
		
		public Adapter() {
			refreshData();
		}

		private void refreshData() {
			items.clear();
			
			Date now = Util.getDate();
			com.grsoft.dataobjects.AgentPlan plan = new com.grsoft.dataobjects.AgentPlan();
			String table = DataObjectInfo.getInstance().getTableName(plan.getClass());
			DbReader r = new DbReader();
			boolean bdo = r.select(plan, table, null, "begin desc");
			while(bdo) {
				PlanItem pi = new PlanItem();
				pi.begin = plan.begin;
				items.add(pi);
				
				boolean usePredict = (plan.begin.compareTo(now) <= 0 && plan.end.compareTo(now) >= 0);
				for(AgentPlanItem i : plan.items) {
					pi = new PlanItem();
					pi.begin = null;
					pi.plan = i.value;
					pi.name = i.getName();
					pi.fact = getFact(plan.begin, plan.end, i.id, usePredict);
					
					items.add(pi);
				}
				bdo = r.selectNext(plan);
			}
			r.close();
		}

		private String getFact(Date begin, Date end, String id, boolean usePredict) {
			if(factdate == null || factdate.compareTo(begin) != 0 ) {
				fact.clear();
				
				Order o = new Order();
				PriceImpl pi = new PriceImpl();
				Price p = pi.getData();
				pi.setReadingFields("weight");
				
				String table = DataObjectInfo.getInstance().getTableName(o.getClass());
				String filter = "created >= " + Long.toString(begin.getTime()) + 
						" and created <= " + Long.toString(end.getTime());
				
				long fact1 = 0;
				long fact2 = 0;
				
				DbReader r = new DbReader();
				boolean bdo = r.select(o, table, filter);
				while(bdo) {
					for(OrderItem oi : o.items) {
						p.id = oi.id;
						if( pi.read() ) {
							long weight = ((long)oi.qty * p.weight / Consts.WEIGHT_SCALE);
							if( oi.id.startsWith(AgentPlanItem.PLAN1_TAG) )
								fact1 += weight;
							else
								fact2 += weight;
						}						
					}
					bdo = r.selectNext(o);
				}
				r.close();
				pi.close();
				
				factdate = begin;
				fact.put(AgentPlanItem.PLAN1_TAG, (int)fact1/1000);
				fact.put(AgentPlanItem.PLAN2_TAG, (int)fact2/1000);
			}
			
			Integer v = fact.get(id);
			if(v == null) v = 0;
			
			String ret = Util.IntToScaleStr(v, Consts.QTY_SCALE, Util.DEC_DELIM, false);
			if( usePredict && v != 0 ) {
				int cur = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, 1);
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.add(Calendar.DAY_OF_MONTH, -1);
				int last = c.get(Calendar.DAY_OF_MONTH);
				v = (int)((long)v * last /  cur);
				ret += "<br><i>" + Util.IntToScaleStr(v, Consts.QTY_SCALE, Util.DEC_DELIM, false) + "</i>";
			}
			return ret;
		}

		@Override public int getCount() { return items.size(); }

		@Override public Object getItem(int arg0) {
			return (arg0 < items.size()) ? items.get(arg0) : null;
		}

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			PlanItem item = (PlanItem)getItem(pos);
			if( item == null )
				return view;
			
			int id = item.begin == null ? R.layout.agent_plan_row : R.layout.agent_date_row;
			
			if( view == null || (Integer)view.getTag() != id )
				view = View.inflate(AgentPlan.this, id, null);
			view.setTag(id);
			
			TextView tv;
			if( item.begin != null ) {
				tv = (TextView)view.findViewById(R.id.tvDate);
				tv.setText("План " + fmt.format(item.begin));
			} else {
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(item.name);
				
				tv = (TextView)view.findViewById(R.id.tvPlan);
				tv.setText(Util.IntToScaleStr(item.plan, Consts.QTY_SCALE, Util.DEC_DELIM, false));
				
				tv = (TextView)view.findViewById(R.id.tvFact);
				tv.setText(Html.fromHtml(item.fact));
			}
			return view;
		}
		
	}
}

class PlanItem {
	public Date begin;
	public String name;
	public int plan;
	public String fact;
}
