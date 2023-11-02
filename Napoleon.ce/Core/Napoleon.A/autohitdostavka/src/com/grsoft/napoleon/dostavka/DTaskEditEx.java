package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.Dispatch;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class DTaskEditEx extends DTaskEdit implements OnClickListener, IncompleteAction {
	@Override protected int getLayoutID() { return R.layout.dtaskeditex;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnIncompletely).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnIncompletely)
			promptIncomplete();
		
	}

	private void promptIncomplete() {
		new IncompleteDialog().show(getFragmentManager(), IncompleteDialog.class.toString());
		
	}

	@Override
	public void doIncomplete(String remark) {
		doc.getData().params |= Dispatch.USER_STATUS;
		doc.getData().remark = remark;
		doc.setReadyToSend();
		doc.write();
		doc.close();
		finish();
	}
}
