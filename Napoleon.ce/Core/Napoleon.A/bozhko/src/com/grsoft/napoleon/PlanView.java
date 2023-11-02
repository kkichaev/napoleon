package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.AgentSalesPlan;
import com.grsoft.dataobjects.impl.AgentSalesPlanImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class PlanView extends Activity {
	
	public static void open(Context context) {
		Intent intent = new Intent(context, PlanView.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_view);

		WebView wv = (WebView) findViewById(R.id.webView1);

		try {
			wv.loadDataWithBaseURL(null, makePage(), "text/html", "utf-8", null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String makePage() {
		AgentSalesPlanImpl.refreshDocCache();
		
		Map<String, Item> map = new HashMap<String, Item>();
		PriceImpl price = new PriceImpl();
		
		for(AgentSalesPlan asp : AgentSalesPlanImpl.plans)
			for(AgentPlanItem api : asp.items){
				Item i = null;
				price.getData().id = api.id;
				price.read();
				
				if(map.containsKey(api.id))
					i = map.get(api.id);
				else{
					i = new Item();
					i.name = price.getData().name;
				}
				
				i.plan += api.qty;
				i.fact += (int)((long)AgentSalesPlanImpl.data.getSales(api.id, asp.dateStart, asp.dateEnd) * Consts.QTY_SCALE / price.getData().qtyInPack);;
				
				map.put(api.id, i);
			}
		
		List<Item> items = new ArrayList<Item>();
		items.addAll(map.values());
		Collections.sort(items, new Comparator<Item>(){

			@Override
			public int compare(Item lhs, Item rhs) {
				int w1 = lhs.getWeight();
				int w2 = rhs.getWeight();
				
				int result = w1 - w2;
				
				if(result == 0)
					result = lhs.name.compareTo(rhs.name);
				
				return result;
			}});
		
		price.close();
		
		StringBuilder result = new StringBuilder();
		
		result.append("<table border='1' style='width:100%'>");
		result.append("<tr>");
		result.append("<td style='width:60%'>&nbsp;</td>");
		result.append("<td style='width:20%'>План</td>");
		result.append("<td style='width:20%'>Факт</td>");
		result.append("</tr>");
		
		for(Item i : items){
			String color = i.getColor();
			result.append("<tr>");
			result.append("<td style='width:60%'><font color='#" + color + "'>" + i.name + "</font></td>");
			result.append("<td style='width:20%'><font color='#" + color + "'>" + Util.IntToScaleStr(i.plan, Consts.QTY_SCALE)+ "</font></td>");
			result.append("<td style='width:20%'><font color='#" + color + "'>" + Util.IntToScaleStr(i.fact, Consts.QTY_SCALE)+ "</font></td>");
			result.append("</tr>");
		}
		
		result.append("</table>");
		return result.toString();
	}
	
}

class Item
{
	public String name;
	public int plan;
	public int fact;
	
	public int getWeight(){
		if (fact == plan)
			return 2;
		
		if (fact > plan)
			return 1;
		
		return 0;
	}
	
	public String getColor(){
		switch(getWeight()){
		case 2:
			return "B0B0B0";
		case 1:
			return "0000FF";
		default:
			return "000000";
		}
	}
}
