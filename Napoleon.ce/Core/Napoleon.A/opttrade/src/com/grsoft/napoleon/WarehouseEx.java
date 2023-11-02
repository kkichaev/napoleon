package com.grsoft.napoleon;

import android.os.Bundle;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
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
	protected void initZeroFilter() { adapter.putFilter(createZeroPositionFilter()); }
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new CustomZeroFilter();
	}

	class CustomZeroFilter extends ZeroPositionFilter {

		public CustomZeroFilter() {
			where = "";
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			boolean ret = false;
			if (filterData.read(priceRowID)) 
				ret = ((Itemsable) document).getItemValue(filterData.getData()) > 0;
			else
				ret = false;

			return ret;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (document instanceof OrderImplEx)
			((OrderImplEx) document).resetSklad();
	}
	
	@SuppressWarnings("unchecked")
	protected int getCost(Price price) {
		if (document.getId().length() == 0 && price.cost.size() > 0)
			return price.cost.get(0).cost;
		else
			return CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getItemCost(price, (Document<?>) document);
	}
}
