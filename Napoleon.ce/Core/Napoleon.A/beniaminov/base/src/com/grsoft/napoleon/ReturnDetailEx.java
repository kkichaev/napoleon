package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import com.grsoft.dataobjects.impl.ReturnImpl;

public class ReturnDetailEx extends ReturnDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnAddItems.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { ReturnPriceList.open(ReturnDetailEx.this, (ReturnImpl)doc); }
		});
	}

}
