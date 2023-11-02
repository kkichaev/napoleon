package com.grsoft.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.WarehouseNew;

public class AssortmentMatrixAdapterEx extends AssortmentMatrixAdapter {
	Set<String> priceIds = new HashSet<String>();
	
	public AssortmentMatrixAdapterEx(WarehouseNew warehouse, String id) {
		super(warehouse, id);
	}
	
	@Override
	protected List<MatrixItem> getMatrixItems() {
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		
		try{
			collectItems(id, result);
			for(MatrixItem i : result)
				if(!priceIds.contains(i.id))
					priceIds.add(i.id);
		}catch(Exception e){
			
		}
		
		return result;
	}
	
	public boolean isIdInMatrix(String id){
		return priceIds.contains(id);
	}

}
