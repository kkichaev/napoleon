package com.grsoft.util;

import com.grsoft.database.TreeNode;
import com.grsoft.napoleon.WarehouseEx;

import android.view.View;
import android.view.ViewGroup;

public class FoldersAdapterEx extends FoldersAdapter {

	public FoldersAdapterEx(WarehouseManager warehouse) {
		super(warehouse);
	}
	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		View result = super.getView(arg0, convertView, arg2);

		TreeNode node = (TreeNode) getItem(arg0);
		((WarehouseEx)warehouse).background(result, node);
		return result;
	}

}
