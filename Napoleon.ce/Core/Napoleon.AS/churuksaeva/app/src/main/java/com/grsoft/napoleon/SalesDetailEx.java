package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.SalesImplEx;

public class SalesDetailEx extends SalesDetail {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnSend).setVisibility(View.VISIBLE);
	}

	@Override
	public void onBackPressed() {
		((SalesEx)doc.getData()).compleete = (((SalesImplEx)doc).isCompleete()) ? 1 : 0;
		doc.write();

		super.onBackPressed();
	}

	@Override
	public void send() {
		if(!((SalesImplEx)doc).isCompleete()) {
			Toast.makeText(this, "¬ документе есть не отсканнированные товары", Toast.LENGTH_LONG).show();
			return;
		}
		super.send();
	}
}
