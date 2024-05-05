package com.grsoft.napoleon;

import java.util.HashSet;

import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends Warehouse {
	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx)
			return new ZeroQtyFilter();
		return super.createZeroPositionFilter();
	}
	
	class ZeroQtyFilter extends Filter {
		HashSet<Long> ids = new HashSet<Long>();
		
		public ZeroQtyFilter() {
			super(ZeroPositionFilter.NAME);
			
			loadFilter();
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			return ids.contains(priceRowID);
		}
		
		void loadFilter() {
			ids.clear();
			
			int index = 0;
			
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();

			if( document instanceof OrderImplEx )
				index = ((OrderImplEx)document).getWhIndex();

			PriceQty pc = new PriceQty();
			String table = DataObjectInfo.getInstance().getTableName(pc.getClass());
			DbReader r = new DbReader();
			boolean bdo = r.select(pc, table, "type=" + Integer.toString(index));
			while(bdo) {
				p.id = pc.id;
				if( pi.read() )
					ids.add(pi.getRowid());
				bdo = r.selectNext(pc);
			}
			r.close();
			pi.close();
		}
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( type == COLUMN_COST ) {
			CostData cd = ((CostStrategyEx)CostStrategy.defaultInstance).getCost(price, document);
			if( cd != null ) {
				String str = Util.IntToScaleStr(cd.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				str += " / " + Util.IntToScaleStr(cd.itemCost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				if( textView != null )
					textView.setText(str);
				return;
			}
			
		}
		super.setTextColumnValue(textView, type, price);
	}
}
