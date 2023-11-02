package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.view.View;
import android.widget.TextView;

public class SalesDetailEx extends SalesDetail {
	
	@Override protected void setContentView() { setContentView(R.layout.salesdetailex); }
	
	@Override
	protected String[] createPrintCaption() {
		return new String[] {"Накладная", "ТТН ТОРГ 12", "Счет-фактура" };
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override int getResourceID() { return R.layout.sales_list_rowex; }
			
			@Override
			protected void drawInternal(View view, String name, int color, OrderItem item) {
				super.drawInternal(view, name, color, item);
				@SuppressWarnings("unchecked")
				CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass());
				PriceImpl p = new PriceImpl();
				p.read("id", item.id);
				int cost = cs.getItemCost(p.getData(), doc);
				TextView tv = (TextView) view.findViewById(R.id.tvCost);
				tv.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE));
			}
		});
	}
}
