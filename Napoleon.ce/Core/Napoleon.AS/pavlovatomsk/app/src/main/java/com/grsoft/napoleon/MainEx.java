package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReqOrderDoc;
import com.grsoft.network.DocExportListener;

public class 	MainEx extends Main{
	
	@Override
	protected void onResume() {
		super.onResume();

		boolean haveBadOrder = false;

		DocExportListener toExp = OrderDoc.instance().getDirtyDocuments();
		if(toExp != null){
			haveBadOrder = isHaveBadOrder(toExp);

		}
		if(!haveBadOrder) {
			toExp = ReqOrderDoc.instance().getDirtyDocuments();
			haveBadOrder = isHaveBadOrder(toExp);
		}

		if(haveBadOrder)
			OrderListM.openOrdList(this);
	}

	private boolean isHaveBadOrder(DocExportListener toExp) {
		boolean haveBadOrder = false;
		OrgImpl oi = new OrgImpl();

		DocList ords = toExp.getDocuments();

		for(Document<?> d : ords){
			OrderImpl ord = (OrderImpl)d;

			long s = ord.sum();
			boolean b = ((OrderEx)ord.getData()).bonus == 1;

			if(oi.read("id", ord.getId())) {
				DeliveryInfo deliveryInfo = DeliveryInfo.collectDelivery(oi.getData().id);

				if(!b && s > 0){
					int os = ((OrgEx)oi.getData()).minSum;

					if(os > 0 && s < os){
						haveBadOrder = true;
						break;
					}
				}

				if(!b && s + deliveryInfo.sum >= ((OrgEx)oi.getData()).limitsum) {
					haveBadOrder = true;
					break;
				}
			}
			oi.close();
		}
		return haveBadOrder;
	}

	@Override
	protected void setOrgBackground(int pos, Org org, View v) {
		super.setOrgBackground(pos, org, v);

		if(org != null) {
			if (((OrgEx)org).merc == 0 && ((OrgEx)org).chznak == 0)
				v.setBackgroundResource(R.drawable.mercchznak_selector);
			else if (((OrgEx)org).merc == 0)
				v.setBackgroundResource(R.drawable.merc_selector);
			else if (((OrgEx)org).chznak == 0)
				v.setBackgroundResource(R.drawable.chznak_selector);
		}
	}
}
