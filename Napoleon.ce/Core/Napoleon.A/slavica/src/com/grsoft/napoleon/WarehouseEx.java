package com.grsoft.napoleon;

import android.os.Bundle;
import com.grsoft.dataobjects.impl.MoveImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	PriceImpl filterData = new PriceImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStop() {
		filterData.close();
		super.onStop();
	}

	@Override
	protected Filter createZeroPositionFilter() {
		return isUSeCustomFilter() ? new CustomZeroFilter() :  super.createZeroPositionFilter();
	}

	class CustomZeroFilter extends ZeroPositionFilter {

		public CustomZeroFilter() {
			where = "";
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			boolean ret = false;
			if (isUSeCustomFilter() && filterData.read(priceRowID)) 
				ret = ((Itemsable) document).getItemValue(filterData.getData()) > 0;
			else
				ret = false;

			return ret;
		}
	}

	public boolean isUSeCustomFilter() { return document instanceof OrderImplEx || document instanceof MoveImpl; }
	
	@Override
	protected void onResume() {
		super.onResume();

		if (document instanceof OrderImplEx)
			((OrderImplEx) document).resetSklad();
	}
	
	protected void updateTotalSum() {
		if (document instanceof MoveImpl){
			MoveImpl mv = (MoveImpl) document;
			updateTotalSum(document.sum(), mv.weight(), mv.count());
		}else 
			super.updateTotalSum(document.sum(), 0);
	}

}
