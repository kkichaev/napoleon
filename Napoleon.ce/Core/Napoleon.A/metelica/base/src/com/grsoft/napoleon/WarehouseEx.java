package com.grsoft.napoleon;

import java.util.Comparator;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeCmp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew implements OnClickListener, android.content.DialogInterface.OnClickListener {
	private final static String SORT_PREF = "SORT_PREF";
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnSort).setOnClickListener(this);
	}

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.TreeNodeComparator = createdComparator(getStoredSort());
		return super.createListAdapter();
	}
	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.btnSort) 
			showDialog(R.id.sort_dlg_id);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.sort_dlg_id)
			return createSortDialog();
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	protected void setDefaultBackground(TextView textView) {
		if(((PriceEx)price.getData()).best > 0) {
			textView.setBackgroundResource(R.drawable.best_item_bg);
		} else 
			super.setDefaultBackground(textView);
	}
	

	private Dialog createSortDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.sort);
		builder.setSingleChoiceItems(getResources().getStringArray(R.array.sortings), 
				getStoredSort(),this);
		return builder.create();
	}

	private int getStoredSort() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		return pref.getInt(SORT_PREF, 0);
	}

	public void setStoredSort(int type) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor ed = pref.edit();
		ed.putInt(SORT_PREF, type);
		ed.commit();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		setStoredSort(which);
		applySort(which);
		dialog.dismiss();
	}

	private void applySort(int which) {
		FoldersAdapter.TreeNodeComparator = createdComparator(which);
		adapter.buildSet(adapter.getFolderTop().id);
	}

	private Comparator<TreeNode> createdComparator(int which) {
		switch(which) {
		case 1: return new TreeNodeCmpInv();
		case 2: return new TreeNodeCmpCost();
		case 3: return new TreeNodeCmpCostInv();
		default: return new TreeNodeCmp();
		}
	}
	
	class TreeNodeCmpInv extends TreeNodeCmp{
		@Override
		public int compare(TreeNode n1, TreeNode n2) {
			int result = super.compare(n1, n2);
			
			if (n1 instanceof PriceTreeNode && n2 instanceof PriceTreeNode)
				return result * -1;
			
			return result;
		}
	}
	
	class TreeNodeCmpCost extends TreeNodeCmp{
		PriceImpl p1 = new PriceImpl();
		PriceImpl p2 = new PriceImpl();
		
		@Override
		public int compare(TreeNode n1, TreeNode n2) {
			int result = super.compare(n1, n2);
			
			if (n1 instanceof PriceTreeNode && n2 instanceof PriceTreeNode) {
				p1.read(n1.getRowid());
				p1.close();
				int c1 = getCost(p1.getData());
				
				p2.read(n2.getRowid());
				p2.close();
				int c2 = getCost(p2.getData());
				
				return c1 - c2;
			}
			
			return result;
		}
	}
	
	class TreeNodeCmpCostInv extends TreeNodeCmpCost{
		@Override
		public int compare(TreeNode n1, TreeNode n2) {
			int result = super.compare(n1, n2);
			
			if (n1 instanceof PriceTreeNode && n2 instanceof PriceTreeNode)
				return result * -1;
			
			return result;
		}
	}
}
