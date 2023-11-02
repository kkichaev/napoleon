package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Distrib;
import com.grsoft.dataobjects.DistribDef;
import com.grsoft.dataobjects.DistribItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.DistribEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import android.content.Context;


public class DistribImpl extends CreatableDocument<Distrib> {
	@Override public void open(Context context) {	DistribEdit.open(context, getRowid()); }
	
	@Override
	public void postInit() {
		super.postInit();
		
		OrgImpl org = new OrgImpl();
		if(org.read("id",getId())){
			List<MatrixItem> m = ((OrgEx)org.getData()).matrix;
			
			if(m != null && m.size() > 0){
				final List<DistribDef> def = new ArrayList<DistribDef>();
				
				DataTraveler.travel(DistribDef.class, new DataTraveler.Travel<DistribDef>() {
					@Override public boolean isDataNewInstance() { return true; }
					@Override
					public boolean travel(DataTraveler<DistribDef> item) {
						def.add(item.data);
						return true;
					}}, null);
				
				Set<String> mids = new HashSet<String>();
				
				PriceImpl check = new PriceImpl();
				
				for(MatrixItem i : m)
					if (!mids.contains(i.id) && check.read("id", i.id)) {
						mids.add(i.id);
						data.matrix.add(i);
					}
				
				for(DistribDef d : def)
					data.defs.add(d);
				
				for(MatrixItem i : data.matrix) 
					for(DistribDef d: data.defs){
						DistribItem di = new DistribItem();
						di.id = i.id;
						di.iddef = d.id;
						
						if(d.type.toUpperCase().equals(DistribDef.NUMBER_TYPE))
							di.val = "0";
						else if (d.type.toUpperCase().equals(DistribDef.BOOL_TYPE))
							di.val = "false";
						
						data.items.add(di);
					}
			}
		}
	}
}
