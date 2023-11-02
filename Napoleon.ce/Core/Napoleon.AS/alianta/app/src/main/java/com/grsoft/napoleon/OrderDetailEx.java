package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;

import com.grsoft.database.OrderResultHitching;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderWhItem;
import com.grsoft.dataobjects.WhData;
import com.grsoft.util.MessageBox;

import android.os.Bundle;
import android.view.View;

public class OrderDetailEx extends OrderDetail {
	
//	ArrayList<CharSequence> actions = new ArrayList<CharSequence>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		actions.add("Нет");
//		
//		ConfigImpl config = new ConfigImpl();
//		config.getData().key = "Акции";
//		if( config.read() )
//			DialogHelper.makeList(config.getData().value, actions);
//		config.close();
//		
//		Spinner s = (Spinner)findViewById(R.id.spActions);			
//		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, actions);
//		s.setAdapter(aa);
//
//		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				if( !doc.isExported() ) {
//					CharSequence a = (position == 0) ? "" : actions.get(position);
//					((OrderEx)doc.getData()).action = a.toString();
//					doc.write();
//				}
//			}
//
//			@Override public void onNothingSelected(AdapterView<?> arg0) {}
//		});
	}
	
	@Override
	public void postSendExecute(boolean result) {
		if(result) {
			if(OrderResultHitching.data != null && OrderResultHitching.data.isFail()) {
				MessageBox.show(this, getString(R.string.error), OrderResultHitching.data.message);				
			} else {
				doc.open(this);
				finish();
			}
		}
	}
	
	@Override
	protected void deleteItem(OrderItem orderItem) {
		HashMap<String, Integer> qty = new HashMap<String, Integer>();
		for(OrderWhItem owi : ((OrderItemEx)orderItem).whData) {
			qty.put(owi.id, owi.qty);
		}
		
		if( qty.size() > 0 ) {
			WhData.updateQty(orderItem.id, qty);
		}
		super.deleteItem(orderItem);		
	}
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		
//		View v = findViewById(R.id.llActions);
//		int visible = View.GONE;
//		
//		if( canShowAction() ) {
////			visible = View.VISIBLE;
////			
////			OrderEx oe = (OrderEx)doc.getData();
////			Spinner s = (Spinner)findViewById(R.id.spActions);			
////			int selected = 0;
////			
////			if( oe.action.length() > 0 )
////				selected = actions.indexOf(oe.action);
////			if( selected < 0 )
////				selected = 0;
////			
////			s.setSelection(selected);
////			if( doc.isExported() )
////				s.setEnabled(false);
//		}
//		
////		v.setVisibility(visible);
//	}
	
//	private boolean canShowAction() {
//		boolean ret = false;
////		OrgImpl o = new OrgImpl();
////		o.getData().id = doc.getId();
////		o.read();
////		
////		OrgEx oe = (OrgEx)o.getData();
////		if( (oe.flags & OrgEx.FL_CAN_ACTION) != 0 ) {
////			PriceImpl pi = new PriceImpl();
////			PriceEx p = (PriceEx)pi.getData();
////			
////			for( OrderItem oi : doc.getData().items ) {
////				p.id = oi.id;
////				if( pi.read() && p.inAction() ) {
////					ret = true;
////					break;
////				}
////			}
////			pi.close();
////		}
//		return ret;
//	}

//	@Override
//	protected void setContentView() {
//		setContentView(R.layout.orderdetailex);
//	}
}
