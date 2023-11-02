package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnAddItems.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( isValidDoc() )
					Warehouse.open(OrderDetailEx.this, doc, true);
				else
					Toast.makeText(OrderDetailEx.this, "Не заполнен склад", Toast.LENGTH_SHORT).show();
//					Toast.makeText(OrderDetailEx.this, "Не заполнен договор, склад или организация", Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected boolean isValidDoc() {
		OrderEx o = (OrderEx) doc.getData();
//		if( o.whName.length() == 0 || o.dog.length() == 0 || o.supplyer < 0 )
		if( o.whName.length() == 0 )
			return false;
		return true;
	}
}
