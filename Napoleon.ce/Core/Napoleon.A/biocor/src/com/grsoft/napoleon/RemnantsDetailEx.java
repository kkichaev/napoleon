package com.grsoft.napoleon;

import java.util.ArrayList;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.RemnantHelper.RemItemVal;

public class RemnantsDetailEx extends RemnantsDetail {
	private EditText edRemark;
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView v = (TextView) findViewById(R.id.textView2);
		edRemark = (EditText) findViewById(R.id.edRemark);
		
		if (v != null)
			v.setText(getString(R.string.remn_item_title));
		
		edRemark.setText(remnantsImpl.getData().remark);
		edRemark.setEnabled(remnantsImpl.isEditable());
	};
	
	@Override
	protected int getLayoutId() { return R.layout.remnantsdetailex; }
	
	@Override
	protected RemnantItemsAdapter createAdapter() {
		return new AdapterEx();
	}
	
	@Override
	protected ItemsOnClickListener createItemsOnClickHandler() {
		return null;
	}
	
	class AdapterEx extends RemnantItemsAdapter {
		@Override
		protected View setView(View view, PriceImpl priceImpl, int qty, Object tag) {
			if (view == null) {
				view = View.inflate(RemnantsDetailEx.this, R.layout.remnantsdetail_list_rowex, null);
				View tvQty = view.findViewById(R.id.tvQty);
				if( tvQty != null )
					tvQty.setVisibility(View.GONE);
			}
			
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			linesController.prepareTextView(tvName);
			tvName.setText(priceImpl.getData().name);
			
			view.setTag(tag);
			
			RemnantHelper.adjustView(view, remnantsImpl, priceImpl.getData().id, setStock, setShelf);
			
			return view;
		}
	}
	
	@Override
	protected void addItem() {
		if(!remnantsImpl.isExported())
			super.addItem();
	}
	
	OnCheckedChangeListener setStock = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			RemnantHelper.updateRemQty(remnantsImpl, buttonView, isChecked , new RemItemVal(){ @Override public void setItemVal(RemnantItemEx i, int val) { i.qty = val; }},
					(BaseAdapter)lvRemnantItems.getAdapter(), false);
		}
	};
	
	OnCheckedChangeListener setShelf = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			RemnantHelper.updateRemQty(remnantsImpl, buttonView, isChecked , new RemItemVal(){ @Override public void setItemVal(RemnantItemEx i, int val) { i.shelf = val; }}, 
					(BaseAdapter)lvRemnantItems.getAdapter(), false);
		}
	};
	
	@Override
	protected void onPause() {
		
		if(remnantsImpl.isEditable()){
			if(isFinishing())
				delEmptyRow();
			
			remnantsImpl.getData().remark = edRemark.getText().toString().trim();
			remnantsImpl.write();	
		}
		
		super.onPause();
	}

	private void delEmptyRow() {
		ArrayList<RemnantItem> list = new ArrayList<RemnantItem>();
		
		for(RemnantItem i : remnantsImpl.getData().items){
			RemnantItemEx ie = (RemnantItemEx)i;
			
			if(ie.qty == 0 && ie.shelf == 0)
				list.add(i);
		}
		
		remnantsImpl.getData().items.removeAll(list);
	};
	
	@Override
	protected void send() {
		delEmptyRow();
		super.send();
		((BaseAdapter)lvRemnantItems.getAdapter()).notifyDataSetChanged();
	}
}
