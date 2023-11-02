package com.grsoft.napoleon;

import java.util.Collections;
import java.util.Comparator;

import com.grsoft.dataobjects.RemnantItem;

public class RemnantsDetailEx extends RemnantsDetail {
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Collections.sort(remnantsImpl.getData().items, new Comparator<RemnantItem>() {
			PriceCmp cmp = new PriceCmp();
			
			@Override
			public int compare(RemnantItem x, RemnantItem y) {
				return cmp.compare(x.id, y.id);
			}
		});
	}
}
