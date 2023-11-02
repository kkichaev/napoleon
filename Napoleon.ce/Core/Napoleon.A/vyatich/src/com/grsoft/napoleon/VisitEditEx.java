package com.grsoft.napoleon;

import com.grsoft.dataobjects.VisitEx;

import android.os.Bundle;
import android.view.View;

public class VisitEditEx extends VisitEdit {
	@Override protected int getContentView() { return R.layout.visiteditex; }
	
	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
		
		if( ((VisitEx)visit.getData()).stock != 0 ) {
			findViewById(R.id.llAction).setVisibility(View.VISIBLE);
		}
	}
}
