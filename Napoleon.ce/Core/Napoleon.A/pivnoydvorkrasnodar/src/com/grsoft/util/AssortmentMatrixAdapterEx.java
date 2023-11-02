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
		
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		collectItems(id, result);
		for(MatrixItem i : result)
			if(!priceIds.contains(i.id))
				priceIds.add(i.id);
		
	}
	
	public boolean isIdInMatrix(String id){
		return priceIds.contains(id);
	}

}
