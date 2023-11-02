package com.grsoft.napoleon;

import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.print.DeliverySource;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ImageButton btnPrint = (ImageButton) findViewById(R.id.btnPrint);
		
		btnPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NPrinter.print(v.getContext(), R.raw.torg12, new DeliverySource(doc));
			}
		});
	}
}
