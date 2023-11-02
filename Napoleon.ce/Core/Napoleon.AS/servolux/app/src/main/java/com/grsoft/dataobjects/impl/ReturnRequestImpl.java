package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.ReturnRequest;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.napoleon.CreateReturnRequest;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.ReturnRequestDetail;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class ReturnRequestImpl extends OrderImplBase<ReturnRequest> {

	@Override public void editItem(long itemRowid, Context context) { ReturnPriceCount.open(context, itemRowid, this); }

	@Override public void editProperties(Context ctx, boolean isOldOrder) { CreateReturnRequest.open(ctx, this, isOldOrder); }
	
	@Override public CreatableDocument<ReturnRequest> copy() { return null; }

	@Override public CreatableDocument<ReturnRequest> createInstance() { return new ReturnRequestImpl(); }
	@Override public void open(Context context) { ReturnRequestDetail.open(context, this); }
	
	@Override protected boolean checkPriceQty() { return false; }
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		boolean ret = super.updateQty(priceImpl, qty, cost, inPack);
		if( qty == 0 ) {
			VisitImplEx ve = new VisitImplEx();
			Visit visit = ve.getData();
			visit.created = data.visitDoc; 
			if(!ve.read()) {
				VisitItemEx ie = ve.findPhoto(priceImpl.getData().id);
				if( ie != null ) {
					visit.items.remove(ie);
					if(ve.isEmpty())
						ve.delete();
					else
						ve.write();
				}
			}
			ve.close();
		}
		return ret;
	}
	
	@Override
	public boolean delete() {
		if(isExported())
			return false;
		
		return super.delete();
	}
	
	@Override
	public void postInit() {
//		data.svChanged = data.created;
	}
	
	@Override
	public String getDescription(Context context) {
//		if(isProceeded()) {
//			if(data.accepted == 0)
//				return "на рассмотрении";
//			
//			int svQty = 0, qty = 0;
//			for(OrderItem i : data.items) {
//				for(ReturnItemDlv rd : ((ReturnRequestItem)i).items) {
//					svQty += rd.svQty;
//					qty += rd.qty;
//				}
//			}
//			
//			return svQty == qty ? "принят полностью" : 
//				svQty > 0 ? "<font color='green'>принят частично</font>" :
//					"<font color='red'><b>отвергнут</b></font>";
//		}
		return super.getDescription(context);
	}
	
//	public boolean isAccepted() { return data.accepted != 0; }
}
