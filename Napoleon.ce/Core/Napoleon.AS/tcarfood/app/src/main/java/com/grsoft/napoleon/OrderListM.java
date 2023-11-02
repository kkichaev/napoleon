package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class OrderListM extends DocList {
	
	static void openOrdList(Context context) {
		Intent i = new Intent(context, OrderListM.class);
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
		btnFilter.setVisibility(View.GONE);
		btnDocFilter.setVisibility(View.GONE);
		btnSend.setVisibility(View.GONE);
		btnDelete.setVisibility(View.GONE);
		llFilterPanel.setVisibility(View.GONE);
		findViewById(R.id.tvDocSum).setVisibility(View.GONE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	protected DocListAdapter createListAdapter(DocType docType){
		return new DocListAdapter(this, OrderDoc.instance(), null){				
			@Override
			public com.grsoft.napoleon.documents.DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
				String where = "(([params] & " + Integer.toString(ParamState.ofExported) + " ) == 0)";
				com.grsoft.napoleon.documents.DocList ns = OrderDoc.instance().docList(null, "created desc", where);
				filterInvalidDoc(ns);
				return ns;
			}

			protected void filterInvalidDoc(com.grsoft.napoleon.documents.DocList ns) {
				OrgImpl oi = new OrgImpl();
				List<Long> toRemoveIds = new ArrayList<Long>();
				
				for (Document<?> d : ns) {
					OrderImpl ord = (OrderImpl)d;
					
					long s = ord.sum();
					
					if(s > 0 && oi.read("id", ord.getId())){
						DeliveryInfo deliveryInfo = DeliveryInfo.collectDelivery(oi.getData().id);

						if(s + deliveryInfo.sum < ((OrgEx)oi.getData()).limitsum){
							toRemoveIds.add(d.getRowid());
							break;
						}
					}
				}
				
				ns.removeDocuments(toRemoveIds);
			}
			
			@Override
			public void notifyDataSetChanged() {
				filterInvalidDoc(documents);
				super.notifyDataSetChanged();
			}
		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(adapter.getCount() == 0)
			finish();
	}
}
