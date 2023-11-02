package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.dataobjects.OrderItem;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	private ImageButton btnDetail; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnDetail = (ImageButton) findViewById(R.id.btnDetail);
		
		btnDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OrderDeliveryDetailSklad.open(v.getContext(), delivery.getRowid());
			}
		});
	}
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdeliverydetailex);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderDeliveryItemsAdapter(){
			@Override
			protected void setItems(List<OrderItem> items) {
				HashSet<String> ids = new HashSet<String>();
				
				this.items = new ArrayList<OrderItem>();
				
				for(OrderItem item : items){
					if(!ids.contains(item.id)){
						this.items.add(item);
						ids.add(item.id);
					}else{
						OrderItem obj = findItem(item);
						
						if(obj != null)
							obj.qty += item.qty;
					}
				}
			}
			
			private OrderItem findItem(OrderItem item){
				for(OrderItem i: items)
					if(item.id.equals(i.id))
						return i;
				
				return null;
			}
		});
	}
}
