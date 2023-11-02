package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

import android.widget.BaseAdapter;

public class WarehouseEx2 extends WarehouseEx {
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter(); 
		if(document instanceof OrderImpl)
			ret.putFilter(new FirmFilter(((OrderImpl)document).getData().supplyer));
		return ret;
	}
	
	
	class FirmFilter extends Filter {

		int suppl;
		public FirmFilter(int suppl) {
			super("FirmFilter" + Integer.toString(suppl));
			this.suppl = suppl;
		}
		
		@Override
		public String getWhereStr() {
			return "firm = " + Integer.toString(suppl);
		}
	}
}
