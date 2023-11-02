package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;
import java.util.Collection;
import com.grsoft.database.AgentOrgHitching;
import com.grsoft.database.PotenzialOrgHitching;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.network.ObjectListener;

public class RemnantsDetailEx extends RemnantsDetail {

//	@Override
//	protected RemnantItemsAdapter createAdapter() {
//		return new AdapterEx();
//	}
	
//	@Override
//	protected ItemsOnClickListener createItemsOnClickHandler() {
//		return null;
//	}
//	
//	class AdapterEx extends RemnantItemsAdapter {
//		@Override
//		protected View setView(View view, PriceImpl priceImpl, int qty, Object tag) {
//			if (view == null) {
//				view = View.inflate(RemnantsDetailEx.this, R.layout.remnantsdetail_list_row, null);
//				View tvQty = view.findViewById(R.id.tvQty);
//				if( tvQty != null )
//					tvQty.setVisibility(View.GONE);
//			}
//			
//			TextView tvName = (TextView)view.findViewById(R.id.tvName);
//			linesController.prepareTextView(tvName);
//			tvName.setText(priceImpl.getData().name);
//			
//			view.setTag(tag);
//			return view;
//		}
//	}
	
	@Override
	protected void addItem() {
		if(!remnantsImpl.isExported())
			super.addItem();
	}
	
	protected void send() {
		new DocumentSender(RemnantsDetailEx.this, null,
				RemnantsDoc.instance().getObjectName(), remnantsImpl, 
				remnantsImpl.getRowid()){
			
			protected Collection<ObjectListener> getObjectsToSend() {
				Collection<ObjectListener> result = super.getObjectsToSend();
				
				PotenzialOrgHitching poh = new PotenzialOrgHitching("Org");
				if( poh.size() > 0 ){
					result.add(poh);
					result.add(new AgentOrgHitching(poh));
				}
				
				return result;
			};
			
			protected com.grsoft.database.PotenzialOrgHitching createPotenzialOrgHitching() {return null; };
		}.execute((Void[])null);
	}
}
