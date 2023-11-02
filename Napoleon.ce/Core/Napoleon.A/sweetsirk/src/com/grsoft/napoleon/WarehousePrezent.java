package com.grsoft.napoleon;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class WarehousePrezent extends WarehouseNew implements OnItemClickListener {
	static public void openPrezent(Context context,  Document<?> doc, boolean editMode) {
		Intent i = new Intent(context, WarehousePrezent.class);
		
		if( doc != null ) {
			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
			i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
			i.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		}
		context.startActivity(i);		
	}
	
	OrderControllerHelper och;
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouse_prezent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		lvItemSelect.setOnItemClickListener(this);
	}
	
	@Override
	protected void postInitUI() {
		super.postInitUI();
		och = new OrderControllerHelper(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		TreeNode tn = (TreeNode) adapter.getItem(position);
		if (tn instanceof FolderTreeNode)
			adapter.onClick(position);
		else if (tn instanceof PriceTreeNode)
			och.selectItem(((PriceTreeNode) tn).getId());
	}

	@Override
	protected FoldersAdapter createAdapterInstance() {
		return och.createAdapter();
	}
	
	@Override
	protected void postAdapterChange() {
		super.postAdapterChange();
		
		och.selectItem(null);
	}
}
