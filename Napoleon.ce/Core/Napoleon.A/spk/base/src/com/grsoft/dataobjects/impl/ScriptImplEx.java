package com.grsoft.dataobjects.impl;

import java.util.List;

import android.content.Context;

import com.grsoft.dataobjects.PlanRouteItem;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class ScriptImplEx extends ScriptImpl {
	@Override
	public CreatableDocument<?>[] getDocuments() {
		CreatableDocument<?>[] res = new CreatableDocument<?>[0];
		PlanRouteItem item = getPlanItem(getId());
		
		if (item == null)
			res = super.getDocuments();
		else{	
			res = new CreatableDocument<?>[item.getDocCount()];
			
			int index = 0;
			if (item.order == 1)
				res[index++] = createDocument("Order", getDate(), null, null);
			if (item.incass == 1)
				res[index++] = createDocument("Incass", getDate(), null, null);
			if (item.returns == 1)
				res[index++] = createDocument("Returns", getDate(), null, null);
			if (item.visit == 1)
				res[index++] = createDocument("Visit", getDate(), null, null);
		}
		
		return res;
	}
	
	public static PlanRouteItem getPlanItem(String orgid){
		PlanRouteItem result = null;
		PlanRouteImpl plan  = new PlanRouteImpl();
		plan.getData().created = Util.getDate();
		
		if(plan.read() && plan.isApproved()){
			List<PlanRouteItem> items = plan.getData().items;
			
			for(PlanRouteItem i : items){
				if(i.id.equals(orgid)){
					result = i;
					break;
				}
			}
		}
		
		plan.close();
		
		return result;
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		PlanRouteItem item = getPlanItem(orgId);
		
		if(item == null)
			return super.init(context, orgId, gpsCoord);
		else {
			initInternal(context, orgId, gpsCoord, item.createScriptDef());
			return false;
		}
	}
}
