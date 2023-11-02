package com.grsoft.napoleon;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.ExtrasConst;


public class RemnantHelper {
	interface RemItemVal { void setItemVal(RemnantItemEx i,  int val); }
	
	public static void updateRemQty(Document<?> document,  CompoundButton buttonView, boolean isChecked, RemItemVal val, BaseAdapter adapter) {
		updateRemQty(document, buttonView, isChecked, val, adapter, true);
	}
	
	public static void updateRemQty(Document<?> document,  CompoundButton buttonView, boolean isChecked, RemItemVal val, BaseAdapter adapter, boolean rem) {
		if (document instanceof RemnantsImpl){
			RemnantsImpl ri = (RemnantsImpl)document;
			
			if(ri.isEditable()){
				String id = buttonView.getTag().toString();
				RemnantItemEx i =  (RemnantItemEx)ri.findItem(id);
				
				if (i == null){
					i = new RemnantItemEx();
					i.id = id;
					ri.getData().items.add(i);
				}
				
				val.setItemVal(i, isChecked ? 1 : 0);
				
				if (rem && i.qty == 0 && i.shelf == 0)
					ri.getData().items.remove(i);
				
				document.write();
				adapter.notifyDataSetChanged();
				RemnantsDoc.instance().refreshDocSum(document.getId());
			}
		}
	}
	
	public static void adjustView(View view, Document<?> document, String id, OnCheckedChangeListener setStock,
			OnCheckedChangeListener setShelf){
		CheckBox cbStock = (CheckBox)view.findViewById(R.id.cbStock);
		CheckBox cbShelf = (CheckBox)view.findViewById(R.id.cbShelf);
		
		if (document instanceof RemnantsImpl  && document.getRowid() != ExtrasConst.INVALID_ROWID){
			RemnantsImpl remn = (RemnantsImpl) document;
			
			cbStock.setVisibility(View.VISIBLE);
			cbStock.setTag(id);
			
			cbShelf.setVisibility(View.VISIBLE);
			cbShelf.setTag(id);
			
			if(remn.isEditable()){
				cbStock.setOnCheckedChangeListener(setStock);
				cbShelf.setOnCheckedChangeListener(setShelf);
			}else{
				cbStock.setEnabled(false);
				cbShelf.setEnabled(false);
			}
				
			
			RemnantItemEx i = (RemnantItemEx)remn.findItem(id);
			boolean stock = false;
			boolean shelf = false;
			
			if (i != null){
				stock = i.qty != 0;
				shelf = i.shelf != 0;
			}
			
			cbStock.setChecked(stock);
			cbShelf.setChecked(shelf);
		}
	}
}
