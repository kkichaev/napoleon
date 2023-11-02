package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.FolderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.OrgMatrixAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

import android.view.Menu;
import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	static int whIndexSave = 0;
	
	PriceImpl pi = new PriceImpl();
	PriceEx p = (PriceEx) pi.getData(); 
	
	@Override
	protected BaseAdapter createListAdapter() {
		BaseAdapter result = null;
		
		if (docRowId == ExtrasConst.INVALID_ID)
			result = super.createListAdapter();
		else{
			if(document.getRowid() == ExtrasConst.INVALID_ID)
				document.read(docRowId);
			OrgImpl orgImpl = new OrgImpl();
			OrgEx oe = (OrgEx) orgImpl.getData(); 
			oe.id = document.getId();
			
			List<OrgMatrixItem> items = null;
			if(document instanceof OrderImplEx)
				items = ((OrderImplEx)document).getOrgMatrix();
			if(items != null && items.size() == 0)
				items = null;
			
			List<FolderItem> folders = null;
			if(orgImpl.read() && oe.folders != null && oe.folders.size() > 0)
				folders = oe.folders;
			
			if (items != null || folders != null)
				result = new OrgMatrixAdapter(this, folders, items, oe.id);
			else {
				int index = 0;
				if( document instanceof OrderImplEx )
					index = ((OrderImplEx)document).getWhIndex();

				if( whIndexSave != index ) {
					FoldersAdapter.resetCache();
					whIndexSave = index;
				}
				result = new Adapter(this, index);
				if( Features.SHOW_ZERO_FILTER )
					((Adapter)result).putFilter(createZeroPositionFilter());
			}
		}
		
		return result;
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		pi.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.warehouse_opt_menu_ex, menu);
		return true;
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		int index = 0;
		if( document instanceof OrderImplEx )
			index = ((OrderImplEx)document).getWhIndex();
		
		return new ZeroQtyFilter(index);
	}
	
	class Adapter extends FoldersAdapter {

		int index;
		
		public Adapter(WarehouseManager warehouse, int index) {
			super(warehouse);
			this.index = index;
		}
		
		@Override
		public String getName() {
			return super.getName() + "|" + Integer.toString(index);
		}
		
	}
	
	class ZeroQtyFilter extends Filter {
		int index;
		
		public ZeroQtyFilter(int index) {
			super(ZeroPositionFilter.NAME);
			
			this.index = index;
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			pi.read(priceRowID);
			return (index == 0 || p.whQty.size() < index) ?
					p.qty > 0 :
					p.whQty.get(index-1).qty > 0;
		}
	}
}
