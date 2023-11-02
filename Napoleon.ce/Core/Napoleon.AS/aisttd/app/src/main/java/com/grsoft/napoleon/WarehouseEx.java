package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;

public class WarehouseEx extends Warehouse {
	final int PERIOD_FOR_DELIVERY = 3;
	final int PERIOD_FOR_ORDER = 1;
	public static final String COST_FILTER = "cost_filter";
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = null;
		
		if( DocType.getCurDoc() == ReturnDoc.instance())
			ret = createAssortementMatrixAdapter();
		else 
			ret = (FoldersAdapter) super.createListAdapter();
		
		return ret;
	}

	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (DocType.getCurDoc() == ReturnDoc.instance()) {
			AssortmentMatrixAdapter.MATRIX_DOC = DeliveryDoc.instance();
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_DELIVERY;
		}else {
			AssortmentMatrixAdapter.MATRIX_DOC = OrderDoc.instance();
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_ORDER;
		}
		
		return super.createAssortementMatrixAdapter();
	}

	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menu_ex;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itCostFilter){
			updateForCostFilter();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	protected void updateForCostFilter() {
		boolean zeroFilter = false;

		if (adapter.getFilter(CostPositionFilter.NAME) == null) {
			adapter.putFilter(createCostPositionFilter());
			zeroFilter = true;
		} else
			adapter.deleteFilter(CostPositionFilter.NAME);

		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = pref.edit();
		ed.putBoolean(COST_FILTER, zeroFilter);
		ed.commit();

		adapter.buildSet();
	}

	@Override
	protected void postAdapterChange() {
		super.postAdapterChange();
		ivFilter.setVisibility(
				adapter.getFilter(ZeroPositionFilter.NAME) != null || adapter.getFilter(CostPositionFilter.NAME) != null
						? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem itCostFilter = menu.findItem(R.id.itCostFilter);
		if (itCostFilter != null) {
			if (adapter.getFilter(CostPositionFilter.NAME) != null)
				itCostFilter.setTitle(R.string.disable_cost_filter);
			else
				itCostFilter.setTitle(R.string.enable_cost_filter);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void postAdapterInit() {
		super.postAdapterInit();
		initCostFilter();
	}

	protected void initCostFilter() {
		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME,
				Context.MODE_PRIVATE);
		if (pref.getBoolean(COST_FILTER, false)
				&& DocType.getCurDoc() != ReturnDoc.instance())
			adapter.putFilter(createCostPositionFilter());
	}

	protected Filter createCostPositionFilter() {
		return new CostPositionFilter(document, price);
	}

	public static class CostPositionFilter extends Filter{
		public static String NAME = "CostPositionFilter";
		Document<?> document;
		PriceImpl price;
		CostStrategy costStrategy;

		public CostPositionFilter(Document<?> document, PriceImpl price) {
			super(NAME);

			this.document = document;
			this.price = price;
			this.costStrategy = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			price.read(priceRowID);
			return (costStrategy.getItemCost(price.getData(), document) > 0);
		}
	}
}
