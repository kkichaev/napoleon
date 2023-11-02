package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class WarehouseSettingEx extends WarehouseSetting {

	@Override
	protected int getContentViewID() { return R.layout.warehouse_settingex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnCommentLines).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { CommentListEditor.open(WarehouseSettingEx.this); }
		});
	}
	
}
