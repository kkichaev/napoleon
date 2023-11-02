package com.grsoft.napoleon;

import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePhotoName;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;

public class PricePhotoList extends PricePresentationFolder {
	public static void open(Context context, long priceRID, long docRID) {
		Intent i = new Intent(context, PricePhotoList.class);
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRID);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, docRID);
		
		context.startActivity(i);
	}
	
	@Override
	protected void initPresentList() {
		PriceImpl prc = new PriceImpl();
		Price price = prc.getData();
		prc.read(priceId);
		prc.close();
		
		PresentImpl prs = new PresentImpl();
		PresentEx pre = (PresentEx) prs.getData();
		pre.id = price.id;
		prs.read();
		prs.close();
		
		list = new PresentationList();
		if(pre.photoPath.length() > 0) {
			PresentationData presData = new PresentationData(priceId, price.folderID, price.name, pre.photoPath, price.id);
			list.add(presData);
			for(PricePhotoName ppn : pre.photas) {
				presData = new PresentationData(priceId, price.folderID, price.name, ppn.name, price.id);
				list.add(presData);
			}
		}				
	}
}
