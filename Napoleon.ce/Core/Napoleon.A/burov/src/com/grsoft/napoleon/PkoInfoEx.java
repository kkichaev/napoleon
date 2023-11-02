package com.grsoft.napoleon;

import com.grsoft.dataobjects.PkoEx;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class PkoInfoEx extends PkoInfo {
	private EditText edRemark;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		edRemark = (EditText) findViewById(R.id.edRemark);
		edRemark.setText(pkoImpl.getData().remark);
		
		if (pkoImpl.isExported())
			edRemark.setEnabled(false);
		else{
			findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					saveDocAndFinish();
				}
			});
			
			findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (((PkoEx)pkoImpl.getData()).inited == 0){
						pkoImpl.delete();
					}
					finish();
				}
			});
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.pkoinfoex;
	}
	
	@Override
	protected void adjustPko() {
		super.adjustPko();
		
		if(!pkoImpl.isExported())
			pkoImpl.getData().remark = edRemark.getText().toString();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if (!pkoImpl.isExported() && 
					((PkoEx)pkoImpl.getData()).inited == 0)
				pkoImpl.delete();
			finish();
			return true;
		}else{
			finish();
			return true;
		}
	}
	
	@Override
	protected void saveDocAndFinish() {
		((PkoEx)pkoImpl.getData()).inited = 1;
		super.saveDocAndFinish();
	}
}
