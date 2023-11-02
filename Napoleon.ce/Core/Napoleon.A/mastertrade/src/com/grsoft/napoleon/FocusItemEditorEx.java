package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.FocusedItemsImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FocusItemEditorEx extends FocusItemEditor implements OnClickListener {
	boolean editMode = false;
	
	public static void open(Context context, OrderImplBase<? extends Order> doc, boolean editMode) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		context.startActivity(i);
	}
	
	@Override
	protected int getLayout() {
		return R.layout.focus_editorex;
	}
	
	View btnPrice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnPrice = findViewById(R.id.btnPrice);
		btnPrice.setOnClickListener(this);
		
		editMode = getIntent().getExtras().getBoolean(ExtrasConst.EDIT_MODE_STR, true);
	}
	
	protected OnCheckedChangeListener setSkipItem = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (buttonView.getTag() != null && buttonView.getTag() instanceof Integer) {
				int pos = (Integer) buttonView.getTag();
				
				FocusGroupItem i = (FocusGroupItem) itemsAdapter.getItem(pos);
				
				if (isChecked)
					i.setComment(order.getData(), getString(R.string.skip_comment));
				else
					i.setComment(order.getData(), "");
				
				order.write();
				order.close();
				
				itemsAdapter.notifyDataSetChanged();
			}
			
		}
	};

	@Override
	protected ItemsAdapter createItemsAdapter() {
		return new ItemsAdapter() {
			
			@Override
			protected int getItemLayout() {
				return R.layout.focus_editor_rowex;
			}
			
			@Override
			public View getView(int pos, View view, ViewGroup arg2) {
				View result = super.getView(pos, view, arg2);
				CheckBox cb = (CheckBox) result.findViewById(R.id.cbSkipped);
				cb.setOnCheckedChangeListener(setSkipItem);
				cb.setTag(pos);
				
				FocusGroupItem item = (FocusGroupItem)getItem(pos);
				cb.setChecked(item.getComment().equals(getString(R.string.skip_comment)));
				
				return result;
			}
			
			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
				updatePriceButton();
			}
		};
	}

	protected void updatePriceButton() {
		btnPrice.setVisibility(FocusedItemsImpl.getUnsettedItems(order).size() == 0 ? View.VISIBLE : View.GONE); 
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnPrice) {
			Warehouse.open(FocusItemEditorEx.this, order, editMode);
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		if (editMode)
			super.onBackPressed();
	}
}
