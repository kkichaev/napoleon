package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DlvHighlightImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.R;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderDocEx extends OrderDoc {
	DlvHighlightImpl dlvs = new DlvHighlightImpl();
	
	public static void initialize(){
		instance = new OrderDocEx();
	}
	
	public OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		
		if(dlvs.read("created", ((OrderImpl)doc).getData().created)){
			LinearLayout lay = (LinearLayout) view;
			for(int i = 0; i < lay.getChildCount(); i++){
				View v = lay.getChildAt(i); 
				if(v instanceof TextView){
					((TextView)v).setTextColor(view.getContext().getResources().getColor(R.color.red));
				}
			}
		}
	}
	
}
