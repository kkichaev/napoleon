package com.grsoft.util;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.WarehouseNew;

public class FoldersAdapterEx extends FoldersAdapter {
	public ArrayList<String> idsAsrtMtx = new ArrayList<String>(); 
	protected String id = "";
	
	public FoldersAdapterEx(WarehouseNew warehouse, String id) {
		super(warehouse);
		this.id = id; 
	}
	
	@Override
	public boolean inset(long rowid, String id, int folder) {
		if(folder < 0)
			return false;
		return super.inset(rowid, id, folder);
	}
	
	@Override
	public void buldProcess(AsyncTask<?, ?, ?> task) {
		super.buldProcess(task);
		
		if (!task.isCancelled()){
			ArrayList<MatrixItem> assortMatrix = new ArrayList<MatrixItem>(); 
			AssortmentMatrixAdapter.collectItems(id, assortMatrix);
			for(MatrixItem mi : assortMatrix)
				idsAsrtMtx.add(mi.id);
		}
	}
}
