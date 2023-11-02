package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends Warehouse {
    PriceImpl pi = new PriceImpl();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pi.close();
    }

    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
        if(document instanceof OrderImpl) {
            ret.putFilter(new ZeroCostFilter(document, price));
        }
        return ret;
    }

    public static class ZeroCostFilter extends Filter {
		public static final String NAME = "ZeroCostFilter";
		Document<?> document;
		CostStrategy costStrategy;
        PriceImpl price;

		@SuppressWarnings("unchecked")
		public ZeroCostFilter(Document<?> document, PriceImpl price) {
			super(NAME);

			this.document = document;
			this.costStrategy = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
            this.price = price;
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			if(document != null && price.read(priceRowID)) {
				return (costStrategy.getItemCost(price.getData(), document) > 0);
			}
			return super.inset(priceRowID, id);
		}
	}
}
