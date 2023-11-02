package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocumentSender;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class OrderDetailEx extends OrderDetail implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnAction).setOnClickListener(this);
	}
	
	@Override
	protected void deleteItem(OrderItem orderItem) {
		super.deleteItem(orderItem);
		
		int partid = ((OrderItemEx)orderItem).partid;
		
		if(partid > 0){
			ArrayList<OrderItem> dest = new ArrayList<OrderItem>(doc.getData().items);

			for(OrderItem i: dest)
				if(((OrderItemEx)i).partid == partid)
					super.deleteItem(i);
		}
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}

	@Override
	public void onClick(View v) {
		ActionView.open(this, doc.getRowid());
	}
	
	@Override
	public void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), ((OrderImplEx)doc).getSendedDocuments()).execute((Void[])null);
	}
}
