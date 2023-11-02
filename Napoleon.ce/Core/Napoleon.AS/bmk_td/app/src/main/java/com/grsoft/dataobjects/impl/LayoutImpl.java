package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Layout;
import com.grsoft.dataobjects.LayoutDef;
import com.grsoft.dataobjects.LayoutDefItem;
import com.grsoft.dataobjects.LayoutItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.LayoutEdit;
import com.grsoft.napoleon.LayoutView;
import com.grsoft.napoleon.documents.CreatableDocument;


public class LayoutImpl extends CreatableDocument<Layout>{
	public static final String DATA_CHANGED_ACTION = "com.grsoft.dataobjects.impl.LayoutImpl.DATA_CHANGED_ACTION";

	@Override public void open(Context context) {	LayoutView.open(context, getRowid()); }
	
	@Override
	public void postInit() {
		super.postInit();
		data.inwork = 1;
		
		String[] whereA = new String[] { "idOrg = '" + data.id + "'", "idOrg = ''" }; 
		
		for(String where : whereA) {
			DataTraveler.travel(LayoutDef.class, new DataTraveler.Travel<LayoutDef>(){
	
				@Override
				public boolean travel(DataTraveler<LayoutDef> item) {
					for(LayoutDefItem i : item.data.items){
						LayoutItem m = new LayoutItem();
						m.grid = item.data.id;
						m.grname = item.data.name;
						m.grpos = item.data.pos;
						m.itid = i.id;
						m.itname = i.name;
						m.qty = 0;
						m.pos = i.pos;
						
						data.items.add(m);
					}
					
					return true;
				}}, where);
			
			if(data.items.size() != 0)
				break;
		}
	}
	
	public LayoutItem findItem(String id){
		LayoutItem result = null;
		
		for(LayoutItem i : data.items)
			if(i.itid.equals(id)){
				result = i;
				break;
			}
		
		return result;
	}
	
	public void editItem(String id, final Context context) { 
		LayoutEdit.open(context, getRowid(), id);
	}
}
