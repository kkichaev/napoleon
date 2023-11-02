package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.NETMtx;
import com.grsoft.dataobjects.NetMtxItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	public static String netid = "";
	public static Map<String, Integer> netItems = new HashMap<String, Integer>();
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter a = (FoldersAdapter) super.createListAdapter();
		
		DocType cd = DocType.getCurDoc(); 
		if(cd == OrderDoc.instance() || cd == RemnantsDoc.instance() ) {
			a.putFilter(new Filter("NETITEMS") {
				@Override
				public boolean inset(long priceRowID, String id) {
					boolean res =  super.inset(priceRowID, id);
					
					if (res && netid.length() > 0)
						res = netItems.containsKey(id);
					
					return res;
				}
			});
		}
		return a;
	}
	
	@Override
	protected void postDocInited() {
		super.postDocInited();
		
		OrgImpl org = new OrgImpl();
		netid = "";
		org.read("id", document.getId());
		
		if (((OrgEx)org.getData()).netid.length() == 0 || !((OrgEx)org.getData()).netid.equals(netid)) {
			netid = ((OrgEx)org.getData()).netid;
			
			DataTraveler.travel(NETMtx.class, new DataTraveler.Travel<NETMtx>() {
				@Override
				public boolean travel(DataTraveler<NETMtx> item) {
					for(NetMtxItem i : item.data.items)
						if(!netItems.containsKey(i.id))
							netItems.put(i.id, i.cost);
					
					return false;
				}
			}, "id = '" + netid + "'");
			
		}
	}
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		FoldersAdapter.resetCache();
		
		if( document instanceof ReturnImplEx)
			return new ReturnAdapter(this, document.getId());
		else
			return super.createAdapterInstance();
	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);
			
			com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId);
			for(Document<?> d : dl) {
				for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
					ids.add(di.id);
			}
			dl.close();
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}

}
