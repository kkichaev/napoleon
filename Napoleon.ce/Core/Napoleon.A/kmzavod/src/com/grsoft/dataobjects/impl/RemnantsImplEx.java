package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.AssortmentMatrixAdapterEx;
import com.grsoft.napoleon.RemnantsDetail;

import android.content.Context;

public class RemnantsImplEx extends RemnantsImpl {
	@Override
	public void postInit() {
		super.postInit();
		PriceImpl p = new PriceImpl();
		
		for (MatrixItem m : AssortmentMatrixAdapterEx.collect(getId())) {
			if (p.read("id", m.id)) {
				RemnantItem i = new RemnantItem();
				i.id = m.id;

				data.items.add(i);
			}
		}
	}
	
	@Override
	protected void openPrice(Context context) {
		RemnantsDetail.open(context, this);
	}
}
