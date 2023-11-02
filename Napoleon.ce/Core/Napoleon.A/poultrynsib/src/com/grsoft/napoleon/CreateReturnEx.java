package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;

public class CreateReturnEx extends CreateReturn {
	private CheckBox cbForsake;
	
	@Override
	int getContentViewID() { return R.layout.createreturnex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		cbForsake = (CheckBox) findViewById(R.id.cbForsake);
		
		ReturnEx ret = (ReturnEx) doc.getData();
		cbForsake.setChecked(ret.forsake > 0);
		
		cbForsake.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(doc.isExported())
				{
					doc.setExported(false);
					View ok = findViewById(R.id.btnOK); 
					ok.setOnClickListener(new OKHandler());
					ok.setEnabled(true);
				}
			}
		});
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		
		((ReturnEx)r).forsake = cbForsake.isChecked() ? 1 : 0;
	}
	
}

