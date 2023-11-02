package com.grsoft.napoleon;

import java.util.List;
import android.widget.BaseAdapter;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.impl.CommonMatrixImpl;
import com.grsoft.dataobjects.impl.OrgMtxImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.ZeroPositionFilter;


public class WarehouseEx extends WarehouseNew {
	
	private boolean matrixInited = false;
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilter();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 
			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		BaseAdapter result = null;
		
		if( document != null && !matrixInited) {
			OrgMtxImpl orgMtx = new OrgMtxImpl();
			orgMtx.getData().id = document.getId();
			boolean m = orgMtx.read();
			orgMtx.close();
			
			if(m){
				CommonMatrixImpl matrix = new CommonMatrixImpl();
				matrix.getData().name = orgMtx.getData().matrix;
				matrix.read();
				matrix.close();
				matrixInited = true;
				
				matrixName = matrix.getData().name; 
				result = new OrgMatrixAdapter(this, matrix);
			}
		}
		
		if(result == null)
			result = super.createListAdapter();
		
		return result;
	}
}

class OrgMatrixAdapter extends MatrixBaseAdapter {
	CommonMatrixImpl matrix;
	
	public OrgMatrixAdapter(WarehouseNew warehouse, CommonMatrixImpl matrix) {
		super(warehouse);
		this.matrix = matrix;
	}

	public String getName() { return matrix.getData().name; }

	@Override
	protected List<MatrixItem> getMatrixItems() {
		return matrix.getData().items;
	}
}
